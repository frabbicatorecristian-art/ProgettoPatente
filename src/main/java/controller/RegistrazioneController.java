package controller;


// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Gestione degli eventi di azione (click)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.CheckBox;        // Casella di controllo selezionabile per opzioni (Mostra password)
import javafx.scene.control.PasswordField;   // Campo di testo speciale con caratteri nascosti (pallini)
import javafx.scene.control.TextField;       // Campo di testo visibile a riga singola
import model.Utente;                         // POJO con i dati anagrafici dell'utente
import service.UtenteService;                // Layer Service per la logica di business e validazione
import util.AlertPersonalizzato;             // Dialoghi fissi in stile MyPatenti

/**
 * =================================================================================
 * CONTROLLER - SCHERMATA DI REGISTRAZIONE (Iscrizione Nuovo Utente)
 * =================================================================================
 * Gestisce la creazione e registrazione di un nuovo profilo allievo nell'applicazione MyPatenti.
 * Delega la validazione e la persistenza al layer UtenteService:
 * 1. Controllo presenza di campi obbligatori vuoti.
 * 2. Convalida formale della lunghezza del Codice Fiscale (esattamente 16 caratteri).
 * 3. Convalida dell'indirizzo Email tramite espressione regolare (Regex).
 * 4. Verifica di corrispondenza tra Password e Conferma Password.
 * 5. Verifiche di sicurezza e robustezza sulla Password scelta.
 */
public class RegistrazioneController extends BaseController {

    // -----------------------------------------------------------------------------
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // -----------------------------------------------------------------------------
    @FXML private TextField txtNome;                             // Campo di testo per l'inserimento del Nome
    @FXML private TextField txtCognome;                          // Campo di testo per l'inserimento del Cognome
    @FXML private TextField txtEmail;                            // Campo di testo per l'inserimento dell'Email
    @FXML private TextField txtCodiceFiscale;                    // Campo di testo per l'inserimento del Codice Fiscale (16 caratteri)
    @FXML private PasswordField txtPassword;                     // Campo password mascherato (pallini)
    @FXML private TextField txtPasswordMostrata;                 // Campo password visibile in chiaro
    @FXML private PasswordField txtConfermaPassword;             // Campo conferma password mascherato (pallini)
    @FXML private TextField txtConfermaPasswordMostrata;         // Campo conferma password visibile in chiaro
    @FXML private CheckBox chkMostraPassword;                   // Casella di spunta per mostrare/nascondere la password
    @FXML private Button btnInviaRegistrazione;                 // Pulsante "CREA ACCOUNT" per confermare l'iscrizione
    @FXML private Button btnAnnulla;                            // Pulsante "ANNULLA" per tornare alla schermata iniziale

    // Istanza del Service Layer per la logica di business
    private final UtenteService utenteService = new UtenteService();

    /**
     * GESTORE EVENTO: MOSTRA / NASCONDI PASSWORD (CheckBox)
     * Copia il testo e alterna la visibilità tra i campi protetti e i campi in chiaro per password e conferma.
     * 
     * @param event L'evento generato dalla selezione/deselezione della casella.
     */
    @FXML
    void gestisciMostraPassword(ActionEvent event) {
        if (chkMostraPassword.isSelected()) {
            // SE SELEZIONATA: Copia il testo nei campi visibili e mostra questi ultimi
            txtPasswordMostrata.setText(txtPassword.getText());
            txtPasswordMostrata.setVisible(true);
            txtPassword.setVisible(false);

            txtConfermaPasswordMostrata.setText(txtConfermaPassword.getText());
            txtConfermaPasswordMostrata.setVisible(true);
            txtConfermaPassword.setVisible(false);
        } else {
            // SE DESELEZIONATA: Copia il testo nei campi mascherati e mostra questi ultimi
            txtPassword.setText(txtPasswordMostrata.getText());
            txtPassword.setVisible(true);
            txtPasswordMostrata.setVisible(false);

            txtConfermaPassword.setText(txtConfermaPasswordMostrata.getText());
            txtConfermaPassword.setVisible(true);
            txtConfermaPasswordMostrata.setVisible(false);
        }
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ANNULLA"
     * Annulla l'operazione di iscrizione e ripristina la schermata iniziale di benvenuto (SchermataIniziale.fxml).
     * 
     * @param event L'evento generato dal click del mouse.
     */
    @FXML
    void gestisciAnnulla(ActionEvent event) {
        naviga("/view/Home.fxml", "MyPatenti - Benvenuto", btnAnnulla.getScene());
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "CREA ACCOUNT"
     * Raccoglie i dati inseriti nei campi del form ed invoca il Service Layer per la validazione e il salvataggio.
     * In caso di successo, notifica l'utente e lo riporta alla schermata iniziale.
     * 
     * @param event L'evento generato dal click sul pulsante "CREA ACCOUNT".
     */
    @FXML
    void gestisciRegistrazione(ActionEvent event) {
        // Estrazione e pulizia dei dati inseriti rimuovendo eventuali spazi di bordo con trim()
        String nome = txtNome.getText().trim();
        String cognome = txtCognome.getText().trim();
        String email = txtEmail.getText().trim();
        String codiceFiscale = txtCodiceFiscale.getText().trim().toUpperCase(); // Codice fiscale sempre in maiuscolo
        String password = chkMostraPassword.isSelected() ? txtPasswordMostrata.getText() : txtPassword.getText();
        String confermaPassword = chkMostraPassword.isSelected() ? txtConfermaPasswordMostrata.getText() : txtConfermaPassword.getText();

        Utente nuovoUtente = new Utente(nome, cognome, email, codiceFiscale);

        // -----------------------------------------------------------------------
        // DELEGA AL SERVICE LAYER (Validazione + Registrazione DB)
        // -----------------------------------------------------------------------
        String errore = utenteService.registra(nuovoUtente, password, confermaPassword);

        if (errore != null) {
            AlertPersonalizzato.mostraErrore("Registrazione non valida", errore);
            return;
        }

        System.out.println("Utente registrato nel DB con successo: " + nome + " " + cognome + " [CF: " + codiceFiscale + "]");

        // Mostriamo un messaggio di successo prima di rimandarlo alla home
        AlertPersonalizzato.mostraInfo("Registrazione completata!", "Il tuo account è stato creato con successo. Ora puoi effettuare l'accesso.");

        // Ritorniamo alla schermata iniziale (Home) chiedendo all'utente di fare login manualmente
        naviga("/view/Home.fxml", "MyPatenti - Benvenuto", btnInviaRegistrazione.getScene());
    }
}
