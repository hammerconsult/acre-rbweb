package br.com.webpublico.nfse.domain.dtos;

import java.util.Date;

public class ReferenciaPeriodoNfseDTO {
    public Date referencia;
    public Integer mes;
    public Integer ano;

    public Date getReferencia() {
        return referencia;
    }

    public void setReferencia(Date referencia) {
        this.referencia = referencia;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}
