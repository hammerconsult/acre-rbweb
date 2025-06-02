
/*
 * To change this template, choos Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ItemRequisicaoCompraVO;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoDescontoItemRequisicao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

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
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @ManyToOne
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @ManyToOne
    @Etiqueta("Derivação Componente")
    private DerivacaoObjetoCompraComponente derivacaoComponente;

    @Lob
    @Etiqueta("Descrição Complementar")
    private String descricaoComplementar;

    @Etiqueta("Arredondar Valor Total")
    private Boolean arredondaValorTotal;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Desconto")
    private TipoDescontoItemRequisicao tipoDesconto;

    @Etiqueta("Valor Desconto Unitário")
    private BigDecimal valorDescontoUnitario;

    @Etiqueta("Valor Desconto Total")
    private BigDecimal valorDescontoTotal;

    @Etiqueta("Número Item Processo")
    private Integer numeroItemProcesso;

    @Invisivel
    @OneToMany(mappedBy = "itemRequisicaoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRequisicaoCompraExecucao> itensRequisicaoExecucao;

    @Transient
    private BigDecimal quantidadeDisponivel;

    public ItemRequisicaoDeCompra() {
        super();
        itensRequisicaoExecucao = Lists.newArrayList();
        quantidadeDisponivel = BigDecimal.ZERO;
        quantidade = BigDecimal.ZERO;
        arredondaValorTotal = Boolean.FALSE;
        tipoDesconto = TipoDescontoItemRequisicao.NAO_ARREDONDAR;
    }

    public DerivacaoObjetoCompraComponente getDerivacaoComponente() {
        return derivacaoComponente;
    }

    public void setDerivacaoComponente(DerivacaoObjetoCompraComponente derivacaoComponente) {
        this.derivacaoComponente = derivacaoComponente;
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
        if (quantidade == null) {
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
        Optional<ItemRequisicaoCompraExecucao> any = itensRequisicaoExecucao.stream()
            .filter(exec -> exec.getExecucaoContratoItem() != null)
            .findAny();
        return any.map(ItemRequisicaoCompraExecucao::getItemContrato).orElse(null);
    }

    public ExecucaoProcessoItem getExecucaoProcessoItem() {
        Optional<ItemRequisicaoCompraExecucao> any = itensRequisicaoExecucao.stream()
            .filter(exec -> exec.getExecucaoProcessoItem() != null)
            .findAny();
        return any.map(ItemRequisicaoCompraExecucao::getExecucaoProcessoItem).orElse(null);
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

    public boolean hasValorUnitarioDesdobrado() {
        return valorUnitarioDesdobrado != null && valorUnitarioDesdobrado.compareTo(BigDecimal.ZERO) > 0;
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

    public Integer getNumeroItemProcesso() {
        return numeroItemProcesso;
    }

    public void setNumeroItemProcesso(Integer numeroItemProcesso) {
        this.numeroItemProcesso = numeroItemProcesso;
    }

    public RoundingMode getRoundigModeValorTotal() {
        try {
            return getArredondaValorTotal() ? RoundingMode.HALF_EVEN : RoundingMode.FLOOR;
        } catch (Exception e) {
            return RoundingMode.HALF_EVEN;
        }
    }

    public TipoControleLicitacao getTipoControle() {
        if (getItemContrato() != null) {
            return getItemContrato().getTipoControle();
        } else if (getExecucaoProcessoItem() != null) {
            return getExecucaoProcessoItem().getItemProcessoCompra().getTipoControle();
        }
        return TipoControleLicitacao.QUANTIDADE;
    }

    public Boolean isControleQuantidade() {
        return getTipoControle().isTipoControlePorQuantidade();
    }

    public BigDecimal getValorUnitarioComDesconto() {
        if (hasValorUnitarioDesdobrado()) {
            return getValorUnitarioDesdobrado().subtract(getValorDescontoUnitario());
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getQuantidadeTotalItemExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequisicaoCompraExecucao item : itensRequisicaoExecucao) {
            total = total.add(item.getQuantidade());
        }
        return total;
    }

    public static ItemRequisicaoDeCompra toVO(ItemRequisicaoCompraVO itemReqVO) {
        ItemRequisicaoDeCompra novoItemReq = new ItemRequisicaoDeCompra();
        novoItemReq.setId(itemReqVO.getId());
        novoItemReq.setNumero(itemReqVO.getNumero());
        novoItemReq.setNumeroItemProcesso(itemReqVO.getNumeroItemProcesso());
        novoItemReq.setRequisicaoDeCompra(itemReqVO.getRequisicaoDeCompra());
        novoItemReq.setQuantidade(itemReqVO.getQuantidade());
        novoItemReq.setValorUnitario(itemReqVO.getValorUnitario());
        novoItemReq.setValorUnitarioDesdobrado(itemReqVO.getValorUnitarioDesdobrado());
        novoItemReq.setPercentualDesconto(itemReqVO.getPercentualDesconto());
        novoItemReq.setValorTotal(itemReqVO.getValorTotal());
        novoItemReq.setObjetoCompra(itemReqVO.getObjetoCompra());
        novoItemReq.setUnidadeMedida(itemReqVO.getUnidadeMedida());
        novoItemReq.setDerivacaoComponente(itemReqVO.getDerivacaoComponente());
        novoItemReq.setDescricaoComplementar(itemReqVO.getDescricaoComplementar());
        novoItemReq.setArredondaValorTotal(itemReqVO.getArredondaValorTotal());
        novoItemReq.setTipoDesconto(itemReqVO.getTipoDesconto());
        novoItemReq.setValorDescontoUnitario(itemReqVO.getValorDescontoUnitario());
        novoItemReq.setValorDescontoTotal(itemReqVO.getValorDescontoTotal());
        return novoItemReq;
    }

    @Override
    public int compareTo(ItemRequisicaoDeCompra o) {
        if (getNumero() != null && o.getNumero() != null) {
            return getNumero().compareTo(o.getNumero());
        }
        return 0;
    }
}
