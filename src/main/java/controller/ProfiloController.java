package controller;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Gestione degli eventi di azione (click)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.fxml.FXMLLoader;               // Carica e legge i file FXML della vista
import javafx.scene.Parent;                 // Nodo radice del layout della scena
import javafx.scene.Scene;                  // Scena grafica principale JavaFX
import javafx.scene.control.Alert;           // Finestra di dialogo/notifica per errori e conferme
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.ButtonBar;       // Classe per la gestione dei pulsanti nei dialoghi
import javafx.scene.control.ButtonType;      // Tipo di pulsante nei dialoghi (OK, CANCEL, ecc.)
import javafx.scene.control.Label;           // Componente di testo statico visualizzato a schermo
import javafx.scene.control.MenuButton;      // Componente menu a tendina posizionabile nei layout
import javafx.scene.control.TextInputDialog; // Dialogo specializzato per l'input di testo singolo
import javafx.stage.Stage;                  // Finestra principale gestita dal sistema operativo
import java.io.IOException;                  // Gestione eccezioni di I/O per il caricamento delle viste
import java.util.Optional;                   // Classe per gestire valori opzionali (null-safe)
import model.SessioneUtente;                 // Singleton che mantiene l'utente loggato in memoria
import model.Utente;                         // POJO con i dati anagrafici dell'utente

/**
 * =================================================================================
 * CONTROLLER - SCHERMATA IL MIO PROFILO
 * =================================================================================
 * Gestisce la visualizzazione e la modifica dei dati anagrafici dell'utente loggato.
 * Permette all'allievo di visualizzare il profilo personale con nome, cognome, email e data iscrizione.
 * Offre la possibilità di modificare l'indirizzo email tramite dialogo TextInputDialog.
 * Fornisce accesso al menu profilo con opzioni per Impostazioni e Logout.
 * I dati attuali sono statici; in futuro verranno letti dal database backend.
 */
public class ProfiloController {

    // =====================================================================
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // =====================================================================
    @FXML private Label lblNomeCompleto;   // Etichetta che visualizza nome e cognome completi (es. "Cristian Fabbricatore")
    @FXML private Label lblRuolo;          // Etichetta che mostra il ruolo dell'utente ("Utente MyPatenti")
    @FXML private Label lblNome;           // Etichetta che visualizza il nome dell'allievo
    @FXML private Label lblCognome;        // Etichetta che visualizza il cognome dell'allievo
    @FXML private Label lblEmail;          // Etichetta che visualizza l'indirizzo email dell'allievo
    @FXML private Label lblDataIscrizione; // Etichetta che mostra la data di registrazione al servizio
    @FXML private MenuButton menuProfilo;  // Menu a tendina collocato nella navbar per accedere a Profilo, Impostazioni, Logout
    @FXML private Button btnTornaDashboard;// Pulsante di navigazione per ritornare alla Dashboard principale
    @FXML private Button btnModifica;      // Pulsante che apre il dialogo di modifica email
    @FXML private Button btnTornaIndietro; // Pulsante per tornare indietro (navigazione browser-like)

    // =====================================================================
    // DATI UTENTE — letti dalla SessioneUtente (non più hardcodati)
    // =====================================================================
    // I dati vengono recuperati in initialize() tramite SessioneUtente.getInstance().getUtente()

    /**
     * METODO INITIALIZE (Hook automatico JavaFX)
     * Eseguito automaticamente da JavaFX subito dopo che il file FXML è stato caricato.
     * Popola tutte le Label della vista con i dati dell'allievo loggato.
     */
    @FXML
    public void initialize() {
        // Popola tutti i campi del profilo con i dati utente salvati
        aggiornaDatiVista();
    }

    /**
     * METODO HELPER: AGGIORNAMENTO GRAFICO DEI DATI PROFILO
     * Sincronizza tutti i componenti Label con i dati dell'allievo memorizzati nelle variabili statiche.
     * Inoltre aggiorna il testo del MenuButton con il saluto personalizzato.
     */
    private void aggiornaDatiVista() {
        // Recupera l'utente loggato dalla sessione globale
        Utente u = SessioneUtente.getInstance().getUtente();
        if (u == null) return; // Protezione: nessuna sessione attiva

        // Aggiorna l'etichetta del nome completo (es. "Mario Rossi")
        lblNomeCompleto.setText(u.getNomeCompleto());
        
        // Aggiorna l'etichetta del ruolo dell'utente
        lblRuolo.setText("Utente MyPatenti");
        
        // Aggiorna i singoli campi nome, cognome, email e data iscrizione
        lblNome.setText(u.getNome());
        lblCognome.setText(u.getCognome());
        lblEmail.setText(u.getEmail());
        lblDataIscrizione.setText(u.getDataIscrizione());
        
        // Aggiorna il testo del MenuButton con il saluto personalizzato
        if (menuProfilo != null) {
            menuProfilo.setText("👤 Ciao, " + u.getNome());
        }
    }

    // =====================================================================
    // AZIONI DI NAVIGAZIONE PRINCIPALE
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "TORNA ALLA DASHBOARD"
     * Carica la schermata della Dashboard principale (SchermataDashboard.fxml).
     * Sostituisce il nodo radice della Scena per visualizzare istantaneamente la nuova schermata.
     * 
     * @param event L'evento scatenato dal click del mouse sul pulsante "Torna Alla Dashboard".
     */
    @FXML
    void tornaAllaDashboard(ActionEvent event) {
        try {
            // Inizializza un caricatore per il file FXML della Dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataDashboard.fxml"));
            
            // Parsa il file FXML e costruisce la gerarchia visiva dei componenti
            Parent root = loader.load();
            
            // Recupera la Scena corrente dal pulsante e sostituisce il nodo radice
            Scene scena = btnTornaDashboard.getScene();
            scena.setRoot(root);
            
            // Aggiorna il titolo della finestra (Stage) per riflettere la pagina attuale
            ((Stage) scena.getWindow()).setTitle("MyPatenti - Dashboard");
        } catch (IOException e) {
            // Segnala nel log di errore se il caricamento della Dashboard fallisce
            System.err.println("Errore durante il ritorno alla Dashboard!");
            e.printStackTrace();
        }
    }

    /**
     * GESTORE EVENTO: PLACEHOLDER PER MENU PROFILO
     * Questo metodo è un placeholder in quanto l'utente è già nella schermata del profilo.
     * Non esegue alcuna azione.
     * 
     * @param event L'evento di azione (non utilizzato).
     */
    @FXML
    void apriProfiloMenu(ActionEvent event) { 
        // Già nella schermata profilo — nessuna azione richiesta
    }

    // =====================================================================
    // AZIONI DI MODIFICA DATI PROFILO
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "MODIFICA PROFILO"
     * Apre un dialogo TextInputDialog che consente all'allievo di modificare il suo indirizzo email.
     * Valida il nuovo indirizzo prima di applicare la modifica:
     * - Controlla che l'email non sia vuota.
     * - Verifica la presenza del simbolo '@' (domain separator).
     * - Verifica la presenza del simbolo '.' (TLD separator).
     * Se la validazione ha esito positivo, aggiorna il dato e mostra un Alert di conferma.
     * In caso di errore, mostra un Alert di errore con la spiegazione.
     * 
     * @param event L'evento scatenato dal click del mouse.
     */
    @FXML
    void gestisciModificaProfilo(ActionEvent event) {
        Utente u = SessioneUtente.getInstance().getUtente();
        if (u == null) return;

        // Crea un dialogo di input con il valore corrente dell'email pre-compilato
        TextInputDialog dialog = new TextInputDialog(u.getEmail());
        dialog.setTitle("Modifica Email");
        dialog.setHeaderText("Aggiorna il tuo indirizzo email");
        dialog.setContentText("Nuova email:");

        // Mostra il dialogo e cattura il risultato fornito dall'allievo
        Optional<String> result = dialog.showAndWait();
        
        // Se l'allievo ha fatto clic su OK (non su Annulla)
        result.ifPresent(nuovaEmail -> {
            // Rimuove gli spazi bianchi di inizio e fine
            String trimmed = nuovaEmail.trim();
            
            // Controlla che l'email non sia vuota e contenga i simboli richiesti
            if (!trimmed.isEmpty() && trimmed.contains("@") && trimmed.contains(".")) {
                // Aggiorna la sessione con il nuovo valore (in futuro: anche UPDATE al DB)
                // TODO DB: DBService.aggiornaEmail(u.getCodiceFiscale(), trimmed);
                u.setEmail(trimmed);
                
                // Sincronizza tutte le Label della vista con il nuovo valore
                aggiornaDatiVista();

                // Mostra un Alert di conferma dell'avvenuta modifica
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Profilo aggiornato");
                ok.setHeaderText(null);
                ok.setContentText("Email aggiornata con successo!");
                ok.showAndWait();
            } else if (!trimmed.isEmpty()) {
                // Se l'email non è vuota ma non è valida, mostra un Alert di errore
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Email non valida");
                err.setHeaderText("Formato email non corretto");
                err.setContentText("Inserisci un'email valida (es. nome@dominio.it).");
                err.showAndWait();
            }
            // Se l'email è vuota, non mostra alcun messaggio (l'utente ha cancellato)
        });
    }

    // =====================================================================
    // AZIONI DEL MENU PROFILO (Impostazioni, Logout, ecc.)
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SU "IMPOSTAZIONI" DAL MENU PROFILO
     * Carica la schermata Impostazioni (SchermataImpostazioni.fxml) e la visualizza.
     * Questa schermata permette all'allievo di personalizzare il tema (Chiaro/Scuro) e altre preferenze.
     * 
     * @param event L'evento di azione scatenato dalla selezione di "Impostazioni" nel menu.
     */
    @FXML
    void apriImpostazioni(ActionEvent event) {
        try {
            // Carica il file FXML della schermata Impostazioni
            Parent root = FXMLLoader.load(getClass().getResource("/view/SchermataImpostazioni.fxml"));
            
            // Recupera la Scena corrente e sostituisce il nodo radice
            Scene scena = btnTornaDashboard.getScene();
            scena.setRoot(root);
            
            // Aggiorna il titolo della finestra (Stage)
            ((Stage) scena.getWindow()).setTitle("MyPatenti - Impostazioni");
        } catch (IOException e) {
            // Segnala l'errore nel log di console se il caricamento fallisce
            System.err.println("Errore apertura Impostazioni!");
            e.printStackTrace();
        }
    }

    /**
     * GESTORE EVENTO: CLICK SU "LOGOUT" DAL MENU PROFILO
     * Mostra una finestra di dialogo di conferma per chiedere all'allievo se vuole effettuare il logout.
     * Se l'allievo conferma (pulsante "Sì, esci"), ricarica la Schermata Iniziale di benvenuto.
     * Se l'allievo annulla, rimane nella schermata del profilo.
     * 
     * @param event L'evento di azione scatenato dalla selezione di "Logout" nel menu.
     */
    @FXML
    void gestisciLogout(ActionEvent event) {
        // Crea un Alert di tipo CONFIRMATION (con pulsanti Sì/No)
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma Uscita");
        alert.setHeaderText("Disconnessione dall'Aula Virtuale");
        alert.setContentText("Sei sicuro di voler effettuare il logout?");

        // Personalizza i pulsanti del dialogo con etichette in italiano
        ButtonType annulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType esci = new ButtonType("Sì, esci", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(annulla, esci);

        // Mostra il dialogo e gestisce la risposta dell'allievo
        alert.showAndWait().ifPresent(risposta -> {
            if (risposta == esci) {
                try {
                    // Carica la Schermata Iniziale di benvenuto
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataIniziale.fxml"));
                    Parent root = loader.load();
                    
                    // Recupera la Scena e sostituisce il nodo radice
                    Scene scena = menuProfilo.getScene();
                    scena.setRoot(root);
                    
                    // Aggiorna il titolo della finestra per riflettere il cambio di pagina
                    ((Stage) scena.getWindow()).setTitle("MyPatenti - Benvenuto");
                } catch (IOException e) {
                    // Segnala nel log se il logout fallisce
                    System.err.println("Errore durante il logout!");
                    e.printStackTrace();
                }
            }
            // Se l'allievo clicca "Annulla", il dialogo si chiude e rimane nella schermata del profilo
        });
    }
}
