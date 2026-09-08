package dao;

import model.ExceptionLog;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * =================================================================================
 * DATA ACCESS OBJECT - LOG ECCEZIONI (ExceptionLogDAO)
 * =================================================================================
 * Gestisce la tabella SQLite 'log_eccezioni' per salvare, consultare e gestire
 * le eccezioni di sistema (Checked e Unchecked) nel database locale.
 */
public class ExceptionLogDAO {

    /**
     * Inizializza e crea la tabella 'log_eccezioni' nel database SQLite se non esiste già.
     */
    public static void creaTabellaSeNonEsiste() {
        String sql = "CREATE TABLE IF NOT EXISTS log_eccezioni (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                     "tipo_eccezione TEXT NOT NULL, " +
                     "classe_eccezione TEXT NOT NULL, " +
                     "messaggio TEXT, " +
                     "stack_trace TEXT, " +
                     "codice_fiscale TEXT, " +
                     "contesto TEXT" +
                     ");";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("❌ Errore durante la creazione della tabella log_eccezioni: " + e.getMessage());
        }
    }

    /**
     * Inserisce un nuovo record di eccezione nel database.
     * 
     * @param log L'oggetto ExceptionLog con le informazioni dell'errore.
     * @return true se il salvataggio è andato a buon fine, false altrimenti.
     */
    public static boolean salvaLog(ExceptionLog log) {
        creaTabellaSeNonEsiste();

        String sql = "INSERT INTO log_eccezioni (tipo_eccezione, classe_eccezione, messaggio, stack_trace, codice_fiscale, contesto) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, log.getTipoEccezione());
            stmt.setString(2, log.getClasseEccezione());
            stmt.setString(3, log.getMessaggio());
            stmt.setString(4, log.getStackTrace());
            stmt.setString(5, log.getCodiceFiscale());
            stmt.setString(6, log.getContesto());

            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Errore salvataggio log eccezione nel DB: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recupera tutti i log delle eccezioni registrati nel database, ordinati per data decrescente.
     * 
     * @return Lista di oggetti ExceptionLog.
     */
    public static List<ExceptionLog> getTuttiILog() {
        creaTabellaSeNonEsiste();
        List<ExceptionLog> lista = new ArrayList<>();

        String sql = "SELECT id, timestamp, tipo_eccezione, classe_eccezione, messaggio, stack_trace, codice_fiscale, contesto " +
                     "FROM log_eccezioni ORDER BY id DESC";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return lista;

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ExceptionLog log = new ExceptionLog(
                    rs.getInt("id"),
                    rs.getString("timestamp"),
                    rs.getString("tipo_eccezione"),
                    rs.getString("classe_eccezione"),
                    rs.getString("messaggio"),
                    rs.getString("stack_trace"),
                    rs.getString("codice_fiscale"),
                    rs.getString("contesto")
                );
                lista.add(log);
            }
        } catch (SQLException e) {
            System.err.println("❌ Errore lettura log eccezioni dal DB: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Svuota completamente la tabella delle eccezioni registrate.
     * 
     * @return true se la tabella è stata svuotata con successo.
     */
    public static boolean svuotaLog() {
        creaTabellaSeNonEsiste();
        String sql = "DELETE FROM log_eccezioni";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Errore svuotamento log eccezioni: " + e.getMessage());
            return false;
        }
    }
}
