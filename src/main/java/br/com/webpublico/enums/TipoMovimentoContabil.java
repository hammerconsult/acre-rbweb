
package br.com.webpublico.enums;

/**
 * Created by mateus on 12/12/17.
 */
public enum TipoMovimentoContabil {

    RECEITA_EXTRAORCAMENTARIA("Receita Extraorçamentária"),
    RECEITA_A_REALIZAR_NEGATIVO_CREDOR("Receita a Realizar - Negativo Credor"),
    RECEITA_A_REALIZAR_POSITIVO_DEVEDOR("Receita a Realizar - Positivo Devedor"),
    RECEITA_REESTIMATIVA("Receita Reestimativa"),
    RECEITA_DEDUCAO_PREVISAO_INICIAL_RECEITA("Receita Dedução - Previsão Inicial da Receita"),
    RECEITA_DEDUCAO_RECEITA_REALIZADA("Receita Dedução - Receita Realizada"),
    RECEITA_RECEITA_REALIZADA_NEGATIVO("Receita - Receita Realizada Negativo"),
    RECEITA_RECEITA_REALIZADA_POSITIVO("Receita - Receita Realizada Positivo"),
    DESPESA_CREDITO_DISPONIVEL("Despesa - Crédito Disponível"),
    DESPESA_CREDITO_ADICIONAL_SUPLEMENTAR("Despesa - Crédito Adicional Suplementar"),
    DESPESA_CREDITO_ADICIONAL_ESPECIAL("Despesa - Crédito Adicional Especial"),
    DESPESA_CREDITO_ADICIONAL_EXTRAORDINARIO("Despesa - Crédito Adicional Extraordinário"),
    DESPESA_ANULACAO_DOTACAO("Despesa - Anulação de Dotação"),
    DESPESA_CREDITO_ADICIONAL_POR_SUPERAVIT_FINANCEIRO("Despesa - Crédito Adicional por Superavit Financeiro"),
    DESPESA_CREDITO_ADICIONAL_POR_EXCESSO_ARRECADACAO("Despesa - Crédito Adicional por Excesso de Arrecadação"),
    DESPESA_CREDITO_ADICIONAL_POR_ANULACAO_DOTACAO("Despesa - Crédito Adicional por Anulação de Dotação"),
    DESPESA_CREDITO_ADICIONAL_POR_OPERACAO_CREDITO("Despesa - Crédito Adicional por Operação de Crédito"),
    DESPESA_CREDITO_ADICIONAL_POR_RESERVA_CONTIGENCIA("Despesa - Crédito Adicional por Reserva de Contingência"),
    DESPESA_CREDITO_ADICIONAL_POR_ANULACAO_CREDITO("Despesa - Crédito Adicional por Anulação de Crédito"),
    EMPENHOS_LIQUIDAR_INSCRITOS_NAO_PROCESSADOS("Empenhos a Liquidar Inscritos Não Processados"),
    EMPENHOS_LIQUIDADOS_INSCRITOS_PROCESSADOS("Empenhos Liquidados Inscritos Processados"),
    EMPENHOS_EM_LIQUIDACAO_INSCRITOS_NAO_PROCESSADOS("Empenhos em Liquidação Inscritos Não Processados"),
    EMPENHOS_CREDITO_EMPENHO_PAGO("Empenhos - Crédito - Empenho Pago"),
    RESTO_PAGAR_PAGO_PROCESSADO_EXERCICIO_ANTERIOR("Restos a Pagar Pagos Processados no Exercício Anterior"),
    RESTO_PAGAR_PAGO_NAO_PROCESSADO_EXERCICIO_ANTERIOR("Restos a Pagar Pagos Não Processados no Exercício Anterior"),
    RESTO_PAGAR_CANCELADO_PROCESSADO_EXERCICIO_ANTERIOR("Restos a Pagar Cancelados Processados no Exercício Anterior"),
    RESTO_PAGAR_CANCELADO_NAO_PROCESSADO_EXERCICIO_ANTERIOR("Restos a Pagar Cancelados Não Processados no Exercício Anterior"),
    DESTINACAO_DE_RECURSO_CREDOR_NEGATIVO("Destinação de Recurso - Credor Negativo"),
    DESTINACAO_DE_RECURSO_DEVEDOR_POSITIVO("Destinação de Recurso - Devedor Positivo"),
    RESULTADO_DIMINUTIVO_DO_EXERCICIO_POSITIVO("Resultado Diminutivo do Exercício Positivo"),
    RESULTADO_DIMINUTIVO_DO_EXERCICIO_NEGATIVO("Resultado Diminutivo do Exercício Negativo"),
    RESULTADO_AUMENTATIVO_DO_EXERCICIO_POSITIVO("Resultado Aumentativo do Exercício Positivo"),
    RESULTADO_AUMENTATIVO_DO_EXERCICIO_NEGATIVO("Resultado Aumentativo do Exercício Negativo"),
    ABERTURA_RESTO_PAGAR_PROCESSADOS("Abertura - Restos a Pagar Processados"),
    ABERTURA_RESTO_PAGAR_NAO_PROCESSADOS("Abertura - Restos a Pagar Não Processados"),
    ABERTURA_RESTO_PAGAR_NAO_PROCESSADOS_EM_LIQUIDACAO("Abertura - Restos a Pagar Não Processados em Liquidação"),
    ABERTURA_TRANSFERENCIA_RESULTADO_NEGATIVO_PRIVADO("Abertura - Transferência de Resultado Negativo - Privado"),
    ABERTURA_TRANSFERENCIA_RESULTADO_NEGATIVO_PUBLICO("Abertura - Transferência de Resultado Negativo - Público"),
    ABERTURA_TRANSFERENCIA_RESULTADO_POSITIVO_PRIVADO("Abertura - Transferência de Resultado Positivo - Privado"),
    ABERTURA_TRANSFERENCIA_RESULTADO_POSITIVO_PUBLICO("Abertura - Transferência de Resultado Positivo - Público"),
    ABERTURA_TRANSFERENCIA_AJUSTES_NEGATIVO_PRIVADO("Abertura - Transferência de Ajustes Negativos - Privado"),
    ABERTURA_TRANSFERENCIA_AJUSTES_NEGATIVO_PUBLICO("Abertura - Transferência de Ajustes Negativos - Público"),
    ABERTURA_TRANSFERENCIA_AJUSTES_POSITIVO_PRIVADO("Abertura - Transferência de Ajustes Positivos - Privado"),
    ABERTURA_TRANSFERENCIA_AJUSTES_POSITIVO_PUBLICO("Abertura - Transferência de Ajustes Positivos - Público");

    private String descricao;

    TipoMovimentoContabil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
