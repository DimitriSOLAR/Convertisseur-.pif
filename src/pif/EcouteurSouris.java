package pif;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * La classe <code>EcouteurSouris</code> gère les interactions à la souris pour permettre le déplacement (panoramique) de l'image affichée.
 * Elle calcule la différence de position entre deux mouvements successifs pour mettre à jour l'affichage en temps réel.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public class EcouteurSouris extends MouseAdapter {
    
    /**
     * Référence vers le panneau affichant l'image pour lui transmettre 
     * les ordres de déplacement. 
     */
    private PanneauImage panneau;
    
    /** 
     * Stocke la dernière position connue de la souris pour calculer 
     * le vecteur de déplacement. 
     */
    private Point dernierPointSouris;

    /**
     * Construit un écouteur de souris associé à un panneau d'image.
     * @param panneau Le {@link PanneauImage} qui sera manipulé par la souris.
     */
    public EcouteurSouris(PanneauImage panneau) {
        this.panneau = panneau;
    }

    /**
     * Capturée lorsque l'utilisateur clique sur le panneau.
     * Initialise le point de départ pour le calcul du futur déplacement.
     * @param e L'événement de souris contenant les coordonnées du clic.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        dernierPointSouris = e.getPoint();
    }

    /**
     * Capturée lorsque l'utilisateur déplace la souris tout en maintenant le bouton enfoncé.
     * Calcule le décalage (delta X et delta Y) et demande au panneau de se mettre à jour.
     * @param e L'événement de souris contenant la position actuelle durant le glissement.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // on ne déplace rien s'il n'y a pas d'image
        if (panneau.getImage() == null)
            return;

        if (dernierPointSouris == null) {
            dernierPointSouris = e.getPoint();
        }

        // Calcul du vecteur de déplacement relatif
        int dx = e.getX() - dernierPointSouris.x;
        int dy = e.getY() - dernierPointSouris.y;

        // Transmission du mouvement au panneau
        panneau.gererDeplacement(dx, dy);

        // Mise à jour du point de référence pour le prochain mouvement
        dernierPointSouris = e.getPoint();
    }
}
