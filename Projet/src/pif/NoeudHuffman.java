package pif;

/**
 * @Author Dimitri SOLAR, Valentin LOISON
 * @version 1.0
 * Représente un nœud dans l'arbre de Huffman.
 * Implémente Comparable pour être utilisé dans une file de priorité.
 */
public class NoeudHuffman implements Comparable<NoeudHuffman> {
    // La fréquence de la ou des valeurs représentées par ce nœud
    public int frequence;

    // La valeur du pixel (0-255) si c'est une feuille, ou -1 sinon
    public int valeur;

    // L'enfant gauche de ce nœud
    public NoeudHuffman gauche;

    // L'enfant droit de ce nœud
    public NoeudHuffman droit;

    /**
     * Construit un nœud feuille.
     * 
     * @param valeur La valeur du pixel (0-255).
     * @param frequence La fréquence de la valeur.
     */
    public NoeudHuffman(int valeur, int frequence) {
        this.valeur = valeur;
        this.frequence = frequence;
        this.gauche = null;
        this.droit = null;
    }

    /**
     * Construit un nœud interne.
     * 
     * @param gauche L'enfant gauche.
     * @param droit L'enfant droit.
     */
    public NoeudHuffman(NoeudHuffman gauche, NoeudHuffman droit) {
        this.valeur = -1;
        this.frequence = gauche.frequence + droit.frequence;
        this.gauche = gauche;
        this.droit = droit;
    }

    /**
     * Vérifie si ce nœud est une feuille.
     * 
     * @return vrai si feuille, faux sinon.
     */
    public boolean estFeuille() {
        return gauche == null && droit == null;
    }

    /**
     * Compare deux nœuds selon leur fréquence pour la file de priorité.
     * Les nœuds avec les fréquences les plus basses sont prioritaires (traités en premier).
     * 
     * @param autre L'autre nœud à comparer.
     * @return négatif si ce nœud est plus petit, positif si plus grand.
     */
    @Override
    public int compareTo(NoeudHuffman autre) {
        return Integer.compare(this.frequence, autre.frequence);
    }
}
