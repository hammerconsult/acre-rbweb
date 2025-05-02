package br.com.webpublico.enums.rh.dirf;

public enum GrauParentescoDirf {
    CONJUGE_COMPANHEIRO("03", "Cônjuge/Companheiro(a)"),
    FILHO("04", "Filho(a)"),
    ENTEADO("06", "Enteado(a)"),
    PAI_MAE("08", "Pai/Mãe"),
    AGREGADO_OUTROS("10", "Agregado/Outros");

    private GrauParentescoDirf(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    private String codigo;
    private String descricao;

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
