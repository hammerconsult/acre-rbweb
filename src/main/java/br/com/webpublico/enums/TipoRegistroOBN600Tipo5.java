/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroOBN600Tipo5 {

    ZEROS("Zeros"),
    BRANCOS("Brancos"),
    CINCO("5"),
    CODIGO_AGENCIA_UG_EMITENTE("Código da agencia Bancaria da UG emitente."),
    CODIGO_UG_GESTAO_EMITENTE_OBS("Código da UG/Gestão emitente das Obs."),
    CODIGO_RELACAO_OB("Código da relação (RE) na qual consta a OB"),
    CODIGO_OB("Código da OB"),
    DATA_REFERENCIA_RELACAO("Data de referencia da relação"),
    CODIGO_OPERACAO("Código de operação (1)"),
    NUMERO_SEQUENCIAL_LISTA("Número seqüencial de lista"),
    VALOR_PAGAMENTO("Valor do pagamento"),
    DATA_PAGAMENTO("Data do pagamento"),
    TIPO_PAGAMENTO("Tipo de pagamento (5)"),
    CODIGO_RECEITA_TRIBUTO("Código da receita do tributo"),
    CODIGO_IDENTIFICACAO_TRIBUTO("Código de identificação do tributo"),
    MES_ANO_COMPETENCIA("Mês e ano de competência"),
    VALOR_PREVISTO_PAGAMENTO_INSS("Valor previsto do pagamento do INSS"),
    VALOR_OUTRAS_ENTIDADES("Valor de outras entidades"),
    ATUALIZACAO_MONETARIA("Atualização monetária"),
    PERIODO_APURACAO("Período de apuração"),
    NUMERO_REFERENCIA("Número de referência"),
    VALOR_PRINCIPAL("Valor principal"),
    VALOR_MULTA("Valor da multa"),
    VALOR_JUROS_ENCARGOS("Valor dos juros/encargos"),
    DATA_VENCIMENTO("Data do vencimento"),
    VALOR_RECEITA_BRUTA_ACUMULADA("Valor da receita bruta acumulada"),
    PERCENTUAL_SOBRE_RECEITA_BRUTA_ACUMULADA("Percentual sobre a receita bruta acumulada"),
    TIPO_IDENTIFICACAO_CONTRIBUINTE("Tipo de identificação do contribuinte"),
    CODIGO_IDENTIFICACAO_CONTRIBUINTE("Código de identificação do contribuinte"),
    NOME_CONTRIBUINTE("Nome do contribuinte"),
    OBSERVACAO_OB("Observação da OB"),
    NUMERO_AUTENTICACAO("Número da autenticação"),
    PREFIXO_AGENCIA_DV_DEBITO("Prefixo da agencia\\DV para débito/ EXCLUSIVO PARA OB DE CONVENIOS/"),
    NUMERO_CONTA_DV_CONVENIO("Número conta\\DV do convênio / EXCLUSIVO PARA OB DE CONVENIOS/"),
    CODIGO_RETORNO_OPERACAO("Código de retorno da operação"),
    NUMERO_SEQUENCIAL_ARQUIVO("Número seqüencial no arquivo, consecutivo");

    private TipoRegistroOBN600Tipo5(String descricao) {
        this.descricao = descricao;
    }
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
