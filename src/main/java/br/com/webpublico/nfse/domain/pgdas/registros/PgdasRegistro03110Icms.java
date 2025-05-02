package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Icms {
    ICMS0("0", "não foi informado"),
    ICMS1("1", "antecipação com encerramento de tributação"),
    ICMS2("2", "exigibilidade suspensa"),
    ICMS3("3", "imunidade"),
    ICMS4("4", "retenção tributária"),
    ICMS5("5", "substituição tributária"),
    ICMS6("6", "tributação monofásica"),
    ICMS7("7", "Isenção / Redução"),
    ICMS8("8", "lançamento de ofício"),
    ICMS9("9", "Isenção/redução cesta básica");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Icms(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static PgdasRegistro03110Icms getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Icms value : PgdasRegistro03110Icms.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Icms.ICMS0;
    }
}
