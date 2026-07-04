import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

/**
 * Point d'entrée de l'interface graphique Swing du système de facturation de
 * location de véhicules (RVV). Réutilise intégralement les classes métier du
 * projet (Vehicule, Facture, GestionVehiculesDisponibles, BaseDeDonnees, etc.)
 * sans modifier aucun fichier existant. Le programme console
 * (ApplicationPrincipale) reste utilisable séparément.
 *
 * @author Hachemi Souici
 */
public class InterfaceGraphique {

    public static void main(String[] args) {
        try {
            BaseDeDonnees.initialiser();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Impossible d'initialiser la base de données : " + e.getMessage(),
                    "Erreur fatale", JOptionPane.ERROR_MESSAGE);
            return;
        }

        GestionVehiculesDisponibles.chargerVehiculesDisponibles();

        SwingUtilities.invokeLater(() -> new FenetrePrincipale().setVisible(true));
    }
}

/**
 * Fenêtre principale de l'application : un onglet par fonctionnalité du menu
 * console (Facturer une location, Statistiques, Inventaire, Factures).
 */
class FenetrePrincipale extends JFrame {

    private final PanelStatistiques panelStatistiques;
    private final PanelInventaire panelInventaire;
    private final PanelFactures panelFactures;

    FenetrePrincipale() {
        super("Roulons les Véhicules Verts (RVV) - Système de facturation");

        panelStatistiques = new PanelStatistiques();
        panelInventaire = new PanelInventaire();
        panelFactures = new PanelFactures();

        Runnable surFactureGeneree = () -> {
            panelStatistiques.rafraichir();
            panelInventaire.rafraichir();
            panelFactures.rafraichir();
        };
        PanelLocation panelLocation = new PanelLocation(surFactureGeneree);

        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Facturer une location", panelLocation);
        onglets.addTab("Statistiques véhicules loués", panelStatistiques);
        onglets.addTab("Inventaire des véhicules", panelInventaire);
        onglets.addTab("Toutes les factures", panelFactures);
        onglets.addChangeListener(e -> rafraichirOngletActif(onglets));

        setContentPane(onglets);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fermerProprement();
            }
        });
    }

    private void rafraichirOngletActif(JTabbedPane onglets) {
        Object selectionne = onglets.getSelectedComponent();
        if (selectionne == panelStatistiques) {
            panelStatistiques.rafraichir();
        } else if (selectionne == panelInventaire) {
            panelInventaire.rafraichir();
        } else if (selectionne == panelFactures) {
            panelFactures.rafraichir();
        }
    }

    private void fermerProprement() {
        ListeDesFactures.sauvegarderFactures();
        BaseDeDonnees.fermer();
        dispose();
        System.exit(0);
    }
}
