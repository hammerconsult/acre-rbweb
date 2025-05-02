package br.com.webpublico.enums.rh.esocial;

public enum TipoEventoFPEmpregador {
    PADRAO("Padrão"),
    ALTERNATIVO("Alternativo"),
    DECIMO_TERCEIRO("Décimo Terceiro");

    TipoEventoFPEmpregador(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

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
