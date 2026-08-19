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
import javafx.scene.control.TextInputDialog; // Dialogo specializzato per l'input di testo singolo
import java.util.Optional;                   // Classe per gestire valori opzionali (null-safe)
import model.SessioneUtente;                 // Singleton che mantiene l'utente loggato in memoria
import model.TemaManager;                    // Singleton per la gestione globale del tema
import model.Utente;                         // POJO con i dati anagrafici dell'utente
import service.UtenteService;                // Service Layer per validazione email
import util.AlertPersonalizzato;             // Dialoghi fissi in stile MyPatenti

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
public class ProfiloController extends BaseController {

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

        // Applica il tema globale salvato (Chiaro/Scuro)
        javafx.application.Platform.runLater(() -> {
            if (btnTornaDashboard != null && btnTornaDashboard.getScene() != null) {
                TemaManager.getInstance().applica(btnTornaDashboard.getScene());
            }
        });
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
        naviga("/view/Dashboard.fxml", "MyPatenti - Dashboard", btnTornaDashboard.getScene());
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
            
            // Controlla che l'email rispetti la validazione regex formale
            if (UtenteService.validaEmail(trimmed)) {
                // Aggiorna la sessione con il nuovo valore
                u.setEmail(trimmed);
                
                // Sincronizza tutte le Label della vista con il nuovo valore
                aggiornaDatiVista();

                // Mostra un Alert di conferma dell'avvenuta modifica
                AlertPersonalizzato.mostraInfo(
                        "Profilo aggiornato",
                        "L'email è stata aggiornata con successo.");
            } else if (!trimmed.isEmpty()) {
                // Se l'email non è vuota ma non è valida, mostra un Alert di errore
                AlertPersonalizzato.mostraErrore(
                        "Email non valida",
                        "Inserisci un'email valida, ad esempio nome@dominio.it.");
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
        naviga("/view/Impostazioni.fxml", "MyPatenti - Impostazioni", btnTornaDashboard.getScene());
    }

    /**
     * GESTORE EVENTO: CLICK SU "LOGOUT" DAL MENU PROFILO
     * Mostra una finestra di dialogo di conferma per chiedere all'allievo se vuole effettuare il logout.
     * Se l'allievo conferma (pulsante "Sì, esci"), ricarica la Schermata Iniziale di benvenuto.
     * Se l'allievo annulla, rimane nella schermata del profilo.
     * 
     * @param event L'evento di azione scatenato dalla selezione di "Logout"
     */
    @FXML
    void gestisciLogout(ActionEvent event) {
        eseguiLogoutConConferma(menuProfilo.getScene());
    }

}
