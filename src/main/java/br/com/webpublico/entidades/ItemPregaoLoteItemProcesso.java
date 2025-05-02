package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by William on 31/08/2016.
 */
@Audited
@Entity
public class ItemPregaoLoteItemProcesso extends SuperEntidade implements Comparable<ItemPregaoLoteItemProcesso> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Item Pregão Lote Processo")
    private ItemPregaoLoteProcesso itemPregaoLoteProcesso;

    @ManyToOne
    @Etiqueta("Item Processo de Compra")
    private ItemProcessoDeCompra itemProcessoDeCompra;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @Etiqueta("Justificativa")
    private String justificativa;

    public ItemPregaoLoteItemProcesso() {
        super();
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemPregaoLoteProcesso getItemPregaoLoteProcesso() {
        return itemPregaoLoteProcesso;
    }

    public void setItemPregaoLoteProcesso(ItemPregaoLoteProcesso itemPregaoLoteProcesso) {
        this.itemPregaoLoteProcesso = itemPregaoLoteProcesso;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public ItemProcessoDeCompra getItemProcessoDeCompra() {
        return itemProcessoDeCompra;
    }

    public void setItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        this.itemProcessoDeCompra = itemProcessoDeCompra;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getPrecoTotalItem() {
        try {
            BigDecimal precoTotal = getValor();
            if (getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle().isTipoControlePorQuantidade()) {
                precoTotal = getItemProcessoDeCompra().getQuantidade().multiply(getValor());
            }
            return precoTotal.setScale(2, RoundingMode.HALF_EVEN);
        } catch (NullPointerException np) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public int compareTo(ItemPregaoLoteItemProcesso o) {
        if (this.itemProcessoDeCompra != null && o.getItemProcessoDeCompra() != null) {
            return this.itemProcessoDeCompra.getNumero().compareTo(o.getItemProcessoDeCompra().getNumero());
        }
        return 0;
    }
}
