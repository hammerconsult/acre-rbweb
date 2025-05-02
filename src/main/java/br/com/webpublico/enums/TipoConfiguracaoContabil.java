package br.com.webpublico.enums;

/**
 * Created by HardRock on 03/01/2017.
 */
public enum TipoConfiguracaoContabil {

    //Eventos
    CLP_LCP("CLP/Item Evento CLP/LCP"),
    //CDEs
    CDE_OBRIGACAO_PAGAR("CDE - Obrigação a pagar"),
    CDE_EMPENHO("CDE - Empenho"),
    CDE_EMPENHO_RESTO("CDE - Inscrição em Restos a Pagar"),
    CDE_CANCELAMENTO_RESTO("CDE - Cancelamento de Restos a Pagar"),
    CDE_LIQUIDACAO("CDE - Liquidação"),
    CDE_LIQUIDACAO_RESTO("CDE - Liquidação de Restos a Pagar"),
    CDE_PAGAMENTO("CDE - Pagamento"),
    CDE_PAGAMENTO_RESTO("CDE - Pagamento de Restos a Pagar"),
    CDE_RECEITA_REALIZADA("CDE - Receita Realizada"),
    CDE_CREDITO_RECEBER("CDE - Crédito a Receber"),
    CDE_DIVIDA_ATIVA("CDE - Dívida Ativa"),
    CDE_INVESTIMENTO("CDE - Investimento"),
    CDE_PATRIMONIO_LIQUIDO("CDE - Patrimônio Líquido"),
    //OCCs
    OCC_CONTA("OCC - Conta"),
    OCC_BANCO("OCC - Conta Financeira"),
    OCC_CLASSE_CREDOR("OCC - Classe de Pessoa"),
    OCC_GRUPO_PATRIMONIAL("OCC - Grupo Patrimonial"),
    OCC_GRUPO_MATERIAL("OCC - Grupo Material"),
    OCC_NATUREZA_DIVIDA_PUBLICA("OCC - Natureza Dívida Pública"),
    OCC_TIPO_PASSIVO_ATUARIAL("OCC - Tipo Passivo Atuarial"),
    OCC_ORIGEM_RECURSO("OCC - Origem do Recurso"),
    //OUTROS
    CONFIGURACAO_CONTA_DESPESA_GRUPO_PATRIMONIAL("Configuração Conta de Despesa Grupo Patrimonial"),
    CONFIGURACAO_CONTA_DESPESA_GRUPO_MATERIAL("Configuração Conta de Despesa Grupo Material"),
    CONFIGURACAO_CONTA_DESPESA_TIPO_VIAGEM("Configuração Conta de Despesa Tipo de Viagem"),
    CONFIGURACAO_CONTA_DESPESA_TIPO_PESSOA("Configuração Conta de Despesa Tipo de Pessoa"),
    CONFIGURACAO_CONTA_DESPESA_TIPO_OBJETO_COMPRA("Configuração Conta de Despesa Tipo de Objeto de Compra"),
    UNIDADE_FONTE_CONTA_FINANCEIRA("Unidade e Fonte de Recurso para Conta Financeira"),
    UNIDADE_GESTORA("Unidade Gestora"),
    GRUPO_ORCAMENTARIO("Grupo Orçamentário");
    private String descricao;

    TipoConfiguracaoContabil(String descricao) {
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
