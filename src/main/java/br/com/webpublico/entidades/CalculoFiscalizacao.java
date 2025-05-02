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
 * @author fabio
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "Fiscalizacao")
public class CalculoFiscalizacao extends Calculo implements Serializable, CalculoAutoInfracao {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private AutoInfracaoFiscal autoInfracaoFiscal;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoFiscal processoCalculoFiscal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "calculoFiscalizacao", orphanRemoval = true)
    private List<ItensCalculoFiscalizacao> itensCalculoFiscalizacao;

    public CalculoFiscalizacao() {
        itensCalculoFiscalizacao = new ArrayList<>();
        super.setTipoCalculo(TipoCalculo.FISCALIZACAO);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public ProcessoCalculoFiscal getProcessoCalculoFiscal() {
        return processoCalculoFiscal;
    }

    public void setProcessoCalculoFiscal(ProcessoCalculoFiscal processoCalculoFiscal) {
        super.setProcessoCalculo(processoCalculoFiscal);
        this.processoCalculoFiscal = processoCalculoFiscal;
    }

    public List<ItensCalculoFiscalizacao> getItensCalculoFiscalizacao() {
        return itensCalculoFiscalizacao;
    }

    public void setItensCalculoFiscalizacao(List<ItensCalculoFiscalizacao> itensCalculoFiscalizacao) {
        this.itensCalculoFiscalizacao = itensCalculoFiscalizacao;
    }

    public AutoInfracaoFiscal getAutoInfracaoFiscal() {
        return autoInfracaoFiscal;
    }

    public void setAutoInfracaoFiscal(AutoInfracaoFiscal autoInfracaoFiscal) {
        this.autoInfracaoFiscal = autoInfracaoFiscal;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoFiscal;
    }

 }
