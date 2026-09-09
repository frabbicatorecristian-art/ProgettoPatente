package controller;


// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;              // Gestione degli eventi di azione (click)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.CheckBox;        // Casella di controllo selezionabile per opzioni (Mostra password)
import javafx.scene.control.ComboBox;        // Menu a tendina per la domanda di sicurezza
import javafx.scene.control.PasswordField;   // Campo di testo speciale con caratteri nascosti (pallini)
import javafx.scene.control.TextField;       // Campo di testo visibile a riga singola
import model.Utente;                         // POJO con i dati anagrafici dell'utente
import service.UtenteService;                // Layer Service per la logica di business e validazione
import util.AlertPersonalizzato;             // Dialoghi fissi in stile MyPatenti

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
    @FXML private ComboBox<String> cmbDomandaSicurezza;         // Menu a tendina per la domanda di sicurezza (facoltativa)
    @FXML private TextField txtRispostaSicurezza;               // Campo per la risposta di sicurezza (facoltativa)
    @FXML private Button btnInviaRegistrazione;                 // Pulsante "CREA ACCOUNT" per confermare l'iscrizione
    @FXML private Button btnAnnulla;                            // Pulsante "ANNULLA" per tornare alla schermata iniziale

    // Istanza del Service Layer per la logica di business
    private final UtenteService utenteService = new UtenteService();

    @FXML
    public void initialize() {
        if (cmbDomandaSicurezza != null) {
            cmbDomandaSicurezza.setItems(FXCollections.observableArrayList(
                "(Facoltativa) Scegli domanda...",
                "Qual è il cognome da nubile di tua madre?",
                "Qual è il nome del tuo primo animale domestico?",
                "In quale città sei nato/a?",
                "Qual è la tua materia preferita a scuola?"
            ));
            cmbDomandaSicurezza.getSelectionModel().selectFirst();
        }
    }

    /**
     * GESTORE EVENTO: MOSTRA / NASCONDI PASSWORD (CheckBox)
     */
    @FXML
    void gestisciMostraPassword(ActionEvent event) {
        if (chkMostraPassword.isSelected()) {
            txtPasswordMostrata.setText(txtPassword.getText());
            txtPasswordMostrata.setVisible(true);
            txtPassword.setVisible(false);

            txtConfermaPasswordMostrata.setText(txtConfermaPassword.getText());
            txtConfermaPasswordMostrata.setVisible(true);
            txtConfermaPassword.setVisible(false);
        } else {
            txtPassword.setText(txtPasswordMostrata.getText());
            txtPassword.setVisible(true);
            txtPasswordMostrata.setVisible(false);

            txtConfermaPassword.setText(txtConfermaPasswordMostrata.getText());
            txtConfermaPassword.setVisible(true);
            txtConfermaPasswordMostrata.setVisible(false);
        }
    }

    @FXML
    void gestisciAnnulla(ActionEvent event) {
        naviga("/view/Home.fxml", "MyPatenti - Benvenuto", btnAnnulla.getScene());
    }

    @FXML
    void gestisciRegistrazione(ActionEvent event) {
        String nome = txtNome.getText().trim();
        String cognome = txtCognome.getText().trim();
        String email = txtEmail.getText().trim();
        String codiceFiscale = txtCodiceFiscale.getText().trim().toUpperCase();
        String password = chkMostraPassword.isSelected() ? txtPasswordMostrata.getText() : txtPassword.getText();
        String confermaPassword = chkMostraPassword.isSelected() ? txtConfermaPasswordMostrata.getText() : txtConfermaPassword.getText();

        String domandaSelezionata = cmbDomandaSicurezza != null ? cmbDomandaSicurezza.getValue() : null;
        String rispostaInserita = txtRispostaSicurezza != null ? txtRispostaSicurezza.getText().trim() : null;

        String domanda = null;
        String risposta = null;

        if (domandaSelezionata != null && !domandaSelezionata.startsWith("(Facoltativa)")) {
            domanda = domandaSelezionata;
            risposta = rispostaInserita;
            if (risposta == null || risposta.isEmpty()) {
                AlertPersonalizzato.mostraErrore("Domanda di Sicurezza", "Hai selezionato una domanda di sicurezza ma non hai inserito la risposta!");
                return;
            }
        }

        Utente nuovoUtente = new Utente(nome, cognome, email, codiceFiscale, domanda, risposta);

        String errore = utenteService.registra(nuovoUtente, password, confermaPassword);

        if (errore != null) {
            AlertPersonalizzato.mostraErrore("Registrazione non valida", errore);
            return;
        }

        System.out.println("Utente registrato nel DB con successo: " + nome + " " + cognome + " [CF: " + codiceFiscale + "]");

        AlertPersonalizzato.mostraInfo("Registrazione completata!", "Il tuo account è stato creato con successo. Ora puoi effettuare l'accesso.");

        naviga("/view/Home.fxml", "MyPatenti - Benvenuto", btnInviaRegistrazione.getScene());
    }
}
