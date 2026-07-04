import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Onglet admin "Tarifs &amp; Stock" : permet de modifier le prix de location, le
 * prix d'assurance et le stock d'une combinaison type/grandeur existante, ou
 * d'en ajouter une nouvelle (ex: une combinaison utilisant un type ou une
 * grandeur tout juste créés dans l'onglet Catalogue).
 *
 * @author Hachemi Souici
 */
public class PanelTarifsStock extends JPanel {

    private final DefaultTableModel modeleCombinaisons = new DefaultTableModel(
            new Object[]{"Type", "Grandeur", "Prix location/jour", "Prix assurance/jour", "Stock"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column >= 2;
        }
    };

    private JComboBox<String> comboType;
    private JComboBox<String> comboGrandeur;
    private List<Character> typesDisponibles;
    private List<Character> grandeursDisponibles;

    public PanelTarifsStock() {
        super(new BorderLayout(10, 10));

        JTable table = new JTable(modeleCombinaisons);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construirePanelAjoutModification(table), BorderLayout.SOUTH);

        rafraichir();
    }

    private JPanel construirePanelAjoutModification(JTable table) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel panelAjout = new JPanel();
        panelAjout.setBorder(javax.swing.BorderFactory.createTitledBorder("Ajouter une nouvelle combinaison"));
        comboType = new JComboBox<>();
        comboGrandeur = new JComboBox<>();
        JTextField champPrixLocation = new JTextField(6);
        JTextField champPrixAssurance = new JTextField(6);
        JSpinner spinnerStock = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        JButton boutonAjouter = new JButton("Ajouter cette combinaison");
        boutonAjouter.addActionListener(e -> ajouterCombinaison(champPrixLocation, champPrixAssurance, spinnerStock));

        panelAjout.add(new JLabel("Type :"));
        panelAjout.add(comboType);
        panelAjout.add(new JLabel("Grandeur :"));
        panelAjout.add(comboGrandeur);
        panelAjout.add(new JLabel("Prix location/jour :"));
        panelAjout.add(champPrixLocation);
        panelAjout.add(new JLabel("Prix assurance/jour :"));
        panelAjout.add(champPrixAssurance);
        panelAjout.add(new JLabel("Stock initial :"));
        panelAjout.add(spinnerStock);
        panelAjout.add(boutonAjouter);

        JButton boutonEnregistrerModifications = new JButton("Enregistrer les modifications du tableau");
        boutonEnregistrerModifications.addActionListener(e -> enregistrerModificationsTableau());

        panel.add(panelAjout);
        panel.add(boutonEnregistrerModifications);
        return panel;
    }

    private void ajouterCombinaison(JTextField champPrixLocation, JTextField champPrixAssurance, JSpinner spinnerStock) {
        try {
            char type = typesDisponibles.get(comboType.getSelectedIndex());
            char grandeur = grandeursDisponibles.get(comboGrandeur.getSelectedIndex());
            float prixLocation = Float.parseFloat(champPrixLocation.getText().trim());
            float prixAssurance = Float.parseFloat(champPrixAssurance.getText().trim());
            int stock = (int) spinnerStock.getValue();

            GestionVehiculesDisponibles.ajouterOuMettreAJourVehiculeDisponible(type, grandeur, prixLocation, prixAssurance, stock);
            rafraichir();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Les prix doivent être des nombres valides.", "Saisie invalide", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enregistrerModificationsTableau() {
        try {
            for (int ligne = 0; ligne < modeleCombinaisons.getRowCount(); ligne++) {
                char type = ((String) modeleCombinaisons.getValueAt(ligne, 0)).charAt(0);
                char grandeur = ((String) modeleCombinaisons.getValueAt(ligne, 1)).charAt(0);
                float prixLocation = Float.parseFloat(modeleCombinaisons.getValueAt(ligne, 2).toString());
                float prixAssurance = Float.parseFloat(modeleCombinaisons.getValueAt(ligne, 3).toString());
                int stock = Integer.parseInt(modeleCombinaisons.getValueAt(ligne, 4).toString());
                GestionVehiculesDisponibles.ajouterOuMettreAJourVehiculeDisponible(type, grandeur, prixLocation, prixAssurance, stock);
            }
            JOptionPane.showMessageDialog(this, "Modifications enregistrées avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            rafraichir();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Les prix et le stock doivent être des nombres valides.", "Saisie invalide", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void rafraichir() {
        typesDisponibles = CatalogueVehicules.obtenirTypesActifs();
        grandeursDisponibles = CatalogueVehicules.obtenirGrandeursActives();

        comboType.removeAllItems();
        for (char type : typesDisponibles) {
            comboType.addItem(CatalogueVehicules.obtenirDescriptionType(type));
        }
        comboGrandeur.removeAllItems();
        for (char grandeur : grandeursDisponibles) {
            comboGrandeur.addItem(CatalogueVehicules.obtenirDescriptionGrandeur(grandeur));
        }

        modeleCombinaisons.setRowCount(0);
        String requete = "SELECT type_vehicule, grandeur_vehicule, prix_location_par_jour, "
                + "prix_assurance_par_jour, nombre_vehicules_disponibles FROM vehicules_disponibles "
                + "ORDER BY type_vehicule, grandeur_vehicule";
        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();
            try (Statement instruction = connexion.createStatement();
                    ResultSet resultat = instruction.executeQuery(requete)) {
                while (resultat.next()) {
                    modeleCombinaisons.addRow(new Object[]{
                        resultat.getString("type_vehicule"),
                        resultat.getString("grandeur_vehicule"),
                        resultat.getDouble("prix_location_par_jour"),
                        resultat.getDouble("prix_assurance_par_jour"),
                        resultat.getInt("nombre_vehicules_disponibles")
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'inventaire : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
