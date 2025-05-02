package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitações")
public class ExecucaoProcessoItem extends SuperEntidade implements Comparable<ExecucaoProcessoItem> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Processo")
    private ExecucaoProcesso execucaoProcesso;

    @ManyToOne
    @Etiqueta("Item Processo de Compra")
    private ItemProcessoDeCompra itemProcessoCompra;

    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public ExecucaoProcessoItem() {
        valorTotal = BigDecimal.ZERO;
        quantidade = BigDecimal.ZERO;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
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
        if (valorTotal != null) {
            return valorTotal.setScale(2, RoundingMode.FLOOR);
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

    public ObjetoCompra getObjetoCompra() {
        try {
            return itemProcessoCompra.getObjetoCompra();
        } catch (Exception e) {
            return null;
        }
    }

    public String getNumeroDescricao() {
        try {
            return itemProcessoCompra.getNumero() + " - " + itemProcessoCompra.getDescricao();
        } catch (Exception e) {
            return null;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        try {
            return itemProcessoCompra.getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraQuantidade();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        try {
            return itemProcessoCompra.getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraValorUnitario();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    @Override
    public String toString() {
        try {
            return itemProcessoCompra.getNumero() + " - " + itemProcessoCompra.getDescricao();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public void calcularValorTotal() {
        setValorTotal(getQuantidade().multiply(getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
    }

    public boolean hasQuantidade() {
        return getQuantidade() != null && getQuantidade().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValor() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(ExecucaoProcessoItem o) {
        try {
            if (o.getItemProcessoCompra() != null && getItemProcessoCompra() != null) {
                return ComparisonChain.start().compare(getItemProcessoCompra().getLoteProcessoDeCompra().getNumero(), o.getItemProcessoCompra().getLoteProcessoDeCompra().getNumero())
                    .compare(getItemProcessoCompra().getNumero(), o.getItemProcessoCompra().getNumero()).result();
            }
            return 0;
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public boolean isExecucaoPorValor() {
        return getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorValor();
    }

    public boolean isExecucaoPorQuantidade() {
        return getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorQuantidade();
    }
}
