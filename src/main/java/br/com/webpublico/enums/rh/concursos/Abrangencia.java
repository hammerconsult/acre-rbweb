package br.com.webpublico.enums.rh.concursos;

/**
 * Created by Buzatto on 18/09/2015.
 */
public enum Abrangencia {

    INDIVIDUAL("Individual"),
    GERAL("Geral");

    private String descricao;

    Abrangencia(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
