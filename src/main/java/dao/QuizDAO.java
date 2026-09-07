package dao;

import model.Quiz;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public static void creaTabellaSeNonEsiste() {
        String sql = "CREATE TABLE IF NOT EXISTS quiz (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "utente_cf TEXT NOT NULL, " +
                "data_ora TEXT NOT NULL, " +
                "errori INTEGER NOT NULL, " +
                "esito TEXT NOT NULL, " +
                "FOREIGN KEY(utente_cf) REFERENCES utenti(codice_fiscale))";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Errore creazione tabella quiz: " + e.getMessage());
        }
    }

    public static List<Quiz> getQuizByUtente(String cf) {
        List<Quiz> lista = new ArrayList<>();
        String sql = "SELECT * FROM quiz WHERE utente_cf = ? ORDER BY data_ora DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cf);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Quiz q = new Quiz(
                        rs.getInt("id"),
                        rs.getString("utente_cf"),
                        LocalDateTime.parse(rs.getString("data_ora")),
                        rs.getInt("errori"),
                        rs.getString("esito")
                );
                lista.add(q);
            }
        } catch (SQLException e) {
            System.out.println("Errore recupero quiz: " + e.getMessage());
        }
        return lista;
    }

    public static boolean inserisciQuiz(Quiz q) {
        String sql = "INSERT INTO quiz(utente_cf, data_ora, errori, esito) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, q.getUtenteCodiceFiscale());
            pstmt.setString(2, q.getDataOra().toString());
            pstmt.setInt(3, q.getErrori());
            pstmt.setString(4, q.getEsito());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Errore inserimento quiz: " + e.getMessage());
            return false;
        }
    }
}
