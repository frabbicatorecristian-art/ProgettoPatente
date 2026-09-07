package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VisualizzatoreController {

    @FXML private VBox boxThumbnails;
    @FXML private ImageView imgMainSlide;
    @FXML private Button btnIndietro;
    @FXML private Button btnAvanti;
    @FXML private Label lblTracker;
    @FXML private StackPane imageContainer;

    private List<Image> slides = new ArrayList<>();
    private int currentIndex = 0;
    private String modulo;

    @FXML
    public void initialize() {
        // Lega (bind) le dimensioni dell'immagine a quelle del contenitore
        // permettendo alla slide di occupare tutto lo spazio disponibile dinamicamente
        if (imageContainer != null && imgMainSlide != null) {
            imgMainSlide.fitWidthProperty().bind(imageContainer.widthProperty());
            imgMainSlide.fitHeightProperty().bind(imageContainer.heightProperty());
        }
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
        caricaSlides();
        mostraSlide(0);
    }

    private void caricaSlides() {
        slides.clear();
        boxThumbnails.getChildren().clear();
        
        int i = 1;
        while (true) {
            String path = "/images/teoria/" + modulo + "/slide_" + i + ".png";
            InputStream stream = getClass().getResourceAsStream(path);
            
            // Fallback to jpg if png is not found
            if (stream == null) {
                path = "/images/teoria/" + modulo + "/slide_" + i + ".jpg";
                stream = getClass().getResourceAsStream(path);
            }
            
            if (stream == null) {
                break; // No more slides found
            }

            Image img = new Image(stream);
            slides.add(img);

            // Create thumbnail
            ImageView thumb = new ImageView(img);
            thumb.setFitWidth(160);
            thumb.setPreserveRatio(true);
            thumb.setCursor(javafx.scene.Cursor.HAND);
            
            StackPane thumbWrapper = new StackPane(thumb);
            thumbWrapper.getStyleClass().add("thumb-wrapper");
            final int index = i - 1;
            thumbWrapper.setOnMouseClicked(e -> mostraSlide(index));
            
            boxThumbnails.getChildren().add(thumbWrapper);
            i++;
        }

        if (slides.isEmpty()) {
            lblTracker.setText("Nessuna slide trovata.");
            btnAvanti.setDisable(true);
            btnIndietro.setDisable(true);
        }
    }

    private void mostraSlide(int index) {
        if (slides.isEmpty()) return;
        
        currentIndex = index;
        imgMainSlide.setImage(slides.get(currentIndex));
        lblTracker.setText("Slide " + (currentIndex + 1) + " / " + slides.size());

        btnIndietro.setDisable(currentIndex == 0);
        btnAvanti.setDisable(currentIndex == slides.size() - 1);

        // Highlight the current thumbnail
        for (int i = 0; i < boxThumbnails.getChildren().size(); i++) {
            StackPane wrapper = (StackPane) boxThumbnails.getChildren().get(i);
            if (i == currentIndex) {
                wrapper.getStyleClass().setAll("thumb-wrapper-active");
            } else {
                wrapper.getStyleClass().setAll("thumb-wrapper");
            }
        }
    }

    @FXML
    void avanti() {
        if (currentIndex < slides.size() - 1) {
            mostraSlide(currentIndex + 1);
        }
    }

    @FXML
    void indietro() {
        if (currentIndex > 0) {
            mostraSlide(currentIndex - 1);
        }
    }

    @FXML
    void chiudi() {
        DashboardController.getInstance().apriSezioneTeoria();
    }
}
