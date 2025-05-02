package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;

public class FiltroUnificacaoCadastroEconomicoLote implements Serializable {

    private String inscricaoCadastral;
    private Date dataCadastro;
    private Boolean consultarMovimento;

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Boolean getConsultarMovimento() {
        return consultarMovimento;
    }

    public void setConsultarMovimento(Boolean consultarMovimento) {
        this.consultarMovimento = consultarMovimento;
    }
}
