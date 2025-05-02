package br.com.webpublico.enums;

/**
 * Created by israeleriston on 25/08/16.
 */
public enum TipoRelatorioApresentacao {

    RESUMIDO("Resumido"),
    DETALHADO("Detalhado");

    private String descricao;

    TipoRelatorioApresentacao(String descricao) {
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
