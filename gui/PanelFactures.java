import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Onglet "Factures" : affiche toutes les factures enregistrées, équivalent
 * graphique de l'option 4 du menu console.
 *
 * ATTENTION : ListeDesFactures.afficher() n'est PAS utilisée ici. Cette
 * méthode appelle ApplicationPrincipale.pauseAvantMenu() entre chaque
 * facture (dès qu'il y en a 2 ou plus), qui bloque sur un Scanner lié à
 * System.in — dans une interface graphique, personne ne tape au terminal,
 * donc l'appel resterait bloqué indéfiniment. Ce panel lit directement les
 * tables "factures" et "vehicules_loues" de la base SQLite, alimentées par
 * ListeDesFactures.sauvegarderFactures() (appelée par PanelLocation après
 * chaque nouvelle facture).
 *
 * @author Hachemi Souici
 */
public class PanelFactures extends JPanel {

    private final DefaultTableModel modeleFactures;
    private final JTextArea zoneDetail;

    public PanelFactures() {
        super(new BorderLayout());

        modeleFactures = new DefaultTableModel(new Object[]{"No Facture", "Date", "Client", "Montant total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tableFactures = new JTable(modeleFactures);
        ListSelectionListener listener = e -> {
            if (!e.getValueIsAdjusting()) {
                afficherDetailFactureSelectionnee(tableFactures);
            }
        };
        tableFactures.getSelectionModel().addListSelectionListener(listener);

        zoneDetail = new JTextArea();
        zoneDetail.setEditable(false);
        zoneDetail.setLineWrap(false);
        zoneDetail.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(tableFactures), new JScrollPane(zoneDetail));
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        JButton boutonRafraichir = new JButton("Rafraîchir");
        boutonRafraichir.addActionListener(e -> rafraichir());
        add(boutonRafraichir, BorderLayout.SOUTH);

        rafraichir();
    }

    public void rafraichir() {
        modeleFactures.setRowCount(0);
        zoneDetail.setText("");

        String requete = "SELECT numero_facture, date_facture, prenom, nom, montant_total "
                + "FROM factures ORDER BY numero_facture";

        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();
            try (Statement instruction = connexion.createStatement();
                    ResultSet resultat = instruction.executeQuery(requete)) {
                boolean auMoinsUneFacture = false;
                while (resultat.next()) {
                    auMoinsUneFacture = true;
                    int numeroFacture = resultat.getInt("numero_facture");
                    String dateFacture = resultat.getString("date_facture");
                    String client = resultat.getString("prenom") + " " + resultat.getString("nom");
                    double montantTotal = resultat.getDouble("montant_total");
                    modeleFactures.addRow(new Object[]{numeroFacture, dateFacture, client,
                        String.format("%.2f$", montantTotal)});
                }
                if (!auMoinsUneFacture) {
                    zoneDetail.setText("Aucune facture disponible.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la lecture des factures : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void afficherDetailFactureSelectionnee(JTable tableFactures) {
        int ligne = tableFactures.getSelectedRow();
        if (ligne < 0) {
            return;
        }
        int numeroFacture = (int) modeleFactures.getValueAt(ligne, 0);
        zoneDetail.setText(construireDetailFacture(numeroFacture));
        zoneDetail.setCaretPosition(0);
    }

    private String construireDetailFacture(int numeroFacture) {
        StringBuilder texte = new StringBuilder();

        String requeteFacture = "SELECT * FROM factures WHERE numero_facture = ?";
        String requeteVehicules = "SELECT * FROM vehicules_loues WHERE numero_facture = ? ORDER BY id";

        try {
            Connection connexion = BaseDeDonnees.obtenirConnexion();

            try (PreparedStatement instructionFacture = connexion.prepareStatement(requeteFacture)) {
                instructionFacture.setInt(1, numeroFacture);
                try (ResultSet resultat = instructionFacture.executeQuery()) {
                    if (resultat.next()) {
                        texte.append(Facture.CADRE).append("\n");
                        texte.append("    ").append(Facture.NOM_RVV).append("\n");
                        texte.append("    Adresse :       ").append(Facture.ADRESSE).append("\n");
                        texte.append("    Téléphone :     ").append(Facture.TELEPHONE).append("\n");
                        texte.append("    Date et Heure  ").append(resultat.getString("date_facture")).append("\n");
                        texte.append("    Facture No     ").append(resultat.getInt("numero_facture")).append("\n");
                        texte.append(Facture.CADRE).append("\n\n");

                        texte.append("    Prénom et nom       ").append(resultat.getString("prenom"))
                                .append(" ").append(resultat.getString("nom")).append("\n");
                        texte.append("    Téléphone           ").append(resultat.getString("telephone")).append("\n");
                        texte.append("    Permis de conduire  ").append(resultat.getString("permis_conduire")).append("\n\n");

                        texte.append("    Mode de paiement    ").append(resultat.getString("mode_paiement")).append("\n");
                        String typeCarte = resultat.getString("type_carte_credit");
                        String numeroCarte = resultat.getString("numero_carte_credit");
                        if (typeCarte != null) {
                            texte.append("    Type de carte de crédit : ").append(typeCarte).append("\n");
                            texte.append("    Numéro de carte de crédit : ").append(numeroCarte).append("\n");
                        }

                        double sousTotal = resultat.getDouble("sous_total");
                        double montantTPS = resultat.getDouble("montant_tps");
                        double montantTVQ = resultat.getDouble("montant_tvq");
                        double montantTotal = resultat.getDouble("montant_total");

                        try (PreparedStatement instructionVehicules = connexion.prepareStatement(requeteVehicules)) {
                            instructionVehicules.setInt(1, numeroFacture);
                            try (ResultSet vehicules = instructionVehicules.executeQuery()) {
                                while (vehicules.next()) {
                                    texte.append("\n    Type du véhicule             ").append(vehicules.getString("type_vehicule")).append("\n");
                                    texte.append("    Grandeur du véhicule         ").append(vehicules.getString("grandeur_vehicule")).append("\n");
                                    texte.append("    Nombre de véhicules loués    ").append(vehicules.getInt("nombre_vehicules_loues")).append("\n");
                                    texte.append("    Nombre de jours de location  ").append(vehicules.getInt("nombre_jours_location")).append("\n");
                                    texte.append("    Date de location    ").append(vehicules.getString("date_location")).append("\n");
                                    texte.append("    Date de retour      ").append(vehicules.getString("date_retour")).append("\n");
                                    texte.append(String.format("    Prix de la location par jour       %.2f$%n", vehicules.getDouble("prix_location_par_jour")));
                                    double rabais = vehicules.getDouble("rabais");
                                    if (rabais != 0) {
                                        texte.append(String.format("    Prix de la rabais par jour         %.2f$%n", rabais));
                                    }
                                    texte.append(String.format("    Prix de l'assurance par jour       %.2f$%n", vehicules.getDouble("prix_assurance_par_jour")));
                                    texte.append(String.format("    Montant de la location             %.2f$%n", vehicules.getDouble("montant_location")));
                                    texte.append(String.format("    Montant de l'assurance             %.2f$%n", vehicules.getDouble("montant_assurance")));
                                }
                            }
                        }

                        texte.append(String.format("%n    Sous-total                         %.2f$%n", sousTotal));
                        texte.append(String.format("    Montant TPS                        %.2f$%n", montantTPS));
                        texte.append(String.format("    Montant TVQ                        %.2f$%n", montantTVQ));
                        texte.append(String.format("    Montant total                      %.2f$%n", montantTotal));
                        texte.append("\n").append(Facture.CADRE).append("\n");
                        texte.append("Merci pour votre confiance!\n");
                    }
                }
            }
        } catch (SQLException e) {
            texte.append("Erreur lors de la lecture du détail de la facture : ").append(e.getMessage());
        }

        return texte.toString();
    }
}
