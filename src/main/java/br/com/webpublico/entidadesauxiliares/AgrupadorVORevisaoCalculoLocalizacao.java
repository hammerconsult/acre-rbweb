package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class AgrupadorVORevisaoCalculoLocalizacao implements Serializable {
    private List<VORevisaoCalculoLocalizacao> alteracoes;

    public AgrupadorVORevisaoCalculoLocalizacao() {
        this.alteracoes = Lists.newArrayList();
    }

    public AgrupadorVORevisaoCalculoLocalizacao(List<VORevisaoCalculoLocalizacao> alteracoes) {
        this.alteracoes = alteracoes;
    }

    public List<VORevisaoCalculoLocalizacao> getAlteracoes() {
        return alteracoes;
    }

    public void setAlteracoes(List<VORevisaoCalculoLocalizacao> alteracoes) {
        this.alteracoes = alteracoes;
    }

    public List<VORevisaoCalculoLocalizacao> getAlteracoesCnae() {
        return buscarAlteracoesCnaeEndereco(true);
    }

    public List<VORevisaoCalculoLocalizacao> getAlteracoesEndereco() {
        return buscarAlteracoesCnaeEndereco(false);
    }

    private List<VORevisaoCalculoLocalizacao> buscarAlteracoesCnaeEndereco(boolean isCnae) {
        List<VORevisaoCalculoLocalizacao> alteracoes = Lists.newArrayList();
        if (this.alteracoes != null && !this.alteracoes.isEmpty()) {
            for (VORevisaoCalculoLocalizacao alteracao : this.alteracoes) {
                if (isCnae && alteracao.isCnae()) {
                    alteracoes.add(alteracao);
                } else if (!isCnae && !alteracao.isCnae()) {
                    alteracoes.add(alteracao);
                }
            }
        }
        return alteracoes;
    }
}
