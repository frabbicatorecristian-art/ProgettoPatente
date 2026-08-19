package controller;


// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Gestione degli eventi di azione (click)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.ButtonType;      // Tipo di pulsante nei dialoghi (OK, CANCEL, ecc.)
import javafx.scene.control.Label;           // Componente di testo statico visualizzato a schermo
import javafx.scene.control.MenuButton;      // Componente menu a tendina posizionabile nei layout
import javafx.scene.layout.VBox;             // Layout verticale per impilare componenti verticalmente
import model.SessioneUtente;                 // Singleton che mantiene l'utente loggato in memoria
import model.TemaManager;                    // Gestisce il tema Chiaro/Scuro (usato nell'initialize)

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
public class DashboardController extends BaseController {

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

        // Legge il nome dell'utente loggato dalla sessione globale.
        // In futuro i dati arriveranno direttamente dal database tramite SessioneUtente.
        String nomeUtenteLoggato = "Utente"; // Fallback se la sessione non è disponibile
        if (SessioneUtente.getInstance().isLoggato()) {
            nomeUtenteLoggato = SessioneUtente.getInstance().getUtente().getNome();
        }

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
    void apriImpostazioni(ActionEvent event) {
        naviga("/view/Impostazioni.fxml", "MyPatenti - Impostazioni", menuProfilo.getScene());
    }

    /**
     * GESTORE EVENTO: CLICK SU "IL MIO PROFILO"
     * Naviga alla pagina del profilo utente per visualizzare e modificare le informazioni personali.
     */
    @FXML
    void apriProfilo(ActionEvent event) {
        naviga("/view/Profilo.fxml", "MyPatenti - Il mio profilo", menuProfilo.getScene());
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
        eseguiLogoutConConferma(menuProfilo.getScene());
    }
}
