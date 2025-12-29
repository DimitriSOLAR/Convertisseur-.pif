package pif;

import java.util.*;

/**
 * @author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 * Logique centrale pour le codage de Huffman, incluant la génération de codes
 * canoniques.
 */
public class CodecHuffman 
{

    	public static class InfoCode implements Comparable<InfoCode> 
	{
        	public int valeur;
        	public int longueur;
        	public String codeChaine; 
        	public int codeEntier; 
	
	        public InfoCode(int valeur, int longueur) 
		{
	            this.valeur = valeur;
	            this.longueur = longueur;
	        }
	
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
     * Calcule la fréquence de chaque valeur (0-255).
     * 
     * @param donnees Les données des pixels.
     * @return Tableau de 256 entiers.
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
     * Construit l'arbre de Huffman à partir des fréquences.
     * 
     * @param frequences Tableau de 256 fréquences.
     * @return La racine de l'arbre.
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
     * Génère les codes initiaux en parcourant l'arbre.
     * 
     * @param racine La racine de l'arbre.
     * @return Map de Valeur -> Chaîne Binaire.
     */
    public static Map<Integer, String> genererCodesInitiaux(NoeudHuffman racine) 
    {
        Map<Integer, String> codes = new HashMap<>();
        if (racine == null)
            return codes;

        // Si la racine est une feuille (une seule couleur dans l'image)
        if (racine.estFeuille()) 
	{
            codes.put(racine.valeur, "0");
            return codes;
        }

        genererCodesRecursif(racine, "", codes);
        return codes;
    }

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
     * Génère les longueurs des codes canoniques.
     * 
     * @param codesInitiaux Map de Valeur -> Code Initial (Chaîne).
     * @return Tableau de 256 longueurs d'octets (0 si absent).
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
     * Génère la map des codes canoniques.
     * Un code de Huffman canonique a la propriété que les codes d'une même longueur sont des valeurs binaires consécutives. 
     * Cela permet de reconstruire l'arbre juste avec les longueurs des codes.
     * 
     * @param longueurs Tableau de 256 longueurs.
     * @return Map de Valeur -> InfoCode (contenant le code binaire et entier).
     */
    public static Map<Integer, InfoCode> genererCodesCanoniques(int[] longueurs) 
    {
        // On crée une liste des valeurs présentes (longueur > 0)
        List<InfoCode> liste = new ArrayList<>();
        for (int i = 0; i < longueurs.length; i++) 
	{
            if (longueurs[i] > 0) 
	    {
                liste.add(new InfoCode(i, longueurs[i]));
            }
        }

        // On trie d'abord par longueur de code (court -> long),
        // Puis par valeur (0 -> 255) pour les longueurs égales.
        Collections.sort(liste);

        Map<Integer, InfoCode> map = new HashMap<>();
        long codeActuel = 0;
        int longueurActuelle = 0;

        // On assigne les codes binaires
        for (int i = 0; i < liste.size(); i++) 
	{
            InfoCode info = liste.get(i);

            if (i > 0) {
                codeActuel++;

                // Si la longueur augmente, on déplace le code vers la gauche (ajout de zéros à droite)
                int diffLongueur = info.longueur - longueurActuelle;
                codeActuel <<= diffLongueur;
            } else {
                codeActuel = 0;
            }

            longueurActuelle = info.longueur;
            info.codeEntier = (int) codeActuel;

            // Conversion en chaîne binaire
            // On écrit les bits un par un en testant chaque bit de l'entier.
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
     * Reconstruit l'arbre de Huffman à partir des longueurs canoniques.
     * Utile pour le décodage.
     * 
     * @param longueurs Tableau de 256 longueurs.
     * @return Racine de l'arbre de décodage.
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
                if (c == '0') {
                    if (courant.gauche == null) 
		    {
                        courant.gauche = new NoeudHuffman(-1, 0);
                    }
                    courant = courant.gauche;
                } else {
                    if (courant.droit == null) 
		    {
                        courant.droit = new NoeudHuffman(-1, 0);
                    }
                    courant = courant.droit;
                }
            }
            // Début d'une feuille, seule la valeur compte ici
            courant.valeur = info.valeur;
        }
        return racine;
    }
}
