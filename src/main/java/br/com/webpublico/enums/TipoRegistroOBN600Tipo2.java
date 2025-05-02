/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroOBN600Tipo2 {

    ZEROS("Zeros"),
    BRANCOS("Brancos"),
    DOIS("2"),
    CODIGO_DV_AGBANCARIA_UG_EMITENTE("Código/DV da agencia Bancaria da UG emitente."),
    CODIGO_UG_GESTAO_EMITENTE_OBS("Código da UG/Gestão emitente das Obs."),
    CODIGO_RELACAO_OB("Código da relação (RE) na qual consta a OB"),
    CODIGO_OB("Código da OB"),
    DATA_REFERENCIA_RELACAO("Data de referencia da relação"),
    CODIGO_OPERACAO("Código de operação (1)"),
    INDICADOR_PAGAMENTO_PESSOAL("Indicador de pagamento de pessoal 0 - Pagamento normal 1 – Pagamento de salário"),
    VALOR_LIQUIDO_OB("Valor liquido da OB (2)"),
    CODIGO_BANCO_FAVORECIDO("Código do Banco favorecido"),
    CODIGO_AGENCIA_FAVORECIDA("Código da Agencia bancaria favorecida"),
    DIGITO_VERIFICADOR_AGENCIA_BANCARIA("Digito verificador da Agência bancaria favor(DV)"),
    CODIGO_CONTA_BANCARIA_FAVORECIDO("Código da conta corrente bancaria do favorecido"),
    NOME_FAVORECIDO("Nome do favorecido"),
    ENDERECO_FAVORECIDO("Endereço do favorecido"),
    MUNICIPIO_FAVORECIDO("Município do favorecido"),
    CODIGO_GRU("Código GRU"),
    CEP_FAVORECIDO("CEP do favorecido"),
    UF_FAVORECIDO("UF do favorecido"),
    OBSERVACAO_OB("Observação da OB"),
    INDICADOR_TIPO_PAGAMENTO("Indicador do tipo de pagamento 0 - não prioritário 1 – prioritário"),
    TIPO_FAVORECIDO("Tipo favorecido: 1 - CGC 2 - CPF 5 – Código receita GRU"),
    CODIGO_FAVORECIDO("Código do favorecido (3)"),
    PREFIXO_AGENCIA_DV_DEBITO("Prefixo da agencia/DV para débito/ EXCLUSIVO PARA OB DE CONVENIOS/"),
    NUMERO_CONTA_DV_CONVENIO("Número conta\\DV do convênio / EXCLUSIVO PARA OB DE CONVENIOS/"),
    FINALIDADE_PAGAMENTO("Finalidade do pagamento - Fundef"),
    CODIGO_RETORNO_OPERACAO("Código de retorno da operação"),
    NUMERO_SEQUENCIAL_ARQUIVO("Número seqüencial no arquivo, consecutivo");

    private TipoRegistroOBN600Tipo2(String descricao) {
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
