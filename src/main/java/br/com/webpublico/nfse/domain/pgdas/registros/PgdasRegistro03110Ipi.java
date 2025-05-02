package br.com.webpublico.nfse.domain.pgdas.registros;

public enum PgdasRegistro03110Ipi {
    IPI0("0", "não foi informado"),
    IPI1("1", "antecipação com encerramento de tributação"),
    IPI2("2", "exigibilidade suspensa"),
    IPI3("3", "imunidade"),
    IPI4("4", "retenção tributária"),
    IPI5("5", "substituição tributária"),
    IPI6("6", "tributação monofásica"),
    IPI8("8", "lançamento de ofício");

    private String codigo;
    private String descricao;

    PgdasRegistro03110Ipi(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static PgdasRegistro03110Ipi getEnumByCodigo(String valor) {
        for (PgdasRegistro03110Ipi value : PgdasRegistro03110Ipi.values()) {
            if (value.codigo.equals(valor)) {
                return value;
            }
        }
        return PgdasRegistro03110Ipi.IPI0;
    }
}
