
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Université du Québec à Montréal (UQAM) INF1120 - 010 - Hiver 2025 Travail
 * pratique 3
 *
 * GestionVehiculesDisponibles : Cette classe gère le nombre de véhicules
 * disponibles pour la location dans l'inventaire des véhicules.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 *
 */
public class GestionVehiculesDisponibles {

    // Déclaration des constantes
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static List<VehiculeDisponible> lesVehiculesDisponibles = new ArrayList<>();

    /**
     * Charge les données des différents véhicules disponibles à partir de la
     * table "vehicules_disponibles" de la base de données (qui remplace
     * l'ancien fichier InventaireVehicules.csv). Chaque ligne de la table
     * contient : - Le type du véhicule - La grandeur du véhicule - Le prix de
     * la location du véhicule par jour - Le prix de l'assurance du véhicule par
     * jour - Le nombre de véhicules disponibles
     *
     * Chacune de ces lignes est lue pour créer un objet de type
     * VehiculeDisponible, qui est ensuite ajouté dans le tableau des véhicules
     * disponibles.
     */
    public static void chargerVehiculesDisponibles() {
        String requete = "SELECT type_vehicule, grandeur_vehicule, prix_location_par_jour, "
                + "prix_assurance_par_jour, nombre_vehicules_disponibles FROM vehicules_disponibles";

        lesVehiculesDisponibles.clear();

        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();
            try (Statement instruction = connexion.createStatement(); ResultSet resultat = instruction.executeQuery(requete)) {

                while (resultat.next()) {
                    char typeVehicule = resultat.getString("type_vehicule").charAt(0);
                    char grandeurVehicule = resultat.getString("grandeur_vehicule").charAt(0);
                    float prixLocationParJour = (float) resultat.getDouble("prix_location_par_jour");
                    float prixAssuranceParJour = (float) resultat.getDouble("prix_assurance_par_jour");
                    int nombreVehicules = resultat.getInt("nombre_vehicules_disponibles");

                    Vehicule vehicule = new Vehicule(typeVehicule, grandeurVehicule, prixLocationParJour, prixAssuranceParJour);
                    VehiculeDisponible vehiculeDisponible = new VehiculeDisponible(vehicule, nombreVehicules);
                    lesVehiculesDisponibles.add(vehiculeDisponible);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur d'accès à la base de données lors du chargement de l'inventaire : " + e.getMessage());
        }
    }

    /**
     * Ajoute une nouvelle combinaison type/grandeur de véhicule à
     * l'inventaire, ou met à jour ses prix et son stock si elle existe déjà.
     * Utilisée uniquement par l'interface d'administration.
     *
     * @param typeVehicule le type du véhicule
     * @param grandeurVehicule la grandeur du véhicule
     * @param prixLocationParJour le prix de location par jour
     * @param prixAssuranceParJour le prix de l'assurance par jour
     * @param nombreVehiculesDisponibles le nombre de véhicules disponibles
     * @throws SQLException si l'opération échoue
     */
    public static void ajouterOuMettreAJourVehiculeDisponible(char typeVehicule, char grandeurVehicule,
            float prixLocationParJour, float prixAssuranceParJour, int nombreVehiculesDisponibles) throws SQLException {
        String requete = "INSERT INTO vehicules_disponibles "
                + "(type_vehicule, grandeur_vehicule, prix_location_par_jour, prix_assurance_par_jour, nombre_vehicules_disponibles) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT(type_vehicule, grandeur_vehicule) DO UPDATE SET "
                + "prix_location_par_jour = excluded.prix_location_par_jour, "
                + "prix_assurance_par_jour = excluded.prix_assurance_par_jour, "
                + "nombre_vehicules_disponibles = excluded.nombre_vehicules_disponibles";

        try (var instruction = BaseDeDonnees.obtenirConnexion().prepareStatement(requete)) {
            instruction.setString(1, String.valueOf(typeVehicule));
            instruction.setString(2, String.valueOf(grandeurVehicule));
            instruction.setDouble(3, prixLocationParJour);
            instruction.setDouble(4, prixAssuranceParJour);
            instruction.setInt(5, nombreVehiculesDisponibles);
            instruction.executeUpdate();
        }

        chargerVehiculesDisponibles();
    }

    /**
     * Obtenir le prix de la location du véhicule par jour. Cette méthode doit
     * trouver le véhicule dans le tableau des véhicules disponibles
     * (lesVehiculesDipsonibles) dont le type et la grandeur sont les mêmes que
     * le type et de la grandeur du véhicule passés en paramètres, ensuite elle
     * doit retourner le prix de la location du véhicule par jour.
     *
     * @param typeVehicule le type du véhicule
     * @param grandeurVehicule la grandeur du véhicule
     * @return le prix de la location du véhicule par jour ou 0 si aucun
     * véhicule trouvé
     */
    public static float obtenirPrixLocationVehParJour(char typeVehicule, char grandeurVehicule) {
        for (VehiculeDisponible vehiculeDisponible : lesVehiculesDisponibles) {
            if (vehiculeDisponible != null
                    && vehiculeDisponible.getVehicule().getTypeVehicule() == typeVehicule
                    && vehiculeDisponible.getVehicule().getGrandeurVehicule() == grandeurVehicule) {
                return vehiculeDisponible.getVehicule().getPrixLocationParJour();
            }
        }
        return 0.0f;
    }

    /**
     * Obtenir le prix de l'assurance du véhicule par jour. Cette méthode doit
     * trouver le véhicule dans le tableau des véhicules disponibles
     * (lesVehiculesDipsonibles) dont le type et la grandeur sont les mémes que
     * le type et de la grandeur du véhicule passés en paramètres, ensuite elle
     * doit retourner le prix de l'assurance du véhicule par jour. Si le
     * paramètre "AssuranceEstZero" est true, le prix de l'assurance doit être
     * 0, sinon la méthode retourne le prix de l'assurance trouvé dans le
     * tableau des véhicules disponibles
     *
     * @param typeVehicule le type du véhicule
     * @param grandeurVehicule la grandeur du véhicule
     * @param assuranceEstZero un indicateur pour savoir s'il faut calculer
     * l'assurance ou la mettre à 0 ou non
     * @return le prix de l'assurance du véhicule par jour ou 0 si aucun
     * véhicule trouvé
     */
    public static float obtenirPrixAssuranceVehParJour(char typeVehicule, char grandeurVehicule,
            boolean assuranceEstZero) {
        if (!assuranceEstZero) {
            for (VehiculeDisponible vehiculeDisponible : lesVehiculesDisponibles) {
                if (vehiculeDisponible != null
                        && vehiculeDisponible.getVehicule().getTypeVehicule() == typeVehicule
                        && vehiculeDisponible.getVehicule().getGrandeurVehicule() == grandeurVehicule) {
                    return vehiculeDisponible.getVehicule().getPrixAssuranceParJour();
                }
            }
        }
        return 0.0f;
    }

    /**
     * Diminuer le nombre de véhicules disponibles dans le tableau des véhicules
     * disponibles.
     *
     * La méthode doit trouver le véhicule dans le tableau des véhicules
     * disponibles dont le type et la grandeur sont les mêmes que le type et la
     * grandeur passés en paramètres, ensuite elle fait le nombre de véhicules
     * disponibles moins le nombre de véhicules loués.
     *
     * Elle retourne vrai si la diminution a été effectuée avec succès, sinon
     * faux.
     *
     * @param typeVehicule le type du véhicule
     * @param grandeurVehicule la grandeur du véhicule
     * @param nombrebVehiculesLoues le nombre de véhicules loués
     * @return vrai si la diminution est faite, sinon faux
     */
    public static boolean diminuerNombreVehiculesDisponibles(char typeVehicule,
            char grandeurVehicule, int nombreVehiculesLoue) {
        for (VehiculeDisponible vehiculeDisponible : lesVehiculesDisponibles) {
            if (vehiculeDisponible != null
                    && vehiculeDisponible.getVehicule().getTypeVehicule() == typeVehicule
                    && vehiculeDisponible.getVehicule().getGrandeurVehicule() == grandeurVehicule) {

                // Vérifiez que la diminution peut être effectuée
                int nombreVehiculesDisponibles = vehiculeDisponible.getNombreVehiculesDisponibles();
                if (nombreVehiculesLoue > nombreVehiculesDisponibles) {
                    System.err.println("Erreur : Nombre insuffisant de véhicules disponibles.");
                    return false;
                }

                // Effectuer la diminution
                vehiculeDisponible.setNombreVehiculesDisponibles(nombreVehiculesDisponibles - nombreVehiculesLoue);
                return true; // Succès
            }
        }

        return false;
    }

    /**
     * Obtenir le nombre de véhicules disponibles. Cette méthode doit trouver le
     * véhicule dans le tableau des véhicules disponibles
     * (lesVehiculesDipsonibles) dont le type et la grandeur sont les mêmes que
     * le type et de la grandeur du véhicule passés en paramètres. Ensuite elle
     * doit retourner le nombre de véhciules disponibles.
     *
     * @param typeVehicule le type du véhicule
     * @param grandeurVehicule la grandeur du véhicule
     * @return le nombre de véhicules disponibles ou 0 si aucun véhicule trouvé
     */
    public static int obtenirNombreVehiculesDisponibles(char typeVehicule, char grandeurVehicule) {

        for (VehiculeDisponible vehiculeDisponible : lesVehiculesDisponibles) {
            if (vehiculeDisponible != null
                    && vehiculeDisponible.getVehicule().getTypeVehicule() == typeVehicule
                    && vehiculeDisponible.getVehicule().getGrandeurVehicule() == grandeurVehicule) {
                return vehiculeDisponible.getNombreVehiculesDisponibles();
            }
        }

        return 0;
    }

    /**
     * Vérifier la disponibilité des véhicules disponibles
     * (lesVehiculesDipsonibles).
     *
     * La méthode doit trouver le véhicule dans le tableau des véhicules
     * disponibles (lesVehiculesDipsonibles) dont le type et la grandeur sont
     * les mêmes que le type et de la grandeur du véhicule passés en paramètres.
     * Ensuite elle doit retourner vrai si le nombre de véhicules loués passé en
     * paramètre (nbVehciculesLoues) est inférieur ou égal au nombre de
     * véhicules disponibles, sinon faux.
     *
     * @param typeVehicule le type du véhicule
     * @param grandeurVehicule la grandeur du véhicule
     * @param nbVehciculesLoues le nombre de véhicules dont la disponibilité
     * doit être vérifiée
     * @return vrai si le nombre de véhicules passé en paramètre est inférieur
     * ou égal au nombre de véhicules disponibles, sinon faux.
     */
    public static boolean estDisponible(char typeVehicule,
            char grandeurVehicule, int nombreVehiculesLoue) {
        int nombreDisponibles = obtenirNombreVehiculesDisponibles(typeVehicule, grandeurVehicule);
        if (nombreVehiculesLoue <= nombreDisponibles) {
            return true;
        }
        return false;
    }

    /**
     * Afficher les différents véhicules disponibles dans le tableau des
     * véhicules disponibles. Pour plus de détails sur l'affichage, voir les
     * exemples de la trace d'exécution du programme fournis avec l'énoncé du
     * Travail pratique 3."
     */
    public static void afficher() {
        LocalDateTime dateHeureActuelle = LocalDateTime.now();

        System.out.printf("%n%s%n", Facture.CADRE);
        System.out.printf("    %s%n", Facture.NOM_RVV);
        System.out.printf("    Adresse :       %s%n", Facture.ADRESSE);
        System.out.printf("    Téléphone :     %s%n", Facture.TELEPHONE);
        System.out.printf("    Date et Heure : %s%n", dateHeureActuelle.format(formatter));
        System.out.printf("%s%n%n", Facture.CADRE);

        // Titre de la section
        System.out.println("    Nombre de véhicules disponibles dans l'inventaire");
        System.out.println("    ***************************************************");

        List<Character> types = CatalogueVehicules.obtenirTypesActifs();
        List<Character> grandeurs = CatalogueVehicules.obtenirGrandeursActives();

        // Entêtes des colonnes (générées dynamiquement selon les types actifs du catalogue)
        StringBuilder entete = new StringBuilder(String.format("    %-16s", "Grandeur"));
        for (char type : types) {
            entete.append(String.format("%-15s", CatalogueVehicules.obtenirDescriptionType(type)));
        }
        System.out.println(entete);
        System.out.println("    " + "*".repeat(Math.max(0, entete.length() - 4)));

        for (char grandeur : grandeurs) {
            StringBuilder ligne = new StringBuilder(String.format("    %-16s", CatalogueVehicules.obtenirDescriptionGrandeur(grandeur)));
            for (char type : types) {
                ligne.append(String.format("%-15d", obtenirNombreVehiculesDisponibles(type, grandeur)));
            }
            System.out.println(ligne);
        }

        // Afficher la fin de l'encadré
        System.out.printf("%s%n%n", Facture.CADRE);

    }

}
