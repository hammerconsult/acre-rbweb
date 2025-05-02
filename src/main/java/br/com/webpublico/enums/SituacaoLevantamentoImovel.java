package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoLevantamentoImovel implements EnumComDescricao {

    COLETA_DADOS("Coleta de Dados"),
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação"),
    FINALIZADO("Finalizado");
    private String descricao;

    SituacaoLevantamentoImovel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
