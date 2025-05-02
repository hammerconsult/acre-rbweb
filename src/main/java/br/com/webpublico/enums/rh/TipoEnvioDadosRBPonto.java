package br.com.webpublico.enums.rh;

public enum TipoEnvioDadosRBPonto {
    ENVIO_AUTOMATICO("Envio automático"),
    ENVIO_MANUAL("Envio manual"),
    NAO_ENVIADO("Não enviado");

    private String descricao;

    TipoEnvioDadosRBPonto( String descricao) {
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
