import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Onglet admin "Rabais" : permet de configurer les règles de rabais
 * (type, grandeur, seuil de jours, pourcentage), auparavant codées en dur
 * (20% après 15 jours pour Électrique Petit/Intermédiaire uniquement).
 *
 * @author Hachemi Souici
 */
public class PanelRabais extends JPanel {

    private final DefaultTableModel modeleRegles = new DefaultTableModel(
            new Object[]{"Type", "Grandeur", "Seuil (jours)", "Rabais (%)"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private JComboBox<String> comboType;
    private JComboBox<String> comboGrandeur;
    private List<Character> typesDisponibles;
    private List<Character> grandeursDisponibles;

    public PanelRabais() {
        super(new BorderLayout(10, 10));

        JTable table = new JTable(modeleRegles);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(construirePanelFormulaire(), BorderLayout.SOUTH);

        rafraichir();
    }

    private JPanel construirePanelFormulaire() {
        JPanel panel = new JPanel();
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Ajouter ou modifier une règle de rabais"));

        comboType = new JComboBox<>();
        comboGrandeur = new JComboBox<>();
        JSpinner spinnerSeuilJours = new JSpinner(new SpinnerNumberModel(15, 1, 365, 1));
        JSpinner spinnerPourcentage = new JSpinner(new SpinnerNumberModel(20, 0, 100, 1));
        JButton boutonEnregistrer = new JButton("Enregistrer la règle");
        boutonEnregistrer.addActionListener(e -> enregistrerRegle(spinnerSeuilJours, spinnerPourcentage));

        panel.add(new JLabel("Type :"));
        panel.add(comboType);
        panel.add(new JLabel("Grandeur :"));
        panel.add(comboGrandeur);
        panel.add(new JLabel("Seuil (jours) :"));
        panel.add(spinnerSeuilJours);
        panel.add(new JLabel("Rabais (%) :"));
        panel.add(spinnerPourcentage);
        panel.add(boutonEnregistrer);

        return panel;
    }

    private void enregistrerRegle(JSpinner spinnerSeuilJours, JSpinner spinnerPourcentage) {
        try {
            char type = typesDisponibles.get(comboType.getSelectedIndex());
            char grandeur = grandeursDisponibles.get(comboGrandeur.getSelectedIndex());
            int seuilJours = (int) spinnerSeuilJours.getValue();
            double pourcentage = ((int) spinnerPourcentage.getValue()) / 100.0;

            GestionReglesRabais.ajouterOuModifier(type, grandeur, seuilJours, pourcentage);
            rafraichir();
            JOptionPane.showMessageDialog(this, "Règle enregistrée avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
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

        modeleRegles.setRowCount(0);
        String requete = "SELECT type_vehicule, grandeur_vehicule, seuil_jours, pourcentage_rabais "
                + "FROM regles_rabais WHERE actif = 1 ORDER BY type_vehicule, grandeur_vehicule";
        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();
            try (Statement instruction = connexion.createStatement();
                    ResultSet resultat = instruction.executeQuery(requete)) {
                while (resultat.next()) {
                    modeleRegles.addRow(new Object[]{
                        resultat.getString("type_vehicule"),
                        resultat.getString("grandeur_vehicule"),
                        resultat.getInt("seuil_jours"),
                        String.format("%.0f%%", resultat.getDouble("pourcentage_rabais") * 100)
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des règles de rabais : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
