package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by mga on 22/06/2017.
 */
public enum OperacaoObrigacaoAPagar {

    RECONHECIMENTO_OBRIGACAO_DESPESA("Reconhecimento de obrigação de despesa"),
    RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS("Reconhecimento de obrigação de despesa de pessoal e encargos RGPS"),
    RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS("Reconhecimento de obrigação de despesa de pessoal e encargos RPPS"),
    RECONHECIMENTO_OBRIGACAO_DESPESA_EM_LIQUIDACAO("Reconhecimento de obrigação de despesa em liquidação"),
    RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS_EM_LIQUIDACAO("Reconhecimento de obrigação de despesa de pessoal e encargos RGPS em liquidação"),
    RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS_EM_LIQUIDACAO("Reconhecimento de obrigação de despesa de pessoal e encargos RPPS em liquidação"),
    RECONHECIMENTO_OBRIGACAO_DE_RESTOS_A_PAGAR_EM_LIQUIDACAO("Reconhecimento de obrigação de restos a pagar em liquidação"),
    RECONHECIMENTO_OBRIGACAO_DE_RESTOS_A_PAGAR_PESSOAL_ENCARGOS_RGPS_EM_LIQUIDACAO("Reconhecimento de obrigação de restos a pagar de pessoal e encargos RGPS em liquidação"),
    RECONHECIMENTO_OBRIGACAO_DE_RESTOS_A_PAGAR_PESSOAL_ENCARGOS_RPPS_EM_LIQUIDACAO("Reconhecimento de obrigação de restos a pagar de pessoal e encargos RPPS em liquidação");
    private String descricao;

    OperacaoObrigacaoAPagar(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static List<OperacaoObrigacaoAPagar> retornaOperacaoComEmpenho(){
        List<OperacaoObrigacaoAPagar> toReturn = Lists.newArrayList();
        toReturn.add(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA);
        toReturn.add(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS);
        toReturn.add(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS);
        return toReturn;
    }

    public static List<OperacaoObrigacaoAPagar> retornaOperacaoSemEmpenho(){
        List<OperacaoObrigacaoAPagar> toReturn = Lists.newArrayList();
        toReturn.add(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_EM_LIQUIDACAO);
        toReturn.add(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RGPS_EM_LIQUIDACAO);
        toReturn.add(OperacaoObrigacaoAPagar.RECONHECIMENTO_OBRIGACAO_DESPESA_PESSOAL_ENCARGOS_RPPS_EM_LIQUIDACAO);
        return toReturn;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
