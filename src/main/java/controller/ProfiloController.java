package controller;


// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Gestione degli eventi di azione (click)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.ButtonType;      // Tipo di pulsante nei dialoghi (OK, CANCEL, ecc.)
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.geometry.Insets;
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
    @FXML private Button btnTema;

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
                boolean isScuro = TemaManager.getInstance().isTemaScuro();
                btnTema.setText(isScuro ? "🌞 TEMA CHIARO" : "🌙 TEMA SCURO");
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
        tornaIndietro(btnTornaDashboard.getScene());
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
     */
    @FXML
    void gestisciModificaProfilo(ActionEvent event) {
        Utente u = SessioneUtente.getInstance().getUtente();
        if (u == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifica Dati");
        dialog.setHeaderText("Aggiorna i tuoi dati o cambia password");

        ButtonType btnSalva = new ButtonType("✓ Salva Modifiche", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnElimina = new ButtonType("🗑 Elimina Account", ButtonBar.ButtonData.LEFT);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(btnSalva, btnElimina, btnAnnulla);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(12);
        grid.setVgap(14);
        grid.setPadding(new Insets(20, 30, 10, 20));

        javafx.scene.control.TextField txtNome = new javafx.scene.control.TextField(u.getNome());
        javafx.scene.control.TextField txtCognome = new javafx.scene.control.TextField(u.getCognome());
        javafx.scene.control.TextField txtEmail = new javafx.scene.control.TextField(u.getEmail());
        javafx.scene.control.PasswordField txtPassword = new javafx.scene.control.PasswordField();
        javafx.scene.control.PasswordField txtConferma = new javafx.scene.control.PasswordField();
        javafx.scene.control.TextField tfPassword = new javafx.scene.control.TextField();
        javafx.scene.control.TextField tfConferma = new javafx.scene.control.TextField();

        txtNome.setPrefWidth(240);
        txtCognome.setPrefWidth(240);
        txtEmail.setPrefWidth(240);
        txtPassword.setPrefWidth(240);
        txtConferma.setPrefWidth(240);
        tfPassword.setPrefWidth(240);
        tfConferma.setPrefWidth(240);

        txtPassword.setPromptText("Lascia vuoto per non cambiare");
        txtConferma.setPromptText("Conferma nuova password");
        tfPassword.setPromptText("Lascia vuoto per non cambiare");
        tfConferma.setPromptText("Conferma nuova password");

        tfPassword.setVisible(false); tfPassword.setManaged(false);
        tfConferma.setVisible(false); tfConferma.setManaged(false);

        tfPassword.textProperty().bindBidirectional(txtPassword.textProperty());
        tfConferma.textProperty().bindBidirectional(txtConferma.textProperty());

        javafx.scene.layout.StackPane stackPassword = new javafx.scene.layout.StackPane(txtPassword, tfPassword);
        javafx.scene.layout.StackPane stackConferma = new javafx.scene.layout.StackPane(txtConferma, tfConferma);

        javafx.scene.control.CheckBox chkMostra = new javafx.scene.control.CheckBox("Mostra password");
        chkMostra.setOnAction(e -> {
            boolean mostra = chkMostra.isSelected();
            tfPassword.setVisible(mostra); tfPassword.setManaged(mostra);
            txtPassword.setVisible(!mostra); txtPassword.setManaged(!mostra);
            tfConferma.setVisible(mostra); tfConferma.setManaged(mostra);
            txtConferma.setVisible(!mostra); txtConferma.setManaged(!mostra);
        });

        grid.add(new Label("Nome:"), 0, 0); grid.add(txtNome, 1, 0);
        grid.add(new Label("Cognome:"), 0, 1); grid.add(txtCognome, 1, 1);
        grid.add(new Label("Email:"), 0, 2); grid.add(txtEmail, 1, 2);
        grid.add(new Label("Nuova Password:"), 0, 3); grid.add(stackPassword, 1, 3);
        grid.add(new Label("Conferma Password:"), 0, 4); grid.add(stackConferma, 1, 4);
        grid.add(chkMostra, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Button btnEliminaNode = (Button) dialog.getDialogPane().lookupButton(btnElimina);
        btnEliminaNode.setStyle("-fx-text-fill: red;");
        btnEliminaNode.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            e.consume(); // Previene la chiusura del dialog
            AlertPersonalizzato.mostraConfermaDistruttiva(
                "Elimina account",
                "Sei sicuro di voler eliminare definitivamente il tuo account?",
                "Sì, elimina").ifPresent(r -> {
                    if (r == ButtonType.OK) {
                        UtenteService service = new UtenteService();
                        if (service.eliminaAccount(u.getCodiceFiscale())) {
                            SessioneUtente.getInstance().logout();
                            dialog.close();
                            naviga("/view/Home.fxml", "MyPatenti - Benvenuto", btnTornaDashboard.getScene());
                        } else {
                            AlertPersonalizzato.mostraErrore("Errore", "Impossibile eliminare l'account.");
                        }
                    }
                });
        });

        dialog.showAndWait().ifPresent(risposta -> {
            if (risposta == btnSalva) {
                String nome = txtNome.getText().trim();
                String cognome = txtCognome.getText().trim();
                String email = txtEmail.getText().trim();
                String pwd = txtPassword.getText().trim();
                String pwdConf = txtConferma.getText().trim();

                if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty()) {
                    AlertPersonalizzato.mostraErrore("Dati mancanti", "Nome, cognome ed email sono obbligatori.");
                    return;
                }
                if (!UtenteService.validaEmail(email)) {
                    AlertPersonalizzato.mostraErrore("Email non valida", "Inserisci un'email valida.");
                    return;
                }

                u.setNome(nome);
                u.setCognome(cognome);
                u.setEmail(email);

                service.UtenteService service = new service.UtenteService();
                String errDati = service.aggiornaDatiUtente(u);
                if (errDati != null) {
                    util.AlertPersonalizzato.mostraErrore("Errore Salvataggio", errDati);
                    return;
                }

                if (!pwd.isEmpty()) {
                    String err = service.cambiaPassword(u.getCodiceFiscale(), pwd, pwdConf);
                    if (err != null) {
                        util.AlertPersonalizzato.mostraErrore("Errore password", err);
                        return;
                    }
                }

                aggiornaDatiVista();
                DashboardController.getInstance().aggiornaBenvenuto();
                AlertPersonalizzato.mostraInfo("Profilo aggiornato", "I tuoi dati sono stati aggiornati con successo.");
            }
        });
    }

    /**
     * GESTORE EVENTO: CAMBIO TEMA
     */
    @FXML
    void cambiaTema(ActionEvent event) {
        boolean nuovoStato = !TemaManager.getInstance().isTemaScuro();
        TemaManager.getInstance().setTemaScuro(nuovoStato);
        TemaManager.getInstance().applica(btnTema.getScene());
        btnTema.setText(nuovoStato ? "🌞 TEMA CHIARO" : "🌙 TEMA SCURO");
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
