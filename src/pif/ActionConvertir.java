package pif;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * La classe <code>ActionConvertir</code> est un écouteur (Listener) qui déclenche
 * le processus de conversion d'image lorsqu'une action est détectée.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public class ActionConvertir implements ActionListener {
    
    /**
     * Référence vers la fenêtre principale de l'application.
     */
    private FenetreConvertisseur fenetre;

    /**
     * Construit une nouvelle instance de l'action de conversion.
     * @param fenetre La fenêtre du convertisseur contenant la logique de conversion.
     */
    public ActionConvertir(FenetreConvertisseur fenetre) {
        this.fenetre = fenetre;
    }

    /**
     * Invoquée lorsqu'une action se produit 
     * Appelle la méthode <code>convertirImage()</code> de la fenêtre associée.
     * @param e L'événement d'action généré par le composant.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        fenetre.convertirImage();
    }
}
