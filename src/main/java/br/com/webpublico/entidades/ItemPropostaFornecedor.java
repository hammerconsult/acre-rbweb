/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Proposta Fornecedor")
@Table(name = "ITEMPROPFORNEC")
public class ItemPropostaFornecedor extends SuperEntidade implements Comparable<ItemPropostaFornecedor> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Monetario
    @Etiqueta("Preço")
    private BigDecimal preco;

    @Obrigatorio
    @Etiqueta("Marca")
    private String marca;

    @Obrigatorio
    @Length(maximo = 255)
    @Etiqueta("Modelo")
    private String modelo;

    @Obrigatorio
    @Length(maximo = 3000)
    @Etiqueta("Descrição do Produto")
    private String descricaoProduto;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    private ItemProcessoDeCompra itemProcessoDeCompra;

    @ManyToOne
    @Tabelavel
    private PropostaFornecedor propostaFornecedor;

    @ManyToOne
    @Tabelavel
    private LotePropostaFornecedor lotePropostaFornecedor;

    @Length(maximo = 255)
    @Etiqueta("Justificativa")
    private String justificativa;

    @Etiqueta("Nota Preço")
    private BigDecimal notaPreco;

    @Etiqueta("Nota Classificação Final")
    private BigDecimal notaClassificacaoFinal;

    @Etiqueta("Percentual Desconto (%)")
    private BigDecimal percentualDesconto;

    @Invisivel
    @Transient
    private Boolean selecionado;
    @Invisivel
    @Transient
    private ItemPregao itemPregao; // Utilizado na tela de habilitação do pregão

    public ItemPropostaFornecedor() {
        super();
        preco = BigDecimal.ZERO;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSelecionado() {
        return selecionado == null ? Boolean.FALSE : selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public ItemProcessoDeCompra getItemProcessoDeCompra() {
        return itemProcessoDeCompra;
    }

    public void setItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        this.itemProcessoDeCompra = itemProcessoDeCompra;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPreco() {
        return preco != null ? preco : BigDecimal.ZERO;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public BigDecimal getPrecoTotal() {
        try {
            if (getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorQuantidade()) {
                return getQuantidadeItem().multiply(getPreco()).setScale(2, RoundingMode.HALF_EVEN);
            }
            return getPreco();
        }catch (Exception e){
            return BigDecimal.ZERO;
        }
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public LotePropostaFornecedor getLotePropostaFornecedor() {
        return lotePropostaFornecedor;
    }

    public void setLotePropostaFornecedor(LotePropostaFornecedor lotePropostaFornecedor) {
        this.lotePropostaFornecedor = lotePropostaFornecedor;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    @Override
    public String toString() {
        return itemProcessoDeCompra.toString();
    }

    public ObjetoCompra getObjetoCompra() {
        return this.getItemProcessoDeCompra().getObjetoCompra();
    }

    public boolean isItemServico() {
        try {
            return this.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemSolicitacaoServico() != null;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public Integer getNumeroLote() {
        return this.getLotePropostaFornecedor().getNumeroLote();
    }

    public Integer getNumeroItem() {
        return this.getItemProcessoDeCompra().getNumero();
    }

    public String getDescricaoItem() {
        return this.getItemProcessoDeCompra().getDescricao();
    }

    public String getDescricaoCurtaItem() {
        try {
            return getDescricaoItem().length() > 50 ? getDescricaoItem().substring(0, 50) + "..." : getDescricaoItem();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public Pessoa getFornecedor() {
        return this.getPropostaFornecedor().getFornecedor();
    }

    public BigDecimal getQuantidadeItem() {
        return this.getItemProcessoDeCompra().getQuantidade();
    }

    public boolean isMaterial() {
        return this.getItemProcessoDeCompra().isMaterial();
    }

    public boolean isServico() {
        return this.getItemProcessoDeCompra().isServico();
    }

    public ServicoCompravel getServicoCompravel() {
        return this.getItemProcessoDeCompra().getServicoCompravel();
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

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public BigDecimal getNotaPreco() {
        return notaPreco != null ? notaPreco : BigDecimal.ZERO;
    }

    public void setNotaPreco(BigDecimal notaPreco) {
        this.notaPreco = notaPreco;
    }

    public BigDecimal getNotaClassificacaoFinal() {
        return notaClassificacaoFinal;
    }

    public void setNotaClassificacaoFinal(BigDecimal notaClassificacaoFinal) {
        this.notaClassificacaoFinal = notaClassificacaoFinal;
    }

    public boolean hasPreco() {
        return preco != null && preco.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasPercentualDesconto() {
        return percentualDesconto != null && percentualDesconto.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasDescricaoProduto() {
        return !Strings.isNullOrEmpty(descricaoProduto);
    }

    @Override
    public int compareTo(ItemPropostaFornecedor o) {
        try {
            Licitacao licitacao = getPropostaFornecedor().getLicitacao();
            BigDecimal campoOrdenacao1 = licitacao.getTipoAvaliacao().isMaiorDesconto() ? o.getPercentualDesconto() : o.getPreco();
            BigDecimal campoOrdenacao2 = licitacao.getTipoAvaliacao().isMaiorDesconto() ? getPercentualDesconto() : getPreco();
            return campoOrdenacao2.compareTo(campoOrdenacao1);
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
