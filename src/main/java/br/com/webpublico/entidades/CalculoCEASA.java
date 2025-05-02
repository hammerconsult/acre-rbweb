/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.CalculoCeasa;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre
 */
@Entity
@Audited
public class CalculoCEASA extends Calculo implements Serializable, CalculoCeasa {

    @ManyToOne
    private ProcessoCalculoCEASA processoCalculoCEASA;
    private static final long serialVersionUID = 1L;
    @ManyToOne
    private ContratoCEASA contrato;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoCEASA", orphanRemoval = true)
    private List<ItensCalculoCEASA> itensCalculoCEASA;

    public CalculoCEASA() {
        itensCalculoCEASA = new ArrayList<ItensCalculoCEASA>();
        super.setTipoCalculo(TipoCalculo.CEASA);
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoCEASA;
    }

    public ContratoCEASA getContrato() {
        return contrato;
    }

    public void setContrato(ContratoCEASA contrato) {
        this.contrato = contrato;
    }

    public ProcessoCalculoCEASA getProcessoCalculoCEASA() {
        return processoCalculoCEASA;
    }

    public void setProcessoCalculoCEASA(ProcessoCalculoCEASA processoCalculoCEASA) {
        super.setProcessoCalculo(processoCalculoCEASA);
        this.processoCalculoCEASA = processoCalculoCEASA;
    }

    public List<ItensCalculoCEASA> getItensCalculoCEASA() {
        return itensCalculoCEASA;
    }

    public void setItensCalculoCEASA(List<ItensCalculoCEASA> itensCalculoCEASA) {
        this.itensCalculoCEASA = itensCalculoCEASA;
    }

}
