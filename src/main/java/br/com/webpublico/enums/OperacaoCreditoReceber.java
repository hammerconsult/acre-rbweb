/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author major
 */
public enum OperacaoCreditoReceber {
    RECONHECIMENTO_CREDITO_A_RECEBER("Reconhecimento de Créditos a Receber"),
    DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER("Dedução de Reconhecimento de Créditos a Receber"),
    AJUSTE_PERDAS_CREDITOS_A_RECEBER("Ajuste de Perdas de Créditos a Receber"),
    AJUSTE_PERDAS_CREDITOS_A_RECEBER_LONGO_PRAZO("Ajuste de Perdas de Créditos a Receber a Longo Prazo"),
    AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO("Ajuste em Crédito a Receber Diminutivo"),
    AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO("Ajuste em Crédito a Receber Aumentativo"),
    AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO_EMPRESA_PUBLICA("Ajuste em Crédito a Receber Diminutivo - Empresa Pública"),
    AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO_EMPRESA_PUBLICA("Ajuste em Crédito a Receber Aumentativo - Empresa Pública"),
    ATUALIZACAO_DE_CREDITOS_A_RECEBER("Atualização de Créditos a Receber"),
    REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER("Reversão de Ajuste de Perdas de Créditos a Receber"),
    REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PRAZO("Reversão de Ajuste de Perdas de Créditos a Receber a Longo Prazo"),
    BAIXA_RECONHECIMENTO_CREDITO_A_RECEBER("Baixa de Reconhecimento de Créditos a Receber"),
    BAIXA_DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER("Baixa de Dedução de Reconhecimento de Rréditos a Receber"),
    RECEBIMENTO("Recebimento"),
    TRANSFERENCIA_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO("Transferência de Créditos a Receber de Curto para Longo Prazo"),
    TRANSFERENCIA_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO("Transferência de Créditos a Receber de Longo para Curto Prazo"),
    TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO("Transferência de Ajuste de Perdas de Créditos a Receber de Curto para Longo Prazo"),
    TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO("Transferência de Ajuste de Perdas de Créditos a Receber de Longo para Curto Prazo");
    private String descricao;

    private OperacaoCreditoReceber(String descricao) {
        this.descricao = descricao;
    }

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
