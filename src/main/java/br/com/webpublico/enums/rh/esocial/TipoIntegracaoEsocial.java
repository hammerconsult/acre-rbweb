package br.com.webpublico.enums.rh.esocial;

public enum TipoIntegracaoEsocial {
    ESOCIAL( "Esocial"),
    REINF( "Efd-Reinf");

    private String descricao;

    TipoIntegracaoEsocial(String descricao) {
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return  descricao;
    }
}
