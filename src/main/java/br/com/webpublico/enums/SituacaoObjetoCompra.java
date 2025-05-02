package br.com.webpublico.enums;

/**
 * Created by HardRock on 30/03/2017.
 */
public enum SituacaoObjetoCompra {

    AGUARDANDO("Aguardando"),
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido");
    private String descricao;

    SituacaoObjetoCompra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
