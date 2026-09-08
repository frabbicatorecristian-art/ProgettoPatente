package util;

import dao.ExceptionLogDAO;
import model.ExceptionLog;
import model.SessioneUtente;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * =================================================================================
 * GESTORE ECCEZIONI GLOBALE (GestoreEccezioni)
 * =================================================================================
 * Intercetta, analizza e registra le eccezioni di sistema nel database SQLite.
 * Distingue automaticamente tra:
 * - Eccezioni CHECKED: derivanti da Exception (es. SQLException, IOException)
 * - Eccezioni UNCHECKED: derivanti da RuntimeException o Error (es. NullPointerException, ArithmeticException)
 */
public class GestoreEccezioni {

    /**
     * Registra un'eccezione nel database SQLite 'log_eccezioni' e ne stampa il resoconto in console.
     * 
     * @param throwable L'oggetto Throwable/Exception intercettato.
     * @param contesto Il contesto o nome del metodo/classe in cui si è verificato l'errore.
     */
    public static void registra(Throwable throwable, String contesto) {
        if (throwable == null) return;

        try {
            // 1. Determina la categoria dell'eccezione (CHECKED vs UNCHECKED)
            String tipo = (throwable instanceof RuntimeException || throwable instanceof Error) 
                          ? "UNCHECKED" 
                          : "CHECKED";

            // 2. Estrae la traccia completa dello stack trace in una stringa
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            String stackTraceStringa = sw.toString();

            // 3. Recupera il codice fiscale dell'utente loggato in sessione (se disponibile)
            String cfUtente = null;
            if (SessioneUtente.getInstance().isLoggato() && SessioneUtente.getInstance().getUtente() != null) {
                cfUtente = SessioneUtente.getInstance().getUtente().getCodiceFiscale();
            }

            // 4. Prepara il messaggio
            String messaggio = throwable.getMessage();
            if (messaggio == null || messaggio.isEmpty()) {
                messaggio = throwable.toString();
            }

            // 5. Crea l'oggetto log
            ExceptionLog log = new ExceptionLog(
                tipo,
                throwable.getClass().getName(),
                messaggio,
                stackTraceStringa,
                cfUtente,
                contesto != null ? contesto : "Applicazione"
            );

            // 6. Salva nel database SQLite
            ExceptionLogDAO.salvaLog(log);

            // 7. Stampa di log formattata in console per il debug sviluppatore
            System.err.println("⚠️ [GESTORE ECCEZIONI - DB LOG] [" + tipo + "] " + 
                               throwable.getClass().getSimpleName() + " in " + contesto + ": " + messaggio);

        } catch (Throwable logError) {
            // Evita che un errore nel logger provochi un crash a cascata
            System.err.println("❌ Impossibile registrare il log dell'eccezione nel DB: " + logError.getMessage());
        }
    }
}
