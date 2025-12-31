package pif;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Fenêtre principale de l'application Visualisateur pour le format d'image PIF.
 * Cette classe assure le chargement des fichiers compressés, leur conversion en {@link BufferedImage} et la gestion de l'interface utilisateur graphique.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public final class FenetreVisualisateur extends JFrame {
    
    // Identifiant de version pour la sérialisation
    private static final long serialVersionUID = 1L;
    
    // L'image actuellement décodée et affichée dans la fenêtre
    private transient BufferedImage image;
    
    // Le panneau personnalisé dédié au rendu graphique de l'image
    private PanneauImage panneauImage;

    /**
     * Construit une nouvelle fenêtre de visualisation.
     * Si un chemin est fourni, l'image est chargée immédiatement. Sinon, une boîte de dialogue de sélection de fichier s'ouvre.
     * @param cheminInitial Le chemin absolu du fichier .pif à ouvrir, ou <code>null</code>.
     */
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

    /**
     * Affiche une boîte de dialogue permettant de choisir un fichier sur le disque.
     * Un filtre est appliqué pour ne proposer que les extensions ".pif".
     */
    private void choisirFichier() {
        JFileChooser selecteur = new JFileChooser();
        selecteur.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images PIF", "pif"));
        if (selecteur.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            chargerFichier(selecteur.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Charge un fichier PIF, le décompresse et adapte l'interface graphique.
     * La fenêtre est automatiquement redimensionnée en fonction des dimensions de l'image chargée, tout en veillant à ne pas dépasser la taille de l'écran.
     * @param chemin Le chemin complet du fichier à charger.
     */
    private void chargerFichier(String chemin) {
        try {
            // Chargement des données compressées
            ImagePIF pif = ImagePIF.charger(chemin);
            this.image = pif.versBufferedImage();
            
            // Mise à jour du composant d'affichage
            panneauImage.setImage(image);

            // Calcul de l'adaptation de la taille de la fenêtre à l'écran
            Dimension tailleEcran = Toolkit.getDefaultToolkit().getScreenSize();
            int l = Math.min(pif.getLargeur() + 50, tailleEcran.width - 100);
            int h = Math.min(pif.getHauteur() + 50, tailleEcran.height - 100);
            
            setSize(l, h);
	    // Recentre la fenêtre après redimensionnement
            setLocationRelativeTo(null); 

            setTitle("Visualisateur PIF - " + new File(chemin).getName());
            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement du fichier : " + e.getMessage(), 
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
