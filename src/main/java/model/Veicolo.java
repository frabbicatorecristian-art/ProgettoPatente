package model;

import java.time.LocalDate;

public class Veicolo {
    private int id;
    private String codiceFiscaleUtente;
    private String tipologia;
    private String nome;
    private String targa;
    private LocalDate dataAssicurazione;
    private String validitaAssicurazione;
    private LocalDate dataImmatricolazione;
    private boolean primaRevisioneFatta;
    private LocalDate dataUltimaRevisione;

    public Veicolo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodiceFiscaleUtente() { return codiceFiscaleUtente; }
    public void setCodiceFiscaleUtente(String codiceFiscaleUtente) { this.codiceFiscaleUtente = codiceFiscaleUtente; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTarga() { return targa; }
    public void setTarga(String targa) { this.targa = targa; }

    public LocalDate getDataAssicurazione() { return dataAssicurazione; }
    public void setDataAssicurazione(LocalDate dataAssicurazione) { this.dataAssicurazione = dataAssicurazione; }

    public String getValiditaAssicurazione() { return validitaAssicurazione; }
    public void setValiditaAssicurazione(String validitaAssicurazione) { this.validitaAssicurazione = validitaAssicurazione; }

    public LocalDate getDataImmatricolazione() { return dataImmatricolazione; }
    public void setDataImmatricolazione(LocalDate dataImmatricolazione) { this.dataImmatricolazione = dataImmatricolazione; }

    public boolean isPrimaRevisioneFatta() { return primaRevisioneFatta; }
    public void setPrimaRevisioneFatta(boolean primaRevisioneFatta) { this.primaRevisioneFatta = primaRevisioneFatta; }

    public LocalDate getDataUltimaRevisione() { return dataUltimaRevisione; }
    public void setDataUltimaRevisione(LocalDate dataUltimaRevisione) { this.dataUltimaRevisione = dataUltimaRevisione; }
}
