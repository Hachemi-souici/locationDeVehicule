import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Université du Québec à Montréal (UQAM)
 * INF1120 - 010 - Hiver 2025
 * Travail pratique 3
 *
 * Classe ApplicationPrincipale contient les méthodes d'affichage de menus, de saisies et de
 * validations. Également elle contient la méthode "main". Cette classe permet de tester toutes
 * les autres classes en créant des objets et en appelant leurs méthodes lors de l'application
 * des règles d'affaires liées aux différentes options du menu principal.
 *
 * @author Hachemi Souici(SOUH14059100)
 * @version 21 Avril 2025
 *
 */

public class ApplicationPrincipale {

    public static final String MSG_ANNULATION = "\n La location de(s) véhicule(s) de type %c et de grandeur %c est annulée...\n";

    // TOUTES LES CONSTANTES SONT DISTRIBUÉES DANS LES DIFFÉRENTES
    // CLASSES. ICI JE VEUX JUSTE VOIR LES CONSTANTES SUIVANTES :
    //  - LES VALEURS ENTIÈRES POUR VALIDER LE CHOIX DU MENU.
    //  - LES VALEURS ENTIÈRES POUR VALIDER LA SAISIE DU NOM, DU PRÉNOM, DU NUMÉRO DE TÉLÉPHONE DU LOCATAIRE .
    //  - LES VALEURS ENTIÈRES POUR VALIDER LA SAISIE DU NOMBRE DE JOURS DE LOCATION ET DU NOMBRE DE VÉHICULES À LOUER.
    public static final int MENU_MIN = 1;
    public static final int MENU_MAX = 5;
    public static final int NOM_MIN_LONGUEUR = 2;
    public static final int NOM_MAX_LONGUEUR = 30;
    public static final int TEL_MIN_LONGUEUR = 10;
    public static final int TEL_MAX_LONGUEUR = 15;
    public static final int JOURS_MIN = 1;
    public static final int JOURS_MAX = 30;
    public static final int VEHICULES_MIN = 0;
    public static final int VEHICULES_MAX = 5;

    private static final Scanner clavier = new Scanner(System.in);

    /**
     * Lit une ligne complète saisie au clavier.
     *
     * @return la ligne saisie.
     */
    private static String lireString() {
        return clavier.nextLine();
    }

    /**
     * Lit une ligne saisie au clavier et la convertit en entier.
     *
     * @return l'entier saisi.
     */
    private static int lireInt() {
        return Integer.parseInt(clavier.nextLine().trim());
    }

    /**
     * Lit une ligne saisie au clavier et retourne son premier caractère.
     *
     * @return le premier caractère de la ligne saisie.
     */
    private static char lireCharLn() {
        String ligne = clavier.nextLine();
        return ligne.isEmpty() ? ' ' : ligne.charAt(0);
    }

    /**
     * Consomme la fin de la ligne courante (utilisé pour une pause avant d'afficher le menu).
     */
    private static void lireFinLigne() {
        clavier.nextLine();
    }

    /****************************************************************************************************
    AJOUTEZ TOUTES VOS MÉTHODES "public" et "static" SUIVANTES DÉFINIES DANS LE TRAVAIL PRATIQUE 2.
    1)  Affichage du message de bienvenue
    2)  Saisie et validation de l’option choisie par l’utilisateur
    3)  Saisie et validation du prénom du locataire

    4)  Saisie et validation du nom du locataire
    5)  Saisie et validation du numéro de téléphone du locataire

    6)  Saisie et validation du numéro de permis de conduire
    7)  Saisie et validation du type de véhicule
    8)  Saisie et validation de la grandeur du véhicule

    9)  Saisie et validation du nombre de jours de location
    10) Saisie et validation du mode de paiement
    11) Saisie et validation du type de la carte de crédit
    12) Saisie et validation du numéro de la carte de crédit
    13) Saisie et validation de la réponse de la question si le locataire veut une assurance
    14) Saisie et validation du nombre de véhicules loués

    15) Saisie et validation de la réponse de la question si le locataire veut louer un autre
    type et une autre grandeur de véhicule

    16) Demander à l’utilisateur d’appuyer sur <ENTRÉE> pour réafficher le menu principal
     *********************************************************************************************************/
    /**
     * Cette méthode affiche un message de bienvenue.
     */
    public static void afficherMenuBienvenue() {
        System.out.println("\n---------------------------------------------------------------------------------");
        System.out.println(" Bienvenue dans le système de facturation de Roulons des véhicules verts (RVV)");
        System.out.println("---------------------------------------------------------------------------------\n");
    }

    /**
     * Cette méthode affiche un menu de choix et invite l'utilisateur à entrer un choix.
     * Elle vérifie que le choix de l'utilisateur est valide (entre 1 et 4).
     * En cas de choix invalide, un message d'erreur est affiché et l'utilisateur est invité à entrer un nouveau choix.
     *
     * @return le choi de menu
     */
    public static int entrerValiderChoix() {
        int choixMenu = 0;
        boolean valide;
        do {
            valide = true;
            System.out.println(" *** Menu de choix ***");
            System.out.println(" 1. Facturer la location d'un véhicule");
            System.out.println(" 2. Afficher le nombre de véhicules hybrides et électriques loués");
            System.out.println(" 3. Afficher l'inventaire des véhicules");
            System.out.println(" 4. Afficher toutes les factures");
            System.out.println(" 5. Quitter le programme");
            System.out.print("\n Entrez votre choix :  ");

            try {
                choixMenu = lireInt();
                if (choixMenu < MENU_MIN || choixMenu > MENU_MAX) {
                    System.out.println("\n L'option choisie est invalide!\n");
                    valide = false;
                }
            } catch (NumberFormatException e) {
                System.out.println("\n L'option choisie est invalide!\n");
                valide = false;
            }
        } while (!valide);
        return choixMenu;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer un prénom.
     * Elle vérifie que le prénom saisi est valide, c'est-à-dire que sa longueur est comprise entre 2 et 30 caractères inclus.
     * Si le prénom est invalide, un message d'erreur s'affiche, et l'utilisateur est invité à ressaisir un prénom jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le prénom de client.
     */
    public static String entrerValiderPrenom() {
        String prenom;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le prenom du locataire (entre 2 et 30 caractères inclusivement):  ");
            prenom = lireString();
            if (prenom.length() < 2 || prenom.length() > 30) {
                System.out.println("\n Le prénom est invalide !");
                valide = !valide;
            }
        }while (!valide);
        return prenom;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer un nom.
     * Elle vérifie que le nom saisi est valide, sa longueur doit etre comprise entre 2 et 30 caractères inclus.
     * Si le nom est invalide, un message d'erreur est affiché et l'utilisateur est invité à ressaisir un nom jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le nom de client .
     */
    public static String entrerValiderNom() {
        String nom;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le nom du locataire (entre 2 et 30 caractères inclusivement): ");
            nom = lireString();
            if (nom.length() < 2 || nom.length() > 30) {
                System.out.println("\n Le nom est invalide !");
                valide = !valide;
            }
        }while (!valide);
        return nom;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer un numéro de téléphone au format spécifique (XXX) XXX-XXXX
     * Elle vérifie que le numéro saisi correspond au format défini par l'expression et que sa longueur est exactement de 14 caractères.
     * En cas de saisie invalide, un message d'erreur est affiché et l'utilisateur est invité à ressaisir un numéro jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le numero de telephone de client.
     */
    public static String entrerValiderNumeroTelephone() {
        String numeroTelephone;
        String modelNumero = "\\(\\d{3}\\) \\d{3}-\\d{4}";
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le numéro de téléphone du locataire (Exemple : (514) 784-6589): ");
            numeroTelephone = lireString();
            if (!numeroTelephone.matches(modelNumero) || numeroTelephone.length() != 14) {
                System.out.println("\n Le numéro de téléphone est invalide !");
                valide =!valide;
            }
        }while (!valide);
        return numeroTelephone;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer un numéro de permis doit respecter le format suivant : 1234-123456-12 et une longeurs 15 caractères.
     * Si la saisie est invalide, un message d'erreur est affiché et l'utilisateur est invité à ressaisir le numéro jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le numero de permis de client.
     */
    public static String entrerValiderNumeroPermis() {
        String numeroPermis;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le numéro de permis du locataire (Exemple : D1234-567891-23): ");
            numeroPermis = lireString();
            if (!numeroPermis.matches("[a-zA-Z]\\d{4}-\\d{6}-\\d{2}") && numeroPermis.length() != 15) {
                System.out.println("\n Le numéro de permis de conduire est invalide !");
                valide =!valide;
            }
        }while (!valide);
        return numeroPermis;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer un type de véhicule et convertir l'entrée en majuscule.
     * Le type de véhicule saisi doit être soit HYBRIDE (I ou i), soit ELECTRIQUE (E ou e).
     * Si la saisie est invalide, un message d'erreur est affiché et l'utilisateur est invité à entrer un nouveau type de véhicule jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le type de vehicule selectionner.
     */
    public static char entrerValiderTypeVehicule() {
        char typeVehicule;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le type du véhicule à louer\n (H ou h pour Hybride, et E ou e pour Électrique) : ");
            typeVehicule = Character.toUpperCase(lireCharLn());
            if (typeVehicule != 'H' && typeVehicule != 'E') {
                System.out.println("\n Le type de véhicule est invalide !");
                valide =!valide;
            }
        }while (!valide);
        return typeVehicule;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer la grandeur d'un véhicule et convertir l'entrée en majuscule.
     * Le type de véhicule saisi doit être soit PETIT (P ou p), INTERMEDIAIRE (I ou i), ou GRANDE (G ou g).
     * Si la saisie est invalide, un message d'erreur est affiché, et l'utilisateur est invité à ressaisir jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return la grandeur de vehicule selectionner.
     */
    public static char entrerValiderGrandeurVehicule() {
        char grandeurVehicule;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le grandeur du véhicule à louér\n (P ou p pour petit,I ou i pour intermédiaire et G ou g pour grand) :  ");
            grandeurVehicule = Character.toUpperCase(lireCharLn());
            if (grandeurVehicule != 'P' && grandeurVehicule != 'I' && grandeurVehicule != 'G') {
                System.out.println("\n La grandeur du véhicule est invalide !");
                valide = !valide;
            }
        }while(!valide);

        return grandeurVehicule;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer un nombre de jours.
     * Le nombre saisi doit être compris entre 1 et 30 inclus. Si le nombre de jours de location est invalide, un message d'erreur est affiché, et
     * l'utilisateur est invité à ressaisir un nombre jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le nombre de jours de location.
     */
    public static int entrerValiderNombreJoursLocation() {
        int nombreJoursLocation;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le nombre de jours de location\n (superieur à 0 et inferieur ou egal à 30) : ");
            nombreJoursLocation = lireInt();
            if (nombreJoursLocation < 1 || nombreJoursLocation > 30) {
                System.out.println("\n Le nombre de jours de location est invalide !");
                valide = !valide;
            }
        }while (!valide);

        return nombreJoursLocation;
    }

    /**
     *
     * Cette méthode invite l'utilisateur à entrer un mode de paiement et convertir l'entrée en majuscule.
     * Les modes de paiement valides doivent correspondre aux constantes définies, DEBIT (D ou d) ou CREDIT (C ou c).
     * Si le mode de paiement saisi est invalide, un message d'erreur est affiché et l'utilisateur est invité à ressaisir un mode de paiement jusqu'à ce qu'une
     * saisie valide soit effectuée.
     *
     * @return le mode de paiement.
     */
    public static char entrerValiderModePaiement() {
        char modePaiement;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le mode  de paiement\n ( D ou d pour Debit, C ou c pour Credit) : ");
            modePaiement = Character.toUpperCase(lireCharLn());
            if (modePaiement != 'D' && modePaiement != 'C') {
                System.out.println("\n Le mode de paiement est invalide !");
            }
        }while (!valide);

        return modePaiement;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer le type de carte de crédit et convertir l'entrée en majuscule.
     * Le type de carte saisi doit être soit VISA (V ou v), soit MASTERCARD (M ou m).
     * Si la saisie est invalide, un message d'erreur est affiché et l'utilisateur est invité à ressaisir un type de carte jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le type de carte de credit utiliser.
     */
    public static char entrerValiderTypeCarteCredit() {
        char typeCarteCredit;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le type  de carte credit\n ( M ou m pour Mastercard,V ou v pour Visa)  : ");
            typeCarteCredit = Character.toUpperCase(lireCharLn());
            if (typeCarteCredit != 'V' && typeCarteCredit != 'M') {
                System.out.println("\n Le type de la carte de crédit est invalide !");
                valide = !valide;
            }
        }while (!valide);

        return typeCarteCredit;
    }

    /**
     * Cette méthode invite l'utilisateur à entrer un numéro de carte de crédit.
     * Le numéro saisi doit respecter un format spécifique (XXXX XXXX XXXX XXXX) d et avoir une longueur exacte de 19 caractères.
     * Si le numéro est invalide, un message d'erreur est affiché et l'utilisateur est invité à ressaisir le numéro jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le numero de la carte.
     */
    public static String entrerValiderNumeroCarteCredit() {
        String numeroCarteCredit;
        String modelCarteCredit = "\\d{4} \\d{4} \\d{4} \\d{4}";
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Entrez le numero de carte (Exemple : 1234 5678 9123 4567): ");
            numeroCarteCredit = lireString();
            if (!numeroCarteCredit.matches(modelCarteCredit) || numeroCarteCredit.length() != 19) {
                System.out.println("\n Le numéro de la carte de crédit est invalide !");
                valide = !valide;
            }
        }while (!valide);

        return numeroCarteCredit;
    }

    /**
     * Cette méthode invite l'utilisateur à répondre à une demande d'assurance et convertir l'entrée en majuscule.
     * La réponse valide doit être soit OUI (O ou o) ou NON (N ou n).
     * Si la réponse est invalide, un message d'erreur est affiché et l'utilisateur est invité à ressaisir sa réponse jusqu'à ce qu'une saisie valide soit effectuée.
     *
     * @return le  choi de client pour l'assurance.
     */
    public static boolean entrerValiderRepondeAssurance() {
        char reponseAssurance = ' ';
        boolean assuranceEstZero= true;
        boolean valide;
        do{
            valide = true;
            System.out.print("\n Désirez-vous prendre l'assurance\n(O ou o pour Oui, N ou n pour Non) ? : ");
            reponseAssurance  = Character.toUpperCase(lireCharLn());
            if (reponseAssurance != 'O' && reponseAssurance != 'N') {
                System.out.println("\n La réponse est invalide !");
                valide = !valide;
            }

        }while (!valide);
        assuranceEstZero = (reponseAssurance == 'N');
        return assuranceEstZero;
    }

    /**
 * Permet à l'utilisateur de saisir le nombre de véhicules à louer.
 * Assure que la saisie est valide en vérifiant les bornes minimales et maximales,
 * ainsi que la disponibilité des véhicules.
 *
 * @param nombreVehiculeDisponibles Le nombre de véhicules disponibles pour la location.
 * @param typeVehicule Le type du véhicule (ex. Électrique, Hybride).
 * @param grandeurVehicule La grandeur du véhicule (ex. Petit, Intermédiaire, Grand).
 * @return Le nombre de véhicules loués validé.
 */

    public static int saisirNombreVehiculesLoues(int nombreVehiculeDisponibles, char typeVehicule, char grandeurVehicule) {
        int nbVehicules;

        do {
            if (nombreVehiculeDisponibles == 0) {
                System.out.printf("\n  Aucun vehicule n'est disponible(s) pour le type %c et grandeur %c.\n",
                     typeVehicule, grandeurVehicule);
            nbVehicules = 0;
            }else {


            System.out.printf("\n Entrez le nombre de véhicules à louer (%d à %d, 0 pour annuler) : ", VEHICULES_MIN, VEHICULES_MAX);
            nbVehicules = lireInt();

            if (nbVehicules < VEHICULES_MIN || nbVehicules > VEHICULES_MAX) {
                System.out.println("\n Veuillez entrer un nombre de véhicules valide.");
            }else if (nombreVehiculeDisponibles < nbVehicules){
                System.out.printf("\n Seulement %d disponible(s) pour le type %c et grandeur %c.\n",
                    nombreVehiculeDisponibles, typeVehicule, grandeurVehicule);
            }
        }

        } while (nbVehicules < VEHICULES_MIN || nbVehicules > VEHICULES_MAX || nombreVehiculeDisponibles < nbVehicules);
        return nbVehicules; // Retourne le nombre de véhicules validé
    }

    public static boolean saisirLocationSupplementaire() {
        char reponse;
        boolean ajouterAutre = true;
        do {
            System.out.print("\n Souhaitez-vous louer un autre type ou grandeur de véhicule (O pour Oui, N pour Non) ? ");
            reponse = Character.toUpperCase(lireCharLn());

            if (reponse != 'O' && reponse != 'N') {
                System.out.println(" Erreur : Veuillez entrer une réponse valide (O pour Oui, N pour Non).");
            }
        } while (reponse != 'O' && reponse != 'N');
        ajouterAutre = (reponse == 'O');
        return ajouterAutre;
    }

    /**
 * Demande à l'utilisateur s'il souhaite louer un autre type ou une autre grandeur de véhicule.
 * Assure que la saisie est valide et renvoie `true` si l'utilisateur souhaite continuer la location.
 *
 * @return `true` si l'utilisateur souhaite louer un autre véhicule, `false` sinon.
 */
    public static void pauseAvantMenu() {
        System.out.println("\n Appuyez sur <ENTRÉE> pour revenir au menu principal.");
        lireFinLigne();
    }

    public static void main(String[] args) {

        // Déclaration des variables
        boolean sortie;
        int choixMenu;
        int nbVehiculesALouer;
        char typeVehicule;
        char grandeurVehicule;
        char reponse = ' ';
        char modePaiement;
        char typeCarteCredit;
        double prixLocationParJour;
        double prixAssuranceParJour;
        boolean assuranceEstZero;
        char reponseAssurance = ' ';
        int nombreJoursLocation;
        int nbDisponibles;


        Locataire locataire;
        VehiculeLoue vehiculeLoue;
        LocationVehicule locationVehicule;
        LocalDateTime dateFacture;
        Facture facture;
        Vehicule vehicule;

        // Initialiser la base de données (création du fichier et des tables au besoin)
        try {
            BaseDeDonnees.initialiser();
        } catch (java.sql.SQLException e) {
            System.err.println("Erreur : impossible d'initialiser la base de données : " + e.getMessage());
            return;
        }

        // Lire les données des véhicules disponibles dans l'inventaire
        GestionVehiculesDisponibles.chargerVehiculesDisponibles();

        // APPELEZ LA MÉTHODE QUI AFFICHE LE MESSAGE DE BIENVENUE
        ApplicationPrincipale.afficherMenuBienvenue();

        /***************************************************
         * Début du programme
         **************************************************/

        sortie = false;

        do {

            // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE L'OPTION CHOISIE PAR L'UTILISATEUR.
            choixMenu = 0;
            choixMenu = ApplicationPrincipale.entrerValiderChoix();

            switch (choixMenu) {

                case 1:
                    // CRÉEZ UN OBJET DE TYPE LocationVehicule AVEC LE CONSTRUCTEUR SANS PARAMÈTRE
                    locationVehicule = null;
                    locationVehicule = new LocationVehicule();

                    // CRÉEZ LA DATE DE LA FACTURE
                    dateFacture = null;
                    dateFacture = LocalDateTime.now();

                    // Saisir les données
                    do {

                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE TYPE DU VÉHICULE.
                        typeVehicule = ApplicationPrincipale.entrerValiderTypeVehicule();

                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LA GRANDEUR DU VÉHICULE.
                        grandeurVehicule = ApplicationPrincipale.entrerValiderGrandeurVehicule();

                        if (locationVehicule.obtenirPositiondeVehiculeLoue(typeVehicule, grandeurVehicule) != -1) {
                            System.out.print("\n  Vous avez déjà loué un ou des véhicules de ce type et de cette grandeur...\n");

                        } else {
                            // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE NOMBRE DE VÉHICULES À LOUER.
                            nbDisponibles = GestionVehiculesDisponibles.obtenirNombreVehiculesDisponibles(typeVehicule, grandeurVehicule);
                            nbVehiculesALouer = ApplicationPrincipale.saisirNombreVehiculesLoues(nbDisponibles, typeVehicule,grandeurVehicule);

                            if (nbVehiculesALouer == 0 ) {
                                System.out.printf(MSG_ANNULATION, typeVehicule, grandeurVehicule);
                            } else {
                                // APPELEZ LA MÉTHODE diminuerNbVehiculesDisponibles DE LA CLASSE GestionVehiculesDisponibles
                                // POUR DIMINUER LE NOMBRE DE VÉHICULES À LOUER DE CE TYPE ET DE CETTE GRANDEUR DANS L'INVENTAIRE
                                GestionVehiculesDisponibles.diminuerNombreVehiculesDisponibles(typeVehicule, grandeurVehicule, nbVehiculesALouer);

                                // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE NOMBRE DE JOURS DE LOCATION.
                                nombreJoursLocation = ApplicationPrincipale.entrerValiderNombreJoursLocation();

                                // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LA RÉPONSE DE LA QUESTION
                                // SI L'UTILISATEUR DÉSIRE PRENDRE UNE ASSURANCE.
                                assuranceEstZero = ApplicationPrincipale.entrerValiderRepondeAssurance();

                                // APPELEZ LA MÉTHODE obtenirPrixLocationVehParJour DE LA CLASSE GestionVehiculesDisponibles
                                // POUR OBTENIR LE PRIX DE LA LOCATION PAR JOUR POUR CE TYPE ET DE CETTE GRANDEUR DE VÉHICULE.
                                prixLocationParJour = GestionVehiculesDisponibles.obtenirPrixLocationVehParJour(typeVehicule, grandeurVehicule);

                                // APPELEZ LA MÉTHODE obtenirPrixAssuranceVehParJour DE LA CLASSE GestionVehiculesDisponibles
                                // POUR OBTENIR LE PRIX DE L'ASSURANCE PAR JOUR POUR CE TYPE ET DE CETTE GRANDEUR DE VÉHICULE.
                                prixAssuranceParJour = GestionVehiculesDisponibles.obtenirPrixAssuranceVehParJour(typeVehicule, grandeurVehicule, assuranceEstZero);

                                // CRÉEZ UN OBJET DE TYPE vehicule AVEC LES PARAMÈTRES SUIVANTS :
                                // LE TYPE DE VÉHICULE, LA GRANDEUR DU VÉHICULE, LE PRIX DE LA LOCATION PAR JOUR,
                                // LE PRIX DE L'ASSURANCE PAR JOUR.
                                vehicule = new Vehicule(typeVehicule, grandeurVehicule, (float) prixLocationParJour, (float) prixAssuranceParJour);

                                // CRÉEZ UN OBJET DE TYPE vehiculeLoue AVEC LES PARAMÈTRES SUIVANTS :
                                // LE VÉHICULE, LA DATE DE LA FACTURE + 3 HEURES, LE NOMBRE DE VÉHICULES À LOUER,
                                // LE NOMBRE DE JOURS DE LOCATION,
                                vehiculeLoue = new VehiculeLoue(vehicule, nbVehiculesALouer, nombreJoursLocation,dateFacture.plusHours(3));

                                // APPELEZ LA MÉTHODE ajouterVehiculeLoue DE L'OBJET locationVehicule
                                // POUR AJOUTER LE VÉHICULE LOUÉ DANS LE TABLEAU DES VÉHICULES LOUÉS
                                locationVehicule.ajouterVehiculeLoue(vehiculeLoue);

                                // APPELEZ LA MÉTHODE augmenterNbVehiculesLoues DE LA CLASSE StatistiquesVehiculesLoues
                                // POUR METTRE À JOUR LE NOMBRE DE VÉHICULES LOUÉS DE CE TYPE ET DE CETTE GRANDEUR DE VÉHICULE
                                StatistiquesVehiculesLoues.augmenterNombreVehiculesLoues(vehiculeLoue);

                            }
                        }
                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LA RÉPONSE DE LA QUESTION
                        // SI LE LOCATAIRE DÉSIRE LOUER D'AUTRES VÉHICULES.
                        reponse = ApplicationPrincipale.saisirLocationSupplementaire() ? 'O' : 'N';

                        // TANT QUE LE LOCATAIRE DÉSIRE LOUER D'AUTRES VÉHICULES
                    } while (reponse == 'O');

                    if (locationVehicule.nombreTypesVehiculesLoues() > 0) {

                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE PRÉNOM DU LOCATAIRE.
                        String prenomLocataire = ApplicationPrincipale.entrerValiderPrenom();

                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE NOM DU LOCATAIRE.
                        String nomLocataire = ApplicationPrincipale.entrerValiderNom();

                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE NUMÉRO DE TÉLÉPHONE DU LOCATAIRE.
                        String numeroTelephone = ApplicationPrincipale.entrerValiderNumeroTelephone();

                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE NUMÉRO DE PERMIS DE CONDUIRE DU LOCATAIRE.
                        String numeroPermisConduire = ApplicationPrincipale.entrerValiderNumeroPermis();

                        // CRÉEZ UN OBJET DE TYPE Locataire AVEC LES PARAMÈTRES SUIVANTS :
                        // LE NOM, LE PRÉNOM, LE NUMÉRO DE TÉLÉPHONE ET LE NUMÉRO DE PERMIS DE CONDUIRE.
                        locataire = new Locataire(nomLocataire, prenomLocataire, numeroTelephone, numeroPermisConduire);

                        // APPELEZ LA MÉTHODE setLocataire DE L'OBJET locationVehicule
                        // AVEC LE PARAMÈTRE LOCATAIRE POUR modifier le locataire
                        locationVehicule.setLocataire(locataire);

                        // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE MODE DE PAIEMENT.
                        modePaiement = ApplicationPrincipale.entrerValiderModePaiement();

                        // CRÉEZ UN OBJET DE TYPE Facture AVEC LES PARAMÈTRES SUIVANTS :
                        // LA DATE DE LA FACTURE, LOCATIONVEHICULE ET LE MODE DE PAIEMENT.
                        facture = new Facture(dateFacture, locationVehicule, modePaiement);

                        // SI LE MODE DE PAIEMENT EST CRÉDIT
                         if (modePaiement == 'C') {
                            // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE TYPE DE LA CARTE DE CRÉDIT.
                            typeCarteCredit = ApplicationPrincipale.entrerValiderTypeCarteCredit();
                            facture.setTypeCarteCredit(typeCarteCredit);

                            // APPELEZ LA MÉTHODE QUI SAISIT ET VALIDE LE NUMÉRO DE LA CARTE DE CRÉDIT.
                            String numeroCarteCredit = ApplicationPrincipale.entrerValiderNumeroCarteCredit();


                            // APPELEZ LA MÉTHODE setTypeCarteCredit DE DE L'OBJET facture
                            // POUR MODIFIER LE TYPE DE LA CARTE DE CRÉDIT.
                            facture.setTypeCarteCredit(typeCarteCredit);

                            // APPELEZ LA MÉTHODE setNumeroCarteCredit DE DE L'OBJET facture
                            // POUR MODIFIER LE NUMÉRO DE LA CARTE DE CRÉDIT.
                            facture.setNumeroCarteCredit(numeroCarteCredit);

                        }

                        // APPELEZ LES MÉTHODES DE L'OBJET facture DANS L'ORDRE SUIVANT :
                        //    - Calcul du sous-total de la facture
                        facture.calculerSousTotal();
                        //    - Calcul du montant TPS
                        facture.calculerMontantTPS();
                        //    - Calcul du montant TVQ
                        facture.calculerMontantTVQ();
                        //    - Calcul du montant total de la facture
                        facture.calculerMontantTotal();
                        //    - Afficher la facture
                        facture.afficherFacture();
                        // APPELEZ LA MÉTHODE ajouterFacture DE LA CLASSE ListeDesFactures
                        // POUR AJOUTER LA FACTURE COURANTE (l'objet facture) DANS LE TABLEAU
                        // DES FACTURES.
                        ListeDesFactures.ajouterFacture(facture);
                    }

                    // VOUS DEVEZ APPELER LA MÉTHODE DE PAUSE AVANT
                    // D'AFFICHER LE MENU PRINCIPAL.
                    ApplicationPrincipale.pauseAvantMenu();

                    break;

                case 2:
                    // APPELEZ LA MÉTHODE afficherNbVehiculesLoues DE LA CLASSE StatistiquesVehiculesLoues
                    // POUR AFFICHER LE NOMBRE DE VÉHICULES LOUÉS PAR TYPE ET GRANDEUR DE VÉHICULE
                    StatistiquesVehiculesLoues.afficherNombreVehiculesLoues();

                    // VOUS DEVEZ APPELER LA MÉTHODE DE PAUSE AVANT
                    // D'AFFICHER LE MENU PRINCIPAL.
                    ApplicationPrincipale.pauseAvantMenu();

                    break;

                case 3:
                    // APPELEZ LA MÉTHODE afficher DE LA CLASSE GestionVehiculesDisponibles
                    // POUR AFFICHER LA LISTE DES VÉHICULES DISPONIBLES
                    GestionVehiculesDisponibles.afficher();

                    // VOUS DEVEZ APPELER LA MÉTHODE DE PAUSE AVANT
                    // D'AFFICHER LE MENU PRINCIPAL.
                    ApplicationPrincipale.pauseAvantMenu();

                    break;

                case 4:

                    // APPELEZ LA MÉTHODE afficher DE LA CLASSE ListeDesFactures
                    // POUR AFFICHER TOUTES LES FACTURES CRÉÉES
                    ListeDesFactures.afficher();

                    // VOUS DEVEZ APPELER LA MÉTHODE DE PAUSE AVANT
                    // D'AFFICHER LE MENU PRINCIPAL.
                    ApplicationPrincipale.pauseAvantMenu();

                    break;

                case 5:

                    // APPELEZ LA MÉTHODE sauvegarderFactures DE LA CLASSE ListeDesFactures
                    // POUR ENREGISTRER LES DONNÉES DE TOUTES LES FACTURES DANS LA BASE DE DONNÉES.
                    ListeDesFactures.sauvegarderFactures();

                    // FERMER LA CONNEXION À LA BASE DE DONNÉES AVANT DE QUITTER.
                    BaseDeDonnees.fermer();

                    // APPELEZ LE MESSAGE DE REMERCIEMENT
                    System.out.println("\n\n  Merci et à la prochaine ! ");

                    sortie = true;
            }
        } while (!sortie);

    }
}
