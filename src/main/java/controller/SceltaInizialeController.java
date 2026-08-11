package controller;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent; // Rappresenta un evento generato da un'azione dell'utente (es. click del mouse su un pulsante)
import javafx.fxml.FXML;        // Annotazione che mappa i campi/metodi definiti nel file FXML con il rispettivo codice Java
import javafx.fxml.FXMLLoader;  // Carica ed analizza la struttura dei file XML di layout (.fxml)
import javafx.scene.Parent;      // Nodo radice astratto (es. VBox, AnchorPane) che contiene la gerarchia visiva
import javafx.scene.Scene;       // Contenitore principale della vista grafica associato allo Stage
import javafx.scene.control.Button; // Componente grafico JavaFX per pulsanti cliccabili
import javafx.stage.Stage;     // Finestra di livello superiore dell'applicazione gestita dal sistema operativo
import java.io.IOException;     // Eccezione lanciata nel caso in cui un file FXML non venga trovato o non sia leggibile

/**
 * =================================================================================
 * CONTROLLER - SCHERMATA DI SCELTA INIZIALE (Home / Benvenuto)
 * =================================================================================
 * Questa classe gestisce la prima schermata presentata all'allievo, permettendogli
 * di scegliere se accedere (Login) o registrarsi come nuovo utente (Registrazione).
 */
public class SceltaInizialeController {

    // -----------------------------------------------------------------------------
    // VARIABILI INIETTATE DAL FILE FXML (Mappate tramite fx:id)
    // -----------------------------------------------------------------------------
    
    // Riferimento al pulsante "ACCEDI" presente in SchermataIniziale.fxml (fx:id="btnAccedi")
    @FXML private Button btnAccedi;
    
    // Riferimento al pulsante "REGISTRATI / MIAO" presente in SchermataIniziale.fxml (fx:id="btnRegistrati")
    @FXML private Button btnRegistrati;

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ACCEDI"
     * Invocato automaticamente da JavaFX quando l'utente clicca sul pulsante "ACCEDI" (onAction="#gestisciAccedi").
     * 
     * @param event L'evento di azione scatenato dal click del mouse.
     */
    @FXML
    void gestisciAccedi(ActionEvent event) {
        // Invoca il metodo helper per sostituire il layout della finestra con la Schermata di Login
        caricaSchermata("/view/SchermataLogin.fxml", "Applicazione - Accedi");
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "REGISTRATI / MIAO"
     * Invocato automaticamente da JavaFX quando l'utente clicca sul pulsante di registrazione (onAction="#gestisciRegistrati").
     * 
     * @param event L'evento di azione scatenato dal click del mouse.
     */
    @FXML
    void gestisciRegistrati(ActionEvent event) {
        // Invoca il metodo helper per sostituire il layout della finestra con la Schermata di Registrazione
        caricaSchermata("/view/SchermataRegistrazione.fxml", "Applicazione - Registrati");
    }

    /**
     * =============================================================================
     * METODO HELPER PER IL CAMBIO DI SCHERMATA SENZA FLICKERING
     * =============================================================================
     * Carica il nuovo file FXML e sostituisce semplicemente il nodo radice (root) della Scena 
     * già esistente. Questo approccio evita di chiudere e riaprire lo Stage (finestra),
     * garantendo una transizione visiva istantanea e senza sfarfallio.
     * 
     * @param percorsoFxml Il percorso relativo del file .fxml da caricare (es. "/view/SchermataLogin.fxml").
     * @param titoloFinestra Il nuovo testo da visualizzare sulla barra del titolo della finestra.
     */
    private void caricaSchermata(String percorsoFxml, String titoloFinestra) {
        try {
            // 1. Crea un'istanza di FXMLLoader puntando al file FXML richiesto nella cartella 'resources'
            FXMLLoader loader = new FXMLLoader(getClass().getResource(percorsoFxml));
            
            // 2. Parsa il file XML ed edifica la gerarchia visiva dei componenti JavaFX (Parent root)
            Parent root = loader.load();

            // 3. Recupera la Scena attiva partendo dal riferimento ad uno dei pulsanti presenti a schermo
            Scene scenaAttuale = btnAccedi.getScene();

            // 4. Sostituisce il nodo radice corrente della Scena con il nuovo nodo caricato dal file FXML
            scenaAttuale.setRoot(root);

            // 5. Recupera la finestra (Stage) che ospita la Scena e ne aggiorna il titolo visibile in alto
            Stage stage = (Stage) scenaAttuale.getWindow();
            stage.setTitle(titoloFinestra);

        } catch (IOException e) {
            // In caso di errore (file non trovato o sintassi FXML errata), segnala l'anomalia sui log di errore
            System.err.println("Errore critico durante il caricamento del file FXML: " + percorsoFxml);
            e.printStackTrace(); // Stampa lo stack trace completo per facilitare il debugging
        }
    }
}
