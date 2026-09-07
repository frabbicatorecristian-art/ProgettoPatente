package controller;

import javafx.fxml.FXML;

public class TeoriaController {

    @FXML
    public void initialize() {
        // Inizializzazione della vista Teoria.
    }

    @FXML
    void apriModulo1(javafx.scene.input.MouseEvent event) {
        DashboardController.getInstance().apriVisualizzatoreTeoria("modulo1");
    }
}
