package pif;
import pif.CodecHuffman.InfoCode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Fenêtre principale de l'application de conversion d'images au format PIF.
 * Cette interface permet de charger une image standard (PNG, JPG), d'analyser ses composantes colorimétriques (RGB), d'afficher les statistiques de Huffman et d'exporter le résultat compressé.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public final class FenetreConvertisseur extends JFrame {
    
    // Identifiant de sérialisation pour la compatibilité 
    private static final long serialVersionUID = 1L;
    
    // L'image actuellement chargée en mémoire
    private transient BufferedImage imageCourante;
    
    // Composant affichant l'aperçu visuel de l'image
    private JLabel etiquetteImage;
    
    // Panneau à onglets affichant les tables de codage pour chaque canal (R, V, B) 
    private JTabbedPane ongletsStats;
    
    // Chemin de sauvegarde prédéfini lors du lancement.
    private String cheminSortieDefaut;

    /**
     * Initialise la fenêtre, ses composants graphiques et tente de charger une image si un chemin est fourni.
     * @param cheminEntree Chemin vers l'image à charger (peut être null).
     * @param cheminSortie Chemin par défaut pour l'exportation PIF.
     */
    public FenetreConvertisseur(String cheminEntree, String cheminSortie) {
        setTitle("Convertisseur PIF");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        this.cheminSortieDefaut = cheminSortie;

        setLayout(new BorderLayout());

        etiquetteImage = new JLabel("Aucune image chargée", SwingConstants.CENTER);
        etiquetteImage.setPreferredSize(new Dimension(300, 300));
        add(new JScrollPane(etiquetteImage), BorderLayout.NORTH);

        ongletsStats = new JTabbedPane();
        add(ongletsStats, BorderLayout.CENTER);

        JPanel controles = new JPanel();
        JButton boutonOuvrir = new JButton("Ouvrir une image");
        JButton boutonConvertir = new JButton("Convertir en PIF");

        boutonOuvrir.addActionListener(new ActionOuvrir(this));
        boutonConvertir.addActionListener(new ActionConvertir(this));

        controles.add(boutonOuvrir);
        controles.add(boutonConvertir);
        add(controles, BorderLayout.SOUTH);

        if (cheminEntree != null) {
            chargerImage(cheminEntree);
        } else {
            ouvrirImage();
        }
    }

    /**
     * Affiche un sélecteur de fichier pour permettre à l'utilisateur de choisir 
     * une image à traiter.
     */
    void ouvrirImage() {
        JFileChooser selecteur = new JFileChooser();
        if (selecteur.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chargerImage(selecteur.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Charge une image depuis le disque, met à jour l'aperçu et recalcule les statistiques de Huffman.
     * @param chemin Le chemin absolu du fichier image.
     */
    private void chargerImage(String chemin) {
        try {
            BufferedImage img = ImageIO.read(new File(chemin));
            if (img == null) {
                JOptionPane.showMessageDialog(this, "Impossible de charger l'image (format non supporté ?)");
                return;
            }
            this.imageCourante = img;

            ImageIcon icone = new ImageIcon(getImageRedimensionnee(img, 300, 300));
            etiquetteImage.setIcon(icone);
            etiquetteImage.setText("");
            calculerEtAfficherStats();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Redimensionne une image pour qu'elle tienne dans les dimensions données tout en conservant son ratio d'aspect.
     * @param src L'image source.
     * @param l Largeur maximale souhaitée.
     * @param h Hauteur maximale souhaitée.
     * @return Une version redimensionnée de l'image.
     */
    private Image getImageRedimensionnee(BufferedImage src, int l, int h) {
        int lOriginale = src.getWidth();
        int hOriginale = src.getHeight();
        double ratio = Math.min((double) l / lOriginale, (double) h / hOriginale);
        int nouvL = (int) (lOriginale * ratio);
        int nouvH = (int) (hOriginale * ratio);
        return src.getScaledInstance(nouvL, nouvH, Image.SCALE_SMOOTH);
    }

    /**
     * Extrait les pixels de l'image et sépare les canaux Rouge, Vert et Bleu
     * pour lancer l'analyse de fréquence.
     */
    private void calculerEtAfficherStats() {
        ongletsStats.removeAll();
        int l = imageCourante.getWidth();
        int h = imageCourante.getHeight();

        int[] rgb = imageCourante.getRGB(0, 0, l, h, null, 0, l);
        int[] r = new int[rgb.length];
        int[] v = new int[rgb.length];
        int[] b = new int[rgb.length];

        for (int i = 0; i < rgb.length; i++) {
            int c = rgb[i];
            r[i] = (c >> 16) & 0xFF;
            v[i] = (c >> 8) & 0xFF;
            b[i] = c & 0xFF;
        }

        ajouterStatsCanal("Rouge", r);
        ajouterStatsCanal("Vert", v);
        ajouterStatsCanal("Bleu", b);
    }

    /**
     * Analyse un canal de couleur spécifique, génère ses codes de Huffman et crée un tableau récapitulatif dans un nouvel onglet.
     * @param nom Nom du canal (ex: "Rouge").
     * @param donnees Tableau des valeurs (0-255) du canal.
     */
    private void ajouterStatsCanal(String nom, int[] donnees) {
        int[] frequences = CodecHuffman.calculerFrequences(donnees);
        NoeudHuffman racine = CodecHuffman.construireArbre(frequences);
        Map<Integer, String> codesInitiaux = CodecHuffman.genererCodesInitiaux(racine);
        int[] longueurs = CodecHuffman.genererLongueursCanoniques(codesInitiaux);
        Map<Integer, InfoCode> canoniques = CodecHuffman.genererCodesCanoniques(longueurs);

        String[] colonnes = { "Valeur", "Fréquence", "Code Initial", "Code Canonique" };

        int nbLignes = 0;
        for (int f : frequences)
            if (f > 0)
                nbLignes++;

        Object[][] donneesLignes = new Object[nbLignes][4];
        int index = 0;
        for (int i = 0; i < 256; i++) {
            if (frequences[i] > 0) {
                donneesLignes[index][0] = i;
                donneesLignes[index][1] = frequences[i];
                donneesLignes[index][2] = codesInitiaux.getOrDefault(i, "");
                InfoCode info = canoniques.get(i);
                donneesLignes[index][3] = (info != null) ? info.codeChaine : "";
                index++;
            }
        }

        JTable table = new JTable(donneesLignes, colonnes);
        ongletsStats.addTab(nom, new JScrollPane(table));
    }

    /**
     * Convertit l'image chargée au format PIF en utilisant le codage de Huffman et enregistre le fichier sur le disque.
     */
    void convertirImage() {
        if (imageCourante == null)
            return;

        String chemin = cheminSortieDefaut;
        if (chemin == null) {
            JFileChooser selecteur = new JFileChooser();
            selecteur.setSelectedFile(new File("sortie.pif"));
            if (selecteur.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                chemin = selecteur.getSelectedFile().getAbsolutePath();
                if (!chemin.endsWith(".pif"))
                    chemin += ".pif";
            } else {
                return;
            }
        }

        try {
            ImagePIF pif = ImagePIF.depuisBufferedImage(imageCourante);
            pif.sauvegarder(chemin);
            JOptionPane.showMessageDialog(this, " Sauvegardé sous " + chemin);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
