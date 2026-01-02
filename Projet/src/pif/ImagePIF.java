package pif;

import java.io.*;
import java.util.Map;
import java.awt.image.BufferedImage;
import pif.CodecHuffman.InfoCode;

/**
 * Représente une image au format PIF (Proprietary Image Format).
 * Cette classe gère la structure de données de l'image (pixels, dimensions) ainsi que les mécanismes de persistance (lecture/écriture) incluant la compression et décompression par codage de Huffman canonique.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public class ImagePIF {
    
    // Largeur de l'image en pixels 
    private int largeur;
    
    // Hauteur de l'image en pixels
    private int hauteur;
    
    // Tableau unidimensionnel des pixels au format ARGB
    private int[] pixels;

    /**
     * Construit une instance d'ImagePIF avec ses données brutes.
     * @param largeur Largeur de l'image.
     * @param hauteur Hauteur de l'image.
     * @param pixels Tableau des pixels.
     */
    public ImagePIF(int largeur, int hauteur, int[] pixels) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.pixels = pixels;
    }

    // @return La largeur de l'image
    public int getLargeur() {
        return largeur;
    }

    // @return La hauteur de l'image
    public int getHauteur() {
        return hauteur;
    }

    // @return Le tableau des pixels
    public int[] getPixels() {
        return pixels;
    }

    /**
     * Convertit l'objet actuel en une image manipulable par Java AWT/Swing.
     * @return Une instance de {@link BufferedImage} de type <code>TYPE_INT_RGB</code>.
     */
    public BufferedImage versBufferedImage() {
        BufferedImage img = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, largeur, hauteur, pixels, 0, largeur);
        return img;
    }

    /**
     * Crée un objet ImagePIF à partir d'une image standard.
     * @param img L'image source à convertir.
     * @return Une nouvelle instance de {@link ImagePIF}.
     */
    public static ImagePIF depuisBufferedImage(BufferedImage img) {
        int l = img.getWidth();
        int h = img.getHeight();
        int[] rgb = img.getRGB(0, 0, l, h, null, 0, l);
        return new ImagePIF(l, h, rgb);
    }

    /**
     * Sauvegarde l'image sur le disque avec compression de Huffman.
     * Le processus suit les étapes suivantes :
     * 1. Écriture des dimensions.
     * 2. Séparation des canaux R, V, B.
     * 3. Calcul des fréquences et des codes canoniques par canal.
     * 4. Écriture des tables de longueurs (768 octets).
     * 5. Écriture du flux binaire compressé.
     * @param chemin Le chemin de destination du fichier .pif.
     * @throws IOException Si une erreur d'accès au fichier survient.
     */
    public void sauvegarder(String chemin) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(chemin);
             FluxSortieBits fsb = new FluxSortieBits(fos)) {

            // En-tête (2 x 16 bits pour les dimensions)
            ecrireShort(fos, largeur);
            ecrireShort(fos, hauteur);

            // Séparation des canaux RGB
            int[] r = new int[pixels.length];
            int[] v = new int[pixels.length];
            int[] b = new int[pixels.length];

            for (int i = 0; i < pixels.length; i++) {
                int c = pixels[i];
                r[i] = (c >> 16) & 0xFF;
                v[i] = (c >> 8) & 0xFF;
                b[i] = c & 0xFF;
            }

            // Calcul des fréquences et construction des codes
            int[] freqR = CodecHuffman.calculerFrequences(r);
            int[] freqV = CodecHuffman.calculerFrequences(v);
            int[] freqB = CodecHuffman.calculerFrequences(b);

            Map<Integer, String> initR = CodecHuffman.genererCodesInitiaux(CodecHuffman.construireArbre(freqR));
            Map<Integer, String> initV = CodecHuffman.genererCodesInitiaux(CodecHuffman.construireArbre(freqV));
            Map<Integer, String> initB = CodecHuffman.genererCodesInitiaux(CodecHuffman.construireArbre(freqB));

            int[] lenR = CodecHuffman.genererLongueursCanoniques(initR);
            int[] lenV = CodecHuffman.genererLongueursCanoniques(initV);
            int[] lenB = CodecHuffman.genererLongueursCanoniques(initB);

            // Stockage des tables de codage (En-tête de décompression)
            ecrireTable(fos, lenR);
            ecrireTable(fos, lenV);
            ecrireTable(fos, lenB);

            Map<Integer, InfoCode> mapR = CodecHuffman.genererCodesCanoniques(lenR);
            Map<Integer, InfoCode> mapV = CodecHuffman.genererCodesCanoniques(lenV);
            Map<Integer, InfoCode> mapB = CodecHuffman.genererCodesCanoniques(lenB);

            // Écriture du corps de l'image (flux de bits)
            for (int i = 0; i < pixels.length; i++) {
                fsb.ecrireChaineBinaire(mapR.get(r[i]).codeChaine);
                fsb.ecrireChaineBinaire(mapV.get(v[i]).codeChaine);
                fsb.ecrireChaineBinaire(mapB.get(b[i]).codeChaine);
            }
            fsb.vider();
        }
    }

    /**
     * Charge et décompresse une image PIF depuis un fichier.
     * @param chemin Le chemin du fichier .pif à lire.
     * @return Une instance d'{@link ImagePIF} prête à l'affichage.
     * @throws IOException Si le fichier est corrompu ou illisible.
     */
    public static ImagePIF charger(String chemin) throws IOException {
        try (FileInputStream fis = new FileInputStream(chemin);
             FluxEntreeBits feb = new FluxEntreeBits(fis)) {

            int l = lireShort(fis);
            int h = lireShort(fis);

            int[] lenR = lireTable(fis);
            int[] lenV = lireTable(fis);
            int[] lenB = lireTable(fis);

            NoeudHuffman racineR = CodecHuffman.reconstruireArbreCanonique(lenR);
            NoeudHuffman racineV = CodecHuffman.reconstruireArbreCanonique(lenV);
            NoeudHuffman racineB = CodecHuffman.reconstruireArbreCanonique(lenB);

            int[] pixels = new int[l * h];
            for (int i = 0; i < pixels.length; i++) {
                int r = lireValeur(feb, racineR);
                int v = lireValeur(feb, racineV);
                int b = lireValeur(feb, racineB);
                pixels[i] = (0xFF << 24) | (r << 16) | (v << 8) | b;
            }

            return new ImagePIF(l, h, pixels);
        }
    }

    /**
     * Décode un symbole unique en parcourant l'arbre de Huffman selon les bits lus.
     * @param feb Le flux d'entrée bit à bit.
     * @param racine La racine de l'arbre de Huffman pour le canal concerné.
     * @return La valeur décodée (0-255).
     * @throws IOException Si la fin du fichier est atteinte prématurément.
     */
    private static int lireValeur(FluxEntreeBits feb, NoeudHuffman racine) throws IOException {
        NoeudHuffman courant = racine;
        while (!courant.estFeuille()) {
            int bit = feb.lireBit();
            if (bit == -1) throw new EOFException("Fin de flux inattendue");
            courant = (bit == 0) ? courant.gauche : courant.droit;
        }
        return courant.valeur;
    }

    // Méthodes utilitaires d'I/O

    // Écrit un entier sur 16 bits dans le flux
    private static void ecrireShort(OutputStream os, int val) throws IOException {
        os.write((val >> 8) & 0xFF);
        os.write(val & 0xFF);
    }

    // Lit un entier sur 16 bits depuis le flux
    private static int lireShort(InputStream is) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        if ((ch1 | ch2) < 0) throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    // Écrit les 256 octets d'une table de longueurs de codes
    private static void ecrireTable(OutputStream os, int[] longueurs) throws IOException {
        for (int l : longueurs) os.write(l);
    }

    // Lit les 256 octets d'une table de longueurs de codes
    private static int[] lireTable(InputStream is) throws IOException {
        int[] longueurs = new int[256];
        for (int i = 0; i < 256; i++) {
            int val = is.read();
            if (val < 0) throw new EOFException();
            longueurs[i] = val;
        }
        return longueurs;
    }
}
