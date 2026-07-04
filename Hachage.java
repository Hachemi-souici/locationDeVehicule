import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/**
 * Utilitaire de hachage SHA-256, utilisé pour ne jamais stocker les mots de
 * passe administrateurs en clair dans la base de données.
 *
 * @author Hachemi Souici
 */
public class Hachage {

    private Hachage() {
    }

    public static String sha256Hex(String texte) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] octetsHaches = digest.digest(texte.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder();
            for (byte octet : octetsHaches) {
                hexadecimal.append(String.format("%02x", octet));
            }
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algorithme SHA-256 indisponible", e);
        }
    }
}
