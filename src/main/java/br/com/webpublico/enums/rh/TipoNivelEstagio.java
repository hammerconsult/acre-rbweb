package br.com.webpublico.enums.rh;

/**
 * Created by William on 12/11/2018.
 */
public enum TipoNivelEstagio {
    FUNDAMENTAL("Fundamental", 1),
    MEDIO("Médio", 2),
    FORMACAO_PROFISSIONAL("Formação Profissional", 3),
    SUPERIOR("Superior", 4),
    ESPECIAL("Especial", 8),
    MAE_SOCIAL("Mãe social. (Lei 7644, de 1987)", 9);

    private String descricao;
    private Integer codigo;

    TipoNivelEstagio(String descricao, Integer codigo) {
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
        return this.codigo + " - " + this.descricao;
    }
}
