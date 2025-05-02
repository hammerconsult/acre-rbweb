/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author fabio
 */
public enum ArquivosSagres {

    ORCAMENTO("1 - Orçamento", "Orcamento", PeriodicidadeArquivoSagres.ANUAL),
    UNIDADE_ORCAMENTARIA("2 - Unidade Orçamentária", "UnidadeOrcamentaria", PeriodicidadeArquivoSagres.ANUAL_MENSAL),
    PROGRAMAS("3 - Programas", "Programas", PeriodicidadeArquivoSagres.ANUAL_MENSAL),
    ACAO("4 - Ação", "Acao", PeriodicidadeArquivoSagres.ANUAL_MENSAL),
    DOTACAO("5 - Dotação", "Dotacao", PeriodicidadeArquivoSagres.ANUAL),
    ELENCO_CONTAS("6 - Elenco Contas", "ElencoContas", PeriodicidadeArquivoSagres.ANUAL_MENSAL),
    CADASTRO_CONTAS("7 - Cadastro Contas", "CadastroContas", PeriodicidadeArquivoSagres.ANUAL_MENSAL),
    RELACIONAMENTO_RECEITA_ORCAM("8 - Relacionamento Receita Orçamentária", "RelacionamentoReceitaOrcamentaria", PeriodicidadeArquivoSagres.ANUAL_MENSAL),
    RELACIONAMENTO_RECEITA_EXTRA("9 - Relacionamento Receita Extra", "RelacionamentoReceitaExtra", PeriodicidadeArquivoSagres.ANUAL_MENSAL),
    RELACIONAMENTO_DESPESA_EXTRA("10 - Relacionamento Despesa Extra", "RelacionamentoDespesaExtra", PeriodicidadeArquivoSagres.ANUAL),
    PREVISAO_RECEITA("11 - Previsão Receita", "PrevisaoReceita", PeriodicidadeArquivoSagres.ANUAL),
    ATUALIZACAO_ORCAMENTARIA("12 - Atualização Orçamentária", "AtualizacaoOrcamentaria", PeriodicidadeArquivoSagres.MENSAL),
    DECRETOS("13 - Decretos", "Decretos", PeriodicidadeArquivoSagres.MENSAL),
    EMPENHOS("14 - Empenhos", "Empenhos", PeriodicidadeArquivoSagres.MENSAL),
    EMPENHO_ESTORNO("15 - Empenho Estorno", "EmpenhoEstorno", PeriodicidadeArquivoSagres.MENSAL),
    LIQUIDACAO("16 - Liquidação", "Liquidacao", PeriodicidadeArquivoSagres.MENSAL),
    PAGAMENTOS("17 - Pagamentos", "Pagamentos", PeriodicidadeArquivoSagres.MENSAL),
    RETENCAO("18 - Retenção", "Retencao", PeriodicidadeArquivoSagres.MENSAL),
    RECEITA_ORCAMENTARIA("19 - Receita Orçamentária", "ReceitaOrcamentaria", PeriodicidadeArquivoSagres.MENSAL),
    RECEITA_EXTRA("20 - Receita Extra", "ReceitaExtra", PeriodicidadeArquivoSagres.ANUAL),
    DESPESA_EXTRA("21 - Despesa Extra", "DespesaExtra", PeriodicidadeArquivoSagres.ANUAL),
    RESTOS_INSCRITOS("22 - Restos Inscritos", "RestosInscritos", PeriodicidadeArquivoSagres.ANUAL),
    ESTORNO_RESTOS("23 - Estorno Restos", "EstornoRestos", PeriodicidadeArquivoSagres.MENSAL),
    PAGAMENTOS_RESTOS("24 - Pagamentos Restos", "PagamentosRestos", PeriodicidadeArquivoSagres.MENSAL),
    RETENCAO_RESTOS("25 - Retenção Restos", "RetencaoRestos", PeriodicidadeArquivoSagres.MENSAL),
    CONCILIACAO_BANCARIA("26 - Conciliação Bancária", "ConciliacaoBancaria", PeriodicidadeArquivoSagres.MENSAL),
    SALDO_INICIAL("27 - Saldo Inicial", "SaldoInicial", PeriodicidadeArquivoSagres.ANUAL),
    SALDO_MENSAL("28 - Saldo Mensal", "SaldoMensal", PeriodicidadeArquivoSagres.MENSAL),
    LACAMENTO_CONTABIL("29 - Lançamento Contábil", "LancamentoContabil", PeriodicidadeArquivoSagres.MENSAL),
    LACAMENTO_CONTABIL_ITEM("30 - Lançamento Contábil Item", "LancamentoContabilItem", PeriodicidadeArquivoSagres.MENSAL),
    FORNACEDORES("31 - Fornecedores", "Fornecedores", PeriodicidadeArquivoSagres.MENSAL),
    EXCESSO_ARRECADACAO("32 - Excesso Arrecadação", "ExcessoArrecadacao", PeriodicidadeArquivoSagres.MENSAL),
    LIQUIDACAO_RESTOS_ESTORNO("33 - Liquidação Restos Estorno", "LiquidacaoRestosEstorno", PeriodicidadeArquivoSagres.MENSAL),
    PAGAMENTO_ESTORNO("34 - Pagamento Estorno", "PagamentoEstorno", PeriodicidadeArquivoSagres.MENSAL),
    LIQUIDACAO_ESTONO("35 - Liquidação Estorno", "LiquidacaoEstorno", PeriodicidadeArquivoSagres.MENSAL),
    LIQUIDACAO_RESTOS("36 - Liquidação Restos", "LiquidacaoRestos", PeriodicidadeArquivoSagres.MENSAL),
    PAGAMENTO_RESTO_ESTORNO("37 - Pagamento Resto Estorno", "PagamentoRestoEstorno", PeriodicidadeArquivoSagres.MENSAL),
    AGENTE_POLITICO("38 - Agente Político", "AgentePolitico", PeriodicidadeArquivoSagres.MENSAL),
    ORDENADOR("39 - Ordenador", "Ordenador", PeriodicidadeArquivoSagres.MENSAL),
    GESTOR("40 - Gestor", "Gestor", PeriodicidadeArquivoSagres.MENSAL);

    private String descricao;
    private String nomeArquivo;
    private PeriodicidadeArquivoSagres periodicidade;

    public String getDescricao() {
        return descricao;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public PeriodicidadeArquivoSagres getTipo() {
        return periodicidade;
    }

    public void setTipo(PeriodicidadeArquivoSagres periodicidade) {
        this.periodicidade = periodicidade;
    }

    private ArquivosSagres(String descricao, String nomeArquivo) {
        this.descricao = descricao;
        this.nomeArquivo = nomeArquivo;
    }

    private ArquivosSagres(String descricao, String nomeArquivo, PeriodicidadeArquivoSagres periodicidade) {
        this.descricao = descricao;
        this.nomeArquivo = nomeArquivo;
        this.periodicidade = periodicidade;

    }

    public enum PeriodicidadeArquivoSagres {
        MENSAL("Mensal"),
        ANUAL("Anual"),
        ANUAL_MENSAL("Anual e Mensal");

        private String descricao;

        private PeriodicidadeArquivoSagres(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

}
