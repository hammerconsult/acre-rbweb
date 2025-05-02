/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class ItemDAM implements Serializable, Comparable<ItemDAM> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DAM DAM;
    @ManyToOne
    private ParcelaValorDivida parcela;
    private BigDecimal valorOriginalDevido;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcaoMonetaria;
    private BigDecimal honorarios;
    private BigDecimal desconto;
    private BigDecimal outrosAcrescimos;
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "itemDAM", cascade= CascadeType.ALL, orphanRemoval=true)
    private List<TributoDAM> tributoDAMs;

    public ItemDAM() {
        criadoEm = System.nanoTime();
        juros = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        correcaoMonetaria = BigDecimal.ZERO;
        desconto = BigDecimal.ZERO;
        outrosAcrescimos = BigDecimal.ZERO;
        valorOriginalDevido = BigDecimal.ZERO;
        tributoDAMs = Lists.newLinkedList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParcelaValorDivida getParcela() {
        return parcela;
    }

    public void setParcela(ParcelaValorDivida parcela) {
        this.parcela = parcela;
    }

    public BigDecimal getJuros() {
        if (juros != null) {
            return juros;
        }
        return BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        if (multa != null) {
            return multa;
        }
        return BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getOutrosAcrescimos() {
        if (outrosAcrescimos != null) {
            return outrosAcrescimos;
        }
        return BigDecimal.ZERO;
    }

    public void setOutrosAcrescimos(BigDecimal outrosAcrescimos) {
        this.outrosAcrescimos = outrosAcrescimos;
    }

    public BigDecimal getValorOriginalDevido() {
        if (valorOriginalDevido != null) {
            return valorOriginalDevido;
        }
        return BigDecimal.ZERO;
    }

    public void setValorOriginalDevido(BigDecimal valorOriginalDevido) {
        this.valorOriginalDevido = valorOriginalDevido;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DAM getDAM() {
        return DAM;
    }

    public void setDAM(DAM dam) {
        this.DAM = dam;
    }

    public BigDecimal getValorTotal() {
        return (getValorOriginalDevido().add(getJuros().add(getMulta().add(getOutrosAcrescimos()).add(getCorrecaoMonetaria()).add(getHonorarios())))).subtract(getDesconto());
    }

    public BigDecimal getCorrecaoMonetaria() {
        if (correcaoMonetaria != null) {
            return correcaoMonetaria;
        }
        return BigDecimal.ZERO;
    }

    public void setCorrecaoMonetaria(BigDecimal correcaoMonetaria) {
        this.correcaoMonetaria = correcaoMonetaria;
    }

    public BigDecimal getDesconto() {
        if (desconto != null) {
            return desconto;
        }
        return BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public List<TributoDAM> getTributoDAMs() {
        return tributoDAMs;
    }

    public void setTributoDAMs(List<TributoDAM> tributoDAMs) {
        this.tributoDAMs = tributoDAMs;
    }

    public BigDecimal getHonorarios() {
        if (honorarios != null) {
            return honorarios;
        }
        return BigDecimal.ZERO;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
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
        return "br.com.webpublico.entidades.novas.ItemDAM[ id=" + id + " ]";
    }

    public BigDecimal getTotalAcrescimos() {
        return getHonorarios().add(getTotalAcrescimosSemHonorarios());
    }

    public BigDecimal getTotalAcrescimosSemHonorarios() {
        return (getJuros().add(getMulta().add(getCorrecaoMonetaria())));
    }

    @Override
    public int compareTo(ItemDAM o) {
        return this.getId().compareTo(o.getId());
    }
}
