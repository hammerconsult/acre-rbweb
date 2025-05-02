package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 30/07/14
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Item Solicitação Externo")
public class ItemSolicitacaoExterno extends SuperEntidade implements Comparable<ItemSolicitacaoExterno> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Solicitação Material Externo")
    private SolicitacaoMaterialExterno solicitacaoMaterialExterno;

    @Etiqueta("Número")
    private Integer numero;

    @ManyToOne
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @ManyToOne
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @ManyToOne
    @Etiqueta("Item Processo de Compra")
    private ItemProcessoDeCompra itemProcessoCompra;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Transient
    private BigDecimal saldoQuantidadeMaximaAdesao;
    @Transient
    private BigDecimal saldoValorMaximoAdesao;

    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @Etiqueta("Quantidade Licitada")
    private BigDecimal quantidadeLicitada;

    @Etiqueta("Valor Licitado")
    private BigDecimal valorLicitado;

    @Etiqueta("Modelo")
    private String modelo;

    @Etiqueta("Descrição do Produto")
    private String descricaoProduto;

    @OneToMany(mappedBy = "itemSolicitacaoExterno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSolicitacaoExternoDotacao> dotacoes;

    @Etiqueta("Valor da pesquisa de mercado")
    private BigDecimal valorPesquisaMercado;

    @Etiqueta("Fonte da Pesquisa")
    private String fonteDaPesquisa;

    @Lob
    private String descricaoComplementar;

    @Etiqueta("Subtipo de Objeto de Compra")
    @Enumerated(EnumType.STRING)
    private SubTipoObjetoCompra subTipoObjetoCompra;

    @Etiqueta("Tipo de Controle")
    @Enumerated(EnumType.STRING)
    private TipoControleLicitacao tipoControle;

    @Transient
    private List<SubTipoObjetoCompra> subTiposObjetoCompra;

    @Invisivel
    @Transient
    private Boolean selecionado;

    public ItemSolicitacaoExterno() {
        super();
        dotacoes = Lists.newArrayList();
        subTiposObjetoCompra = Lists.newArrayList();
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public List<SubTipoObjetoCompra> getSubTiposObjetoCompra() {
        return subTiposObjetoCompra;
    }

    public void setSubTiposObjetoCompra(List<SubTipoObjetoCompra> subTiposObjetoCompra) {
        this.subTiposObjetoCompra = subTiposObjetoCompra;
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(SubTipoObjetoCompra subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoMaterialExterno getSolicitacaoMaterialExterno() {
        return solicitacaoMaterialExterno;
    }

    public void setSolicitacaoMaterialExterno(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        this.solicitacaoMaterialExterno = solicitacaoMaterialExterno;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }


    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public List<ItemSolicitacaoExternoDotacao> getDotacoes() {
        return dotacoes;
    }

    public void setDotacoes(List<ItemSolicitacaoExternoDotacao> dotacoes) {
        this.dotacoes = dotacoes;
    }

    public BigDecimal getValorPesquisaMercado() {
        return valorPesquisaMercado;
    }

    public void setValorPesquisaMercado(BigDecimal valorPesquisaMercado) {
        this.valorPesquisaMercado = valorPesquisaMercado;
    }

    public String getFonteDaPesquisa() {
        return fonteDaPesquisa;
    }

    public void setFonteDaPesquisa(String fonteDaPesquisa) {
        this.fonteDaPesquisa = fonteDaPesquisa;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
    }

    public String getCodigoDescricaoObjetoCompra(){
        return this.getObjetoCompra().getCodigo() + " - " + this.getObjetoCompra().getDescricao();
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public String getTipoObjetoCompra() {
        try {
            return this.getObjetoCompra().getTipoObjetoCompra().getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getDescricao() {
        try {
            return this.descricaoProduto;
        } catch (Exception ex) {
            return "";
        }
    }

    public String getNumeroDescricaoItem() {
        try {
            return this.numero + " - " + this.descricaoProduto;
        } catch (Exception ex) {
            return "";
        }
    }

    public BigDecimal calcularValorTotal() {
        try {
            return this.getQuantidade().multiply(this.getValorUnitario()).setScale(2,RoundingMode.HALF_EVEN);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorReservado() {
        BigDecimal valorReservado = BigDecimal.ZERO;
        if (getDotacoes() != null) {
            for (ItemSolicitacaoExternoDotacao dotacao : getDotacoes()) {
                valorReservado = valorReservado.add(dotacao.getValorReservado());
            }
        }
        return valorReservado;
    }

    public BigDecimal getValorParaReservar() {
        return getValorTotal().subtract(getValorReservado());
    }

    public boolean isMesmoItemObjetoCompra(ItemSolicitacaoExterno item) {
        if (item.getItemProcessoCompra() != null && getItemProcessoCompra() != null) {
            return item.getItemProcessoCompra().equals(getItemProcessoCompra());
        }
        if (!Strings.isNullOrEmpty(getDescricaoComplementar()) && !Strings.isNullOrEmpty(item.getDescricaoComplementar())) {
            return getObjetoCompra().equals(item.getObjetoCompra()) && getDescricaoComplementar().trim().equals(item.getDescricaoComplementar().trim());
        }
        return getObjetoCompra().equals(item.getObjetoCompra());
    }

    public BigDecimal getQuantidadeLicitada() {
        return quantidadeLicitada;
    }

    public void setQuantidadeLicitada(BigDecimal quantidadeLicitada) {
        this.quantidadeLicitada = quantidadeLicitada;
    }

    public BigDecimal getValorLicitado() {
        return valorLicitado;
    }

    public void setValorLicitado(BigDecimal valorLicitado) {
        this.valorLicitado = valorLicitado;
    }

    public BigDecimal getSaldoQuantidadeMaximaAdesao() {
        return saldoQuantidadeMaximaAdesao;
    }

    public void setSaldoQuantidadeMaximaAdesao(BigDecimal saldoQuantidadeMaximaAdesao) {
        this.saldoQuantidadeMaximaAdesao = saldoQuantidadeMaximaAdesao;
    }

    public BigDecimal getSaldoValorMaximoAdesao() {
        return saldoValorMaximoAdesao;
    }

    public void setSaldoValorMaximoAdesao(BigDecimal saldoValorMaximoAdesao) {
        this.saldoValorMaximoAdesao = saldoValorMaximoAdesao;
    }

    public BigDecimal getQuantidadeMaximaAdesao() {
        if (quantidadeLicitada == null) {
            return BigDecimal.ZERO;
        }
        if (isControleValor()) {
            return BigDecimal.ONE;
        }
        return quantidadeLicitada.divide(new BigDecimal("2"), 2);
    }

    public BigDecimal getValorMaximoAdesao() {
        if (valorLicitado == null) {
            return BigDecimal.ZERO;
        }
        return valorLicitado.divide(new BigDecimal("2"), 2, RoundingMode.HALF_EVEN);
    }

    public void definirTipoControle() {
        if (getSolicitacaoMaterialExterno().getAtaRegistroPreco().getLicitacao().getTipoAvaliacao().isMaiorDesconto()
            && getSolicitacaoMaterialExterno().getAtaRegistroPreco().getLicitacao().getTipoApuracao().isPorLote()
            && getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorValor()) {
            setTipoControle(TipoControleLicitacao.VALOR);
        }
        if (getItemProcessoCompra() != null) {
            setTipoControle(getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle());
        }
    }

    public boolean isControleValor() {
        return TipoControleLicitacao.VALOR.equals(tipoControle);
    }

    public boolean isControleQuantidade() {
        return TipoControleLicitacao.QUANTIDADE.equals(tipoControle);
    }

    public boolean hasMaisDeUmSubTipo() {
        return subTiposObjetoCompra != null && subTiposObjetoCompra.size() > 1;
    }

    @Override
    public String toString() {
        return objetoCompra.toString();
    }

    @Override
    public int compareTo(ItemSolicitacaoExterno o) {
        if (o.getNumero() != null && getNumero() != null) {
            return getNumero().compareTo(o.getNumero());
        }
        return 0;
    }
}
