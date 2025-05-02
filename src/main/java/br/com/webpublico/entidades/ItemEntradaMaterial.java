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
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
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
@Etiqueta("Item da Entrada de Materiais")
public class ItemEntradaMaterial extends SuperEntidade implements ItemMovimentoEstoque, Comparable<ItemEntradaMaterial> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private EntradaMaterial entradaMaterial;

    @Etiqueta("Quantidade")
    @Tabelavel
    @Obrigatorio
    private BigDecimal quantidade;

    @Invisivel
    private BigDecimal quantidadeDevolvida;

    @Etiqueta("Valor Unitário")
    @Tabelavel
    @Obrigatorio
    private BigDecimal valorUnitario;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @Obrigatorio
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;

    @Transient
    private LocalEstoque localEstoquePai;

    @ManyToOne
    private LoteMaterial loteMaterial;

    @ManyToOne
    private Material material;

    @OneToOne(mappedBy = "itemEntradaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemCompraMaterial itemCompraMaterial;

    @OneToOne(mappedBy = "itemEntradaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemTransferenciaMaterial itemTransferenciaMaterial;

    @OneToOne(mappedBy = "itemEntradaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemIncorporacaoMaterial itemIncorporacaoMaterial;

    @OneToOne(mappedBy = "itemEntradaMaterial")
    private ItemProduzido itemProduzido;

    @OneToOne(mappedBy = "itemEntradaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemInventarioItemEntrada itemInventarioItemEntrada;

    @OneToOne(mappedBy = "itemEntradaMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemEntradaLevantamento itemEntradaLevantamento;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private MovimentoEstoque movimentoEstoque;

    @Etiqueta("Número do Item")
    private Integer numeroItem;

    public ItemEntradaMaterial() {
        inicializarOsMesmosAtributos();
    }

    public ItemEntradaMaterial(EntradaMaterial entrada) {
        this.setEntradaMaterial(entrada);
        ItemIncorporacaoMaterial novo = new ItemIncorporacaoMaterial();
        novo.setItemEntradaMaterial(this);
        this.setItemIncorporacaoMaterial(novo);
        inicializarOsMesmosAtributos();
    }

    public ItemEntradaMaterial(EntradaMaterial em, ItemRequisicaoDeCompra irc) {
        inicializarOsMesmosAtributos();
        this.entradaMaterial = em;
        this.itemCompraMaterial = new ItemCompraMaterial(irc, this);
        this.quantidade = irc.getQuantidadeDisponivel();
        this.valorUnitario = irc.getValorUnitario();
        this.valorTotal = irc.getValorTotal();
    }

    public ItemEntradaMaterial(EntradaMaterial em, ItemSaidaMaterial ism) {
        inicializarOsMesmosAtributos();
        this.entradaMaterial = em;
        this.itemTransferenciaMaterial = new ItemTransferenciaMaterial(ism, this);
        this.quantidade = ism.getQuantidade();
        this.valorUnitario = ism.getValorUnitario();
        this.localEstoqueOrcamentario.setLocalEstoque(ism.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getRequisicaoMaterial().getLocalEstoqueDestino());
        this.loteMaterial = ism.getLoteMaterial();
        this.setMaterial(ism.getMaterial());
        calcularValorTotal();
    }

    private void inicializarOsMesmosAtributos() {
        this.quantidadeDevolvida = BigDecimal.ZERO;
        this.quantidade = BigDecimal.ZERO;
        this.valorUnitario = BigDecimal.ZERO;
        this.localEstoqueOrcamentario = new LocalEstoqueOrcamentario();
    }

    public EntradaMaterial getEntradaMaterial() {
        return entradaMaterial;
    }

    public void setEntradaMaterial(EntradaMaterial entradaMaterial) {
        this.entradaMaterial = entradaMaterial;
    }

    @Override
    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    public void setLocalEstoqueOrcamentario(LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    @Override
    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        if (valorUnitario == null) {
            return BigDecimal.ZERO;
        }
        return valorUnitario;
    }

    public String getValorUnitarioQuatroCadasDecimais() {
        return Util.formataQuandoDecimal(valorUnitario, TipoMascaraUnidadeMedida.QUATRO_CASAS_DECIMAL);
    }

    public String getQuantidadeFormatada() {
        return Util.formataQuandoDecimal(getQuantidade(), getMaterial().getMascaraQuantidade());
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }


    public ItemCompraMaterial getItemCompraMaterial() {
        return itemCompraMaterial;
    }

    public void setItemCompraMaterial(ItemCompraMaterial itemCompraMaterial) {
        this.itemCompraMaterial = itemCompraMaterial;
    }

    public ItemTransferenciaMaterial getItemTransferenciaMaterial() {
        return itemTransferenciaMaterial;
    }

    public void setItemTransferenciaMaterial(ItemTransferenciaMaterial itemTransferenciaMaterial) {
        this.itemTransferenciaMaterial = itemTransferenciaMaterial;
    }

    public ItemIncorporacaoMaterial getItemIncorporacaoMaterial() {
        return itemIncorporacaoMaterial;
    }

    public void setItemIncorporacaoMaterial(ItemIncorporacaoMaterial itemIncorporacaoMaterial) {
        this.itemIncorporacaoMaterial = itemIncorporacaoMaterial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuantidadeDevolvida() {
        return quantidadeDevolvida;
    }

    public void setQuantidadeDevolvida(BigDecimal quantidadeDevolvida) {
        this.quantidadeDevolvida = quantidadeDevolvida;
    }

    public BigDecimal quantidadeADevolver() {
        return this.getQuantidade().subtract(this.quantidadeDevolvida);
    }

    public MovimentoEstoque getMovimentoEstoque() {
        return movimentoEstoque;
    }

    public void setMovimentoEstoque(MovimentoEstoque movimentoEstoque) {
        this.movimentoEstoque = movimentoEstoque;
    }

    public ItemProduzido getItemProduzido() {
        return itemProduzido;
    }

    public void setItemProduzido(ItemProduzido itemProduzido) {
        this.itemProduzido = itemProduzido;
    }

    public LocalEstoque getLocalEstoquePai() {
        return localEstoquePai;
    }

    public void setLocalEstoquePai(LocalEstoque localEstoquePai) {
        this.localEstoquePai = localEstoquePai;
    }

    public ItemEntradaLevantamento getItemEntradaLevantamento() {
        return itemEntradaLevantamento;
    }

    public void setItemEntradaLevantamento(ItemEntradaLevantamento itemEntradaLevantamento) {
        this.itemEntradaLevantamento = itemEntradaLevantamento;
    }


    @Override
    public TipoBaixaBens getTipoBaixaBens() {
        return null;
    }

    @Override
    public TipoIngresso getTipoIngressoBens() {
        return entradaMaterial.getTipoIngresso();
    }

    @Override
    public Boolean ehEstorno() {
        return false;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public Material getMaterial() {
        if (this.getItemTransferenciaMaterial() != null) {
            return this.getItemTransferenciaMaterial().getItemSaidaMaterial().getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial().getMaterialAprovado();
        }
        if (this.getItemProduzido() != null) {
            return this.getItemProduzido().getMaterial();
        }
        if (this.getItemInventarioItemEntrada() != null) {
            return this.getItemInventarioItemEntrada().getItemInventarioEstoque().getMaterial();
        }
        if (this.getItemIncorporacaoMaterial() != null) {
            return this.getItemIncorporacaoMaterial().getMaterial();
        }
        if (this.getItemEntradaLevantamento() != null) {
            return this.getItemEntradaLevantamento().getItemLevantamentoEstoque().getMaterial();
        }
        return material;
    }

    public ItemInventarioItemEntrada getItemInventarioItemEntrada() {
        return itemInventarioItemEntrada;
    }

    public void setItemInventarioItemEntrada(ItemInventarioItemEntrada itemInventarioItemEntrada) {
        this.itemInventarioItemEntrada = itemInventarioItemEntrada;
    }

    public String getDescricao() {
        String codigoDescricaoMaterial = getMaterial() != null ? getMaterial().getCodigoDescricaoCurta() : "";
        if (getNumeroItem() != null) {
            return getNumeroItem() + " / " + codigoDescricaoMaterial;
        }
        return codigoDescricaoMaterial;
    }

    public boolean itemDevolvidoTotalmente() {
        if (this.quantidadeADevolver().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.getDescricao();
    }

    @Override
    public Long getIdOrigem() {
        return entradaMaterial.getId();
    }

    @Override
    public Date getDataMovimento() {
        if (entradaMaterial.getDataConclusao() != null) {
            return DataUtil.getDataComHoraAtual(entradaMaterial.getDataConclusao());
        }
        return DataUtil.getDataComHoraAtual(entradaMaterial.getDataEntrada());
    }

    @Override
    public BigDecimal getValorTotal() {
        try {
            return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public BigDecimal getQuantidade() {
        return quantidade;
    }

    @Override
    public TipoOperacao getTipoOperacao() {
        return TipoOperacao.CREDITO;
    }

    @Override
    public TipoEstoque getTipoEstoque() {
        return localEstoqueOrcamentario.getLocalEstoque().getTipoEstoque();
    }

    @Override
    public String getDescricaoDaOperacao() {
        String descricaoOperacao = "Entrada de Material";
        try {
            if (this.getItemCompraMaterial() != null) {
                descricaoOperacao = "Entrada por Compra N°: " + this.entradaMaterial.getNumero();
            }
            if (this.getItemTransferenciaMaterial() != null) {
                descricaoOperacao = "Entrada por Transferência N°: " + this.entradaMaterial.getNumero();
            }
            if (this.getItemProduzido() != null) {
                descricaoOperacao = "Entrada por Produção N°: " + this.entradaMaterial.getNumero();
            }
            if (this.getItemInventarioItemEntrada() != null) {
                descricaoOperacao = "Entrada por Inventário N°: " + this.entradaMaterial.getNumero();
            }
            if (this.getItemIncorporacaoMaterial() != null) {
                descricaoOperacao = "Entrada por Incorporação N°: " + this.entradaMaterial.getNumero();
            }
            if (this.getItemEntradaLevantamento() != null) {
                descricaoOperacao = "Entrada por Levantamento N°: " + this.entradaMaterial.getNumero();
            }
            return descricaoOperacao;
        } catch (Exception ex) {
            return descricaoOperacao;
        }
    }

    public void calcularValorTotal() {
        if (getQuantidade() != null && getValorUnitario() != null) {
            BigDecimal valorTotal = getQuantidade().multiply(getValorUnitario());
            this.valorTotal = valorTotal.setScale(2, RoundingMode.HALF_EVEN);
        }
    }

    public Boolean requerLote() {
        return getMaterial() != null && getMaterial().getControleDeLote();
    }

    public ItemSaidaMaterial getItemSaidaMaterial() {
        return itemTransferenciaMaterial.getItemSaidaMaterial();
    }

    public boolean loteVencidoNaData(Date data) {
        return loteMaterial.vencidoNaData(data);
    }

    public boolean localEstoqueFechadoNaData(Date data) {
        return getLocalEstoque().fechadoNaData(data);
    }

    public LocalEstoque getLocalEstoque() {
        return localEstoqueOrcamentario.getLocalEstoque();
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return localEstoqueOrcamentario.getUnidadeOrcamentaria();
    }

    public ObjetoCompra getObjetoCompra() {
        return itemCompraMaterial.getObjetoCompra();
    }

    public Integer getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Integer numeroItem) {
        this.numeroItem = numeroItem;
    }


    @Override
    public int compareTo(ItemEntradaMaterial o) {
        if (getNumeroItem() != null && o.getNumeroItem() != null) {
            return getNumeroItem().compareTo(o.getNumeroItem());
        }
        return 0;
    }

    public String getHistoricoRazaoIncorporacao() {
        try {
            return "Entrada por Incorporação n° " + entradaMaterial.getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO
                + "Item " + getNumeroItem() + " - " + getMaterial().getCodigo() + " - " + getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO
                + entradaMaterial.getHistorico();
        } catch (Exception ex) {
            return "Entrada por Incorporação";
        }
    }

    public String getHistoricoRazaoTransferencia() {
        try {
            return "Entrada por Transferência n° " + entradaMaterial.getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO
                + "Item " + getNumeroItem() + " - " + getMaterial().getCodigo() + " - " + getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO
                + entradaMaterial.getHistorico();
        } catch (Exception ex) {
            return "Entrada por Transferência";
        }
    }
}
