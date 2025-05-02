package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 03/12/2018.
 */
public enum TipoSituacaoGeracaoEsocial {
    ARQUIVO_GERADO("Arquivo criado"),
    ARQUIVO_NAO_GERADO("Arquivo não gerado");

    private String descricao;

    TipoSituacaoGeracaoEsocial (String descricao) {
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
