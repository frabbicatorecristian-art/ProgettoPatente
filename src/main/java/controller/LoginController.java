package controller;


// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import javafx.event.ActionEvent;              // Classe per la gestione degli eventi di azione (click pulsanti)
import javafx.fxml.FXML;                     // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.geometry.Insets;
import javafx.scene.control.Button;          // Componente pulsante cliccabile
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;        // Casella di controllo selezionabile per opzioni (Mostra password)
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;          // Componente di testo visibile a riga singola
import javafx.scene.control.PasswordField;   // Campo di testo speciale con caratteri nascosti (pallini)
import javafx.scene.control.TextField;       // Campo di testo visibile a riga singola
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import model.Utente;                         // POJO con i dati anagrafici dell'utente
import model.SessioneUtente;                 // Singleton che mantiene l'utente loggato in memoria
import dao.UtenteDAO;
import service.UtenteService;                // Layer Service per l'autenticazione
import util.AlertPersonalizzato;             // Dialoghi personalizzati

import java.util.Optional;

public class LoginController extends BaseController {

    // -----------------------------------------------------------------------------
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // -----------------------------------------------------------------------------
    @FXML private TextField txtCodiceFiscale;     // Campo di testo per l'inserimento del Codice Fiscale
    @FXML private PasswordField txtPassword;      // Campo di testo mascherato per l'inserimento della Password
    @FXML private TextField txtPasswordMostrata;  // Campo di testo in chiaro attivo nella modalità "Mostra Password"
    @FXML private CheckBox chkMostraPassword;    // Casella di spunta "Mostra password"
    @FXML private Hyperlink linkPasswordDimenticata; // Link "Hai dimenticato la password?"
    @FXML private Button btnLogin;               // Pulsante di conferma "ACCESSO"
    @FXML private Button btnAnnulla;             // Pulsante "ANNULLA" per ritornare alla schermata di benvenuto

    // Istanza del Service Layer per l'autenticazione
    private final UtenteService utenteService = new UtenteService();
    private final UtenteDAO utenteDAO = new UtenteDAO();

    /**
     * GESTORE EVENTO: MOSTRA / NASCONDI PASSWORD (CheckBox)
     */
    @FXML
    void gestisciMostraPassword(ActionEvent event) {
        if (chkMostraPassword.isSelected()) {
            txtPasswordMostrata.setText(txtPassword.getText());
            txtPasswordMostrata.setVisible(true);
            txtPassword.setVisible(false);
        } else {
            txtPassword.setText(txtPasswordMostrata.getText());
            txtPassword.setVisible(true);
            txtPasswordMostrata.setVisible(false);
        }
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ANNULLA"
     */
    @FXML
    void gestisciAnnulla(ActionEvent event) {
        naviga("/view/Home.fxml", "MyPatenti - Benvenuto", btnAnnulla.getScene());
    }

    /**
     * GESTORE EVENTO: HAI DIMENTICATO LA PASSWORD?
     */
    @FXML
    void gestisciPasswordDimenticata(ActionEvent event) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Recupero Password");
        inputDialog.setHeaderText("Passo 1: Inserisci il tuo Codice Fiscale");
        inputDialog.setContentText("Codice Fiscale:");
        
        Optional<String> cfResult = inputDialog.showAndWait();
        if (!cfResult.isPresent() || cfResult.get().trim().isEmpty()) {
            return;
        }

        String cf = cfResult.get().trim().toUpperCase();
        if (cf.length() != 16) {
            AlertPersonalizzato.mostraErrore("Codice Fiscale non valido", "Il codice fiscale deve essere di 16 caratteri.");
            return;
        }

        String domanda = utenteDAO.recuperaDomandaSicurezza(cf);
        if (domanda == null || domanda.trim().isEmpty()) {
            AlertPersonalizzato.mostraErrore("Recupero non disponibile",
                    "Nessun utente trovato con questo Codice Fiscale, oppure l'utente non ha impostato la domanda di sicurezza durante la registrazione.");
            return;
        }

        // Dialogo Passo 2: Domanda, Risposta e Nuova Password
        Dialog<ButtonType> recoveryDialog = new Dialog<>();
        recoveryDialog.setTitle("Recupero Password");
        recoveryDialog.setHeaderText("Passo 2: Rispondi alla domanda di sicurezza");

        ButtonType btnConferma = new ButtonType("Reimposta Password", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        recoveryDialog.getDialogPane().getButtonTypes().addAll(btnConferma, btnAnnulla);

        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(15));

        Label lblDomandaTitle = new Label("Domanda di sicurezza:");
        lblDomandaTitle.setStyle("-fx-font-weight: bold;");

        Label lblDomandaText = new Label(domanda);
        lblDomandaText.setStyle("-fx-text-fill: #0284c7; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label lblRisposta = new Label("Risposta:");
        TextField txtRisposta = new TextField();
        txtRisposta.setPromptText("Inserisci la tua risposta");

        Label lblNuovaPassword = new Label("Nuova Password:");
        PasswordField pwdNuova = new PasswordField();
        pwdNuova.setPromptText("Nuova password (minimo 8 caratteri)");
        TextField txtNuova = new TextField();
        txtNuova.setPromptText(pwdNuova.getPromptText());
        txtNuova.textProperty().bindBidirectional(pwdNuova.textProperty());

        Label lblConfermaPassword = new Label("Conferma Nuova Password:");
        PasswordField pwdConferma = new PasswordField();
        pwdConferma.setPromptText("Ripeti la nuova password");
        TextField txtConferma = new TextField();
        txtConferma.setPromptText(pwdConferma.getPromptText());
        txtConferma.textProperty().bindBidirectional(pwdConferma.textProperty());

        CheckBox chkMostraRecupero = new CheckBox("Mostra password");
        chkMostraRecupero.setOnAction(e -> {
            boolean mostra = chkMostraRecupero.isSelected();
            int iNuova = contentBox.getChildren().indexOf(mostra ? pwdNuova : txtNuova);
            if (iNuova != -1) {
                contentBox.getChildren().set(iNuova, mostra ? txtNuova : pwdNuova);
            }
            int iConf = contentBox.getChildren().indexOf(mostra ? pwdConferma : txtConferma);
            if (iConf != -1) {
                contentBox.getChildren().set(iConf, mostra ? txtConferma : pwdConferma);
            }
        });

        contentBox.getChildren().addAll(
                lblDomandaTitle, lblDomandaText,
                lblRisposta, txtRisposta,
                lblNuovaPassword, pwdNuova,
                lblConfermaPassword, pwdConferma,
                chkMostraRecupero
        );

        recoveryDialog.getDialogPane().setContent(contentBox);

        Optional<ButtonType> result = recoveryDialog.showAndWait();
        if (result.isPresent() && result.get() == btnConferma) {
            String risposta = txtRisposta.getText().trim();
            String nuovaPassword = pwdNuova.getText();
            String conferma = pwdConferma.getText();

            if (risposta.isEmpty()) {
                AlertPersonalizzato.mostraErrore("Risposta Mancante", "Devi inserire la risposta alla domanda di sicurezza.");
                return;
            }
            if (nuovaPassword.isEmpty() || nuovaPassword.length() < 8) {
                AlertPersonalizzato.mostraErrore("Password non valida", "La nuova password deve contenere almeno 8 caratteri.");
                return;
            }
            if (!nuovaPassword.equals(conferma)) {
                AlertPersonalizzato.mostraErrore("Password non coincidenti", "La nuova password e la conferma non coincidono.");
                return;
            }

            String hashSalvato = utenteDAO.getPasswordHash(cf);
            if (hashSalvato != null && util.PasswordUtil.checkPassword(nuovaPassword, hashSalvato)) {
                AlertPersonalizzato.mostraErrore("Password non valida", "La nuova password non può essere uguale a quella precedente.");
                return;
            }

            boolean successo = utenteDAO.verificaRispostaEResetPassword(cf, risposta, nuovaPassword);
            if (successo) {
                AlertPersonalizzato.mostraInfo("Password Aggiornata!", "La tua password è stata modificata con successo. Ora puoi accedere con le nuove credenziali.");
                txtCodiceFiscale.setText(cf);
                txtPassword.clear();
                txtPasswordMostrata.clear();
            } else {
                AlertPersonalizzato.mostraErrore("Risposta Errata", "La risposta alla domanda di sicurezza è errata.");
            }
        }
    }

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ACCESSO"
     */
    @FXML
    void gestisciLogin(ActionEvent event) {
        String codiceFiscale = txtCodiceFiscale.getText().trim().toUpperCase();
        String password = chkMostraPassword.isSelected() ? txtPasswordMostrata.getText() : txtPassword.getText();

        if (codiceFiscale.isEmpty() || password.isEmpty()) {
            AlertPersonalizzato.mostraErrore("Campi incompleti!", "Inserisci sia il Codice Fiscale che la Password per accedere.");
            return;
        }

        if (codiceFiscale.length() != 16) {
            AlertPersonalizzato.mostraErrore("Codice Fiscale non valido.", "Il codice fiscale inserito deve essere composto esattamente da 16 caratteri.");
            return;
        }

        Utente utenteLoggato = utenteService.login(codiceFiscale, password);

        if (utenteLoggato == null) {
            AlertPersonalizzato.mostraErrore("Impossibile accedere.", "Il Codice Fiscale non esiste o la password è sbagliata.");
            return;
        }

        System.out.println("Login autorizzato con successo per l'utente CF: " + codiceFiscale);
        SessioneUtente.getInstance().setUtente(utenteLoggato);

        naviga("/view/Dashboard.fxml", "MyPatenti - Dashboard", btnLogin.getScene());
    }
}
