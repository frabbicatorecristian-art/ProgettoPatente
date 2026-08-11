package controller;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TemaManager;
import java.io.IOException;
import java.util.Optional;

/**
 * =================================================================================
 * CONTROLLER - DASHBOARD PRINCIPALE (Aula Virtuale MyPatenti)
 * =================================================================================
 * Gestisce l'interfaccia principale dell'applicazione dopo che l'allievo ha effettuato l'accesso o la registrazione.
 * 
 * FUNZIONALITÀ PRINCIPALI:
 * 1. Personalizzazione dinamica del messaggio di benvenuto con il nome dell'utente.
 * 2. Menu a tendina del profilo utente (MenuButton) con opzioni (Profilo, Impostazioni, Notifiche, Info, Logout).
 * 3. Navigazione tra le sezioni principali: Le Mie Patenti, I Miei Veicoli, Teoria & Manuale, Quiz.
 * 4. Barra di navigazione inferiore (Tab Bar) con evidenziazione dello stato attivo.
 * 5. Procedura di Logout con finestra di conferma (Alert CONFIRMATION) e ritorno sicuro alla Schermata Iniziale.
 */
public class DashboardController {

    // -----------------------------------------------------------------------------
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // -----------------------------------------------------------------------------
    @FXML private Label lblBenvenuto;          // Etichetta del messaggio di benvenuto centrale
    @FXML private MenuButton menuProfilo;       // Menu a tendina posizionato in alto a destra nella Navbar
    @FXML private Label lblDettaglioSezione;    // Etichetta di dettaglio informativa collocata sopra la Tab Bar
    @FXML private VBox barNavInferiore;         // Barra di navigazione inferiore (Tab Bar)
    
    // Pulsanti della barra di navigazione inferiore (Tab Bar)
    @FXML private Button btnNavHome;           // Scheda Home
    @FXML private Button btnNavPatenti;        // Scheda Patenti
    @FXML private Button btnNavVeicoli;        // Scheda Veicoli
    @FXML private Button btnNavTeoria;         // Scheda Teoria
    @FXML private Button btnNavQuiz;           // Scheda Quiz

    /**
     * METODO INITIALIZE (Inizializzazione automatica JavaFX)
     * Viene eseguito automaticamente da JavaFX subito dopo che il file FXML è stato caricato.
     * Inizializza i dati dinamici dell'utente e imposta lo stato iniziale della vista.
     */
    @FXML
    public void initialize() {
        System.out.println("Dashboard MyPatenti inizializzata.");

        String nomeUtenteLoggato = "Cristian";
        menuProfilo.setText("👤 Ciao, " + nomeUtenteLoggato);
        lblBenvenuto.setText("Bentornato, " + nomeUtenteLoggato + "! 👋");
        apriSezioneHome(null);

        // Applica il tema globale salvato (Chiaro/Scuro)
        javafx.application.Platform.runLater(() ->
            TemaManager.getInstance().applica(menuProfilo.getScene())
        );
    }

    /**
     * METODO HELPER: AGGIORNAMENTO GRAFICO DELLA TAB BAR INFERIORE
     * Cambia lo stile del pulsante attivo evidenziandolo con lo sfondo blu scuro (#0369a1)
     * e ripristina lo stile trasparente per tutti gli altri pulsanti non selezionati.
     * 
     * @param btnAttivo Il pulsante della Tab Bar attualmente selezionato dall'allievo.
     */
    private void aggiornaNavBar(Button btnAttivo) {
        // Stile CSS per le schede inattive (sfondo trasparente, testo bianco)
        String stileInattivo = "-fx-background-color: transparent; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-cursor: hand;";
        
        // Stile CSS per la scheda attiva (sfondo blu scuro evidenziato)
        String stileAttivo = "-fx-background-color: #0369a1; -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-cursor: hand;";

        // Ripristina lo stile inattivo su tutti i pulsanti della navigazione inferiore
        btnNavHome.setStyle(stileInattivo);
        btnNavPatenti.setStyle(stileInattivo);
        btnNavVeicoli.setStyle(stileInattivo);
        btnNavTeoria.setStyle(stileInattivo);
        btnNavQuiz.setStyle(stileInattivo);

        // Applica lo stile evidenziato solo al pulsante della sezione corrente
        btnAttivo.setStyle(stileAttivo);
    }

    // -----------------------------------------------------------------------------
    // AZIONI DEL MENU A TENDINA PROFILO (MenuButton)
    // -----------------------------------------------------------------------------

    @FXML
    void apriProfilo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataProfilo.fxml"));
            Parent root = loader.load();
            Scene scenaAttuale = menuProfilo.getScene();
            scenaAttuale.setRoot(root);
            Stage stage = (Stage) scenaAttuale.getWindow();
            stage.setTitle("MyPatenti - Il mio profilo");
        } catch (IOException e) {
            System.err.println("Errore durante l'apertura del profilo!");
            e.printStackTrace();
        }
    }

    @FXML
    void apriImpostazioni(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataImpostazioni.fxml"));
            Parent root = loader.load();
            Scene scenaAttuale = menuProfilo.getScene();
            scenaAttuale.setRoot(root);
            ((Stage) scenaAttuale.getWindow()).setTitle("MyPatenti - Impostazioni");
        } catch (IOException e) {
            System.err.println("Errore durante l'apertura delle Impostazioni!");
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------------------
    // AZIONI DI NAVIGAZIONE TRA LE SEZIONI DELL'APPLICAZIONE
    // -----------------------------------------------------------------------------

    @FXML
    void apriSezioneHome(ActionEvent event) {
        lblDettaglioSezione.setText("🏠 PANORAMICA DASHBOARD:\nSei nella pagina principale dell'aula virtuale. Monitora le scadenze o inizia un quiz.");
        aggiornaNavBar(btnNavHome);
        if (barNavInferiore != null) {
            // La barra inferiore deve rimanere nascosta nella Home; compare solo nelle sezioni dei pulsanti principali
            barNavInferiore.setVisible(false);
            barNavInferiore.setManaged(false);
        }
    }

    // Overload del metodo per invocazione diretta senza evento
    public void apriSezioneHome() {
        apriSezioneHome(null);
    }

    @FXML
    void apriSezionePatenti(ActionEvent event) {
        lblDettaglioSezione.setText("💳 SEZIONE LE MIE PATENTI:\nDocumenti attivi: Categoria B. Stato: Validata. Punteggio corrente: 20 Punti.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavPatenti);
    }

    @FXML
    void apriSezioneVeicoli(ActionEvent event) {
        lblDettaglioSezione.setText("🚗 SEZIONE VEICOLI:\nRisulta registrato 1 veicolo (Autovettura). Assicurazione e Revisione in stato regolare.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavVeicoli);
    }

    @FXML
    void apriSezioneTeoria(ActionEvent event) {
        lblDettaglioSezione.setText("📖 SEZIONE MANUALE TEORICO:\nSfoglia i 25 capitoli ministeriali del Codice della Strada completi di illustrazioni.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavTeoria);
    }

    @FXML
    void apriSezioneQuiz(ActionEvent event) {
        lblDettaglioSezione.setText("📝 SIMULATORE QUIZ:\nPronto per avviare una nuova simulazione d'esame? 30 domande per 20 minuti totali.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavQuiz);
    }

    /**
     * =============================================================================
     * GESTIONE LOGOUT CON POPUP DI CONFERMA (Alert CONFIRMATION)
     * =============================================================================
     * Mostra una finestra popup chiedendo conferma all'utente prima di uscire.
     * Se l'utente conferma, disconnette l'allievo e riporta la vista alla Schermata Iniziale.
     * 
     * @param event L'evento scatenato dalla selezione di "Esci" dal menu a tendina.
     */
    @FXML
    void gestisciLogout(ActionEvent event) {
        // Inizializza una finestra popup di conferma
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Uscita");
        alert.setHeaderText("Disconnessione dall'Aula Virtuale");
        alert.setContentText("Sei sicuro di voler effettuare il logout e tornare alla pagina iniziale?");

        // Personalizza i pulsanti del dialogo con testo italiano chiaro
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnSiEsci = new ButtonType("Sì, esci", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(btnAnnulla, btnSiEsci);

        // Mostra la finestra di dialogo e gestisce il pulsante premuto dall'utente
        Optional<ButtonType> risultato = alert.showAndWait();
        if (risultato.isPresent() && risultato.get() == btnSiEsci) {
            try {
                // Carica la vista iniziale di benvenuto
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataIniziale.fxml"));
                Parent root = loader.load();

                // Recupera la Scena corrente partendo dal MenuButton 'menuProfilo'
                Scene scenaAttuale = menuProfilo.getScene();
                scenaAttuale.setRoot(root); // Sostituisce il layout grafico

                // Aggiorna il titolo dello Stage
                Stage stage = (Stage) scenaAttuale.getWindow();
                stage.setTitle("MyPatenti - Benvenuto");

                System.out.println("Logout completato con successo. Ritornati alla Schermata Iniziale.");
            } catch (IOException e) {
                System.err.println("Errore critico durante il logout: impossibile caricare SchermataIniziale.fxml");
                e.printStackTrace();
            }
        }
    }
}
