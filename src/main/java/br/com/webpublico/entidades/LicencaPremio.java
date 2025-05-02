/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author andre
 *
 * Classe está desativada, a classe que substitui é ConcessaoFeriasLicenca.
 */
@Entity

@Audited
@Etiqueta("Licença Prêmio")
@GrupoDiagrama(nome = "RecursosHumanos")
@Deprecated
public class LicencaPremio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Obrigatorio
    @Pesquisavel
    private String codigo;
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    @Obrigatorio
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início da Vigência")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Base do Período Aquisitivo")
    @Obrigatorio
    @ManyToOne
    private BasePeriodoAquisitivo basePeriodoAquisitivo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Ato Legal")
    @Obrigatorio
    @ManyToOne
    private AtoLegal atoLegal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public BasePeriodoAquisitivo getBasePeriodoAquisitivo() {
        return basePeriodoAquisitivo;
    }

    public void setBasePeriodoAquisitivo(BasePeriodoAquisitivo basePeriodoAquisitivo) {
        this.basePeriodoAquisitivo = basePeriodoAquisitivo;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LicencaPremio)) {
            return false;
        }
        LicencaPremio other = (LicencaPremio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String s = codigo + " - " + descricao + " - Início : " + Util.dateToString(inicioVigencia);

        if (finalVigencia != null) {
            s += " - Final: " + Util.dateToString(finalVigencia);
        }

        return s;
    }
}
