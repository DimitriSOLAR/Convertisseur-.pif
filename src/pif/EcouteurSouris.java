package pif;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;

public class EcouteurSouris extends MouseAdapter {
    private PanneauImage panneau;
    private Point dernierPointSouris;

    public EcouteurSouris(PanneauImage panneau) {
        this.panneau = panneau;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dernierPointSouris = e.getPoint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (panneau.getImage() == null)
            return;

        if (dernierPointSouris == null) {
            dernierPointSouris = e.getPoint();
        }

        int dx = e.getX() - dernierPointSouris.x;
        int dy = e.getY() - dernierPointSouris.y;

        panneau.gererDeplacement(dx, dy);

        // Check if movement actually happened or was needed?
        // Logic in original was: calculate, update decalage checked bounds, repaint.
        // gererDeplacement will handle updating decalage and checking bounds.
        // It will return true if repaint needed? Or just repaint itself.
        // In Swing repaint() is lightweight, so calling it is fine.

        dernierPointSouris = e.getPoint();
    }
}
