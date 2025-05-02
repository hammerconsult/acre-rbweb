package br.com.webpublico.enums.rh.previdencia;

public enum SituacaoAdmissaoBBPrev {
    PARTICIPANTE_ADMITIDO_NA_EMPRESA("A", "Participante está sendo admitido na empresa"),
    PARTICIPANTE_READMITIDO_NA_EMPRESA("R", "Participante está sendo readmitido na empresa");

    private String codigo;
    private String descricao;

    SituacaoAdmissaoBBPrev(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}
