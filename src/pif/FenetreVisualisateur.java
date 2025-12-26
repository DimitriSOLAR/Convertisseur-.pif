package pif;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * La fenêtre principale de l'application Visualisateur.
 */
public final class FenetreVisualisateur extends JFrame {
    private static final long serialVersionUID = 1L;
    private transient BufferedImage image;
    private PanneauImage panneauImage;

    public FenetreVisualisateur(String cheminInitial) {
        setTitle("Visualisateur PIF");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        panneauImage = new PanneauImage();
        add(panneauImage, BorderLayout.CENTER);

        if (cheminInitial != null) {
            chargerFichier(cheminInitial);
        } else {
            choisirFichier();
        }
    }

    private void choisirFichier() {
        JFileChooser selecteur = new JFileChooser();
        selecteur.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images PIF", "pif"));
        if (selecteur.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chargerFichier(selecteur.getSelectedFile().getAbsolutePath());
        }
    }

    private void chargerFichier(String chemin) {
        try {
            ImagePIF pif = ImagePIF.charger(chemin);
            this.image = pif.versBufferedImage();
            panneauImage.setImage(image);

            // Adapter la taille de la fenêtre
            Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
            int l = Math.min(pif.getLargeur() + 50, tailleEcran.width - 100);
            int h = Math.min(pif.getHauteur() + 50, tailleEcran.height - 100);
            setSize(l, h);
            setLocationRelativeTo(null);

            setTitle("Visualisateur PIF - " + new File(chemin).getName());
            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement du fichier : " + e.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
