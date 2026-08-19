package util;

// =================================================================================
// IMPORT DELLE CLASSI PER LA CRITTOGRAFIA E SICUREZZA
// =================================================================================
import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * =================================================================================
 * CLASSE DI UTILITA' - CRITTOGRAFIA PASSWORD (PasswordUtil)
 * =================================================================================
 * Questa classe si occupa ESCLUSIVAMENTE di prendere una password scritta in chiaro
 * e trasformarla in un "Hash" incomprensibile prima che venga salvata nel database,
 * rispettando i requisiti di sicurezza delle slide del corso.
 * 
 * IMPLEMENTAZIONE BCRYPT:
 * Usa l'algoritmo BCrypt (tramite Spring Security Core) come da specifiche.
 * BCrypt genera un "salt" casuale per ogni password, rendendola molto più
 * sicura rispetto ai vecchi algoritmi come SHA-256 o MD5.
 */
public class PasswordUtil {

    /**
     * Cripta la password in chiaro generando un hash sicuro con BCrypt.
     * Viene usato un cost di 12 (come da esempio slide).
     * 
     * @param password La password in chiaro inserita dall'utente.
     * @return L'hash BCrypt da salvare nel database.
     */
    public static String hashPassword(String password) {
        // Genera la password criptata con un "salt" di 12 round (raccomandato)
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    /**
     * Verifica se una password in chiaro corrisponde a un hash salvato.
     * Dato che BCrypt usa un salt casuale, l'unico modo per verificare
     * la password è usare questo metodo apposito.
     * 
     * @param passwordInChiaro La password tentata dall'utente al login
     * @param hashSalvato L'hash criptato che abbiamo estratto dal database
     * @return true se le password coincidono, false altrimenti
     */
    public static boolean checkPassword(String passwordInChiaro, String hashSalvato) {
        try {
            return BCrypt.checkpw(passwordInChiaro, hashSalvato);
        } catch (Exception e) {
            // Se l'hash salvato nel DB è vecchio o malformato (es. SHA-256), fallisce senza crashare
            return false;
        }
    }
}
