package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 12/08/14
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public enum TipoPublicacao {

    EDITAL("Edital"),
    HOMOLOGACAO("Homologação"),
    ATA_REGISTRO_PRECO("Ata de Registro de Preço"),
    OUTROS("Outros");

    private String descricao;

    private TipoPublicacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
