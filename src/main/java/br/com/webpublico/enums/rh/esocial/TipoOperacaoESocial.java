package br.com.webpublico.enums.rh.esocial;


public enum TipoOperacaoESocial {
    INCLUSAO("Inclusão"), ALTERACAO("Alteração");

    private String descricao;

    TipoOperacaoESocial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
