/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Requisição de Compra Execução")
@Table(name = "ITEMREQUISICAOCOMPRAEXEC")
public class ItemRequisicaoCompraExecucao extends SuperEntidade implements Comparable<ItemRequisicaoCompraExecucao> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Item Requisição de Compra")
    @ManyToOne
    private ItemRequisicaoDeCompra itemRequisicaoCompra;

    @ManyToOne
    @Etiqueta("Item Execução Contrato")
    private ExecucaoContratoItem execucaoContratoItem;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @Transient
    private BigDecimal quantidadeDisponivel;
    @Transient
    private Boolean ajusteValor;

    public ItemRequisicaoCompraExecucao() {
        super();
        ajusteValor = false;
    }

    public ItemRequisicaoDeCompra getItemRequisicaoCompra() {
        return itemRequisicaoCompra;
    }

    public void setItemRequisicaoCompra(ItemRequisicaoDeCompra itemRequisicaoCompra) {
        this.itemRequisicaoCompra = itemRequisicaoCompra;
    }

    public ExecucaoContratoItem getExecucaoContratoItem() {
        return execucaoContratoItem;
    }

    public void setExecucaoContratoItem(ExecucaoContratoItem execucaoContratoItem) {
        this.execucaoContratoItem = execucaoContratoItem;
    }

    public BigDecimal getValorUnitario() {
        if (this.valorUnitario == null) {
            return this.getExecucaoContratoItem().getItemContrato().getValorUnitario();
        }
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidadeRequisitada) {
        this.quantidade = quantidadeRequisitada;
    }

    public BigDecimal getValorTotal() {
        if (valorTotal == null) {
            this.valorTotal = BigDecimal.ZERO;
        }
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Boolean getAjusteValor() {
        return ajusteValor;
    }

    public void setAjusteValor(Boolean ajusteValor) {
        this.ajusteValor = ajusteValor;
    }

    @Override
    public String toString() {
        return itemRequisicaoCompra.toString();
    }

    public boolean quantidadeDisponivelEhRequisitavel() {
        return !BigDecimal.ZERO.equals(execucaoContratoItem.getQuantidadeDisponivel());
    }

    public void calcularValorTotal() {
        if (hasQuantidade() && hasValorUnitario()) {
            setValorTotal(quantidade.multiply(valorUnitario));
        }
    }

    public boolean hasQuantidade() {
        return quantidade != null;
    }

    public boolean hasValorUnitario() {
        return valorUnitario != null;
    }

    @Override
    public int compareTo(ItemRequisicaoCompraExecucao o) {
        try {
            return ComparisonChain.start()
                .compare(getExecucaoContratoItem().getExecucaoContrato().getNumero(), o.getExecucaoContratoItem().getExecucaoContrato().getNumero())
                .result();
        } catch (Exception e) {
            return 0;
        }
    }
}
