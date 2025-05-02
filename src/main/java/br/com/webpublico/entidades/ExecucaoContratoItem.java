package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Audited

@Etiqueta("Itens da Execução Contrato")
public class ExecucaoContratoItem extends SuperEntidade implements Comparable<ExecucaoContratoItem> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Contrato")
    private ExecucaoContrato execucaoContrato;

    @ManyToOne
    @Etiqueta("Itens do Contrato")
    private ItemContrato itemContrato;

    @Transient
    @Etiqueta("Quantidade Disponível")
    private BigDecimal quantidadeDisponivel;

    @Transient
    @Etiqueta("Valor Disponível")
    private BigDecimal valorDisponivel;

    private BigDecimal quantidadeUtilizada;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public ExecucaoContratoItem() {
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario != null ? valorUnitario : BigDecimal.ZERO;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        if (valorTotal != null) {
            return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public BigDecimal getQuantidadeUtilizada() {
        return quantidadeUtilizada != null ? quantidadeUtilizada : BigDecimal.ZERO;
    }

    public void setQuantidadeUtilizada(BigDecimal quantidadeUtilizada) {
        this.quantidadeUtilizada = quantidadeUtilizada;
    }

    public BigDecimal getQuantidadeDisponivel() {
        if (quantidadeDisponivel != null && quantidadeDisponivel.compareTo(BigDecimal.ZERO) < 0) {
            quantidadeDisponivel = BigDecimal.ZERO;
        }
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getValorDisponivel() {
        return valorDisponivel != null ? valorDisponivel : BigDecimal.ZERO;
    }

    public void setValorDisponivel(BigDecimal valorDisponivel) {
        this.valorDisponivel = valorDisponivel;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public ObjetoCompra getObjetoCompra() {
        try {
            return itemContrato.getItemAdequado().getObjetoCompra();
        } catch (Exception e) {
            return null;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        try {
            return itemContrato.getItemAdequado().getUnidadeMedida().getMascaraQuantidade();
        } catch (NullPointerException e) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        try {
            return itemContrato.getItemAdequado().getUnidadeMedida().getMascaraValorUnitario();
        } catch (NullPointerException e) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    @Override
    public String toString() {
        return "Execução: " + execucaoContrato + " - " + " Item: " + itemContrato + " Quantidade do Item de Execução: " + quantidadeUtilizada;
    }

    public void calcularValorTotal() {
        if (hasQuantidade()) {
            BigDecimal total = getQuantidadeUtilizada().multiply(getValorUnitario());
            setValorTotal(total.setScale(2, RoundingMode.HALF_EVEN));
        } else {
            setValorTotal(BigDecimal.ZERO);
        }
    }

    public boolean hasQuantidade() {
        return quantidadeUtilizada != null && quantidadeUtilizada.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorTotal() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(ExecucaoContratoItem o) {
        try {
            if (o.getItemContrato().getItemAdequado() != null && getItemContrato().getItemAdequado() != null) {
                return ComparisonChain.start().compare(getItemContrato().getItemAdequado().getNumeroLote(), o.getItemContrato().getItemAdequado().getNumeroLote())
                    .compare(getItemContrato().getItemAdequado().getNumeroItem(), o.getItemContrato().getItemAdequado().getNumeroItem()).result();
            }
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public boolean isExecucaoPorValor() {
        return getItemContrato().getControleValor();
    }

    public boolean isExecucaoPorQuantidade() {
        return getItemContrato().getControleQuantidade();
    }
}
