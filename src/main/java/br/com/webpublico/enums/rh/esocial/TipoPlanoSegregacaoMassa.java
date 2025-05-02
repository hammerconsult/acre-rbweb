package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 04/12/2018.
 */
public enum TipoPlanoSegregacaoMassa {
    PLANO_PREVIDENCIARIO_OU_UNICO("Plano previdenciário ou único", 1),
    PLANO_FINANCEIRO("Plano financeiro", 2);

    private String descricao;
    private Integer codigo;

    TipoPlanoSegregacaoMassa(String descricao, Integer codigo) {
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
        return codigo + " - " + descricao;
    }
}
