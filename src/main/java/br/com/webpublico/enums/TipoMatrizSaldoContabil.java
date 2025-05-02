package br.com.webpublico.enums;

public enum TipoMatrizSaldoContabil {
    BEGINNING_BALANCE("beginning_balance"),
    PERIOD_CHANGE("period_change"),
    ENDING_BALANCE("ending_balance");

    private String tipo;

    TipoMatrizSaldoContabil(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
