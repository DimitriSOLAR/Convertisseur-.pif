package pif;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
