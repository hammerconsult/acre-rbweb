/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoSituacaoLicitacao {
    ADJUDICADA("Adjudicada"),
    ANDAMENTO("Andamento"),
    ANULADA("Anulada"),
    CANCELADA("Cancelada"),
    DESERTA("Deserta"),
    EM_RECURSO("Em Recurso"),
    FRACASSADA("Fracassada"),
    HOMOLOGADA("Homologada"),
    PRORROGADA("Prorrogada"),
    REVOGADA("Revogada"),
    SUSPENSA("Suspensa");
    private String descricao;

    TipoSituacaoLicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isAndamento() {
        return TipoSituacaoLicitacao.ANDAMENTO.equals(this);
    }

    public static List<TipoSituacaoLicitacao> getSituacoesAtualizacaoPncp(){
        List<TipoSituacaoLicitacao> situacoes = Lists.newArrayList();
        situacoes.add(TipoSituacaoLicitacao.ANDAMENTO);
        situacoes.add(TipoSituacaoLicitacao.HOMOLOGADA);
        situacoes.add(TipoSituacaoLicitacao.ANULADA);
        situacoes.add(TipoSituacaoLicitacao.REVOGADA);
        situacoes.add(TipoSituacaoLicitacao.CANCELADA);
        situacoes.add(TipoSituacaoLicitacao.DESERTA);
        situacoes.add(TipoSituacaoLicitacao.FRACASSADA);
        return situacoes;
    }
}
