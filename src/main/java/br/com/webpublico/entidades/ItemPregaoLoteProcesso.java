/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Pregão Lote Processo")
@Table(name = "ITPRELOTPRO")
public class ItemPregaoLoteProcesso extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Item Pregão")
    private ItemPregao itemPregao;

    @ManyToOne
    @Etiqueta("Lote Processo de Compra")
    private LoteProcessoDeCompra loteProcessoDeCompra;

    @OneToMany(mappedBy = "itemPregaoLoteProcesso", cascade = CascadeType.REMOVE)
    private List<ItemPregaoLoteItemProcesso> itensPregaoLoteItemProcesso;

    public ItemPregaoLoteProcesso(){
        itensPregaoLoteItemProcesso = Lists.newArrayList();
    }

    ItemPregaoLoteProcesso(ItemPregao ip, LoteProcessoDeCompra lpc) {
        super();
        this.itemPregao = ip;
        this.loteProcessoDeCompra = lpc;
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

    public LoteProcessoDeCompra getLoteProcessoDeCompra() {
        return loteProcessoDeCompra;
    }

    public void setLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        this.loteProcessoDeCompra = loteProcessoDeCompra;
    }

    public List<ItemPregaoLoteItemProcesso> getItensPregaoLoteItemProcesso() {
        return itensPregaoLoteItemProcesso;
    }

    public void setItensPregaoLoteItemProcesso(List<ItemPregaoLoteItemProcesso> itensPregaoLoteItemProcesso) {
        this.itensPregaoLoteItemProcesso = itensPregaoLoteItemProcesso;
    }

    public BigDecimal getValor() {
        return this.getLoteProcessoDeCompra().getValor();
    }

    public BigDecimal getPrecoTotalLote() {
        BigDecimal valor = BigDecimal.ZERO;
        try {
            for (ItemPregaoLoteItemProcesso item : getItensPregaoLoteItemProcesso()) {
                valor = valor.add(item.getPrecoTotalItem());
            }
        } catch (NullPointerException np) {
            return BigDecimal.ZERO;
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }
}
