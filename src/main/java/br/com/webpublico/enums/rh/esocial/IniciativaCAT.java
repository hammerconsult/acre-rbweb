package br.com.webpublico.enums.rh.esocial;

public enum IniciativaCAT  {
    EMPREGADOR("Empregador", 1),
    ORDEM_JUDICIAL("Ordem judicial", 2),
    DETERMINACAO_ORGAO_FISCALIZADOR("Determinação de órgão fiscalizador", 3);

    private String descricao;
    private Integer codigo;

    IniciativaCAT(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
