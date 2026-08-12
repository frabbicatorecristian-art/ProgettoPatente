package org.example;

// =================================================================================
// IMPORTAZIONI JAVAFX
// =================================================================================
import javafx.application.Application; // Classe astratta fondamentale che definisce il ciclo di vita di un'applicazione JavaFX.

/**
 * =================================================================================
 * CLASSE DI AVVIO PRINCIPALE (Main)
 * =================================================================================
 * Questa classe è l'unico punto d'ingresso (Entry Point) dell'applicazione.
 * 
 * PERCHÉ QUESTA CLASSE È NECESSARIA?
 * Nei progetti JavaFX moderni (da Java 11+ in poi), se la classe che contiene il metodo main()
 * estende direttamente 'Application', la Java Virtual Machine (JVM) controlla la presenza dei
 * moduli JavaFX prima dell'esecuzione. Se le librerie JavaFX non sono nel classpath dei moduli,
 * la JVM lancia l'errore: "JavaFX runtime components are missing, and are required to run this application".
 * 
 * Creando una classe 'Main' separata che NON estende 'Application', si aggira questo controllo
 * permettendo l'avvio corretto dell'applicazione sia da IDE (es. IntelliJ, Eclipse, VS Code) 
 * che da file JAR o Maven senza configurazioni complesse.
 *
 * CONFIGURAZIONE DI AVVIO:
 * In IntelliJ deve essere selezionata una sola configurazione di tipo Application
 * con classe principale "org.example.Main". La classe MainApp non viene avviata
 * direttamente: JavaFX la istanzia tramite Application.launch().
 */
public class Main {

    /**
     * Metodo main() standard richiesto dal linguaggio Java per avviare l'applicazione.
     * 
     * @param args Array di stringhe contenente gli eventuali argomenti passati da riga di comando.
     */
    public static void main(String[] args) {
        // Application.launch(): Metodo statico fornito da JavaFX.
        // Inizializza il runtime grafico di JavaFX, crea un'istanza della classe specificata (MainApp.class)
        // e invoca il suo metodo start(Stage primaryStage) sul Thread grafico dedicato (JavaFX Application Thread).
        Application.launch(MainApp.class, args);
    }
}
