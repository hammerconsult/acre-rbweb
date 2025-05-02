package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/07/14
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "ITEMPROPOSTAFORNEDISP")
@Etiqueta("Item Proposta Fornecedor Dispensa")
public class ItemPropostaFornecedorDispensa extends SuperEntidade implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Lote Proposta Fornecedor Dispensa")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @JoinColumn(name = "LOTEPROPOSTAFORNEDISP_ID")
    private LotePropostaFornecedorDispensa lotePropostaFornecedorDispensa;

    @Etiqueta("Item Processo de Compra")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ItemProcessoDeCompra itemProcessoDeCompra;

    @Etiqueta("Número")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Integer numero;

    @Etiqueta("Quantidade")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private BigDecimal quantidade;

    @Etiqueta("Marca")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String marca;

    @Etiqueta("Modelo")
    @Obrigatorio
    @Pesquisavel
    private String modelo;

    @Etiqueta("Descrição do Produto")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String descricaoProduto;

    @Etiqueta("Preço")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private BigDecimal preco;

    @Transient
    private Boolean selecionado = Boolean.FALSE;

    public ItemPropostaFornecedorDispensa() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LotePropostaFornecedorDispensa getLotePropostaFornecedorDispensa() {
        return lotePropostaFornecedorDispensa;
    }

    public void setLotePropostaFornecedorDispensa(LotePropostaFornecedorDispensa lotePropostaFornecedorDispensa) {
        this.lotePropostaFornecedorDispensa = lotePropostaFornecedorDispensa;
    }

    public ItemProcessoDeCompra getItemProcessoDeCompra() {
        return itemProcessoDeCompra;
    }

    public void setItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        this.itemProcessoDeCompra = itemProcessoDeCompra;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Boolean getSelecionado() {
        return selecionado == null ? Boolean.FALSE : selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public boolean isItemMaterial() {
        return this.getItemProcessoDeCompra().getItemSolicitacaoMaterial().isMaterial();
    }

    public boolean isItemServico() {
        return this.getItemProcessoDeCompra().getItemSolicitacaoMaterial().isServico();
    }

    public boolean isItemComPrecoLancado() {
        return this.getPreco() != null && this.getPreco().compareTo(BigDecimal.ZERO) > 0;
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

    public BigDecimal getPrecoTotal() {
        if (getPreco() != null) {
            if (itemProcessoDeCompra.getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorQuantidade() && getQuantidade() != null) {
                return getQuantidade().multiply(getPreco()).setScale(2, RoundingMode.HALF_EVEN);
            }
            return getPreco();
        }
        return BigDecimal.ZERO;
    }
}
