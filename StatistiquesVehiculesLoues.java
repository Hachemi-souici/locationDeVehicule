
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private static List<VehiculeLoue> lesVehiculesLoues = new ArrayList<>();

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
        // Parcourir la liste des véhicules loués
        for (VehiculeLoue existant : lesVehiculesLoues) {
            if (existant.getVehicule().getTypeVehicule() == vehiculeLoue.getVehicule().getTypeVehicule()
                    && existant.getVehicule().getGrandeurVehicule() == vehiculeLoue.getVehicule().getGrandeurVehicule()) {
                // Ajouter le nombre de véhicules loués
                existant.setNombreVehiculeLoue(existant.getNombreVehiculesLoue() + vehiculeLoue.getNombreVehiculesLoue());
                return;
            }
        }

        // Aucun véhicule loué correspondant trouvé : l'ajouter à la liste
        lesVehiculesLoues.add(vehiculeLoue);
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

        List<Character> types = CatalogueVehicules.obtenirTypesActifs();
        List<Character> grandeurs = CatalogueVehicules.obtenirGrandeursActives();

        // Entêtes des colonnes (générées dynamiquement selon les types actifs du catalogue)
        StringBuilder entete = new StringBuilder(String.format("    %-18s", "Grandeur"));
        for (char type : types) {
            entete.append(String.format("%-13s", CatalogueVehicules.obtenirDescriptionType(type)));
        }
        System.out.println(entete);
        System.out.println("    " + "*".repeat(Math.max(0, entete.length() - 4)));

        for (char grandeur : grandeurs) {
            StringBuilder ligne = new StringBuilder(String.format("    %-18s", CatalogueVehicules.obtenirDescriptionGrandeur(grandeur)));
            for (char type : types) {
                ligne.append(String.format("%-13d", obtenirNombreVehiculesLoues(type, grandeur)));
            }
            System.out.println(ligne);
        }

        // Fin de tableau
        System.out.println(Facture.CADRE);

    }

}
