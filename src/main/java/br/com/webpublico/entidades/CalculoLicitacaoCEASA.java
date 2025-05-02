/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.CalculoCeasa;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author Andre
 */
@Entity
@Audited
public class CalculoLicitacaoCEASA extends Calculo implements Serializable, CalculoCeasa{
    private static final long serialVersionUID = 1L;
    @ManyToOne
    @JoinColumn(name = "PROCESSOCALCULO_ID")
    private ProcessoCalculoLicitacaoCEASA processoCalculoLicitacaoCEASA;
    @ManyToOne
    private ContratoCEASA contrato;

    public CalculoLicitacaoCEASA() {
        super.setTipoCalculo(TipoCalculo.LICITACAO_CEASA);
    }

    public ContratoCEASA getContrato() {
        return contrato;
    }

    public void setContrato(ContratoCEASA contrato) {
        this.contrato = contrato;
    }

    public ProcessoCalculoLicitacaoCEASA getProcessoCalculoLicitacaoCEASA() {
        return processoCalculoLicitacaoCEASA;
    }

    public void setProcessoCalculoLicitacaoCEASA(ProcessoCalculoLicitacaoCEASA processoCalculoLicitacaoCEASA) {
        super.setProcessoCalculo(processoCalculoLicitacaoCEASA);
        this.processoCalculoLicitacaoCEASA = processoCalculoLicitacaoCEASA;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoLicitacaoCEASA;
    }

}
