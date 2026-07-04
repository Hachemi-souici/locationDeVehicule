/**
 * Règles de validation des saisies utilisateur pour l'interface graphique.
 * Réimplémente les mêmes règles que les méthodes "entrerValiderXxx" de
 * ApplicationPrincipale, mais sans dépendance à Scanner/System.in : ces
 * méthodes sont donc utilisables depuis des composants Swing.
 *
 * @author Hachemi Souici
 */
public class Validation {

    public static final int NOM_MIN_LONGUEUR = 2;
    public static final int NOM_MAX_LONGUEUR = 30;
    public static final int JOURS_MIN = 1;
    public static final int JOURS_MAX = 30;
    public static final int VEHICULES_MIN = 0;
    public static final int VEHICULES_MAX = 5;

    private static final String MODELE_TELEPHONE = "\\(\\d{3}\\) \\d{3}-\\d{4}";
    private static final String MODELE_PERMIS = "[a-zA-Z]\\d{4}-\\d{6}-\\d{2}";
    private static final String MODELE_CARTE_CREDIT = "\\d{4} \\d{4} \\d{4} \\d{4}";

    private Validation() {
    }

    public static boolean nomValide(String nom) {
        return nom != null && nom.length() >= NOM_MIN_LONGUEUR && nom.length() <= NOM_MAX_LONGUEUR;
    }

    public static boolean telephoneValide(String telephone) {
        return telephone != null && telephone.matches(MODELE_TELEPHONE);
    }

    public static boolean permisValide(String permis) {
        return permis != null && permis.matches(MODELE_PERMIS) && permis.length() == 15;
    }

    public static boolean carteCreditValide(String numeroCarte) {
        return numeroCarte != null && numeroCarte.matches(MODELE_CARTE_CREDIT);
    }

    public static boolean joursValides(int nombreJours) {
        return nombreJours >= JOURS_MIN && nombreJours <= JOURS_MAX;
    }

    public static boolean nombreVehiculesValide(int nombreVehicules) {
        return nombreVehicules >= VEHICULES_MIN && nombreVehicules <= VEHICULES_MAX;
    }
}
