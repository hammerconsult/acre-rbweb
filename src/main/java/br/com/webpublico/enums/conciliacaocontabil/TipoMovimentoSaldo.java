package br.com.webpublico.enums.conciliacaocontabil;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoMovimentoSaldo implements EnumComDescricao {
    MOVIMENTO("Movimento"),
    SALDO("Saldo");

    private String descricao;

    TipoMovimentoSaldo(String descricao) {
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

    public boolean isSaldo() {
        return TipoMovimentoSaldo.SALDO.equals(this);
    }
}
