package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Pis {
    PIS0("0", "não foi informado"),
    PIS1("1", "antecipação com encerramento de tributação"),
    PIS2("2", "exigibilidade suspensa"),
    PIS3("3", "imunidade"),
    PIS4("4", "retenção tributária"),
    PIS5("5", "substituição tributária"),
    PIS6("6", "tributação monofásica"),
    PIS8("8", "lançamento de ofício"),
    PIS9("9", "Isenção/redução cesta básica");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Pis(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }


    public static PgdasRegistro03110Pis getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Pis value : PgdasRegistro03110Pis.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Pis.PIS0;
    }
}
