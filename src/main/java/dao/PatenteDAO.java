package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.CategoriaPatente;
import model.Patente;
import util.DatabaseConnection;

public class PatenteDAO {

    public static void creaTabelleSeNonEsistono() {
        String sqlPatenti = "CREATE TABLE IF NOT EXISTS patenti (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codice_fiscale_utente TEXT NOT NULL UNIQUE, " +
                "numero_patente TEXT NOT NULL, " +
                "data_nascita DATE NOT NULL, " +
                "ente_rilascio TEXT NOT NULL, " +
                "saldo_punti INTEGER NOT NULL, " +
                "FOREIGN KEY(codice_fiscale_utente) REFERENCES utenti(codice_fiscale))";

        String sqlCategorie = "CREATE TABLE IF NOT EXISTS patente_categorie (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "patente_id INTEGER NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "data_rilascio DATE NOT NULL, " +
                "FOREIGN KEY(patente_id) REFERENCES patenti(id) ON DELETE CASCADE)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sqlPatenti);
                stmt.execute(sqlCategorie);
            }
        } catch (SQLException e) {
            System.out.println("Errore nella creazione tabelle Patente: " + e.getMessage());
        }
    }

    public static boolean inserisciPatenteConCategoria(Patente patente, String categoria, LocalDate dataRilascio) {
        String sqlInserisciPatente = "INSERT INTO patenti(codice_fiscale_utente, numero_patente, data_nascita, ente_rilascio, saldo_punti) VALUES(?, ?, ?, ?, ?)";
        String sqlInserisciCategoria = "INSERT INTO patente_categorie(patente_id, categoria, data_rilascio) VALUES(?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inizio transazione

            int patenteId = -1;
            
            // 1. Inserisci Patente
            try (PreparedStatement pstmt = conn.prepareStatement(sqlInserisciPatente, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, patente.getCodiceFiscaleUtente());
                pstmt.setString(2, patente.getNumeroPatente());
                pstmt.setString(3, patente.getDataNascita().toString());
                pstmt.setString(4, patente.getEnteRilascio());
                pstmt.setInt(5, patente.getSaldoPunti());
                pstmt.executeUpdate();
                
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        patenteId = rs.getInt(1);
                    }
                }
            }

            // 2. Inserisci Categoria se la patente è stata creata
            if (patenteId != -1) {
                try (PreparedStatement pstmt = conn.prepareStatement(sqlInserisciCategoria)) {
                    pstmt.setInt(1, patenteId);
                    pstmt.setString(2, categoria);
                    pstmt.setString(3, dataRilascio.toString());
                    pstmt.executeUpdate();
                }
            } else {
                conn.rollback();
                return false;
            }

            conn.commit(); // Fine transazione
            return true;

        } catch (SQLException e) {
            System.out.println("Errore salvataggio patente: " + e.getMessage());
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException re) {
                System.out.println("Errore rollback: " + re.getMessage());
            }
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Errore ripristino autocommit: " + e.getMessage());
            }
        }
    }

    public static Patente getPatenteByCodiceFiscale(String codiceFiscale) {
        String sql = "SELECT * FROM patenti WHERE codice_fiscale_utente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codiceFiscale);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Patente p = new Patente();
                p.setId(rs.getInt("id"));
                p.setCodiceFiscaleUtente(rs.getString("codice_fiscale_utente"));
                p.setNumeroPatente(rs.getString("numero_patente"));
                p.setDataNascita(LocalDate.parse(rs.getString("data_nascita")));
                p.setEnteRilascio(rs.getString("ente_rilascio"));
                p.setSaldoPunti(rs.getInt("saldo_punti"));
                return p;
            }
        } catch (SQLException e) {
            System.out.println("Errore recupero patente: " + e.getMessage());
        }
        return null;
    }

    public static List<CategoriaPatente> getCategorieByPatenteId(int patenteId) {
        List<CategoriaPatente> categorie = new ArrayList<>();
        String sql = "SELECT * FROM patente_categorie WHERE patente_id = ? ORDER BY data_rilascio ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patenteId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                CategoriaPatente c = new CategoriaPatente();
                c.setId(rs.getInt("id"));
                c.setPatenteId(rs.getInt("patente_id"));
                c.setCategoria(rs.getString("categoria"));
                c.setDataRilascio(LocalDate.parse(rs.getString("data_rilascio")));
                categorie.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Errore recupero categorie: " + e.getMessage());
        }
        return categorie;
    }
    
    public static boolean inserisciCategoriaSingola(int patenteId, String categoria, LocalDate dataRilascio) {
        String sql = "INSERT INTO patente_categorie(patente_id, categoria, data_rilascio) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patenteId);
            pstmt.setString(2, categoria);
            pstmt.setString(3, dataRilascio.toString());
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Errore inserimento singola categoria: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean eliminaCategoria(int idCategoria) {
        String sql = "DELETE FROM patente_categorie WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idCategoria);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Errore eliminazione categoria: " + e.getMessage());
            return false;
        }
    }

    public static boolean eliminaPatente(int idPatente) {
        String sqlCategorie = "DELETE FROM patente_categorie WHERE patente_id = ?";
        String sqlPatente = "DELETE FROM patenti WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmtCat = conn.prepareStatement(sqlCategorie);
                 PreparedStatement pstmtPat = conn.prepareStatement(sqlPatente)) {
                
                pstmtCat.setInt(1, idPatente);
                pstmtCat.executeUpdate();
                
                pstmtPat.setInt(1, idPatente);
                int rows = pstmtPat.executeUpdate();
                
                conn.commit();
                return rows > 0;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Errore eliminazione patente e categorie: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean aggiornaPuntiPatente(int idPatente, int nuoviPunti) {
        String sql = "UPDATE patenti SET saldo_punti = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, nuoviPunti);
            pstmt.setInt(2, idPatente);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Errore aggiornamento punti: " + e.getMessage());
            return false;
        }
    }
}
