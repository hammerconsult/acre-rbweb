package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 12/02/14
 * Time: 11:11
 * To change this template use File | Settings | File Templates.
 */

//Equivale a Conta Corrento do TCE
public enum TipoMovimentoTCE {
    NAO_APLICAVEL("Não Aplicável", 0),
    CREDOR("Credor", 1),
    RECEITA_ORCAMENTARIA("Receita Orçamentária", 2),
    DOTACAO("Dotacao", 3),
    DESPESA_ORCAMENTARIA("Despesa Orçamentária", 4),
    MOVIMENTACAO_FINANCEIRA("Movimentação Financeria", 5),
    FONTE_DE_RECURSO("Fonte de Recursos", 6),
    RESTO_A_PAGAR("Resto a Pagar", 7),
    CONTRATOS_PASSIVOS("Contratos Passivos", 8),
    CONTRATOS("Contratos", 9);

    private String descricao;
    private Integer codigo;

    private TipoMovimentoTCE(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
