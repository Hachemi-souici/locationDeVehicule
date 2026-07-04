
/**
 * Représente un locataire avec ses informations personnelles.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 */
public class Locataire {

    // Attributs privés
    private String nom;
    private String prenom;
    private String numeroTelephone;
    private String numeroPermisConduire;

    /**
     * Constructeur de la classe Locataire.
     *
     * @param nom Le nom du locataire.
     * @param prenom Le prénom du locataire.
     * @param numeroTelephone Le numéro de téléphone du locataire.
     * @param numeroPermisConduire Le numéro de permis de conduire du locataire.
     */
    public Locataire(String nom, String prenom, String numeroTelephone, String numeroPermisConduire) {
        this.nom = nom;
        this.prenom = prenom;
        this.numeroTelephone = numeroTelephone;
        this.numeroPermisConduire = numeroPermisConduire;
    }

    /**
     * Retourne le nom du locataire.
     *
     * @return Le nom du locataire.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne le prénom du locataire.
     *
     * @return Le prénom du locataire.
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Retourne le numéro de téléphone du locataire.
     *
     * @return Le numéro de téléphone du locataire.
     */
    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    /**
     * Retourne le numéro de permis de conduire du locataire.
     *
     * @return Le numéro de permis de conduire du locataire.
     */
    public String getNumeroPermisConduire() {
        return numeroPermisConduire;
    }
}
