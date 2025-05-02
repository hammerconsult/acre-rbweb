package br.com.webpublico.enums;

/**
 * Created by Popovicz on 04/06/2018.
 */
public enum SituacaoAtoLegal {
    CRIACAO("Criação", 1),
    EXTINCAO("Extinção", 2),
    REESTRUTURACAO("Reestruturação", 3);
    private String descricao;
    private Integer codigo;

    SituacaoAtoLegal(String descricao, Integer codigo) {
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
