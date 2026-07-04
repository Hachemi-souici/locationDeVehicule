
import java.time.LocalDateTime;

/**
 * Université du Québec à Montréal (UQAM) INF1120 - 010 - Hiver 2025 Travail
 * pratique 3
 *
 * StatistiqueVentes : Cette classe contient le nombre de véhicules hybrides et
 * électriques loués
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 *
 */
public class StatistiquesVehiculesLoues {

    // Déclaration des variables de classe
    public static final String INVENTAIRE_VEHICULES_LOUES = """
        Nombre de véhicules loués par type et par catégorie
          ***************************************************
        """;
    public static final String CATEGORIE_VEHICULES = """
        Grandeur           Hybride        Électrique
          ********************************************""";
    private static VehiculeLoue[] lesVehiculesLoues = new VehiculeLoue[6];

    /**
     * Augmenter le nombre de véhicules loués par type et par grandeur de
     * véhicule.
     *
     * La méthode doit trouver le véhicule loué dans le tableau des véhicules
     * loués (lesVehiculesLoues) dont le type et la grandeur sont les mêmes que
     * le type et la grandeur du véhicule loué passé en paramètre, ensuite elle
     * doit ajouter le nombre de véhicules loués du véhicule loué passé en
     * paramètre au nombre de véhicule loué dans le tableau des véhicules loués.
     *
     * Si aucun véhicule loué n'est trouvé dans le tableau des véhicules loués
     * qui correspond au véhicule loué passé en paramètre, le véhicule loué
     * passé en paramètre est ajouté dans le tableau des véhicules loués à la
     * prochaine position libre.
     *
     * @param vehiculeLoue le véhicule loué
     */
    public static void augmenterNombreVehiculesLoues(VehiculeLoue vehiculeLoue) {
        // Parcourir le tableau des véhicules loués
        for (int i = 0; i < lesVehiculesLoues.length; i++) {
            VehiculeLoue existant = lesVehiculesLoues[i];
            if (existant != null
                    && existant.getVehicule().getTypeVehicule() == vehiculeLoue.getVehicule().getTypeVehicule()
                    && existant.getVehicule().getGrandeurVehicule() == vehiculeLoue.getVehicule().getGrandeurVehicule()) {
                // Ajouter le nombre de véhicules loués
                existant.setNombreVehiculeLoue(existant.getNombreVehiculesLoue() + vehiculeLoue.getNombreVehiculesLoue());
                //return; // Terminer une fois que la mise à jour est faite

            }

        }

        for (int i = 0; i < lesVehiculesLoues.length; i++) {
            if (lesVehiculesLoues[i] == null) {
                lesVehiculesLoues[i] = vehiculeLoue;
                return; // Terminer après avoir ajouté le véhicule
            }
        }

        // Si le tableau est plein (aucune position libre), afficher un message d'erreur
        System.err.println("Erreur : Le tableau des véhicules loués est plein !");

    }

    /**
     * Obtenir le nombre de véhicules loués.
     *
     * La méthode doit trouver le véhicule loués dans le tableau des véhicules
     * loués (lesVehiculesLoues) dont le type et la grandeur sont les mêmes que
     * le type et la grandeur passés en paramètres. Ensuite elle doit retourner
     * le nombre de véhicules disponibles.
     *
     * @param typeVehicule le type du véhicule loué
     * @param grandeurVehicule la grandeur du véhicule loué
     * @return le nombre de véhicules disponibles ou 0 si aucun véhicule trouvé
     */
    public static int obtenirNombreVehiculesLoues(char typeVehicule, char grandeurVehicule) {

        // Parcourir le tableau des véhicules loués
        for (VehiculeLoue vehiculeLoue : lesVehiculesLoues) {
            if (vehiculeLoue != null
                    && vehiculeLoue.getVehicule().getTypeVehicule() == typeVehicule
                    && vehiculeLoue.getVehicule().getGrandeurVehicule() == grandeurVehicule) {
                // Retourner le nombre de véhicules loués si trouvé
                return vehiculeLoue.getNombreVehiculesLoue();
            }
        }
        // Retourner 0 si aucun véhicule correspondant n'est trouvé
        return 0;
    }

    /**
     * La méthode doit afficher le nombre de véhicules hybrides et électriques
     * loués par type et par grandeur. Pour plus de détails sur l'affichage,
     * voir les exemples de la trace d'exécution du programme fournis avec
     * l'énoncé du Travail pratique 3.
     */
    public static void afficherNombreVehiculesLoues() {
        LocalDateTime dateHeureActuelle = LocalDateTime.now();

        System.out.printf("%n%s%n", Facture.CADRE);
        System.out.printf("    %s%n", Facture.NOM_RVV);
        System.out.printf("    Adresse :       %s%n", Facture.ADRESSE);
        System.out.printf("    Téléphone :     %s%n", Facture.TELEPHONE);
        System.out.printf("    Date et Heure : %s%n", dateHeureActuelle.format(Facture.formatter));
        System.out.printf("%s%n%n", Facture.CADRE);

        // Titre de la section
        System.out.println("    Nombre de véhicules loués par type et par catégorie");
        System.out.println("    ***************************************************");

        // Entêtes des colonnes
        System.out.println("    Grandeur           Hybride        Électrique");
        System.out.println("    ********************************************");

        // Afficher les données pour chaque grandeur de véhicule
        String[] grandeurs = {"Petit", "Intermédiaire", "Grand"};
        char[] codesGrandeurs = {Vehicule.PETIT, Vehicule.INTERMEDIAIRE, Vehicule.GRAND};

        for (int i = 0; i < grandeurs.length; i++) {
            int nbHybrides = obtenirNombreVehiculesLoues(Vehicule.HYBRIDE, codesGrandeurs[i]);
            int nbElectriques = obtenirNombreVehiculesLoues(Vehicule.ELECTRIQUE, codesGrandeurs[i]);
            System.out.printf("    %-18s %-12d %-12d%n", grandeurs[i], nbHybrides, nbElectriques);
        }

        // Fin de tableau
        System.out.println(Facture.CADRE);

    }

}
