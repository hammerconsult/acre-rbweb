/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item da Saída de Material")
public class ItemSaidaMaterial extends SuperEntidade implements ItemMovimentoEstoque, Comparable<ItemSaidaMaterial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private SaidaMaterial saidaMaterial;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Invisivel
    private BigDecimal valorUnitarioReajustado;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @ManyToOne
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;

    @ManyToOne
    private LoteMaterial loteMaterial;

    @ManyToOne
    private Material material;

    @OneToOne(mappedBy = "itemSaidaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemRequisicaoSaidaMaterial itemRequisicaoSaidaMaterial;

    @OneToOne(mappedBy = "itemSaidaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemDevolucaoMaterial itemDevolucaoMaterial;

    @OneToOne(mappedBy = "itemSaidaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemSaidaDesincorporacao itemSaidaDesincorporacao;

    @OneToOne(mappedBy = "itemSaidaMaterial")
    private InsumoProducao insumoProducao;

    @OneToOne(mappedBy = "itemSaidaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemInventarioItemSaida itemInventarioItemSaida;

    @OneToOne(mappedBy = "itemSaidaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemSaidaDireta itemSaidaDireta;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MovimentoEstoque movimentoEstoque;

    @Etiqueta("Número do Item")
    private Integer numeroItem;

    public ItemSaidaMaterial() {
        super();
    }

    public ItemSaidaMaterial(SaidaMaterial sm) {
        this.saidaMaterial = sm;
        this.localEstoqueOrcamentario = new LocalEstoqueOrcamentario();
    }

    public ItemSaidaMaterial(SaidaMaterial sm, ItemRequisicaoMaterial irm) {
        this.saidaMaterial = sm;
        this.itemRequisicaoSaidaMaterial = new ItemRequisicaoSaidaMaterial(irm, this);
        this.material = irm.getMaterialAprovado();
    }

    public ItemSaidaMaterial(SaidaMaterial sm, ItemRequisicaoMaterial irm, LocalEstoqueOrcamentario leo, LoteMaterial lote, BigDecimal quantidade) {
        this.saidaMaterial = sm;
        this.itemRequisicaoSaidaMaterial = new ItemRequisicaoSaidaMaterial(irm, this);
        this.localEstoqueOrcamentario = leo;
        this.loteMaterial = lote;
        this.material = irm.getMaterialAprovado();
    }

    public ItemSaidaMaterial(SaidaMaterial sm, ItemEntradaMaterial iem) {
        this.saidaMaterial = sm;
        this.itemDevolucaoMaterial = new ItemDevolucaoMaterial(iem, this);
        this.quantidade = iem.quantidadeADevolver();
        this.localEstoqueOrcamentario = iem.getLocalEstoqueOrcamentario();
        this.loteMaterial = iem.getLoteMaterial();
        this.material = iem.getMaterial();
    }

    public ItemSaidaMaterial(SaidaMaterial sm, ItemSaidaDesincorporacao isi) {
        this.saidaMaterial = sm;
        this.itemSaidaDesincorporacao = isi;
        this.itemSaidaDesincorporacao.setItemSaidaMaterial(this);
        this.localEstoqueOrcamentario = new LocalEstoqueOrcamentario();
        this.material = isi.getMaterial();
    }

    public ItemSaidaMaterial(SaidaMaterial sm, ItemSaidaDireta isd) {
        this.saidaMaterial = sm;
        this.itemSaidaDireta = isd;
        this.itemSaidaDireta.setItemSaidaMaterial(this);
        this.material = isd.getMaterial();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    public void setLocalEstoqueOrcamentario(LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public BigDecimal getQuantidadeTotal() {
        if (saidaMaterial.isSaidaConsumo()) {
            return itemRequisicaoSaidaMaterial.getItemRequisicaoMaterial().getQuantidadeAutorizada();
        }
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public SaidaMaterial getSaidaMaterial() {
        return saidaMaterial;
    }

    public void setSaidaMaterial(SaidaMaterial saidaMaterial) {
        this.saidaMaterial = saidaMaterial;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorUnitarioReajustado() {
        return valorUnitarioReajustado;
    }

    public void setValorUnitarioReajustado(BigDecimal valorUnitarioReajustado) {
        this.valorUnitarioReajustado = valorUnitarioReajustado;
    }

    public ItemRequisicaoSaidaMaterial getItemRequisicaoSaidaMaterial() {
        return itemRequisicaoSaidaMaterial;
    }

    public void setItemRequisicaoSaidaMaterial(ItemRequisicaoSaidaMaterial itemRequisicaoSaidaMaterial) {
        this.itemRequisicaoSaidaMaterial = itemRequisicaoSaidaMaterial;
    }

    public ItemDevolucaoMaterial getItemDevolucaoMaterial() {
        return itemDevolucaoMaterial;
    }

    public void setItemDevolucaoMaterial(ItemDevolucaoMaterial itemDevolucaoMaterial) {
        this.itemDevolucaoMaterial = itemDevolucaoMaterial;
    }

    public ItemSaidaDesincorporacao getItemSaidaDesincorporacao() {
        return itemSaidaDesincorporacao;
    }

    public void setItemSaidaDesincorporacao(ItemSaidaDesincorporacao itemSaidaDesincorporacao) {
        this.itemSaidaDesincorporacao = itemSaidaDesincorporacao;
    }

    public InsumoProducao getInsumoProducao() {
        return insumoProducao;
    }

    public void setInsumoProducao(InsumoProducao insumoProducao) {
        this.insumoProducao = insumoProducao;
    }

    public MovimentoEstoque getMovimentoEstoque() {
        return movimentoEstoque;
    }

    public void setMovimentoEstoque(MovimentoEstoque movimentoEstoque) {
        this.movimentoEstoque = movimentoEstoque;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void calcularValorTotal() {
        if (getQuantidade() != null && getValorUnitario() != null) {
            BigDecimal valorTotal = getQuantidade().multiply(getValorUnitario());
            this.valorTotal = valorTotal.setScale(2, RoundingMode.FLOOR);
        }
    }

    public boolean requerLote() {
        try {
            return getMaterial().getControleDeLote();
        } catch (NullPointerException ex) {
            return false;
        }
    }

    public BigDecimal recuperarQuantidadeItemRequisicaoOuItemEntrada() {
        if (this.saidaMaterial.isSaidaRequisicao()) {
            if (itemRequisicaoSaidaMaterial.getItemRequisicaoMaterial().getQuantidadeAAtender().equals(BigDecimal.ZERO)) {
                return itemRequisicaoSaidaMaterial.getItemRequisicaoMaterial().getQuantidadeAutorizada();
            }
            return itemRequisicaoSaidaMaterial.getItemRequisicaoMaterial().getQuantidadeAAtender();
        } else {
            if (itemDevolucaoMaterial.getItemEntradaMaterial().quantidadeADevolver().equals(BigDecimal.ZERO)) {
                return itemDevolucaoMaterial.getItemEntradaMaterial().getQuantidade();
            }
            return itemDevolucaoMaterial.getItemEntradaMaterial().quantidadeADevolver();
        }
    }

    public String getDescricao() {
        return this.getMaterial() != null ? getMaterial().getDescricao() : "";
    }

    public ItemInventarioItemSaida getItemInventarioItemSaida() {
        return itemInventarioItemSaida;
    }

    public void setItemInventarioItemSaida(ItemInventarioItemSaida itemInventarioItemSaida) {
        this.itemInventarioItemSaida = itemInventarioItemSaida;
    }

    public ItemSaidaDireta getItemSaidaDireta() {
        return itemSaidaDireta;
    }

    public void setItemSaidaDireta(ItemSaidaDireta itemSaidaDireta) {
        this.itemSaidaDireta = itemSaidaDireta;
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoqueOrcamentario.getLocalEstoque();
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return localEstoqueOrcamentario.getUnidadeOrcamentaria();
    }

    @Override
    public TipoBaixaBens getTipoBaixaBens() {
        return saidaMaterial.getTipoBaixaBens();
    }

    @Override
    public TipoIngresso getTipoIngressoBens() {
        return null;
    }

    @Override
    public Boolean ehEstorno() {
        return false;
    }

    public boolean loteVencidoNaDataDaSaida() {
        return DataUtil.dataSemHorario(loteMaterial.getValidade()).compareTo(DataUtil.dataSemHorario(getDataMovimento())) < 0;
    }

    @Override
    public Long getIdOrigem() {
        return saidaMaterial.getId();
    }

    @Override
    public Date getDataMovimento() {
        return DataUtil.getDataComHoraAtual(saidaMaterial.getDataConclusao() != null ? saidaMaterial.getDataConclusao() : saidaMaterial.getDataSaida());
    }

    @Override
    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    @Override
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    @Override
    public TipoEstoque getTipoEstoque() {
        return localEstoqueOrcamentario.getLocalEstoque().getTipoEstoque();
    }

    @Override
    public String getDescricaoDaOperacao() {
        String descricaoOperacao = "Saída de Material";
        try {
            if (this.getItemDevolucaoMaterial() != null) {
                descricaoOperacao = "Saída por Devolução n° " + this.saidaMaterial.getNumero();
            }
            if (this.getItemRequisicaoSaidaMaterial() != null) {
                SaidaRequisicaoMaterial saidaRequisicaoMaterial = (SaidaRequisicaoMaterial) saidaMaterial;
                if (saidaRequisicaoMaterial.getRequisicaoMaterial().isRequisicaoConsumo()) {
                    descricaoOperacao = "Saída por Consumo n° " + this.saidaMaterial.getNumero();
                }
                if (saidaRequisicaoMaterial.getRequisicaoMaterial().isRequisicaoTransferencia()) {
                    descricaoOperacao = "Saída por Transferência n° " + this.saidaMaterial.getNumero();
                }
            }
            if (this.getItemSaidaDesincorporacao() != null) {
                descricaoOperacao = "Saída por Desincorporação n° " + this.saidaMaterial.getNumero();
            }
            if (this.getInsumoProducao() != null) {
                descricaoOperacao = "Saída por Insumo n° " + this.saidaMaterial.getNumero();
            }
            if (this.getItemInventarioItemSaida() != null) {
                descricaoOperacao = "Saída por Inventário n° " + this.saidaMaterial.getNumero();
            }
            if (this.getItemSaidaDireta() != null) {
                descricaoOperacao = "Saída Direta n° " + this.saidaMaterial.getNumero();
            }
            return descricaoOperacao;
        } catch (Exception ex) {
            return descricaoOperacao;
        }
    }

    @Override
    public String toString() {
        try {
            return getMaterial() + " - Quantidade: " + this.quantidade;
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public TipoOperacao getTipoOperacao() {
        return TipoOperacao.DEBITO;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public Material getMaterial() {
        if (this.getItemDevolucaoMaterial() != null) {
            return this.getItemDevolucaoMaterial().getItemEntradaMaterial().getMaterial();
        }
        if (this.getItemRequisicaoSaidaMaterial() != null) {
            return this.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getMaterialAprovado();
        }
        if (this.getItemSaidaDesincorporacao() != null) {
            return this.getItemSaidaDesincorporacao().getMaterial();
        }
        if (this.getInsumoProducao() != null) {
            return this.getInsumoProducao().getMaterial();
        }
        if (this.getItemInventarioItemSaida() != null) {
            return this.getItemInventarioItemSaida().getItemInventarioEstoque().getMaterial();
        }
        if (this.getItemSaidaDireta() != null) {
            return this.getItemSaidaDireta().getMaterial();
        }
        return null;
    }

    @Override
    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }

    @Override
    public int compareTo(ItemSaidaMaterial o) {
        if (getMaterial() != null && o.getMaterial() != null) {
            return StringUtil.removeAcentuacao(getMaterial().getDescricao())
                .compareToIgnoreCase(StringUtil.removeAcentuacao(o.getMaterial().getDescricao()));
        }
        return 0;
    }

    public String getQuantidadeFormatada() {
        return Util.formataQuandoDecimal(quantidade, getMaterial() != null ? getMaterial().getMascaraQuantidade() : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
    }

    public String getQuantidadeAtenderFormatada() {
        return Util.formataQuandoDecimal(getQuantidadeTotal(), getMaterial() != null ? getMaterial().getMascaraQuantidade() : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
    }
}

