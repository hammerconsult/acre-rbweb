package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoCalculoAlvara implements EnumComDescricao {

    INTEGRAL("Integral"),
    PROPORCIONAL("Proporcional"),
    FIXO_UFMRB("Fixo em UFMRB");

    private String descricao;

    TipoCalculoAlvara(String descricao) {
        this.descricao = descricao;
    }

    @Override
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

    public boolean isIntegral() {
        return INTEGRAL.equals(this);
    }

    public boolean isProporcional() {
        return PROPORCIONAL.equals(this);
    }

    public boolean isFixo() {
        return FIXO_UFMRB.equals(this);
    }
}
