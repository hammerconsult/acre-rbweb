/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "RBTrans")
@Audited
@Entity

@Etiqueta(value = "Cálculo de Lançamento de Outorga")
public class CalculoLancamentoOutorga extends Calculo{

    @ManyToOne
    private ProcessoCalculoLancamentoOutorga procCalcLancamentoOutorga;
    @ManyToOne
    @OneToOne
    @Etiqueta("Tributo")
    private Tributo tributo;

    public CalculoLancamentoOutorga() {
        super.setTipoCalculo(TipoCalculo.LANCAMENTO_OUTORGA);
    }

    public ProcessoCalculoLancamentoOutorga getProcCalcLancamentoOutorga() {
        return procCalcLancamentoOutorga;
    }

    public void setProcCalcLancamentoOutorga(ProcessoCalculoLancamentoOutorga procCalcLancamentoOutorga) {
        super.setProcessoCalculo(procCalcLancamentoOutorga);
        this.procCalcLancamentoOutorga = procCalcLancamentoOutorga;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return procCalcLancamentoOutorga;
    }

}
