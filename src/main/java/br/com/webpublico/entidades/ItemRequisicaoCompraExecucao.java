/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
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

    @ManyToOne
    @Etiqueta("Item Requisição de Compra")
    private ItemRequisicaoDeCompra itemRequisicaoCompra;

    @ManyToOne
    @Etiqueta("Item Execução Contrato")
    private ExecucaoContratoItem execucaoContratoItem;

    @ManyToOne
    @Etiqueta("Item Execução Processo")
    private ExecucaoProcessoItem execucaoProcessoItem;

    @ManyToOne
    @Etiqueta("Item Reconhecimento Dívida")
    private ItemReconhecimentoDivida itemReconhecimentoDivida;

    @ManyToOne
    @Etiqueta("Empenho")
    private Empenho empenho;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    public ItemRequisicaoCompraExecucao() {
        super();
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

    public ItemContrato getItemContrato() {
        return execucaoContratoItem.getItemContrato();
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public ItemReconhecimentoDivida getItemReconhecimentoDivida() {
        return itemReconhecimentoDivida;
    }

    public void setItemReconhecimentoDivida(ItemReconhecimentoDivida itemReconhecimentoDivida) {
        this.itemReconhecimentoDivida = itemReconhecimentoDivida;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public BigDecimal getValorUnitario() {
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidadeRequisitada) {
        this.quantidade = quantidadeRequisitada;
    }

    public BigDecimal getValorTotal() {
        if (valorTotal == null) {
            valorTotal = BigDecimal.ZERO;
        }
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public String toString() {
        return itemRequisicaoCompra.toString();
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


    public String getDescricaoExecucao() {
        try {
            String emp = "Empenho: " + empenho.getNumeroAno();
            if (execucaoContratoItem != null) {
                return "Nº " + execucaoContratoItem.getExecucaoContrato().getNumero() + " - " + emp;
            } else if (execucaoProcessoItem != null) {
                return "Nº " + execucaoProcessoItem.getExecucaoProcesso().getNumero() + " - " + emp;
            }
            return "Nº " + itemReconhecimentoDivida.getReconhecimentoDivida().getNumero() + " - " + emp;
        } catch (NullPointerException e) {
            return "Sem Execução";
        }
    }

    public static ItemRequisicaoCompraExecucao toVO(br.com.webpublico.entidadesauxiliares.ItemRequisicaoCompraExecucaoVO itemReqProcVO, ItemRequisicaoDeCompra itemReq) {
        ItemRequisicaoCompraExecucao novoItemReqProc = new ItemRequisicaoCompraExecucao();
        novoItemReqProc.setItemRequisicaoCompra(itemReq);
        novoItemReqProc.setEmpenho(itemReqProcVO.getRequisicaoEmpenhoVO().getEmpenho());
        novoItemReqProc.setExecucaoContratoItem(itemReqProcVO.getExecucaoContratoItem());
        novoItemReqProc.setExecucaoProcessoItem(itemReqProcVO.getExecucaoProcessoItem());
        novoItemReqProc.setItemReconhecimentoDivida(itemReqProcVO.getItemReconhecimentoDivida());
        novoItemReqProc.setQuantidade(itemReqProcVO.getQuantidade());
        novoItemReqProc.setValorUnitario(itemReqProcVO.getValorUnitario());
        novoItemReqProc.setValorTotal(itemReqProcVO.getValorTotal());
        return novoItemReqProc;
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
