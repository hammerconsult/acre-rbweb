/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Andre Gustavo
 */
public enum EtapaUploadArquivoRBTrans {

    GERAL("Geral"),
    RECURSO("Recurso"),
    RECURSO_JARI("Recurso Jari"),
    RECURSO_CONSELHO("Recurso Conselho"),
    CANCELAMENTO_MULTA("Cancelamento da Multa");
    private String descricao;

    private EtapaUploadArquivoRBTrans(String descricao) {
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
