package controller;

import dao.QuizDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import model.Quiz;
import model.SessioneUtente;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuizController {

    @FXML private Arc arcProgress;
    @FXML private Label lblProgress;
    @FXML private Label lblTotali;
    @FXML private Label lblIdonei;
    @FXML private Label lblRespinti;
    @FXML private Label lblMediaBox;
    @FXML private VBox storicoVBox;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy - HH:mm", java.util.Locale.ITALIAN);

    @FXML
    public void initialize() {
        caricaDatiQuiz();
    }

    private void caricaDatiQuiz() {
        if (!SessioneUtente.getInstance().isLoggato()) return;

        String cf = SessioneUtente.getInstance().getUtente().getCodiceFiscale();
        List<Quiz> listaQuiz = QuizDAO.getQuizByUtente(cf);

        storicoVBox.getChildren().clear();

        if (listaQuiz.isEmpty()) {
            Label lblEmpty = new Label("Nessun quiz svolto finora.");
            lblEmpty.setStyle("-fx-font-style: italic; -fx-text-fill: #94a3b8;");
            storicoVBox.getChildren().add(lblEmpty);

            lblTotali.setText("0");
            lblIdonei.setText("0");
            lblRespinti.setText("0");
            lblMediaBox.setText("0.0");
            lblProgress.setText("0%");
            arcProgress.setLength(0);
            return;
        }

        int totali = listaQuiz.size();
        int idonei = 0;
        int sumErrori = 0;

        for (Quiz q : listaQuiz) {
            sumErrori += q.getErrori();
            if (q.getEsito().equalsIgnoreCase("IDONEO")) {
                idonei++;
            }

            // Aggiungi riga allo storico
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("history-row");
            
            Label lblData = new Label(q.getDataOra().format(formatter));
            lblData.setStyle("-fx-font-size: 14px;");
            lblData.getStyleClass().add("history-row-date");
            
            Region r1 = new Region();
            HBox.setHgrow(r1, Priority.ALWAYS);
            
            Label lblErr = new Label(q.getErrori() + (q.getErrori() == 1 ? " Errore" : " Errori"));
            lblErr.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            lblErr.getStyleClass().add("history-row-text");
            
            Region r2 = new Region();
            HBox.setHgrow(r2, Priority.ALWAYS);
            
            HBox badge = new HBox();
            badge.setAlignment(Pos.CENTER);
            badge.setSpacing(5);
            badge.setPadding(new Insets(5, 10, 5, 10));
            badge.setStyle("-fx-background-radius: 20;");
            
            Label lblEsito = new Label(q.getEsito().toUpperCase());
            lblEsito.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
            
            ImageView iconBadge = new ImageView();
            iconBadge.setFitHeight(14);
            iconBadge.setFitWidth(14);
            
            if (q.getEsito().equalsIgnoreCase("IDONEO")) {
                badge.setStyle(badge.getStyle() + "-fx-background-color: #89D19E; -fx-border-color: #539e6a; -fx-border-width: 1px; -fx-border-radius: 20;");
                lblEsito.setStyle(lblEsito.getStyle() + "-fx-text-fill: #0c401d;");
                try { iconBadge.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/status-green.png"))); } catch (Exception e) {}
            } else {
                badge.setStyle(badge.getStyle() + "-fx-background-color: #F17773; -fx-border-color: #c94b46; -fx-border-width: 1px; -fx-border-radius: 20;");
                lblEsito.setStyle(lblEsito.getStyle() + "-fx-text-fill: #610d0a;");
                try { iconBadge.setImage(new Image(getClass().getResourceAsStream("/images/vehicle/status-red.png"))); } catch (Exception e) {}
            }
            badge.getChildren().addAll(lblEsito, iconBadge);
            
            row.getChildren().addAll(lblData, r1, lblErr, r2, badge);
            storicoVBox.getChildren().add(row);
        }

        double media = (double) sumErrori / totali;
        int respinti = totali - idonei;
        double passRate = (double) idonei / totali;

        lblTotali.setText(String.valueOf(totali));
        lblIdonei.setText(String.valueOf(idonei));
        lblRespinti.setText(String.valueOf(respinti));
        lblMediaBox.setText(String.format(java.util.Locale.US, "%.1f", media));
        
        if (media > 3.0) {
            lblMediaBox.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
        } else {
            lblMediaBox.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #16a34a;");
        }

        lblProgress.setText(String.format("%d%%", (int)(passRate * 100)));

        // -180 is the full arc for JavaFX when start angle is 180 (it sweeps clockwise if negative).
        double sweep = -180.0 * passRate;
        arcProgress.setLength(sweep);
        if (passRate >= 0.9) {
            arcProgress.setStroke(Color.web("#16a34a")); // green
        } else if (passRate >= 0.7) {
            arcProgress.setStroke(Color.web("#eab308")); // yellow
        } else {
            arcProgress.setStroke(Color.web("#dc2626")); // red
        }
    }

    @FXML
    void iniziaQuiz(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/view/EsecuzioneQuiz.fxml"));
            javafx.scene.Parent root = loader.load();
            EsecuzioneQuizController ctrl = loader.getController();
            
            javafx.scene.layout.VBox contentArea = (javafx.scene.layout.VBox) arcProgress.getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(root);
            
            if (DashboardController.getInstance() != null) {
                DashboardController.getInstance().mostraDock(false);
            }
            
            ctrl.setRunnableOnExit(() -> {
                if (DashboardController.getInstance() != null) {
                    DashboardController.getInstance().mostraDock(true);
                    DashboardController.getInstance().apriSezioneQuiz();
                } else {
                    try {
                        javafx.scene.Parent quizView = javafx.fxml.FXMLLoader.load(getClass().getResource("/view/Quiz.fxml"));
                        contentArea.getChildren().setAll(quizView);
                    } catch(Exception e) { e.printStackTrace(); }
                }
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
