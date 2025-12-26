package pif;

import javax.imageio.ImageIO;
import pif.CodecHuffman.InfoCode;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Fenêtre principale de l'application Convertisseur.
 */
public final class FenetreConvertisseur extends JFrame {
    private static final long serialVersionUID = 1L;
    private transient BufferedImage imageCourante;
    private JLabel etiquetteImage;
    private JTabbedPane ongletsStats;
    private String cheminSortieDefaut;

    public FenetreConvertisseur(String cheminEntree, String cheminSortie) {
        setTitle("Convertisseur PIF");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        this.cheminSortieDefaut = cheminSortie;

        // Mise en page
        setLayout(new BorderLayout());

        // Haut : Image
        etiquetteImage = new JLabel("Aucune image chargée", SwingConstants.CENTER);
        etiquetteImage.setPreferredSize(new Dimension(300, 300));
        add(new JScrollPane(etiquetteImage), BorderLayout.NORTH);

        // Centre : Statistiques
        ongletsStats = new JTabbedPane();
        add(ongletsStats, BorderLayout.CENTER);

        // Bas : Contrôles
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

    void ouvrirImage() {
        JFileChooser selecteur = new JFileChooser();
        if (selecteur.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chargerImage(selecteur.getSelectedFile().getAbsolutePath());
        }
    }

    private void chargerImage(String chemin) {
        try {
            BufferedImage img = ImageIO.read(new File(chemin));
            if (img == null) {
                JOptionPane.showMessageDialog(this, "Impossible de charger l'image (format non supporté ?)");
                return;
            }
            this.imageCourante = img;

            // Afficher l'image redimensionnée
            ImageIcon icone = new ImageIcon(getImageRedimensionnee(img, 300, 300));
            etiquetteImage.setIcon(icone);
            etiquetteImage.setText("");
            calculerEtAfficherStats();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Image getImageRedimensionnee(BufferedImage src, int l, int h) {
        int lOriginale = src.getWidth();
        int hOriginale = src.getHeight();
        double ratio = Math.min((double) l / lOriginale, (double) h / hOriginale);
        int nouvL = (int) (lOriginale * ratio);
        int nouvH = (int) (hOriginale * ratio);
        return src.getScaledInstance(nouvL, nouvH, Image.SCALE_SMOOTH);
    }

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

    private void ajouterStatsCanal(String nom, int[] donnees) {
        int[] frequences = CodecHuffman.calculerFrequences(donnees);
        NoeudHuffman racine = CodecHuffman.construireArbre(frequences);
        Map<Integer, String> codesInitiaux = CodecHuffman.genererCodesInitiaux(racine);
        int[] longueurs = CodecHuffman.genererLongueursCanoniques(codesInitiaux);
        Map<Integer, InfoCode> canoniques = CodecHuffman.genererCodesCanoniques(longueurs);

        String[] colonnes = { "Valeur", "Fréquence", "Code Initial", "Code Canonique" };

        // On ne garde que les lignes où la fréquence > 0 pour un tableau plus propre
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

        // Utilisation directe du tableau sans DefaultTableModel
        JTable table = new JTable(donneesLignes, colonnes);
        ongletsStats.addTab(nom, new JScrollPane(table));
    }

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
            JOptionPane.showMessageDialog(this, "Succès ! Sauvegardé sous " + chemin);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde PIF : " + e.getMessage());
            e.printStackTrace();
        }
    }
}