package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created by carlos on 17/10/16.
 */
public enum TipoEmissaoDirf implements EnumComDescricao {
    PESSOA_FISICA("Pessoa Física"),
    PESSOA_JURIDICA("Pessoa Jurídica"),
    AMBOS("Ambos");

    TipoEmissaoDirf(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isPessoaFisicaOuAmbos() {
        return TipoEmissaoDirf.PESSOA_FISICA.equals(this) || TipoEmissaoDirf.AMBOS.equals(this);
    }

    public boolean isPessoaJuridicaOuAmbos() {
        return TipoEmissaoDirf.PESSOA_JURIDICA.equals(this) || TipoEmissaoDirf.AMBOS.equals(this);
    }
}
