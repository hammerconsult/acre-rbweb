package br.com.webpublico.enums.rh.creditosalario;

public enum FormaLancamentoCreditoSalario {

    CREDITO_EM_CONTA_CORRENTE_SALARIO("01", "Crédito em Conta Corrente/Salário"),
    CHEQUE_PAGAMENTO_ADMINISTRATIVO("02", "Cheque Pagamento / Administrativo"),
    DOC_TED("03", "DOC/TED"),
    CARTAO_SALARIO("04", "Cartão Salário"),
    CREDITO_EM_CONTA_POUPANCA("05", "Crédito em Conta Poupança"),
    OP_A_DISPOSICAO("10", "OP à Disposição"),
    PAGAMENTO_CONTAS_TRIBUTOS_COM_CODIGO_DE_BARRAS("11", "Pagamento de Contas e Tributos com Código de Barras"),
    TRIBUTO_DARF_NORMAL("16", "Tributo - DARF Normal"),
    TRIBUTO_GPS("17", "Tributo - GPS (Guia da Previdência Social)"),
    TRIBUTO_DARF_SIMPLES("18", "Tributo - DARF Simples"),
    TRIBUTO_IPTU_PREFEITURAS("19", "Tributo - IPTU – Prefeituras"),
    PAGAMENTO_COM_AUTENTICACAO("20", "Pagamento com Autenticação"),
    TRIBUTO_DARJ("21", "Tributo – DARJ"),
    TRIBUTO_GARE_SP_ICMS("22", "Tributo - GARE-SP ICMS"),
    TRIBUTO_GARE_SP_DR("23", "Tributo - GARE-SP DR"),
    TRIBUTO_GARE_SP_ITCMD("24", "Tributo - GARE-SP ITCMD"),
    TRIBUTO_IPVA("25", "Tributo - IPVA"),
    TRIBUTO_LICENCIAMENTO("26", "Tributo - Licenciamento"),
    TRIBUTO_DPVAT("27", "Tributo – DPVAT"),
    LIQUIDACAO_TITULOS_PROPRIO_BANCO("30", "Liquidação de Títulos do Próprio Banco"),
    PAGAMENTO_TITULOS_OUTROS_BANCOS("31", "Pagamento de Títulos de Outros Bancos"),
    EXTRATO_CONTA_CORRENTE("40", "Extrato de Conta Corrente"),
    TED_OUTRA_TITULARIDADE("41", "TED – Outra Titularidade"),
    TED_MESMA_TITULARIDADE("43", "TED – Mesma Titularidade"),
    TED_PARA_TRANSFERENCIA_DE_CONTA_INVESTIMENTO("44", "TED para Transferência de Conta Investimento"),
    PIX_TRANSFERENCIA("45", "PIX Transferência"),
    PIX_QR_CODE("47", "PIX QR-CODE"),
    DEBITO_EM_CONTA_CORRENTE("50", "Débito em Conta Corrente"),
    EXTRATO_PARA_GESTAO_DE_CAIXA("70", "Extrato para Gestão de Caixa"),
    DEPOSITO_JUDICIAL_EM_CONTA_CORRENTE("71", "Depósito Judicial em Conta Corrente"),
    DEPOSITO_JUDICIAL_EM_POUPANCA("72", "Depósito Judicial em Poupança"),
    EXTRATO_CONTA_INVESTIMENTO("73", "Extrato de Conta Investimento"),
    PAGAMENTO_TRIBUTOS_MUNICIPAIS_ISS_LCP_157_PROPRIO_BANCO("80", "Pagamento de tributos municipais ISS – LCP 157 – próprio Banco"),
    PAGAMENTO_TRIBUTOS_MUNICIPAIS_ISS_LCP_157_OUTROS_BANCOS("81", "Pagamento de Tributos Municipais ISS – LCP 157 – outros Bancos");

    private String descricao;
    private String codigo;

    FormaLancamentoCreditoSalario(String codigo, String descricao) {
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
