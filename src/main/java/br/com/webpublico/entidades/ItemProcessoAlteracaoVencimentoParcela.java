package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Wellington on 07/12/2015.
 */
@Table(name = "ITEMPROCALTERACAOVENCPARC")
@Entity
@Audited
public class ItemProcessoAlteracaoVencimentoParcela extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoAlteracaoVencimentoParcela procAlteracaoVencimentoParc;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    private Date vencimentoAnterior;
    private Date vencimentoModificado;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoAlteracaoVencimentoParcela getProcAlteracaoVencimentoParc() {
        return procAlteracaoVencimentoParc;
    }

    public void setProcAlteracaoVencimentoParc(ProcessoAlteracaoVencimentoParcela procAlteracaoVencimentoParc) {
        this.procAlteracaoVencimentoParc = procAlteracaoVencimentoParc;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }

    public Date getVencimentoAnterior() {
        return vencimentoAnterior;
    }

    public void setVencimentoAnterior(Date vencimentoAnterior) {
        this.vencimentoAnterior = vencimentoAnterior;
    }

    public Date getVencimentoModificado() {
        return vencimentoModificado;
    }

    public void setVencimentoModificado(Date vencimentoModificado) {
        this.vencimentoModificado = vencimentoModificado;
    }
}
