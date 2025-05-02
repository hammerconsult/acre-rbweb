/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Claudio
 */
public enum StatusProcessoFiscalizacao implements EnumComDescricao {

    DIGITADO("Digitado"),
    TERMO_ADVERTENCIA("Termo de Advertência/Notificação"),
    AUTUADO("Autuado"),
    RECURSO_1INSTANCIA("Recurso do Auto de Infração - 1º Instância"),
    RECURSO_2INSTANCIA("Recurso do Auto de Infração - 2º Instância"),
    RECURSO_TERMO_ADVERTENCIA("Recurso do Termo de Advertência"),
    LANCADO_CONTA_CORRENTE("Lançado em Conta Corrente"),
    PAGO("Pago"),
    EMBARGADO("Embargado"),
    ENCERRADO("Encerrado"),
    CANCELADO("Cancelado"),
    SUSPENSO("Suspenso"),
    OUTROS("Outros");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private StatusProcessoFiscalizacao(String descricao) {
        this.descricao = descricao;
    }
}
