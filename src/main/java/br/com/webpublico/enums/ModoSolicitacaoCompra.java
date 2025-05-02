package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum ModoSolicitacaoCompra implements EnumComDescricao {

    LICITACAO("Licitação"),
    DISPENSA_INEXIGIBILIDADE("Dispensa ou Inexigibilidade"),
    PROCEDIMENTO_AUXILIARES("Procedimentos Auxiliares");
    private String descricao;

    ModoSolicitacaoCompra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isLicitacao() {
        return LICITACAO.equals(this);
    }

    public boolean isDispensa() {
        return DISPENSA_INEXIGIBILIDADE.equals(this);
    }

    public boolean isProcedimentosAuxiliares() {
        return PROCEDIMENTO_AUXILIARES.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
