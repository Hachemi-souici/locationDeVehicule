import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Onglet "Inventaire" : affiche le nombre de véhicules disponibles à la
 * location par type et par grandeur, équivalent graphique de l'option 3 du
 * menu console (GestionVehiculesDisponibles.afficher()), mais sans passer
 * par System.out. Les colonnes (types) et lignes (grandeurs) sont générées
 * dynamiquement à partir du catalogue, qui peut être étendu par
 * l'interface d'administration.
 *
 * @author Hachemi Souici
 */
public class PanelInventaire extends JPanel {

    private final DefaultTableModel modele;

    public PanelInventaire() {
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

        JButton boutonRecharger = new JButton("Recharger le catalogue");
        boutonRecharger.setToolTipText("Recharge les types/grandeurs/tarifs depuis la base, utile après une modification faite dans l'interface d'administration.");
        boutonRecharger.addActionListener(e -> rechargerCatalogueEtRafraichir());
        add(boutonRecharger, BorderLayout.NORTH);

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
                ligne[i + 1] = GestionVehiculesDisponibles.obtenirNombreVehiculesDisponibles(types.get(i), grandeur);
            }
            modele.addRow(ligne);
        }
    }

    private void rechargerCatalogueEtRafraichir() {
        CatalogueVehicules.invalider();
        GestionReglesRabais.invalider();
        GestionVehiculesDisponibles.chargerVehiculesDisponibles();
        rafraichir();
    }
}
