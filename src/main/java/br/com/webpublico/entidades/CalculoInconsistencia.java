/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import java.io.Serializable;
import java.util.Date;

/**
 * @author gustavo
 */
@Entity

@Audited
public class CalculoInconsistencia extends Calculo implements Serializable {

    @ManyToOne
    private ProcessoInconsistencia processoInconsistencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @ManyToOne
    private Tributo tributo;
    @ManyToOne
    private ItemLoteBaixa itemLoteBaixa;

    public CalculoInconsistencia() {
        super.setTipoCalculo(TipoCalculo.INCONSISTENCIA);
    }

    public ProcessoInconsistencia getProcessoInconsistencia() {
        return processoInconsistencia;
    }

    public void setProcessoCalcInconsistencia(ProcessoInconsistencia processoCalcInconsistencia) {
        super.setProcessoCalculo(processoCalcInconsistencia);
        this.processoInconsistencia = processoCalcInconsistencia;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public ItemLoteBaixa getItemLoteBaixa() {
        return itemLoteBaixa;
    }

    public void setItemLoteBaixa(ItemLoteBaixa itemLoteBaixa) {
        this.itemLoteBaixa = itemLoteBaixa;
    }

    public void setProcessoInconsistencia(ProcessoInconsistencia processoInconsistencia) {
        this.processoInconsistencia = processoInconsistencia;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoInconsistencia;
    }

}
