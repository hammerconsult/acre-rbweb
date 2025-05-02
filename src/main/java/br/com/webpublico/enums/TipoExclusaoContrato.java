package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoExclusaoContrato implements EnumComDescricao {

    CONTRATO("Contrato"),
    EXECUCAO_CONTRATO("Execução de Contrato"),
    ADITIVO("Aditivo Contrato"),
    APOSTILAMENTO("Apostilamento Contrato");
    private String descricao;

    TipoExclusaoContrato(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
