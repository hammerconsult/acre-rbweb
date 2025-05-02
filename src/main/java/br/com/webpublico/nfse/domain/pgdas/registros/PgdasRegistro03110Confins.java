package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Confins {
    CONFINS0("0", "não foi informado"),
    CONFINS1("1", "antecipação com encerramento de tributação"),
    CONFINS2("2", "exigibilidade suspensa"),
    CONFINS3("3", "imunidade"),
    CONFINS4("4", "retenção tributária"),
    CONFINS5("5", "substituição tributária"),
    CONFINS6("6", "tributação monofásica"),
    CONFINS8("8", "lançamento de ofício"),
    CONFINS9("9", "Isenção/redução cesta básica");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Confins(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static PgdasRegistro03110Confins getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Confins value : PgdasRegistro03110Confins.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Confins.CONFINS0;
    }
}
