package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * =================================================================================
 * MODEL - UTENTE
 * =================================================================================
 * POJO (Plain Old Java Object) che rappresenta un utente registrato nell'applicazione.
 * Contiene tutti i campi anagrafici raccolti durante la registrazione.
 *
 * In futuro questo oggetto sarà popolato direttamente dal database (es. tramite JDBC/JPA).
 */
public class Utente {

    // =========================================================================
    // CAMPI ANAGRAFICI
    // =========================================================================
    private String nome;
    private String cognome;
    private String email;
    private String codiceFiscale;
    private String dataIscrizione;  // Formato "dd/MM/yyyy"

    // =========================================================================
    // COSTRUTTORI
    // =========================================================================

    /**
     * Costruttore completo: usato alla registrazione di un nuovo utente.
     * La data di iscrizione viene impostata automaticamente al giorno corrente.
     *
     * @param nome         Nome dell'utente
     * @param cognome      Cognome dell'utente
     * @param email        Indirizzo email
     * @param codiceFiscale Codice Fiscale (16 caratteri, maiuscolo)
     */
    public Utente(String nome, String cognome, String email, String codiceFiscale) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.codiceFiscale = codiceFiscale;
        // Imposta la data di iscrizione al momento attuale
        this.dataIscrizione = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /**
     * Costruttore con data esplicita: usato quando i dati vengono caricati dal database
     * e la data di iscrizione è già memorizzata.
     *
     * @param nome           Nome dell'utente
     * @param cognome        Cognome dell'utente
     * @param email          Indirizzo email
     * @param codiceFiscale  Codice Fiscale
     * @param dataIscrizione Data di iscrizione già formattata (es. "05/01/2026")
     */
    public Utente(String nome, String cognome, String email,
                  String codiceFiscale, String dataIscrizione) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.codiceFiscale = codiceFiscale;
        this.dataIscrizione = dataIscrizione;
    }

    // =========================================================================
    // GETTER
    // =========================================================================

    public String getNome()           { return nome; }
    public String getCognome()        { return cognome; }
    public String getNomeCompleto()   { return nome + " " + cognome; }
    public String getEmail()          { return email; }
    public String getCodiceFiscale()  { return codiceFiscale; }
    public String getDataIscrizione() { return dataIscrizione; }

    // =========================================================================
    // SETTER (solo i campi modificabili dall'utente)
    // =========================================================================

    public void setNome(String nome)         { this.nome = nome; }
    public void setCognome(String cognome)   { this.cognome = cognome; }
    public void setEmail(String email)       { this.email = email; }

    // =========================================================================
    // UTILITY
    // =========================================================================

    @Override
    public String toString() {
        return "Utente{nome='" + nome + "', cognome='" + cognome
                + "', email='" + email + "', CF='" + codiceFiscale
                + "', iscritto='" + dataIscrizione + "'}";
    }
}
