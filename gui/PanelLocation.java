import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;

/**
 * Onglet "Facturer une location" : équivalent graphique de l'option 1 du
 * menu console. Permet d'ajouter un ou plusieurs véhicules à une location en
 * cours, de saisir les informations du locataire et du paiement, puis de
 * générer la facture.
 *
 * Les validations sont réimplémentées (voir Validation.java) sans réutiliser
 * les méthodes "entrerValiderXxx" de ApplicationPrincipale, qui bloquent sur
 * un Scanner lié à System.in. Le calcul et la persistance passent en
 * revanche par exactement les mêmes classes métier que la console
 * (Vehicule, VehiculeLoue, LocationVehicule, Facture, GestionVehiculesDisponibles,
 * StatistiquesVehiculesLoues, ListeDesFactures).
 *
 * @author Hachemi Souici
 */
public class PanelLocation extends JPanel {

    private final Runnable surFactureGeneree;

    private LocationVehicule locationEnCours;
    private LocalDateTime dateFacture;

    private JComboBox<String> comboType;
    private JComboBox<String> comboGrandeur;
    private JSpinner spinnerNombreVehicules;
    private JSpinner spinnerNombreJours;
    private JCheckBox caseAssurance;
    private DefaultTableModel modeleVehiculesAjoutes;

    private JTextField champPrenom;
    private JTextField champNom;
    private JTextField champTelephone;
    private JTextField champPermis;

    private JRadioButton radioDebit;
    private JRadioButton radioCredit;
    private JComboBox<String> comboTypeCarte;
    private JTextField champNumeroCarte;

    public PanelLocation(Runnable surFactureGeneree) {
        super(new BorderLayout());
        this.surFactureGeneree = surFactureGeneree;

        add(construirePanelAjoutVehicule(), BorderLayout.NORTH);
        add(construireTableauRecapitulatif(), BorderLayout.CENTER);
        add(construirePanelLocataireEtPaiement(), BorderLayout.SOUTH);

        demarrerNouvelleLocation();
    }

    private JPanel construirePanelAjoutVehicule() {
        JPanel panel = new JPanel(new GridLayout(2, 6, 5, 5));
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Ajouter un véhicule à la location"));

        comboType = new JComboBox<>(new String[]{"Hybride", "Électrique"});
        comboGrandeur = new JComboBox<>(new String[]{"Petit", "Intermédiaire", "Grand"});
        spinnerNombreVehicules = new JSpinner(new SpinnerNumberModel(
                1, Validation.VEHICULES_MIN, Validation.VEHICULES_MAX, 1));
        spinnerNombreJours = new JSpinner(new SpinnerNumberModel(
                1, Validation.JOURS_MIN, Validation.JOURS_MAX, 1));
        caseAssurance = new JCheckBox("Prendre l'assurance");
        JButton boutonAjouter = new JButton("Ajouter ce véhicule");
        boutonAjouter.addActionListener(this::onAjouterVehicule);

        panel.add(new JLabel("Type de véhicule :"));
        panel.add(new JLabel("Grandeur :"));
        panel.add(new JLabel("Nombre de véhicules :"));
        panel.add(new JLabel("Nombre de jours :"));
        panel.add(caseAssurance);
        panel.add(boutonAjouter);

        panel.add(comboType);
        panel.add(comboGrandeur);
        panel.add(spinnerNombreVehicules);
        panel.add(spinnerNombreJours);
        panel.add(new JLabel());
        panel.add(new JLabel());

        return panel;
    }

    private JScrollPane construireTableauRecapitulatif() {
        modeleVehiculesAjoutes = new DefaultTableModel(
                new Object[]{"Type", "Grandeur", "Nb véhicules", "Nb jours", "Assurance"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(modeleVehiculesAjoutes);
        return new JScrollPane(table);
    }

    private JPanel construirePanelLocataireEtPaiement() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelLocataire = new JPanel(new GridLayout(2, 4, 5, 5));
        panelLocataire.setBorder(javax.swing.BorderFactory.createTitledBorder("Locataire"));
        champPrenom = new JTextField();
        champNom = new JTextField();
        champTelephone = new JTextField();
        champPermis = new JTextField();
        panelLocataire.add(new JLabel("Prénom :"));
        panelLocataire.add(new JLabel("Nom :"));
        panelLocataire.add(new JLabel("Téléphone (ex: (514) 784-6589) :"));
        panelLocataire.add(new JLabel("Permis (ex: D1234-567891-23) :"));
        panelLocataire.add(champPrenom);
        panelLocataire.add(champNom);
        panelLocataire.add(champTelephone);
        panelLocataire.add(champPermis);

        JPanel panelPaiement = new JPanel(new GridLayout(2, 4, 5, 5));
        panelPaiement.setBorder(javax.swing.BorderFactory.createTitledBorder("Paiement"));
        radioDebit = new JRadioButton("Débit", true);
        radioCredit = new JRadioButton("Crédit");
        ButtonGroup groupe = new ButtonGroup();
        groupe.add(radioDebit);
        groupe.add(radioCredit);
        comboTypeCarte = new JComboBox<>(new String[]{"Visa", "Mastercard"});
        champNumeroCarte = new JTextField();
        comboTypeCarte.setEnabled(false);
        champNumeroCarte.setEnabled(false);
        radioCredit.addActionListener(e -> basculerChampsCarteCredit(true));
        radioDebit.addActionListener(e -> basculerChampsCarteCredit(false));

        panelPaiement.add(radioDebit);
        panelPaiement.add(radioCredit);
        panelPaiement.add(new JLabel("Type de carte :"));
        panelPaiement.add(new JLabel("Numéro de carte (ex: 1234 5678 9123 4567) :"));
        panelPaiement.add(new JLabel());
        panelPaiement.add(new JLabel());
        panelPaiement.add(comboTypeCarte);
        panelPaiement.add(champNumeroCarte);

        JButton boutonGenererFacture = new JButton("Générer la facture");
        boutonGenererFacture.addActionListener(this::onGenererFacture);

        JPanel panelBas = new JPanel(new GridLayout(3, 1));
        panelBas.add(panelLocataire);
        panelBas.add(panelPaiement);
        panelBas.add(boutonGenererFacture);

        panelPrincipal.add(panelBas, BorderLayout.CENTER);
        return panelPrincipal;
    }

    private void basculerChampsCarteCredit(boolean actif) {
        comboTypeCarte.setEnabled(actif);
        champNumeroCarte.setEnabled(actif);
    }

    private void onAjouterVehicule(ActionEvent evenement) {
        char type = comboType.getSelectedIndex() == 0 ? Vehicule.HYBRIDE : Vehicule.ELECTRIQUE;
        char grandeur = switch (comboGrandeur.getSelectedIndex()) {
            case 0 -> Vehicule.PETIT;
            case 1 -> Vehicule.INTERMEDIAIRE;
            default -> Vehicule.GRAND;
        };
        int nombreVehicules = (int) spinnerNombreVehicules.getValue();
        int nombreJours = (int) spinnerNombreJours.getValue();
        boolean assuranceDesiree = caseAssurance.isSelected();

        if (locationEnCours.obtenirPositiondeVehiculeLoue(type, grandeur) != -1) {
            afficherErreur("Vous avez déjà loué un ou des véhicules de ce type et de cette grandeur.");
            return;
        }

        int nombreDisponibles = GestionVehiculesDisponibles.obtenirNombreVehiculesDisponibles(type, grandeur);
        if (nombreVehicules == 0) {
            afficherErreur("Le nombre de véhicules à louer doit être supérieur à 0 pour l'ajouter.");
            return;
        }
        if (!GestionVehiculesDisponibles.estDisponible(type, grandeur, nombreVehicules)) {
            afficherErreur(String.format("Seulement %d véhicule(s) disponible(s) pour ce type et cette grandeur.",
                    nombreDisponibles));
            return;
        }

        GestionVehiculesDisponibles.diminuerNombreVehiculesDisponibles(type, grandeur, nombreVehicules);

        float prixLocationParJour = GestionVehiculesDisponibles.obtenirPrixLocationVehParJour(type, grandeur);
        float prixAssuranceParJour = GestionVehiculesDisponibles.obtenirPrixAssuranceVehParJour(
                type, grandeur, !assuranceDesiree);

        Vehicule vehicule = new Vehicule(type, grandeur, prixLocationParJour, prixAssuranceParJour);
        VehiculeLoue vehiculeLoue = new VehiculeLoue(vehicule, nombreVehicules, nombreJours, dateFacture.plusHours(3));

        locationEnCours.ajouterVehiculeLoue(vehiculeLoue);
        StatistiquesVehiculesLoues.augmenterNombreVehiculesLoues(vehiculeLoue);

        modeleVehiculesAjoutes.addRow(new Object[]{
            vehicule.getDescriptionTypeVehicule(),
            vehicule.getDescriptionGrandeurVehicule(),
            nombreVehicules,
            nombreJours,
            assuranceDesiree ? "Oui" : "Non"
        });
    }

    private void onGenererFacture(ActionEvent evenement) {
        if (locationEnCours.nombreTypesVehiculesLoues() == 0) {
            afficherErreur("Ajoutez au moins un véhicule avant de générer la facture.");
            return;
        }

        String prenom = champPrenom.getText().trim();
        String nom = champNom.getText().trim();
        String telephone = champTelephone.getText().trim();
        String permis = champPermis.getText().trim();

        if (!Validation.nomValide(prenom)) {
            afficherErreur("Le prénom doit contenir entre 2 et 30 caractères.");
            return;
        }
        if (!Validation.nomValide(nom)) {
            afficherErreur("Le nom doit contenir entre 2 et 30 caractères.");
            return;
        }
        if (!Validation.telephoneValide(telephone)) {
            afficherErreur("Le numéro de téléphone doit respecter le format (514) 784-6589.");
            return;
        }
        if (!Validation.permisValide(permis)) {
            afficherErreur("Le numéro de permis doit respecter le format D1234-567891-23.");
            return;
        }

        char modePaiement = radioCredit.isSelected() ? Facture.CREDIT : Facture.DEBIT;
        char typeCarteCredit = ' ';
        String numeroCarteCredit = null;
        if (modePaiement == Facture.CREDIT) {
            numeroCarteCredit = champNumeroCarte.getText().trim();
            if (!Validation.carteCreditValide(numeroCarteCredit)) {
                afficherErreur("Le numéro de carte doit respecter le format 1234 5678 9123 4567.");
                return;
            }
            typeCarteCredit = comboTypeCarte.getSelectedIndex() == 0 ? Facture.VISA : Facture.MASTERCARD;
        }

        Locataire locataire = new Locataire(nom, prenom, telephone, permis);
        locationEnCours.setLocataire(locataire);

        Facture facture = new Facture(dateFacture, locationEnCours, modePaiement);
        if (modePaiement == Facture.CREDIT) {
            facture.setTypeCarteCredit(typeCarteCredit);
            facture.setNumeroCarteCredit(numeroCarteCredit);
        }

        facture.calculerSousTotal();
        facture.calculerMontantTPS();
        facture.calculerMontantTVQ();
        facture.calculerMontantTotal();

        ListeDesFactures.ajouterFacture(facture);
        ListeDesFactures.sauvegarderFactures();

        afficherRecuFacture(facture);
        demarrerNouvelleLocation();

        if (surFactureGeneree != null) {
            surFactureGeneree.run();
        }
    }

    private void demarrerNouvelleLocation() {
        locationEnCours = new LocationVehicule();
        dateFacture = LocalDateTime.now();
        modeleVehiculesAjoutes.setRowCount(0);
        champPrenom.setText("");
        champNom.setText("");
        champTelephone.setText("");
        champPermis.setText("");
        champNumeroCarte.setText("");
        radioDebit.setSelected(true);
        basculerChampsCarteCredit(false);
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
    }

    private void afficherRecuFacture(Facture facture) {
        StringBuilder texte = new StringBuilder();
        texte.append(Facture.CADRE).append("\n");
        texte.append("    ").append(Facture.NOM_RVV).append("\n");
        texte.append("    Adresse :       ").append(Facture.ADRESSE).append("\n");
        texte.append("    Téléphone :     ").append(Facture.TELEPHONE).append("\n");
        texte.append("    Date et Heure  ").append(facture.getDateFacture().format(Facture.formatter)).append("\n");
        texte.append("    Facture No     ").append(facture.getNumeroFacture()).append("\n");
        texte.append(Facture.CADRE).append("\n\n");

        LocationVehicule location = facture.getLocationVehicule();
        texte.append("    Prénom et nom       ").append(location.getLocataire().getPrenom())
                .append(" ").append(location.getLocataire().getNom()).append("\n");
        texte.append("    Téléphone           ").append(location.getLocataire().getNumeroTelephone()).append("\n");
        texte.append("    Permis de conduire  ").append(location.getLocataire().getNumeroPermisConduire()).append("\n\n");

        texte.append("    Mode de paiement    ").append(facture.getDescriptionModePaiement()).append("\n");
        if (facture.getModePaiement() == Facture.CREDIT) {
            texte.append("    Type de carte de crédit : ").append(facture.getDescriptionTypeCarteCredit()).append("\n");
            texte.append("    Numéro de carte de crédit : ").append(facture.getNumeroCarteCredit()).append("\n");
        }

        for (VehiculeLoue vehiculeLoue : location.getTableauVehiculesLoues()) {
            if (vehiculeLoue != null) {
                texte.append("\n    Type du véhicule             ").append(vehiculeLoue.getVehicule().getDescriptionTypeVehicule()).append("\n");
                texte.append("    Grandeur du véhicule         ").append(vehiculeLoue.getVehicule().getDescriptionGrandeurVehicule()).append("\n");
                texte.append("    Nombre de véhicules loués    ").append(vehiculeLoue.getNombreVehiculesLoue()).append("\n");
                texte.append("    Nombre de jours de location  ").append(vehiculeLoue.getNombreJoursLocation()).append("\n");
                texte.append("    Date de location   ").append(vehiculeLoue.getDateLocation().format(Facture.formatter)).append("\n");
                texte.append("    Date de retour     ").append(vehiculeLoue.calculerDateRetour().format(Facture.formatter)).append("\n");
                texte.append(String.format("    Prix de la location par jour       %.2f$%n", vehiculeLoue.getVehicule().getPrixLocationParJour()));
                if (vehiculeLoue.calculerRabais() != 0) {
                    texte.append(String.format("    Prix de la rabais par jour         %.2f$%n", vehiculeLoue.calculerRabais()));
                }
                texte.append(String.format("    Prix de l'assurance par jour       %.2f$%n", vehiculeLoue.getVehicule().getPrixAssuranceParJour()));
                texte.append(String.format("    Montant de la location             %.2f$%n",
                        (vehiculeLoue.getVehicule().getPrixLocationParJour() - vehiculeLoue.calculerRabais())
                                * vehiculeLoue.getNombreJoursLocation() * vehiculeLoue.getNombreVehiculesLoue()));
                texte.append(String.format("    Montant de l'assurance             %.2f$%n",
                        vehiculeLoue.getVehicule().getPrixAssuranceParJour()
                                * vehiculeLoue.getNombreJoursLocation() * vehiculeLoue.getNombreVehiculesLoue()));
            }
        }

        texte.append(String.format("%n    Sous-total                         %.2f$%n", facture.getSousTotal()));
        texte.append(String.format("    Montant TPS                        %.2f$%n", facture.getMontantTPS()));
        texte.append(String.format("    Montant TVQ                        %.2f$%n", facture.getMontantTVQ()));
        texte.append(String.format("    Montant total                      %.2f$%n", facture.getMontantTotal()));
        texte.append("\n").append(Facture.CADRE).append("\n");
        texte.append("Merci pour votre confiance!\n");

        JTextArea zoneTexte = new JTextArea(texte.toString());
        zoneTexte.setEditable(false);
        zoneTexte.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));

        Frame fenetreParente = (Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        JDialog dialogue = new JDialog(fenetreParente, "Facture No " + facture.getNumeroFacture(), true);
        dialogue.add(new JScrollPane(zoneTexte));
        dialogue.setSize(500, 600);
        dialogue.setLocationRelativeTo(this);
        dialogue.setVisible(true);
    }
}
