package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum SituacaoRomaneioFeira implements EnumComDescricao {
    EM_ABERTO("Em Aberto"),
    FINALIZADO("Finalizado");

    private String descricao;

    SituacaoRomaneioFeira(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isEmAberto() {
        return SituacaoRomaneioFeira.EM_ABERTO.equals(this);
    }

    public boolean isFinalizado() {
        return SituacaoRomaneioFeira.FINALIZADO.equals(this);
    }
}
