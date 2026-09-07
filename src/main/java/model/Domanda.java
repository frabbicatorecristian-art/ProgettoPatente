package model;

public class Domanda {
    private int id;
    private String testo;
    private boolean vero;
    private String spiegazione;
    private String immaginePath;

    public Domanda(int id, String testo, boolean vero, String spiegazione, String immaginePath) {
        this.id = id;
        this.testo = testo;
        this.vero = vero;
        this.spiegazione = spiegazione;
        this.immaginePath = immaginePath;
    }

    public int getId() { return id; }
    public String getTesto() { return testo; }
    public boolean isVero() { return vero; }
    public String getSpiegazione() { return spiegazione; }
    public String getImmaginePath() { return immaginePath; }
}
