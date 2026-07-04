
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Université du Québec à Montréal (UQAM) INF1120 - 010 - Hiver 2025 Travail
 * pratique 3
 *
 * ListeDesFactures : Cette classe gère toutes les factures des différentes
 * locations de véhicules.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 *
 */
public class ListeDesFactures {

    private static final int NB_MAX_FACTURES = 50;

    // Déclaration des variables de classee
    private static int nombreFactures = 0;
    private static Facture[] lesFactures = new Facture[NB_MAX_FACTURES];

    /**
     * Ajouter la facture à la prochaine position libre du tableau des factures
     * (lesFactures). Cette position libre doit être inférieure à la taille du
     * tableau des factures. Le nombre courant de factures doit être
     * incrémenter.
     *
     * @param facture la facture à ajouter
     * @return vrai si la facture a été ajouté, sinon faux
     */
    public static boolean ajouterFacture(Facture facture) {
        // Vérifier si le nombre courant de factures est inférieur à la capacité maximale
        if (nombreFactures < lesFactures.length) {
            // Ajouter la facture à la prochaine position libre dans le tableau
            lesFactures[nombreFactures] = facture;

            // Incrémenter le compteur de factures
            nombreFactures++;

            // Retourner vrai pour indiquer que la facture a été ajoutée avec succès
            return true;
        }

        // Retourner faux si le tableau est plein
        return false;

    }

    /**
     * Enregistre toutes les factures dans les tables "factures" et
     * "vehicules_loues" de la base de données (qui remplacent l'ancien fichier
     * Factures.csv).
     *
     * Chaque facture correspond à une ligne de la table "factures", et chaque
     * véhicule loué de cette facture correspond à une ligne de la table
     * "vehicules_loues" reliée à celle-ci par le numéro de facture.
     *
     * Les nouvelles données des factures remplacent les anciennes données : les
     * tables sont vidées avant d'y réinsérer toutes les factures actuellement
     * en mémoire, le tout dans une seule transaction.
     */
    public static void sauvegarderFactures() {
        String insererFacture = "INSERT INTO factures (numero_facture, date_facture, prenom, nom, "
                + "telephone, permis_conduire, mode_paiement, type_carte_credit, numero_carte_credit, "
                + "sous_total, montant_tps, montant_tvq, montant_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String insererVehiculeLoue = "INSERT INTO vehicules_loues (numero_facture, type_vehicule, "
                + "grandeur_vehicule, nombre_vehicules_loues, nombre_jours_location, date_location, date_retour, "
                + "prix_location_par_jour, rabais, prix_assurance_par_jour, montant_location, montant_assurance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();
            connexion.setAutoCommit(false);

            try (Statement instructionVidage = connexion.createStatement(); PreparedStatement instructionFacture = connexion.prepareStatement(insererFacture); PreparedStatement instructionVehiculeLoue = connexion.prepareStatement(insererVehiculeLoue)) {

                // Les anciennes données doivent être remplacées par les nouvelles
                instructionVidage.execute("DELETE FROM vehicules_loues");
                instructionVidage.execute("DELETE FROM factures");

                for (int i = 0; i < nombreFactures; i++) {
                    Facture facture = lesFactures[i];
                    if (facture == null) {
                        continue;
                    }

                    LocationVehicule location = facture.getLocationVehicule();
                    VehiculeLoue[] vehiculesLoues = location.getTableauVehiculesLoues();

                    instructionFacture.setInt(1, facture.getNumeroFacture());
                    instructionFacture.setString(2, facture.getDateFacture().format(Facture.formatter));
                    instructionFacture.setString(3, location.getLocataire().getPrenom());
                    instructionFacture.setString(4, location.getLocataire().getNom());
                    instructionFacture.setString(5, location.getLocataire().getNumeroTelephone());
                    instructionFacture.setString(6, location.getLocataire().getNumeroPermisConduire());
                    instructionFacture.setString(7, facture.getDescriptionModePaiement());
                    if (facture.getModePaiement() == Facture.CREDIT) {
                        instructionFacture.setString(8, facture.getDescriptionTypeCarteCredit());
                        instructionFacture.setString(9, facture.getNumeroCarteCredit());
                    } else {
                        instructionFacture.setNull(8, java.sql.Types.VARCHAR);
                        instructionFacture.setNull(9, java.sql.Types.VARCHAR);
                    }
                    instructionFacture.setDouble(10, facture.getSousTotal());
                    instructionFacture.setDouble(11, facture.getMontantTPS());
                    instructionFacture.setDouble(12, facture.getMontantTVQ());
                    instructionFacture.setDouble(13, facture.getMontantTotal());
                    instructionFacture.executeUpdate();

                    for (VehiculeLoue vehiculeLoue : vehiculesLoues) {
                        if (vehiculeLoue == null) {
                            continue;
                        }

                        float montantLocation = vehiculeLoue.getNombreVehiculesLoue() * vehiculeLoue.getNombreJoursLocation()
                                * (vehiculeLoue.getVehicule().getPrixLocationParJour() - vehiculeLoue.calculerRabais());
                        float montantAssurance = vehiculeLoue.getNombreVehiculesLoue() * vehiculeLoue.getNombreJoursLocation()
                                * vehiculeLoue.getVehicule().getPrixAssuranceParJour();

                        instructionVehiculeLoue.setInt(1, facture.getNumeroFacture());
                        instructionVehiculeLoue.setString(2, vehiculeLoue.getVehicule().getDescriptionTypeVehicule());
                        instructionVehiculeLoue.setString(3, vehiculeLoue.getVehicule().getDescriptionGrandeurVehicule());
                        instructionVehiculeLoue.setInt(4, vehiculeLoue.getNombreVehiculesLoue());
                        instructionVehiculeLoue.setInt(5, vehiculeLoue.getNombreJoursLocation());
                        instructionVehiculeLoue.setString(6, vehiculeLoue.getDateLocation().format(Facture.formatter));
                        instructionVehiculeLoue.setString(7, vehiculeLoue.calculerDateRetour().format(Facture.formatter));
                        instructionVehiculeLoue.setDouble(8, vehiculeLoue.getVehicule().getPrixLocationParJour());
                        instructionVehiculeLoue.setDouble(9, vehiculeLoue.calculerRabais());
                        instructionVehiculeLoue.setDouble(10, vehiculeLoue.getVehicule().getPrixAssuranceParJour());
                        instructionVehiculeLoue.setDouble(11, montantLocation);
                        instructionVehiculeLoue.setDouble(12, montantAssurance);
                        instructionVehiculeLoue.executeUpdate();
                    }
                }

                connexion.commit();
                System.out.println("Les factures ont été enregistrées avec succès dans la base de données !");
            } catch (SQLException e) {
                connexion.rollback();
                System.err.println("Erreur lors de l'enregistrement des factures, annulation des changements : " + e.getMessage());
            } finally {
                connexion.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Erreur d'accès à la base de données : " + e.getMessage());
        }
    }

    /**
     * La méthode doit afficher toutes les factures qui sont dans le tableau des
     * factures. Pour plus de détails sur l'affichage, voir les exemples de la
     * trace d'exécution du programme fournis avec l'énoncé du Travail pratique
     * 3.
     */
    public static void afficher() {

        // Vérifier s'il y a des factures dans le tableau
        if (nombreFactures == 0) {
            System.out.println("Aucune facture disponible.");
            return;
        }

        // Parcourir le tableau des factures
        for (int i = 0; i < nombreFactures; i++) {
            Facture facture = lesFactures[i];
            if (facture != null) {
                // En-tête
                System.out.printf("%n%s%n", Facture.CADRE);
                System.out.printf("    %s%n", Facture.NOM_RVV);
                System.out.printf("    Adresse :       %s%n", Facture.ADRESSE);
                System.out.printf("    Téléphone :     %s%n", Facture.TELEPHONE);
                System.out.printf("    Date et Heure  %s%n", facture.getDateFacture().format(Facture.formatter));
                System.out.printf("    Facture No     %d%n", facture.getNumeroFacture());
                System.out.printf("%n%s%n", Facture.CADRE);
                // Informations du locataire
                System.out.printf("%n    Prénom et nom       %s %s%n", facture.getLocationVehicule().getLocataire().getPrenom(), facture.getLocationVehicule().getLocataire().getNom());
                System.out.printf("    Téléphone           %s%n", facture.getLocationVehicule().getLocataire().getNumeroTelephone());
                System.out.printf("    Permis de conduire  %s%n", facture.getLocationVehicule().getLocataire().getNumeroPermisConduire());
                // Informations du mode de paiement
                System.out.printf("%n    Mode de paiement    %s%n", facture.getDescriptionModePaiement());
                if (facture.getModePaiement() == Facture.CREDIT) {
                    System.out.printf("    Type de carte de crédit : %s%n", facture.getDescriptionTypeCarteCredit());
                    System.out.printf("    Numéro de carte de crédit : %s%n", facture.getNumeroCarteCredit());
                }
                // Affichage des véhicules loués
                for (VehiculeLoue vehiculeLoue : facture.getLocationVehicule().getTableauVehiculesLoues()) {
                    if (vehiculeLoue != null) {
                        System.out.printf("%n    Type du véhicule             %s%n", vehiculeLoue.getVehicule().getDescriptionTypeVehicule());
                        System.out.printf("    Grandeur du véhicule         %s%n", vehiculeLoue.getVehicule().getDescriptionGrandeurVehicule());
                        System.out.printf("    Nombre de véhicules loués    %d%n", vehiculeLoue.getNombreVehiculesLoue());
                        System.out.printf("    Nombre de jours de location  %d%n", vehiculeLoue.getNombreJoursLocation());
                        System.out.printf("    Date de location   %s%n", vehiculeLoue.getDateLocation().format(Facture.formatter));
                        System.out.printf("    Date de retour     %s%n", vehiculeLoue.calculerDateRetour().format(Facture.formatter));
                        System.out.printf("    Prix de la location par jour       %.2f$%n", vehiculeLoue.getVehicule().getPrixLocationParJour());
                        if (vehiculeLoue.calculerRabais() != 0) {
                            System.out.printf("    Prix de la rabais par jour         %.2f$%n", vehiculeLoue.calculerRabais());
                        }
                        System.out.printf("    Prix de l'assurance par jour       %.2f$%n", vehiculeLoue.getVehicule().getPrixAssuranceParJour());
                        System.out.printf("    Montant de la location             %.2f$%n", vehiculeLoue.getNombreVehiculesLoue() * vehiculeLoue.getNombreJoursLocation()
                                * (vehiculeLoue.getVehicule().getPrixLocationParJour() - vehiculeLoue.calculerRabais()));
                        System.out.printf("    Montant de l'assurance             %.2f$%n", (vehiculeLoue.getNombreVehiculesLoue() * vehiculeLoue.getNombreJoursLocation()
                                * vehiculeLoue.getVehicule().getPrixAssuranceParJour()));
                    }
                }
                // Calculs de la facture
                System.out.printf("%n    Sous-total                         %.2f$%n", facture.getSousTotal());
                System.out.printf("    Montant TPS                        %.2f$%n", facture.getMontantTPS());
                System.out.printf("    Montant TVQ                        %.2f$%n", facture.getMontantTVQ());
                System.out.printf("    Montant total                      %.2f$%n", facture.getMontantTotal());

                // Bas de facture
                System.out.printf("%n%s%n", Facture.CADRE);
                System.out.println("Merci pour votre confiance!");
                if (i < (nombreFactures - 1)) {
                    ApplicationPrincipale.pauseAvantMenu();
                }
            }

        }
    }

}
