/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoReferenciaFP;

import java.math.BigDecimal;

/**
 * Classe utilizada para recuperar os valores de valor e percentual da faixa referencia.
 *
 * @author peixe
 */
public class ReferenciaFPFuncao {

    private Double valor;
    private Double percentual;
    private TipoReferenciaFP tipoReferenciaFP;
    private BigDecimal referenciaAte;

    public ReferenciaFPFuncao() {
    }

    public ReferenciaFPFuncao(Double valor, Double percentual) {
        this.valor = valor;
        this.percentual = percentual;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public TipoReferenciaFP getTipoReferenciaFP() {
        return tipoReferenciaFP;
    }

    public void setTipoReferenciaFP(TipoReferenciaFP tipoReferenciaFP) {
        this.tipoReferenciaFP = tipoReferenciaFP;
    }

    public BigDecimal getReferenciaAte() {
        return referenciaAte;
    }

    public void setReferenciaAte(BigDecimal referenciaAte) {
        this.referenciaAte = referenciaAte;
    }

    @Override
    public String toString() {
        return "ReferenciaFPFuncao{" + "valor=" + valor + ", percentual=" + percentual + ", tipoReferenciaFP=" + tipoReferenciaFP + '}';
    }
}
