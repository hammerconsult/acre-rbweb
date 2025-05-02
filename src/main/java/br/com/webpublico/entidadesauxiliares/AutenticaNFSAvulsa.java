package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.ws.model.WsAutenticaNFSAvulsa;

import java.io.Serializable;
import java.util.Date;

/**
* Created with IntelliJ IDEA.
* User: William
* Date: 27/01/15
* Time: 17:47
* To change this template use File | Settings | File Templates.
*/
public class AutenticaNFSAvulsa implements Serializable {
    private String numeroNota;
    private String cpfCnpj;
    private Date dataLancamento;
    private String numeroAutenticidade;

    public AutenticaNFSAvulsa(WsAutenticaNFSAvulsa autenticaNFSAvulsa) {
        this.setNumeroNota(autenticaNFSAvulsa.getNumeroNota());
        this.setCpfCnpj(autenticaNFSAvulsa.getCpfCnpj());
        this.setDataLancamento(autenticaNFSAvulsa.getDataLancamento());
        this.setNumeroAutenticidade(autenticaNFSAvulsa.getNumeroAutenticidade());
    }

    public AutenticaNFSAvulsa() {
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumeroAutenticidade() {
        return numeroAutenticidade;
    }

    public void setNumeroAutenticidade(String numeroAutenticidade) {
        this.numeroAutenticidade = numeroAutenticidade;
    }
}
