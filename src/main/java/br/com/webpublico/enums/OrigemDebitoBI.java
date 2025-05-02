package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum OrigemDebitoBI implements EnumComDescricao {

    CONTRIBUINTE("Contribuinte", 1),
    IMOBILIARIO("Imobiliário", 2),
    MOBILIARIO("Mobiliário", 3);
    private String descricao;
    private Integer codigoBI;

    OrigemDebitoBI(String descricao, Integer codigoBI) {
        this.descricao = descricao;
        this.codigoBI = codigoBI;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigoBI() {
        return codigoBI;
    }
}
