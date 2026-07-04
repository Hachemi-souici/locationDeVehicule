
import java.time.LocalDateTime;

/**
 * votre classe VehiculeLoue gere la creation et la modification des vehicule
 * loue et aussi calculer le rabais
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 */
public class VehiculeLoue {

    public static final float RABAIS = 0.20f;
    public static final int JOURS_POUR_RABAIS = 15;

    private Vehicule vehicule;
    private int nombreVehiculesLoue;
    private int nombreJoursLocation;
    private LocalDateTime dateLocation;

    /**
     * Constructeur de la classe VehiculeLoue. Initialise une location de
     * véhicule avec les détails fournis.
     *
     * @param vehicule Le véhicule loué.
     * @param nombreVehiculesLoue Le nombre de véhicules loués.
     * @param nombreJoursLocation Le nombre de jours de location.
     * @param dateLocation La date de début de la location.
     */
    public VehiculeLoue(Vehicule vehicule, int nombreVehiculesLoue, int nombreJoursLocation, LocalDateTime dateLocation) {
        this.vehicule = vehicule;
        this.nombreVehiculesLoue = nombreVehiculesLoue;
        this.nombreJoursLocation = nombreJoursLocation;
        this.dateLocation = dateLocation;
    }

    /**
     * Définit le nombre de véhicules loués.
     *
     * @param nombreDeVehiculesLoue Le nouveau nombre de véhicules loués.
     */
    public void setNombreVehiculeLoue(int nombreDeVehiculesLoue) {
        this.nombreVehiculesLoue = nombreVehiculesLoue;
    }

    /**
     * Retourne le véhicule loué.
     *
     * @return Le véhicule loué.
     */
    public Vehicule getVehicule() {
        return vehicule;
    }

    /**
     * Retourne le nombre de véhicules loués.
     *
     * @return Le nombre de véhicules loués.
     */
    public int getNombreVehiculesLoue() {
        return nombreVehiculesLoue;
    }

    /**
     * Retourne le nombre de jours de location.
     *
     * @return Le nombre de jours de location.
     */
    public int getNombreJoursLocation() {
        return nombreJoursLocation;
    }

    /**
     * Retourne la date de début de la location.
     *
     * @return La date de début de la location.
     */
    public LocalDateTime getDateLocation() {
        return dateLocation;
    }

    /**
     * Calcule et retourne la date de retour du véhicule en location de la durée
     * de location.
     *
     * @return La date de retour du véhicule.
     */
    public LocalDateTime calculerDateRetour() {
        LocalDateTime dateRetour = dateLocation.plusDays(nombreJoursLocation);
        return dateRetour;
    }

    /**
     * Calcule le rabais applicable à la location en fonction du type et de la
     * grandeur du véhicule ainsi que du nombre de jours de location.
     *
     * @return Le montant du rabais appliqué à la location.
     */
    public float calculerRabais() {
        float prixLocationAvecRabais = 0;
        if (nombreJoursLocation > JOURS_POUR_RABAIS && vehicule.getTypeVehicule() == Vehicule.ELECTRIQUE
                && (vehicule.getGrandeurVehicule() == Vehicule.PETIT || vehicule.getGrandeurVehicule() == Vehicule.INTERMEDIAIRE)) {
            prixLocationAvecRabais = vehicule.getPrixLocationParJour() * RABAIS;
        } else {
            prixLocationAvecRabais = 0;
        }
        return prixLocationAvecRabais;
    }

}
