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


        dernierPointSouris = e.getPoint();
    }
}
