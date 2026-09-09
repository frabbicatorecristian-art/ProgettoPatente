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

    public UtenteDAO() {
        inizializzaTabella();
    }

    public void inizializzaTabella() {
        String sql = "CREATE TABLE IF NOT EXISTS utenti (" +
                "codice_fiscale TEXT PRIMARY KEY, " +
                "nome TEXT, " +
                "cognome TEXT, " +
                "email TEXT, " +
                "password TEXT, " +
                "data_iscrizione TEXT, " +
                "domanda_sicurezza TEXT, " +
                "risposta_sicurezza TEXT)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore creazione tabella utenti: " + e.getMessage());
        }

        // Migration per aggiungere colonne se la tabella esisteva già
        try (PreparedStatement stmt = conn.prepareStatement("ALTER TABLE utenti ADD COLUMN domanda_sicurezza TEXT")) {
            stmt.executeUpdate();
        } catch (SQLException ignored) {}

        try (PreparedStatement stmt = conn.prepareStatement("ALTER TABLE utenti ADD COLUMN risposta_sicurezza TEXT")) {
            stmt.executeUpdate();
        } catch (SQLException ignored) {}
    }

    /**
     * =============================================================================
     * 1. REGISTRAZIONE (CREATE - INSERT)
     * =============================================================================
     * Inserisce un nuovo utente nella tabella MySQL/SQLite.
     * 
     * @param utente L'oggetto Utente con tutti i dati (nome, cognome, email, CF, domanda, risposta)
     * @param passwordInChiaro La password inserita dall'utente nel form (verrà criptata qui dentro)
     * @return TRUE se la registrazione è andata a buon fine, FALSE se c'è stato un errore
     */
    public boolean registraUtente(Utente utente, String passwordInChiaro) {
        
        String sql = "INSERT INTO utenti (codice_fiscale, nome, cognome, email, password, data_iscrizione, domanda_sicurezza, risposta_sicurezza) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, utente.getCodiceFiscale());
            stmt.setString(2, utente.getNome());
            stmt.setString(3, utente.getCognome());
            stmt.setString(4, utente.getEmail());
            
            String passwordCriptata = PasswordUtil.hashPassword(passwordInChiaro);
            stmt.setString(5, passwordCriptata);
            stmt.setString(6, utente.getDataIscrizione());
            stmt.setString(7, utente.getDomandaSicurezza());
            
            String rispostaNorm = utente.getRispostaSicurezza() != null ? utente.getRispostaSicurezza().trim().toLowerCase() : null;
            String rispostaSalvataggio = (rispostaNorm != null && !rispostaNorm.isEmpty()) ? PasswordUtil.hashPassword(rispostaNorm) : null;
            stmt.setString(8, rispostaSalvataggio);
            
            int righeInserite = stmt.executeUpdate();
            return righeInserite > 0;
            
        } catch (SQLException e) {
            System.err.println("Errore durante la registrazione: " + e.getMessage());
            return false;
        }
    }

    /**
     * =============================================================================
     * 2. ACCESSO / LOGIN (READ - SELECT)
     * =============================================================================
     */
    public Utente login(String codiceFiscale, String passwordInChiaro) {
        
        String sql = "SELECT nome, cognome, email, data_iscrizione, password, domanda_sicurezza, risposta_sicurezza FROM utenti WHERE codice_fiscale = ?";
        
        System.out.println("[DEBUG LOGIN] Tentativo di login con CF: '" + codiceFiscale + "'");
        
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("[DEBUG LOGIN] ❌ Connessione al DB è null!");
            return null;
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceFiscale);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG LOGIN] ✅ Utente trovato nel DB con CF: " + codiceFiscale);
                    
                    String hashSalvato = rs.getString("password");
                    
                    if (PasswordUtil.checkPassword(passwordInChiaro, hashSalvato)) {
                        System.out.println("[DEBUG LOGIN] ✅ Password CORRETTA!");
                        String nome = rs.getString("nome");
                        String cognome = rs.getString("cognome");
                        String email = rs.getString("email");
                        String dataIscrizione = rs.getString("data_iscrizione");
                        String domanda = rs.getString("domanda_sicurezza");
                        String risposta = rs.getString("risposta_sicurezza");
                        
                        return new Utente(nome, cognome, email, codiceFiscale, dataIscrizione, domanda, risposta);
                    } else {
                        System.out.println("[DEBUG LOGIN] ❌ Password ERRATA! Gli hash non coincidono.");
                    }
                } else {
                    System.out.println("[DEBUG LOGIN] ❌ Nessun utente trovato con CF: '" + codiceFiscale + "'");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DEBUG LOGIN] ❌ Errore SQL durante il login: " + e.getMessage());
        }
        return null;
    }

    /**
     * Recupera la domanda di sicurezza impostata per un dato codice fiscale.
     */
    public String recuperaDomandaSicurezza(String codiceFiscale) {
        String sql = "SELECT domanda_sicurezza FROM utenti WHERE UPPER(codice_fiscale) = UPPER(?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceFiscale);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("domanda_sicurezza");
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore recupero domanda sicurezza: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica la risposta fornita dall'utente e, se corretta, aggiorna la password.
     */
    public boolean verificaRispostaEResetPassword(String codiceFiscale, String rispostaData, String nuovaPasswordInChiaro) {
        String sql = "SELECT risposta_sicurezza FROM utenti WHERE UPPER(codice_fiscale) = UPPER(?)";
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codiceFiscale);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String rispostaSalvata = rs.getString("risposta_sicurezza");
                    if (rispostaSalvata == null || rispostaSalvata.trim().isEmpty()) {
                        System.err.println("Nessuna risposta di sicurezza impostata per l'utente.");
                        return false;
                    }
                    String rispostaNorm = rispostaData != null ? rispostaData.trim().toLowerCase() : "";
                    if (PasswordUtil.checkPassword(rispostaNorm, rispostaSalvata)) {
                        return cambiaPassword(codiceFiscale.toUpperCase(), nuovaPasswordInChiaro);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore verifica risposta e reset password: " + e.getMessage());
        }
        return false;
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
    
    public boolean aggiornaDatiUtente(model.Utente u) {
        String sql = "UPDATE utenti SET nome = ?, cognome = ?, email = ? WHERE codice_fiscale = ?";
        java.sql.Connection conn = util.DatabaseConnection.getConnection();
        if (conn == null) return false;
        
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getCognome());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getCodiceFiscale());
            return stmt.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            System.err.println("Errore durante l'aggiornamento dati utente: " + e.getMessage());
            return false;
        }
    }

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
