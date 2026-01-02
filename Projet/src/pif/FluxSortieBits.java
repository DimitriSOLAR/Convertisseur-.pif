package pif;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @Author Dimitri SOLAR, Valentin LOISON
 * @Version 1.0
 * Écrit des bits dans un flux de sortie sous-jacent (OutputStream).
 * Tamponne les bits jusqu'à ce qu'un octet complet puisse être écrit.
 */
public class FluxSortieBits implements AutoCloseable {
    private final OutputStream sortie;
    private int tampon;
    private int bitsDansTampon;

    /**
     * Crée un nouveau FluxSortieBits.
     * 
     * @param sortie Le flux de sortie sous-jacent.
     */
    public FluxSortieBits(OutputStream sortie) {
        this.sortie = sortie;
        this.tampon = 0;
        this.bitsDansTampon = 0;
    }

    /**
     * Écrit une séquence de bits fournie sous forme d'entier.
     * 
     * @param valeur  La valeur contenant les bits à écrire.
     * @param nbrBits Le nombre de bits à écrire depuis la valeur.
     * @throws IOException En cas d'erreur d'E/S.
     */
    public void ecrireBits(int valeur, int nbrBits) throws IOException {
        for (int i = nbrBits - 1; i >= 0; i--) {
            int bit = (valeur >> i) & 1;
            ecrireBit(bit);
        }
    }

    /**
     * Écrit les bits représentant un code canonique (chaîne binaire).
     * 
     * @param chaineBinaire Une chaîne de '0' et '1'.
     * @throws IOException En cas d'erreur d'E/S.
     */
    public void ecrireChaineBinaire(String chaineBinaire) throws IOException {
        for (char c : chaineBinaire.toCharArray()) {
            ecrireBit(c == '1' ? 1 : 0);
        }
    }

    /**
     * Écrit un seul bit.
     * 
     * @param bit 0 ou 1.
     * @throws IOException En cas d'erreur d'E/S.
     */

    public void ecrireBit(int bit) throws IOException {
        // On décale le tampon vers la gauche pour faire de la place pour le nouveau bit
        // Et on ajoute le bit à droite (avec un OU binaire)
        tampon = (tampon << 1) | (bit & 1);
        bitsDansTampon++;

        // Si le tampon est plein (8 bits = 1 octet), on l'écrit dans le flux
        if (bitsDansTampon == 8) {
            viderTampon();
        }
    }

    private void viderTampon() throws IOException {
        if (bitsDansTampon > 0) {
            sortie.write(tampon);
            tampon = 0;
            bitsDansTampon = 0;
        }
    }

    /**
     * Vide les bits restants dans le flux, en complétant avec des zéros si nécessaire.
     */
    public void vider() throws IOException {
        if (bitsDansTampon > 0) {
            while (bitsDansTampon < 8) {
                tampon = tampon << 1;
                bitsDansTampon++;
            }
            sortie.write(tampon);
            tampon = 0;
            bitsDansTampon = 0;
        }
        sortie.flush();
    }

    @Override
    public void close() throws IOException {
        vider();
        sortie.close();
    }
}
