package dao;

// =================================================================================
// IMPORT DELLE CLASSI DEL MODELLO E DELLE UTILITY
// =================================================================================
import model.Utente;                 // L'oggetto Java che rappresenta l'utente
import util.DatabaseConnection;      // La classe che abbiamo creato per avere la connessione al DB
import util.PasswordUtil;            // La classe che abbiamo creato per criptare la password

// =================================================================================
// IMPORT DELLE CLASSI PER IL DATABASE (JDBC)
// =================================================================================
import java.sql.Connection;          // La sessione viva col database
import java.sql.PreparedStatement;   // Il "veicolo" che trasporta il nostro comando SQL al database
import java.sql.ResultSet;           // Il "vassoio" che contiene i risultati che il DB ci restituisce (le righe lette)
import java.sql.SQLException;        // Gestione degli errori SQL

/**
 * =================================================================================
 * DATA ACCESS OBJECT - UTENTE (UtenteDAO)
 * =================================================================================
 * DAO significa "Data Access Object". È un pattern architetturale (una regola di design).
 * L'idea è molto semplice: i Controller (quelli che gestiscono l'interfaccia grafica e i click)
 * NON devono scrivere comandi SQL direttamente. Quando un controller vuole salvare un utente,
 * chiama questa classe DAO. Il DAO prende i dati, crea il comando SQL, si collega a MySQL
 * e fa il "lavoro sporco". 
 * 
 * In questo modo, se un domani cambiassimo database (da MySQL a Oracle o MongoDB), 
 * dovremmo modificare solo le classi DAO, lasciando intatti tutti i Controller!
 */
public class UtenteDAO {

    /**
     * =============================================================================
     * 1. REGISTRAZIONE (CREATE - INSERT)
     * =============================================================================
     * Inserisce un nuovo utente nella tabella MySQL.
     * 
     * @param utente L'oggetto Utente con tutti i dati (nome, cognome, email, CF)
     * @param passwordInChiaro La password inserita dall'utente nel form (verrà criptata qui dentro)
     * @return TRUE se la registrazione è andata a buon fine, FALSE se c'è stato un errore
     */
    public boolean registraUtente(Utente utente, String passwordInChiaro) {
        
        // 1. Scriviamo il comando SQL. I punti interrogativi (?) sono dei "segnaposto".
        // Usiamo i segnaposto invece di concatenare direttamente le stringhe (es. ... VALUES ('" + nome + "'))
        // per prevenire gravissimi attacchi hacker chiamati "SQL Injection".
        String sql = "INSERT INTO utenti (codice_fiscale, nome, cognome, email, password, data_iscrizione) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false; // Se non c'è connessione, fallisce subito

        // 2. Usiamo il pattern "try-with-resources" per garantire la chiusura automatica del PreparedStatement
        // e liberare le risorse di sistema anche in caso di eccezioni improvvise.
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // 3. Sostituiamo i punti interrogativi (?) con i dati veri estratti dall'oggetto Utente
            stmt.setString(1, utente.getCodiceFiscale()); // Sostituisce il primo ?
            stmt.setString(2, utente.getNome());          // Sostituisce il secondo ?
            stmt.setString(3, utente.getCognome());
            stmt.setString(4, utente.getEmail());
            
            // PRIMA di salvare la password, la criptiamo chiamando la nostra Utility
            String passwordCriptata = PasswordUtil.hashPassword(passwordInChiaro);
            stmt.setString(5, passwordCriptata);
            
            stmt.setString(6, utente.getDataIscrizione());
            
            // 4. ESECUZIONE DEL COMANDO (executeUpdate si usa per comandi che MODIFICANO il db come INSERT, UPDATE, DELETE)
            // executeUpdate() restituisce il numero di righe modificate nella tabella.
            int righeInserite = stmt.executeUpdate();
            
            // Se ha inserito almeno 1 riga, significa che ha funzionato
            return righeInserite > 0;
            
        } catch (SQLException e) {
            // Questo errore scatta solitamente se si cerca di registrare un Codice Fiscale già presente
            // (visto che il CF è una Primary Key e deve essere univoco)
            System.err.println("Errore durante la registrazione: " + e.getMessage());
            return false;
        }
    }

    /**
     * =============================================================================
     * 2. ACCESSO / LOGIN (READ - SELECT)
     * =============================================================================
     * Cerca un utente tramite il CF, e controlla se la password combacia.
     * 
     * @param codiceFiscale Il CF usato come username
     * @param passwordInChiaro La password inserita nel form di login
     * @return L'oggetto Utente ricostruito coi dati del Database (se login ok), oppure null se credenziali errate
     */
    public Utente login(String codiceFiscale, String passwordInChiaro) {
        
        // 1. Comando SQL: "Seleziona i dati di un utente DOVE il codice fiscale è uguale a quello inserito (?)"
        String sql = "SELECT nome, cognome, email, data_iscrizione, password FROM utenti WHERE codice_fiscale = ?";
        
        // DEBUG: stampa in console il CF che stiamo cercando
        System.out.println("[DEBUG LOGIN] Tentativo di login con CF: '" + codiceFiscale + "'");
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("[DEBUG LOGIN] ❌ Connessione al DB è null!");
            return null;
        }

        // 2. Try-with-resources annidato per PreparedStatement e ResultSet: entrambi vengono chiusi in modo sicuro al termine
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceFiscale); // Sostituisce il ?
            
            // executeQuery si usa SOLO per la SELECT, quando vogliamo LEGGERE dati
            try (ResultSet rs = stmt.executeQuery()) {
                // 3. rs.next() sposta il cursore sulla prima riga trovata. 
                // Se restituisce true, significa che l'utente esiste nel database!
                if (rs.next()) {
                    System.out.println("[DEBUG LOGIN] ✅ Utente trovato nel DB con CF: " + codiceFiscale);
                    
                    // Estraiamo la lunghissima password criptata dal database
                    String hashSalvato = rs.getString("password");
                    
                    // 4. VERIFICA DELLA PASSWORD CON BCRYPT
                    if (PasswordUtil.checkPassword(passwordInChiaro, hashSalvato)) {
                        System.out.println("[DEBUG LOGIN] ✅ Password CORRETTA!");
                        // Password corretta! Ora estraiamo tutti gli altri dati dalla riga del Database
                        String nome = rs.getString("nome");
                        String cognome = rs.getString("cognome");
                        String email = rs.getString("email");
                        String dataIscrizione = rs.getString("data_iscrizione");
                        
                        // Ricreiamo l'oggetto Java "Utente" usando il costruttore completo 
                        // e lo restituiamo al Controller che ci ha chiamato
                        return new Utente(nome, cognome, email, codiceFiscale, dataIscrizione);
                    } else {
                        System.out.println("[DEBUG LOGIN] ❌ Password ERRATA! Gli hash non coincidono.");
                    }
                } else {
                    System.out.println("[DEBUG LOGIN] ❌ Nessun utente trovato con CF: '" + codiceFiscale + "'");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG LOGIN] ❌ Errore SQL durante il login: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Se arriviamo qui, significa che o il CF non esiste o la password è sbagliata
        return null; 
    }

    /**
     * =============================================================================
     * 3. CAMBIO PASSWORD (UPDATE)
     * =============================================================================
     * Modifica SOLO la colonna password di un utente specifico.
     * 
     * @param codiceFiscale Il CF dell'utente a cui cambiare la password
     * @param nuovaPasswordInChiaro La nuova password scelta (verrà criptata)
     * @return true se l'aggiornamento ha successo
     */
    public boolean cambiaPassword(String codiceFiscale, String nuovaPasswordInChiaro) {
        
        // Comando UPDATE: "Aggiorna la tabella utenti, IMPOSTA la password a (?), DOVE il codice fiscale è (?)"
        String sql = "UPDATE utenti SET password = ? WHERE codice_fiscale = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Cripta la nuova password
            String passwordCriptata = PasswordUtil.hashPassword(nuovaPasswordInChiaro);
            
            stmt.setString(1, passwordCriptata); // Primo ?
            stmt.setString(2, codiceFiscale);    // Secondo ?
            
            // Esegue l'aggiornamento. Ritorna true se almeno 1 riga è stata modificata.
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore durante il cambio password: " + e.getMessage());
            return false;
        }
    }

    /**
     * =============================================================================
     * 4. ELIMINAZIONE ACCOUNT (DELETE)
     * =============================================================================
     * Rimuove l'intera riga dell'utente dal database.
     * 
     * @param codiceFiscale Il CF dell'utente da eliminare
     * @return true se l'eliminazione ha successo
     */
    public boolean eliminaUtente(String codiceFiscale) {
        
        // Comando DELETE: "Elimina dalla tabella utenti DOVE il codice fiscale è (?)"
        String sql = "DELETE FROM utenti WHERE codice_fiscale = ?";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceFiscale); // Sostituisce il ?
            
            // Esegue l'eliminazione. Ritorna true se almeno 1 riga è stata cancellata.
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore durante l'eliminazione account: " + e.getMessage());
            return false;
        }
    }
}
