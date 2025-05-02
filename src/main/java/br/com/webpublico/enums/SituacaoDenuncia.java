package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum SituacaoDenuncia {

    ABERTO("Aberto"),
    DESIGNADO("Designado"),
    CONCLUIDO("Concluído"),
    FISCALIZACAO("Processo Fiscalização"),
    CANCELADO("Cancelado");
    private String descricao;

    private SituacaoDenuncia(String descricao) {
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
