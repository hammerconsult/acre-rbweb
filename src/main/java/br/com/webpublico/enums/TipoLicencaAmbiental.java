package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoLicencaAmbiental implements EnumComDescricao {

    LICENCA_PREVIA("Licença Prévia - LP"),
    LICENCA_DE_INSTALACAO("Licença de Instalação - LI"),
    LICENCA_DE_OPERACAO("Licença de Operação - LO"),
    ANALISE_DE_EIA_RIMA_E_OUTROS_ESTUDOS_AMBIENTAIS("Análise de EIA-RIMA e outros Estudos Ambientais"),
    LICENCA_AMBIENTAL_SIMPLIFICADA("Licença Ambiental Simplificada - LAS"),
    LICENCA_DE_EXTRACAO_MINERAL("Licença para Extração Mineral"),
    AUTORIZACAO_PODA_OU_CORTE_DE_ARVORES("Autorização para Poda ou Corte de Árvores"),
    AUTORIZACOES_DIVERSAS("Autorizações Diversas"),
    EMISSAO_DE_LAUDOS_DIVERSOS("Emissão de Laudos Diversos");

    TipoLicencaAmbiental(String descricao) {
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
