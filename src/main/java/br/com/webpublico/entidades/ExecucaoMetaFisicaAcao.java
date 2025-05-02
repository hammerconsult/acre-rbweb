/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.envers.Audited;

/**
 *
 * @author Edi
 */
@Entity
@Audited
@Table(name = "ExecMetaFisicaAcao")
@Etiqueta("Execução da Meta Física da Ação")
public class ExecucaoMetaFisicaAcao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Registro")
    private Date dataRegistro;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valor Físico")
    private BigDecimal valorFisico;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("LDO")
    private LDO ldo;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Sub-ação")
    private SubAcaoPPA subAcao;

    public ExecucaoMetaFisicaAcao() {
        this.dataRegistro =  new Date();
    }

    public ExecucaoMetaFisicaAcao(Date dataRegistro, BigDecimal valorFisico, LDO ldo, SubAcaoPPA subAcao) {
        this.dataRegistro = dataRegistro;
        this.valorFisico = valorFisico;
        this.ldo = ldo;
        this.subAcao = subAcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public BigDecimal getValorFisico() {
        return valorFisico;
    }

    public void setValorFisico(BigDecimal valorFisico) {
        this.valorFisico = valorFisico;
    }

    public LDO getLdo() {
        return ldo;
    }

    public void setLdo(LDO ldo) {
        this.ldo = ldo;
    }

    public SubAcaoPPA getSubAcao() {
        return subAcao;
    }

    public void setSubAcao(SubAcaoPPA subAcao) {
        this.subAcao = subAcao;
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
        if (!(object instanceof ExecucaoMetaFisicaAcao)) {
            return false;
        }
        ExecucaoMetaFisicaAcao other = (ExecucaoMetaFisicaAcao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
