import javax.swing.JPanel;
import javax.swing.JLabel;
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
import java.sql.SQLException;
import java.util.List;

/**
 * Onglet admin "Types &amp; Grandeurs" : permet d'ajouter de nouveaux types de
 * véhicules (ex: Essence) et de nouvelles grandeurs (ex: XL) au catalogue.
 *
 * @author Hachemi Souici
 */
public class PanelCatalogue extends JPanel {

    private final DefaultTableModel modeleTypes = new DefaultTableModel(new Object[]{"Code", "Description"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeleGrandeurs = new DefaultTableModel(new Object[]{"Code", "Description", "Ordre"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public PanelCatalogue() {
        super(new GridLayout(2, 1, 10, 10));

        add(construirePanelTypes());
        add(construirePanelGrandeurs());

        rafraichir();
    }

    private JPanel construirePanelTypes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Types de véhicules"));

        JTable table = new JTable(modeleTypes);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JTextField champCode = new JTextField(3);
        JTextField champDescription = new JTextField(15);
        JButton boutonAjouter = new JButton("Ajouter un type");
        boutonAjouter.addActionListener(e -> {
            String code = champCode.getText().trim();
            String description = champDescription.getText().trim();
            if (code.length() != 1 || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le code doit être 1 caractère et la description non vide.",
                        "Saisie invalide", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                CatalogueVehicules.ajouterType(code.toUpperCase().charAt(0), description);
                champCode.setText("");
                champDescription.setText("");
                rafraichir();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelFormulaire = new JPanel();
        panelFormulaire.add(new JLabel("Code (1 caractère) :"));
        panelFormulaire.add(champCode);
        panelFormulaire.add(new JLabel("Description :"));
        panelFormulaire.add(champDescription);
        panelFormulaire.add(boutonAjouter);
        panel.add(panelFormulaire, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel construirePanelGrandeurs() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Grandeurs de véhicules"));

        JTable table = new JTable(modeleGrandeurs);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JTextField champCode = new JTextField(3);
        JTextField champDescription = new JTextField(15);
        JSpinner spinnerOrdre = new JSpinner(new SpinnerNumberModel(4, 1, 99, 1));
        JButton boutonAjouter = new JButton("Ajouter une grandeur");
        boutonAjouter.addActionListener(e -> {
            String code = champCode.getText().trim();
            String description = champDescription.getText().trim();
            if (code.length() != 1 || description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Le code doit être 1 caractère et la description non vide.",
                        "Saisie invalide", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                CatalogueVehicules.ajouterGrandeur(code.toUpperCase().charAt(0), description, (int) spinnerOrdre.getValue());
                champCode.setText("");
                champDescription.setText("");
                rafraichir();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelFormulaire = new JPanel();
        panelFormulaire.add(new JLabel("Code (1 caractère) :"));
        panelFormulaire.add(champCode);
        panelFormulaire.add(new JLabel("Description :"));
        panelFormulaire.add(champDescription);
        panelFormulaire.add(new JLabel("Ordre d'affichage :"));
        panelFormulaire.add(spinnerOrdre);
        panelFormulaire.add(boutonAjouter);
        panel.add(panelFormulaire, BorderLayout.SOUTH);

        return panel;
    }

    public void rafraichir() {
        CatalogueVehicules.invalider();

        modeleTypes.setRowCount(0);
        List<Character> types = CatalogueVehicules.obtenirTypesActifs();
        for (char type : types) {
            modeleTypes.addRow(new Object[]{type, CatalogueVehicules.obtenirDescriptionType(type)});
        }

        modeleGrandeurs.setRowCount(0);
        List<Character> grandeurs = CatalogueVehicules.obtenirGrandeursActives();
        for (char grandeur : grandeurs) {
            modeleGrandeurs.addRow(new Object[]{grandeur, CatalogueVehicules.obtenirDescriptionGrandeur(grandeur), ""});
        }
    }
}
