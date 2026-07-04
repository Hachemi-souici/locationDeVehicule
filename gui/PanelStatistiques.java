import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * Onglet "Statistiques" : affiche le nombre de véhicules loués par type et
 * par grandeur, équivalent graphique de l'option 2 du menu console
 * (StatistiquesVehiculesLoues.afficherNombreVehiculesLoues()), mais sans
 * passer par System.out.
 *
 * @author Hachemi Souici
 */
public class PanelStatistiques extends JPanel {

    private static final String[] GRANDEURS_LABELS = {"Petit", "Intermédiaire", "Grand"};
    private static final char[] GRANDEURS_CODES = {Vehicule.PETIT, Vehicule.INTERMEDIAIRE, Vehicule.GRAND};

    private final DefaultTableModel modele;

    public PanelStatistiques() {
        super(new BorderLayout());

        modele = new DefaultTableModel(new Object[]{"Grandeur", "Hybride", "Électrique"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(modele);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton boutonRafraichir = new JButton("Rafraîchir");
        boutonRafraichir.addActionListener(e -> rafraichir());
        add(boutonRafraichir, BorderLayout.SOUTH);

        rafraichir();
    }

    public void rafraichir() {
        modele.setRowCount(0);
        for (int i = 0; i < GRANDEURS_LABELS.length; i++) {
            int nbHybrides = StatistiquesVehiculesLoues.obtenirNombreVehiculesLoues(Vehicule.HYBRIDE, GRANDEURS_CODES[i]);
            int nbElectriques = StatistiquesVehiculesLoues.obtenirNombreVehiculesLoues(Vehicule.ELECTRIQUE, GRANDEURS_CODES[i]);
            modele.addRow(new Object[]{GRANDEURS_LABELS[i], nbHybrides, nbElectriques});
        }
    }
}
