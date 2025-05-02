package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@Audited
@Etiqueta("Cálculo de Contratro")
@GrupoDiagrama(nome = "Tributario")
public class CalculoContrato extends Calculo {

    @ManyToOne
    private ProcessoCalculoContrato processoCalculoContrato;

    public CalculoContrato() {
        setTipoCalculo(TipoCalculo.CONTRATO_CONCESSAO);
    }

    public Contrato getContrato(){
        return processoCalculoContrato.getContrato();
    }

    public ProcessoCalculoContrato getProcessoCalculoContrato() {
        return processoCalculoContrato;
    }

    public void setProcessoCalculoContrato(ProcessoCalculoContrato processoCalculoContrato) {
        this.processoCalculoContrato = processoCalculoContrato;
    }
}
