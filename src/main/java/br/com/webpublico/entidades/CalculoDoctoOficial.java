/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author fabio
 */
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@GrupoDiagrama(nome = "Certidao")
public class CalculoDoctoOficial extends Calculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @ManyToOne
    private SolicitacaoDoctoOficial solicitacaoDoctoOficial;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vencimento;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private ProcessoCalculoDoctoOfc processoCalculoDoctoOfc;
    @ManyToOne
    private Tributo tributo;

    public CalculoDoctoOficial() {
        super.setTipoCalculo(TipoCalculo.DOCTO_OFICIAL);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public SolicitacaoDoctoOficial getSolicitacaoDoctoOficial() {
        return solicitacaoDoctoOficial;
    }

    public void setSolicitacaoDoctoOficial(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        this.solicitacaoDoctoOficial = solicitacaoDoctoOficial;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public ProcessoCalculoDoctoOfc getProcessoCalculoDoctoOfc() {
        return processoCalculoDoctoOfc;
    }

    public void setProcessoCalculoDoctoOfc(ProcessoCalculoDoctoOfc processoCalculoDoctoOfc) {
        super.setProcessoCalculo(processoCalculoDoctoOfc);
        this.processoCalculoDoctoOfc = processoCalculoDoctoOfc;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    @Override
    public ProcessoCalculo getProcessoCalculo() {
        return processoCalculoDoctoOfc;
    }

}
