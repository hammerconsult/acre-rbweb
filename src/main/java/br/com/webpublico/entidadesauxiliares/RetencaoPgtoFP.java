/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.SubConta;

import java.math.BigDecimal;

/**
 * @author everton
 */
public class RetencaoPgtoFP {
    private Conta contaExtraorcamentaria;
    private SubConta subConta;
    private BigDecimal valor;

    public RetencaoPgtoFP(Conta contaExtraorcamentaria, SubConta subConta, BigDecimal valor) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
        this.subConta = subConta;
        this.valor = valor;
    }

    public Conta getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(Conta contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
