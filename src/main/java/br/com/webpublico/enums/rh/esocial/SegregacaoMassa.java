package br.com.webpublico.enums.rh.esocial;

public enum SegregacaoMassa {
    SEM_SEGREGACAO_MASSA("Sem segregação da massa", 0),
    FUNDO_CAPITALIZACAO("Fundo em capitalização", 1),
    FUNDO_REPARTICAO("Fundo em repartição", 2),
    MANTIDO_TESOURO("Mantido pelo Tesouro", 3);

    private String descricao;
    private Integer codigo;

    SegregacaoMassa(String descricao, Integer codigo) {
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
