import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Vérifie les identifiants d'un administrateur contre la table
 * "administrateurs" de la base de données. Les mots de passe ne sont jamais
 * stockés ni comparés en clair : seul leur hachage SHA-256 (voir Hachage.java,
 * classe racine partagée avec BaseDeDonnees) est manipulé.
 *
 * @author Hachemi Souici
 */
public class Authentification {

    private Authentification() {
    }

    public static boolean verifierIdentifiants(String identifiant, String motDePasse) throws SQLException {
        String requete = "SELECT mot_de_passe_hache FROM administrateurs WHERE identifiant = ?";
        Connection connexion = BaseDeDonnees.obtenirConnexion();
        try (PreparedStatement instruction = connexion.prepareStatement(requete)) {
            instruction.setString(1, identifiant);
            try (ResultSet resultat = instruction.executeQuery()) {
                if (!resultat.next()) {
                    return false;
                }
                String hacheStocke = resultat.getString("mot_de_passe_hache");
                return hacheStocke.equals(Hachage.sha256Hex(motDePasse));
            }
        }
    }

    public static boolean changerMotDePasse(String identifiant, String nouveauMotDePasse) throws SQLException {
        String requete = "UPDATE administrateurs SET mot_de_passe_hache = ? WHERE identifiant = ?";
        Connection connexion = BaseDeDonnees.obtenirConnexion();
        try (PreparedStatement instruction = connexion.prepareStatement(requete)) {
            instruction.setString(1, Hachage.sha256Hex(nouveauMotDePasse));
            instruction.setString(2, identifiant);
            return instruction.executeUpdate() > 0;
        }
    }
}
