package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by mga on 22/06/2017.
 */
public enum TipoReconhecimentoObrigacaoPagar {

    RECONHECIMENTO_OBRIGACAO_APOS_EMPENHO_DESPESA("Reconhecimento de obrigação após o empenho de despesa"),
    RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA("Reconhecimento de obrigação antes do empenho de despesa"),
    SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA("Sem reconhecimento de obrigação antes do empenho de despesa"),
    COM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA("Com Reconhecimento de obrigação antes do empenho de despesa"),
    SEM_RECONHECIMENTO_OBRIGACAO("Sem reconhecimento de obrigação"),
    COM_RECONHECIMENTO_OBRIGACAO("Com reconhecimento de obrigação");
    private String descricao;

    TipoReconhecimentoObrigacaoPagar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static List<TipoReconhecimentoObrigacaoPagar> retornaTipoReconhecimentosLiquidacao() {
        List<TipoReconhecimentoObrigacaoPagar> toReturn = Lists.newArrayList();
        toReturn.add(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO);
        toReturn.add(TipoReconhecimentoObrigacaoPagar.COM_RECONHECIMENTO_OBRIGACAO);
        return toReturn;
    }

    public static List<TipoReconhecimentoObrigacaoPagar> retornaTipoReconhecimentosEmpenho() {
        List<TipoReconhecimentoObrigacaoPagar> toReturn = Lists.newArrayList();
        toReturn.add(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        toReturn.add(TipoReconhecimentoObrigacaoPagar.COM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        return toReturn;
    }

    public static List<TipoReconhecimentoObrigacaoPagar> retornaTipoReconhecimentosObrigacaoPagar() {
        List<TipoReconhecimentoObrigacaoPagar> toReturn = Lists.newArrayList();
        toReturn.add(TipoReconhecimentoObrigacaoPagar.RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        toReturn.add(TipoReconhecimentoObrigacaoPagar.RECONHECIMENTO_OBRIGACAO_APOS_EMPENHO_DESPESA);
        return toReturn;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
