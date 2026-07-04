import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Gère les règles de rabais configurables (type de véhicule, grandeur, seuil
 * de jours, pourcentage), chargées depuis la table "regles_rabais". Remplace
 * la règle auparavant codée en dur dans VehiculeLoue.calculerRabais() (20%
 * après 15 jours, uniquement Électrique Petit/Intermédiaire), tout en
 * conservant ce comportement par défaut via l'amorçage initial de la table.
 *
 * @author Hachemi Souici
 */
public class GestionReglesRabais {

    private record RegleRabais(char type, char grandeur, int seuilJours, double pourcentage) {
    }

    private static final List<RegleRabais> REGLES = new ArrayList<>();
    private static boolean charge = false;

    private GestionReglesRabais() {
    }

    public static synchronized void charger() {
        REGLES.clear();
        String requete = "SELECT type_vehicule, grandeur_vehicule, seuil_jours, pourcentage_rabais "
                + "FROM regles_rabais WHERE actif = 1";
        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();
            try (Statement instruction = connexion.createStatement();
                    ResultSet resultat = instruction.executeQuery(requete)) {
                while (resultat.next()) {
                    REGLES.add(new RegleRabais(
                            resultat.getString("type_vehicule").charAt(0),
                            resultat.getString("grandeur_vehicule").charAt(0),
                            resultat.getInt("seuil_jours"),
                            resultat.getDouble("pourcentage_rabais")));
                }
            }
            charge = true;
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des règles de rabais : " + e.getMessage());
        }
    }

    public static float calculerRabais(char type, char grandeur, int nombreJoursLocation, float prixLocationParJour) {
        if (!charge) {
            charger();
        }
        for (RegleRabais regle : REGLES) {
            if (regle.type() == type && regle.grandeur() == grandeur && nombreJoursLocation > regle.seuilJours()) {
                return (float) (prixLocationParJour * regle.pourcentage());
            }
        }
        return 0f;
    }

    public static void ajouterOuModifier(char type, char grandeur, int seuilJours, double pourcentage) throws SQLException {
        String requete = "INSERT INTO regles_rabais (type_vehicule, grandeur_vehicule, seuil_jours, pourcentage_rabais, actif) "
                + "VALUES (?, ?, ?, ?, 1) "
                + "ON CONFLICT(type_vehicule, grandeur_vehicule) DO UPDATE SET "
                + "seuil_jours = excluded.seuil_jours, pourcentage_rabais = excluded.pourcentage_rabais, actif = 1";
        try (var instruction = BaseDeDonnees.obtenirConnexion().prepareStatement(requete)) {
            instruction.setString(1, String.valueOf(type));
            instruction.setString(2, String.valueOf(grandeur));
            instruction.setInt(3, seuilJours);
            instruction.setDouble(4, pourcentage);
            instruction.executeUpdate();
        }
        invalider();
    }

    public static List<String> obtenirReglesFormattees() {
        if (!charge) {
            charger();
        }
        List<String> lignes = new ArrayList<>();
        for (RegleRabais regle : REGLES) {
            lignes.add(String.format("%s / %s : %.0f%% après %d jours",
                    CatalogueVehicules.obtenirDescriptionType(regle.type()),
                    CatalogueVehicules.obtenirDescriptionGrandeur(regle.grandeur()),
                    regle.pourcentage() * 100, regle.seuilJours()));
        }
        return lignes;
    }

    public static void invalider() {
        charge = false;
    }
}
