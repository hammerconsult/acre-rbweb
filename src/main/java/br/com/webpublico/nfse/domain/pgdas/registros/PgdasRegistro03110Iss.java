package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Iss {
    ISS0("0", "não foi informado"),
    ISS1("1", "antecipação com encerramento de tributação"),
    ISS2("2", "exigibilidade suspensa"),
    ISS3("3", "imunidade"),
    ISS4("4", "retenção tributária"),
    ISS5("5", "substituição tributária"),
    ISS6("6", "tributação monofásica"),
    ISS7("7", "Isenção / Redução"),
    ISS8("8", "lançamento de ofício");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Iss(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static PgdasRegistro03110Iss getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Iss value : PgdasRegistro03110Iss.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Iss.ISS0;
    }
}
