import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

/**
 * Onglet "Inventaire" : affiche le nombre de véhicules disponibles à la
 * location par type et par grandeur, équivalent graphique de l'option 3 du
 * menu console (GestionVehiculesDisponibles.afficher()), mais sans passer
 * par System.out.
 *
 * @author Hachemi Souici
 */
public class PanelInventaire extends JPanel {

    private static final String[] GRANDEURS_LABELS = {"Petit", "Intermédiaire", "Grand"};
    private static final char[] GRANDEURS_CODES = {Vehicule.PETIT, Vehicule.INTERMEDIAIRE, Vehicule.GRAND};

    private final DefaultTableModel modele;

    public PanelInventaire() {
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
            int nbHybrides = GestionVehiculesDisponibles.obtenirNombreVehiculesDisponibles(Vehicule.HYBRIDE, GRANDEURS_CODES[i]);
            int nbElectriques = GestionVehiculesDisponibles.obtenirNombreVehiculesDisponibles(Vehicule.ELECTRIQUE, GRANDEURS_CODES[i]);
            modele.addRow(new Object[]{GRANDEURS_LABELS[i], nbHybrides, nbElectriques});
        }
    }
}
