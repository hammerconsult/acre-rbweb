
/*
 * To change this template, choos Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDescontoItemRequisicao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Requisição de Compra")
public class ItemRequisicaoDeCompra extends SuperEntidade implements Comparable<ItemRequisicaoDeCompra> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Integer numero;

    @ManyToOne
    @Etiqueta("Requisição de Compra")
    private RequisicaoDeCompra requisicaoDeCompra;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Etiqueta("Valor Unitário Desdobrado")
    private BigDecimal valorUnitarioDesdobrado;

    @Etiqueta("Percentual de Desconto")
    private BigDecimal percentualDesconto;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @ManyToOne
    @Etiqueta("Item Contrato")
    private ItemContrato itemContrato;

    @ManyToOne
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @ManyToOne
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @ManyToOne
    @Etiqueta("Item Reconhecimento Dívida")
    private ItemReconhecimentoDivida itemReconhecimentoDivida;

    @ManyToOne
    private ExecucaoProcessoItem execucaoProcessoItem;

    @ManyToOne
    @Etiqueta("Derivação Componente")
    private DerivacaoObjetoCompraComponente derivacaoComponente;

    @Lob
    private String descricaoComplementar;

    private Boolean arredondaValorTotal;

    @Enumerated(EnumType.STRING)
    private TipoDescontoItemRequisicao tipoDesconto;

    private BigDecimal valorDescontoUnitario;
    private BigDecimal valorDescontoTotal;

    @Invisivel
    @OneToMany(mappedBy = "itemRequisicaoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRequisicaoCompraExecucao> itensRequisicaoExecucao;

    @Transient
    private BigDecimal quantidadeDisponivel;
    @Transient
    private BigDecimal valorTotalItemEntrada;
    @Transient
    private Material material;

    public ItemRequisicaoDeCompra() {
        super();
        itensRequisicaoExecucao = Lists.newArrayList();
        quantidadeDisponivel = BigDecimal.ZERO;
        quantidade = BigDecimal.ZERO;
        valorTotalItemEntrada = BigDecimal.ZERO;
        arredondaValorTotal = Boolean.FALSE;
        tipoDesconto = TipoDescontoItemRequisicao.NAO_ARREDONDAR;
    }

    public ItemReconhecimentoDivida getItemReconhecimentoDivida() {
        return itemReconhecimentoDivida;
    }

    public void setItemReconhecimentoDivida(ItemReconhecimentoDivida itemReconhecimentoDivida) {
        this.itemReconhecimentoDivida = itemReconhecimentoDivida;
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        return execucaoProcessoItem;
    }

    public void setExecucaoProcessoItem(ExecucaoProcessoItem execucaoProcessoItem) {
        this.execucaoProcessoItem = execucaoProcessoItem;
    }

    public DerivacaoObjetoCompraComponente getDerivacaoComponente() {
        return derivacaoComponente;
    }

    public void setDerivacaoComponente(DerivacaoObjetoCompraComponente derivacaoComponente) {
        this.derivacaoComponente = derivacaoComponente;
    }

    public Boolean isControleQuantidade() {
        if (requisicaoDeCompra.isTipoContrato()) {
            return itemContrato.getControleQuantidade();
        }
        return requisicaoDeCompra.isTipoExecucaoProcesso() && execucaoProcessoItem.isExecucaoPorQuantidade();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal preco) {
        this.valorUnitario = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(BigDecimal quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public BigDecimal getQuantidade() {
        if (quantidade == null){
            return BigDecimal.ZERO;
        }
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public RequisicaoDeCompra getRequisicaoDeCompra() {
        return requisicaoDeCompra;
    }

    public void setRequisicaoDeCompra(RequisicaoDeCompra requisicaoDeCompra) {
        this.requisicaoDeCompra = requisicaoDeCompra;
    }

    public boolean isDerivacaoObjetoCompra() {
        return derivacaoComponente != null;
    }

    @Override
    public String toString() {
        return "Nrº " + numero + " - " + getDescricao();
    }

    public String getDescricao() {
        try {
            return objetoCompra.getDescricao();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }


    public boolean quantidadeDisponivelEhRequisitavel() {
        return !BigDecimal.ZERO.equals(getQuantidadeDisponivel());
    }

    public boolean isItemAjusteValor(){
        return itensRequisicaoExecucao.stream().anyMatch(ItemRequisicaoCompraExecucao::getAjusteValor);
    }

    public BigDecimal getValorTotal() {
        if (valorTotal == null) {
            return BigDecimal.ZERO;
        }
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<ItemRequisicaoCompraExecucao> getItensRequisicaoExecucao() {
        return itensRequisicaoExecucao;
    }

    public void setItensRequisicaoExecucao(List<ItemRequisicaoCompraExecucao> itensDesdobrados) {
        this.itensRequisicaoExecucao = itensDesdobrados;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public Boolean getArredondaValorTotal() {
        return arredondaValorTotal;
    }

    public void setArredondaValorTotal(Boolean arredondaValorTotal) {
        this.arredondaValorTotal = arredondaValorTotal;
    }

    public TipoDescontoItemRequisicao getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDescontoItemRequisicao tipoDescontoValorDesconto) {
        this.tipoDesconto = tipoDescontoValorDesconto;
    }

    public boolean hasItensRequisicaoExecucao() {
        return itensRequisicaoExecucao != null && !itensRequisicaoExecucao.isEmpty();
    }

    public boolean hasMaisDeUmItemRequisicaoExecucao() {
        return hasItensRequisicaoExecucao() && itensRequisicaoExecucao.size() > 1;
    }

    public boolean isNaoArredondarValorDesconto() {
        return tipoDesconto.isNaoArredondar();
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorUnitario() {
        return valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasDiferencaRequisicaoComEntrada() {
        return getValorTotal().compareTo(getValorTotalItemEntrada()) != 0;
    }

    public boolean hasValorUnitarioDesdobrado() {
        return valorUnitarioDesdobrado != null && valorUnitarioDesdobrado.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasPercentualDesconto() {
        return percentualDesconto != null && percentualDesconto.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public BigDecimal getValorUnitarioDesdobrado() {
        return valorUnitarioDesdobrado;
    }

    public void setValorUnitarioDesdobrado(BigDecimal valorUnitarioOriginal) {
        this.valorUnitarioDesdobrado = valorUnitarioOriginal;
    }

    public BigDecimal getValorDescontoUnitario() {
        if (valorDescontoUnitario == null) {
            return BigDecimal.ZERO;
        }
        return valorDescontoUnitario;
    }

    public void setValorDescontoUnitario(BigDecimal valorDesconto) {
        this.valorDescontoUnitario = valorDesconto;
    }

    public BigDecimal getValorDescontoTotal() {
        if (valorDescontoTotal == null) {
            return BigDecimal.ZERO;
        }
        return valorDescontoTotal;
    }

    public void setValorDescontoTotal(BigDecimal valorDescontoTotal) {
        this.valorDescontoTotal = valorDescontoTotal;
    }

    public RoundingMode getRoundigModeValorTotal() {
        try {
            return getArredondaValorTotal() ? RoundingMode.HALF_EVEN : RoundingMode.FLOOR;
        } catch (Exception e) {
            return RoundingMode.HALF_EVEN;
        }
    }

    public RoundingMode getRoundigModeValorUnitario() {
        try {
            return tipoDesconto.isArredondar() ? RoundingMode.HALF_EVEN : RoundingMode.FLOOR;
        } catch (Exception e) {
            return RoundingMode.HALF_EVEN;
        }
    }

    public int getScaleUnitario() {
        try {
            if (requisicaoDeCompra.getTipoObjetoCompra().isMaterialPermanente() && tipoDesconto.isNaoArredondar()) {
                return TipoDescontoItemRequisicao.MATERIAL_PERMANTENTE.getScale();
            }
            return tipoDesconto.getScale();
        } catch (Exception e) {
            return TipoDescontoItemRequisicao.ARREDONDAR.getScale();
        }
    }

    public void calcularValoresItemRequisicaoAndItemExecucao() {
        if (requisicaoDeCompra.isTipoContrato() && !hasMaisDeUmItemRequisicaoExecucao()) {
            ItemRequisicaoCompraExecucao itemReqExecucao = itensRequisicaoExecucao.get(0);
            itemReqExecucao.setQuantidade(hasQuantidade() ? quantidade : BigDecimal.ZERO);
            itemReqExecucao.setValorUnitario(valorUnitario);

            BigDecimal valorTotal = itemReqExecucao.getQuantidade().multiply(itemReqExecucao.getValorUnitario());
            itemReqExecucao.setValorTotal(valorTotal.setScale(2, RoundingMode.HALF_EVEN));
        }
        calcularValorTotal();
    }

    public void calcularValorTotal() {
        setValorTotal(BigDecimal.ZERO);
        if (hasQuantidade() && hasValorUnitario()) {
            BigDecimal valorTotal = quantidade.multiply(valorUnitario);
            setValorTotal(valorTotal.setScale(2, RoundingMode.HALF_EVEN));

            if (requisicaoDeCompra.isLicitacaoMaiorDesconto()) {
                BigDecimal subtract = getValorTotalDesdobrado().subtract(getValorDescontoTotal());
                valorTotal = subtract.setScale(2, getRoundigModeValorTotal());
                setValorTotal(valorTotal);
            }
        }
    }

    public void atribuirValorUnitario() {
        if (hasValorUnitarioDesdobrado()) {
            if (isControleQuantidade()) {
                setValorUnitario(getValorUnitarioDesdobrado());
                return;
            }
            setValorUnitario(getValorUnitarioComDesconto().setScale(getScaleUnitario(), RoundingMode.FLOOR));
        }
    }

    public BigDecimal getValorUnitarioComDesconto() {
        if (hasValorUnitarioDesdobrado()) {
            return getValorUnitarioDesdobrado().subtract(getValorDescontoUnitario());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getQuantidadeDisponivelItemExecucao() {
        BigDecimal quantidade = BigDecimal.ZERO;
        for (ItemRequisicaoCompraExecucao item : itensRequisicaoExecucao) {
            if (!item.getAjusteValor()) {
                quantidade = quantidade.add(item.getQuantidadeDisponivel());
            }
        }
        return quantidade;
    }

    public BigDecimal getValorTotalItemExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoCompraExecucao item : itensRequisicaoExecucao) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getQuantidadeTotalItemExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoCompraExecucao item : itensRequisicaoExecucao) {
            if (!item.getAjusteValor()) {
                total = total.add(item.getQuantidade());
            }
        }
        return total;
    }

    public BigDecimal getValorUnitarioTotalItemExecucao() {
        BigDecimal valorUnitario = BigDecimal.ZERO;
        Map<BigDecimal, ItemRequisicaoCompraExecucao> map = new HashMap<>();
        for (ItemRequisicaoCompraExecucao item : itensRequisicaoExecucao) {
            if (!map.containsKey(item.getValorUnitario())) {
                map.put(item.getValorUnitario(), item);
            }
        }
        for (Map.Entry<BigDecimal, ItemRequisicaoCompraExecucao> entry : map.entrySet()) {
            valorUnitario = valorUnitario.add(entry.getKey());
        }
        return valorUnitario;
    }

    public BigDecimal getValorDescontoUnitarioPorTipoDesconto(TipoDescontoItemRequisicao tipoDesconto) {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            if (tipoDesconto.isNaoArredondar()) {
                return getValorDescontoSemArredondar();
            } else {
                return getValorDescontoSemArredondar().setScale(getScaleUnitario(), getRoundigModeValorUnitario());
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorDescontoTotalPorTipoDesconto(TipoDescontoItemRequisicao tipoDesconto) {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            return getQuantidade().multiply(getValorDescontoUnitarioPorTipoDesconto(tipoDesconto)).setScale(2, getRoundigModeValorUnitario());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorDescontoSemArredondar() {
        BigDecimal valorDesconto = BigDecimal.ZERO;
        if (!isControleQuantidade() && hasValorUnitarioDesdobrado() && hasPercentualDesconto()) {
            valorDesconto = valorUnitarioDesdobrado.multiply(percentualDesconto).divide(new BigDecimal("100"), 10, RoundingMode.FLOOR);
        }
        return valorDesconto;
    }

    public void calcularValorTotalItemEntradaPorCompra() {
        if (hasQuantidade() && hasValorUnitario()) {
            setValorTotalItemEntrada(getQuantidade().multiply(getValorUnitario()).setScale(2, getRoundigModeValorTotal()));
        }
    }

    public BigDecimal getValorTotalDesdobrado() {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            return quantidade.multiply(valorUnitarioDesdobrado).setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPercentualDescontoDaNota() {
        if (hasQuantidade() && hasValorUnitarioDesdobrado()) {
            BigDecimal descontoTotal = getValorDescontoTotal();
            return descontoTotal.multiply(new BigDecimal("100")).divide(getValorTotalDesdobrado(), 10, RoundingMode.FLOOR);
        }
        return BigDecimal.ZERO;
    }

    public Long getIdItemProcesso() {
        if (itemContrato != null) {
            return itemContrato.getId();
        } else if (execucaoProcessoItem != null) {
            return execucaoProcessoItem.getId();
        }
        return itemReconhecimentoDivida.getId();
    }

    public BigDecimal getValorTotalItemEntrada() {
        return valorTotalItemEntrada;
    }

    public void setValorTotalItemEntrada(BigDecimal valorTotalItemEntrada) {
        this.valorTotalItemEntrada = valorTotalItemEntrada;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public int compareTo(ItemRequisicaoDeCompra o) {
        if (getNumero() != null && o.getNumero() != null) {
            return getNumero().compareTo(o.getNumero());
        }
        return 0;
    }
}
