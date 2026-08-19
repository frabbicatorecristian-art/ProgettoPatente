package util;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX PER LA CREAZIONE DEI DIALOGHI PERSONALIZZATI
// =================================================================================
import javafx.geometry.Insets;         // Margini interni dei contenitori
import javafx.geometry.Pos;            // Costanti per l'allineamento dei nodi
import javafx.scene.control.Button;    // Pulsante cliccabile
import javafx.scene.control.ButtonType; // Tipo di risposta del dialogo (OK, ANNULLA, ecc.)
import javafx.scene.control.Dialog;    // Finestra di dialogo base JavaFX
import javafx.scene.control.Label;     // Etichetta di testo
import javafx.scene.layout.HBox;       // Layout orizzontale per affiancare i pulsanti
import javafx.scene.layout.Region;     // Regione vuota per il "spacer" (spingere elementi)
import javafx.scene.layout.VBox;       // Layout verticale per impilare titolo, testo e pulsanti
import javafx.stage.Stage;             // La finestra del dialogo (StageStyle per rimuovere la decorazione)
import javafx.stage.StageStyle;        // Enum per lo stile della finestra (UNDECORATED = senza barra titolo)

import java.util.Optional;             // Wrapper per il valore di ritorno del dialogo (può essere "vuoto")

/**
 * =================================================================================
 * CLASSE DI UTILITA' - ALERT PERSONALIZZATO (AlertPersonalizzato)
 * =================================================================================
 * Questa classe sostituisce completamente i brutti Alert di sistema di JavaFX
 * con delle finestre di dialogo completamente personalizzate in stile MyPatenti.
 *
 * CARATTERISTICHE:
 * - Non spostabile (non ha la barra del titolo del sistema operativo).
 * - Sfondo con angoli arrotondati e colori del progetto.
 * - Pulsante di azione colorato (blu per info, rosso per errori e conferme pericolose).
 * - Testo chiaro e ben formattato.
 */
public class AlertPersonalizzato {

    // =============================================================================
    // COSTANTI DI STILE (Estratte dal file style.css del progetto)
    // =============================================================================
    private static final String COLORE_BLU          = "#0284c7"; // Blu primario MyPatenti
    private static final String COLORE_ROSSO        = "#e94560"; // Rosso primario MyPatenti
    private static final String COLORE_ROSSO_HOVER  = "#d1344e"; // Rosso scuro per hover
    private static final String COLORE_BLU_HOVER    = "#0369a1"; // Blu scuro per hover
    private static final String COLORE_SFONDO       = "#ffffff"; // Sfondo bianco
    private static final String COLORE_TESTO        = "#1e293b"; // Grigio scuro per testo
    private static final String COLORE_TESTO_LIGHT  = "#475569"; // Grigio medio per sottotesti
    private static final String COLORE_BORDO        = "#e2e8f0"; // Grigio chiaro per bordi

    // =============================================================================
    // METODI PUBBLICI - API della classe
    // =============================================================================

    /**
     * Mostra un dialogo di INFORMAZIONE (striscia superiore blu, pulsante OK blu).
     * Usato per comunicare all'utente un'operazione completata con successo.
     */
    public static void mostraInfo(String titolo, String messaggio) {
        costruisciDialogo(titolo, messaggio, "✅  " + titolo, COLORE_BLU, false, "OK").showAndWait();
    }

    /**
     * Mostra un dialogo di ERRORE (striscia superiore rossa, pulsante OK rosso).
     * Usato per segnalare all'utente che qualcosa è andato storto.
     */
    public static void mostraErrore(String titolo, String messaggio) {
        costruisciDialogo(titolo, messaggio, "❌  " + titolo, COLORE_ROSSO, false, "OK").showAndWait();
    }

    /**
     * Mostra un dialogo di CONFERMA generico (blu).
     * Usato per operazioni come il Logout.
     */
    public static Optional<ButtonType> mostraConferma(String titolo, String messaggio, String testoPulsanteConferma) {
        return costruisciDialogo(titolo, messaggio, "⚠️  " + titolo, COLORE_BLU, true, testoPulsanteConferma).showAndWait();
    }
    
    /**
     * Mostra un dialogo di CONFERMA DISTRUTTIVA (striscia rossa, pulsante rosso).
     * Usato per le operazioni irreversibili come Elimina Account.
     */
    public static Optional<ButtonType> mostraConfermaDistruttiva(String titolo, String messaggio, String testoPulsanteConferma) {
        return costruisciDialogo(titolo, messaggio, "⚠️  " + titolo, COLORE_ROSSO, true, testoPulsanteConferma).showAndWait();
    }

    // =============================================================================
    // METODO PRIVATO - Costruisce il dialogo grafico vero e proprio
    // =============================================================================

    private static Dialog<ButtonType> costruisciDialogo(
            String titolo, String messaggio, String headerTesto,
            String coloreHeader, boolean isConferma, String testoPulsante) {

        // 1. CREAZIONE DEL DIALOG BASE
        Dialog<ButtonType> dialog = new Dialog<>();
        // UNDECORATED rimuove la barra del titolo → la finestra non è più spostabile!
        dialog.initStyle(StageStyle.UNDECORATED);
        
        // Obbligatorio per Dialog: aggiunge almeno un ButtonType nascosto
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setVisible(false);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setManaged(false);

        // 2. INTESTAZIONE COLORATA
        Label lblHeader = new Label(headerTesto);
        lblHeader.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-padding: 16 20 16 20;"
        );

        HBox header = new HBox(lblHeader);
        header.setStyle(
            "-fx-background-color: " + coloreHeader + ";" +
            "-fx-background-radius: 14 14 0 0;"
        );

        // 3. TESTO DEL MESSAGGIO
        Label lblMessaggio = new Label(messaggio);
        lblMessaggio.setWrapText(true);
        lblMessaggio.setMaxWidth(340);
        lblMessaggio.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + COLORE_TESTO_LIGHT + ";" +
            "-fx-line-spacing: 3px;" +
            "-fx-padding: 0 4 0 4;"
        );

        VBox corpoTesto = new VBox(lblMessaggio);
        corpoTesto.setPadding(new Insets(18, 24, 10, 24));

        // 4. PULSANTI
        HBox rigaPulsanti = new HBox(10);
        rigaPulsanti.setPadding(new Insets(8, 20, 20, 20));
        rigaPulsanti.setAlignment(Pos.CENTER_RIGHT);

        if (isConferma) {
            Button btnConferma = new Button(testoPulsante);
            btnConferma.setStyle(buildStilePulsante(coloreHeader, "white"));
            btnConferma.setOnMouseEntered(e -> btnConferma.setStyle(buildStilePulsante(
                coloreHeader.equals(COLORE_ROSSO) ? COLORE_ROSSO_HOVER : COLORE_BLU_HOVER, "white")));
            btnConferma.setOnMouseExited(e -> btnConferma.setStyle(buildStilePulsante(coloreHeader, "white")));
            btnConferma.setOnAction(e -> {
                dialog.setResult(ButtonType.OK);
                dialog.close();
            });

            Button btnAnnulla = new Button("Annulla");
            btnAnnulla.setStyle(buildStilePulsanteOutline());
            btnAnnulla.setOnMouseEntered(e -> btnAnnulla.setStyle(
                buildStilePulsanteOutline() + "-fx-background-color: #f8fafc;"));
            btnAnnulla.setOnMouseExited(e -> btnAnnulla.setStyle(buildStilePulsanteOutline()));
            btnAnnulla.setOnAction(e -> {
                dialog.setResult(ButtonType.CANCEL);
                dialog.close();
            });

            rigaPulsanti.getChildren().addAll(btnAnnulla, btnConferma);
        } else {
            Button btnOk = new Button("   OK   ");
            btnOk.setStyle(buildStilePulsante(coloreHeader, "white"));
            btnOk.setOnMouseEntered(e -> btnOk.setStyle(buildStilePulsante(
                coloreHeader.equals(COLORE_ROSSO) ? COLORE_ROSSO_HOVER : COLORE_BLU_HOVER, "white")));
            btnOk.setOnMouseExited(e -> btnOk.setStyle(buildStilePulsante(coloreHeader, "white")));
            btnOk.setOnAction(e -> {
                dialog.setResult(ButtonType.OK);
                dialog.close();
            });

            rigaPulsanti.getChildren().add(btnOk);
        }

        // 5. SEPARATORE
        Region separatore = new Region();
        separatore.setPrefHeight(1);
        separatore.setStyle("-fx-background-color: " + COLORE_BORDO + ";");
        HBox.setHgrow(separatore, javafx.scene.layout.Priority.ALWAYS);

        // 6. ASSEMBLAGGIO FINALE
        VBox contenutoPrincipale = new VBox();
        contenutoPrincipale.getChildren().addAll(header, corpoTesto, separatore, rigaPulsanti);
        contenutoPrincipale.setStyle(
            "-fx-background-color: " + COLORE_SFONDO + ";" +
            "-fx-background-radius: 14px;" +
            "-fx-border-color: " + COLORE_BORDO + ";" +
            "-fx-border-radius: 14px;" +
            "-fx-border-width: 1px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 20, 0, 0, 4);"
        );

        dialog.getDialogPane().setContent(contenutoPrincipale);
        dialog.getDialogPane().setStyle(
            "-fx-background-color: transparent;" +
            "-fx-padding: 0;" +
            "-fx-border-width: 0;"
        );

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return dialog;
    }

    private static String buildStilePulsante(String sfondo, String testo) {
        return "-fx-background-color: " + sfondo + ";" +
               "-fx-text-fill: " + testo + ";" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: bold;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;" +
               "-fx-padding: 8 20 8 20;";
    }

    private static String buildStilePulsanteOutline() {
        return "-fx-background-color: transparent;" +
               "-fx-text-fill: " + COLORE_TESTO + ";" +
               "-fx-font-size: 13px;" +
               "-fx-font-weight: bold;" +
               "-fx-border-color: " + COLORE_BORDO + ";" +
               "-fx-border-radius: 8px;" +
               "-fx-background-radius: 8px;" +
               "-fx-cursor: hand;" +
               "-fx-padding: 8 20 8 20;";
    }
}
