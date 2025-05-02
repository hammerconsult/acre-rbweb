/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Indenizações")
public class ItemBeneficiario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicioVigencia;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date finalVigencia;
    @Monetario
    @Transient
    private BigDecimal valor;
    @ManyToOne
    private ParametroCalcIndenizacao parametroCalcIndenizacao;
    @ManyToOne
    private Beneficiario beneficiario;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Transient
    private Date dataRegistro;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public ParametroCalcIndenizacao getParametroCalcIndenizacao() {
        return parametroCalcIndenizacao;
    }

    public void setParametroCalcIndenizacao(ParametroCalcIndenizacao parametroCalcIndenizacao) {
        this.parametroCalcIndenizacao = parametroCalcIndenizacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Beneficiario getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(Beneficiario beneficiario) {
        this.beneficiario = beneficiario;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemBeneficiario other = (ItemBeneficiario) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    public boolean equalsID(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemBeneficiario)) {
            return false;
        }
        ItemBeneficiario other = (ItemBeneficiario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Início Vigência : " + inicioVigencia + " - Final Vigência : " + finalVigencia
                + " Parâmetro : " + parametroCalcIndenizacao + " - Valor : " + valor;
    }
}
