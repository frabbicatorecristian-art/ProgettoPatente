package model;

import java.time.LocalDate;

public class Patente {
    private int id;
    private String codiceFiscaleUtente;
    private String numeroPatente;
    private LocalDate dataNascita;
    private String enteRilascio;
    private int saldoPunti;

    public Patente() {}

    public Patente(String codiceFiscaleUtente, String numeroPatente, LocalDate dataNascita, String enteRilascio, int saldoPunti) {
        this.codiceFiscaleUtente = codiceFiscaleUtente;
        this.numeroPatente = numeroPatente;
        this.dataNascita = dataNascita;
        this.enteRilascio = enteRilascio;
        this.saldoPunti = saldoPunti;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodiceFiscaleUtente() { return codiceFiscaleUtente; }
    public void setCodiceFiscaleUtente(String codiceFiscaleUtente) { this.codiceFiscaleUtente = codiceFiscaleUtente; }

    public String getNumeroPatente() { return numeroPatente; }
    public void setNumeroPatente(String numeroPatente) { this.numeroPatente = numeroPatente; }

    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }

    public String getEnteRilascio() { return enteRilascio; }
    public void setEnteRilascio(String enteRilascio) { this.enteRilascio = enteRilascio; }

    public int getSaldoPunti() { return saldoPunti; }
    public void setSaldoPunti(int saldoPunti) { this.saldoPunti = saldoPunti; }
}
