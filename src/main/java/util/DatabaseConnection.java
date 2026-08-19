package util;

// =================================================================================
// IMPORT DELLE CLASSI PER IL DATABASE (JDBC - Java Database Connectivity)
// =================================================================================
import java.sql.Connection;      // Interfaccia principale che rappresenta la sessione fisica con il database
import java.sql.DriverManager;   // Classe responsabile del caricamento del driver e della creazione della connessione
import java.sql.SQLException;    // Eccezione specifica per gli errori SQL (es. password errata, database non trovato)

/**
 * =================================================================================
 * CLASSE DI UTILITA' - CONNESSIONE AL DATABASE (DatabaseConnection)
 * =================================================================================
 * Questa classe ha un solo scopo: stabilire il collegamento tra l'applicazione Java
 * e il server MySQL. Funziona come un "ponte".
 * 
 * DESIGN PATTERN: SINGLETON
 * Utilizza il pattern architetturale "Singleton". Significa che viene creata 
 * UNA SOLA connessione (Connection connection) e viene riutilizzata da tutto 
 * il programma. Questo è fondamentale per le performance, perché aprire 
 * una nuova connessione al DB è un'operazione lenta e costosa in termini di memoria.
 */
public class DatabaseConnection {
    
    // -----------------------------------------------------------------------------
    // CREDENZIALI E INDIRIZZO DEL DATABASE (Costanti)
    // -----------------------------------------------------------------------------
    // URL: è l'indirizzo a cui Java deve "bussare".
    // jdbc:sqlite:mypatenti.db specifica che stiamo usando SQLite e il file si chiamerà mypatenti.db
    // Verrà creato automaticamente nella root del progetto se non esiste.
    private static final String URL = "jdbc:sqlite:mypatenti.db";
    
    // Oggetto statico che manterrà viva la connessione. Inizialmente è null (nessuna connessione)
    private static Connection connection = null;

    /**
     * METODO getConnection()
     * È il metodo che tutte le altre classi (come UtenteDAO) chiameranno quando
     * hanno bisogno di parlare col database.
     * 
     * @return L'oggetto Connection pronto all'uso.
     */
    public static Connection getConnection() {
        try {
            // Controlla se la connessione è null OPPURE se SQLite l'ha chiusa nel frattempo
            if (connection == null || connection.isClosed()) {
                // Il DriverManager usa l'URL per connettersi al file (o crearlo se non esiste)
                // Con SQLite non servono credenziali (USER/PASSWORD) di default.
                connection = DriverManager.getConnection(URL);
                
                // Se arriva qui senza lanciare eccezioni, significa che il collegamento è riuscito!
                System.out.println("✅ Connessione al database SQLite stabilita con successo!");
            }
        } catch (SQLException e) {
            // Errore scatenato se c'è un problema di permessi col file
            System.err.println("❌ Errore di connessione al database SQLite.");
            e.printStackTrace();
        }
        // Ritorna la connessione
        return connection;
    }

    /**
     * METODO closeConnection()
     * Utile da chiamare alla chiusura definitiva del programma per "spegnere il ponte"
     * e liberare la memoria del computer e le risorse del server MySQL.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                // Chiude fisicamente il collegamento
                connection.close();
                // Rimette la variabile a null, così se in futuro serve di nuovo, verrà ricreata
                connection = null;
                System.out.println("🔒 Connessione al database chiusa in modo sicuro.");
            } catch (SQLException e) {
                System.err.println("❌ Errore durante la chiusura della connessione.");
                e.printStackTrace();
            }
        }
    }
}
