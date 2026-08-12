package org.example;

// =================================================================================
// IMPORT DELLE CLASSI FONDAMENTALI DI JAVAFX
// =================================================================================
import javafx.application.Application; // Classe astratta radice per tutte le applicazioni grafiche JavaFX
import javafx.fxml.FXMLLoader;        // Classe responsabile del parsing e del caricamento dei file di layout FXML
import javafx.scene.Parent;            // Classe base (superclasse) per tutti i nodi visivi della gerarchia (Scene Graph)
import javafx.scene.Scene;             // Contenitore della scena grafica che ospita l'albero dei nodi visivi
import javafx.scene.image.Image;       // Gestione delle immagini visive e icone della finestra
import javafx.stage.Stage;             // Finestra principale di livello superiore gestita dal sistema operativo

/**
 * =================================================================================
 * CLASSE PRINCIPALE JAVAFX (MainApp)
 * =================================================================================
 * Questa classe estende 'Application' e costituisce il cuore dell'inizializzazione
 * dell'interfaccia utente dell'applicazione MyPatenti.
 * 
 * CICLO DI VITA JAVAFX:
 * 1. init(): (Opzionale) Inizializza le risorse non grafiche.
 * 2. start(Stage stage): Inizializza e mostra la finestra e la prima scena grafica.
 * 3. stop(): (Opzionale) Eseguito automaticamente alla chiusura della finestra per pulire le risorse.
 */
public class MainApp extends Application {

    /**
     * Metodo astratto ereditato da Application che DEVE essere sovrascritto (@Override).
     * Rappresenta il punto d'ingresso per la costruzione dell'interfaccia grafica.
     * 
     * @param primaryStage Lo 'Stage' (finestra di primo livello) creato e fornito automaticamente dall'ambiente JavaFX.
     * @throws Exception Gestisce eventuali errori durante la lettura o il parsing dei file FXML e CSS.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        
        // -------------------------------------------------------------------------
        // PASSAGGIO 1: CARICAMENTO DEL LAYOUT GRAFICO FXML
        // -------------------------------------------------------------------------
        // FXMLLoader: Oggetto che legge il file FXML specificato e converte i suoi tag XML
        // in istanze di oggetti JavaFX reali (VBox, Button, Label, ecc.).
        // getClass().getResource("/view/SchermataIniziale.fxml") individua il file FXML nella cartella 'resources'.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataIniziale.fxml"));
        
        // loader.load(): Esegue la lettura del file FXML e restituisce il nodo radice dell'interfaccia (in questo caso un VBox).
        Parent root = loader.load();

        // -------------------------------------------------------------------------
        // PASSAGGIO 2: CREAZIONE E CONFIGURAZIONE DELLA SCENA (Scene)
        // -------------------------------------------------------------------------
        // La 'Scene' è il contenitore intermedio tra lo 'Stage' (finestra) e i nodi grafici (root).
        Scene scene = new Scene(root);
        
        // Collega il foglio di stile CSS globale (style.css) alla scena per applicare colori, font e bordi.
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // -------------------------------------------------------------------------
        // PASSAGGIO 3: CONFIGURAZIONE DELLA FINESTRA (Stage) E VISUALIZZIONE
        // -------------------------------------------------------------------------
        // Imposta il testo visibile nella barra del titolo della finestra del sistema operativo.
        primaryStage.setTitle("MyPatenti - Quiz Patente");
        
        // Imposta l'icona personalizzata della finestra dell'applicazione (automobile sport-car.png)
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/sport-car.png")));
        
        // Assegna la scena creata alla finestra principale.
        primaryStage.setScene(scene);
        
        // Mostra la finestra a schermo (rende visibile l'interfaccia utente).
        primaryStage.show();
    }

}
