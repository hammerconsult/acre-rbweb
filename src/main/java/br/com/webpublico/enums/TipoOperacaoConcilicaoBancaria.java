/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author Renato
 */
public enum TipoOperacaoConcilicaoBancaria implements EnumComDescricao {

    DEBITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO("Débito no razão não considerado no banco", 1),
    CREDITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO("Crédito no razão não considerado no banco", 2),
    DEBITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO("Débito no banco não considerado no razão", 3),
    CREDITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO("Crédito no banco não considerado no razão", 4);

    private String descricao;
    private int numero;

    TipoOperacaoConcilicaoBancaria(String descricao, int numero) {
        this.descricao = descricao;
        this.numero = numero;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public int getNumero() {
        return numero;
    }

    public Boolean isOperacaoCredito() {
        if (TipoOperacaoConcilicaoBancaria.CREDITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO.equals(this)
            || TipoOperacaoConcilicaoBancaria.CREDITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO.equals(this)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Boolean isOperacaoDebito() {
        if (TipoOperacaoConcilicaoBancaria.DEBITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO.equals(this)
            || TipoOperacaoConcilicaoBancaria.DEBITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO.equals(this)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


}
