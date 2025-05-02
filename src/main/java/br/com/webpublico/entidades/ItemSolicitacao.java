/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item de Solicitação de Material")
public class ItemSolicitacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Tabelavel
    private Integer numero;

    @Tabelavel
    private BigDecimal quantidade;

    @Etiqueta("Preço")
    private BigDecimal preco;

    @Etiqueta("Preço")
    private BigDecimal precoTotal;

    @Tabelavel
    @Etiqueta("Descrição Complementar")
    private String descricaoComplementar;

    @ManyToOne
    @Etiqueta("Lote Solicitação Material")
    private LoteSolicitacaoMaterial loteSolicitacaoMaterial;

    @ManyToOne
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @OneToOne(mappedBy = "itemSolicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemSolicitacaoServico itemSolicitacaoServico;

    @OneToOne
    @Etiqueta("Item Cotação")
    private ItemCotacao itemCotacao;

    private BigDecimal percentualDescontoMinimo;

    @Etiqueta("Objeto de Compra - PCA")
    @ManyToOne
    private PlanoContratacaoAnualObjetoCompra pcaObjetoCompra;

    @ManyToOne
    private ObjetoCompra objetoCompra;

    public ItemSolicitacao() {
        super();
        this.percentualDescontoMinimo = BigDecimal.ZERO;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteSolicitacaoMaterial getLoteSolicitacaoMaterial() {
        return loteSolicitacaoMaterial;
    }

    public void setLoteSolicitacaoMaterial(LoteSolicitacaoMaterial loteSolicitacaoMaterial) {
        this.loteSolicitacaoMaterial = loteSolicitacaoMaterial;
    }

    public String getDescricao() {
        return getObjetoCompra().getDescricao();
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public ItemSolicitacaoServico getItemSolicitacaoServico() {
        return itemSolicitacaoServico;
    }

    public void setItemSolicitacaoServico(ItemSolicitacaoServico itemSolicitacaoServico) {
        this.itemSolicitacaoServico = itemSolicitacaoServico;
    }

    public BigDecimal getPercentualDescontoMinimo() {
        return percentualDescontoMinimo;
    }

    public void setPercentualDescontoMinimo(BigDecimal percentualDescontoMinimo) {
        this.percentualDescontoMinimo = percentualDescontoMinimo;
    }

    public BigDecimal getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(BigDecimal precoTotal) {
        this.precoTotal = precoTotal;
    }

    @Override
    public String toString() {
        return getDescricao();
    }

    public ObjetoCompra getObjetoCompra() {
        return this.objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public ServicoCompravel getServicoCompravel() {
        return this.getItemSolicitacaoServico().getServicoCompravel();
    }

    public boolean isMaterial() {
        return TipoObjetoCompra.getTiposObjetoCompraMaterial().contains(getObjetoCompra().getTipoObjetoCompra());
    }

    public boolean isServico() {
        return TipoObjetoCompra.getTiposObjetoCompraServico().contains(getObjetoCompra().getTipoObjetoCompra());
    }

    public ItemCotacao getItemCotacao() {
        return itemCotacao;
    }

    public void setItemCotacao(ItemCotacao itemCotacao) {
        this.itemCotacao = itemCotacao;
    }

    public PlanoContratacaoAnualObjetoCompra getPcaObjetoCompra() {
        return pcaObjetoCompra;
    }

    public void setPcaObjetoCompra(PlanoContratacaoAnualObjetoCompra pcaObjetoCompra) {
        this.pcaObjetoCompra = pcaObjetoCompra;
    }
}
