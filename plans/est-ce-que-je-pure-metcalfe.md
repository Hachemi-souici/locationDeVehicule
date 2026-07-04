# Ajouter une interface graphique Swing au projet LocationDeVoiture + tag git v2

## Contexte

Le projet `LocationDeVoiture` est un TP universitaire Java (INF1120, UQAM) qui gère
la location de véhicules hybrides/électriques via une interface **console**
(`Scanner` / `System.out`) dans [ApplicationPrincipale.java](ApplicationPrincipale.java).
Toute la logique métier (calcul de prix, rabais, taxes, factures, inventaire,
persistance SQLite) est déjà encapsulée dans des classes séparées et n'a **aucune
dépendance à la console** — ce qui la rend directement réutilisable par une interface
graphique.

L'utilisateur souhaite ajouter une interface graphique **sans modifier le projet
existant** (aucun fichier `.java` actuel ne doit changer), puis marquer cette étape
avec un tag git `v2`.

Le projet n'utilise ni Maven ni Gradle : il est compilé manuellement avec
`javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java`. Java 21 LTS est installé.
Aucun tag git n'existe encore ; remote `origin` déjà configuré vers GitHub.

## Choix du framework : Swing

**Swing** est recommandé plutôt que JavaFX :
- Inclus nativement dans le JDK (`javax.swing`), donc **zéro dépendance à ajouter** —
  cohérent avec un projet sans build tool où gérer un module JavaFX externe serait
  compliqué (JavaFX a été retiré du JDK depuis Java 11 et nécessite un SDK séparé
  + des options `--module-path`/`--add-modules` à la compilation et à l'exécution).
- Compile et s'exécute avec la même commande `javac`/`java` déjà utilisée par le
  projet, sans rien ajouter au classpath.
- Largement suffisant pour un formulaire + des tableaux d'affichage comme ici.

`★ Insight ─────────────────────────────────────`
Swing vs JavaFX pour un TP sans build tool : JavaFX a été extrait du JDK en 2018
(JEP 320) et vit maintenant comme bibliothèque externe (jar à télécharger +
`--module-path`). Sans Maven/Gradle pour gérer cette dépendance proprement, il
faudrait manuellement télécharger les jars JavaFX et alourdir la commande de
compilation. Swing, lui, est toujours livré avec le JDK standard depuis Java 1.2 —
c'est le choix qui respecte le mieux la contrainte "ne pas complexifier le projet".
`─────────────────────────────────────────────────`

## Principe : additif, aucune modification de l'existant

Aucun fichier `.java` existant ne sera touché. Les nouveaux fichiers seront placés
dans un **nouveau sous-dossier `gui/`** à la racine du projet, pour signaler
clairement qu'il s'agit d'un ajout séparé de la logique métier (qui reste à plat
à la racine). Comme le projet n'a pas de notion de package Java (toutes les classes
existantes sont dans le package par défaut), les nouvelles classes GUI devront
**aussi rester dans le package par défaut** (pas de `package gui;` en en-tête) pour
pouvoir appeler directement `Vehicule`, `Facture`, `GestionVehiculesDisponibles`,
etc. sans import — Java exige que le code du package par défaut ne soit pas
importé depuis un package nommé. Le dossier `gui/` sert donc uniquement à organiser
les fichiers sur le disque ; la compilation continuera d'utiliser `*.java` élargi
à ce dossier.

Les méthodes de saisie console de `ApplicationPrincipale` (`entrerValiderXxx`, etc.)
**ne seront pas réutilisées** : elles lisent sur un `Scanner` lié à `System.in` et
bloqueraient une interface graphique. Le GUI réimplémente sa propre validation
(via des `JOptionPane` d'erreur et des contrôles de saisie Swing adaptés), mais
appelle **exactement les mêmes classes métier** pour le calcul et la persistance.

## Nouveaux fichiers à créer

Tous dans `gui/`, sans déclaration de package :

### 1. `gui/InterfaceGraphique.java` — point d'entrée
- `public static void main(String[] args)` : appelle `BaseDeDonnees.initialiser()`,
  `GestionVehiculesDisponibles.chargerVehiculesDisponibles()`, puis construit et
  affiche la fenêtre principale via `SwingUtilities.invokeLater(...)`.
- Classe `FenetrePrincipale extends JFrame` : contient un `JTabbedPane` avec 4 onglets
  correspondant aux options 1 à 4 du menu console (l'option "Quitter" devient la
  fermeture native de la fenêtre). Ajoute un `WindowListener` (ou
  `setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)` + `windowClosing`) qui, à la
  fermeture : appelle `ListeDesFactures.sauvegarderFactures()` puis
  `BaseDeDonnees.fermer()` avant `System.exit(0)` — reproduit exactement la logique
  du `case 5` de `ApplicationPrincipale.main()`.

### 2. `gui/PanelLocation.java` — onglet "Facturer une location" (option 1)
Le plus complexe : reproduit la boucle d'ajout de véhicules + saisie locataire/paiement.
- Formulaire avec `JComboBox<Character>` (ou boutons radio) pour type (H/E) et
  grandeur (P/I/G), `JSpinner` pour le nombre de jours (1-30) et le nombre de
  véhicules, `JCheckBox` pour l'assurance.
- Bouton "Ajouter ce véhicule à la location" : valide la disponibilité via
  `GestionVehiculesDisponibles.obtenirNombreVehiculesDisponibles(type, grandeur)`,
  construit un `Vehicule` puis un `VehiculeLoue`, l'ajoute à une instance de
  `LocationVehicule` conservée en champ de la classe (créée au chargement de
  l'onglet), l'affiche dans un `JTable`/`JList` récapitulatif, et appelle
  `GestionVehiculesDisponibles.diminuerNombreVehiculesDisponibles(...)` et
  `StatistiquesVehiculesLoues.augmenterNombreVehiculesLoues(...)`.
- Section locataire : `JTextField` pour prénom/nom/téléphone/permis avec validation
  au clic sur "Générer la facture" (mêmes règles que la console : longueur 2-30,
  regex téléphone `(XXX) XXX-XXXX`, regex permis `X1234-567891-23`) ; erreurs
  affichées via `JOptionPane.showMessageDialog(..., ERROR_MESSAGE)`.
- Mode de paiement : `JRadioButton` Débit/Crédit ; si Crédit, active des champs
  type de carte (Visa/Mastercard) et numéro de carte (regex `XXXX XXXX XXXX XXXX`).
- Bouton "Générer la facture" : construit `Locataire`, appelle
  `locationVehicule.setLocataire(...)`, crée `Facture`, appelle dans l'ordre
  `calculerSousTotal()` → `calculerMontantTPS()` → `calculerMontantTVQ()` →
  `calculerMontantTotal()`, puis **au lieu** d'appeler `facture.afficherFacture()`
  (qui fait du `System.out`), affiche le contenu formaté dans un `JTextArea` en
  lecture seule dans une boîte de dialogue (`JDialog` ou `JOptionPane`) — même
  contenu, juste rendu à l'écran au lieu de la console. Termine par
  `ListeDesFactures.ajouterFacture(facture)` et réinitialise le formulaire pour
  une nouvelle location.

### 3. `gui/PanelInventaire.java` — option 3 "Afficher l'inventaire"
- `JTable` avec colonnes Grandeur / Hybride / Électrique, remplie en appelant
  `GestionVehiculesDisponibles.obtenirNombreVehiculesDisponibles(type, grandeur)`
  pour chaque combinaison (reproduit la boucle de `GestionVehiculesDisponibles.afficher()`
  sans utiliser cette méthode qui imprime sur `System.out`).
- Bouton "Rafraîchir" pour recharger les données après une location.

### 4. `gui/PanelStatistiques.java` — option 2 "Statistiques véhicules loués"
- Même structure de `JTable` (Grandeur / Hybride / Électrique), alimentée par
  `StatistiquesVehiculesLoues.obtenirNombreVehiculesLoues(type, grandeur)`.

### 5. `gui/PanelFactures.java` — option 4 "Afficher toutes les factures"
- `ListeDesFactures` ne fournit aucun accès en lecture à son tableau interne de
  factures — seulement `ajouterFacture(...)`, `sauvegarderFactures()` et
  `afficher()`. Or **`afficher()` est inutilisable depuis une GUI** : dès qu'il y a
  2 factures ou plus, elle appelle `ApplicationPrincipale.pauseAvantMenu()` entre
  chaque facture, qui fait un `Scanner.nextLine()` bloquant sur `System.in` — sans
  personne pour taper au terminal, le thread Swing (ou l'appelant) resterait figé
  indéfiniment.
- Solution retenue : ce panel lit **directement les tables SQLite** `factures` et
  `vehicules_loues` via `BaseDeDonnees.obtenirConnexion()` (schéma déjà défini dans
  `BaseDeDonnees.creerTables()`), avec du SQL simple (`SELECT ... ORDER BY numero_facture`,
  puis pour chaque facture sélectionnée `SELECT ... FROM vehicules_loues WHERE numero_facture = ?`).
  Pour que les factures de la session courante apparaissent, `PanelLocation` doit
  appeler `ListeDesFactures.sauvegarderFactures()` immédiatement après chaque
  `ajouterFacture(...)` (pas seulement à la fermeture de l'application).
- Affichage : `JTable` maître (numéro, date, client, montant total) + zone de
  détail (`JTextArea`) qui se remplit à la sélection avec les véhicules loués de
  la facture, en reconstruisant un texte lisible à partir des colonnes lues —
  jamais via `Facture.afficherFacture()` ni `ListeDesFactures.afficher()`.
- Cas 0 facture : afficher "Aucune facture disponible." plutôt qu'un tableau vide.

## Compilation automatique (sans build tool)

Le projet reste sans Maven/Gradle, mais pour éviter de retaper la commande
`javac`/`java` à chaque fois, un **script de compilation automatique** est ajouté :
`gui/lancer-gui.sh` (et son équivalent Windows `gui/lancer-gui.bat`), tous deux
nouveaux fichiers qui n'affectent pas le projet existant.

`gui/lancer-gui.sh` :
```bash
#!/usr/bin/env bash
set -e
cd "$(dirname "$0")/.."
javac -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" *.java gui/*.java
java  -cp ".:libs/sqlite-jdbc-3.46.1.3.jar" InterfaceGraphique
```

`gui/lancer-gui.bat` (équivalent Windows, `;` au lieu de `:`) :
```bat
@echo off
cd /d "%~dp0.."
javac -cp ".;libs/sqlite-jdbc-3.46.1.3.jar" *.java gui/*.java
java  -cp ".;libs/sqlite-jdbc-3.46.1.3.jar" InterfaceGraphique
```

Utilisation : `./gui/lancer-gui.sh` (après `chmod +x gui/lancer-gui.sh` une seule
fois) recompile automatiquement tout le projet (fichiers existants + `gui/`) puis
lance la fenêtre — une seule commande couvre compilation et exécution, sans jamais
introduire de dépendance à un outil de build. Le programme console reste inchangé
et utilisable via `java ... ApplicationPrincipale` comme avant. Le [README.md](README.md)
sera complété (nouvelle section) pour documenter ce second point d'entrée et le
script — sans retirer les instructions existantes.

## Étapes d'implémentation

1. Créer le dossier `gui/` avec : `Validation.java` (utilitaire de règles de
   saisie, indépendant, à écrire en premier), `PanelInventaire.java` et
   `PanelStatistiques.java` (lecture seule, simples), `PanelFactures.java`
   (lecture SQL directe), `PanelLocation.java` (le plus complexe, orchestration
   des appels métier), `InterfaceGraphique.java` (assemble tout), puis les scripts
   `lancer-gui.sh` / `lancer-gui.bat`.
2. Rendre le script exécutable (`chmod +x gui/lancer-gui.sh`) et l'utiliser pour
   compiler ; corriger les éventuelles erreurs.
3. Lancer via le script, tester le parcours complet : ajouter une location avec
   plusieurs véhicules, générer une facture (paiement débit puis crédit, avec et
   sans assurance), vérifier l'inventaire qui diminue, les statistiques qui
   augmentent, la facture qui apparaît dans l'onglet Factures sans blocage, puis
   fermer la fenêtre et relancer pour vérifier que `GestionLocation.db` a bien
   persisté la nouvelle facture.
4. Une fois validé, committer les nouveaux fichiers (`gui/`, `README.md` mis à jour).
5. Créer le tag `git tag v2` puis, si demandé, `git push origin v2`.

## Vérification

- `./gui/lancer-gui.sh` compile et lance sans erreur.
- La fenêtre s'affiche avec 4 onglets (Location, Statistiques, Inventaire, Factures).
- Test bout-en-bout d'une location + facture, comparée manuellement à une
  exécution de `ApplicationPrincipale` pour vérifier que les montants
  (sous-total, TPS, TVQ, total, rabais éventuel) correspondent.
- Créer 2 factures dans la même session GUI puis ouvrir l'onglet Factures :
  confirmer qu'il n'y a **aucun blocage** (preuve que `ListeDesFactures.afficher()`
  n'est jamais appelée) et que les 2 factures apparaissent.
- Fermeture de la fenêtre : vérifier via `sqlite3 GestionLocation.db` (ou un
  client SQLite) que la table `factures` contient bien les nouvelles entrées.
- Confirmer qu'aucun fichier `.java` existant n'a de diff (`git status` /
  `git diff` ne montrent que des fichiers ajoutés sous `gui/` et le README modifié).
