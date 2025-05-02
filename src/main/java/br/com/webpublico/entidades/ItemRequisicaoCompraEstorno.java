package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Table(name = "ITEMREQUISICAOCOMPRAEST")
public class ItemRequisicaoCompraEstorno extends SuperEntidade implements Comparable<ItemRequisicaoCompraEstorno> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @ManyToOne
    private RequisicaoCompraEstorno requisicaoCompraEstorno;

    @ManyToOne
    @Etiqueta("Item Requisição Execução")
    private ItemRequisicaoCompraExecucao itemRequisicaoCompraExec;

    @ManyToOne
    @Etiqueta("Item Requisição de Compra")
    private ItemRequisicaoDeCompra itemRequisicaoCompra;

    @Transient
    private BigDecimal quantidadeDisponivel;

    public ItemRequisicaoCompraEstorno() {
        super();
    }

    public ItemRequisicaoDeCompra getItemRequisicaoCompra() {
        return itemRequisicaoCompra;
    }

    public void setItemRequisicaoCompra(ItemRequisicaoDeCompra itemRequisicaoCompra) {
        this.itemRequisicaoCompra = itemRequisicaoCompra;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public RequisicaoCompraEstorno getRequisicaoCompraEstorno() {
        return requisicaoCompraEstorno;
    }

    public void setRequisicaoCompraEstorno(RequisicaoCompraEstorno requisicaoCompraEstorno) {
        this.requisicaoCompraEstorno = requisicaoCompraEstorno;
    }

    public ItemRequisicaoCompraExecucao getItemRequisicaoCompraExec() {
        return itemRequisicaoCompraExec;
    }

    public void setItemRequisicaoCompraExec(ItemRequisicaoCompraExecucao itemRequisicaoCompraExec) {
        this.itemRequisicaoCompraExec = itemRequisicaoCompraExec;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getQuantidadeRequisitada() {
        if (isRequisicaoTipoContrato()) {
            return itemRequisicaoCompraExec.getQuantidade();
        }
        return itemRequisicaoCompra.getQuantidade();
    }

    public boolean isRequisicaoTipoContrato() {
        return
            requisicaoCompraEstorno != null
                && requisicaoCompraEstorno.getRequisicaoDeCompra() != null
                && requisicaoCompraEstorno.getRequisicaoDeCompra().isTipoContrato();
    }

    public BigDecimal getValorUnitario() {
        if (itemRequisicaoCompraExec != null) {
            return itemRequisicaoCompraExec.getValorUnitario();
        }
        return itemRequisicaoCompra.getValorUnitario();
    }

    public BigDecimal getValorTotal() {
        return quantidade.multiply(getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public int compareTo(ItemRequisicaoCompraEstorno o) {
        return getItemRequisicaoCompra().getNumero()
            .compareTo(o.getItemRequisicaoCompra().getNumero());
    }
}
