package dao;

import model.Domanda;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DomandaDAO {

    public static void creaTabellaSeNonEsiste() {
        String sql = "CREATE TABLE IF NOT EXISTS domande (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "testo TEXT NOT NULL, " +
                "is_vero INTEGER NOT NULL, " +
                "spiegazione TEXT, " +
                "immagine_path TEXT)";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Domanda> getDomandeCasuali(int limite) {
        List<Domanda> lista = new ArrayList<>();
        String sql = "SELECT * FROM domande ORDER BY RANDOM() LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Domanda d = new Domanda(
                        rs.getInt("id"),
                        rs.getString("testo"),
                        rs.getInt("is_vero") == 1,
                        rs.getString("spiegazione"),
                        rs.getString("immagine_path")
                );
                lista.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}