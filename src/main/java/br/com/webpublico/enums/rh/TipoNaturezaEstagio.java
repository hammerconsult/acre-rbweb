package br.com.webpublico.enums.rh;

/**
 * Created by William on 12/11/2018.
 */
public enum TipoNaturezaEstagio {
    OBRIGATORIO("Obrigatório", "O"),
    NAO_OBRIGATORIO("Não Obrigatório", "N");

    private String descricao;
    private String sigla;

    TipoNaturezaEstagio(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
