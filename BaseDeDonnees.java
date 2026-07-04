
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Université du Québec à Montréal (UQAM) INF1120 - 010 - Hiver 2025 Travail
 * pratique 3
 *
 * BaseDeDonnees : Cette classe gère la connexion à la base de données SQLite
 * qui remplace les anciens fichiers InventaireVehicules.csv et Factures.csv.
 * Elle crée le fichier de base de données et ses tables au premier démarrage,
 * et fournit la connexion partagée utilisée par GestionVehiculesDisponibles et
 * ListeDesFactures.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 02 Juillet 2026
 */
public class BaseDeDonnees {

    private static final String FIC_BASE_DONNEES = "GestionLocation.db";
    private static final String URL_CONNEXION = "jdbc:sqlite:" + FIC_BASE_DONNEES;

    // Données initiales de l'inventaire (les mêmes que l'ancien InventaireVehicules.csv),
    // utilisées uniquement pour créer la table la toute première fois.
    private static final Object[][] INVENTAIRE_INITIAL = {
        {"H", "P", 55.75, 13.50, 12},
        {"H", "I", 60.25, 14.50, 10},
        {"H", "G", 65.50, 15.50, 3},
        {"E", "P", 45.50, 12.50, 11},
        {"E", "I", 50.50, 12.75, 9},
        {"E", "G", 55.25, 13.25, 5}
    };

    private static Connection connexion;
    private static boolean initialisee = false;

    // Empêche l'instanciation : classe utilitaire uniquement composée de méthodes statiques.
    private BaseDeDonnees() {
    }

    /**
     * Initialise la base de données : établit la connexion, crée les tables si
     * elles n'existent pas encore et amorce l'inventaire des véhicules
     * disponibles s'il est vide. Cette méthode doit être appelée une seule
     * fois, au démarrage de l'application.
     *
     * @throws SQLException si la connexion ou la création des tables échoue.
     */
    public static void initialiser() throws SQLException {
        if (initialisee) {
            return;
        }

        obtenirConnexion();
        creerTables();
        amorcerInventaireSiVide();
        amorcerCataloguesSiVide();
        initialisee = true;
    }

    /**
     * Retourne la connexion partagée vers la base de données, en l'ouvrant si
     * nécessaire.
     *
     * @return la connexion active vers la base de données.
     * @throws SQLException si la connexion ne peut pas être établie.
     */
    public static Connection obtenirConnexion() throws SQLException {
        if (connexion == null || connexion.isClosed()) {
            connexion = DriverManager.getConnection(URL_CONNEXION);
            try (Statement instructionCles = connexion.createStatement()) {
                instructionCles.execute("PRAGMA foreign_keys = ON");
            }
        }
        return connexion;
    }

    /**
     * Crée les tables de la base de données si elles n'existent pas déjà.
     *
     * @throws SQLException si la création d'une des tables échoue.
     */
    private static void creerTables() throws SQLException {
        String creerVehiculesDisponibles
                = "CREATE TABLE IF NOT EXISTS vehicules_disponibles ("
                + "  type_vehicule TEXT NOT NULL,"
                + "  grandeur_vehicule TEXT NOT NULL,"
                + "  prix_location_par_jour REAL NOT NULL,"
                + "  prix_assurance_par_jour REAL NOT NULL,"
                + "  nombre_vehicules_disponibles INTEGER NOT NULL,"
                + "  PRIMARY KEY (type_vehicule, grandeur_vehicule)"
                + ")";

        String creerFactures
                = "CREATE TABLE IF NOT EXISTS factures ("
                + "  numero_facture INTEGER PRIMARY KEY,"
                + "  date_facture TEXT NOT NULL,"
                + "  prenom TEXT NOT NULL,"
                + "  nom TEXT NOT NULL,"
                + "  telephone TEXT NOT NULL,"
                + "  permis_conduire TEXT NOT NULL,"
                + "  mode_paiement TEXT NOT NULL,"
                + "  type_carte_credit TEXT,"
                + "  numero_carte_credit TEXT,"
                + "  sous_total REAL NOT NULL,"
                + "  montant_tps REAL NOT NULL,"
                + "  montant_tvq REAL NOT NULL,"
                + "  montant_total REAL NOT NULL"
                + ")";

        String creerVehiculesLoues
                = "CREATE TABLE IF NOT EXISTS vehicules_loues ("
                + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "  numero_facture INTEGER NOT NULL REFERENCES factures(numero_facture) ON DELETE CASCADE,"
                + "  type_vehicule TEXT NOT NULL,"
                + "  grandeur_vehicule TEXT NOT NULL,"
                + "  nombre_vehicules_loues INTEGER NOT NULL,"
                + "  nombre_jours_location INTEGER NOT NULL,"
                + "  date_location TEXT NOT NULL,"
                + "  date_retour TEXT NOT NULL,"
                + "  prix_location_par_jour REAL NOT NULL,"
                + "  rabais REAL NOT NULL,"
                + "  prix_assurance_par_jour REAL NOT NULL,"
                + "  montant_location REAL NOT NULL,"
                + "  montant_assurance REAL NOT NULL"
                + ")";

        String creerTypesVehicules
                = "CREATE TABLE IF NOT EXISTS types_vehicules ("
                + "  code TEXT PRIMARY KEY,"
                + "  description TEXT NOT NULL,"
                + "  actif INTEGER NOT NULL DEFAULT 1"
                + ")";

        String creerGrandeursVehicules
                = "CREATE TABLE IF NOT EXISTS grandeurs_vehicules ("
                + "  code TEXT PRIMARY KEY,"
                + "  description TEXT NOT NULL,"
                + "  ordre_affichage INTEGER NOT NULL DEFAULT 0,"
                + "  actif INTEGER NOT NULL DEFAULT 1"
                + ")";

        String creerReglesRabais
                = "CREATE TABLE IF NOT EXISTS regles_rabais ("
                + "  type_vehicule TEXT NOT NULL,"
                + "  grandeur_vehicule TEXT NOT NULL,"
                + "  seuil_jours INTEGER NOT NULL,"
                + "  pourcentage_rabais REAL NOT NULL,"
                + "  actif INTEGER NOT NULL DEFAULT 1,"
                + "  PRIMARY KEY (type_vehicule, grandeur_vehicule)"
                + ")";

        String creerAdministrateurs
                = "CREATE TABLE IF NOT EXISTS administrateurs ("
                + "  identifiant TEXT PRIMARY KEY,"
                + "  mot_de_passe_hache TEXT NOT NULL,"
                + "  date_creation TEXT NOT NULL"
                + ")";

        try (Statement instruction = obtenirConnexion().createStatement()) {
            instruction.execute(creerVehiculesDisponibles);
            instruction.execute(creerFactures);
            instruction.execute(creerVehiculesLoues);
            instruction.execute(creerTypesVehicules);
            instruction.execute(creerGrandeursVehicules);
            instruction.execute(creerReglesRabais);
            instruction.execute(creerAdministrateurs);
        }
    }

    /**
     * Amorce la table des véhicules disponibles avec les données de départ,
     * mais seulement si elle est vide (par exemple lors de la toute première
     * exécution).
     *
     * @throws SQLException si l'insertion des données de départ échoue.
     */
    private static void amorcerInventaireSiVide() throws SQLException {
        String compter = "SELECT COUNT(*) FROM vehicules_disponibles";
        try (Statement instruction = obtenirConnexion().createStatement()) {
            var resultat = instruction.executeQuery(compter);
            if (resultat.next() && resultat.getInt(1) > 0) {
                return;
            }
        }

        String insertion = "INSERT INTO vehicules_disponibles "
                + "(type_vehicule, grandeur_vehicule, prix_location_par_jour, prix_assurance_par_jour, nombre_vehicules_disponibles) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (var instructionPreparee = obtenirConnexion().prepareStatement(insertion)) {
            for (Object[] ligne : INVENTAIRE_INITIAL) {
                instructionPreparee.setString(1, (String) ligne[0]);
                instructionPreparee.setString(2, (String) ligne[1]);
                instructionPreparee.setDouble(3, (Double) ligne[2]);
                instructionPreparee.setDouble(4, (Double) ligne[3]);
                instructionPreparee.setInt(5, (Integer) ligne[4]);
                instructionPreparee.addBatch();
            }
            instructionPreparee.executeBatch();
        }
    }

    /**
     * Amorce les tables de référence du catalogue (types de véhicules,
     * grandeurs, règles de rabais, compte administrateur), mais seulement si
     * elles sont vides. Reproduit exactement les valeurs qui étaient
     * auparavant codées en dur (Hybride/Électrique, Petit/Intermédiaire/Grand,
     * rabais de 20% après 15 jours sur Électrique Petit/Intermédiaire), afin
     * que le comportement existant ne change pas après cette migration.
     *
     * @throws SQLException si l'insertion des données de départ échoue.
     */
    private static void amorcerCataloguesSiVide() throws SQLException {
        if (estVide("types_vehicules")) {
            String insertion = "INSERT INTO types_vehicules (code, description, actif) VALUES (?, ?, 1)";
            try (var instructionPreparee = obtenirConnexion().prepareStatement(insertion)) {
                instructionPreparee.setString(1, "H");
                instructionPreparee.setString(2, "Hybride");
                instructionPreparee.addBatch();
                instructionPreparee.setString(1, "E");
                instructionPreparee.setString(2, "Électrique");
                instructionPreparee.addBatch();
                instructionPreparee.executeBatch();
            }
        }

        if (estVide("grandeurs_vehicules")) {
            String insertion = "INSERT INTO grandeurs_vehicules (code, description, ordre_affichage, actif) VALUES (?, ?, ?, 1)";
            try (var instructionPreparee = obtenirConnexion().prepareStatement(insertion)) {
                instructionPreparee.setString(1, "P");
                instructionPreparee.setString(2, "Petit");
                instructionPreparee.setInt(3, 1);
                instructionPreparee.addBatch();
                instructionPreparee.setString(1, "I");
                instructionPreparee.setString(2, "Intermédiaire");
                instructionPreparee.setInt(3, 2);
                instructionPreparee.addBatch();
                instructionPreparee.setString(1, "G");
                instructionPreparee.setString(2, "Grand");
                instructionPreparee.setInt(3, 3);
                instructionPreparee.addBatch();
                instructionPreparee.executeBatch();
            }
        }

        if (estVide("regles_rabais")) {
            String insertion = "INSERT INTO regles_rabais (type_vehicule, grandeur_vehicule, seuil_jours, pourcentage_rabais, actif) "
                    + "VALUES (?, ?, ?, ?, 1)";
            try (var instructionPreparee = obtenirConnexion().prepareStatement(insertion)) {
                instructionPreparee.setString(1, "E");
                instructionPreparee.setString(2, "P");
                instructionPreparee.setInt(3, 15);
                instructionPreparee.setDouble(4, 0.20);
                instructionPreparee.addBatch();
                instructionPreparee.setString(1, "E");
                instructionPreparee.setString(2, "I");
                instructionPreparee.setInt(3, 15);
                instructionPreparee.setDouble(4, 0.20);
                instructionPreparee.addBatch();
                instructionPreparee.executeBatch();
            }
        }

        if (estVide("administrateurs")) {
            String insertion = "INSERT INTO administrateurs (identifiant, mot_de_passe_hache, date_creation) VALUES (?, ?, ?)";
            try (var instructionPreparee = obtenirConnexion().prepareStatement(insertion)) {
                instructionPreparee.setString(1, "admin");
                instructionPreparee.setString(2, Hachage.sha256Hex("admin"));
                instructionPreparee.setString(3, java.time.LocalDateTime.now().toString());
                instructionPreparee.executeUpdate();
            }
            System.out.println("[SÉCURITÉ] Compte administrateur par défaut créé (identifiant: admin, mot de passe: admin). Changez ce mot de passe dans l'interface d'administration !");
        }
    }

    /**
     * Vérifie si une table est vide.
     *
     * @param nomTable le nom de la table à vérifier.
     * @return vrai si la table ne contient aucune ligne.
     * @throws SQLException si la requête échoue.
     */
    private static boolean estVide(String nomTable) throws SQLException {
        try (Statement instruction = obtenirConnexion().createStatement();
                var resultat = instruction.executeQuery("SELECT COUNT(*) FROM " + nomTable)) {
            return resultat.next() && resultat.getInt(1) == 0;
        }
    }

    /**
     * Ferme la connexion à la base de données si elle est ouverte.
     */
    public static void fermer() {
        if (connexion != null) {
            try {
                connexion.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la base de données : " + e.getMessage());
            }
        }
    }
}
