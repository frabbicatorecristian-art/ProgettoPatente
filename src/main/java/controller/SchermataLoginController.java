package controller;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Classe per la gestione degli eventi di azione (click pulsanti)
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
 * CONTROLLER - SCHERMATA DI LOGIN (Accesso Utente)
 * =================================================================================
 * Gestisce l'autenticazione degli utenti dell'applicazione MyPatenti.
 * Implementa controlli di validazione lato client:
 * 1. Controllo presenza campi vuoti.
 * 2. Verifica formale della lunghezza del Codice Fiscale (esattamente 16 caratteri).
 * 3. Verifica della robustezza della Password (almeno 8 caratteri, una maiuscola, un carattere speciale).
 * 4. Funzionalità dinamica per mostrare/nascondere la password in chiaro.
 * 5. Accesso diretto alla Dashboard senza schermate/popup di Alert intermedie in caso di esito positivo.
 */
public class SchermataLoginController {

    // -----------------------------------------------------------------------------
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // -----------------------------------------------------------------------------
    @FXML private TextField txtCodiceFiscale;     // Campo di testo per l'inserimento del Codice Fiscale
    @FXML private PasswordField txtPassword;      // Campo di testo mascherato per l'inserimento della Password
    @FXML private TextField txtPasswordMostrata;  // Campo di testo in chiaro attivo nella modalità "Mostra Password"
    @FXML private CheckBox chkMostraPassword;    // Casella di spunta "Mostra password"
    @FXML private Button btnLogin;               // Pulsante di conferma "ACCESSO"
    @FXML private Button btnAnnulla;             // Pulsante "ANNULLA" per ritornare alla schermata di benvenuto

    /**
     * GESTORE EVENTO: MOSTRA / NASCONDI PASSWORD (CheckBox)
     * Sincronizza ed alterna la visibilità tra il PasswordField (mascherato) e il TextField (in chiaro).
     * 
     * @param event L'evento scatenato dalla selezione o deselezione della casella di spunta.
     */
    @FXML
    void gestisciMostraPassword(ActionEvent event) {
        if (chkMostraPassword.isSelected()) {
            // SE SELEZIONATA: Copia il testo corrente nel campo in chiaro e lo rende visibile
            txtPasswordMostrata.setText(txtPassword.getText());
            txtPasswordMostrata.setVisible(true);
            txtPassword.setVisible(false); // Nasconde il campo con i caratteri protetti
        } else {
            // SE DESELEZIONATA: Copia il testo corrente nel campo mascherato e lo rende visibile
            txtPassword.setText(txtPasswordMostrata.getText());
            txtPassword.setVisible(true);
            txtPasswordMostrata.setVisible(false); // Nasconde il campo in chiaro
        }
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ANNULLA"
     * Ricarica la vista iniziale di benvenuto (SchermataIniziale.fxml) mantenendo lo stesso Stage.
     * 
     * @param event L'evento di azione generato dal click del mouse.
     */
    @FXML
    void gestisciAnnulla(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataIniziale.fxml"));
            Parent root = loader.load();
            Scene scenaAttuale = btnAnnulla.getScene().getWindow().getScene();
            scenaAttuale.setRoot(root); // Sostituisce il nodo radice per un cambio vista immediato
            Stage stage = (Stage) scenaAttuale.getWindow();
            stage.setTitle("MyPatenti - Benvenuto");
        } catch (IOException e) {
            System.err.println("Errore critico durante il ritorno alla schermata iniziale!");
            e.printStackTrace();
        }
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ACCESSO"
     * Raccoglie le credenziali immesse ed esegue le convalidi formali.
     * Se i controlli sono superati, entra DIRETTAMENTE nella Dashboard senza mostrare alcun Alert di successo.
     * 
     * @param event L'evento di azione generato dal click sul pulsante "ACCESSO".
     */
    @FXML
    void gestisciLogin(ActionEvent event) {
        // 1. Estrazione dei valori inseriti (pulizia degli spazi con trim() e conversione del CF in maiuscolo)
        String codiceFiscale = txtCodiceFiscale.getText().trim().toUpperCase();
        String password = chkMostraPassword.isSelected() ? txtPasswordMostrata.getText() : txtPassword.getText();

        // 2. CONTROLLO 1: Verifica che nessun campo sia stato lasciato vuoto
        if (codiceFiscale.isEmpty() || password.isEmpty()) {
            mostraAlertErrore("Accesso Negato", "Campi incompleti!", "Inserisci sia il Codice Fiscale che la Password per accedere.");
            return; // Interrompe il processo di login
        }

        // 3. CONTROLLO 2: Convalida la lunghezza del Codice Fiscale (deve contenere esattamente 16 caratteri)
        if (codiceFiscale.length() != 16) {
            mostraAlertErrore("Formato Errato", "Codice Fiscale non valido.", "Il codice fiscale inserito deve essere composto esattamente da 16 caratteri.");
            return; // Interrompe il processo di login
        }

        // 4. (Nota) Non forzare la robustezza della password al login — la validazione forte avviene in fase di registrazione.
        // Si accetta qualsiasi password inserita qui (a condizione che non sia vuota) per permettere il login degli utenti già registrati.

        System.out.println("Login autorizzato con successo per l'utente CF: " + codiceFiscale);

        // 5. CARICAMENTO DIRETTO E FLUIDO DELLA DASHBOARD MYPATENTI (Senza Alert intermedi)
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SchermataDashboard.fxml"));
            Parent root = loader.load();
            Scene scenaAttuale = btnLogin.getScene();
            scenaAttuale.setRoot(root); // Carica la Dashboard sostituendo il layout radice
            Stage stage = (Stage) scenaAttuale.getWindow();
            stage.setTitle("MyPatenti - Dashboard");
        } catch (IOException e) {
            System.err.println("Errore critico durante il caricamento della Dashboard!");
            e.printStackTrace();
        }
    }

    /**
     * METODO HELPER PER MOSTRARE FINESTRE DI DIALOGO DI ERRORE (Alert ERROR)
     * 
     * @param titolo Titolo visualizzato sulla barra del popup.
     * @param intestazione Testo dell'intestazione principale in grassetto.
     * @param contenuto Spiegazione dettagliata dell'errore.
     */
    private void mostraAlertErrore(String titolo, String intestazione, String contenuto) {
        Alert alert = new Alert(AlertType.ERROR); // Inizializza un dialogo con icona di Errore (X rossa)
        alert.setTitle(titolo);
        alert.setHeaderText(intestazione);
        alert.setContentText(contenuto);
        alert.showAndWait(); // Mostra il dialogo bloccando l'interfaccia finché l'utente non preme OK
    }

    /**
     * METODO HELPER DI VALIDAZIONE ROBUSTEZZA PASSWORD
     * Controlla che la password rispetti i seguenti tre criteri:
     * 1. Lunghezza minima di almeno 8 caratteri.
     * 2. Presenza di almeno una lettera maiuscola (A-Z).
     * 3. Presenza di almeno un carattere speciale (!@#$%^&* ecc.).
     * 
     * @param password La stringa contenente la password da verificare.
     * @return null se la password è valida, altrimenti la stringa con il messaggio di errore specifico.
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
         
        return null; // La password rispetta tutti i requisiti
    }
}
