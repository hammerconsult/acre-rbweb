package br.com.webpublico.enums;

public enum TipoRepresentanteLegal {
    ADMINISTRADOR("Administrador", 5),
    CURADOR("Curador", 9),
    MAE("Mãe", 14),
    PAI("Pai", 15),
    PROCURADOR("Procurador", 17),
    TUTOR("Tutor", 35);

    private String descricao;
    private int codigo;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private TipoRepresentanteLegal(String descricao, int codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

}

