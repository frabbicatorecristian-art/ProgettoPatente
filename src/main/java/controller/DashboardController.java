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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import model.SessioneUtente;                 // Singleton che mantiene l'utente loggato in memoria
import model.TemaManager;                    // Gestisce il tema Chiaro/Scuro (usato nell'initialize)

/**
 * =================================================================================
 * CONTROLLER - DASHBOARD PRINCIPALE (Aula Virtuale MyPatenti)
 * =================================================================================
 * Gestisce l'interfaccia principale dell'applicazione dopo che l'allievo ha effettuato l'accesso o la registrazione.
 * 
 * FUNZIONALITÃ€ PRINCIPALI:
 * 1. Personalizzazione dinamica del messaggio di benvenuto con il nome dell'utente.
 * 2. Menu a tendina del profilo utente (MenuButton) con opzioni (Profilo, Impostazioni, Notifiche, Info, Logout).
 * 3. Navigazione tra le sezioni principali: Le Mie Patenti, I Miei Veicoli, Teoria & Manuale, Quiz.
 * 4. Barra di navigazione inferiore (Tab Bar) con evidenziazione dello stato attivo.
 * 5. Procedura di Logout con finestra di conferma (Alert CONFIRMATION) e ritorno sicuro alla Schermata Iniziale.
 */
public class DashboardController extends BaseController {

    private static DashboardController instance;

    public DashboardController() {
        instance = this;
    }

    public static DashboardController getInstance() {
        return instance;
    }

    public void aggiornaBenvenuto() {
        if (model.SessioneUtente.getInstance().getUtente() != null) {
            String nomeUtente = model.SessioneUtente.getInstance().getUtente().getNome();
            if (menuProfilo != null) menuProfilo.setText("👤 Ciao, " + nomeUtente);
            if (lblBenvenuto != null) lblBenvenuto.setText("Bentornato, " + nomeUtente + "! 👋");
        }
    }
    public void mostraDock(boolean visibile) {
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(visibile);
            barNavInferiore.setManaged(visibile);
        }
    }

    // -----------------------------------------------------------------------------
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // -----------------------------------------------------------------------------
    @javafx.fxml.FXML private javafx.scene.control.Label lblStatPatenti;
    @javafx.fxml.FXML private javafx.scene.control.Label lblStatVeicoli;
    @javafx.fxml.FXML private javafx.scene.control.Label lblStatScadenze;
    @javafx.fxml.FXML private javafx.scene.control.Label lblStatQuiz;
    
    @javafx.fxml.FXML private javafx.scene.control.Label lblBenvenuto;          // Etichetta del messaggio di benvenuto centrale
    @FXML private MenuButton menuProfilo;       // Menu a tendina posizionato in alto a destra nella Navbar
    @FXML private Label lblDettaglioSezione;    // Etichetta di dettaglio informativa collocata sopra la Tab Bar
    @FXML private VBox barNavInferiore;         // Barra di navigazione inferiore (Tab Bar)
    
    // Pulsanti della barra di navigazione inferiore (Tab Bar)
    @FXML private Button btnNavHome;           // Scheda Home
    @FXML private Button btnNavPatenti;        // Scheda Patenti
    @FXML private Button btnNavVeicoli;        // Scheda Veicoli
    @FXML private Button btnNavTeoria;         // Scheda Teoria
    @FXML private Button btnNavQuiz;           // Scheda Quiz
    @FXML
    private VBox contentArea;
    private List<Node> homeNodes;

    /**
     * METODO INITIALIZE (Inizializzazione automatica JavaFX)
     * Viene eseguito automaticamente da JavaFX subito dopo che il file FXML Ã¨ stato caricato.
     * Inizializza i dati dinamici dell'utente e imposta lo stato iniziale della vista.
     */
    @FXML
    public void initialize() {
        System.out.println("Dashboard MyPatenti inizializzata.");
        dao.QuizDAO.creaTabellaSeNonEsiste();
        dao.DomandaDAO.creaTabellaSeNonEsiste();

        // Salva i nodi originali della dashboard per poterli ripristinare tornando alla Home
        homeNodes = new ArrayList<>(contentArea.getChildren());

        // Legge il nome dell'utente loggato dalla sessione globale.
        // In futuro i dati arriveranno direttamente dal database tramite SessioneUtente.
        aggiornaBenvenuto();
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
        // Ripristina lo stile inattivo su tutti i pulsanti della navigazione inferiore rimuovendo la classe attiva
        btnNavHome.getStyleClass().remove("btn-nav-active");
        btnNavPatenti.getStyleClass().remove("btn-nav-active");
        btnNavVeicoli.getStyleClass().remove("btn-nav-active");
        btnNavTeoria.getStyleClass().remove("btn-nav-active");
        btnNavQuiz.getStyleClass().remove("btn-nav-active");

        // Applica lo stile evidenziato solo al pulsante della sezione corrente
        if (!btnAttivo.getStyleClass().contains("btn-nav-active")) {
            btnAttivo.getStyleClass().add("btn-nav-active");
        }
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
        lblDettaglioSezione.setText("ðŸ  PANORAMICA DASHBOARD:\nSei nella pagina principale dell'aula virtuale. Monitora le scadenze o inizia un quiz.");
        aggiornaNavBar(btnNavHome);
        if (barNavInferiore != null) {
            // La barra inferiore deve rimanere nascosta nella Home; compare solo nelle sezioni dei pulsanti principali
            barNavInferiore.setVisible(false);
            barNavInferiore.setManaged(false);
        }
        
        if (homeNodes != null && contentArea != null) {
            contentArea.getChildren().setAll(homeNodes);
            aggiornaStatistiche();
        }
    }

    // Overload del metodo per invocazione diretta senza evento
        private void aggiornaStatistiche() {
        if (model.SessioneUtente.getInstance().getUtente() == null) return;
        String cf = model.SessioneUtente.getInstance().getUtente().getCodiceFiscale();
        
        // 1. Patenti
        model.Patente p = dao.PatenteDAO.getPatenteByCodiceFiscale(cf);
        java.util.List<model.CategoriaPatente> categorie = new java.util.ArrayList<>();
        if (p != null) {
            categorie = dao.PatenteDAO.getCategorieByPatenteId(p.getId());
        }
        if (lblStatPatenti != null) lblStatPatenti.setText(String.valueOf(categorie.size()));
        
        // 2. Veicoli
        java.util.List<model.Veicolo> veicoli = dao.VeicoloDAO.getVeicoliPerUtente(cf);
        if (lblStatVeicoli != null) lblStatVeicoli.setText(String.valueOf(veicoli.size()));
        
        // 3. Scadenze
        int scadenzeProssime = 0;
        for (model.Veicolo v : veicoli) {
            java.time.LocalDate scaAssic = null;
            if (v.getDataAssicurazione() != null) {
                scaAssic = "Annuale".equals(v.getValiditaAssicurazione()) ? 
                    v.getDataAssicurazione().plusYears(1) : v.getDataAssicurazione().plusMonths(6);
            }
            if (scaAssic != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), scaAssic);
                if (days >= 0 && days <= 30) scadenzeProssime++;
            }
            
            java.time.LocalDate scaRev = null;
            if (v.isPrimaRevisioneFatta() && v.getDataUltimaRevisione() != null) {
                scaRev = v.getDataUltimaRevisione().plusYears(2);
            } else if (!v.isPrimaRevisioneFatta() && v.getDataImmatricolazione() != null) {
                scaRev = v.getDataImmatricolazione().plusYears(4);
            }
            if (scaRev != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), scaRev);
                if (days >= 0 && days <= 30) scadenzeProssime++;
            }
        }
        if (lblStatScadenze != null) lblStatScadenze.setText(String.valueOf(scadenzeProssime));
        
        // 4. Quiz
        try {
            int numQuiz = dao.QuizDAO.getQuizByUtente(cf).size();
            if (lblStatQuiz != null) lblStatQuiz.setText(String.valueOf(numQuiz));
        } catch (Exception e) {
            if (lblStatQuiz != null) lblStatQuiz.setText("0");
        }
    }
    
    public void apriSezioneHome() {
        apriSezioneHome(null);
    }

    @FXML
    void apriSezionePatenti(ActionEvent event) {
        lblDettaglioSezione.setText("SEZIONE LE MIE PATENTI:\nConfigura e gestisci i tuoi documenti di guida.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavPatenti);
        
        try {
            javafx.scene.Parent patentiView = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/Patenti.fxml"));
            VBox.setVgrow(patentiView, javafx.scene.layout.Priority.ALWAYS);
            contentArea.getChildren().setAll(patentiView);
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void apriSezioneVeicoli(ActionEvent event) {
        lblDettaglioSezione.setText("ðŸš— SEZIONE VEICOLI:\nRisulta registrato 1 veicolo (Autovettura). Assicurazione e Revisione in stato regolare.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavVeicoli);
        
        try {
            Parent veicoliView = FXMLLoader.load(getClass().getResource("/view/Veicoli.fxml"));
            VBox.setVgrow(veicoliView, javafx.scene.layout.Priority.ALWAYS);
            contentArea.getChildren().setAll(veicoliView);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void apriSezioneTeoria(ActionEvent event) {
        lblDettaglioSezione.setText("ðŸ“– SEZIONE MANUALE TEORICO:\nSfoglia i 25 capitoli ministeriali del Codice della Strada completi di illustrazioni.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavTeoria);
        
        try {
            javafx.scene.Parent teoriaView = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/Teoria.fxml"));
            VBox.setVgrow(teoriaView, javafx.scene.layout.Priority.ALWAYS);
            contentArea.getChildren().setAll(teoriaView);
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
    }

        public void apriVisualizzatoreTeoria(String modulo) {
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(false);
            barNavInferiore.setManaged(false);
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/Visualizzatore.fxml"));
            javafx.scene.Parent view = loader.load();
            controller.VisualizzatoreController vc = loader.getController();
            vc.setModulo(modulo);
            javafx.scene.layout.VBox.setVgrow(view, javafx.scene.layout.Priority.ALWAYS);
            contentArea.getChildren().setAll(view);
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void apriSezioneTeoria() {
        apriSezioneTeoria(null);
    }

    @FXML
    void apriSezioneQuiz(ActionEvent event) {
        lblDettaglioSezione.setText("SIMULATORE QUIZ:\nPronto per avviare una nuova simulazione d'esame? 30 domande per 20 minuti totali.");
        if (barNavInferiore != null) {
            barNavInferiore.setVisible(true);
            barNavInferiore.setManaged(true);
        }
        aggiornaNavBar(btnNavQuiz);
        
        try {
            javafx.scene.Parent quizView = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/Quiz.fxml"));
            VBox.setVgrow(quizView, javafx.scene.layout.Priority.ALWAYS);
            contentArea.getChildren().setAll(quizView);
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void apriSezioneQuiz() {
        apriSezioneQuiz(null);
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

