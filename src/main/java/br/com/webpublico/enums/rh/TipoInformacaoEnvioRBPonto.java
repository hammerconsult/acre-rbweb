package br.com.webpublico.enums.rh;

public enum TipoInformacaoEnvioRBPonto {
    AFASTAMENTO("Afastamento"),
    FERIAS("Férias");

    private String descricao;

    TipoInformacaoEnvioRBPonto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}
