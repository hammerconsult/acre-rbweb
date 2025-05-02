/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.anotacoes.Tabelavel;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Temporal;

/**
 * @author reidocrime
 */
@Table(name = "VWHIERARQUIADESPESAORC")
@Entity
public class VwHierarquiaDespesaORC implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Tabelavel
    @Column(name = "ID", insertable = false, updatable = false, unique = true)
    private Long id;
    @Tabelavel
    @Column(name = "ORGAO", insertable = false, updatable = false)
    private String orgao;
    @Tabelavel
    @Column(name = "UNIDADE", insertable = false, updatable = false)
    private String unidade;
    @Tabelavel
    @Column(name = "SUBACAO", insertable = false, updatable = false)
    private String subAcao;
    @Tabelavel
    @Column(name = "CONTA", insertable = false, updatable = false)
    private String conta;
    @Tabelavel
    @Column(name = "EXERCICIO", insertable = false, updatable = false)
    private String exercicio;
    @Tabelavel
    @Column(name = "UNIDADE_ID", insertable = false, updatable = false)
    private Long unidadeId;
    @Tabelavel
    @Column(name = "TIPOCONTADESPESA", insertable = false, updatable = false)
    private String tipoContaDespesa;
    @Tabelavel
    @Column(name = "INICIOVIGENCIA", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Column(name = "FIMVIGENCIA", insertable = false, updatable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @Tabelavel
    @Column(name = "CODIGOREDUZIDO", insertable = false, updatable = false)
    private String codigoReduzido;
    @Tabelavel
    @Column(name = "CONTA_ID", insertable = false, updatable = false)
    private Long contaId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getSubAcao() {
        return subAcao;
    }

    public void setSubAcao(String subAcao) {
        this.subAcao = subAcao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(String tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public String getCodigoReduzido() {
        return codigoReduzido;
    }

    public void setCodigoReduzido(String codigoReduzido) {
        this.codigoReduzido = codigoReduzido;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VwHierarquiaDespesaORC)) {
            return false;
        }
        VwHierarquiaDespesaORC other = (VwHierarquiaDespesaORC) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.orgao + " - " + this.unidade + " - " + this.subAcao + " - " + this.conta;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }
}
