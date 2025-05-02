package br.com.webpublico.enums;

/**
 * @author Fabio
 */
public enum SituacaoFinalDenuncia {

    NAO_FINALIZADO("Não Finalizado"),
    CONCLUIDO("Concluído"),
    FISCALIZACAO("Processo Fiscalização"),
    CANCELADO("Cancelado");
    private String descricao;

    private SituacaoFinalDenuncia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
