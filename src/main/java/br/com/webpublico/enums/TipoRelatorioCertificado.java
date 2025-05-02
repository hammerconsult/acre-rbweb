package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoRelatorioCertificado implements EnumComDescricao {

    RELATORIO_CONDUTOR_OTT("Relatório Condutor OTT"),
    RELATORIO_OPERADORA_OTT("Relatório Operadora de Tecnologia de Transporte ");

    private String descricao;

    TipoRelatorioCertificado(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
