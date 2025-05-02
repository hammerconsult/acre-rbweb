package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Wellington on 14/10/2015.
 */

@Entity
@Audited
@Table(name = "ITEMSOLICITACAOEXTDOTACAO")
public class ItemSolicitacaoExternoDotacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemSolicitacaoExterno itemSolicitacaoExterno;
    @Transient
    private DespesaORC despesaORC;
    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal valorReservado;

    public ItemSolicitacaoExternoDotacao() {
        super();
        valorReservado = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemSolicitacaoExterno getItemSolicitacaoExterno() {
        return itemSolicitacaoExterno;
    }

    public void setItemSolicitacaoExterno(ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.itemSolicitacaoExterno = itemSolicitacaoExterno;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }
}
