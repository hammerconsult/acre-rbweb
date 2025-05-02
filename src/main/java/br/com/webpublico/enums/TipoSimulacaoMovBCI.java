package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoSimulacaoMovBCI implements EnumComDescricao {
    DIVIDAS_ATIVAS("Dívidas Ativas"),
    CALCULOS_REVISOES_IPTU("Cálculos e Revisões de IPTU"),
    ISENCOES_IPTU("Isenções de IPTU"),
    PARCELAMENTOS("Parcelamentos"),
    LANCAMENTOS_ITBI("Lançamentos de ITBI"),
    DEBITOS_DAMS("Débitos/DAMs"),
    CERTIDOES("Certidões"),
    PROCESSOS_DEBITO("Processos de Débito");

    private String descricao;

    TipoSimulacaoMovBCI(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
