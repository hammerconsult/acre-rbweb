package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * User: Wellington
 * Date: 23/06/17
 */
@Entity
@Audited
@Etiqueta("Redução de Valor do Bem - Residual")
@Table
public class ReducaoValorBemResidual extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LoteReducaoValorBem loteReducaoValorBem;
    @ManyToOne
    private GrupoBem grupoBem;
    @ManyToOne
    private Bem bem;
    private BigDecimal valorOriginal;
    private BigDecimal valorLiquido;

    public ReducaoValorBemResidual() {
        super();
    }

    public ReducaoValorBemResidual(LoteReducaoValorBem loteReducaoValorBem, GrupoBem grupoBem,
                                   Bem bem, BigDecimal valorOriginal, BigDecimal valorLiquido) {
        this();
        this.loteReducaoValorBem = loteReducaoValorBem;
        this.grupoBem = grupoBem;
        this.bem = bem;
        this.valorOriginal = valorOriginal;
        this.valorLiquido = valorLiquido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteReducaoValorBem getLoteReducaoValorBem() {
        return loteReducaoValorBem;
    }

    public void setLoteReducaoValorBem(LoteReducaoValorBem loteReducaoValorBem) {
        this.loteReducaoValorBem = loteReducaoValorBem;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }
}
