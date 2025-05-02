package br.com.webpublico.enums.rh;

public enum TipoServicoPrestador {
    FRETES_PRESTACAO_SERVICOS("(10%) Fretes e prestação de serviços com terraplangem, colheitadeiras e assemelhado."),
    TRANSPORTE_DE_PASSAGEIROS("(60%) Transporte de Passageiros.");

    private String descricao;

    TipoServicoPrestador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
