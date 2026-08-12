package model;

/**
 * =================================================================================
 * MODEL - SESSIONE UTENTE (Singleton)
 * =================================================================================
 * Mantiene in memoria l'utente attualmente loggato per tutta la durata della sessione.
 * Funziona come un "ponte" tra le schermate: il controller di Login/Registrazione
 * imposta l'utente qui, e tutti gli altri controller lo leggono da qui.
 *
 * STRUTTURA DB-READY:
 * Quando verrà integrato un database, sarà sufficiente:
 * 1. Aggiungere le chiamate al DB nel controller Login e Registrazione
 * 2. Il resto dell'applicazione legge già da questa sessione — nessun altro cambiamento
 *
 * UTILIZZO:
 *   // Impostare l'utente dopo login/registrazione:
 *   SessioneUtente.getInstance().setUtente(utente);
 *
 *   // Leggere i dati in qualsiasi controller:
 *   Utente u = SessioneUtente.getInstance().getUtente();
 *
 *   // Effettuare il logout:
 *   SessioneUtente.getInstance().logout();
 */
public class SessioneUtente {

    // =========================================================================
    // SINGLETON — istanza unica globale
    // =========================================================================
    private static final SessioneUtente INSTANCE = new SessioneUtente();

    /** Restituisce l'unica istanza globale della sessione. */
    public static SessioneUtente getInstance() {
        return INSTANCE;
    }

    // Costruttore privato: impedisce l'istanziazione diretta dall'esterno
    private SessioneUtente() {}

    // =========================================================================
    // STATO DELLA SESSIONE
    // =========================================================================

    /** L'utente attualmente loggato. null se nessun utente è attivo. */
    private Utente utente;

    // =========================================================================
    // ACCESSO ALLA SESSIONE
    // =========================================================================

    /**
     * Restituisce l'utente attualmente loggato.
     * @return L'oggetto {@link Utente} attivo, oppure {@code null} se non c'è nessuna sessione.
     */
    public Utente getUtente() {
        return utente;
    }

    /**
     * Imposta l'utente loggato nella sessione corrente.
     * Chiamare questo metodo subito dopo un login o una registrazione avvenuta con successo.
     *
     * // TODO DB: Qui in futuro si potrà aggiungere anche la chiamata al DB
     *             per caricare dati aggiuntivi (es. statistiche, patenti, ecc.)
     *
     * @param utente L'oggetto {@link Utente} da impostare come utente attivo.
     */
    public void setUtente(Utente utente) {
        this.utente = utente;
        System.out.println("[Sessione] Utente impostato: " + utente);
    }

    /**
     * Effettua il logout resettando la sessione corrente.
     * Dopo questa chiamata, {@link #getUtente()} restituirà {@code null}.
     */
    public void logout() {
        System.out.println("[Sessione] Logout effettuato per: " + (utente != null ? utente.getNomeCompleto() : "—"));
        this.utente = null;
    }

    /**
     * Controlla se c'è un utente attivo nella sessione.
     * @return {@code true} se un utente è loggato, {@code false} altrimenti.
     */
    public boolean isLoggato() {
        return utente != null;
    }
}
