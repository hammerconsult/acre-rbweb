/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Leonardo
 */
@Table(name = "VWHIERARQUIAADMINISTRATIVA")
@Entity
public class VwHierarquiaAdministrativa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID", insertable = false, updatable = false, unique = true)
    private Long id;
    @Column(name = "SUPERIOR_ID", insertable = false, updatable = false)
    private Long superiorId;
    @Column(name = "SUBORDINADA_ID", insertable = false, updatable = false)
    private Long subordinadaId;
    @Column(name = "ENTIDADE_ID", insertable = false, updatable = false)
    private Long entidadeId;
    @Column(name = "CODIGO", insertable = false, updatable = false)
    private String codigo;
    @Column(name = "DESCRICAO", insertable = false, updatable = false)
    private String descricao;
    @Column(name = "EXERCICIO_ID", insertable = false, updatable = false)
    private Long exercicioId;
    @Column(name = "NATUREZA", insertable = false, updatable = false)
    private String natureza;
    @Column(name = "ESFERADOPODER", insertable = false, updatable = false)
    private String esferoDoPoder;
    @Column(name = "INICIOVIGENCIA", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Column(name = "FIMVIGENCIA", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @Column(name = "ANO", insertable = false, updatable = false)
    private Integer ano;

    public VwHierarquiaAdministrativa() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(Long superiorId) {
        this.superiorId = superiorId;
    }

    public Long getSubordinadaId() {
        return subordinadaId;
    }

    public void setSubordinadaId(Long subordinadaId) {
        this.subordinadaId = subordinadaId;
    }

    public Long getEntidadeId() {
        return entidadeId;
    }

    public void setEntidadeId(Long entidadeId) {
        this.entidadeId = entidadeId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getExercicioId() {
        return exercicioId;
    }

    public void setExercicioId(Long exercicioId) {
        this.exercicioId = exercicioId;
    }

    public String getNatureza() {
        return natureza;
    }

    public void setNatureza(String natureza) {
        this.natureza = natureza;
    }

    public String getEsferoDoPoder() {
        return esferoDoPoder;
    }

    public void setEsferoDoPoder(String esferoDoPoder) {
        this.esferoDoPoder = esferoDoPoder;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
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
        if (!(object instanceof VwHierarquiaAdministrativa)) {
            return false;
        }
        VwHierarquiaAdministrativa other = (VwHierarquiaAdministrativa) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.VwHierarquiaAdministrativa[ id=" + id + " ]";
    }
}
