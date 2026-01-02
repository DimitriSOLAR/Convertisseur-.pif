package pif;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Composant graphique personnalisé permettant d'afficher et de manipuler une image.
 * Ce panneau gère deux modes d'affichage :
 * <li><b>Centrage automatique :</b> Si l'image est plus petite que le panneau.</li>
 * <li><b>Mode panoramique :</b> Si l'image dépasse les dimensions du panneau, 
 * l'utilisateur peut la faire glisser via l'{@link EcouteurSouris}.</li>
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public final class PanneauImage extends JPanel {
    
    // Identifiant de sérialisation
    private static final long serialVersionUID = 1L;
    
    // L'image source à dessiner dans le composant
    private transient BufferedImage img;

    // Décalage horizontal actuel de l'image (utilisé pour le panoramique)
    private int decalageX = 0;
    
    // Décalage vertical actuel de l'image (utilisé pour le panoramique)
    private int decalageY = 0;

    /**
     * Initialise le panneau et enregistre les écouteurs de souris pour 
     * permettre l'interaction avec l'image.
     */
    public PanneauImage() {
        EcouteurSouris es = new EcouteurSouris(this);
        addMouseListener(es);
        addMouseMotionListener(es);
    }

    /**
     * Définit l'image à afficher et réinitialise les décalages à zéro.
     * @param img La nouvelle {@link BufferedImage} à afficher.
     */
    public void setImage(BufferedImage img) {
        this.img = img;
        this.decalageX = 0;
        this.decalageY = 0;
        repaint();
    }

    // return L'image actuellement chargée
    public BufferedImage getImage() {
        return img;
    }

    /**
     * Met à jour les coordonnées de l'image en fonction d'un déplacement relatif.
     * La méthode applique des contraintes de bordures pour empêcher l'utilisateur 
     * de faire sortir l'image des limites visibles du panneau si elle est plus grande.
     * @param dx Déplacement horizontal en pixels (delta X).
     * @param dy Déplacement vertical en pixels (delta Y).
     */
    void gererDeplacement(int dx, int dy) {
        if (img == null)
            return;

        boolean peutBougerX = img.getWidth() > getWidth();
        boolean peutBougerY = img.getHeight() > getHeight();

        if (peutBougerX) {
            decalageX += dx;
            int minX = getWidth() - img.getWidth();
            if (decalageX > 0)
                decalageX = 0;
            if (decalageX < minX)
                decalageX = minX;
        }

        if (peutBougerY) {
            decalageY += dy;
            int minY = getHeight() - img.getHeight();
            if (decalageY > 0)
                decalageY = 0;
            if (decalageY < minY)
                decalageY = minY;
        }

        if (peutBougerX || peutBougerY) {
            repaint();
        }
    }

    /**
     * Assure le rendu graphique du composant.
     * Calcule la position (x, y) de l'image : centrée si l'image tient dans le panneau,
     * ou décalée selon les variables de panoramique si elle est plus grande.
     * @param g L'instance de {@link Graphics} utilisée pour le dessin.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img == null)
            return;

        int l = getWidth();
        int h = getHeight();
        int imgL = img.getWidth();
        int imgH = img.getHeight();

        int x, y;

        // Logique de positionnement horizontal
        if (imgL <= l) {
	    // Centrage
            x = (l - imgL) / 2; 
        } else {
	    // Utilisation du panoramique
            x = decalageX; 
        }

        // Logique de positionnement vertical
        if (imgH <= h) {
	    // Centrage
            y = (h - imgH) / 2; 
        } else {
	    // Utilisation du panoramique
            y = decalageY; 
        }

        g.drawImage(img, x, y, null);
    }
}
