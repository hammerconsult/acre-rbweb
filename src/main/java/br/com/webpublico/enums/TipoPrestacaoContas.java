package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 02/01/14
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public enum TipoPrestacaoContas {

    APROVADO("Aprovado"),
    EM_ANALISE("Em Análise"),
    EM_DILIGENCIA("Em Diligência"),
    EM_TCE("Em TCE"),
    REPROVADO("Reprovado");
    private String descricao;

    private TipoPrestacaoContas(String descricao) {
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
