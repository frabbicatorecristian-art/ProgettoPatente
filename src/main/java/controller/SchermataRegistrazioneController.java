package controller;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Gestione degli eventi di azione (click)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.fxml.FXMLLoader;               // Carica e legge i file FXML della vista
import javafx.scene.Parent;                 // Nodo radice del layout della scena
import javafx.scene.Scene;                  // Scena grafica principale JavaFX
import javafx.scene.control.Alert;           // Finestra di dialogo/notifica per errori
import javafx.scene.control.Alert.AlertType; // Enumerazione dei tipi di Alert (ERROR, ecc.)
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.CheckBox;        // Casella di controllo selezionabile per opzioni (Mostra password)
import javafx.scene.control.PasswordField;   // Campo di testo speciale con caratteri nascosti (pallini)
import javafx.scene.control.TextField;       // Campo di testo visibile a riga singola
import javafx.stage.Stage;                  // Finestra principale gestita dal sistema operativo
import java.io.IOException;                  // Gestione eccezioni di I/O per il caricamento delle viste

/**
 * =================================================================================
 * CONTROLLER - SCHERMATA DI REGISTRAZIONE (Iscrizione Nuovo Utente)
 * =================================================================================
 * Gestisce la creazione e registrazione di un nuovo profilo allievo nell'applicazione MyPatenti.
 * Esegue quattro livelli consecutivi di validazione dei dati di input:
 * 1. Controllo presenza di campi obbligatori vuoti.
 * 2. Convalida formale della lunghezza del Codice Fiscale (esattamente 16 caratteri).
 * 3. Convalida sintattica dell'indirizzo Email (presenza dei simboli '@' e '.').
 * 4. Verifiche di sicurezza e robustezza sulla Password scelta.
 * 5. Accesso diretto alla Dashboard senza schermate/popup di Alert intermedie in caso di esito positivo.
 */
public class SchermataRegistrazioneController {

    // -----------------------------------------------------------------------------
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // -----------------------------------------------------------------------------
    @FXML private TextField txtNome;               // Campo di testo per l'inserimento del Nome
    @FXML private TextField txtCognome;            // Campo di testo per l'inserimento del Cognome
    @FXML private TextField txtEmail;              // Campo di testo per l'inserimento dell'Email
    @FXML private TextField txtCodiceFiscale;      // Campo di testo per l'inserimento del Codice Fiscale (16 caratteri)
    @FXML private PasswordField txtPassword;       // Campo password mascherato (pallini)
    @FXML private TextField txtPasswordMostrata;   // Campo password visibile in chiaro per la modalità "Mostra Password"
    @FXML private CheckBox chkMostraPassword;     // Casella di spunta per mostrare/nascondere la password
    @FXML private Button btnInviaRegistrazione;   // Pulsante "CREA ACCOUNT" per confermare l'iscrizione
    @FXML private Button btnAnnulla;              // Pulsante "ANNULLA" per tornare alla schermata iniziale

    /**
     * GESTORE EVENTO: MOSTRA / NASCONDI PASSWORD (CheckBox)
     * Copia il testo e alterna la visibilità tra il campo protetto e il campo in chiaro.
     * 
     * @param event L'evento generato dalla selezione/deselezione della casella.
     */
    @FXML
    void gestisciMostraPassword(ActionEvent event) {
        if (chkMostraPassword.isSelected()) {
            // SE SELEZIONATA: Copia il testo nel campo visibile e mostra quest'ultimo
            txtPasswordMostrata.setText(txtPassword.getText());
            txtPasswordMostrata.setVisible(true);
            txtPassword.setVisible(false);
        } else {
            // SE DESELEZIONATA: Copia il testo nel campo mascherato e mostra quest'ultimo
            txtPassword.setText(txtPasswordMostrata.getText());
            txtPassword.setVisible(true);
            txtPasswordMostrata.setVisible(false);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataIniziale.fxml"));
            Parent root = loader.load();
            Scene scenaAttuale = btnAnnulla.getScene().getWindow().getScene();
            scenaAttuale.setRoot(root); // Sostituzione istantanea del layout radice
            Stage stage = (Stage) scenaAttuale.getWindow();
            stage.setTitle("MyPatenti - Benvenuto");
        } catch (IOException e) {
            System.err.println("Errore critico durante il ritorno alla schermata iniziale!");
            e.printStackTrace();
        }
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "CREA ACCOUNT"
     * Raccoglie i dati inseriti nei campi del form ed esegue la catena di controlli di validazione.
     * Se i controlli sono superati, entra DIRETTAMENTE nella Dashboard senza mostrare alcun Alert di successo.
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

        // CONTROLLO 1: Tutti i campi sono obbligatori per completare la registrazione
        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || codiceFiscale.isEmpty() || password.isEmpty()) {
            mostraAlertErrore("Campi Incompleti", "Tutti i campi sono obbligatori!", "Per favore, compila ogni sezione del modulo di iscrizione.");
            return; // Interrompe l'iscrizione
        }

        // CONTROLLO 2: Validazione formale della lunghezza del Codice Fiscale (esattamente 16 caratteri)
        if (codiceFiscale.length() != 16) {
            mostraAlertErrore("Codice Fiscale Errato", "Lunghezza del Codice Fiscale non valida.", "Il codice fiscale deve essere composto esattamente da 16 caratteri alfanumerici.");
            return; // Interrompe l'iscrizione
        }

        // CONTROLLO 3: Validazione sintattica elementare dell'indirizzo Email
        if (!email.contains("@") || !email.contains(".")) {
            mostraAlertErrore("Email Non Valida", "L'indirizzo email inserito non è corretto.", "Inserisci un'email valida per poter ricevere le notifiche da MyPatenti.");
            return; // Interrompe l'iscrizione
        }

        // CONTROLLO 4: Verifica dei requisiti di sicurezza sulla Password
        String validazionePassword = validaPassword(password);
        if (validazionePassword != null) {
            mostraAlertErrore("Password Debole", "La password non è sufficientemente forte!", validazionePassword);
            return; // Interrompe l'iscrizione
        }

        System.out.println("Utente registrato con successo: " + nome + " " + cognome + " [CF: " + codiceFiscale + "]");

        // CARICAMENTO DIRETTO E FLUIDO DELLA DASHBOARD MYPATENTI (Senza Alert intermedi)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataDashboard.fxml"));
            Parent root = loader.load();
            Scene scenaAttuale = btnInviaRegistrazione.getScene();
            scenaAttuale.setRoot(root); // Visualizza la Dashboard dell'allievo
            Stage stage = (Stage) scenaAttuale.getWindow();
            stage.setTitle("MyPatenti - Dashboard");
        } catch (IOException e) {
            System.err.println("Errore critico durante il caricamento della Dashboard!");
            e.printStackTrace();
        }
    }

    /**
     * METODO HELPER PER MOSTRARE POPUP DI ERRORE
     */
    private void mostraAlertErrore(String titolo, String intestazione, String contenuto) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(intestazione);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }

    /**
     * VALIDATORE DELLA ROBUSTEZZA DELLA PASSWORD
     * Criteri richiesti:
     * - Minimo 8 caratteri.
     * - Almeno una lettera maiuscola.
     * - Almeno un carattere speciale (!@#$%^&* ecc.).
     * 
     * @param password La stringa password da verificare.
     * @return null se valida, altrimenti la descrizione dell'errore.
     */
    private String validaPassword(String password) {
        if (password.length() < 8) {
            return "La password deve contenere almeno 8 caratteri.";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "La password deve contenere almeno una lettera maiuscola (A-Z).";
        }

        if (!password.matches(".*[!@#$%^&*()_+=\\-\\[\\]{};:'\",.<>?/\\\\|`~].*")) {
            return "La password deve contenere almeno un carattere speciale (!@#$%^&* ecc.).";
        }

        return null; // Password valida
    }
}
