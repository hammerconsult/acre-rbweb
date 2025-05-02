package br.com.webpublico.entidades;

import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 27/11/14
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Item Docto Item Entrada")
public class ItemDoctoItemEntrada extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private DoctoFiscalEntradaCompra doctoFiscalEntradaCompra;

    @ManyToOne
    private ItemEntradaMaterial itemEntradaMaterial;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor Liquidado")
    private BigDecimal valorLiquidado;

    @Enumerated(EnumType.STRING)
    private SituacaoDocumentoFiscalEntradaMaterial situacao;

    public ItemDoctoItemEntrada() {
        super();
        valorLiquidado = BigDecimal.ZERO;
        situacao = SituacaoDocumentoFiscalEntradaMaterial.AGUARDANDO_LIQUIDACAO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DoctoFiscalEntradaCompra getDoctoFiscalEntradaCompra() {
        return doctoFiscalEntradaCompra;
    }

    public void setDoctoFiscalEntradaCompra(DoctoFiscalEntradaCompra doctoFiscalEntradaCompra) {
        this.doctoFiscalEntradaCompra = doctoFiscalEntradaCompra;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return getItemEntradaMaterial().getValorUnitario();
    }

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public SituacaoDocumentoFiscalEntradaMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDocumentoFiscalEntradaMaterial situacao) {
        this.situacao = situacao;
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorUnitario() {
        return getValorUnitario() != null && getValorUnitario().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        RoundingMode roundingMode = RoundingMode.HALF_EVEN;
        if (hasQuantidade() && hasValorUnitario()) {
            total = getQuantidade().multiply(getValorUnitario());

            if (getItemEntradaMaterial() != null) {
                ItemRequisicaoDeCompra itemReq = getItemEntradaMaterial().getItemCompraMaterial().getItemRequisicaoDeCompra();
                if (itemReq.hasValorUnitarioDesdobrado()) {
                    roundingMode = itemReq.getRoundigModeValorTotal();
                    BigDecimal valorUnitarioMaiorDesconto = itemReq.isNaoArredondarValorDesconto() ? itemReq.getValorUnitarioComDesconto() : itemReq.getValorUnitario();
                    total = getQuantidade().multiply(valorUnitarioMaiorDesconto);
                }
            }
        }
        return total.setScale(2, roundingMode);
    }

    public String getDescricao() {
        try {
            return this.getItemEntradaMaterial().getDescricao();
        } catch (Exception ex) {
            return "Erro ao recuperar o item entrada material.";
        }
    }
}
