package model;

// =================================================================================
// IMPORT DELLE CLASSI JAVAFX
// =================================================================================
import javafx.scene.Scene;                   // Rappresenta la scena grafica principale JavaFX

/**
 * =================================================================================
 * TEMA MANAGER - Singleton Pattern
 * =================================================================================
 * Gestisce la personalizzazione globale del tema dell'applicazione (Chiaro / Scuro).
 * Implementa il pattern Singleton per garantire un'unica istanza in tutta l'applicazione.
 * 
 * UTILIZZO:
 * Ogni controller deve richiamare TemaManager.getInstance().applica(scena) 
 * nel proprio metodo initialize() per applicare il tema attualmente selezionato.
 * 
 * FUNZIONALITÀ:
 * 1. Memorizza lo stato del tema globale (true = Scuro, false = Chiaro).
 * 2. Fornisce costanti di colori CSS per entrambi i temi (sfondo, navbar, ecc.).
 * 3. Applica il tema a una Scena JavaFX modificandone lo stile CSS.
 */
public class TemaManager {

    // =====================================================================
    // VARIABILI SINGLETON
    // =====================================================================
    private static TemaManager instance;        // Istanza statica unica del TemaManager (Singleton)
    
    // =====================================================================
    // VARIABILI DI ISTANZA - Stato del tema
    // =====================================================================
    private boolean temaScuro = false;          // Stato del tema: false = Chiaro, true = Scuro (default: Chiaro)

    // =====================================================================
    // COSTANTI - Palette di colori per il tema Chiaro
    // =====================================================================
    public static final String BG_CHIARO    = "#f1f5f9";    // Colore di sfondo principale nel tema Chiaro (grigio molto chiaro)
    public static final String BG_NAVBAR_CHIARO = "#0284c7"; // Colore della navbar nel tema Chiaro (blu moderato)
    
    // =====================================================================
    // COSTANTI - Palette di colori per il tema Scuro
    // =====================================================================
    public static final String BG_SCURO     = "#0f172a";    // Colore di sfondo principale nel tema Scuro (blu-nero molto scuro)
    public static final String BG_NAVBAR_SCURO = "#1e3a5f"; // Colore della navbar nel tema Scuro (blu scuro profondo)

    // =====================================================================
    // COSTRUTTORE PRIVATO (Pattern Singleton)
    // =====================================================================
    /**
     * Costruttore privato per impedire l'istanziazione diretta della classe.
     * La creazione dell'istanza è controllata dal metodo statico getInstance().
     */
    private TemaManager() {}

    // =====================================================================
    // METODI SINGLETON - Accesso all'istanza unica
    // =====================================================================

    /**
     * METODO GETTER: Restituisce l'istanza unica del TemaManager (Singleton Pattern).
     * Se l'istanza non esiste ancora, ne crea una nuova.
     * Garantisce che in tutta l'applicazione esista una sola istanza di TemaManager.
     * 
     * @return L'istanza unica del TemaManager.
     */
    public static TemaManager getInstance() {
        if (instance == null) {
            instance = new TemaManager();  // Lazy initialization: crea l'istanza al primo uso
        }
        return instance;
    }

    // =====================================================================
    // METODI GETTER/SETTER - Accesso allo stato del tema
    // =====================================================================

    /**
     * METODO GETTER: Verifica se il tema corrente è in modalità Scura.
     * 
     * @return true se il tema è Scuro, false se è Chiaro.
     */
    public boolean isTemaScuro() { 
        return temaScuro; 
    }

    /**
     * METODO SETTER: Imposta lo stato del tema globale.
     * Modifica il tema che verrà applicato a tutte le schermate successive.
     * 
     * @param scuro true per attivare il tema Scuro, false per attivare il tema Chiaro.
     */
    public void setTemaScuro(boolean scuro) { 
        this.temaScuro = scuro; 
    }

    // =====================================================================
    // METODI DI APPLICAZIONE DEL TEMA
    // =====================================================================

    /**
     * METODO: APPLICA IL TEMA ALLA SCENA
     * Modifica lo stile CSS del nodo radice della Scena per applicare il tema corrente.
     * Seleziona il colore di sfondo appropriato in base allo stato di temaScuro.
     * 
     * FUNZIONAMENTO:
     * 1. Verifica che la Scena non sia null (per evitare eccezioni).
     * 2. Estrae il nodo radice (Region) dalla Scena.
     * 3. Applica lo stile CSS corrispondente al tema corrente.
     * 
     * @param scene La Scena JavaFX su cui applicare il tema. Se null, nessuna azione.
     */
    public void applica(Scene scene) {
        // Protezione: se la scena è null, non fare nulla
        if (scene == null) return;
        
        // Casting del nodo radice a Region (classe base per i nodi che supportano lo stile CSS)
        javafx.scene.layout.Region root = (javafx.scene.layout.Region) scene.getRoot();
        
        // Applica il colore di sfondo appropriato in base al tema corrente
        if (temaScuro) {
            // TEMA SCURO: Usa il colore di sfondo scuro (blu-nero)
            root.setStyle("-fx-background-color: " + BG_SCURO + ";");
        } else {
            // TEMA CHIARO: Usa il colore di sfondo chiaro (grigio molto chiaro)
            root.setStyle("-fx-background-color: " + BG_CHIARO + ";");
        }
    }
}
