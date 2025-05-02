package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoConvenioArquivoMensal implements EnumComDescricao {
    CONVENIO_RECEITA("Convênio de Receita"),
    CONVENIO_DESPESA("Convênio de Despesa");
    private String descricao;

    TipoConvenioArquivoMensal(String descricao) {
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
