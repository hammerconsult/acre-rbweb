package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.interfaces.ItemLicitatorioContrato;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@Audited
@Etiqueta("Itens do Contrato")
public class ItemContrato extends SuperEntidade implements Comparable<ItemContrato> {
    public static final int CEM = 80;
    public static final int ZERO = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "itemContrato", orphanRemoval = true)
    @Etiqueta("Itens Proposta Fornecedor")
    private ItemContratoItemPropostaFornecedor itemContratoItemPropostaFornecedor;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "itemContrato", orphanRemoval = true)
    @Etiqueta("Item Contrato Vigente")
    private ItemContratoVigente itemContratoVigente;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "itemContrato", orphanRemoval = true)
    @Etiqueta("Itens Proposta Fornecedor Dispensa")
    private ItemContratoItemPropostaFornecedorDispensa itemContratoItemPropostaFornecedorDispensa;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "itemContrato", orphanRemoval = true)
    @Etiqueta("Itens Contrato Solicitação Externa")
    private ItemContratoItemSolicitacaoExterno itemContratoItemSolicitacaoExterno;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "itemContrato", orphanRemoval = true)
    @Etiqueta("Itens Contrato Irp")
    private ItemContratoItemIRP itemContratoItemIRP;

    @Invisivel
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "itemContrato", orphanRemoval = true)
    @Etiqueta("Item Contrato Adesão Ata Interna")
    private ItemContratoAdesaoAtaInterna itemContratoAdesaoAtaInt;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "itemContrato", orphanRemoval = true)
    private List<MovimentoItemContrato> movimentosItemContrato;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "itemContrato", orphanRemoval = true)
    private List<SaldoItemContrato> saldosItemContrato;

    @Invisivel
    @Transient
    @Etiqueta("Quantidade em Requisição")
    private BigDecimal quantidadeEmRequisicao;
    @Invisivel
    @Transient
    @Etiqueta("Quantidade Entregue")
    private BigDecimal quantidadeEntregue;
    @Invisivel
    @Transient
    @Etiqueta("Quantidade Outros Contratos")
    private BigDecimal quantidadeEmOutrosContratos;

    //Controle de Valor
    @Invisivel
    @Transient
    @Etiqueta("Valor em Outros Contrato")
    private BigDecimal valorEmOutrosContratos;
    @Invisivel
    @Transient
    @Etiqueta("Valor Licitado")
    private BigDecimal valorLicitadoItem;

    private BigDecimal quantidadeTotalContrato;
    private BigDecimal quantidadeRescisao;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private BigDecimal valorTotalRescisao;
    private String codigoCmed;

    @Enumerated(EnumType.STRING)
    private TipoControleLicitacao tipoControle;

    @Transient
    private Boolean selecionado;
    @Transient
    private Boolean utilizadoProcesso;
    @ManyToOne
    private ObjetoCompra objetoCompraContrato;

    private String descricaoComplementar;

    public ItemContrato() {
        this.movimentosItemContrato = Lists.newArrayList();
        this.saldosItemContrato = Lists.newArrayList();
        this.selecionado = Boolean.FALSE;
        this.utilizadoProcesso = Boolean.FALSE;
        this.quantidadeTotalContrato = BigDecimal.ZERO;
        this.quantidadeRescisao = BigDecimal.ZERO;
        this.valorTotal = BigDecimal.ZERO;
        this.valorTotalRescisao = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ItemContratoVigente getItemContratoVigente() {
        return itemContratoVigente;
    }

    public void setItemContratoVigente(ItemContratoVigente itemContratoVigente) {
        this.itemContratoVigente = itemContratoVigente;
    }

    public ItemContratoItemPropostaFornecedor getItemContratoItemPropostaFornecedor() {
        return itemContratoItemPropostaFornecedor;
    }

    public void setItemContratoItemPropostaFornecedor(ItemContratoItemPropostaFornecedor itemContratoItemPropostaFornecedor) {
        this.itemContratoItemPropostaFornecedor = itemContratoItemPropostaFornecedor;
    }

    public ItemContratoItemPropostaFornecedorDispensa getItemContratoItemPropostaFornecedorDispensa() {
        return itemContratoItemPropostaFornecedorDispensa;
    }

    public void setItemContratoItemPropostaFornecedorDispensa(ItemContratoItemPropostaFornecedorDispensa itemContratoItemPropostaFornecedorDispensa) {
        this.itemContratoItemPropostaFornecedorDispensa = itemContratoItemPropostaFornecedorDispensa;
    }

    public ItemContratoItemSolicitacaoExterno getItemContratoItemSolicitacaoExterno() {
        return itemContratoItemSolicitacaoExterno;
    }

    public void setItemContratoItemSolicitacaoExterno(ItemContratoItemSolicitacaoExterno itemContratoItemSolicitacaoExterno) {
        this.itemContratoItemSolicitacaoExterno = itemContratoItemSolicitacaoExterno;
    }

    public BigDecimal getQuantidadeTotalContrato() {
        if (quantidadeTotalContrato == null) {
            return BigDecimal.ZERO;
        }
        return quantidadeTotalContrato;
    }

    public void setQuantidadeTotalContrato(BigDecimal quantidadeTotalContrato) {
        if (quantidadeTotalContrato == null) {
            this.quantidadeTotalContrato = BigDecimal.ZERO;
            return;
        }
        if (quantidadeTotalContrato.compareTo(BigDecimal.ZERO) < 0) {
            this.quantidadeTotalContrato = BigDecimal.ZERO;
            return;
        }
        this.quantidadeTotalContrato = quantidadeTotalContrato;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public ItemLicitatorioContrato getItemAdequado() {
        if (getItemContratoItemPropostaFornecedor() != null) {
            return getItemContratoItemPropostaFornecedor();
        }
        if (getItemContratoItemPropostaFornecedorDispensa() != null) {
            return getItemContratoItemPropostaFornecedorDispensa();
        }
        if (getItemContratoAdesaoAtaInt() != null) {
            return getItemContratoAdesaoAtaInt();
        }
        if (getItemContratoItemSolicitacaoExterno() != null) {
            return getItemContratoItemSolicitacaoExterno();
        }
        if (getItemContratoItemIRP() != null) {
            return getItemContratoItemIRP();
        }
        if (getItemContratoVigente() != null) {
            return getItemContratoVigente();
        }
        return null;
    }

    public BigDecimal getQuantidadeLicitada() {
        return getItemAdequado().getQuantidadeLicitada();
    }

    public BigDecimal getQuantidadeEmRequisicao() {
        return quantidadeEmRequisicao;
    }

    public void setQuantidadeEmRequisicao(BigDecimal quantidadeEmRequisicao) {
        this.quantidadeEmRequisicao = quantidadeEmRequisicao;
    }

    public BigDecimal getQuantidadeEntregue() {
        return quantidadeEntregue;
    }

    public void setQuantidadeEntregue(BigDecimal quantidadeEntregue) {
        this.quantidadeEntregue = quantidadeEntregue;
    }

    public BigDecimal getValorEmOutrosContratos() {
        if (valorEmOutrosContratos == null) {
            return BigDecimal.ZERO;
        }
        return valorEmOutrosContratos.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setValorEmOutrosContratos(BigDecimal valorEmOutrosContratos) {
        this.valorEmOutrosContratos = valorEmOutrosContratos;
    }

    public BigDecimal getValorLicitadoItem() {
        if (valorLicitadoItem == null) {
            return BigDecimal.ZERO;
        }
        return valorLicitadoItem.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setValorLicitadoItem(BigDecimal valorLicitadoItem) {
        this.valorLicitadoItem = valorLicitadoItem;
    }

    public BigDecimal getQuantidadeDisponivel() {
        try {
            return getQuantidadeLicitada().subtract((quantidadeEmOutrosContratos));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorDisponivel() {
        try {
            return valorLicitadoItem.subtract(valorEmOutrosContratos);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getQuantidadeRescisao() {
        return quantidadeRescisao;
    }

    public void setQuantidadeRescisao(BigDecimal quantidadeRescisao) {
        this.quantidadeRescisao = quantidadeRescisao;
    }

    public BigDecimal getValorTotalRescisao() {
        return valorTotalRescisao;
    }

    public void setValorTotalRescisao(BigDecimal valorTotalRescisao) {
        this.valorTotalRescisao = valorTotalRescisao;
    }

    public String getCodigoCmed() {
        return codigoCmed;
    }

    public void setCodigoCmed(String codigoCemed) {
        this.codigoCmed = codigoCemed;
    }

    @Override
    public String toString() {
        return getItemAdequado().getDescricao();
    }

    public String getDescricaoCurta() {
        if (getItemAdequado().getDescricao().length() > CEM) {
            return getItemAdequado().getDescricao().substring(ZERO, CEM).concat("...");
        }
        return getItemAdequado().getDescricao();
    }

    public String getNumeroDescricao() {
        try {
            return getItemAdequado().getNumeroItem() + " - " + getItemAdequado().getDescricao();
        } catch (NullPointerException e) {
            return getItemAdequado().getDescricao();
        }
    }

    public Integer getNumero() {
        try {
            return getItemAdequado().getNumeroItem();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public BigDecimal getQuantidadeEmOutrosContratos() {
        if (quantidadeEmOutrosContratos == null) {
            return BigDecimal.ZERO;
        }
        return quantidadeEmOutrosContratos;
    }

    public void setQuantidadeEmOutrosContratos(BigDecimal quantidadeEmOutrosContratos) {
        this.quantidadeEmOutrosContratos = quantidadeEmOutrosContratos;
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        if (getContrato().isDeIrp() && getItemContratoItemIRP() != null) {
            return getItemContratoItemIRP().getItemPropostaFornecedor();

        } else if (getContrato().isDeAdesaoAtaRegistroPrecoInterna() && getItemContratoAdesaoAtaInt() != null) {
            return getItemContratoAdesaoAtaInt().getItemPropostaFornecedor();
        }
        return getItemContratoItemPropostaFornecedor().getItemPropostaFornecedor();
    }

    public ItemPropostaFornecedorDispensa getItemPropostaFornecedorDispensa() {
        return getItemContratoItemPropostaFornecedorDispensa().getItemPropostaFornecedorDispensa();
    }

    public ItemSolicitacaoExterno getItemSolicitacaoExterno() {
        return getItemContratoItemSolicitacaoExterno().getItemSolicitacaoExterno();
    }

    public void calcularValorTotalItemContrato() {
        if (hasQuantidadeTotalMaiorQueZero() && hasValorUnitarioMaiorQueZero()) {
            setValorTotal(getQuantidadeTotalContrato().multiply(getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
        } else {
            setValorTotal(BigDecimal.ZERO);
        }
    }

    public boolean hasValorUnitarioMaiorQueZero() {
        return valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasQuantidadeTotalMaiorQueZero() {
        return quantidadeTotalContrato != null && quantidadeTotalContrato.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorTotalMaiorQueZero() {
        return valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public boolean isItemMaiorDescontoApuracaoPorLoteValor() {
        return getContrato().isTipoAvaliacaoMaiorDesconto()
            && getContrato().isTipoApuracaoPorLote()
            && getItemAdequado().getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorValor();
    }

    public Boolean getControleQuantidade() {
        return tipoControle != null && tipoControle.isTipoControlePorQuantidade();
    }

    public Boolean getControleValor() {
        return tipoControle != null && tipoControle.isTipoControlePorValor();
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Boolean getUtilizadoProcesso() {
        return utilizadoProcesso;
    }

    public void setUtilizadoProcesso(Boolean utilizadoProcesso) {
        this.utilizadoProcesso = utilizadoProcesso;
    }

    public ItemContratoItemIRP getItemContratoItemIRP() {
        return itemContratoItemIRP;
    }

    public void setItemContratoItemIRP(ItemContratoItemIRP itemContratoItemIRP) {
        this.itemContratoItemIRP = itemContratoItemIRP;
    }

    public ItemContratoAdesaoAtaInterna getItemContratoAdesaoAtaInt() {
        return itemContratoAdesaoAtaInt;
    }

    public void setItemContratoAdesaoAtaInt(ItemContratoAdesaoAtaInterna itemContratoAdesaoAtaInt) {
        this.itemContratoAdesaoAtaInt = itemContratoAdesaoAtaInt;
    }

    public ObjetoCompra getObjetoCompraContrato() {
        return objetoCompraContrato;
    }

    public void setObjetoCompraContrato(ObjetoCompra objetoCompraContrato) {
        this.objetoCompraContrato = objetoCompraContrato;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    @Override
    public int compareTo(ItemContrato o) {
        if (o.getItemAdequado() != null && getItemAdequado() != null) {
            if (o.getItemAdequado().getNumeroLote() != null && getItemAdequado().getNumeroLote() != null
                && o.getItemAdequado().getNumeroItem() != null && getItemAdequado().getNumeroItem() != null)
                return ComparisonChain.start().compare(getItemAdequado().getNumeroLote(), o.getItemAdequado().getNumeroLote())
                    .compare(getItemAdequado().getNumeroItem(), o.getItemAdequado().getNumeroItem()).result();

            if (o.getItemAdequado().getDescricao() != null && getItemAdequado().getDescricao() != null)
                return ComparisonChain.start().compare(getItemAdequado().getNumeroLote(), o.getItemAdequado().getNumeroLote())
                    .compare(getItemAdequado().getDescricao(), o.getItemAdequado().getDescricao()).result();
        }
        return 0;
    }
}
