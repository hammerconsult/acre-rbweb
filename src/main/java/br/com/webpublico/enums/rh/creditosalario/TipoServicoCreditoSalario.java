package br.com.webpublico.enums.rh.creditosalario;

public enum TipoServicoCreditoSalario {

    OPTANTES("00", "Optantes"),
    COBRANCA("01", "Cobrança"),
    BOLETO_PAGAMENTO_ELETRONICO ("03", "Boleto de Pagamento Eletrônico"),
    CONCILIACAO_BANCARIA("04", "Conciliação Bancária"),
    DEBITOS("05", "Débitos"),
    CUSTODIA_DE_CHEQUES("06", "Custódia de Cheques"),
    GESTAO_DE_CAIXA("07", "Gestão de Caixa"),
    CONSULTA_INFORMACAO_MARGEM("08", "Consulta/Informação Margem"),
    AVERBACAO_CONSIGNACAO_RETENCAO("09", "Averbação da Consignação/Retenção"),
    PAGAMENTO_DIVIDENDOS("10", "Pagamento Dividendos"),
    MANUTENCAO_DA_CONSIGNACAO("11", "Manutenção da Consignação"),
    CONSIGNACAO_DE_PARCELAS("12", "Consignação de Parcelas"),
    GLOSA_DA_CONSIGNACAO("13", "Glosa da Consignação (INSS)"),
    CONSULTA_TRIBUTOS_A_PAGAR("14", "Consulta de Tributos a pagar"),
    PAGAMENTO_FORNECEDOR("20", "Pagamento Fornecedor "),
    PAGAMENTO_CONTAS_TRIBUTOS_IMPOSTOS("22", "Pagamento de Contas, Tributos e Impostos"),
    INTEROPERABILIDADE_ENTRE_CONTAS_DE_INSTITUICOES_DE_PAGAMENTOS("23", "Interoperabilidade entre Contas de Instituições de Pagamentos"),
    COMPROR("25", "Compror"),
    COMPROR_ROTATIVO("26", "Compror Rotativo"),
    ALEGACAO_DO_PAGADOR("29", "Alegação do Pagador"),
    PAGAMENTO_SALARIOS("30", "Pagamento Salários"),
    PAGAMENTO_DE_HONORARIOS("32", "Pagamento de honorários"),
    PAGAMENTO_DE_BOLSA_AUXILIO("33", "Pagamento de bolsa auxílio"),
    PAGAMENTO_DE_PREBENDA("34", "Pagamento de prebenda (remuneração a padres e sacerdotes)"),
    VENDOR("40", "Vendor"),
    VENDOR_A_TERMO("41", "Vendor a Termo"),
    PAGAMENTO_SINISTROS_SEGURADOS("50", "Pagamento Sinistros Segurados"),
    PAGAMENTO_DESPESAS_VIAJANTE_EM_TRANSITO("60", "Pagamento Despesas Viajante em Trânsito"),
    PAGAMENTO_AUTORIZADO("70", "Pagamento Autorizado"),
    PAGAMENTO_CREDENCIADOS("75", "Pagamento Credenciados"),
    PAGAMENTO_DE_REMUNERACAO("77", "Pagamento de Remuneração"),
    PAGAMENTO_REPRESENTANTES_VENDEDORES_AUTORIZADOS("80", "Pagamento Representantes / Vendedores Autorizados"),
    PAGAMENTO_BENEFICIOS("90", "Pagamento Benefícios"),
    PAGAMENTOS_DIVERSOS("98", "Pagamentos Diversos");

    private String descricao;
    private String codigo;

    TipoServicoCreditoSalario(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
