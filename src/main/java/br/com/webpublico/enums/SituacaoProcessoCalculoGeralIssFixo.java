package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 20/09/13
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoProcessoCalculoGeralIssFixo {
    SIMULACAO("Simulação"),
    EFETIVADO("Efetivado");
    public String descricao;

    private SituacaoProcessoCalculoGeralIssFixo(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
