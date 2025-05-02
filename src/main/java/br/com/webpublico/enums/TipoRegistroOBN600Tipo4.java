/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author wiplash
 */
public enum TipoRegistroOBN600Tipo4 {

    ZEROS("Zeros"),
    BRANCOS("Brancos"),
    QUATRO("4"),
    CODIGO_AGENCIA_BANCARIA_UG_EMITENTE("Código da agencia Bancaria da UG emitente."),
    CODIGO_UG_GESTAO_EMITENTE_OBS("Código da UG/Gestão emitente das Obs."),
    CODIGO_RELACAO_OB("Código da relação (RE) na qual consta a OB"),
    CODIGO_OB("Código da OB"),
    DATA_REFERENCIA_RELACAO("Data de referencia da relação"),
    CODIGO_OPERACAO("Código de operação (1)"),
    NUMERO_SEQUENCIAL_LISTA("Número seqüencial de lista"),
    VALOR_LIQUIDO_OB("Valor liquido da OB"),
    TIPO_FATURA("Tipo de fatura (4)"),
    CODIGO_BARRA("Código de barra"),
    DATA_VENCIMENTO("Data do vencimento"),
    VALOR_NOMINAL("Valor nominal"),
    VALOR_DESCONTO_ABATIMENTO("Valor desconto abatimento"),
    VALOR_MORA_JUROS("Valor mora juros"),
    OBSERVACAO_OB("Observação da OB"),
    NUMERO_AUTENTICACAO("Número da autenticação"),
    PREFIXO_AGENCIA_DV_DEBITO("Prefixo da agencia\\DV para débito/ EXCLUSIVO PARA OB DE CONVENIOS/"),
    NUMERO_CONTA_DV_CONVENIO("Número conta\\DV do convênio / EXCLUSIVO PARA OB DE CONVENIOS/"),
    CODIGO_RETORNO_OPERACAO("Código de retorno da operação"),
    NUMERO_SEQUENCIAL_ARQUIVO("Número seqüencial no arquivo, consecutivo");

    private TipoRegistroOBN600Tipo4(String descricao) {
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
