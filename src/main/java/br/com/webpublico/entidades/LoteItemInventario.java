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

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 * Representa as quantidades esperada (estado do estoque no início do inventário)
 * e encontrada (constatada durante a realização do inventário) desmembradas para
 * cada lote de determinado Material.
 *
 * @author arthur
 */
public class LoteItemInventario extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Item Inventário Estoque")
    private ItemInventarioEstoque itemInventarioEstoque;

    @ManyToOne
    @Etiqueta("Lote Material")
    private LoteMaterial loteMaterial;

    @Etiqueta("Quantidade Esperada")
    private BigDecimal quantidadeEsperada;

    @Etiqueta("Quantidade Constatada")
    private BigDecimal quantidadeConstatada;

    public LoteItemInventario() {
        quantidadeConstatada = BigDecimal.ZERO;
        quantidadeEsperada = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemInventarioEstoque getItemInventarioEstoque() {
        return itemInventarioEstoque;
    }

    public void setItemInventarioEstoque(ItemInventarioEstoque itemInventarioEstoque) {
        this.itemInventarioEstoque = itemInventarioEstoque;
    }

    public LoteMaterial getLoteMaterial() {
        return loteMaterial;
    }

    public void setLoteMaterial(LoteMaterial loteMaterial) {
        this.loteMaterial = loteMaterial;
    }

    public BigDecimal getQuantidadeConstatada() {
        return quantidadeConstatada;
    }

    public void setQuantidadeConstatada(BigDecimal quantidadeConstatada) {
        this.quantidadeConstatada = quantidadeConstatada;
    }

    public BigDecimal getQuantidadeEsperada() {
        return quantidadeEsperada;
    }

    public void setQuantidadeEsperada(BigDecimal quantidadeEsperada) {
        this.quantidadeEsperada = quantidadeEsperada;
    }

    @Override
    public String toString() {
        return id + " - " + quantidadeEsperada;
    }

}
