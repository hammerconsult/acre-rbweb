package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 31/03/2017.
 */
@Entity
@Audited
public class ProcessoCompensacaoIptuParcela extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoCompensacaoIptuItem processoCompensacaoIptuItem;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoCompensacaoIptuItem getProcessoCompensacaoIptuItem() {
        return processoCompensacaoIptuItem;
    }

    public void setProcessoCompensacaoIptuItem(ProcessoCompensacaoIptuItem processoCompensacaoIptuItem) {
        this.processoCompensacaoIptuItem = processoCompensacaoIptuItem;
    }

    public ParcelaValorDivida getParcelaValorDivida() {
        return parcelaValorDivida;
    }

    public void setParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        this.parcelaValorDivida = parcelaValorDivida;
    }
}
