package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Veicolo;
import util.DatabaseConnection;

public class VeicoloDAO {

    public static void creaTabellaSeNonEsiste() {
        String sql = "CREATE TABLE IF NOT EXISTS veicoli (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "codice_fiscale_utente TEXT NOT NULL, " +
                     "tipologia TEXT NOT NULL, " +
                     "nome TEXT NOT NULL, " +
                     "targa TEXT NOT NULL, " +
                     "data_assicurazione DATE, " +
                     "validita_assicurazione TEXT, " +
                     "data_immatricolazione DATE, " +
                     "prima_revisione_fatta BOOLEAN, " +
                     "data_ultima_revisione DATE, " +
                     "FOREIGN KEY(codice_fiscale_utente) REFERENCES utenti(codice_fiscale))";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean inserisciVeicolo(Veicolo v) {
        String sql = "INSERT INTO veicoli (codice_fiscale_utente, tipologia, nome, targa, data_assicurazione, validita_assicurazione, data_immatricolazione, prima_revisione_fatta, data_ultima_revisione) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) return false;
            
            pstmt.setString(1, v.getCodiceFiscaleUtente());
            pstmt.setString(2, v.getTipologia());
            pstmt.setString(3, v.getNome());
            pstmt.setString(4, v.getTarga());
            pstmt.setDate(5, v.getDataAssicurazione() != null ? Date.valueOf(v.getDataAssicurazione()) : null);
            pstmt.setString(6, v.getValiditaAssicurazione());
            pstmt.setDate(7, v.getDataImmatricolazione() != null ? Date.valueOf(v.getDataImmatricolazione()) : null);
            pstmt.setBoolean(8, v.isPrimaRevisioneFatta());
            pstmt.setDate(9, v.getDataUltimaRevisione() != null ? Date.valueOf(v.getDataUltimaRevisione()) : null);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Veicolo> getVeicoliPerUtente(String cf) {
        List<Veicolo> lista = new ArrayList<>();
        String sql = "SELECT * FROM veicoli WHERE codice_fiscale_utente = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            if (conn == null) return lista;
            pstmt.setString(1, cf);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Veicolo v = new Veicolo();
                v.setId(rs.getInt("id"));
                v.setCodiceFiscaleUtente(rs.getString("codice_fiscale_utente"));
                v.setTipologia(rs.getString("tipologia"));
                v.setNome(rs.getString("nome"));
                v.setTarga(rs.getString("targa"));
                
                Date da = rs.getDate("data_assicurazione");
                if (da != null) v.setDataAssicurazione(da.toLocalDate());
                
                v.setValiditaAssicurazione(rs.getString("validita_assicurazione"));
                
                Date di = rs.getDate("data_immatricolazione");
                if (di != null) v.setDataImmatricolazione(di.toLocalDate());
                
                v.setPrimaRevisioneFatta(rs.getBoolean("prima_revisione_fatta"));
                
                Date dr = rs.getDate("data_ultima_revisione");
                if (dr != null) v.setDataUltimaRevisione(dr.toLocalDate());
                
                lista.add(v);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public static boolean aggiornaVeicolo(Veicolo v) {
        String sql = "UPDATE veicoli SET tipologia = ?, nome = ?, targa = ?, data_assicurazione = ?, validita_assicurazione = ?, data_immatricolazione = ?, prima_revisione_fatta = ?, data_ultima_revisione = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (conn == null) return false;
            
            pstmt.setString(1, v.getTipologia());
            pstmt.setString(2, v.getNome());
            pstmt.setString(3, v.getTarga());
            pstmt.setDate(4, v.getDataAssicurazione() != null ? Date.valueOf(v.getDataAssicurazione()) : null);
            pstmt.setString(5, v.getValiditaAssicurazione());
            pstmt.setDate(6, v.getDataImmatricolazione() != null ? Date.valueOf(v.getDataImmatricolazione()) : null);
            pstmt.setBoolean(7, v.isPrimaRevisioneFatta());
            pstmt.setDate(8, v.getDataUltimaRevisione() != null ? Date.valueOf(v.getDataUltimaRevisione()) : null);
            pstmt.setInt(9, v.getId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean eliminaVeicolo(int id) {
        String sql = "DELETE FROM veicoli WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            if (conn == null) return false;
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
