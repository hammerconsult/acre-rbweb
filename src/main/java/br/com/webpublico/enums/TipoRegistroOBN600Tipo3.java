/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroOBN600Tipo3 {

    ZEROS("Zeros"),
    BRANCOS("Brancos"),
    TRES("3"),
    CODIGO_AGENCIA_BANCARIA_EMITENTE("Código da agencia Bancaria da UG emitente."),
    CODIGO_UG_GESTAO_EMITENTE("Código da UG/Gestão emitente das Obs."),
    CODIGO_RELACAO_OB("Código da relação (RE) na qual consta a OB"),
    CODIGO_OB("Código da OB"),
    DATA_REFERENCIA_RELACAO("Data de referencia da relação"),
    CODIGO_OPERACAO("Código de operação (1)"),
    TIPO_PAGAMENTO("Tipo de pagamento 1 - Pagamento crédito em conta BB 2 – Pagamento de salário 3 – Pagamento no caixa 4 – Pagamento em outros bancos"),
    NUMERO_SEQUENCIAL_LISTA("Número seqüencial de lista "),
    VALOR_LIQUIDO_OB("Valor liquido da OB (2)"),
    CODIGO_BANCO_FAVORECIDO("Código do Banco favorecido"),
    CODIGO_AGENCIA_BANCARIA_FAVORECIDA("Código da Agencia bancaria favorecida"),
    DIGITO_VERIFICADOR_AGBANCARIA_FAVOR("Digito verificador da Agência bancaria favor(DV)"),
    CODIGO_CONTA_BANCARIA_FAVORECIDO("Código da conta corrente bancaria do favorecido"),
    NOME_FAVORECIDO("Nome do favorecido"),
    ENDERECO_FAVORECIDO("Endereço do favorecido"),
    MUNICIPIO_FAVORECIDO("Município do favorecido"),
    CODIGO_GRU("Código GRU"),
    CEP_FAVORECIDO("CEP do favorecido"),
    UF_FAVORECIDO("UF do favorecido"),
    OBSERVACAO_OB("Observação da OB"),
    TIPO_FAVORECIDO("Tipo favorecido: 1 - CGC 2 - CPF 5 – Código receita GRU"),
    CODIGO_FAVORECIDO("Código do favorecido (3)"),
    PREFIXO_AGENCIA_DV_DEBITO("Prefixo da agencia\\DV para débito/ EXCLUSIVO PARA OB DE CONVENIOS/"),
    NUMERO_CONTA_DV_CONVENIO("Número conta\\DV do convênio / EXCLUSIVO PARA OB DE CONVENIOS/"),
    CODIGO_RETORNO_OPERACAO("Código de retorno da operação"),
    NUMERO_SEQUENCIAL_ARQUIVO("Número seqüencial no arquivo, consecutivo");

    private TipoRegistroOBN600Tipo3(String descricao) {
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
