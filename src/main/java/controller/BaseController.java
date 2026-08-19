package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.SessioneUtente;
import model.TemaManager;
import util.AlertPersonalizzato;

import java.io.IOException;

/**
 * Controller base da cui ereditano tutte le altre schermate.
 * Fornisce metodi di utilità condivisi per snellire il codice delle classi figlie.
 */
public abstract class BaseController {

    /**
     * Cambia schermata senza sfarfallio sostituendo il nodo radice della Scena attuale.
     * Applica automaticamente il tema coerente (Chiaro/Scuro).
     *
     * @param percorsoFxml Il percorso del file .fxml da caricare (es. "/view/Dashboard.fxml")
     * @param titoloFinestra Il nuovo titolo da applicare alla finestra
     * @param scenaAttuale La scena attualmente visibile (necessaria per fare il setRoot)
     */
    protected void naviga(String percorsoFxml, String titoloFinestra, Scene scenaAttuale) {
        if (scenaAttuale == null) {
            System.err.println("Errore: Impossibile navigare, scena attuale è null.");
            return;
        }

        try {
            Parent root = FXMLLoader.load(getClass().getResource(percorsoFxml));
            scenaAttuale.setRoot(root);
            
            // Applica il tema solo se NON siamo nelle schermate iniziali/di login.
            // Queste schermate hanno un loro sfondo CSS fisso (blu sfumato) e testi bianchi.
            if (!percorsoFxml.contains("Iniziale") && 
                !percorsoFxml.contains("Login") && 
                !percorsoFxml.contains("Registrazione")) {
                TemaManager.getInstance().applica(scenaAttuale);
            }
            
            // Aggiorna il titolo
            Stage stage = (Stage) scenaAttuale.getWindow();
            if (stage != null) {
                stage.setTitle(titoloFinestra);
            }
        } catch (IOException e) {
            System.err.println("Errore critico navigazione verso: " + percorsoFxml);
            e.printStackTrace();
        }
    }

    /**
     * Esegue il logout mostrando prima un popup di conferma.
     * Se confermato, disconnette l'utente e naviga verso la schermata iniziale.
     *
     * @param scenaAttuale La scena attuale per poter navigare in caso di conferma
     */
    protected void eseguiLogoutConConferma(Scene scenaAttuale) {
        AlertPersonalizzato.mostraConferma(
                "Conferma uscita",
                "Sei sicuro di voler effettuare il logout?",
                "Sì, esci").ifPresent(risposta -> {
            if (risposta == ButtonType.OK) {
                // Pulisce i dati in memoria
                SessioneUtente.getInstance().logout();
                
                // Torna al login
                naviga("/view/Home.fxml", "MyPatenti - Benvenuto", scenaAttuale);
                System.out.println("Logout completato con successo.");
            }
        });
    }
}
