/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

/**
 * @author Renato
 */
public enum OperacaoMovimentoDividaPublica implements EnumComDescricao {

    INSCRICAO_PRINCIPAL("Inscrição Principal"),
    ATUALIZACAO_JUROS("Atualização de Juros"),
    ATUALIZACAO_ENCARGOS("Atualização de Encargos"),
    APROPRIACAO_JUROS("Apropriação de Juros"),
    APROPRIACAO_ENCARGOS("Apropriação de Encargos"),
    TRANSFERENCIA_LONGO_PARA_CURTO_PRAZO("Transferência de Longo para Curto prazo"),
    TRANSFERENCIA_CURTO_PARA_LONGO_PRAZO("Transferência de Curto para Longo prazo"),
    CANCELAMENTO_APROPRIACAO_JUROS("Cancelamento de Apropriação de Juros"),
    CANCELAMENTO_APROPRIACAO_ENCARGOS("Cancelamento de Apropriação de Encargos"),
    CANCELAMENTO_PRINCIPAL("Cancelamento do Principal"),
    CANCELAMENTO_JUROS("Cancelamento de Juros"),
    CANCELAMENTO_ENCARGOS("Cancelamento de Encargos"),
    EMPENHO("Empenho"),
    PAGAMENTO_AMORTIZACAO("Pagamento/Amortização"),
    RECEITA_OPERACAO_CREDITO("Receita de operação de crédito");
    private String descricao;

    OperacaoMovimentoDividaPublica(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<OperacaoMovimentoDividaPublica> getTiposOperacaoSomar() {
        List<OperacaoMovimentoDividaPublica> retorno = Lists.newArrayList();
        retorno.add(OperacaoMovimentoDividaPublica.INSCRICAO_PRINCIPAL);
        retorno.add(OperacaoMovimentoDividaPublica.ATUALIZACAO_ENCARGOS);
        retorno.add(OperacaoMovimentoDividaPublica.ATUALIZACAO_JUROS);
        retorno.add(OperacaoMovimentoDividaPublica.RECEITA_OPERACAO_CREDITO);;
        return retorno;
    }
}
