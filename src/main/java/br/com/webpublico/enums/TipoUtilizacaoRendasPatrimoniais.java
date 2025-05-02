package br.com.webpublico.enums;

/**
 * @author daniel
 */
public enum TipoUtilizacaoRendasPatrimoniais {
    ALUGUEL_PATRIMONIO_PUBLICO("Aluguel de Patrimônio Público"),
    OCUPACAO_SOLO_URBANO("Ocupação de Solo Urbano");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    TipoUtilizacaoRendasPatrimoniais(String descricao) {
        this.descricao = descricao;
    }

}
