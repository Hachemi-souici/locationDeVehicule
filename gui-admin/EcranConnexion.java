import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.sql.SQLException;

/**
 * Écran de connexion modal affiché avant la fenêtre principale de
 * l'administration. Limite le nombre de tentatives pour éviter une boucle
 * infinie en cas d'erreur répétée.
 *
 * @author Hachemi Souici
 */
public class EcranConnexion extends JDialog {

    private static final int TENTATIVES_MAX = 3;

    private final JTextField champIdentifiant = new JTextField(15);
    private final JPasswordField champMotDePasse = new JPasswordField(15);
    private boolean authentifie = false;
    private int tentativesRestantes = TENTATIVES_MAX;

    public EcranConnexion(Frame parent) {
        super(parent, "Connexion administrateur", true);

        JPanel panelFormulaire = new JPanel(new GridLayout(2, 2, 5, 5));
        panelFormulaire.add(new JLabel("Identifiant :"));
        panelFormulaire.add(champIdentifiant);
        panelFormulaire.add(new JLabel("Mot de passe :"));
        panelFormulaire.add(champMotDePasse);

        JButton boutonConnexion = new JButton("Se connecter");
        boutonConnexion.addActionListener(e -> tenterConnexion());

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(panelFormulaire, BorderLayout.CENTER);
        panelPrincipal.add(boutonConnexion, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void tenterConnexion() {
        String identifiant = champIdentifiant.getText().trim();
        String motDePasse = new String(champMotDePasse.getPassword());

        try {
            if (Authentification.verifierIdentifiants(identifiant, motDePasse)) {
                authentifie = true;
                dispose();
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur d'accès à la base de données : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tentativesRestantes--;
        if (tentativesRestantes <= 0) {
            JOptionPane.showMessageDialog(this, "Nombre maximal de tentatives atteint. Fermeture.",
                    "Accès refusé", JOptionPane.ERROR_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Identifiant ou mot de passe incorrect (" + tentativesRestantes + " tentative(s) restante(s)).",
                    "Échec de connexion", JOptionPane.ERROR_MESSAGE);
            champMotDePasse.setText("");
        }
    }

    public boolean estAuthentifie() {
        return authentifie;
    }
}
