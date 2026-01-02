package pif;

import java.util.Arrays;

/**
 * Classe d'entrée principale de l'application PIF (Proprietary Image Format).
 * Cette classe agit comme un répartiteur (dispatcher) qui permet de lancer soit l'outil de conversion, soit l'outil de visualisation selon les arguments fournis en ligne de commande.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public class Main {

    /**
     * Point d'entrée de l'application.
     * La syntaxe attendue est la suivante :
     * <li><code>java pif.Main convertisseur [entrée] [sortie]</code></li>
     * <li><code>java pif.Main visualisateur [entrée]</code></li>
     * @param args Les arguments de la ligne de commande. 
     * Le premier argument spécifie le mode (<code>convertisseur</code> ou <code>visualisateur</code>).
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

    /**
     * Initialise et affiche l'interface du convertisseur d'images.
     * @param args Arguments optionnels : args[0] pour le fichier source, 
     * args[1] pour la destination par défaut.
     */
    private static void lancerConvertisseur(String[] args) {
        String cheminEntree = args.length > 0 ? args[0] : null;
        String cheminSortie = args.length > 1 ? args[1] : null;
        new FenetreConvertisseur(cheminEntree, cheminSortie).setVisible(true);
    }

    /**
     * Initialise et affiche l'interface du visualisateur de fichiers PIF.
     * @param args Argument optionnel : args[0] pour le chemin du fichier .pif à ouvrir.
     */
    private static void lancerVisualisateur(String[] args) {
        String chemin = (args.length > 0) ? args[0] : null;
        new FenetreVisualisateur(chemin).setVisible(true);
    }

    /**
     * Affiche l'aide utilisateur dans le flux d'erreur standard en cas 
     * d'arguments invalides ou manquants.
     */
    private static void printUsage() {
        System.err.println("Usage: java pif.Main <commande> [options]");
        System.err.println("Commandes:");
        System.err.println("  convertisseur [fichier-entree] [fichier-sortie]");
        System.err.println("  visualisateur [fichier-entree]");
    }
}
