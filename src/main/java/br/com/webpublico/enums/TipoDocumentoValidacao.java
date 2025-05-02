package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 27/01/15
 * Time: 19:33
 * To change this template use File | Settings | File Templates.
 */
public enum TipoDocumentoValidacao {
    NFA("Nota Fiscal Avulsa"),
    NFSE("Nota Fiscal Eletronica"),
    ALVARA("Alvará"),
    CERTIDAO("Certidão"),
    ITBI("ITBI");
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    private TipoDocumentoValidacao(String descricao) {
        this.descricao = descricao;
    }
}
