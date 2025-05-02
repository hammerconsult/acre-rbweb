package br.com.webpublico.enums;

/**
 * Created by Wellington on 10/09/2015.
 */
public enum TipoInventarioBemMovel {
    INICIAL("Inicial"), PASSAGEM_DE_RESPONSABILIDADE("Passagem de Responsabilidade"),
    MENSAL("Mensal"), ANUAL("Anual"), ENCERRAMENTO("Encerramento"), EVENTUAL("Eventual");

    private String descricao;

    TipoInventarioBemMovel(String descricao) {
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
