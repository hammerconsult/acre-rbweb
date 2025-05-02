package br.com.webpublico.entidadesauxiliares.rh.portal;

import java.io.Serializable;
import java.util.Date;

public class FeriadoDTO implements Serializable {

    private String descricao;
    private Date dataFeriado;

    public FeriadoDTO() {
    }

    public FeriadoDTO(String descricao, Date dataFeriado) {
        this.descricao = descricao;
        this.dataFeriado = dataFeriado;
    }

    public Date getDataFeriado() {
        return dataFeriado;
    }

    public void setDataFeriado(Date dataFeriado) {
        this.dataFeriado = dataFeriado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
