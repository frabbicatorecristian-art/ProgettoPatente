package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import dao.VeicoloDAO;
import model.Veicolo;
import model.SessioneUtente;
import util.AlertPersonalizzato;

/**
 * Controller per la gestione della sezione "Veicoli".
 * Gestisce l'interfaccia principale del garage virtuale e il modale di aggiunta.
 */
public class VeicoliController extends BaseController {

    private Veicolo veicoloInModifica = null;

    @FXML private StackPane garageView;
    @FXML private VBox formView;
    @FXML private VBox emptyStateBox;
    @FXML private ScrollPane scrollVeicoli;
    @FXML private VBox listaVeicoliBox;
    @FXML private Button btnSalvaVeicolo;
    @FXML private Button btnAggiungiVeicolo;
    
    @FXML private ComboBox<String> cmbTipologia;
    @FXML private TextField txtNomeVeicolo;
    @FXML private TextField txtTarga;
    
    @FXML private DatePicker dpAssicurazione;
    @FXML private ComboBox<String> cmbValidita;
    @FXML private Label lblScadenzaAssicurazione;
    
    @FXML private DatePicker dpImmatricolazione;
    @FXML private ToggleButton tglPrimaRevisione;
    @FXML private VBox boxUltimaRevisione;
    @FXML private DatePicker dpUltimaRevisione;
    @FXML private Label lblScadenzaRevisione;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        System.out.println("Sezione Veicoli inizializzata.");
        
        // Inizializza tabella nel DB se non esiste
        VeicoloDAO.creaTabellaSeNonEsiste();
        
        // Inizializzazione combo tipologia
        cmbTipologia.getItems().addAll("Autoveicolo", "Motociclo", "Ciclomotore");
        
        // Inizializzazione combo validità assicurazione
        cmbValidita.getItems().addAll("Annuale", "Semestrale");
        cmbValidita.getSelectionModel().select("Annuale");
        
        // Listeners Assicurazione
        dpAssicurazione.valueProperty().addListener((obs, oldVal, newVal) -> calcolaScadenzaAssicurazione());
        cmbValidita.valueProperty().addListener((obs, oldVal, newVal) -> calcolaScadenzaAssicurazione());
        
        // Listeners Revisione
        dpImmatricolazione.valueProperty().addListener((obs, oldVal, newVal) -> calcolaScadenzaRevisione());
        dpUltimaRevisione.valueProperty().addListener((obs, oldVal, newVal) -> calcolaScadenzaRevisione());
        
        tglPrimaRevisione.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boxUltimaRevisione.setVisible(newVal);
            boxUltimaRevisione.setManaged(newVal);
            
            if (newVal) {
                if (!tglPrimaRevisione.getStyleClass().contains("toggle-on")) {
                    tglPrimaRevisione.getStyleClass().add("toggle-on");
                }
            } else {
                tglPrimaRevisione.getStyleClass().remove("toggle-on");
            }
            
            calcolaScadenzaRevisione();
        });
        
        aggiornaListaVeicoli();
    }

    @FXML
    void aggiungiVeicolo(ActionEvent event) {
        veicoloInModifica = null; // Stiamo aggiungendo, non modificando
        btnSalvaVeicolo.setText("✓ Aggiungi Veicolo");
        pulisciForm();
        garageView.setVisible(false);
        formView.setVisible(true);
    }
    
    private void apriModaleModifica(Veicolo v) {
        veicoloInModifica = v;
        btnSalvaVeicolo.setText("✓ Modifica Veicolo");
        cmbTipologia.setValue(v.getTipologia());
        txtNomeVeicolo.setText(v.getNome());
        txtTarga.setText(v.getTarga());
        dpAssicurazione.setValue(v.getDataAssicurazione());
        cmbValidita.setValue(v.getValiditaAssicurazione() != null ? v.getValiditaAssicurazione() : "Annuale");
        dpImmatricolazione.setValue(v.getDataImmatricolazione());
        tglPrimaRevisione.setSelected(v.isPrimaRevisioneFatta());
        dpUltimaRevisione.setValue(v.getDataUltimaRevisione());
        
        garageView.setVisible(false);
        formView.setVisible(true);
    }
    
    private void pulisciForm() {
        txtNomeVeicolo.clear();
        txtTarga.clear();
        dpAssicurazione.setValue(null);
        dpImmatricolazione.setValue(null);
        tglPrimaRevisione.setSelected(false);
        dpUltimaRevisione.setValue(null);
        cmbTipologia.getSelectionModel().clearSelection();
        cmbValidita.getSelectionModel().select("Annuale");
    }
    
    @FXML
    void salvaVeicolo(ActionEvent event) {
        String tipologia = cmbTipologia.getValue();
        String nome = txtNomeVeicolo.getText();
        String targa = txtTarga.getText() != null ? txtTarga.getText().toUpperCase().trim() : "";
        
        if (tipologia == null || tipologia.isEmpty()) {
            AlertPersonalizzato.mostraErrore("Dati Mancanti", "Seleziona la tipologia di veicolo.");
            return;
        }
        if (nome == null || nome.trim().isEmpty()) {
            AlertPersonalizzato.mostraErrore("Dati Mancanti", "Inserisci il nome del veicolo.");
            return;
        }
        if (targa.isEmpty()) {
            AlertPersonalizzato.mostraErrore("Dati Mancanti", "Inserisci la targa del veicolo.");
            return;
        }
        
        // Validazione formati targa
        if ("Autoveicolo".equalsIgnoreCase(tipologia)) {
            if (!targa.matches("^[A-Z]{2}[0-9]{3}[A-Z]{2}$")) {
                AlertPersonalizzato.mostraErrore("Formato Targa Errato", "Per gli Autoveicoli la targa deve essere: 2 Lettere, 3 Numeri, 2 Lettere\n(es. AB123CD)");
                return;
            }
        } else if ("Motociclo".equalsIgnoreCase(tipologia)) {
            if (!targa.matches("^[A-Z]{2}[0-9]{5}$")) {
                AlertPersonalizzato.mostraErrore("Formato Targa Errato", "Per i Motocicli la targa deve essere: 2 Lettere, 5 Numeri\n(es. AB12345)");
                return;
            }
        } else if ("Ciclomotore".equalsIgnoreCase(tipologia)) {
            if (!targa.matches("^[A-Z0-9]{6}$")) {
                AlertPersonalizzato.mostraErrore("Formato Targa Errato", "Per i Ciclomotori la targa deve essere esattamente di 6 caratteri alfanumerici\n(es. A1B2C3)");
                return;
            }
        }

        Veicolo v = veicoloInModifica != null ? veicoloInModifica : new Veicolo();
        v.setCodiceFiscaleUtente(SessioneUtente.getInstance().getUtente().getCodiceFiscale());
        v.setTipologia(tipologia);
        v.setNome(nome);
        v.setTarga(targa);
        v.setDataAssicurazione(dpAssicurazione.getValue());
        v.setValiditaAssicurazione(cmbValidita.getValue());
        v.setDataImmatricolazione(dpImmatricolazione.getValue());
        v.setPrimaRevisioneFatta(tglPrimaRevisione.isSelected());
        v.setDataUltimaRevisione(dpUltimaRevisione.getValue());
        
        boolean ok;
        if (veicoloInModifica == null) {
            ok = VeicoloDAO.inserisciVeicolo(v);
        } else {
            ok = VeicoloDAO.aggiornaVeicolo(v);
        }
        
        if (ok) {
            pulisciForm();
            chiudiModale(null);
            aggiornaListaVeicoli();
        } else {
            AlertPersonalizzato.mostraErrore("Errore", "Impossibile salvare il veicolo. Riprova.");
        }
    }
    
    @FXML
    void chiudiModale(ActionEvent event) {
        // Nasconde il form e ripristina la vista del garage
        formView.setVisible(false);
        garageView.setVisible(true);
    }
    
    private void calcolaScadenzaAssicurazione() {
        LocalDate data = dpAssicurazione.getValue();
        if (data == null) {
            lblScadenzaAssicurazione.setText("Scadenza prevista: [Calcolata in tempo reale]");
            return;
        }
        
        String validita = cmbValidita.getValue();
        LocalDate scadenza = "Annuale".equals(validita) ? data.plusYears(1) : data.plusMonths(6);
        lblScadenzaAssicurazione.setText("Scadenza prevista: " + scadenza.format(formatter));
    }
    
    private void calcolaScadenzaRevisione() {
        if (tglPrimaRevisione.isSelected()) {
            LocalDate data = dpUltimaRevisione.getValue();
            if (data == null) {
                lblScadenzaRevisione.setText("Scadenza ricalcolata (2 anni): [Calcolata in tempo reale]");
            } else {
                lblScadenzaRevisione.setText("Scadenza ricalcolata (2 anni): " + data.plusYears(2).format(formatter));
            }
        } else {
            LocalDate data = dpImmatricolazione.getValue();
            if (data == null) {
                lblScadenzaRevisione.setText("Scadenza prevista: [Calcolata in tempo reale]");
            } else {
                lblScadenzaRevisione.setText("Scadenza prevista (4 anni): " + data.plusYears(4).format(formatter));
            }
        }
    }
    
    private void aggiornaListaVeicoli() {
        if (SessioneUtente.getInstance().getUtente() == null) return;
        
        List<Veicolo> veicoli = VeicoloDAO.getVeicoliPerUtente(SessioneUtente.getInstance().getUtente().getCodiceFiscale());
        
        if (veicoli.isEmpty()) {
            emptyStateBox.setVisible(true);
            emptyStateBox.setManaged(true);
            scrollVeicoli.setVisible(false);
            scrollVeicoli.setManaged(false);
        } else {
            emptyStateBox.setVisible(false);
            emptyStateBox.setManaged(false);
            scrollVeicoli.setVisible(true);
            scrollVeicoli.setManaged(true);
            
            listaVeicoliBox.getChildren().clear();
            for (Veicolo v : veicoli) {
                listaVeicoliBox.getChildren().add(creaCardVeicolo(v));
            }
        }
    }
    
    private HBox creaCardVeicolo(Veicolo v) {
        HBox card = new HBox(20);
        card.getStyleClass().add("profile-card"); // Stile standard: bianco in tema chiaro, scuro in tema scuro
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        
        // 0. Icona quadrata a sinistra con colore pastello
        StackPane iconBox = new StackPane();
        iconBox.setPrefSize(90, 90);
        iconBox.setMinSize(90, 90);
        
        ImageView iconView = new ImageView();
        iconView.setFitWidth(65);
        iconView.setFitHeight(65);
        iconView.setPreserveRatio(true);
        
        if ("Motociclo".equalsIgnoreCase(v.getTipologia())) {
            iconBox.setStyle("-fx-background-color: #fef08a; -fx-background-radius: 12;");
            try {
                iconView.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/moto-icon.png")));
            } catch (Exception e) { e.printStackTrace(); }
        } else if ("Ciclomotore".equalsIgnoreCase(v.getTipologia())) {
            iconBox.setStyle("-fx-background-color: #bae6fd; -fx-background-radius: 12;"); // Azzurro pastello
            try {
                iconView.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/ciclo-icon.png")));
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            iconBox.setStyle("-fx-background-color: #fecaca; -fx-background-radius: 12;");
            try {
                iconView.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/auto-icon.png")));
                // L'icona auto ha del padding trasparente sbilanciato, la abbassiamo leggermente per centrarla
                iconView.setTranslateY(5);
            } catch (Exception e) { e.printStackTrace(); }
        }
        iconBox.getChildren().add(iconView);
        
        // 1. Titolo e Tipologia
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        Label lblTipo = new Label(v.getTipologia());
        lblTipo.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b;");
        Label lblNome = new Label(v.getNome());
        lblNome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        // Applichiamo la classe label per fargli ereditare il colore corretto nel tema scuro, 
        // oppure forziamo se necessario, ma profile-name (se c'è in CSS) gestisce già.
        lblNome.getStyleClass().add("profile-name");
        
        infoBox.getChildren().addAll(lblTipo, lblNome);
        
        // Spazio flessibile tra il Nome del veicolo e la parte destra
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        
        // 2. Targa (sfondo immagine vera)
        StackPane targaBox = new StackPane();
        
        ImageView targaBg = new ImageView();
        targaBg.setFitHeight(60); // Regola l'altezza della targa
        targaBg.setPreserveRatio(true);
        try {
            targaBg.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/targa-bg.png")));
        } catch (Exception e) { e.printStackTrace(); }
        
        Label lblTarga = new Label(v.getTarga() != null && !v.getTarga().isEmpty() ? v.getTarga().toUpperCase() : "N/D");
        // Testo riportato al font bold standard, ingrandito e allineato ai nuovi spazi
        lblTarga.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #000000; -fx-background-color: transparent; -fx-padding: 0 15 0 15;");
        lblTarga.setMaxWidth(170);
        lblTarga.setAlignment(Pos.CENTER);
        
        targaBox.getChildren().addAll(targaBg, lblTarga);
        
        // 3. Scadenze
        VBox scadenzeBox = new VBox(10);
        scadenzeBox.setAlignment(Pos.CENTER_RIGHT);
        
        // Assicurazione calcolo
        LocalDate scaAssicurazione = null;
        if (v.getDataAssicurazione() != null) {
            scaAssicurazione = "Annuale".equals(v.getValiditaAssicurazione()) ? v.getDataAssicurazione().plusYears(1) : v.getDataAssicurazione().plusMonths(6);
        }
        HBox boxScadAssic = creaBoxScadenza("Assicurazione", scaAssicurazione);
        
        // Revisione calcolo
        LocalDate scaRevisione = null;
        if (v.isPrimaRevisioneFatta() && v.getDataUltimaRevisione() != null) {
            scaRevisione = v.getDataUltimaRevisione().plusYears(2);
        } else if (!v.isPrimaRevisioneFatta() && v.getDataImmatricolazione() != null) {
            scaRevisione = v.getDataImmatricolazione().plusYears(4);
        }
        HBox boxScadRev = creaBoxScadenza("Revisione", scaRevisione);
        
        scadenzeBox.getChildren().addAll(boxScadAssic, boxScadRev);
        
        // 4. Bottoni Azione (Modifica e Elimina)
        VBox buttonsBox = new VBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        Button btnEdit = new Button("⚙");
        btnEdit.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        btnEdit.setOnAction(e -> apriModaleModifica(v));
        
        Button btnDelete = new Button("X");
        btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        btnDelete.setOnAction(e -> {
            AlertPersonalizzato.mostraConferma("Conferma eliminazione", "Vuoi davvero eliminare il veicolo " + v.getNome() + "?", "Sì, elimina").ifPresent(res -> {
                if (res == ButtonType.OK) {
                    VeicoloDAO.eliminaVeicolo(v.getId());
                    aggiornaListaVeicoli();
                }
            });
        });
        
        buttonsBox.getChildren().addAll(btnEdit, btnDelete);
        
        // Container di destra per tenere Targa, Scadenze e Bottoni vicini
        HBox rightSideBox = new HBox(15);
        rightSideBox.setAlignment(Pos.CENTER_RIGHT);
        rightSideBox.getChildren().addAll(targaBox, scadenzeBox, buttonsBox);
        
        card.getChildren().addAll(iconBox, infoBox, spacer1, rightSideBox);
        return card;
    }
    
    private HBox creaBoxScadenza(String prefisso, LocalDate scadenza) {
        HBox box = new HBox();
        box.setPrefWidth(280);
        box.setMinHeight(35);
        box.setAlignment(Pos.CENTER_LEFT);
        
        Label lbl = new Label();
        lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        ImageView iconView = new ImageView();
        iconView.setFitWidth(20);
        iconView.setFitHeight(20);
        iconView.setPreserveRatio(true);
        
        if (scadenza == null) {
            lbl.setText(prefisso + ": Dato Mancante");
            lbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151;");
            box.setStyle("-fx-background-color: #e5e7eb; -fx-border-color: #9ca3af; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 15;");
            box.getChildren().addAll(lbl, spacer);
            return box;
        }
        
        LocalDate oggi = LocalDate.now();
        long giorniRimasti = ChronoUnit.DAYS.between(oggi, scadenza);
        
        String testoBase = prefisso + ": " + scadenza.format(formatter);
        lbl.setText(testoBase);
        
        if (giorniRimasti < 0) {
            // Scaduta (ROSSO) - usa colore #F17773
            lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #610d0a;");
            box.setStyle("-fx-background-color: #F17773; -fx-border-color: #c94b46; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 15;");
            try {
                iconView.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/status-red.png")));
            } catch (Exception e) {}
        } else if (giorniRimasti <= 30) {
            // In scadenza (GIALLO) - usa colore #FAD576
            lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #705006;");
            box.setStyle("-fx-background-color: #FAD576; -fx-border-color: #c79b32; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 15;");
            try {
                iconView.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/status-yellow.png")));
            } catch (Exception e) {}
        } else {
            // Valida (VERDE) - usa colore #89D19E
            lbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0c401d;");
            box.setStyle("-fx-background-color: #89D19E; -fx-border-color: #539e6a; -fx-border-width: 1px; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 5 15;");
            try {
                iconView.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/status-green.png")));
            } catch (Exception e) {}
        }
        
        box.getChildren().addAll(lbl, spacer, iconView);
        return box;
    }
}
