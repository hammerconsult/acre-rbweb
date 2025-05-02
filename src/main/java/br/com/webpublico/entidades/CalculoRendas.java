/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author daniel
 */
@Entity
@Audited
public class CalculoRendas extends Calculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private ProcessoCalculoRendas processoCalculoRendas;
    @ManyToOne
    private ContratoRendasPatrimoniais contrato;

    public CalculoRendas() {
        super.setTipoCalculo(TipoCalculo.RENDAS);
    }

    public ContratoRendasPatrimoniais getContrato() {
        return contrato;
    }

    public void setContrato(ContratoRendasPatrimoniais contrato) {
        this.contrato = contrato;
    }

    public ProcessoCalculoRendas getProcessoCalculoRendas() {
        return processoCalculoRendas;
    }

    public void setProcessoCalculoRendas(ProcessoCalculoRendas processoCalculoRendas) {
        super.setProcessoCalculo(processoCalculoRendas);
        this.processoCalculoRendas = processoCalculoRendas;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoRendas;
    }

}
