/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Certame Item Processo")
@Table(name = "ITEMCERTAMEITEMPRO")
public class ItemCertameItemProcesso extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ItemCertame itemCertame;
    @OneToOne
    @Tabelavel
    @Etiqueta("Item Proposta Fornecedor ")
    private ItemPropostaFornecedor itemPropostaVencedor;
    @OneToOne
    @Tabelavel
    @Etiqueta("Item Processo de Compra ")
    private ItemProcessoDeCompra itemProcessoDeCompra;

    public ItemCertameItemProcesso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public br.com.webpublico.entidades.ItemCertame getItemCertame() {
        return itemCertame;
    }

    public void setItemCertame(br.com.webpublico.entidades.ItemCertame itemCertame) {
        this.itemCertame = itemCertame;
    }

    public ItemPropostaFornecedor getItemPropostaVencedor() {
        return itemPropostaVencedor;
    }

    public void setItemPropostaVencedor(ItemPropostaFornecedor itemPropostaVencedor) {
        this.itemPropostaVencedor = itemPropostaVencedor;
    }

    public ItemProcessoDeCompra getItemProcessoDeCompra() {
        return itemProcessoDeCompra;
    }

    public void setItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        this.itemProcessoDeCompra = itemProcessoDeCompra;
    }

    public Integer getNumeroLote() {
        try {
            return this.getItemPropostaVencedor().getNumeroLote();
        } catch (NullPointerException npe) {
            return this.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero();
        }
    }

    public Integer getNumeroItem() {
        try {
            return this.getItemPropostaVencedor().getNumeroItem();
        } catch (NullPointerException npe) {
            return this.getItemProcessoDeCompra().getNumero();
        }
    }

    public String getDescricaoItem() {
        try {
            return this.getItemPropostaVencedor().getDescricaoItem();
        } catch (NullPointerException npe) {
            return this.getItemProcessoDeCompra().getDescricao();
        }
    }

    public Pessoa getFornecedor() {
        return this.getItemPropostaVencedor().getFornecedor();
    }

    public String getMarcaItem() {
        return this.getItemPropostaVencedor().getMarca();
    }

    public String getDescricaoProdutoItem() {
        return this.getItemPropostaVencedor().getDescricaoProduto();
    }

    public BigDecimal getQuantidadeItem() {
        return this.getItemPropostaVencedor().getQuantidadeItem();
    }

    public BigDecimal getPrecoItem() {
        return this.getItemPropostaVencedor().getPreco();
    }

    public BigDecimal getValorTotal() {
        return this.getItemPropostaVencedor().getPrecoTotal();
    }

    public boolean isMaterial() {
        return this.getItemPropostaVencedor().isMaterial();
    }

    public boolean isServico() {
        return this.getItemPropostaVencedor().isServico();
    }

    public ObjetoCompra getObjetoCompra() {
        return this.getItemPropostaVencedor().getObjetoCompra();
    }

    public ServicoCompravel getServicoCompravel() {
        return this.getItemPropostaVencedor().getServicoCompravel();
    }

    public void setValorItem(BigDecimal precoProposto) {
        this.getItemPropostaVencedor().setPreco(precoProposto);
    }
}
