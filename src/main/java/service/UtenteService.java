package service;

// =================================================================================
// IMPORT DELLE CLASSI DEL DAO E DEL MODELLO
// =================================================================================
import dao.UtenteDAO;
import model.Utente;

/**
 * =================================================================================
 * SERVICE LAYER - UTENTE (UtenteService)
 * =================================================================================
 * Questa classe rappresenta lo strato di Business Logic (Logica di Business)
 * dell'applicazione per la gestione degli utenti.
 * 
 * ARCHITETTURA A LIVELLI (Layered Architecture):
 * 1. Controller: Gestisce l'interfaccia grafica (FXML) e gli eventi utente.
 * 2. Service (QUESTA CLASSE): Contiene le regole di validazione, sicurezza e business logic.
 * 3. DAO: Esegue le query SQL vere e proprie verso il database SQLite.
 * 
 * VANTAGGI DEL SERVICE LAYER:
 * - Disaccoppiamento: I controller non parlano direttamente con il DB.
 * - Riutilizzabilità: I controlli di validazione (email, password) sono centralizzati in un unico posto.
 * - Manutenibilità e testabilità: La logica applicativa è isolata e indipendente dall'UI.
 */
public class UtenteService {

    // =============================================================================
    // COSTANTI DI VALIDAZIONE
    // =============================================================================
    
    /**
     * Espressione regolare (Regex) conforme allo standard RFC 5322 per la validazione delle email.
     * Verifica la presenza di un prefisso valido, del carattere '@', del dominio e del TLD (es. .it, .com).
     */
    public static final String REGEX_CODICE_FISCALE = "^[A-Za-z]{6}[0-9]{2}[A-Za-z]{1}[0-9]{2}[A-Za-z]{1}[0-9]{3}[A-Za-z]{1}$";
    public static final String REGEX_EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    // Istanza del Data Access Object
    private final UtenteDAO utenteDAO;

    /**
     * Costruttore standard: inizializza il DAO per l'accesso ai dati.
     */
    public UtenteService() {
        this.utenteDAO = new UtenteDAO();
    }

    /**
     * Costruttore con iniezione delle dipendenze (utile per testing/mock).
     * 
     * @param utenteDAO Istanza del DAO da utilizzare.
     */
    public UtenteService(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    // =============================================================================
    // 1. REGISTRAZIONE UTENTE
    // =============================================================================

    /**
     * Esegue la registrazione di un nuovo utente applicando tutte le regole di business.
     * 
     * @param utente L'oggetto Utente contenente i dati anagrafici.
     * @param password La password in chiaro scelta dall'utente.
     * @param confermaPassword La conferma della password inserita nel form.
     * @return Stringa di errore se la validazione fallisce, oppure null se la registrazione ha successo.
     */
    public String registra(Utente utente, String password, String confermaPassword) {
        // Controllo campi obbligatori
        if (utente == null || 
            utente.getNome() == null || utente.getNome().isBlank() ||
            utente.getCognome() == null || utente.getCognome().isBlank() ||
            utente.getEmail() == null || utente.getEmail().isBlank() ||
            utente.getCodiceFiscale() == null || utente.getCodiceFiscale().isBlank() ||
            password == null || password.isBlank() ||
            confermaPassword == null || confermaPassword.isBlank()) {
            return "Tutti i campi sono obbligatori! Per favore compila ogni sezione del modulo.";
        }

        // Controllo lunghezza Codice Fiscale
        if (utente.getCodiceFiscale().length() != 16) {
            return "Il Codice Fiscale deve contenere esattamente 16 caratteri alfanumerici.";
        }

        // Controllo formato Email con Regex
        if (!validaEmail(utente.getEmail())) {
            return "L'indirizzo email inserito non è valido (es. nome@dominio.it).";
        }

        // Controllo corrispondenza password e conferma
        if (!password.equals(confermaPassword)) {
            return "La password e la conferma password non coincidono.";
        }

        // Controllo robustezza password
        String errorePassword = validaPassword(password);
        if (errorePassword != null) {
            return errorePassword;
        }

        // Delega al DAO per il salvataggio persistente nel DB
        boolean successo = utenteDAO.registraUtente(utente, password);
        if (!successo) {
            return "Impossibile registrare l'utente. Il Codice Fiscale potrebbe essere già presente nel sistema.";
        }

        return null; // Operazione completata con successo
    }

    // =============================================================================
    // 2. AUTENTICAZIONE / LOGIN
    // =============================================================================

    /**
     * Autentica un utente verificando il Codice Fiscale e la password inseriti.
     * 
     * @param codiceFiscale Il Codice Fiscale inserito come credenziale di accesso.
     * @param password La password in chiaro inserita nel form di login.
     * @return L'oggetto {@link Utente} se le credenziali sono corrette, oppure null se errate o inesistenti.
     */
    public Utente login(String codiceFiscale, String password) {
        if (codiceFiscale == null || codiceFiscale.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        return utenteDAO.login(codiceFiscale.trim().toUpperCase(), password);
    }

    // =============================================================================
    // 3. GESTIONE PASSWORD
    // =============================================================================

    /**
     * Modifica la password di un utente esistente previa validazione.
     * 
     * @param codiceFiscale Il Codice Fiscale dell'utente.
     * @param nuovaPassword La nuova password desiderata.
     * @param confermaPassword La conferma della nuova password.
     * @return Stringa di errore se la validazione fallisce, oppure null se l'aggiornamento ha successo.
     */
    public String cambiaPassword(String codiceFiscale, String nuovaPassword, String confermaPassword) {
        if (codiceFiscale == null || codiceFiscale.isBlank()) {
            return "Sessione non valida.";
        }

        if (nuovaPassword == null || nuovaPassword.isBlank() || confermaPassword == null || confermaPassword.isBlank()) {
            return "Compila entrambi i campi della password.";
        }

        if (!nuovaPassword.equals(confermaPassword)) {
            return "La nuova password e la conferma non corrispondono.";
        }

        String errorePassword = validaPassword(nuovaPassword);
        if (errorePassword != null) {
            return errorePassword;
        }

        boolean successo = utenteDAO.cambiaPassword(codiceFiscale, nuovaPassword);
        if (!successo) {
            return "Impossibile aggiornare la password nel database.";
        }

        return null;
    }

    // =============================================================================
    // 4. ELIMINAZIONE ACCOUNT
    // =============================================================================

    /**
     * Elimina definitivamente l'account di un utente dal database.
     * 
     * @param codiceFiscale Il Codice Fiscale dell'utente da rimuovere.
     * @return true se l'eliminazione è andata a buon fine, false altrimenti.
     */
    public boolean eliminaAccount(String codiceFiscale) {
        if (codiceFiscale == null || codiceFiscale.isBlank()) {
            return false;
        }
        return utenteDAO.eliminaUtente(codiceFiscale);
    }

    // =============================================================================
    // 5. METODI DI VALIDAZIONE CONDIVISI (HELPER)
    // =============================================================================

    /**
     * Verifica la validità formale di un indirizzo email tramite espressione regolare.
     * 
     * @param email La stringa email da validare.
     * @return true se l'email ha un formato valido, false altrimenti.
     */
    public static boolean validaEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return email.trim().matches(REGEX_EMAIL);
    }

    /**
     * Verifica la robustezza di una password secondo i requisiti di sicurezza:
     * - Lunghezza minima di 8 caratteri.
     * - Almeno una lettera maiuscola (A-Z).
     * - Almeno un carattere speciale (!@#$%^&*...).
     * 
     * @param password La password da validare.
     * @return null se la password soddisfa tutti i requisiti, altrimenti una stringa con l'errore descrittivo.
     */
    public static String validaPassword(String password) {
        if (password == null || password.length() < 8) {
            return "La password deve contenere almeno 8 caratteri.";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "La password deve contenere almeno una lettera maiuscola (A-Z).";
        }

        if (!password.matches(".*[!@#$%^&*()_+=\\-\\[\\]{};:'\",.<>?/\\\\|~].*")) {
            return "La password deve contenere almeno un carattere speciale (!@#$%^&* ecc.).";
        }

        return null; // Password valida
    }
}

