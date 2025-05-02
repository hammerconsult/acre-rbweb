package br.com.webpublico.enums;

/**
 * Created by mga on 25/08/2017.
 */
public enum SituacaoAjusteDeposito {

    ABERTO("Aberto"),
    EFETUADO("Efetuado"),
    ESTORNADA("Estornado");
    private String descricao;

    private SituacaoAjusteDeposito(String descricao) {
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
