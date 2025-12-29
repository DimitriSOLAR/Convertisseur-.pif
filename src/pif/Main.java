package pif;

import java.util.Arrays;

/**
 * Classe principale de l'application.
 * Lance le convertisseur ou le visualisateur en fonction des arguments.
 */
public class Main {

    /**
     * @Author Dimitri SOLAR, Valentin LOISON
     * @version 1.0
     *
     * Point d'entrée de l'application.
     * 
     * @param args Les arguments de la ligne de commande.
     *             Le premier argument doit être "convertisseur" or "visualisateur".
     *             Les arguments suivants sont passés à l'application choisie.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0];
        String[] appArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (command) {
            case "convertisseur":
                lancerConvertisseur(appArgs);
                break;
            case "visualisateur":
                lancerVisualisateur(appArgs);
                break;
            default:
                System.err.println("Commande inconnue: " + command);
                printUsage();
                break;
        }
    }

    private static void lancerConvertisseur(String[] args) {
        String cheminEntree = args.length > 0 ? args[0] : null;
        String cheminSortie = args.length > 1 ? args[1] : null;
        new FenetreConvertisseur(cheminEntree, cheminSortie).setVisible(true);
    }

    private static void lancerVisualisateur(String[] args) {
        String chemin = (args.length > 0) ? args[0] : null;
        new FenetreVisualisateur(chemin).setVisible(true);
    }

    private static void printUsage() {
        System.err.println("Usage: java pif.Main <commande> [options]");
        System.err.println("Commandes:");
        System.err.println("  convertisseur [fichier-entree] [fichier-sortie]");
        System.err.println("  visualisateur [fichier-entree]");
    }
}
