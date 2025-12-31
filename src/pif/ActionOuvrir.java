package pif;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 * */

public class ActionOuvrir implements ActionListener {
    private FenetreConvertisseur fenetre;

    public ActionOuvrir(FenetreConvertisseur fenetre) {
        this.fenetre = fenetre;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fenetre.ouvrirImage();
    }
}
