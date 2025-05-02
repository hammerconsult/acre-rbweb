package br.com.webpublico.enums;

/**
 * Created by venom on 03/11/14.
 */
public enum NivelConfea {
    GRADUACAO("1 - Graduação"),
    TECNOLOGO("2 - Tecnólogo"),
    TECNICO_MEDIO("3 - Técnico de Nível Médio"),
    ESPECIALIZACAO("4 - Especialização");
    private String descricao;

    NivelConfea(String descricao) {
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
