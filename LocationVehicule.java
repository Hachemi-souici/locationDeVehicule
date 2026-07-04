
/**
 * LocationVehicule Représente une location de véhicules par un locataire.
 * Gère les véhicules loués et leur stockage dans un tableau.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 */
public class LocationVehicule {

    //Nombre maximal de véhicules pouvant être loués
    public static final int NOMBRE_MAX_DE_LOCATION = 50;

    private Locataire locataire;
    private VehiculeLoue[] tableauVehiculesLoues;

    /**
     * Constructeur de la classe LocationVehicule. Initialise un tableau pour
     * stocker les véhicules loués.
     */
    public LocationVehicule() {
        tableauVehiculesLoues = new VehiculeLoue[NOMBRE_MAX_DE_LOCATION];
    }

    /**
     * Retourne le locataire associé à cette location.
     *
     * @return Le locataire.
     */
    public Locataire getLocataire() {
        return locataire;
    }

    /**
     * Retourne le tableau des véhicules loués.
     *
     * @return Un tableau contenant les véhicules loués.
     */
    public VehiculeLoue[] getTableauVehiculesLoues() {
        return tableauVehiculesLoues;
    }

    /**
     * Calcule le nombre de types de véhicules loués.
     *
     * @return Le nombre de types de véhicules loués.
     */
    public int nombreTypesVehiculesLoues() {
        int compteur = 0;
        for (VehiculeLoue vehiculeLoue : tableauVehiculesLoues) {
            if (vehiculeLoue != null) {
                compteur++;
            }
        }
        return compteur;
    }

    /**
     * Ajoute un véhicule loué à la liste s'il n'est pas déjà présent.
     *
     * @param vehiculeLoue Le véhicule loué à ajouter.
     * @return `true` si l'ajout est réussi, `false` si le véhicule est déjà
     * présent.
     */
    public boolean ajouterVehiculeLoue(VehiculeLoue vehiculeLoue) {

        for (VehiculeLoue v : tableauVehiculesLoues) {
            if (v != null && v.getVehicule().equals(vehiculeLoue.getVehicule())) {
                return false; // Le véhicule est déjà présent
            }
        }
        int indexLibre = nombreTypesVehiculesLoues();
        if (indexLibre < tableauVehiculesLoues.length) {
            tableauVehiculesLoues[indexLibre] = vehiculeLoue;
            return true;
        }
        return false;
    }

    /**
     * Trouve la position d'un véhicule loué en fonction de son type et de sa
     * grandeur.
     *
     * @param typeVehicule Le type du véhicule recherché.
     * @param grandeurVehicule La grandeur du véhicule recherché.
     * @return L'index du véhicule dans le tableau ou `-1` si non trouvé.
     */
    public int obtenirPositiondeVehiculeLoue(char typeVehicule, char grandeurVehicule) {
        for (int i = 0; i < tableauVehiculesLoues.length; i++) {
            VehiculeLoue vehiculeLoue = tableauVehiculesLoues[i];
            if (vehiculeLoue != null && vehiculeLoue.getVehicule().getTypeVehicule() == typeVehicule
                    && vehiculeLoue.getVehicule().getGrandeurVehicule() == grandeurVehicule) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Définit le locataire associé à cette location.
     *
     * @param locataire Le locataire.
     */
    public void setLocataire(Locataire locataire) {
        this.locataire = locataire;
    }

}
