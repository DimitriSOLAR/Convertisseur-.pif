package pif;

import java.io.*;
import java.util.Map;
import java.awt.image.BufferedImage;
import pif.CodecHuffman.InfoCode;

/**
 * Représente une image au format PIF.
 * Gère la lecture et l'écriture des fichiers .pif.
 */
public class ImagePIF {
    private int largeur;
    private int hauteur;
    private int[] pixels; // ARGB ou RGB

    public ImagePIF(int largeur, int hauteur, int[] pixels) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.pixels = pixels;
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public int[] getPixels() {
        return pixels;
    }

    /**
     * Convertit en BufferedImage.
     * 
     * @return Une BufferedImage standard.
     */
    public BufferedImage versBufferedImage() {
        BufferedImage img = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, largeur, hauteur, pixels, 0, largeur);
        return img;
    }

    /**
     * Crée une ImagePIF à partir d'une BufferedImage.
     * 
     * @param img L'image source.
     * @return Une nouvelle ImagePIF.
     */
    public static ImagePIF depuisBufferedImage(BufferedImage img) {
        int l = img.getWidth();
        int h = img.getHeight();
        int[] rgb = img.getRGB(0, 0, l, h, null, 0, l);
        return new ImagePIF(l, h, rgb);
    }

    /**
     * Sauvegarde l'image dans un fichier PIF.
     * Cette méthode réalise la compression de l'image en utilisant le codage de
     * Huffman.
     * 
     * @param chemin Le chemin de sortie.
     * @throws IOException Si une erreur d'écriture survient.
     */
    public void sauvegarder(String chemin) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(chemin);
                FluxSortieBits fsb = new FluxSortieBits(fos)) {

            // Écriture de l'en-tête (dimensions de l'image)
            // On écrit la largeur et la hauteur sur 2 octets chacun.
            ecrireShort(fos, largeur);
            ecrireShort(fos, hauteur);

            // Séparation des canaux RGB
            // On sépare les composantes Rouge, Vert et Bleu pour les compresser
            // indépendamment.
            int[] r = new int[pixels.length];
            int[] v = new int[pixels.length];
            int[] b = new int[pixels.length];

            for (int i = 0; i < pixels.length; i++) {
                int c = pixels[i];
                // Extraction des octets par décalage de bits
                r[i] = (c >> 16) & 0xFF; // Décalage de 16 bits pour le rouge
                v[i] = (c >> 8) & 0xFF; // Décalage de 8 bits pour le vert
                b[i] = c & 0xFF; // Les 8 derniers bits pour le bleu
            }

            // Calcul des fréquences d'apparition de chaque niveau de couleur (0-255)
            // Cela sert à construire l'arbre de Huffman optimal pour chaque canal.
            int[] freqR = CodecHuffman.calculerFrequences(r);
            int[] freqV = CodecHuffman.calculerFrequences(v);
            int[] freqB = CodecHuffman.calculerFrequences(b);

            // Construction des arbres de Huffman et génération des codes
            // Chaque canal a son propre arbre car les statistiques de couleurs peuvent
            // varier.
            NoeudHuffman racineR = CodecHuffman.construireArbre(freqR);
            NoeudHuffman racineV = CodecHuffman.construireArbre(freqV);
            NoeudHuffman racineB = CodecHuffman.construireArbre(freqB);

            // Génération des codes binaires initiaux (par parcours de l'arbre)
            Map<Integer, String> initR = CodecHuffman.genererCodesInitiaux(racineR);
            Map<Integer, String> initV = CodecHuffman.genererCodesInitiaux(racineV);
            Map<Integer, String> initB = CodecHuffman.genererCodesInitiaux(racineB);

            // Normalisation vers des codes Canoniques
            // Les codes canoniques permettent de sauvegarder uniquement les longueurs des
            // codes
            // dans le fichier, plutôt que l'arbre entier, ce qui gagne de la place.
            int[] lenR = CodecHuffman.genererLongueursCanoniques(initR);
            int[] lenV = CodecHuffman.genererLongueursCanoniques(initV);
            int[] lenB = CodecHuffman.genererLongueursCanoniques(initB);

            // Écriture des tables de Huffman (les longueurs des codes)
            // On écrit 256 octets pour chaque canal, représentant la longueur du code pour
            // chaque valeur 0-255.
            ecrireTable(fos, lenR);
            ecrireTable(fos, lenV);
            ecrireTable(fos, lenB);

            // Génération des tables de codage finales (valeur -> code binaire)
            Map<Integer, InfoCode> mapR = CodecHuffman.genererCodesCanoniques(lenR);
            Map<Integer, InfoCode> mapV = CodecHuffman.genererCodesCanoniques(lenV);
            Map<Integer, InfoCode> mapB = CodecHuffman.genererCodesCanoniques(lenB);

            // Écriture des pixels compressés
            // Pour chaque pixel, on écrit le code binaire correspondant à ses valeurs R, V
            // et B.
            for (int i = 0; i < pixels.length; i++) {
                // Rouge
                InfoCode infoR = mapR.get(r[i]);
                fsb.ecrireChaineBinaire(infoR.codeChaine);
                // Vert
                InfoCode infoV = mapV.get(v[i]);
                fsb.ecrireChaineBinaire(infoV.codeChaine);
                // Bleu
                InfoCode infoB = mapB.get(b[i]);
                fsb.ecrireChaineBinaire(infoB.codeChaine);
            }
            // On s'assure que le dernier octet est bien écrit (bourrage avec des zéros si
            // nécessaire)
            fsb.vider();
        }
    }

    /**
     * Charge une image PIF depuis un fichier.
     * Cette méthode réalise la décompression en reconstruisant les arbres de
     * Huffman
     * à partir des tables stockées dans le fichier.
     * 
     * @param chemin Le chemin du fichier à charger.
     * @return L'objet ImagePIF contenant les pixels décompressés.
     * @throws IOException Si erreur de lecture ou fichier invalide.
     */
    public static ImagePIF charger(String chemin) throws IOException {
        try (FileInputStream fis = new FileInputStream(chemin);
                FluxEntreeBits feb = new FluxEntreeBits(fis)) {

            // Lecture de l'en-tête (dimensions)
            int l = lireShort(fis);
            int h = lireShort(fis);

            // Lecture des tables de Huffman
            // On lit 256 octets pour chaque canal (R, V, B), qui donnent la longueur du
            // code
            // pour chaque valeur de couleur (0-255).
            int[] lenR = lireTable(fis);
            int[] lenV = lireTable(fis);
            int[] lenB = lireTable(fis);

            // Reconstruction des arbres de Huffman canoniques
            // À partir des longueurs, on peut reconstruire exactement le même arbre qu'à la
            // compression.
            NoeudHuffman racineR = CodecHuffman.reconstruireArbreCanonique(lenR);
            NoeudHuffman racineV = CodecHuffman.reconstruireArbreCanonique(lenV);
            NoeudHuffman racineB = CodecHuffman.reconstruireArbreCanonique(lenB);

            // Lecture des pixels compressés
            int[] pixels = new int[l * h];
            for (int i = 0; i < pixels.length; i++) {
                // Pour chaque pixel, on décode les 3 composantes en parcourant l'arbre bit par
                // bit.
                int r = lireValeur(feb, racineR);
                int v = lireValeur(feb, racineV);
                int b = lireValeur(feb, racineB);

                // On recompose la couleur finale (ARGB)
                // 0xFF << 24 met l'opacité (Alpha) à 100% (255)
                pixels[i] = (0xFF << 24) | (r << 16) | (v << 8) | b;
            }

            return new ImagePIF(l, h, pixels);
        }
    }

    private static int lireValeur(FluxEntreeBits feb, NoeudHuffman racine) throws IOException {
        NoeudHuffman courant = racine;
        while (!courant.estFeuille()) {
            int bit = feb.lireBit();
            if (bit == -1)
                throw new EOFException("Fin de flux inattendue");
            if (bit == 0) {
                courant = courant.gauche;
            } else {
                courant = courant.droit;
            }
        }
        return courant.valeur;
    }

    private static void ecrireShort(OutputStream os, int val) throws IOException {
        os.write((val >> 8) & 0xFF);
        os.write(val & 0xFF);
    }

    private static int lireShort(InputStream is) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    private static void ecrireTable(OutputStream os, int[] longueurs) throws IOException {
        for (int l : longueurs) {
            os.write(l);
        }
    }

    private static int[] lireTable(InputStream is) throws IOException {
        int[] longueurs = new int[256];
        for (int i = 0; i < 256; i++) {
            int val = is.read();
            if (val < 0)
                throw new EOFException();
            longueurs[i] = val;
        }
        return longueurs;
    }
}
