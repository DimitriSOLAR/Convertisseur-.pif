package pif;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * La classe <code>ActionOuvrir</code> gère l'événement d'ouverture d'un fichier image.
 * Elle sert d'interface entre le clic de l'utilisateur et la logique de sélection de fichier.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public class ActionOuvrir implements ActionListener 
{
	
   	/**
   	* Référence vers la fenêtre principale pour accéder aux méthodes de gestion de fichiers.
    	*/
    	private FenetreConvertisseur fenetre;
	/**
	* Initialise l'action d'ouverture avec la fenêtre parente.
	* @param fenetre L'instance de {@link FenetreConvertisseur} associée à cette action.
	*/
	public ActionOuvrir(FenetreConvertisseur fenetre) 
	{
		this.fenetre = fenetre;
	}
	/**
	* Déclenchée lors d'une interaction utilisateur. 
	* Appelle la méthode <code>ouvrirImage()</code> de la fenêtre.
	* @param e L'événement qui a déclenché l'action.
	*/
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		fenetre.ouvrirImage();
	}
}
