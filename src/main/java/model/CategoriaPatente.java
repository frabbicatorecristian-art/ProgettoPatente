package model;

import java.time.LocalDate;

public class CategoriaPatente {
    private int id;
    private int patenteId;
    private String categoria;
    private LocalDate dataRilascio;

    public CategoriaPatente() {}

    public CategoriaPatente(int patenteId, String categoria, LocalDate dataRilascio) {
        this.patenteId = patenteId;
        this.categoria = categoria;
        this.dataRilascio = dataRilascio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatenteId() { return patenteId; }
    public void setPatenteId(int patenteId) { this.patenteId = patenteId; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDate getDataRilascio() { return dataRilascio; }
    public void setDataRilascio(LocalDate dataRilascio) { this.dataRilascio = dataRilascio; }
}
