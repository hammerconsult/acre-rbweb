package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SolicitacaoDoctoOficial;

import java.io.Serializable;

public class ValidacaoSolicitacaoDoctoOficial implements Serializable {
    private SolicitacaoDoctoOficial solicitacaoDoctoOficial;
    private String mensagemValidacao;
    private byte[] certidao;

    public ValidacaoSolicitacaoDoctoOficial() {
    }

    public SolicitacaoDoctoOficial getSolicitacaoDoctoOficial() {
        return solicitacaoDoctoOficial;
    }

    public void setSolicitacaoDoctoOficial(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        this.solicitacaoDoctoOficial = solicitacaoDoctoOficial;
    }

    public String getMensagemValidacao() {
        return mensagemValidacao;
    }

    public void setMensagemValidacao(String mensagemValidacao) {
        this.mensagemValidacao = mensagemValidacao;
    }

    public byte[] getCertidao() {
        return certidao;
    }

    public void setCertidao(byte[] certidao) {
        this.certidao = certidao;
    }
}
