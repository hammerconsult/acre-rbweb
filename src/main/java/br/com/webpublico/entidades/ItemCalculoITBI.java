package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "ITBI")
@Etiqueta("Item do Cálculo do ITBI")
public class ItemCalculoITBI implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CalculoITBI calculoITBI;
    private BigDecimal valorInformado;
    private BigDecimal valorCalculado;
    private BigDecimal percentual;
    @Transient
    private Long criadoEm;

    @ManyToOne
    private TipoNegociacao tipoNegociacao;

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ItemCalculoITBI() {
        criadoEm = System.nanoTime();
        percentual = BigDecimal.valueOf(100);
    }

    public CalculoITBI getCalculoITBI() {
        return calculoITBI;
    }

    public void setCalculoITBI(CalculoITBI calculoITBI) {
        this.calculoITBI = calculoITBI;
    }

    public TipoNegociacao getTipoNegociacao() {
        return tipoNegociacao;
    }

    public void setTipoNegociacao(TipoNegociacao tipoNegociacao) {
        this.tipoNegociacao = tipoNegociacao;
    }

    public BigDecimal getValorCalculado() {
        return valorCalculado;
    }

    public void setValorCalculado(BigDecimal valorCalculado) {
        this.valorCalculado = valorCalculado;
    }

    public BigDecimal getValorInformado() {
        return valorInformado;
    }

    public void setValorInformado(BigDecimal valorInformado) {
        this.valorInformado = valorInformado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPercentual() {
        return percentual != null ? percentual.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(object, this);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemCalculoITBI[ id=" + id + " ]";
    }

}
