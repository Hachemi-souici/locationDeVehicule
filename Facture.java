
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Représente une facture pour la location de véhicules. Contient les détails du
 * paiement, du locataire et du montant total.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 */
public class Facture {

    // Constantes pour les types de paiement
    public static final char DEBIT = 'D';
    public static final char CREDIT = 'C';
    public static final char MASTERCARD = 'M';
    public static final char VISA = 'V';

    // Descriptions associées aux types de paiement
    public static final String DISCRIPTION_DEBIT = "Débit";
    public static final String DISCRIPTION_CREDIT = "crédit";
    public static final String DISCRIPTION_MASTERCARD = "Mastercard";
    public static final String DISCRIPTION_VISA = "Visa";

    // Taxes applicables
    public static final float TPS = 0.05f;
    public static final float TVQ = 0.09975f;

    // Informations de l’entreprise
    public static final String NOM_RVV = "Roulons les Véhicules Verts (RVV)";
    public static final String ADRESSE = "1500 rue Matata, Hakuna, Québec Y0Z 6Y7";
    public static final String TELEPHONE = "438 222-1111";
    public static final String CADRE = "-----------------------------------------------------------";

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Attributs privés
    private static int comptureFacture = 0;
    private int numeroFacture;
    private char modePaiement;
    private char typeCarteCredit;
    private String numeroCarteCredit;
    private LocalDateTime dateFacture;
    private LocationVehicule locationVehicule;
    private float sousTotal;
    private float montantTPS;
    private float montantTVQ;
    private float montantTotal;

    /**
     * Constructeur de la classe Facture.
     *
     * @param dateFacture Date de création de la facture.
     * @param locationVehicule Détails de la location du véhicule.
     * @param modePaiement Mode de paiement utilisé (Débit ou Crédit).
     */
    public Facture(LocalDateTime dateFacture, LocationVehicule locationVehicule, char modePaiement) {
        this.dateFacture = dateFacture;
        this.locationVehicule = locationVehicule;
        this.modePaiement = modePaiement;

        comptureFacture++;
        this.numeroFacture = comptureFacture;
    }

    /**
     * Définit le type de carte de crédit.
     *
     * @param typeCarteCredit Type de carte (Visa ou Mastercard).
     */
    public void setTypeCarteCredit(char typeCarteCredit) {
        this.typeCarteCredit = typeCarteCredit;
    }

    /**
     * Définit le numéro de carte de crédit.
     *
     * @param numeroCarteCredit Numéro de carte de crédit.
     */
    public void setNumeroCarteCredit(String numeroCarteCredit) {
        this.numeroCarteCredit = numeroCarteCredit;
    }

    /**
     * Retourne le numéro de la facture.
     *
     * @return Numéro unique de la facture.
     */
    public int getNumeroFacture() {
        return numeroFacture;
    }

    /**
     * Retourne la date de création de la facture.
     *
     * @return Date de la facture.
     */
    public LocalDateTime getDateFacture() {
        return dateFacture;
    }

    /**
     * Retourne les détails de la location du véhicule.
     *
     * @return Location du véhicule associée à la facture.
     */
    public LocationVehicule getLocationVehicule() {
        return locationVehicule;
    }

    /**
     * Retourne le mode de paiement utilisé.
     *
     * @return Mode de paiement (Débit ou Crédit).
     */
    public char getModePaiement() {
        return modePaiement;
    }

    /**
     * Retourne le type de carte de crédit.
     *
     * @return Type de carte (Visa ou Mastercard).
     */
    public char getTypeCarteCredit() {
        return typeCarteCredit;
    }

    /**
     * Retourne le numéro de carte de crédit.
     *
     * @return Numéro de carte de crédit.
     */
    public String getNumeroCarteCredit() {
        return numeroCarteCredit;
    }

    /**
     * Retourne le sous-total de la facture avant taxes.
     *
     * @return Montant avant application des taxes.
     */
    public float getSousTotal() {
        return sousTotal;
    }

    /**
     * Retourne le montant de la TPS.
     *
     * @return Montant de la taxe TPS.
     */
    public float getMontantTPS() {
        return montantTPS;
    }

    /**
     * Retourne le montant de la TVQ.
     *
     * @return Montant de la taxe TVQ.
     */
    public float getMontantTVQ() {
        return montantTVQ;
    }

    /**
     * Retourne le montant total après application des taxes.
     *
     * @return Montant final à payer.
     */
    public float getMontantTotal() {
        return montantTotal;
    }

    /**
     * Retourne la description du mode de paiement.
     *
     * @return La description du mode de paiement (Débit ou Crédit).
     */
    public String getDescriptionModePaiement() {
        String discreptionModePaiement = "";
        switch (modePaiement) {
            case DEBIT:
                discreptionModePaiement = DISCRIPTION_DEBIT;
                break;
            case CREDIT:
                discreptionModePaiement = DISCRIPTION_CREDIT;
                break;
        }
        return discreptionModePaiement;
    }

    /**
     * Retourne la description du type de carte de crédit.
     *
     * @return La description du type de carte de crédit (Mastercard ou Visa).
     */
    public String getDescriptionTypeCarteCredit() {
        String discreptionTypeCarteCredit = "";
        switch (typeCarteCredit) {
            case MASTERCARD:
                discreptionTypeCarteCredit = DISCRIPTION_MASTERCARD;
                break;
            case VISA:
                discreptionTypeCarteCredit = DISCRIPTION_VISA;
                break;
        }
        return discreptionTypeCarteCredit;
    }

    /**
     * Calcule le sous-total de la facture en fonction de la location du
     * véhicule.
     */
    public void calculerSousTotal() {
        sousTotal = 0;
        for (VehiculeLoue vehiculeLoue : locationVehicule.getTableauVehiculesLoues()) {
            if (vehiculeLoue != null) {
                float prixLocation = vehiculeLoue.getVehicule().getPrixLocationParJour() - vehiculeLoue.calculerRabais();
                float prixAssurance = vehiculeLoue.getVehicule().getPrixAssuranceParJour();
                sousTotal += (prixLocation * vehiculeLoue.getNombreJoursLocation() * vehiculeLoue.getNombreVehiculesLoue())
                        + (prixAssurance * vehiculeLoue.getNombreJoursLocation() * vehiculeLoue.getNombreVehiculesLoue());
            }
        }
    }

    /**
     * Calcule le montant de la TPS.
     */
    public void calculerMontantTPS() {
        montantTPS = sousTotal * TPS;
    }

    /**
     * Calcule le montant de la TVQ.
     */
    public void calculerMontantTVQ() {
        montantTVQ = sousTotal * TVQ;
    }

    /**
     * Calcule le montant total à payer.
     */
    public void calculerMontantTotal() {
        montantTotal = sousTotal + montantTPS + montantTVQ;
    }

    /**
     * Affiche les détails complets de la facture incluant : - Les informations
     * du locataire - Le mode de paiement utilisé - Les véhicules loués et leurs
     * coûts associés - Les calculs des taxes et du montant total
     */
    public void afficherFacture() {
        // En-tête
        System.out.printf("%n%s%n", CADRE);
        System.out.printf("    %s%n", NOM_RVV);
        System.out.printf("    Adresse :       %s%n", ADRESSE);
        System.out.printf("    Téléphone :     %s%n", TELEPHONE);
        System.out.printf("    Date et Heure  %s%n", dateFacture.format(formatter));
        System.out.printf("    Facture No     %d%n", numeroFacture);
        System.out.printf("%n%s%n", CADRE);

        // Informations du locataire
        System.out.printf("%n    Prénom et nom       %s %s%n", locationVehicule.getLocataire().getPrenom(), locationVehicule.getLocataire().getNom());
        System.out.printf("    Téléphone           %s%n", locationVehicule.getLocataire().getNumeroTelephone());
        System.out.printf("    Permis de conduire  %s%n", locationVehicule.getLocataire().getNumeroPermisConduire());

        // Informations du mode de paiement
        System.out.printf("%n    Mode de paiement    %s%n", getDescriptionModePaiement());
        if (modePaiement == CREDIT) {
            System.out.printf("    Type de carte de crédit : %s%n", getDescriptionTypeCarteCredit());
            System.out.printf("    Numéro de carte de crédit : %s%n", numeroCarteCredit);
        }

        // Affichage des véhicules loués
        for (VehiculeLoue vehiculeLoue : locationVehicule.getTableauVehiculesLoues()) {
            if (vehiculeLoue != null) {
                System.out.printf("%n    Type du véhicule             %s%n", vehiculeLoue.getVehicule().getDescriptionTypeVehicule());
                System.out.printf("    Grandeur du véhicule         %s%n", vehiculeLoue.getVehicule().getDescriptionGrandeurVehicule());
                System.out.printf("    Nombre de véhicules loués    %d%n", vehiculeLoue.getNombreVehiculesLoue());
                System.out.printf("    Nombre de jours de location  %d%n", vehiculeLoue.getNombreJoursLocation());
                System.out.printf("    Date de location   %s%n", vehiculeLoue.getDateLocation().format(formatter));
                System.out.printf("    Date de retour     %s%n", vehiculeLoue.calculerDateRetour().format(formatter));
                System.out.printf("    Prix de la location par jour       %.2f$%n", vehiculeLoue.getVehicule().getPrixLocationParJour());
                if (vehiculeLoue.calculerRabais() != 0) {
                    System.out.printf("    Prix de la rabais par jour         %.2f$%n", vehiculeLoue.calculerRabais());
                }
                System.out.printf("    Prix de l'assurance par jour       %.2f$%n", vehiculeLoue.getVehicule().getPrixAssuranceParJour());
                System.out.printf("    Montant de la location             %.2f$%n", ((vehiculeLoue.getVehicule().getPrixLocationParJour() - vehiculeLoue.calculerRabais())
                        * vehiculeLoue.getNombreJoursLocation()
                        * vehiculeLoue.getNombreVehiculesLoue()));
                System.out.printf("    Montant de l'assurance             %.2f$%n", (vehiculeLoue.getVehicule().getPrixAssuranceParJour()
                        * vehiculeLoue.getNombreJoursLocation()
                        * vehiculeLoue.getNombreVehiculesLoue()));
            }
        }

        // Calculs de la facture
        System.out.printf("%n    Sous-total                         %.2f$%n", sousTotal);
        System.out.printf("    Montant TPS                        %.2f$%n", montantTPS);
        System.out.printf("    Montant TVQ                        %.2f$%n", montantTVQ);
        System.out.printf("    Montant total                      %.2f$%n", montantTotal);

        // Bas de facture
        System.out.println("\n------------------------------------------------------");
        System.out.println("Merci pour votre confiance!");

    }

}
