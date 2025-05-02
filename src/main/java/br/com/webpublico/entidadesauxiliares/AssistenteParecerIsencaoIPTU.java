package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.IsencaoCadastroImobiliario;
import br.com.webpublico.entidades.ParecerProcIsencaoIPTU;
import br.com.webpublico.entidades.ProcessoIsencaoIPTU;

import java.io.Serializable;

public class AssistenteParecerIsencaoIPTU implements Serializable {
    private BarraProgressoItens barraProgresso;
    private ParecerProcIsencaoIPTU parecerIsencaoIPTU;
    private IsencaoCadastroImobiliario.Situacao situacaoIsencao;
    private ProcessoIsencaoIPTU.Situacao situacaoSolicitacao;
    private String mensagem;

    public BarraProgressoItens getBarraProgresso() {
        return barraProgresso;
    }

    public void setBarraProgresso(BarraProgressoItens barraProgresso) {
        this.barraProgresso = barraProgresso;
    }

    public ParecerProcIsencaoIPTU getParecerIsencaoIPTU() {
        return parecerIsencaoIPTU;
    }

    public void setParecerIsencaoIPTU(ParecerProcIsencaoIPTU parecerIsencaoIPTU) {
        this.parecerIsencaoIPTU = parecerIsencaoIPTU;
    }

    public IsencaoCadastroImobiliario.Situacao getSituacaoIsencao() {
        return situacaoIsencao;
    }

    public void setSituacaoIsencao(IsencaoCadastroImobiliario.Situacao situacaoIsencao) {
        this.situacaoIsencao = situacaoIsencao;
    }

    public ProcessoIsencaoIPTU.Situacao getSituacaoSolicitacao() {
        return situacaoSolicitacao;
    }

    public void setSituacaoSolicitacao(ProcessoIsencaoIPTU.Situacao situacaoSolicitacao) {
        this.situacaoSolicitacao = situacaoSolicitacao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
