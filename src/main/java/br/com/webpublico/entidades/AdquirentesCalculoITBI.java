package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "ITBI")
@Etiqueta("Adquirentes do Cálculo do ITBI")
public class AdquirentesCalculoITBI implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal percentual;
    @ManyToOne
    private CalculoITBI calculoITBI;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private Pessoa adquirente;

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public AdquirentesCalculoITBI() {
        criadoEm = System.nanoTime();
    }

    public CalculoITBI getCalculoITBI() {
        return calculoITBI;
    }

    public void setCalculoITBI(CalculoITBI calculoITBI) {
        this.calculoITBI = calculoITBI;
    }

    public Pessoa getAdquirente() {
        return adquirente;
    }

    public void setAdquirente(Pessoa adquirente) {
        this.adquirente = adquirente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return adquirente.getNome() + " " + adquirente.getCpf_Cnpj();
    }
}
