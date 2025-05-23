package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
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

    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @ManyToOne
    private RequisicaoCompraEstorno requisicaoCompraEstorno;

    @ManyToOne
    @Etiqueta("Item Requisição Execução")
    private ItemRequisicaoCompraExecucao itemRequisicaoCompraExec;

    @Transient
    private BigDecimal quantidadeDisponivel;

    public ItemRequisicaoCompraEstorno() {
        super();
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

    public ItemRequisicaoDeCompra getItemRequisicaoCompra() {
        return itemRequisicaoCompraExec.getItemRequisicaoCompra();
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getQuantidadeRequisitada() {
        return itemRequisicaoCompraExec.getQuantidade();
    }

    public BigDecimal getValorUnitario() {
        return itemRequisicaoCompraExec.getValorUnitario();
    }

    public BigDecimal getValorTotal() {
        if (hasQuantidade()) {
            return quantidade.multiply(getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public int compareTo(ItemRequisicaoCompraEstorno o) {
        try {
            return getItemRequisicaoCompra().getNumero().compareTo(o.getItemRequisicaoCompra().getNumero());
        } catch (Exception e) {
            return 0;
        }
    }
}
