
package br.com.webpublico.ws.model;

import br.com.webpublico.enums.TipoCadastroDoctoOficial;

import java.io.Serializable;

public class WsDadosPessoaisSolicitacaoCertidaoNegativa implements Serializable {

    protected TipoCadastroDoctoOficial tipoCadastroTributario;
    protected String cpfCnpj;
    protected String nomeRazaoSocial;
    protected String inscricaoCadastral;


    public TipoCadastroDoctoOficial getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroDoctoOficial tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }
}
