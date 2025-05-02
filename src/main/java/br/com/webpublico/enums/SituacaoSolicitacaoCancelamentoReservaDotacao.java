package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 06/06/14
 * Time: 15:57
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoSolicitacaoCancelamentoReservaDotacao {
    ABERTA("Aberta"),
    APROVADA("Aprovada"),
    REPROVADA("Reprovada");

    private String descricao;

    private SituacaoSolicitacaoCancelamentoReservaDotacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
