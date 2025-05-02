package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Cacheable;
import org.hibernate.envers.Audited;

@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
@Deprecated
public class Acrescimo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ItemParcelaValorDivida itemParcelaValorDivida;
    private BigDecimal multa;
    private BigDecimal juros;
    private BigDecimal correcaoMonetaria;
    @Temporal(TemporalType.DATE)
    private Date dataBase;
    @Transient
    private Long criadoEm = System.nanoTime();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCorrecaoMonetaria() {
        return correcaoMonetaria;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ItemParcelaValorDivida getItemParcelaValorDivida() {
        return itemParcelaValorDivida;
    }

    public void setItemParcelaValorDivida(ItemParcelaValorDivida itemParcelaValorDivida) {
        this.itemParcelaValorDivida = itemParcelaValorDivida;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public Date getDataBase() {
        return dataBase;
    }

    public void setDataBase(Date dataBase) {
        this.dataBase = dataBase;
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
        return "br.com.webpublico.entidades.Acrescimo[ id=" + id + " ]";
    }
}
