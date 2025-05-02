package br.com.webpublico.enums.rh.previdencia;

public enum TipoEnvioBBPrev {
    PROPOSTA_ADESAO("P", "Proposta de Adesão"),
    MANUTENCAO("M", "Manutenção");

    private String codigo;
    private String descricao;

    TipoEnvioBBPrev(String codigo, String descricao) {
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
