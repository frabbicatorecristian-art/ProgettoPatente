package org.example;

/**
 * =================================================================================
 * CLASSE LAUNCHER (Bootstrap alternativo)
 * =================================================================================
 * Questa classe viene utilizzata come classe di lancio ausiliaria per la build Maven
 * e per l'esecuzione tramite pacchetti JAR eseguibili.
 * 
 * POCHÉ È UTILE?
 * Alcuni plugin di build (es. maven-shade-plugin o exec-maven-plugin) richiedono una 
 * classe principale neutra per evitare problemi di caricamento del modulo runtime JavaFX.
 */
public class Launcher {

    /**
     * Metodo main che inoltra la chiamata alla classe Main principale.
     * 
     * @param args Parametri da riga di comando passati all'avvio.
     */
    public static void main(String[] args) {
        // Delega l'avvio al metodo statico main della classe org.example.Main
        Main.main(args);
    }
}
