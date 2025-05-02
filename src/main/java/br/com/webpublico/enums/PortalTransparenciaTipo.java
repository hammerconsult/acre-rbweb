package br.com.webpublico.enums;

/**
 * Created by renat on 26/04/2016.
 */
public enum PortalTransparenciaTipo {
    RREO("RREO"),
    RGF("RGF"),
    LEI_4320("Lei 4320"),
    PRESTACAO_CONTAS_ANUAL("Prestação de contas Anual"),
    PRESTACAO_CONTAS_MENSAL("Prestação de contas Mensal"),
    ANUAL("Anual"),
    GERAL("Geral"),
    PROGRAMACAO_FINANCEIRA("Programação Financeira");
    private String descricao;

    PortalTransparenciaTipo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isTipoAnexoLRF() {
        return PortalTransparenciaTipo.RREO.equals(this) || PortalTransparenciaTipo.RGF.equals(this);
    }

    public boolean isTipoAnexoLei4320() {
        return PortalTransparenciaTipo.LEI_4320.equals(this);
    }
}
