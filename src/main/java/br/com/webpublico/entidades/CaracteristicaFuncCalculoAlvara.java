package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPagamento;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "CARACFUNCCALCALVARA")
public class CaracteristicaFuncCalculoAlvara extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoCalculoAlvara processoCalculoAlvara;
    @ManyToOne
    private CaracFuncionamento caracFuncionamento;
    @Enumerated(EnumType.STRING)
    private TipoPagamento formaPagamento;
    private Long quantidade;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoCalculoAlvara getProcessoCalculoAlvara() {
        return processoCalculoAlvara;
    }

    public void setProcessoCalculoAlvara(ProcessoCalculoAlvara processoCalculoAlvara) {
        this.processoCalculoAlvara = processoCalculoAlvara;
    }

    public CaracFuncionamento getCaracFuncionamento() {
        return caracFuncionamento;
    }

    public void setCaracFuncionamento(CaracFuncionamento caracFuncionamento) {
        this.caracFuncionamento = caracFuncionamento;
    }

    public TipoPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(TipoPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}
