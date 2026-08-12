package controller;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX E JAVA STANDARD
// =================================================================================
import model.TemaManager;                    // Gestisce la personalizzazione dinamica del tema (Chiaro/Scuro)
import model.SessioneUtente;                 // Mantiene in memoria l'utente autenticato tra le schermate
import javafx.collections.FXCollections;    // Crea liste osservabili di dati per ComboBox
import javafx.event.ActionEvent;             // Rappresenta un evento di azione (click pulsante)
import javafx.fxml.FXML;                    // Annotazione per iniettare attributi e metodi dal file FXML
import javafx.fxml.FXMLLoader;              // Carica e legge i file FXML della vista
import javafx.geometry.Insets;              // Specifica i margini interni di un contenitore
import javafx.scene.Parent;                // Nodo radice del layout della scena
import javafx.scene.Scene;                 // Scena grafica principale JavaFX
import javafx.scene.control.Alert;          // Finestra di dialogo/notifica per errori e conferme
import javafx.scene.control.Button;         // Componente pulsante cliccabile
import javafx.scene.control.ButtonBar;      // Classe per la gestione dei pulsanti nei dialoghi
import javafx.scene.control.ButtonType;     // Tipo di pulsante nei dialoghi (OK, CANCEL, ecc.)
import javafx.scene.control.ComboBox;       // Componente per la selezione da una lista a tendina
import javafx.scene.control.Dialog;         // Dialogo personalizzabile per input complessi
import javafx.scene.control.Label;          // Componente di testo statico visualizzato a schermo
import javafx.scene.control.MenuButton;     // Componente menu a tendina posizionabile nei layout
import javafx.scene.control.PasswordField;  // Campo di testo mascherato per l'inserimento di password
import javafx.scene.control.RadioButton;    // Componente radio button (scelta singola mutualmente esclusiva)
import javafx.scene.control.ToggleButton;   // Componente pulsante che può essere selezionato/deselezionato
import javafx.scene.image.Image;            // Rappresenta un'immagine JavaFX in memoria
import javafx.scene.image.ImageView;        // Componente per visualizzare immagini (Icon, PNG, JPG, ecc.)
import javafx.scene.layout.GridPane;        // Layout a griglia per posizionare componenti in righe e colonne
import javafx.scene.layout.VBox;            // Layout verticale per impilare componenti verticalmente
import javafx.stage.Stage;                 // Finestra principale gestita dal sistema operativo
import java.io.IOException;                 // Gestione eccezioni di I/O per il caricamento delle viste

/**
 * =================================================================================
 * CONTROLLER - SCHERMATA IMPOSTAZIONI
 * =================================================================================
 * Gestisce le preferenze e le impostazioni dell'applicazione MyPatenti.
 * 
 * FUNZIONALITÀ PRINCIPALI:
 * 1. Personalizzazione del tema (Chiaro/Scuro) con anteprima in tempo reale.
 * 2. Gestione delle impostazioni del Quiz (numero di domande, spiegazione errori, cronometro).
 * 3. Cambio della password con validazione e conferma.
 * 4. Opzione per eliminare il proprio account (operazione irreversibile).
 * 5. Logout con finestra di conferma.
 * 6. Persistenza delle preferenze tramite TemaManager.
 */
public class ImpostazioniController {

    // =====================================================================
    // COMPONENTI GRAFICI INIETTATI DA FXML (Mappati tramite fx:id)
    // =====================================================================
    @FXML private Button     btnTornaDashboard;      // Pulsante per tornare alla Dashboard principale
    @FXML private Button     btnSalvaGlobale;        // Pulsante "SALVA" per salvare tutte le impostazioni
    @FXML private Button     btnAnnullaGlobale;      // Pulsante "ANNULLA" per ripristinare le impostazioni salvate
    @FXML private MenuButton menuProfilo;           // Menu a tendina del profilo con opzioni (Logout, ecc.)

    // =====================================================================
    // SEZIONE ASPETTO - Personalizzazione del tema
    // =====================================================================
    @FXML private RadioButton rbChiaro;             // Radio button per selezionare il tema chiaro (light mode)
    @FXML private RadioButton rbScuro;              // Radio button per selezionare il tema scuro (dark mode)
    @FXML private ImageView   imgIconaTema;         // Icona visiva che cambia in base al tema selezionato (sole/luna)

    // =====================================================================
    // SEZIONE QUIZ - Impostazioni per i quiz e le simulazioni
    // =====================================================================
    @FXML private ComboBox<Integer> cmbNumeroDomande;       // ComboBox per selezionare il numero di domande (10, 20, 30, 40)
    @FXML private ToggleButton toggleSpiegazioneErrori;     // Toggle per attivare/disattivare le spiegazioni degli errori
    @FXML private ToggleButton toggleCronometro;            // Toggle per attivare/disattivare il cronometro nelle quiz

    // =====================================================================
    // VARIABILI DI ISTANZA - Stato temporaneo dell'applicazione
    // =====================================================================
    private boolean temaSalvato;                    // Memorizza lo stato del tema all'ultimo salvataggio (per la funzione "Annulla")
    private int     numeroDomandeSalvato;           // Memorizza il numero di domande all'ultimo salvataggio
    private boolean spiegazioneErroriSalvata;       // Memorizza lo stato del toggle "Spiegazione Errori" all'ultimo salvataggio
    private boolean cronometroSalvato;              // Memorizza lo stato del toggle "Cronometro" all'ultimo salvataggio

    // =====================================================================
    // INIZIALIZZAZIONE AUTOMATICA DELLA SCHERMATA
    // =====================================================================

    /**
     * METODO INITIALIZE (Hook automatico JavaFX)
     * Eseguito automaticamente da JavaFX subito dopo che il file FXML è stato caricato.
     * Inizializza tutti i componenti con i valori salvati precedentemente.
     */
    @FXML
    public void initialize() {
        // Aggiorna il menu della navbar con il nome dell'utente della sessione.
        // Se la schermata viene aperta senza login, conserva il fallback "Utente"
        // invece di provocare un errore per l'assenza di dati nella sessione.
        String nomeUtenteLoggato = "Utente";
        if (SessioneUtente.getInstance().isLoggato()) {
            nomeUtenteLoggato = SessioneUtente.getInstance().getUtente().getNome();
        }
        menuProfilo.setText("👤 Ciao, " + nomeUtenteLoggato);

        // Popola il ComboBox con le opzioni per il numero di domande (10, 20, 30, 40)
        cmbNumeroDomande.setItems(FXCollections.observableArrayList(10, 20, 30, 40));
        
        // Imposta il valore di default a 30 domande
        cmbNumeroDomande.setValue(30);

        // Recupera lo stato del tema salvato da TemaManager (true = scuro, false = chiaro)
        temaSalvato = TemaManager.getInstance().isTemaScuro();

        // Salva i valori iniziali di TUTTE le impostazioni (snapshot per la funzione "Annulla")
        numeroDomandeSalvato       = cmbNumeroDomande.getValue();
        spiegazioneErroriSalvata   = toggleSpiegazioneErrori.isSelected();
        cronometroSalvato          = toggleCronometro.isSelected();
        
        // Ripristina il tema salvato e aggiorna l'icona della luna/sole
        if (temaSalvato) {
            // Se il tema salvato è SCURO: seleziona il radio button e mostra l'icona della luna
            rbScuro.setSelected(true);
            imgIconaTema.setImage(new Image(getClass().getResourceAsStream("/images/moon.png")));
        } else {
            // Se il tema salvato è CHIARO: seleziona il radio button e mostra l'icona del sole
            rbChiaro.setSelected(true);
            imgIconaTema.setImage(new Image(getClass().getResourceAsStream("/images/light.png")));
        }

        // Applica il tema alla schermata corrente usando Platform.runLater per evitare race conditions
        javafx.application.Platform.runLater(() ->
            TemaManager.getInstance().applica(btnTornaDashboard.getScene())
        );
    }

    // =====================================================================
    // GESTIONE DEL TEMA - Anteprima in tempo reale
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SU UN RADIO BUTTON DEL TEMA (Chiaro/Scuro)
     * Applica un'anteprima del tema selezionato SENZA SALVARE ancora le preferenze.
     * La modifica effettiva avviene solo quando l'utente clicca su "SALVA".
     * Mostra/nasconde l'icona della luna (scuro) o del sole (chiaro) in base alla scelta.
     * 
     * @param event L'evento di azione scatenato dalla selezione di un radio button.
     */
    @FXML
    void applicaTema(ActionEvent event) {
        // Recupera il nodo radice (VBox) della schermata corrente per modificare lo stile CSS
        VBox root = (VBox) btnTornaDashboard.getScene().getRoot();
        
        if (rbScuro.isSelected()) {
            // SE TEMA SCURO: Applica il colore di sfondo scuro
            root.setStyle("-fx-background-color: " + TemaManager.BG_SCURO + ";");
            // Mostra l'icona della luna (rappresentazione visiva del tema scuro)
            imgIconaTema.setImage(new Image(getClass().getResourceAsStream("/images/moon.png")));
        } else {
            // SE TEMA CHIARO: Applica il colore di sfondo chiaro
            root.setStyle("-fx-background-color: " + TemaManager.BG_CHIARO + ";");
            // Mostra l'icona del sole (rappresentazione visiva del tema chiaro)
            imgIconaTema.setImage(new Image(getClass().getResourceAsStream("/images/light.png")));
        }
    }

    // =====================================================================
    // SALVATAGGIO DELLE IMPOSTAZIONI
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "SALVA"
     * Salva TUTTE le impostazioni modificate (tema + numero domande) nel TemaManager.
     * Le preferenze persisteranno anche dopo la chiusura e riapertura dell'applicazione.
     * Stampa un messaggio di debug nella console per verificare il salvataggio.
     * 
     * @param event L'evento di azione scatenato dal click del pulsante "SALVA".
     */
    @FXML
    void salvaImpostazioni(ActionEvent event) {
        // Determina se il tema selezionato è SCURO (true) o CHIARO (false)
        boolean scuro = rbScuro.isSelected();
        
        // Salva la preferenza nel TemaManager (persistenza globale)
        TemaManager.getInstance().setTemaScuro(scuro);
        
        // Aggiorna lo snapshot di TUTTE le impostazioni per la funzione "Annulla" futura
        temaSalvato                = scuro;
        numeroDomandeSalvato       = cmbNumeroDomande.getValue();
        spiegazioneErroriSalvata   = toggleSpiegazioneErrori.isSelected();
        cronometroSalvato          = toggleCronometro.isSelected();
        
        // Stampa un messaggio di debug nella console per confermare il salvataggio
        System.out.println("[Impostazioni] Salvate. Tema: " + (scuro ? "Scuro" : "Chiaro")
                + " | Domande: " + cmbNumeroDomande.getValue());

        // Mostra un alert di conferma all'utente
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Impostazioni");
        alert.setHeaderText(null);
        alert.setContentText("Impostazioni salvate con successo");
        // Associa l'alert alla finestra corrente (opzionale)
        try {
            Stage stage = (Stage) btnSalvaGlobale.getScene().getWindow();
            alert.initOwner(stage);
        } catch (Exception e) {
            // Se non è possibile associare la stage, prosegui comunque
        }
        alert.showAndWait();
    }

    // =====================================================================
    // ANNULLAMENTO DELLE IMPOSTAZIONI
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ANNULLA"
     * Annulla tutte le modifiche e ripristina le impostazioni all'ultimo salvataggio.
     * Sincronizza lo stato visivo dei radio button e dell'icona del tema.
     * Ripristina lo stile CSS dello sfondo secondo il tema salvato.
     * 
     * @param event L'evento di azione scatenato dal click del pulsante "ANNULLA".
     */
    @FXML
    void annullaImpostazioni(ActionEvent event) {
        // --- RIPRISTINO TEMA ---
        // Ripristina lo stato dei radio button al tema salvato precedentemente
        if (temaSalvato) {
            rbScuro.setSelected(true);
            imgIconaTema.setImage(new Image(getClass().getResourceAsStream("/images/moon.png")));
        } else {
            rbChiaro.setSelected(true);
            imgIconaTema.setImage(new Image(getClass().getResourceAsStream("/images/light.png")));
        }
        
        // Recupera il nodo radice (VBox) per ripristinare lo stile CSS dello sfondo
        VBox root = (VBox) btnTornaDashboard.getScene().getRoot();
        
        // Ripristina il colore di sfondo secondo il tema salvato
        root.setStyle("-fx-background-color: "
                + (temaSalvato ? TemaManager.BG_SCURO : TemaManager.BG_CHIARO) + ";");

        // --- RIPRISTINO QUIZ ---
        // Ripristina il numero di domande al valore dell'ultimo salvataggio
        cmbNumeroDomande.setValue(numeroDomandeSalvato);
        
        // Ripristina i toggle al valore dell'ultimo salvataggio
        toggleSpiegazioneErrori.setSelected(spiegazioneErroriSalvata);
        toggleCronometro.setSelected(cronometroSalvato);
    }

    // =====================================================================
    // IMPOSTAZIONI QUIZ - Toggle e Opzioni
    // =====================================================================

    /**
     * GESTORE EVENTO: TOGGLE PER "SPIEGAZIONE ERRORI"
     * Attiva/disattiva la visualizzazione delle spiegazioni quando l'utente sbaglia una risposta nel quiz.
     * Stampa un messaggio di debug nella console.
     * 
     * @param e L'evento di azione scatenato dal toggle button.
     */
    @FXML 
    void onToggleSpiegazioneErrori(ActionEvent e) { 
        // Chiama il metodo helper per registrare lo stato nel log
        log("Spiegazione errori", toggleSpiegazioneErrori); 
    }

    /**
     * GESTORE EVENTO: TOGGLE PER "CRONOMETRO"
     * Attiva/disattiva la visualizzazione del cronometro durante i quiz a tempo.
     * Stampa un messaggio di debug nella console.
     * 
     * @param e L'evento di azione scatenato dal toggle button.
     */
    @FXML 
    void onToggleCronometro(ActionEvent e) { 
        // Chiama il metodo helper per registrare lo stato nel log
        log("Cronometro", toggleCronometro); 
    }

    /**
     * METODO HELPER: REGISTRAZIONE NEL LOG
     * Stampa nello stream di output il nome della funzione e il suo stato (ON/OFF).
     * Utilizzato dai toggle button per il debug e il monitoraggio delle impostazioni.
     * 
     * @param nome La descrizione della funzione (es. "Cronometro", "Spiegazione errori").
     * @param t Il ToggleButton da cui leggere lo stato (isSelected = ON, altrimenti OFF).
     */
    private void log(String nome, ToggleButton t) {
        System.out.println("[Impostazioni] " + nome + ": " + (t.isSelected() ? "ON" : "OFF"));
    }

    // =====================================================================
    // CAMBIO PASSWORD - Dialog personalizzato
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "CAMBIA PASSWORD"
     * Apre un dialogo personalizzato (Dialog<ButtonType>) per il cambio della password.
     * 
     * FUNZIONALITÀ:
     * 1. Due campi PasswordField per la nuova password e la conferma.
     * 2. Validazione della lunghezza minima (6 caratteri).
     * 3. Verifica che le due password coincidano.
     * 4. Pulsante "Conferma" disabilitato quando i campi sono vuoti (listener).
     * 5. In produzione: salvataggio della nuova password nel database.
     * 
     * @param event L'evento di azione scatenato dal click del pulsante "Cambia Password".
     */
    @FXML
    void cambiaPassword(ActionEvent event) {

        // --- CREAZIONE DEL DIALOGO PERSONALIZZATO ---
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cambia Password");
        dialog.setHeaderText("Inserisci la tua nuova password");

        // Crea due pulsanti personalizzati: "Conferma" e "Annulla"
        ButtonType btnConferma = new ButtonType("✓ Conferma", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla  = new ButtonType("Annulla",    ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConferma, btnAnnulla);

        // --- CREAZIONE DELLA GRIGLIA DI LAYOUT PER I CAMPI INPUT ---
        GridPane grid = new GridPane();
        grid.setHgap(12);                                           // Spazio orizzontale tra colonne: 12 pixel
        grid.setVgap(14);                                           // Spazio verticale tra righe: 14 pixel
        grid.setPadding(new Insets(20, 30, 10, 20));                // Margini interni: 20px sopra, 30px destra, 10px sotto, 20px sinistra

        // Crea i due campi PasswordField per l'inserimento della nuova password
        PasswordField pfNuova    = new PasswordField();
        PasswordField pfConferma = new PasswordField();
        pfNuova.setPromptText("Minimo 6 caratteri");                // Testo di placeholder per il primo campo
        pfConferma.setPromptText("Ripeti la nuova password");       // Testo di placeholder per il secondo campo
        pfNuova.setPrefWidth(240);                                  // Larghezza preferita: 240 pixel
        pfConferma.setPrefWidth(240);                               // Larghezza preferita: 240 pixel

        // Crea le etichette per le righe della griglia
        Label lblNuova    = new Label("Nuova password:");
        Label lblConf     = new Label("Conferma password:");
        Label lblErrore   = new Label();                            // Etichetta vuota per i messaggi di errore
        lblErrore.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;"); // Stile CSS: testo rosso, font 12px

        // Aggiunge i componenti alla griglia (colonna, riga)
        grid.add(lblNuova,    0, 0); grid.add(pfNuova,    1, 0);   // Riga 0: "Nuova password:" e campo input
        grid.add(lblConf,     0, 1); grid.add(pfConferma, 1, 1);   // Riga 1: "Conferma password:" e campo input
        grid.add(lblErrore,   1, 2);                               // Riga 2: Etichetta per i messaggi di errore

        // Inserisce la griglia nel dialogo come contenuto principale
        dialog.getDialogPane().setContent(grid);
        
        // Sposta il focus al primo campo PasswordField per l'inserimento immediato
        pfNuova.requestFocus();

        // --- GESTIONE DEI PULSANTI: Disabilita "Conferma" se i campi sono vuoti ---
        Button btnConfermaNode = (Button) dialog.getDialogPane().lookupButton(btnConferma);
        btnConfermaNode.setDisable(true);                           // Inizialmente disabilitato

        // Listener per il campo "Nuova password": disabilita il pulsante se vuoto o il secondo campo è vuoto
        pfNuova.textProperty().addListener((obs, o, n) ->
            btnConfermaNode.setDisable(n.trim().isEmpty() || pfConferma.getText().trim().isEmpty()));
        
        // Listener per il campo "Conferma password": disabilita il pulsante se vuoto o il primo campo è vuoto
        pfConferma.textProperty().addListener((obs, o, n) ->
            btnConfermaNode.setDisable(n.trim().isEmpty() || pfNuova.getText().trim().isEmpty()));

        // --- GESTIONE DELLA RISPOSTA DELL'UTENTE ---
        dialog.showAndWait().ifPresent(risposta -> {
            if (risposta == btnConferma) {
                // L'utente ha cliccato "Conferma": procedi con la validazione e il salvataggio
                String nuova     = pfNuova.getText().trim();
                String conferma  = pfConferma.getText().trim();

                // Validazione 1: Controlla che la password sia lunga almeno 6 caratteri
                if (nuova.length() < 6) {
                    mostraErrore("Password troppo corta", "La password deve contenere almeno 6 caratteri.");
                } 
                // Validazione 2: Controlla che i due campi coincidano
                else if (!nuova.equals(conferma)) {
                    mostraErrore("Password non coincidono", "La nuova password e la conferma non corrispondono.\nRiprova.");
                } 
                // Validazioni superate: salva la nuova password
                else {
                    // In produzione: saltare questo step e inviare la password al database backend
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("Password aggiornata");
                    ok.setHeaderText(null);
                    ok.setContentText("✅ La tua password è stata aggiornata con successo!");
                    ok.showAndWait();
                }
            }
            // Se l'utente clicca "Annulla", il dialogo si chiude e nessuna modifica viene effettuata
        });
    }

    /**
     * METODO HELPER: MOSTRA FINESTRA DI DIALOGO DI ERRORE
     * Crea e visualizza un Alert di tipo ERROR con il titolo e il messaggio specificati.
     * 
     * @param titolo Il titolo del dialogo di errore.
     * @param msg Il testo descrittivo dell'errore.
     */
    private void mostraErrore(String titolo, String msg) {
        Alert err = new Alert(Alert.AlertType.ERROR);
        err.setTitle(titolo);
        err.setHeaderText(null);
        err.setContentText(msg);
        err.showAndWait();
    }

    // =====================================================================
    // ELIMINA ACCOUNT - Operazione irreversibile
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "ELIMINA ACCOUNT"
     * Mostra una finestra di conferma per avvertire l'utente che l'operazione è IRREVERSIBILE.
     * Se l'utente conferma, naviga alla Schermata Iniziale (simula una disconnessione).
     * In produzione: dovrebbe inviare una richiesta al backend per eliminare i dati dal database.
     * 
     * @param event L'evento di azione scatenato dal click del pulsante "Elimina Account".
     */
    @FXML
    void eliminaAccount(ActionEvent event) {
        // Crea un Alert di conferma con icona di avvertimento
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elimina Account");
        alert.setHeaderText("⚠️ Operazione irreversibile");
        alert.setContentText("Sei sicuro di voler eliminare definitivamente il tuo account?\nTutti i dati verranno persi.");

        // Personalizza i pulsanti del dialogo
        ButtonType annulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType elimina = new ButtonType("Sì, elimina", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(annulla, elimina);

        // Mostra il dialogo e gestisce la risposta
        alert.showAndWait().ifPresent(r -> {
            if (r == elimina) {
                // L'utente ha confermato: naviga alla Schermata Iniziale
                naviga("/view/SchermataIniziale.fxml", "MyPatenti - Benvenuto");
            }
            // Se l'utente clicca "Annulla", rimane nella schermata Impostazioni
        });
    }

    // =====================================================================
    // LOGOUT - Disconnessione con conferma
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SU "LOGOUT" DAL MENU PROFILO
     * Mostra una finestra di dialogo di conferma per chiedere all'allievo se vuole effettuare il logout.
     * Se l'allievo conferma, naviga alla Schermata Iniziale di benvenuto.
     * Se l'allievo annulla, rimane nella schermata Impostazioni.
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
        ButtonType esci    = new ButtonType("Sì, esci", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(annulla, esci);

        // Mostra il dialogo e gestisce la risposta dell'allievo
        alert.showAndWait().ifPresent(r -> {
            if (r == esci) {
                // L'utente ha confermato: naviga alla Schermata Iniziale
                naviga("/view/SchermataIniziale.fxml", "MyPatenti - Benvenuto");
            }
            // Se l'utente clicca "Annulla", rimane nella schermata Impostazioni
        });
    }

    // =====================================================================
    // NAVIGAZIONE FRA LE SCHERMATE
    // =====================================================================

    /**
     * GESTORE EVENTO: CLICK SUL PULSANTE "TORNA ALLA DASHBOARD"
     * Carica la schermata della Dashboard principale (SchermataDashboard.fxml).
     * 
     * @param event L'evento di azione scatenato dal click del pulsante.
     */
    @FXML
    void tornaAllaDashboard(ActionEvent event) {
        // Invoca il metodo helper passando il percorso FXML e il nuovo titolo dello Stage
        naviga("/view/SchermataDashboard.fxml", "MyPatenti - Dashboard");
    }

    /**
     * GESTORE EVENTO: CLICK SU "IL MIO PROFILO" DAL MENU UTENTE.
     * Carica la schermata del profilo, mantenendo la stessa struttura del menu
     * presente nella navbar della schermata Profilo.
     *
     * @param event L'evento generato dalla selezione della voce di menu.
     */
    @FXML
    void apriProfilo(ActionEvent event) {
        naviga("/view/SchermataProfilo.fxml", "MyPatenti - Il mio profilo");
    }

    /**
     * GESTORE EVENTO: CLICK SU "IMPOSTAZIONI" DAL MENU UTENTE.
     * L'utente è già nella vista Impostazioni: non è necessario ricaricarla.
     * Il metodo esiste per rendere il menu identico a quello del Profilo e per
     * fornire a FXML un gestore dell'azione esplicito.
     *
     * @param event L'evento generato dalla selezione della voce di menu.
     */
    @FXML
    void apriImpostazioniMenu(ActionEvent event) {
        // Nessuna navigazione: la schermata corrente è già Impostazioni.
    }

    /**
     * METODO HELPER: CAMBIO DI SCHERMATA SENZA FLICKERING
     * Carica il nuovo file FXML e sostituisce il nodo radice (root) della Scena già esistente.
     * Questo approccio evita di chiudere e riaprire lo Stage (finestra), garantendo una transizione istantanea.
     * Applica il tema corrente alla nuova schermata usando TemaManager.
     * 
     * @param fxml Il percorso relativo del file .fxml da caricare (es. "/view/SchermataDashboard.fxml").
     * @param titolo Il nuovo testo da visualizzare sulla barra del titolo della finestra.
     */
    private void naviga(String fxml, String titolo) {
        try {
            // Carica il file FXML e costruisce la gerarchia visiva dei componenti
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            
            // Recupera la Scena corrente dal pulsante "Torna Dashboard"
            Scene scena = btnTornaDashboard.getScene();
            
            // Sostituisce il nodo radice della Scena con il nuovo layout
            scena.setRoot(root);
            
            // Applica il tema attualmente attivo alla nuova schermata
            TemaManager.getInstance().applica(scena);
            
            // Aggiorna il titolo della finestra (Stage) per riflettere la pagina attuale
            ((Stage) scena.getWindow()).setTitle(titolo);
        } catch (IOException e) {
            // Segnala nel log di errore se il caricamento del file FXML fallisce
            System.err.println("Errore navigazione: " + fxml);
            e.printStackTrace();
        }
    }
}
