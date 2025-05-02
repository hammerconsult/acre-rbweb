package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Audited
@Table(name = "EXECUCAOCONTRATOITEMDOT")
@Etiqueta("Execução Contrato Dotação Item")
public class ExecucaoContratoItemDotacao extends SuperEntidade implements Comparable<ExecucaoContratoItemDotacao> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Contrato Tipo Fonte")
    private ExecucaoContratoTipoFonte execucaoContratoTipoFonte;

    @ManyToOne
    @Etiqueta("Execução contrato item")
    private ExecucaoContratoItem execucaoContratoItem;

    @ManyToOne
    @Etiqueta("Conta de Despesa")
    private Conta contaDespesa;

    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    @Transient
    private BigDecimal quantidadeReservada;
    @Transient
    private BigDecimal valorReservado;

    public ExecucaoContratoItemDotacao() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        quantidadeReservada = BigDecimal.ZERO;
        valorReservado = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContratoTipoFonte getExecucaoContratoTipoFonte() {
        return execucaoContratoTipoFonte;
    }

    public void setExecucaoContratoTipoFonte(ExecucaoContratoTipoFonte execucaoContratoTipoFonte) {
        this.execucaoContratoTipoFonte = execucaoContratoTipoFonte;
    }

    public BigDecimal getQuantidadeReservada() {
        return quantidadeReservada;
    }

    public void setQuantidadeReservada(BigDecimal quantidadeReservada) {
        this.quantidadeReservada = quantidadeReservada;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public ExecucaoContratoItem getExecucaoContratoItem() {
        return execucaoContratoItem;
    }

    public void setExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        this.execucaoContratoItem = execucaoContratoItem;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public BigDecimal getQuantidadeDisponivel() {
        try {
            return getExecucaoContratoItem().getQuantidadeUtilizada().subtract(quantidadeReservada);
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorDisponivel() {
        try {
            return getExecucaoContratoItem().getValorTotal().subtract(valorReservado);
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public void calcularValorTotal() {
        if (hasQuantidade()) {
            BigDecimal total = getQuantidade().multiply(getValorUnitario());
            setValorTotal(total.setScale(2, RoundingMode.HALF_EVEN));
        } else {
            setValorTotal(BigDecimal.ZERO);
        }
    }

    public BigDecimal getQuantidadeCalculada() {
        try {
            return getValorTotal().divide(getValorUnitario(), 4, RoundingMode.HALF_EVEN);
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public Boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public Boolean hasValor() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(ExecucaoContratoItemDotacao o) {
        try {
            return ComparisonChain.start()
                .compare(getExecucaoContratoItem().getItemContrato().getItemAdequado().getNumeroItem(), o.getExecucaoContratoItem().getItemContrato().getItemAdequado().getNumeroItem())
                .result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
