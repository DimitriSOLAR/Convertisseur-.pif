package pif;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionConvertir implements ActionListener {
    private FenetreConvertisseur fenetre;

    public ActionConvertir(FenetreConvertisseur fenetre) {
        this.fenetre = fenetre;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fenetre.convertirImage();
    }
}
