/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author boy
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ValorPensaoAlimenticia implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private BeneficioPensaoAlimenticia beneficioPensaoAlimenticia;
    @Etiqueta("Início de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Etiqueta("Valor da Pensão")
    @Monetario
    private BigDecimal valor;
    @Transient
    private Date dataRegistro;
    @ManyToOne
    private BaseFP baseFP;
    private Boolean irrfFicticio;

    public ValorPensaoAlimenticia() {
        dataRegistro = new Date();
        irrfFicticio = true;
    }

    public BeneficioPensaoAlimenticia getBeneficioPensaoAlimenticia() {
        return beneficioPensaoAlimenticia;
    }

    public void setBeneficioPensaoAlimenticia(BeneficioPensaoAlimenticia beneficioPensaoAlimenticia) {
        this.beneficioPensaoAlimenticia = beneficioPensaoAlimenticia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
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

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return getFinalVigencia();
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        setFinalVigencia(data);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIrrfFicticio() {
        return irrfFicticio != null ? irrfFicticio : false;
    }

    public void setIrrfFicticio(Boolean irrfFicticio) {
        this.irrfFicticio = irrfFicticio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ValorPensaoAlimenticia)) {
            return false;
        }
        ValorPensaoAlimenticia other = (ValorPensaoAlimenticia) object;
        if ((this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro)))
            || ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.getId().equals(other.getId())))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ValorPensaoAlimenticia[ id=" + id + " ]";
    }

    public String getDescricao() {
        String dataIni = inicioVigencia != null ? Util.dateToString(inicioVigencia) : " ";
        String dataEnd = finalVigencia != null ? Util.dateToString(finalVigencia) : " ";
        String base = baseFP != null ? baseFP + "" : "Não Disponível";
        String val = "valor: " + (valor != null ? valor + "" : "00");
        return dataIni + " - " + dataEnd + " - " + val + " - " + base;
    }
}
