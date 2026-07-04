
/**
 * Représente un véhicule disponible à la location, incluant le nombre de véhicules en stock.
 *
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 */
public class VehiculeDisponible {

    private Vehicule vehicule;
    private int nombreVehiculesDisponibles;

    /**
     * Constructeur de la classe VehiculeDisponible.
     *
     * @param vehicule Le véhicule disponible.
     * @param nombreVehiculeDisponibles Le nombre de véhicules disponibles.
     */
    public VehiculeDisponible(Vehicule vehicule, int nombreVehiculeDisponibles) {
        this.vehicule = vehicule;
        this.nombreVehiculesDisponibles = nombreVehiculeDisponibles;
    }

    /**
     * Retourne le véhicule disponible.
     *
     * @return Le véhicule disponible.
     */
    public Vehicule getVehicule() {
        return vehicule;
    }

    /**
     * Retourne le nombre de véhicules disponibles.
     *
     * @return Le nombre de véhicules disponibles.
     */
    public int getNombreVehiculesDisponibles() {
        return nombreVehiculesDisponibles;
    }

    /**
     * Met à jour le nombre de véhicules disponibles.
     *
     * @param nombreVehiculesDisponibles Le nouveau nombre de véhicules
     * disponibles.
     */
    public void setNombreVehiculesDisponibles(int nombreVehiculesDisponibles) {
        this.nombreVehiculesDisponibles = nombreVehiculesDisponibles;
    }

}
