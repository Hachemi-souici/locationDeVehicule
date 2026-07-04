import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

/**
 * Point d'entrée de l'interface d'administration : application Java Swing
 * séparée de l'interface client (gui/InterfaceGraphique), protégée par un
 * écran de connexion. Permet de gérer le catalogue de véhicules (types,
 * grandeurs), les tarifs, le stock et les règles de rabais, sans jamais
 * modifier les fichiers du programme original.
 *
 * @author Hachemi Souici
 */
public class InterfaceAdmin {

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

        SwingUtilities.invokeLater(() -> {
            EcranConnexion ecranConnexion = new EcranConnexion(null);
            ecranConnexion.setVisible(true);

            if (ecranConnexion.estAuthentifie()) {
                new FenetreAdmin().setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}

/**
 * Fenêtre principale de l'interface d'administration : un onglet par
 * fonctionnalité de gestion du catalogue.
 */
class FenetreAdmin extends JFrame {

    FenetreAdmin() {
        super("RVV - Administration du catalogue de véhicules");

        PanelCatalogue panelCatalogue = new PanelCatalogue();
        PanelTarifsStock panelTarifsStock = new PanelTarifsStock();
        PanelRabais panelRabais = new PanelRabais();

        JTabbedPane onglets = new JTabbedPane();
        onglets.addTab("Types et grandeurs", panelCatalogue);
        onglets.addTab("Tarifs et stock", panelTarifsStock);
        onglets.addTab("Règles de rabais", panelRabais);
        onglets.addChangeListener(e -> {
            Object selectionne = onglets.getSelectedComponent();
            if (selectionne == panelTarifsStock) {
                panelTarifsStock.rafraichir();
            } else if (selectionne == panelRabais) {
                panelRabais.rafraichir();
            }
        });

        setContentPane(onglets);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                BaseDeDonnees.fermer();
                dispose();
                System.exit(0);
            }
        });
    }
}
