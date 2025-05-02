package br.com.webpublico.enums;

public enum TipoIdentificaoCalculoAlvara {
    PRIMEIRO_CALCULO,
    CALCULO,
    RENOVACAO;

    TipoIdentificaoCalculoAlvara() {
    }

    public boolean isPrimeiroCalculo() {
        return PRIMEIRO_CALCULO.equals(this);
    }

    public boolean isCalculo() {
        return CALCULO.equals(this);
    }

    public boolean isRenovacao() {
        return RENOVACAO.equals(this);
    }
}
