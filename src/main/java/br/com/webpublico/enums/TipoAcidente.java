package br.com.webpublico.enums;

/**
 * Created by israeleriston on 18/11/15.
 */
public enum TipoAcidente {
    PERCURSO("Acidente de Percurso"),
    TRABALHO("Acidente de Trabalho"),
    TODOS("Acidente de Percurso e Trabalho");

    private String descricao;

    TipoAcidente(String descricao) {
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
