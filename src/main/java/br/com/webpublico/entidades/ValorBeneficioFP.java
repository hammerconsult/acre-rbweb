/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Valor Benefício Folha de Pagamento")

public class ValorBeneficioFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Benefício")
    private BeneficioFP beneficioFP;
    @Etiqueta("Início da Vigência")
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final da Vigência")
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @Monetario
    private BigDecimal valor;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public ValorBeneficioFP() {
        dataRegistro = new Date();
    }

    public ValorBeneficioFP(BeneficioFP beneficioFP, Date inicioVigencia, Date finalVigencia, BigDecimal valor) {
        this.beneficioFP = beneficioFP;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.valor = valor;
    }

    public BeneficioFP getBeneficioFP() {
        return beneficioFP;
    }

    public void setBeneficioFP(BeneficioFP beneficioFP) {
        this.beneficioFP = beneficioFP;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValorBeneficioFP other = (ValorBeneficioFP) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    public boolean equalsID(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValorBeneficioFP other = (ValorBeneficioFP) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return beneficioFP + " - " + inicioVigencia + " - " + finalVigencia + " - " + valor;
    }
}
