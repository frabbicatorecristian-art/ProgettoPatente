package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import util.AlertPersonalizzato;

public class PatentiController {

    @FXML private VBox patentiView;
    @FXML private VBox formView;
    
    @FXML private DatePicker dpDataNascita;
    @FXML private DatePicker dpDataRilascio;
    @FXML private ComboBox<String> cmbCategoria;
    
    @FXML private TextField txtNumeroPatente;
    @FXML private ComboBox<String> cmbEnteRilascio;
    @FXML private Spinner<Integer> spnPunti;
    @FXML private VBox emptyStateBox;
    @FXML private javafx.scene.layout.StackPane patenteConfigurataView;
    
    @FXML private javafx.scene.shape.Arc arcPunti;
    @FXML private Label lblPuntiCentro;
    @FXML private Label lblIntestatario;
    @FXML private Label lblScadenzaCard;
    @FXML private Label lblNumeroPatente;
    @FXML private Label lblEnteRilascio;
    @FXML private javafx.scene.layout.FlowPane categorieFlowPane;

    @FXML private VBox aggiungiCategoriaView;
    @FXML private ComboBox<String> cmbNuovaCategoria;
    @FXML private DatePicker dpNuovaDataRilascio;
    @FXML private Label lblNuovaScadenzaCalcolata;

    @FXML
    public void initialize() {
        dao.PatenteDAO.creaTabelleSeNonEsistono();
        
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 30, 20);
        spnPunti.setValueFactory(valueFactory);
        
        cmbCategoria.getItems().addAll("AM", "A1", "B1", "B");
        cmbEnteRilascio.getItems().addAll("MIT-UCO", "MC-RC", "MC-CZ", "MC-CS", "MC-VV", "MC-KR");
        
        cmbNuovaCategoria.getItems().addAll("AM", "A1", "A2", "A", "B1", "B", "C", "D");
        
        dpNuovaDataRilascio.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                aggiornaScadenzaPrevista(newVal);
            } else {
                lblNuovaScadenzaCalcolata.setText("--/--/----");
            }
        });
        
        caricaDatiPatente();
    }
    
    private void aggiornaScadenzaPrevista(java.time.LocalDate nuovaDataRilascio) {
        String cf = model.SessioneUtente.getInstance().getUtente().getCodiceFiscale();
        model.Patente patente = dao.PatenteDAO.getPatenteByCodiceFiscale(cf);
        if (patente == null) return;
        
        // Trova la data più recente tra quella nuova, il fallback e tutte le categorie esistenti
        java.time.LocalDate ultimaDataRilascio = patente.getDataNascita();
        if (nuovaDataRilascio.isAfter(ultimaDataRilascio)) {
            ultimaDataRilascio = nuovaDataRilascio;
        }
        
        java.util.List<model.CategoriaPatente> esistenti = dao.PatenteDAO.getCategorieByPatenteId(patente.getId());
        for (model.CategoriaPatente cat : esistenti) {
            if (cat.getDataRilascio().isAfter(ultimaDataRilascio)) {
                ultimaDataRilascio = cat.getDataRilascio();
            }
        }
        
        java.time.LocalDate dataScadenza = ultimaDataRilascio.plusYears(10);
        java.time.LocalDate compleannoScadenza = dataScadenza.withMonth(patente.getDataNascita().getMonthValue())
                                                             .withDayOfMonth(patente.getDataNascita().getDayOfMonth());
        if (compleannoScadenza.isBefore(dataScadenza)) {
            compleannoScadenza = compleannoScadenza.plusYears(1);
        }
        
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.ITALIAN);
        lblNuovaScadenzaCalcolata.setText(compleannoScadenza.format(formatter));
    }
    
    private void caricaDatiPatente() {
        if (!model.SessioneUtente.getInstance().isLoggato()) return;
        
        String cf = model.SessioneUtente.getInstance().getUtente().getCodiceFiscale();
        model.Patente patente = dao.PatenteDAO.getPatenteByCodiceFiscale(cf);
        
        if (patente != null) {
            emptyStateBox.setVisible(false);
            patenteConfigurataView.setVisible(true);
            
            // Popola Dati Master
            String nomeComp = model.SessioneUtente.getInstance().getUtente().getNomeCompleto();
            lblIntestatario.setText(nomeComp != null ? nomeComp : "Utente Sconosciuto");
            
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.ITALIAN);
            
            lblNumeroPatente.setText(patente.getNumeroPatente());
            lblEnteRilascio.setText(patente.getEnteRilascio());
            
            // Logica Punti (Max 30)
            lblPuntiCentro.setText(patente.getSaldoPunti() + "/30");
            double percentuale = (double) patente.getSaldoPunti() / 30.0;
            arcPunti.setLength(-360.0 * percentuale); // Negative for clockwise drawing
            
            // Colore dinamico dell'arco in base ai punti
            if (patente.getSaldoPunti() >= 15) {
                arcPunti.setStroke(javafx.scene.paint.Color.web("#4ade80")); // Verde
            } else if (patente.getSaldoPunti() >= 10) {
                arcPunti.setStroke(javafx.scene.paint.Color.web("#facc15")); // Giallo
            } else {
                arcPunti.setStroke(javafx.scene.paint.Color.web("#ef4444")); // Rosso
            }
            
            // Carica Categorie e Trova Ultima Data di Rilascio
            java.util.List<model.CategoriaPatente> categorie = dao.PatenteDAO.getCategorieByPatenteId(patente.getId());
            categorieFlowPane.getChildren().clear();
            
            java.time.LocalDate ultimaDataRilascio = patente.getDataNascita(); // fallback
            
            for (model.CategoriaPatente cat : categorie) {
                if (cat.getDataRilascio().isAfter(ultimaDataRilascio)) {
                    ultimaDataRilascio = cat.getDataRilascio();
                }
                
                // Crea box per categoria
                javafx.scene.layout.StackPane containerBox = new javafx.scene.layout.StackPane();
                
                VBox box = new VBox();
                box.setSpacing(10);
                box.setAlignment(javafx.geometry.Pos.CENTER);
                box.setPrefSize(190, 120);
                
                // Assegna colore in base alla categoria
                String coloreBg = "#e2e8f0";
                String iconPath = "auto-icon.png";
                
                switch (cat.getCategoria()) {
                    case "AM": coloreBg = "#d8b4e2"; iconPath = "ciclo-icon.png"; break;
                    case "A1": coloreBg = "#fde047"; iconPath = "icon-a1.png"; break;
                    case "A2": coloreBg = "#a7f3d0"; iconPath = "moto-icon.png"; break;
                    case "A":  coloreBg = "#fca5a5"; iconPath = "icon-a.png"; break;
                    case "B1": coloreBg = "#93c5fd"; iconPath = "icon-b1.png"; break;
                    case "B":  coloreBg = "#fed7aa"; iconPath = "auto-icon.png"; break;
                    case "C":  coloreBg = "#cbd5e1"; iconPath = "icon-c.png"; break;
                    case "D":  coloreBg = "#fbcfe8"; iconPath = "icon-d.png"; break;
                }
                
                box.setStyle("-fx-background-color: " + coloreBg + "; -fx-background-radius: 12; -fx-padding: 15;");
                
                Label lblTitoloCat = new Label(cat.getCategoria());
                lblTitoloCat.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1e293b;");
                
                javafx.scene.image.ImageView imgIcon = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(getClass().getResourceAsStream("/images/vehicle/" + iconPath))
                );
                imgIcon.setFitHeight(40);
                imgIcon.setPreserveRatio(true);
                
                Label lblData = new Label("OTTENUTA: " + cat.getDataRilascio().format(formatter).toUpperCase());
                lblData.setStyle("-fx-font-size: 10px; -fx-text-fill: #334155; -fx-font-weight: bold;");
                
                box.getChildren().addAll(lblTitoloCat, imgIcon, lblData);
                
                // Pulsante X (Elimina)
                Button btnDelete = new Button("X");
                btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
                btnDelete.setOnAction(e -> {
                    if (categorie.size() == 1) {
                        java.util.Optional<ButtonType> res = util.AlertPersonalizzato.mostraConfermaDistruttiva(
                            "Elimina Patente",
                            "Attenzione: stai rimuovendo l'unica categoria rimasta. Facendo ciò, verrà rimossa l'intera patente dal database!",
                            "Elimina Patente"
                        );
                        if (res.isPresent() && res.get().getButtonData() == javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                            if (dao.PatenteDAO.eliminaPatente(patente.getId())) {
                                caricaDatiPatente();
                            }
                        }
                    } else {
                        java.util.Optional<ButtonType> res = util.AlertPersonalizzato.mostraConfermaDistruttiva(
                            "Rimuovi Categoria",
                            "Vuoi rimuovere definitivamente la categoria " + cat.getCategoria() + "?",
                            "Rimuovi"
                        );
                        if (res.isPresent() && res.get().getButtonData() == javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                            if (dao.PatenteDAO.eliminaCategoria(cat.getId())) {
                                caricaDatiPatente();
                            }
                        }
                    }
                });
                
                javafx.scene.layout.StackPane.setAlignment(btnDelete, javafx.geometry.Pos.TOP_RIGHT);
                javafx.scene.layout.StackPane.setMargin(btnDelete, new javafx.geometry.Insets(-8, -8, 0, 0));
                
                containerBox.getChildren().addAll(box, btnDelete);
                categorieFlowPane.getChildren().add(containerBox);
            }
            
            // Calcolo Scadenza
            java.time.LocalDate dataScadenza = ultimaDataRilascio.plusYears(10);
            java.time.LocalDate compleannoScadenza = dataScadenza.withMonth(patente.getDataNascita().getMonthValue())
                                                                 .withDayOfMonth(patente.getDataNascita().getDayOfMonth());
            
            if (compleannoScadenza.isBefore(dataScadenza)) {
                compleannoScadenza = compleannoScadenza.plusYears(1);
            }
            
            lblScadenzaCard.setText(compleannoScadenza.format(formatter));
            
        } else {
            emptyStateBox.setVisible(true);
            patenteConfigurataView.setVisible(false);
        }
    }

    @FXML
    void aggiungiCategoria(ActionEvent event) {
        patenteConfigurataView.setVisible(false);
        aggiungiCategoriaView.setVisible(true);
        cmbNuovaCategoria.setValue(null);
        dpNuovaDataRilascio.setValue(null);
        lblNuovaScadenzaCalcolata.setText("--/--/----");
    }
    
    @FXML
    void annullaAggiungiCategoria(ActionEvent event) {
        aggiungiCategoriaView.setVisible(false);
        patenteConfigurataView.setVisible(true);
    }
    
    @FXML
    void salvaNuovaCategoria(ActionEvent event) {
        if (cmbNuovaCategoria.getValue() == null || dpNuovaDataRilascio.getValue() == null) {
            util.AlertPersonalizzato.mostraErrore("Errore di Validazione", "Seleziona la categoria e la data di ottenimento.");
            return;
        }
        
        String cf = model.SessioneUtente.getInstance().getUtente().getCodiceFiscale();
        model.Patente patente = dao.PatenteDAO.getPatenteByCodiceFiscale(cf);
        if (patente != null) {
            // Controlla se la categoria esiste già
            java.util.List<model.CategoriaPatente> esistenti = dao.PatenteDAO.getCategorieByPatenteId(patente.getId());
            for (model.CategoriaPatente cat : esistenti) {
                if (cat.getCategoria().equals(cmbNuovaCategoria.getValue())) {
                    util.AlertPersonalizzato.mostraErrore("Categoria Duplicata", "Hai già inserito questa categoria sulla patente.");
                    return;
                }
            }

            boolean success = dao.PatenteDAO.inserisciCategoriaSingola(patente.getId(), cmbNuovaCategoria.getValue(), dpNuovaDataRilascio.getValue());
            if (success) {
                util.AlertPersonalizzato.mostraInfo("Successo", "Nuova categoria aggiunta correttamente!");
                aggiungiCategoriaView.setVisible(false);
                caricaDatiPatente(); // Reloads data, returns to patenteConfigurataView, and updates the global expiration!
            } else {
                util.AlertPersonalizzato.mostraErrore("Errore", "Impossibile salvare la nuova categoria.");
            }
        }
    }

    @FXML
    void modificaPunti(javafx.scene.input.MouseEvent event) {
        String cf = model.SessioneUtente.getInstance().getUtente().getCodiceFiscale();
        model.Patente patente = dao.PatenteDAO.getPatenteByCodiceFiscale(cf);
        if (patente == null) return;

        java.util.List<Integer> scelte = new java.util.ArrayList<>();
        for (int i = 30; i >= 1; i--) scelte.add(i); // Dal 30 all'1

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(patente.getSaldoPunti(), scelte);
        dialog.setTitle("Modifica Saldo Punti");
        dialog.setHeaderText("Aggiorna il saldo punti della tua patente");
        dialog.setContentText("Nuovo saldo:");

        java.util.Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(nuoviPunti -> {
            if (dao.PatenteDAO.aggiornaPuntiPatente(patente.getId(), nuoviPunti)) {
                caricaDatiPatente(); // Aggiorna l'interfaccia con colori e numero aggiornati
            } else {
                util.AlertPersonalizzato.mostraErrore("Errore", "Impossibile aggiornare i punti.");
            }
        });
    }

    @FXML
    void mostraFormConfigurazione(ActionEvent event) {
        patentiView.setVisible(false);
        formView.setVisible(true);
        
        // Svuota i campi in caso di ricreazione patente
        dpDataNascita.setValue(null);
        dpDataRilascio.setValue(null);
        cmbCategoria.setValue(null);
        txtNumeroPatente.setText("");
        cmbEnteRilascio.setValue(null);
        spnPunti.getValueFactory().setValue(20);
    }

    @FXML
    void nascondiFormConfigurazione(ActionEvent event) {
        formView.setVisible(false);
        patentiView.setVisible(true);
    }

    @FXML
    void salvaDatiPatente(ActionEvent event) {
        String numeroPatente = txtNumeroPatente.getText() != null ? txtNumeroPatente.getText().toUpperCase().trim() : "";
        
        // Controlli base su campi vuoti obbligatori
        if (dpDataNascita.getValue() == null || dpDataRilascio.getValue() == null || 
            cmbCategoria.getValue() == null || cmbEnteRilascio.getValue() == null || numeroPatente.isEmpty()) {
            AlertPersonalizzato.mostraErrore("Dati Mancanti", "Compila tutti i campi richiesti prima di salvare.");
            return;
        }
        
        // Controllo validità sintattica numero patente: 1 alfabetico + 8 alfanumerici + 1 alfabetico
        if (!numeroPatente.matches("^[A-Z][A-Z0-9]{8}[A-Z]$")) {
            AlertPersonalizzato.mostraErrore("Formato Patente Errato", "Il numero della patente deve essere composto da 10 caratteri:\n1 lettera iniziale, 8 caratteri alfanumerici e 1 lettera finale.\nEsempio: A12345678B");
            return;
        }

        // Crea oggetto Patente e inserisce nel database
        model.Patente master = new model.Patente(
            model.SessioneUtente.getInstance().getUtente().getCodiceFiscale(),
            numeroPatente,
            dpDataNascita.getValue(),
            cmbEnteRilascio.getValue(),
            spnPunti.getValue()
        );
        
        boolean success = dao.PatenteDAO.inserisciPatenteConCategoria(master, cmbCategoria.getValue(), dpDataRilascio.getValue());
        
        if (success) {
            AlertPersonalizzato.mostraInfo("Successo", "Patente inizializzata correttamente!");
            nascondiFormConfigurazione(null);
            caricaDatiPatente(); // Ricarica e mostra la view corretta
        } else {
            AlertPersonalizzato.mostraErrore("Errore", "Impossibile salvare la patente nel database.");
        }
    }
}
