package br.com.webpublico.enums.rh.esocial;

/**
 * Created by mateus on 18/06/18.
 */
public enum IndicativoAutoria {
    PROPRIO_CONTRIBUINTE(1, "Próprio contribuinte"),
    OUTRA_ENTIDADE_EMPRESA_OU_EMPREGADO(2, "Outra entidade, empresa ou empregado");

    private Integer codigo;
    private String descricao;

    IndicativoAutoria(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
