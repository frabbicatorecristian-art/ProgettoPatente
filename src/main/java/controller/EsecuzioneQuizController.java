package controller;

import dao.DomandaDAO;
import dao.QuizDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Domanda;
import model.Quiz;
import model.SessioneUtente;
import util.AlertPersonalizzato;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class EsecuzioneQuizController {

    @FXML private Label lblContatore;
    @FXML private Label lblTimer;
    @FXML private VBox boxImmagine;
    @FXML private Label lblIdDomanda;
    @FXML private ImageView imgDomanda;
    @FXML private Label lblNumeroDomanda;
    @FXML private Label lblTestoDomanda;
    @FXML private Button btnVero;
    @FXML private Button btnFalso;
    @FXML private VBox boxSpiegazione;
    @FXML private Label lblSpiegazione;
    @FXML private FlowPane paginatorePane;
    @FXML private Button btnInvia;
    @FXML private Button btnEsci;

    private List<Domanda> domande;
    private Boolean[] risposteUtente; 
    private int currentIndex = 0;
    
    private Timeline timer;
    private int secondiRimasti = 1200; // 20 minuti
    private boolean isRevisione = false;

    private Runnable onExitCallback;

    public void setRunnableOnExit(Runnable onExitCallback) {
        this.onExitCallback = onExitCallback;
    }

    @FXML
    public void initialize() {
        domande = DomandaDAO.getDomandeCasuali(30);
        risposteUtente = new Boolean[domande.size()];
        
        inizializzaPaginatore();
        avviaTimer();
        mostraDomanda(0);
    }

    private void inizializzaPaginatore() {
        paginatorePane.getChildren().clear();
        for (int i = 0; i < domande.size(); i++) {
            Button btnPagina = new Button(String.valueOf(i + 1));
            btnPagina.setStyle("-fx-background-color: #e2e8f0; -fx-background-radius: 50%; -fx-min-width: 35px; -fx-min-height: 35px; -fx-padding: 0; -fx-cursor: hand;");
            
            final int index = i;
            btnPagina.setOnAction(e -> mostraDomanda(index));
            paginatorePane.getChildren().add(btnPagina);
        }
    }

    private void avviaTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondiRimasti--;
            aggiornaLabelTimer();
            if (secondiRimasti <= 0) {
                timer.stop();
                javafx.application.Platform.runLater(() -> consegnaForzata());
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
        aggiornaLabelTimer();
    }

    private void aggiornaLabelTimer() {
        int minuti = secondiRimasti / 60;
        int secondi = secondiRimasti % 60;
        lblTimer.setText(String.format("%02d:%02d", minuti, secondi));
    }

    private void mostraDomanda(int index) {
        currentIndex = index;
        if (domande == null || domande.isEmpty()) return;
        Domanda d = domande.get(currentIndex);
        
        lblContatore.setText((currentIndex + 1) + " di " + domande.size());
        lblNumeroDomanda.setText("Domanda " + (currentIndex + 1));
        lblTestoDomanda.setText(d.getTesto());
        lblIdDomanda.setText("Codice: " + d.getId());
        
        if (d.getImmaginePath() != null) {
            boxImmagine.setVisible(true);
            boxImmagine.setManaged(true);
            try {
                imgDomanda.setImage(new Image(getClass().getResourceAsStream("/images/quiz/" + d.getImmaginePath())));
            } catch (Exception ex) {
                imgDomanda.setImage(null);
            }
        } else {
            boxImmagine.setVisible(false);
            boxImmagine.setManaged(false);
        }
        
        aggiornaStatoBottoniRisposta();
        aggiornaStatoPaginatore();
        
        if (isRevisione) {
            mostraRevisioneCorrente();
        } else {
            boxSpiegazione.setVisible(false);
            boxSpiegazione.setManaged(false);
        }
    }

    private void aggiornaStatoBottoniRisposta() {
        btnVero.getStyleClass().removeAll("quiz-choice-btn-selected");
        btnFalso.getStyleClass().removeAll("quiz-choice-btn-selected");
        btnVero.setStyle(""); // Pulisci inline styles della revisione
        btnFalso.setStyle("");
        
        if (risposteUtente[currentIndex] != null) {
            if (risposteUtente[currentIndex]) {
                btnVero.getStyleClass().add("quiz-choice-btn-selected");
            } else {
                btnFalso.getStyleClass().add("quiz-choice-btn-selected");
            }
        }
        
        btnVero.setDisable(isRevisione);
        btnFalso.setDisable(isRevisione);
    }

    private void aggiornaStatoPaginatore() {
        for (int i = 0; i < paginatorePane.getChildren().size(); i++) {
            Button btn = (Button) paginatorePane.getChildren().get(i);
            
            String baseStyle = "-fx-min-width: 35px; -fx-min-height: 35px; -fx-padding: 0; -fx-background-radius: 50%; -fx-cursor: hand;";
            
            if (isRevisione) {
                if (risposteUtente[i] == null) {
                    btn.setStyle(baseStyle + " -fx-background-color: #cbd5e1;");
                } else {
                    boolean corretta = (risposteUtente[i] == domande.get(i).isVero());
                    if (corretta) {
                        btn.setStyle(baseStyle + " -fx-background-color: #22c55e; -fx-text-fill: white;");
                    } else {
                        btn.setStyle(baseStyle + " -fx-background-color: #ef4444; -fx-text-fill: white;");
                    }
                }
            } else {
                if (i == currentIndex) {
                    btn.setStyle(baseStyle + " -fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold;");
                } else if (risposteUtente[i] != null) {
                    btn.setStyle(baseStyle + " -fx-background-color: #94a3b8; -fx-text-fill: white;");
                } else {
                    btn.setStyle(baseStyle + " -fx-background-color: #e2e8f0;");
                }
            }
        }
    }

    @FXML
    void selezionaVero(ActionEvent event) {
        risposteUtente[currentIndex] = true;
        avanzaAutomaticamente();
    }

    @FXML
    void selezionaFalso(ActionEvent event) {
        risposteUtente[currentIndex] = false;
        avanzaAutomaticamente();
    }

    private void avanzaAutomaticamente() {
        aggiornaStatoBottoniRisposta();
        aggiornaStatoPaginatore();
        if (currentIndex < domande.size() - 1) {
            mostraDomanda(currentIndex + 1);
        }
    }

    @FXML
    void consegnaQuiz(ActionEvent event) {
        int risposteDate = 0;
        for (Boolean b : risposteUtente) if (b != null) risposteDate++;
        
        if (risposteDate < domande.size()) {
            AlertPersonalizzato.mostraErrore("Impossibile Consegnare", "Devi rispondere a TUTTE le 30 domande prima di poter inviare il quiz.\nDomande risposte: " + risposteDate + " su " + domande.size());
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Consegna Quiz");
        alert.setHeaderText("Sei sicuro di voler consegnare il quiz?");
        alert.setContentText("Hai risposto a tutte le domande. Procedere con la consegna e la correzione?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eseguiConsegna(false);
        }
    }

    private void consegnaForzata() {
        eseguiConsegna(true);
    }

    private void eseguiConsegna(boolean tempoScaduto) {
        if (timer != null) timer.stop();
        lblTimer.setVisible(false);
        
        int errori = 0;
        for (int i = 0; i < domande.size(); i++) {
            if (risposteUtente[i] == null || risposteUtente[i] != domande.get(i).isVero()) {
                errori++;
            }
        }
        
        String esito = errori <= 3 ? "IDONEO" : "RESPINTO";
        
        if (SessioneUtente.getInstance().isLoggato()) {
            Quiz quizSalvato = new Quiz(0, SessioneUtente.getInstance().getUtente().getCodiceFiscale(), LocalDateTime.now(), errori, esito);
            QuizDAO.inserisciQuiz(quizSalvato);
        }
        
        String msgInfo = "";
        if (tempoScaduto) {
            msgInfo = "IL TEMPO A DISPOSIZIONE È TERMINATO!\nIl quiz è stato consegnato automaticamente.\nLe risposte non date sono state contate come errori.\n\n";
        }
        
        String msg = msgInfo + "Hai commesso " + errori + " errori.\nEsito: " + esito;
        AlertPersonalizzato.mostraInfo("Risultato Quiz", msg);
        
        isRevisione = true;
        btnInvia.setVisible(false);
        btnInvia.setManaged(false);
        btnEsci.setVisible(true);
        btnEsci.setManaged(true);
        
        mostraDomanda(0);
        aggiornaStatoPaginatore();
    }

    private void mostraRevisioneCorrente() {
        Domanda d = domande.get(currentIndex);
        boxSpiegazione.setVisible(true);
        boxSpiegazione.setManaged(true);
        
        lblSpiegazione.setText(d.getSpiegazione() != null ? d.getSpiegazione() : "Nessuna spiegazione disponibile.");
        
        if (d.isVero()) {
            btnVero.setStyle("-fx-background-color: #dcfce7; -fx-border-color: #22c55e; -fx-text-fill: #166534; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
            btnFalso.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-text-fill: #94a3b8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 18px; -fx-padding: 10 40;");
            if (risposteUtente[currentIndex] != null && !risposteUtente[currentIndex]) {
                btnFalso.setStyle("-fx-background-color: #fee2e2; -fx-border-color: #ef4444; -fx-text-fill: #991b1b; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 18px; -fx-padding: 10 40; -fx-font-weight: bold;");
            }
        } else {
            btnFalso.setStyle("-fx-background-color: #dcfce7; -fx-border-color: #22c55e; -fx-text-fill: #166534; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10 40;");
            btnVero.setStyle("-fx-background-color: #f1f5f9; -fx-border-color: #cbd5e1; -fx-text-fill: #94a3b8; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 18px; -fx-padding: 10 40;");
            if (risposteUtente[currentIndex] != null && risposteUtente[currentIndex]) {
                btnVero.setStyle("-fx-background-color: #fee2e2; -fx-border-color: #ef4444; -fx-text-fill: #991b1b; -fx-border-radius: 8; -fx-background-radius: 8; -fx-font-size: 18px; -fx-padding: 10 40; -fx-font-weight: bold;");
            }
        }
    }

    @FXML
    void esci(ActionEvent event) {
        if (onExitCallback != null) {
            onExitCallback.run();
        }
    }
}
