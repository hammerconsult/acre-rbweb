/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Leonardo
 */
public enum TipoRecursoFiscalizacao {

    TERMO_ADVERTENCIA("Termo de Advertência"),
    AUTOINFRACAO_1INSTANCIA("Auto Infração - 1ª Instância"),
    AUTOINFRACAO_2INSTANCIA("Auto Infração - 2ª Instância");


    private String descricao;

    private TipoRecursoFiscalizacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }


}
