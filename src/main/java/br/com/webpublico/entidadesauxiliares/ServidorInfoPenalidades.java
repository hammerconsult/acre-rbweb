package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoPenalidades {

    private Integer item;
    private Date inicioVigencia;
    private Date fimVigencia;
    private String dias;
    private String tipoPenalidade;
    private String observacao;

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getTipoPenalidade() {
        return tipoPenalidade;
    }

    public void setTipoPenalidade(String tipoPenalidade) {
        this.tipoPenalidade = tipoPenalidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
