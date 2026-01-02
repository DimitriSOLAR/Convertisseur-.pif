package pif;

import java.util.*;

/**
 * Logique centrale pour le codage de Huffman, incluant la génération de codes canoniques.
 * Cette classe fournit des outils pour calculer les fréquences, construire l'arbre de Huffman et transformer les codes standards en codes canoniques pour optimiser le stockage de l'en-tête.
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 */
public class CodecHuffman
{
	/**
	* Structure de données interne regroupant toutes les informations relatives
	* au code d'un symbole (valeur, longueur et représentations binaires).
	*/
    	public static class InfoCode implements Comparable<InfoCode>
    	{
        	// La valeur du symbole 
        	public int valeur;
        	// La longueur du code de Huffman en bits
        	public int longueur;
        	// Représentation du code sous forme de chaîne de caractères
        	public String codeChaine;
        	// Représentation numérique du code
        	public int codeEntier;
	
	        /**
	         * Construit une structure d'information de code.
	         * @param valeur La valeur du symbole.
	         * @param longueur La longueur du code associé.
	         */
        	public InfoCode(int valeur, int longueur)
        	{
        		this.valeur = valeur;
        	    	this.longueur = longueur;
        	}
        	/**
        	* Compare deux codes pour le tri canonique.
        	* Le tri s'effectue d'abord par longueur, puis par valeur de symbole.
        	* @param autre L'autre objet {@link InfoCode} à comparer.
        	* @return Un entier négatif, zéro ou positif selon l'ordre.
        	*/
        	@Override
        	public int compareTo(InfoCode autre)
        	{
            	if (this.longueur != autre.longueur)
            	{
                	return Integer.compare(this.longueur, autre.longueur);
            	}
            	return Integer.compare(this.valeur, autre.valeur);
        	}
    	}
    	/**
    	* Calcule la fréquence d'apparition de chaque valeur d'octet (0-255).
    	*
    	* @param donnees Tableau contenant les valeurs des pixels de l'image.
     	* @return Un tableau de 256 entiers où l'indice correspond à la valeur du pixel.
     	*/	
    	public static int[] calculerFrequences(int[] donnees)
    	{
        	int[] frequences = new int[256];
        	for (int val : donnees)
        	{
        		if (val >= 0 && val < 256)
            		{
                		frequences[val]++;
            		}
        	}
        	return frequences;
    	}
    	/**
     	* Construit l'arbre de Huffman à l'aide d'une file de priorité.
     	*
     	* @param frequences Tableau des fréquences de chaque symbole.
     	* @return La racine du {@link NoeudHuffman} ou <code>null</code> si aucune donnée.
     	*/
    	public static NoeudHuffman construireArbre(int[] frequences)
    	{
        	PriorityQueue<NoeudHuffman> filePriorite = new PriorityQueue<>();
        	for (int i = 0; i < frequences.length; i++)
        	{
            		if (frequences[i] > 0)
            		{
                		filePriorite.add(new NoeudHuffman(i, frequences[i]));
            		}
        	}
        	if (filePriorite.isEmpty())
        	{
        		return null;
        	}
        	while (filePriorite.size() > 1)
        	{
            		NoeudHuffman gauche = filePriorite.poll();
            		NoeudHuffman droit = filePriorite.poll();
            		NoeudHuffman parent = new NoeudHuffman(gauche, droit);
            		filePriorite.add(parent);
        	}
        	return filePriorite.poll();
    	}
    	/**
    	* Génère les codes de Huffman initiaux par un parcours récursif de l'arbre.
    	*
    	* @param racine La racine de l'arbre de Huffman.
    	* @return Une <code>Map</code> associant chaque valeur à son code binaire (String).
    	*/
    	public static Map<Integer, String> genererCodesInitiaux(NoeudHuffman racine)
    	{
        	Map<Integer, String> codes = new HashMap<>();
       	 	if (racine == null)
            	return codes;
        	if (racine.estFeuille())
        	{
        		codes.put(racine.valeur, "0");
        	    	return codes;
        	}
        	genererCodesRecursif(racine, "", codes);
        	return codes;
    	}
    	/**
    	* Méthode récursive pour explorer l'arbre et construire les chaînes binaires.
     	* @param noeud Le noeud courant.
     	* @param codeActuel Le préfixe binaire accumulé.
     	* @param codes La map de stockage des résultats.
     	*/
    	private static void genererCodesRecursif(NoeudHuffman noeud, String codeActuel, Map<Integer, String> codes)
    	{
        	if (noeud.estFeuille())
        	{
        		codes.put(noeud.valeur, codeActuel);
            		return;
        	}
       		if (noeud.gauche != null)
        	{
            		genererCodesRecursif(noeud.gauche, codeActuel + "0", codes);
        	}
        	if (noeud.droit != null)
        	{
            		genererCodesRecursif(noeud.droit, codeActuel + "1", codes);
        	}
    	}
    	/**
    	 * Extrait uniquement les longueurs de bits de chaque code.
    	 *
   	 * @param codesInitiaux La map issue de {@link #genererCodesInitiaux}.
   	 * @return Un tableau de 256 entiers contenant les longueurs des codes.
   	 */
    	public static int[] genererLongueursCanoniques(Map<Integer, String> codesInitiaux)
    	{
        	int[] longueurs = new int[256];
        	for (Map.Entry<Integer, String> entree : codesInitiaux.entrySet())
        	{
	    		longueurs[entree.getKey()] = entree.getValue().length();
        	}
        	return longueurs;
    	}
    	/**
    	* Génère les codes Huffman canoniques à partir des longueurs.
     	*
     	* Un code canonique garantit que les symboles de même longueur ont des codes  binaires consécutifs, facilitant la reconstruction de l'arbre.
     	*	
     	* @param longueurs Tableau des longueurs de codes pour chaque symbole.
     	* @return Une map associant la valeur du symbole à son objet {@link InfoCode} complet.
     	*/
   	public static Map<Integer, InfoCode> genererCodesCanoniques(int[] longueurs)
    	{
        	List<InfoCode> liste = new ArrayList<>();
        	for (int i = 0; i < longueurs.length; i++)
        	{
       			if (longueurs[i] > 0)
        		{
                		liste.add(new InfoCode(i, longueurs[i]));
            		}
        	}
        	Collections.sort(liste);
        	Map<Integer, InfoCode> map = new HashMap<>();
        	long codeActuel = 0;
        	int longueurActuelle = 0;
	        for (int i = 0; i < liste.size(); i++)
	        {
         		InfoCode info = liste.get(i);
    			if (i > 0) 
			{
                		codeActuel++;
                		int diffLongueur = info.longueur - longueurActuelle;
                		codeActuel <<= diffLongueur;
            		} else 
			{
                		codeActuel = 0;
            		}
            		longueurActuelle = info.longueur;
            		info.codeEntier = (int) codeActuel;
            		StringBuilder sb = new StringBuilder();
            		for (int b = info.longueur - 1; b >= 0; b--)
            		{
                		sb.append(((codeActuel >> b) & 1) == 1 ? '1' : '0');
            		}
            		info.codeChaine = sb.toString();
            		map.put(info.valeur, info);
        	}
        	return map;
    	}
    	/**
    	* Reconstruit un arbre de Huffman fonctionnel à partir des seules longueurs canoniques.
     	* Cette méthode est essentielle pour le décodage d'un fichier compressé.
     	*
     	* @param longueurs Tableau des longueurs de codes.
     	* @return La racine de l'arbre de Huffman reconstruit.
     	*/
    	public static NoeudHuffman reconstruireArbreCanonique(int[] longueurs)
    	{
        	Map<Integer, InfoCode> codes = genererCodesCanoniques(longueurs);
        	NoeudHuffman racine = new NoeudHuffman(-1, 0);
        	for (InfoCode info : codes.values())
        	{
        		NoeudHuffman courant = racine;
            		for (int i = 0; i < info.codeChaine.length(); i++)
            		{
                		char c = info.codeChaine.charAt(i);
                		if (c == '0') 
				{
                    			if (courant.gauche == null)
                        		courant.gauche = new NoeudHuffman(-1, 0);
                    			courant = courant.gauche;
                		} 
				else
			       	{
                    			if (courant.droit == null)
                        		courant.droit = new NoeudHuffman(-1, 0);
                    			courant = courant.droit;
                		}
            		}
            		courant.valeur = info.valeur;
        	}
        	return racine;
    	}
}
