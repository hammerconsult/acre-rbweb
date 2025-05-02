package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoEntradaMaterialOuAquisicao implements EnumComDescricao {
    //Enum criado para englobar os valores dos enums SituacaoEntradaMaterial e SituacaoAquisicao.
    //É utilizado no listar da tela de /documento-fiscal/listar/
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação"),
    FINALIZADO("Finalizado"),
    ESTORNADO("Estornado"),
    RECUSADO("Recusado"),
    EM_ELABORACAO("Em Elaboração"),
    CONCLUIDA("Concluída"),
    ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO("Atesto Provisório - Aguardando Liquidação"),
    ATESTO_PROVISORIO_COM_PENDENCIA("Atesto Provisório - Com Pendência"),
    ATESTO_DEFINITIVO_LIQUIDADO("Atesto Definitivo - Liquidado");
    private String descricao;

    SituacaoEntradaMaterialOuAquisicao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
