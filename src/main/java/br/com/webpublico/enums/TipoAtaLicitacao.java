package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 03/12/14
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public enum TipoAtaLicitacao {
    NOVO("Novo"),
    RETIFICACAO("Retificação");
    private String descricao;

    TipoAtaLicitacao(String descricao) {
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
