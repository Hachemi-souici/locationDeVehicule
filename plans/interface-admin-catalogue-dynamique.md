# Catalogue dynamique + interface d'administration sécurisée

## Contexte

La GUI Swing existante ([gui/](gui/), version `v2`) permet de facturer des
locations, mais l'inventaire de véhicules reste figé à 6 combinaisons
(Hybride/Électrique × Petit/Intermédiaire/Grand), câblées en dur dans le code :
tableaux de taille fixe, `switch`/`case` qui ne connaissent que ces 5 valeurs,
et une règle de rabais (20% après 15 jours, Électrique P/I uniquement) écrite
en dur dans [VehiculeLoue.java](VehiculeLoue.java).

L'utilisateur veut pouvoir **ajouter de vrais nouveaux types/grandeurs de
véhicule**, modifier prix et stock, et gérer les remises — depuis une
**deuxième interface graphique, réservée à un administrateur, protégée par
un écran de connexion**. Il a explicitement accepté que cela nécessite de
modifier 4 fichiers du TP original ([Vehicule.java](Vehicule.java),
[GestionVehiculesDisponibles.java](GestionVehiculesDisponibles.java),
[StatistiquesVehiculesLoues.java](StatistiquesVehiculesLoues.java),
[VehiculeLoue.java](VehiculeLoue.java)) — c'est la seule façon technique
d'avoir un catalogue extensible qui s'affiche correctement partout (facture,
inventaire, statistiques), car `char` est un type fermé et ces classes
utilisent des tableaux de taille 6 codés en dur.

**Contrainte de non-régression** : après ces changements, `ApplicationPrincipale`
(programme console original) doit continuer à fonctionner exactement comme
avant pour les 6 combinaisons d'origine, et refléter aussi les nouveaux
types/grandeurs ajoutés par l'admin (les classes modifiées sont partagées).

**Limite acceptée** : `ApplicationPrincipale.java` n'est pas modifié, donc sa
méthode `entrerValiderTypeVehicule()` continuera de n'accepter que H/E au
clavier (elle valide la saisie contre un test `!= 'H' && != 'E'` en dur). La
console pourra donc **afficher** correctement un nouveau type ajouté par
l'admin (facture, inventaire, stats, puisque ces couches deviennent
dynamiques), mais un utilisateur de la console ne pourra pas **saisir** ce
nouveau type au clavier — seule la GUI client (`gui/PanelLocation.java`,
mise à jour dynamiquement) permettra de louer les nouveaux types/grandeurs.

## Schéma de données : nouvelles tables SQLite

Ajoutées dans [BaseDeDonnees.java](BaseDeDonnees.java) `creerTables()`, peuplées
une seule fois si vides (même pattern que `amorcerInventaireSiVide()`) :

```sql
CREATE TABLE IF NOT EXISTS types_vehicules (
  code TEXT PRIMARY KEY,        -- ex: 'H', 'E', 'X' (premier caractère utilisé comme char partout)
  description TEXT NOT NULL     -- ex: "Hybride", "Électrique", "Essence"
);

CREATE TABLE IF NOT EXISTS grandeurs_vehicules (
  code TEXT PRIMARY KEY,        -- ex: 'P', 'I', 'G', 'X' (pour XL)
  description TEXT NOT NULL,    -- ex: "Petit", "Intermédiaire", "Grand", "XL"
  ordre_affichage INTEGER NOT NULL  -- pour garder P/I/G dans le bon ordre dans les tableaux
);

CREATE TABLE IF NOT EXISTS regles_rabais (
  type_vehicule TEXT NOT NULL,
  grandeur_vehicule TEXT NOT NULL,
  seuil_jours INTEGER NOT NULL,
  pourcentage_rabais REAL NOT NULL,
  PRIMARY KEY (type_vehicule, grandeur_vehicule)
);

CREATE TABLE IF NOT EXISTS administrateurs (
  identifiant TEXT PRIMARY KEY,
  mot_de_passe_hache TEXT NOT NULL   -- SHA-256 en hexadécimal
);
```

Amorçage initial (si tables vides) :
- `types_vehicules` : `('H','Hybride')`, `('E','Électrique')`.
- `grandeurs_vehicules` : `('P','Petit',1)`, `('I','Intermédiaire',2)`, `('G','Grand',3)`.
- `regles_rabais` : une ligne `('E','P',15,0.20)` et `('E','I',15,0.20)` — reproduit
  exactement la règle actuelle. Une combinaison sans ligne dans cette table = pas de rabais.
- `administrateurs` : si vide, insérer `admin` / SHA-256("admin"), avec un
  message affiché au premier login recommandant de changer le mot de passe.

La table existante `vehicules_disponibles` n'a pas besoin de migration de
schéma : ses colonnes `type_vehicule`/`grandeur_vehicule` sont déjà `TEXT`
(pas de contrainte de longueur 1 imposée par SQLite), donc un nouveau type
"X" s'y insère sans changement de structure.

## Modifications aux 4 fichiers existants

### [Vehicule.java](Vehicule.java)
Remplacer les `switch`/`case` fixes de `getDescriptionTypeVehicule()` et
`getDescriptionGrandeurVehicule()` par une lecture dans une nouvelle classe
utilitaire `CatalogueVehicules` (nouveau fichier, à la racine — car `Vehicule`
en a besoin et ne peut pas dépendre de `gui/`) :
```java
public String getDescriptionTypeVehicule() {
    return CatalogueVehicules.descriptionType(typeVehicule);
}
public String getDescriptionGrandeurVehicule() {
    return CatalogueVehicules.descriptionGrandeur(grandeurVehicule);
}
```
Les constantes `HYBRIDE`/`ELECTRIQUE`/`PETIT`/`INTERMEDIAIRE`/`GRAND` restent
(elles servent de valeurs par défaut et sont utilisées par `ApplicationPrincipale`
et les tests), mais ne sont plus les seules valeurs possibles. Le constructeur
`Vehicule(char, char, float, float)` ne change pas de signature.

### Nouveau fichier `CatalogueVehicules.java` (racine, additif)
Classe statique, chargée une fois depuis SQL (comme `GestionVehiculesDisponibles`) :
- `static void charger()` : lit `types_vehicules` et `grandeurs_vehicules` dans deux
  `Map<Character,String>` statiques.
- `static String descriptionType(char code)`, `static String descriptionGrandeur(char code)`.
- `static List<Character> typesDisponibles()`, `static List<Character> grandeursDisponibles()`
  (grandeurs triées par `ordre_affichage`) — utilisées par `GestionVehiculesDisponibles.afficher()`,
  `StatistiquesVehiculesLoues.afficherNombreVehiculesLoues()`, et les panels GUI.
- `static void ajouterType(char code, String description)`, `static void ajouterGrandeur(char code, String description, int ordre)`
  — appelées uniquement par l'interface admin, font l'`INSERT` SQL puis rechargent les Maps en mémoire.

### [GestionVehiculesDisponibles.java](GestionVehiculesDisponibles.java)
- Remplacer `private static VehiculeDisponible[] lesVehiculesDisponibles = new VehiculeDisponible[6];`
  par `private static List<VehiculeDisponible> lesVehiculesDisponibles = new ArrayList<>();`
- `chargerVehiculesDisponibles()` : `lesVehiculesDisponibles.clear()` puis `.add(...)` au lieu d'indexer un tableau de taille fixe — supprime la limite de 6.
- Les méthodes `obtenirNombreVehiculesDisponibles`/`diminuerNombreVehiculesDisponibles`/`obtenirPrixLocationVehParJour`/`obtenirPrixAssuranceVehParJour`/`estDisponible` : le `for (VehiculeDisponible v : lesVehiculesDisponibles)` fonctionne à l'identique sur une `List` (signatures publiques inchangées, donc `ApplicationPrincipale` et `PanelLocation` ne voient aucune différence).
- `afficher()` : remplacer les tableaux `String[] grandeurs = {...}` / `char[] codesGrandeurs = {...}` par `CatalogueVehicules.grandeursDisponibles()`, et boucler aussi sur `CatalogueVehicules.typesDisponibles()` pour générer dynamiquement une colonne par type (au lieu de 2 colonnes Hybride/Électrique fixes).
- Nouvelle méthode publique `ajouterOuMettreAJourVehiculeDisponible(char type, char grandeur, float prixLocation, float prixAssurance, int stock)` : `UPDATE` si la combinaison existe déjà en base, sinon `INSERT`, puis recharge `lesVehiculesDisponibles` depuis SQL. Utilisée uniquement par l'interface admin pour ajouter une combinaison ou modifier prix/stock.

### [StatistiquesVehiculesLoues.java](StatistiquesVehiculesLoues.java)
Même traitement : `VehiculeLoue[] lesVehiculesLoues = new VehiculeLoue[6]` devient
`List<VehiculeLoue> lesVehiculesLoues = new ArrayList<>()`. `afficherNombreVehiculesLoues()`
boucle sur `CatalogueVehicules.grandeursDisponibles()`/`typesDisponibles()` au lieu des tableaux fixes.

### [VehiculeLoue.java](VehiculeLoue.java)
`calculerRabais()` interroge une nouvelle méthode
`RegleRabais.obtenirPourcentage(char type, char grandeur)` (nouveau fichier
`RegleRabais.java`, racine) qui lit la table `regles_rabais` (chargée en mémoire
comme `CatalogueVehicules`, `Map<String,double[]>` clé `type+grandeur` →
`{seuilJours, pourcentage}`). Si aucune règle trouvée pour cette combinaison,
retourne `{Integer.MAX_VALUE, 0}` (jamais de rabais). Le comportement par défaut
(20% après 15 jours pour E/P et E/I) est garanti par l'amorçage SQL initial —
donc `ApplicationPrincipale` ne voit aucun changement de comportement.
```java
public float calculerRabais() {
    double[] regle = RegleRabais.obtenirPourcentage(vehicule.getTypeVehicule(), vehicule.getGrandeurVehicule());
    if (nombreJoursLocation > regle[0]) {
        return (float) (vehicule.getPrixLocationParJour() * regle[1]);
    }
    return 0;
}
```
`RegleRabais.ajouterOuModifier(char type, char grandeur, int seuilJours, double pourcentage)`
est appelée uniquement par l'admin.

## Nouvelle interface Admin (dossier séparé, additif)

Dossier `gui-admin/` (parallèle à `gui/`, même principe : pas de `package`,
réutilise directement les classes de la racine et `CatalogueVehicules`/`RegleRabais`).

### `Hachage.java` (racine, nouvelle classe minuscule et indépendante)
`static String sha256Hex(String texte)` : seule responsabilité, convertir un
texte en son empreinte SHA-256 hexadécimale (`MessageDigest.getInstance("SHA-256")`).
Placée à la racine (pas dans `gui-admin/`) car `BaseDeDonnees.amorcerCataloguesSiVide()`
en a besoin pour créer le compte `admin` par défaut, et `BaseDeDonnees` ne doit
pas dépendre d'un sous-dossier applicatif comme `gui-admin/`.

### `gui-admin/Authentification.java`
- `static boolean verifierIdentifiants(String identifiant, String motDePasse)` :
  compare `Hachage.sha256Hex(motDePasse)` au hash stocké dans la table `administrateurs`.
- `static boolean changerMotDePasse(String identifiant, String nouveauMotDePasse)` :
  `UPDATE administrateurs SET mot_de_passe_hache = ? WHERE identifiant = ?`.

### `gui-admin/EcranConnexion.java`
`JDialog` modal (identifiant + champ mot de passe `JPasswordField`, bouton
"Se connecter") affiché avant la fenêtre principale admin. 3 tentatives max
avant fermeture de l'application (mesure de bon sens, pas une exigence de
sécurité forte pour un TP). En cas de succès, ouvre `FenetreAdmin`.

### `gui-admin/PanelGestionCatalogue.java`
- Tableau des types de véhicules existants (code, description) + formulaire
  "Ajouter un type" (code 1 caractère, description) → `CatalogueVehicules.ajouterType(...)`.
- Tableau des grandeurs existantes + formulaire "Ajouter une grandeur" →
  `CatalogueVehicules.ajouterGrandeur(...)`.

### `gui-admin/PanelGestionInventaire.java`
- Tableau de toutes les combinaisons type×grandeur existantes (prix location,
  prix assurance, stock), éditable directement dans le `JTable`
  (`DefaultTableModel` avec `isCellEditable` vrai sur les 3 colonnes numériques)
  ou via formulaire dédié ; bouton "Enregistrer" → `GestionVehiculesDisponibles.ajouterOuMettreAJourVehiculeDisponible(...)`.
- Formulaire "Ajouter une nouvelle combinaison" : sélection du type et de la
  grandeur (JComboBox alimentées par `CatalogueVehicules.typesDisponibles()`/`grandeursDisponibles()`,
  y compris les types/grandeurs tout juste ajoutés dans l'onglet catalogue),
  saisie prix + stock initial.

### `gui-admin/PanelGestionRabais.java`
- Tableau des règles de rabais existantes (type, grandeur, seuil en jours, %),
  éditable, bouton "Enregistrer" → `RegleRabais.ajouterOuModifier(...)`.

### `gui-admin/InterfaceAdmin.java` — point d'entrée séparé
`public static void main(String[] args)` : `BaseDeDonnees.initialiser()`,
`Authentification.creerCompteParDefautSiVide()`, `CatalogueVehicules.charger()`,
`RegleRabais.charger()`, `GestionVehiculesDisponibles.chargerVehiculesDisponibles()`,
affiche `EcranConnexion` ; si succès, `FenetreAdmin` (`JTabbedPane` à 3 onglets :
Catalogue, Inventaire, Rabais) ; fermeture propre via `BaseDeDonnees.fermer()`
au `windowClosing` (pas de sauvegarde de factures ici, l'admin ne facture rien).

## Cohabitation console / GUI client / GUI admin

Les trois points d'entrée (`ApplicationPrincipale`, `gui/InterfaceGraphique`,
`gui-admin/InterfaceAdmin`) appellent chacun `BaseDeDonnees.initialiser()` au
démarrage, qui est idempotente (`if (initialisee) return;` déjà présent) et
utilise `CREATE TABLE IF NOT EXISTS` partout — peu importe lequel démarre en
premier, aucun conflit. SQLite gère nativement les accès multi-process
(verrouillage fichier), suffisant pour un usage TP.

**Limitation acceptée** : si l'admin modifie le catalogue (nouveau type, nouveau
prix) pendant que la GUI client tourne déjà, le client garde en mémoire son
catalogue chargé au démarrage — il ne voit pas les changements automatiquement.
Pour couvrir ce cas simplement, un bouton **"Recharger le catalogue"** est ajouté
dans `gui/PanelInventaire.java` (à côté du bouton "Rafraîchir" existant), qui
appelle `GestionVehiculesDisponibles.chargerVehiculesDisponibles()` +
`CatalogueVehicules.invalider()` + `GestionReglesRabais.invalider()`, puis
rafraîchit tous les onglets. Sinon, il suffit de fermer/relancer la GUI client
après une session admin.

## Mise à jour des panels client existants (gui/)

- [gui/PanelLocation.java](gui/PanelLocation.java) : `comboType`/`comboGrandeur`
  alimentées dynamiquement par `CatalogueVehicules.typesDisponibles()`/`grandeursDisponibles()`
  au lieu des tableaux `{"Hybride","Électrique"}`/`{"Petit","Intermédiaire","Grand"}`.
  Le mapping par **index de position** (`comboType.getSelectedIndex() == 0 ? HYBRIDE : ELECTRIQUE`)
  est remplacé par un mapping direct vers le `char` réel (garder une `List<Character>`
  parallèle à la liste de labels affichés, indexée pareil).
- [gui/PanelInventaire.java](gui/PanelInventaire.java) et
  [gui/PanelStatistiques.java](gui/PanelStatistiques.java) : remplacer les
  constantes `GRANDEURS_LABELS`/`GRANDEURS_CODES` fixes par un appel à
  `CatalogueVehicules.grandeursDisponibles()`, et générer dynamiquement une
  colonne de tableau par type retourné par `CatalogueVehicules.typesDisponibles()`
  (au lieu des 2 colonnes Hybride/Électrique câblées).

## Compilation

Étendre [gui/lancer-gui.sh](gui/lancer-gui.sh) pour inclure `CatalogueVehicules.java`
et `RegleRabais.java` (fichiers racine, déjà couverts par `*.java`). Nouveau script
`gui-admin/lancer-admin.sh` (+ `.bat`) sur le même modèle :
```bash
#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/.."
javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java gui-admin/*.java
java  -cp ".:gui-admin:libs/sqlite-jdbc-3.46.1.3.jar" InterfaceAdmin
```

## Ordre d'implémentation

1. `CatalogueVehicules.java` et `RegleRabais.java` (nouveaux fichiers racine, indépendants).
2. Ajouter les 4 nouvelles tables + amorçage dans `BaseDeDonnees.java`.
3. Modifier `Vehicule.java` (délègue au catalogue), `GestionVehiculesDisponibles.java`
   et `StatistiquesVehiculesLoues.java` (tableaux → `List`, `afficher()` dynamique),
   `VehiculeLoue.java` (`calculerRabais()` via `RegleRabais`).
4. Compiler et relancer `ApplicationPrincipale` : vérifier manuellement que les 6
   combinaisons d'origine, les prix, le rabais électrique P/I après 15 jours, et
   l'affichage de l'inventaire/statistiques sont identiques à avant (non-régression).
5. Mettre à jour `gui/PanelLocation.java`, `gui/PanelInventaire.java`,
   `gui/PanelStatistiques.java` pour lire le catalogue dynamiquement. Relancer la
   GUI client, vérifier que rien n'a changé visuellement avec les 6 combinaisons existantes.
6. Créer `gui-admin/` (Authentification, EcranConnexion, les 3 panels, InterfaceAdmin)
   + scripts de lancement.
7. Test bout-en-bout : lancer l'admin, ajouter un type "Essence" et une grandeur "XL",
   créer une combinaison Essence/XL avec prix et stock, ajouter une règle de rabais
   custom ; fermer l'admin ; relancer la GUI client et vérifier que Essence/XL apparaît
   dans les combobox de facturation, dans l'inventaire et dans les statistiques après une location.
8. Commit + tag (proposer `v3` une fois validé).

## Vérification

- `ApplicationPrincipale` compile et s'exécute sans régression sur les 6 combinaisons
  d'origine (mêmes prix, mêmes calculs de facture, même comportement de rabais).
- `gui-admin` : connexion refusée avec mauvais mot de passe, acceptée avec `admin`/`admin`
  par défaut ; ajout d'un type/grandeur/combinaison/règle de rabais persisté en SQLite.
- `gui/InterfaceGraphique` : les nouveaux types/grandeurs ajoutés par l'admin
  apparaissent dans les combobox et tableaux sans redémarrage du code (juste
  relancer l'appli, puisque le catalogue est chargé au démarrage).
- Une location avec le nouveau type + règle de rabais custom produit une facture
  avec le bon montant de rabais.
- `git diff` sur les 4 fichiers modifiés ne montre que les changements décrits
  ci-dessus, sans autre effet de bord sur les signatures publiques utilisées
  par `ApplicationPrincipale`.
