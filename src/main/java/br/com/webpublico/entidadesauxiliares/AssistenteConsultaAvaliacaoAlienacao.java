package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LoteAvaliacaoAlienacao;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteConsultaAvaliacaoAlienacao {

    private List<VOItemSolicitacaoAlienacao> bensAprovados;
    private List<LoteAvaliacaoAlienacao> lotesAvaliacao;

    public AssistenteConsultaAvaliacaoAlienacao() {
        bensAprovados = Lists.newArrayList();
        lotesAvaliacao = Lists.newArrayList();
    }

    public List<VOItemSolicitacaoAlienacao> getBensAprovados() {
        return bensAprovados;
    }

    public void setBensAprovados(List<VOItemSolicitacaoAlienacao> bensAprovados) {
        this.bensAprovados = bensAprovados;
    }

    public List<LoteAvaliacaoAlienacao> getLotesAvaliacao() {
        return lotesAvaliacao;
    }

    public void setLotesAvaliacao(List<LoteAvaliacaoAlienacao> lotesAvaliacao) {
        this.lotesAvaliacao = lotesAvaliacao;
    }
}
