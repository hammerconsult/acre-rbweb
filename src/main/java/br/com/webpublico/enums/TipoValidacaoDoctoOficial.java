/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author fabio
 */
public enum TipoValidacaoDoctoOficial {

    SEMVALIDACAO("Sem Validação"),
    CERTIDAONEGATIVA("Certidão Negativa"),
    CERTIDAOPOSITIVA_EFEITONEGATIVA("Certidão Positiva com Efeito de Negativa"),
    CERTIDAOPOSITIVA("Certidão Positiva"),
    CERTIDAO_POSITIVA_BENS_IMOVEIS("Certidão Positiva de Bens Imóveis"),
    CERTIDAO_NEGATIVA_BENS_IMOVEIS("Certidão Negativa de Bens Imóveis");

    private String descricao;

    private TipoValidacaoDoctoOficial(String descricao) {
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

    public boolean isValidacaoDeDebito() {
        return this.equals(CERTIDAONEGATIVA) ||
            this.equals(CERTIDAOPOSITIVA_EFEITONEGATIVA) ||
            this.equals(CERTIDAOPOSITIVA);
    }
}
