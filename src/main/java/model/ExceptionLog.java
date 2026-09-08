package model;

/**
 * =================================================================================
 * MODELLO - LOG ECCEZIONE (ExceptionLog)
 * =================================================================================
 * Rappresenta una voce di errore/eccezione memorizzata nel database SQLite.
 * Traccia eccezioni di tipo CHECKED e UNCHECKED registrate durante l'esecuzione dell'app.
 */
public class ExceptionLog {

    private int id;
    private String timestamp;
    private String tipoEccezione;    // 'CHECKED' oppure 'UNCHECKED'
    private String classeEccezione;  // es. 'java.sql.SQLException', 'java.lang.NullPointerException'
    private String messaggio;        // Messaggio dell'eccezione
    private String stackTrace;       // Traceback completo dell'errore
    private String codiceFiscale;    // CF dell'utente loggato in quel momento (opzionale)
    private String contesto;         // Metodo/Controller di origine dell'eccezione

    // Costruttore completo
    public ExceptionLog(int id, String timestamp, String tipoEccezione, String classeEccezione, 
                        String messaggio, String stackTrace, String codiceFiscale, String contesto) {
        this.id = id;
        this.timestamp = timestamp;
        this.tipoEccezione = tipoEccezione;
        this.classeEccezione = classeEccezione;
        this.messaggio = messaggio;
        this.stackTrace = stackTrace;
        this.codiceFiscale = codiceFiscale;
        this.contesto = contesto;
    }

    // Costruttore senza ID per nuovi inserimenti
    public ExceptionLog(String tipoEccezione, String classeEccezione, String messaggio, 
                        String stackTrace, String codiceFiscale, String contesto) {
        this(0, null, tipoEccezione, classeEccezione, messaggio, stackTrace, codiceFiscale, contesto);
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getTipoEccezione() { return tipoEccezione; }
    public void setTipoEccezione(String tipoEccezione) { this.tipoEccezione = tipoEccezione; }

    public String getClasseEccezione() { return classeEccezione; }
    public void setClasseEccezione(String classeEccezione) { this.classeEccezione = classeEccezione; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String codiceFiscale) { this.codiceFiscale = codiceFiscale; }

    public String getContesto() { return contesto; }
    public void setContesto(String contesto) { this.contesto = contesto; }

    @Override
    public String toString() {
        return "[" + timestamp + "] [" + tipoEccezione + "] " + classeEccezione + " (" + contesto + "): " + messaggio;
    }
}
