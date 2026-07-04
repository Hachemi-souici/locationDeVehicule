import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Onglet "Statistiques" : affiche le nombre de véhicules loués par type et
 * par grandeur, équivalent graphique de l'option 2 du menu console
 * (StatistiquesVehiculesLoues.afficherNombreVehiculesLoues()), mais sans
 * passer par System.out. Les colonnes (types) et lignes (grandeurs) sont
 * générées dynamiquement à partir du catalogue.
 *
 * @author Hachemi Souici
 */
public class PanelStatistiques extends JPanel {

    private final DefaultTableModel modele;

    public PanelStatistiques() {
        super(new BorderLayout());

        modele = new DefaultTableModel() {
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
        List<Character> types = CatalogueVehicules.obtenirTypesActifs();
        List<Character> grandeurs = CatalogueVehicules.obtenirGrandeursActives();

        Object[] entetes = new Object[types.size() + 1];
        entetes[0] = "Grandeur";
        for (int i = 0; i < types.size(); i++) {
            entetes[i + 1] = CatalogueVehicules.obtenirDescriptionType(types.get(i));
        }
        modele.setColumnIdentifiers(entetes);
        modele.setRowCount(0);

        for (char grandeur : grandeurs) {
            Object[] ligne = new Object[types.size() + 1];
            ligne[0] = CatalogueVehicules.obtenirDescriptionGrandeur(grandeur);
            for (int i = 0; i < types.size(); i++) {
                ligne[i + 1] = StatistiquesVehiculesLoues.obtenirNombreVehiculesLoues(types.get(i), grandeur);
            }
            modele.addRow(ligne);
        }
    }
}
