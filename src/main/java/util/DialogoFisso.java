package util;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX
// =================================================================================
import javafx.scene.control.Dialog; // Classe base per tutti i dialoghi (popup) in JavaFX
import javafx.stage.Stage;          // Finestra del dialogo
import javafx.stage.Window;         // Classe padre di Stage, rappresenta una finestra generica sul desktop

import java.util.Optional;          // Wrapper per gestire il risultato (l'utente potrebbe chiudere la finestra senza cliccare nulla)

/**
 * =================================================================================
 * CLASSE DI UTILITA' - DIALOGO FISSO (DialogoFisso)
 * =================================================================================
 * Questa classe ha uno scopo molto specifico: risolvere un problema estetico di JavaFX.
 * I dialoghi di default (Alert, TextInputDialog) possono essere trascinati dall'utente.
 * Poiché l'applicazione MyPatenti ha un design molto "bloccato" e moderno,
 * vogliamo impedire all'utente di poter trascinare i popup in giro per lo schermo.
 *
 * Questa classe non rimuove la barra del titolo (quello lo fa StageStyle.UNDECORATED),
 * ma ascolta i cambiamenti di coordinata X e Y e li "forza" a rimanere fermi.
 */
public final class DialogoFisso {

    /**
     * Costruttore privato: essendo una classe di utilità (solo metodi statici),
     * impedisce che qualcuno possa creare un oggetto 'new DialogoFisso()'.
     */
    private DialogoFisso() {
    }

    /**
     * Mostra un dialogo e ne impedisce contemporaneamente il trascinamento.
     * È una scorciatoia (wrapper) per dialog.showAndWait().
     *
     * @param dialogo Il dialogo JavaFX da mostrare.
     * @param <R> Il tipo di dato restituito dal dialogo (es. ButtonType, String).
     * @return Un Optional contenente la risposta dell'utente, se presente.
     */
    public static <R> Optional<R> mostra(Dialog<R> dialogo) {
        bloccaPosizione(dialogo);
        return dialogo.showAndWait();
    }

    /**
     * Aggiunge un "ascoltatore" (listener) alla finestra del dialogo.
     * Appena il dialogo appare sullo schermo, registra la sua posizione iniziale (X, Y)
     * e forza la finestra a tornare su queste coordinate se l'utente tenta di spostarla.
     *
     * @param dialogo Il dialogo a cui impedire il movimento.
     */
    public static void bloccaPosizione(Dialog<?> dialogo) {
        // L'evento setOnShown scatta esattamente nel momento in cui la finestra diventa visibile
        dialogo.setOnShown(event -> {
            // Ottiene il riferimento alla finestra (Window) che contiene il dialogo
            Window finestra = dialogo.getDialogPane().getScene().getWindow();
            
            // Si assicura che la finestra sia effettivamente uno Stage (finestra standard di JavaFX)
            if (!(finestra instanceof Stage stage)) {
                return; // Se non è uno stage, interrompe l'esecuzione (sicurezza)
            }

            // 1. Salva la posizione iniziale in cui il sistema operativo ha disegnato la finestra
            double posizioneX = stage.getX();
            double posizioneY = stage.getY();

            // 2. Aggiunge un "segugio" (Listener) che controlla la proprietà X (spostamento orizzontale)
            stage.xProperty().addListener((osservabile, precedente, nuova) -> {
                // Se la nuova coordinata X differisce dall'originale per più di 0.5 pixel
                if (Math.abs(nuova.doubleValue() - posizioneX) > 0.5) {
                    // Costringe la finestra a tornare alla posizione X iniziale!
                    stage.setX(posizioneX);
                }
            });
            
            // 3. Aggiunge un "segugio" (Listener) che controlla la proprietà Y (spostamento verticale)
            stage.yProperty().addListener((osservabile, precedente, nuova) -> {
                // Se la nuova coordinata Y differisce dall'originale per più di 0.5 pixel
                if (Math.abs(nuova.doubleValue() - posizioneY) > 0.5) {
                    // Costringe la finestra a tornare alla posizione Y iniziale!
                    stage.setY(posizioneY);
                }
            });
        });
    }
}
