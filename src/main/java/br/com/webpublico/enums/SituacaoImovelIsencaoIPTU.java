package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum SituacaoImovelIsencaoIPTU {

    NAO_ATUALIZA("1-Não Atualiza"),
    REATIVA("2-Reativa"),
    BAIXA("3-Baixa");

    private String descricao;

    private SituacaoImovelIsencaoIPTU(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
