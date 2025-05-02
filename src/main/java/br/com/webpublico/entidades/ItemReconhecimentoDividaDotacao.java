package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Item do Reconhecimento da Dívida - Dotação")
@Table(name = "ITEMRECONHECDIVIDADOTACAO")
public class ItemReconhecimentoDividaDotacao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemReconhecimentoDivida itemReconhecimentoDivida;
    @Transient
    private DespesaORC despesaORC;
    @ManyToOne
    private FonteDespesaORC fonteDespesaORC;
    private BigDecimal valorReservado;

    public ItemReconhecimentoDividaDotacao() {
        super();
        valorReservado = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemReconhecimentoDivida getItemReconhecimentoDivida() {
        return itemReconhecimentoDivida;
    }

    public void setItemReconhecimentoDivida(ItemReconhecimentoDivida itemReconhecimentoDivida) {
        this.itemReconhecimentoDivida = itemReconhecimentoDivida;
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
