package pif;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author Dimitri SOLAR, Valentin LOISON
 * @Version 1.0
 * Lit des bits depuis un flux d'entrée sous-jacent (InputStream).
 */
public class FluxEntreeBits implements AutoCloseable {
    private final InputStream entree;
    private int tampon;
    private int bitsRestants; 

    /**
     * Crée un nouveau FluxEntreeBits.
     * 
     * @param entree Le flux d'entrée sous-jacent.
     */
    public FluxEntreeBits(InputStream entree) {
        this.entree = entree;
        this.tampon = 0;
        this.bitsRestants = 0;
    }

    /**
     * Lit un seul bit.
     * 
     * @return 0 ou 1, ou -1 si fin de flux.
     * @throws IOException En cas d'erreur d'E/S.
     */

    public int lireBit() throws IOException {
        // On n'a plus de bits en réserve dans le tampon ?
        if (bitsRestants == 0)
       	{
	    // On lit le prochain octet (8 bits)
            tampon = entree.read(); 
            if (tampon == -1) {
                return -1; 
            }
	    // On a refait le plein de 8 bits
            bitsRestants = 8; 
        }

        // Lecture du bit le plus significatif au moins significatif 
        int bit = (tampon >> (bitsRestants - 1)) & 1;
        bitsRestants--;
        return bit;
    }

    @Override
    public void close() throws IOException {
        entree.close();
    }
}
