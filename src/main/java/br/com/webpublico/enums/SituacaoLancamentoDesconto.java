package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum SituacaoLancamentoDesconto {

    ABERTO("Aberto"),
    EFETIVADO("Efetivado"),
    ESTORNADO("Estornado");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private SituacaoLancamentoDesconto(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
