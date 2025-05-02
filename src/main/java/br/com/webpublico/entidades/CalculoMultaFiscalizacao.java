/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.CalculoAutoInfracao;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "Fiscalizacao")
public class CalculoMultaFiscalizacao extends Calculo implements Serializable, CalculoAutoInfracao {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private AutoInfracaoFiscal autoInfracaoFiscal;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private ProcessoCalculoMultaFiscal processoCalculoMultaFiscal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoMultaFiscalizacao", orphanRemoval = true)
    private List<ItemCalculoMultaFiscal> itemCalculoMultaFiscal;

    public CalculoMultaFiscalizacao() {
        super.setTipoCalculo(TipoCalculo.MULTA_FISCALIZACAO);
        this.itemCalculoMultaFiscal = new ArrayList<>();
    }

    public AutoInfracaoFiscal getAutoInfracaoFiscal() {
        return autoInfracaoFiscal;
    }

    public void setAutoInfracaoFiscal(AutoInfracaoFiscal autoInfracaoFiscal) {
        this.autoInfracaoFiscal = autoInfracaoFiscal;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ItemCalculoMultaFiscal> getItemCalculoMultaFiscal() {
        return itemCalculoMultaFiscal;
    }

    public void setItemCalculoMultaFiscal(List<ItemCalculoMultaFiscal> itemCalculoMultaFiscal) {
        this.itemCalculoMultaFiscal = itemCalculoMultaFiscal;
    }

    public ProcessoCalculoMultaFiscal getProcessoCalculoMultaFiscal() {
        return processoCalculoMultaFiscal;
    }

    public void setProcessoCalculoMultaFiscal(ProcessoCalculoMultaFiscal processoCalculoMultaFiscal) {
        super.setProcessoCalculo(processoCalculoMultaFiscal);
        this.processoCalculoMultaFiscal = processoCalculoMultaFiscal;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoMultaFiscal;
    }

}
