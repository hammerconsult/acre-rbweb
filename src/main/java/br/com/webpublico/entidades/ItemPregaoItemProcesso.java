/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Pregão Item Processo")
@Table(name = "ITPREITPRO")
public class ItemPregaoItemProcesso extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ItemPregao itemPregao;

    @ManyToOne
    private ItemProcessoDeCompra itemProcessoDeCompra;

    public ItemPregaoItemProcesso() {
        super();
    }

    ItemPregaoItemProcesso(ItemPregao ip, ItemProcessoDeCompra ipdc) {
        super();
        this.itemPregao = ip;
        this.itemProcessoDeCompra = ipdc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public ItemProcessoDeCompra getItemProcessoDeCompra() {
        return itemProcessoDeCompra;
    }

    public void setItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        this.itemProcessoDeCompra = itemProcessoDeCompra;
    }

    public BigDecimal getQuantidade() {
        return this.getItemProcessoDeCompra().getQuantidade();
    }

    public BigDecimal getValorTotal() {
        try {
            return this.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPrecoTotal();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public boolean isMaterial() {
        return this.getItemProcessoDeCompra().isMaterial();
    }

    public boolean isServico() {
        return this.getItemProcessoDeCompra().isServico();
    }

    public ObjetoCompra getObjetoCompra() {
        return this.getItemProcessoDeCompra().getObjetoCompra();
    }

    public ServicoCompravel getServicoCompravel() {
        return this.getItemProcessoDeCompra().getServicoCompravel();
    }
}
