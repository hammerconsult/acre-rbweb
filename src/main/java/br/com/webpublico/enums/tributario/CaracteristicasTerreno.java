package br.com.webpublico.enums.tributario;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum CaracteristicasTerreno implements EnumComDescricao {

    TOPOGRAFIA("Topografia", "1"),
    PEDOLOGIA("Pedologia", "2"),
    SITUACAO("Situação", "4"),
    PATRIMONIO("Patrimônio", "6");

    private String descricao;
    private String codigo;

    CaracteristicasTerreno(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }
}
