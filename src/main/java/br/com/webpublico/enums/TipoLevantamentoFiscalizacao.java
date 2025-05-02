/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Claudio
 */
public enum TipoLevantamentoFiscalizacao {

    DENUNCIA("Denúncia"),
    FISCALIZACAO("Fiscalização"),
    LICENCIAMENTO("Licenciamento");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoLevantamentoFiscalizacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

}
