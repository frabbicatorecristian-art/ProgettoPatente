package model;

import java.time.LocalDateTime;

public class Quiz {
    private int id;
    private String utenteCodiceFiscale;
    private LocalDateTime dataOra;
    private int errori;
    private String esito;

    public Quiz(int id, String utenteCodiceFiscale, LocalDateTime dataOra, int errori, String esito) {
        this.id = id;
        this.utenteCodiceFiscale = utenteCodiceFiscale;
        this.dataOra = dataOra;
        this.errori = errori;
        this.esito = esito;
    }

    public int getId() { return id; }
    public String getUtenteCodiceFiscale() { return utenteCodiceFiscale; }
    public LocalDateTime getDataOra() { return dataOra; }
    public int getErrori() { return errori; }
    public String getEsito() { return esito; }
}
