import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Référentiel dynamique des types et grandeurs de véhicules, chargé depuis
 * les tables "types_vehicules" et "grandeurs_vehicules" de la base de
 * données. Remplace les anciennes constantes figées de Vehicule.java (qui ne
 * connaissaient que Hybride/Électrique et Petit/Intermédiaire/Grand) par un
 * catalogue extensible, géré par l'interface d'administration.
 *
 * @author Hachemi Souici
 */
public class CatalogueVehicules {

    private static final Map<Character, String> DESCRIPTIONS_TYPES = new LinkedHashMap<>();
    private static final Map<Character, String> DESCRIPTIONS_GRANDEURS = new LinkedHashMap<>();
    private static boolean charge = false;

    private CatalogueVehicules() {
    }

    public static synchronized void charger() {
        DESCRIPTIONS_TYPES.clear();
        DESCRIPTIONS_GRANDEURS.clear();

        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();

            String requeteTypes = "SELECT code, description FROM types_vehicules WHERE actif = 1";
            try (Statement instruction = connexion.createStatement();
                    ResultSet resultat = instruction.executeQuery(requeteTypes)) {
                while (resultat.next()) {
                    DESCRIPTIONS_TYPES.put(resultat.getString("code").charAt(0), resultat.getString("description"));
                }
            }

            String requeteGrandeurs = "SELECT code, description FROM grandeurs_vehicules WHERE actif = 1 ORDER BY ordre_affichage";
            try (Statement instruction = connexion.createStatement();
                    ResultSet resultat = instruction.executeQuery(requeteGrandeurs)) {
                while (resultat.next()) {
                    DESCRIPTIONS_GRANDEURS.put(resultat.getString("code").charAt(0), resultat.getString("description"));
                }
            }

            charge = true;
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du catalogue de véhicules : " + e.getMessage());
            if (DESCRIPTIONS_TYPES.isEmpty()) {
                DESCRIPTIONS_TYPES.put(Vehicule.HYBRIDE, Vehicule.DISCRIPTION_HYBRIDE);
                DESCRIPTIONS_TYPES.put(Vehicule.ELECTRIQUE, Vehicule.DISCRIPTION_ELECTRIQUE);
            }
            if (DESCRIPTIONS_GRANDEURS.isEmpty()) {
                DESCRIPTIONS_GRANDEURS.put(Vehicule.PETIT, Vehicule.DISCRIPTION_PETIT);
                DESCRIPTIONS_GRANDEURS.put(Vehicule.INTERMEDIAIRE, Vehicule.DISCRIPTION_INTERMIDIAIRE);
                DESCRIPTIONS_GRANDEURS.put(Vehicule.GRAND, Vehicule.DISCRIPTION_GRANDE);
            }
        }
    }

    public static String obtenirDescriptionType(char code) {
        if (!charge) {
            charger();
        }
        return DESCRIPTIONS_TYPES.getOrDefault(code, "");
    }

    public static String obtenirDescriptionGrandeur(char code) {
        if (!charge) {
            charger();
        }
        return DESCRIPTIONS_GRANDEURS.getOrDefault(code, "");
    }

    public static List<Character> obtenirTypesActifs() {
        if (!charge) {
            charger();
        }
        return new ArrayList<>(DESCRIPTIONS_TYPES.keySet());
    }

    public static List<Character> obtenirGrandeursActives() {
        if (!charge) {
            charger();
        }
        return new ArrayList<>(DESCRIPTIONS_GRANDEURS.keySet());
    }

    public static void ajouterType(char code, String description) throws SQLException {
        String requete = "INSERT INTO types_vehicules (code, description, actif) VALUES (?, ?, 1)";
        try (var instruction = BaseDeDonnees.obtenirConnexion().prepareStatement(requete)) {
            instruction.setString(1, String.valueOf(code));
            instruction.setString(2, description);
            instruction.executeUpdate();
        }
        invalider();
    }

    public static void ajouterGrandeur(char code, String description, int ordreAffichage) throws SQLException {
        String requete = "INSERT INTO grandeurs_vehicules (code, description, ordre_affichage, actif) VALUES (?, ?, ?, 1)";
        try (var instruction = BaseDeDonnees.obtenirConnexion().prepareStatement(requete)) {
            instruction.setString(1, String.valueOf(code));
            instruction.setString(2, description);
            instruction.setInt(3, ordreAffichage);
            instruction.executeUpdate();
        }
        invalider();
    }

    public static void invalider() {
        charge = false;
    }
}
