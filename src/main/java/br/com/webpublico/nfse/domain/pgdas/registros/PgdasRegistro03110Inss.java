package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Inss {
    INSS0("0", "não foi informado"),
    INSS2("2", "exigibilidade suspensa"),
    INSS3("3", "imunidade"),
    INSS8("8", "lançamento de ofício");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Inss(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static PgdasRegistro03110Inss getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Inss value : PgdasRegistro03110Inss.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Inss.INSS0;
    }
}
