package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Csll {
    CSLL0("0", "não foi informado"),
    CSLL2("2", "exigibilidade suspensa"),
    CSLL3("3", "imunidade"),
    CSLL8("8", "lançamento de ofício");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Csll(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static PgdasRegistro03110Csll getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Csll value : PgdasRegistro03110Csll.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Csll.CSLL0;
    }
}
