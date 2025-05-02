package br.com.webpublico.entidades;

import java.io.Serializable;
import java.util.Date;

public class PeriodoExcludenteDTO implements Serializable {

    private Date inicio;
    private Date fim;
    private Integer dias;
    private String observacao;

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
