package br.com.webpublico.entidadesauxiliares.datajud;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RegistroDatajud implements Serializable {
    private Classe classe;
    private String numeroProcesso;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date dataAjuizamento;
    private List<Movimento> movimentos;

    public RegistroDatajud() {
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Date getDataAjuizamento() {
        return dataAjuizamento;
    }

    public void setDataAjuizamento(Date dataAjuizamento) {
        this.dataAjuizamento = dataAjuizamento;
    }

    public List<Movimento> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<Movimento> movimentos) {
        this.movimentos = movimentos;
    }
}
