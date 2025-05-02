
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Audited
public class TributoDAM implements Serializable, Comparable<TributoDAM> {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valorOriginal;
    private BigDecimal desconto;
    @ManyToOne
    private Tributo tributo;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private ItemDAM itemDAM;

    public TributoDAM() {
    }

    public TributoDAM(Tributo tributo, BigDecimal valorOriginal, BigDecimal desconto, ItemDAM itemDAM) {
        this.tributo = tributo;
        this.valorOriginal = valorOriginal;
        this.desconto = desconto;
        this.itemDAM = itemDAM;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getDesconto() {
        return desconto != null ? desconto : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ItemDAM getItemDAM() {
        return itemDAM;
    }

    public void setItemDAM(ItemDAM itemDAM) {
        this.itemDAM = itemDAM;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.TributoDAM[ id=" + id + " ]";
    }

    public boolean isDividaAtiva() {
        return this.getItemDAM().getParcela().getDividaAtiva();
    }

    public Entidade getEntidade() {
        return this.getItemDAM().getParcela().getValorDivida().getDivida().getEntidade();
    }

    public BigDecimal getValorComDesconto() {
        return valorOriginal.subtract(getDesconto());
    }

    @Override
    public int compareTo(TributoDAM o) {
        return this.getId().compareTo(o.getId());
    }
}
