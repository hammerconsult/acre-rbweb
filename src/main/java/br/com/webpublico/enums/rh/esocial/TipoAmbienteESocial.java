package br.com.webpublico.enums.rh.esocial;

public enum TipoAmbienteESocial {

    PRODUCAO_RESTRITA("Produção Restrita", 2),
    PRODUCAO("Produção", 1);

    private String descricao;
    private Integer codigo;

    TipoAmbienteESocial(String descricao, Integer codigo) {
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
