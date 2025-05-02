package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum TipoSaldoReceitaORC {
    // + ReceitaLOA
    // - EstornoReceitaLOA
    // + AlteracaoORC (Somente 'Previsão Adicional da Receita') (Adicional +) (Anulação -)
    // - EstornoAlteracaoORC (NÃO EXISTE A ENTIDADE AINDA)
    // + LancamentoReceitaOrc
    // - ReceitaORCEstorno

    RECEITALOA("Receita LOA"),
    ESTORNORECEITALOA("Estorno da Receita LOA"),
    ALTERACAOORC_ADICIONAL("Alteração ORC - Adicional"),
    ALTERACAOORC_ANULACAO("Alteração ORC - Anulação"),
    ESTORNOALTERACAOORC("Estorno de Alteração ORC"),
    LANCAMENTORECEITAORC("Lançamento de Receita ORC"),
    RECEITAORCESTORNO("Estorno de Receita ORC");

    private String descricao;

    private TipoSaldoReceitaORC(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
