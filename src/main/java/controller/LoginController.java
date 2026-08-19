package controller;


// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Classe per la gestione degli eventi di azione (click pulsanti)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.CheckBox;        // Casella di controllo selezionabile per opzioni (Mostra password)
import javafx.scene.control.PasswordField;   // Campo di testo speciale con caratteri nascosti (pallini)
import javafx.scene.control.TextField;       // Campo di testo visibile a riga singola
import model.Utente;                         // POJO con i dati anagrafici dell'utente
import model.SessioneUtente;                 // Singleton che mantiene l'utente loggato in memoria
import service.UtenteService;                // Layer Service per l'autenticazione
import util.AlertPersonalizzato;             // Dialoghi personalizzati

/**
 * =================================================================================
 * CONTROLLER - SCHERMATA DI LOGIN (Accesso Utente)
 * =================================================================================
 * Gestisce l'autenticazione degli utenti dell'applicazione MyPatenti.
 * Implementa controlli di validazione lato client e delega l'autenticazione a UtenteService:
 * 1. Controllo presenza campi vuoti.
 * 2. Verifica formale della lunghezza del Codice Fiscale (esattamente 16 caratteri).
 * 3. Funzionalità dinamica per mostrare/nascondere la password in chiaro.
 * 4. Accesso diretto alla Dashboard senza schermate/popup di Alert intermedie in caso di esito positivo.
 */
public class LoginController extends BaseController {

    // -----------------------------------------------------------------------------
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // -----------------------------------------------------------------------------
    @FXML private TextField txtCodiceFiscale;     // Campo di testo per l'inserimento del Codice Fiscale
    @FXML private PasswordField txtPassword;      // Campo di testo mascherato per l'inserimento della Password
    @FXML private TextField txtPasswordMostrata;  // Campo di testo in chiaro attivo nella modalità "Mostra Password"
    @FXML private CheckBox chkMostraPassword;    // Casella di spunta "Mostra password"
    @FXML private Button btnLogin;               // Pulsante di conferma "ACCESSO"
    @FXML private Button btnAnnulla;             // Pulsante "ANNULLA" per ritornare alla schermata di benvenuto

    // Istanza del Service Layer per l'autenticazione
    private final UtenteService utenteService = new UtenteService();

    /**
     * GESTORE EVENTO: MOSTRA / NASCONDI PASSWORD (CheckBox)
     * Sincronizza e alterna la visibilità tra il PasswordField (mascherato) e il TextField (in chiaro).
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
        naviga("/view/Home.fxml", "MyPatenti - Benvenuto", btnAnnulla.getScene());
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
            AlertPersonalizzato.mostraErrore("Campi incompleti!", "Inserisci sia il Codice Fiscale che la Password per accedere.");
            return; // Interrompe il processo di login
        }

        // 3. CONTROLLO 2: Convalida la lunghezza del Codice Fiscale (deve contenere esattamente 16 caratteri)
        if (codiceFiscale.length() != 16) {
            AlertPersonalizzato.mostraErrore("Codice Fiscale non valido.", "Il codice fiscale inserito deve essere composto esattamente da 16 caratteri.");
            return; // Interrompe il processo di login
        }

        // -----------------------------------------------------------------------
        // AUTENTICAZIONE TRAMITE SERVICE LAYER E SALVATAGGIO IN SESSIONE
        // -----------------------------------------------------------------------
        Utente utenteLoggato = utenteService.login(codiceFiscale, password);

        if (utenteLoggato == null) {
            AlertPersonalizzato.mostraErrore("Impossibile accedere.", "Il Codice Fiscale non esiste o la password è sbagliata.");
            return;
        }

        System.out.println("Login autorizzato con successo per l'utente CF: " + codiceFiscale);
        SessioneUtente.getInstance().setUtente(utenteLoggato);

        // 4. CARICAMENTO DIRETTO E FLUIDO DELLA DASHBOARD MYPATENTI (Senza Alert intermedi)
        naviga("/view/Dashboard.fxml", "MyPatenti - Dashboard", btnLogin.getScene());
    }
}
