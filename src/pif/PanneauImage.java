package pif;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * @Author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 * Panneau pour l'affichage de l'image.
 */
public final class PanneauImage extends JPanel {
    private static final long serialVersionUID = 1L;
    private transient BufferedImage img;

    // Variables pour gérer le déplacement (pan) de l'image
    private int decalageX = 0;
    private int decalageY = 0;

    public PanneauImage() {
        EcouteurSouris es = new EcouteurSouris(this);
        addMouseListener(es);
        addMouseMotionListener(es);
    }

    public void setImage(BufferedImage img) {
        this.img = img;
        this.decalageX = 0;
        this.decalageY = 0;
        repaint();
    }

    public BufferedImage getImage() {
        return img;
    }

    /**
     * Gère le déplacement de l'image (pan).
     * 
     * @param dx Déplacement en X depuis la dernière position.
     * @param dy Déplacement en Y depuis la dernière position.
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

        if (imgL <= l) {
            x = (l - imgL) / 2;
        } else {
            x = decalageX;
        }

        if (imgH <= h) {
            y = (h - imgH) / 2;
        } else {
            y = decalageY;
        }

        g.drawImage(img, x, y, null);
    }
}
