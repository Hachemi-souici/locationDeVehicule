
/**
 * Vehicule représente un véhicule avec son type, sa grandeur et ses tarifs.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 */
public class Vehicule {

    public static final char HYBRIDE = 'H';
    public static final char ELECTRIQUE = 'E';
    public static final char PETIT = 'P';
    public static final char INTERMEDIAIRE = 'I';
    public static final char GRAND = 'G';

    public static final String DISCRIPTION_HYBRIDE = "Hybride";
    public static final String DISCRIPTION_ELECTRIQUE = "Électrique";
    public static final String DISCRIPTION_PETIT = "Petit";
    public static final String DISCRIPTION_INTERMIDIAIRE = "Intermédiaire";
    public static final String DISCRIPTION_GRANDE = "Grande";

    private char typeVehicule;
    private char grandeurVehicule;
    private float prixLocationParJour;
    private float prixAssuranceParJour;

    /**
     * Constructeur de la classe Vehicule.
     *
     * @param typeVehicule Le type de véhicule (Hybride ou Électrique).
     * @param grandeurVehicule La taille du véhicule (Petit, Intermédiaire,
     * Grand).
     * @param prixLocationParJour Le prix de location par jour.
     * @param prixAssuranceParJour Le prix de l’assurance par jour.
     */
    public Vehicule(char typeVehicule, char grandeurVehicule, float prixLocationParJour, float prixAssuranceParJour) {
        this.typeVehicule = typeVehicule;
        this.grandeurVehicule = grandeurVehicule;
        this.prixLocationParJour = prixLocationParJour;
        this.prixAssuranceParJour = prixAssuranceParJour;

    }

    /**
     * Retourne le type du véhicule.
     *
     * @return Le type du véhicule.
     */
    public char getTypeVehicule() {
        return typeVehicule;
    }

    /**
     * Retourne la grandeur du véhicule.
     *
     * @return La grandeur du véhicule.
     */
    public char getGrandeurVehicule() {
        return grandeurVehicule;
    }

    /**
     * Retourne le prix de location par jour.
     *
     * @return Le prix de location par jour.
     */
    public float getPrixLocationParJour() {
        return prixLocationParJour;
    }

    /**
     * Retourne le prix de l’assurance par jour.
     *
     * @return Le prix de l’assurance par jour.
     */
    public float getPrixAssuranceParJour() {
        return prixAssuranceParJour;
    }

    /**
     * Retourne la description textuelle du type de véhicule.
     *
     * @return La description du type du véhicule.
     */
    public String getDescriptionTypeVehicule() {
        String discreptionTypeVehicule = "";
        switch (typeVehicule) {
            case HYBRIDE:
                discreptionTypeVehicule = DISCRIPTION_HYBRIDE;
                break;
            case ELECTRIQUE:
                discreptionTypeVehicule = DISCRIPTION_ELECTRIQUE;
                break;
        }
        return discreptionTypeVehicule;
    }

    /**
     * Retourne la description textuelle de la grandeur du véhicule.
     *
     * @return La description de la grandeur du véhicule.
     */
    public String getDescriptionGrandeurVehicule() {
        String discreptionGrandeurVehicule = "";
        switch (grandeurVehicule) {
            case PETIT:
                discreptionGrandeurVehicule = DISCRIPTION_PETIT;
                break;
            case INTERMEDIAIRE:
                discreptionGrandeurVehicule = DISCRIPTION_INTERMIDIAIRE;
                break;
            case GRAND:
                discreptionGrandeurVehicule = DISCRIPTION_GRANDE;
                break;
        }
        return discreptionGrandeurVehicule;
    }
}
