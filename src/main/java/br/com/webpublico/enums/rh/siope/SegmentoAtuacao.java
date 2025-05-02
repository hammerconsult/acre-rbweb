package br.com.webpublico.enums.rh.siope;

public enum SegmentoAtuacao {
    CRECHE("Creche", 1),
    PRE_ESCOLA("Pre-escola", 2),
    FUNDAMENTAL_1("Fundamental 1", 3),
    FUNDAMENTAL_2("Fundamental 2", 4),
    MEDIO("Médio", 5),
    PROFISSIONAL("Profissional", 6),
    ADMINISTRATIVO("Administrativo", 7),
    EJA("EJA", 8),
    ESPECIAL("Especial", 9);

    private String descricao;
    private Integer codigo;

    SegmentoAtuacao(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
