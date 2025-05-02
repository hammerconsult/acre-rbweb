package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Irpj {
    IRPJ0("0", "não foi informado"),
    IRPJ2("2", "exigibilidade suspensa"),
    IRPJ3("3", "imunidade"),
    IRPJ8("8", "lançamento de ofício");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Irpj(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static PgdasRegistro03110Irpj getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Irpj value : PgdasRegistro03110Irpj.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Irpj.IRPJ0;
    }
}
