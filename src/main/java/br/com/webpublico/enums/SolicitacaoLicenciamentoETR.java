package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SolicitacaoLicenciamentoETR implements EnumComDescricao {
    INSTALACAO("Instalação"), REMANEJAMENTO("Remanejamento"), SUBSTITUICAO("Substituição");

    private String descricao;

    SolicitacaoLicenciamentoETR(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
