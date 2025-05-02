package br.com.webpublico.enums;

public enum TipoControleCalculoAlvara {
    CALCULO, RECALCULO, AGUARDANDO_CANCELAMENTO, CANCELADO;

    TipoControleCalculoAlvara() {
    }

    public boolean isCalculo() {
        return this.equals(CALCULO);
    }

    public boolean isRecalculo() {
        return this.equals(RECALCULO);
    }

    public boolean isAguardandoCancelamento() {
        return this.equals(AGUARDANDO_CANCELAMENTO);
    }
}
