package br.com.webpublico.enums;

/**
 * Created by mga on 04/06/19.
 */
public enum TipoVinculoImobiliario {

    VINCULACAO_INDIVIDUALIZADA("Vinculação Individualizada"),
    VINCULACAO_TOTALIZADA("Vinculação Totalizada");

    private String descricao;

    TipoVinculoImobiliario(String descricao){
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
}
