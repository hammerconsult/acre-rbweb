package br.com.webpublico.enums.rh.estudoatuarial;

public enum TipoRegimePrevidenciarioEstudoAtuarial {
    RGPS("RGPS", "1"),
    RPPS_ESFERA_MUNICIPAL("RPPS (Outros Esfera Municipal)", "2"),
    RPPS_ESFERA_ESTADUAL("RPPS (Outros Esfera Estadual)", "3"),
    RPPS_ESFERA_FEDERAL("RPPS (Outros Esfera Federal)", "4");

    private String descricao;
    private String codigo;

    TipoRegimePrevidenciarioEstudoAtuarial(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}

