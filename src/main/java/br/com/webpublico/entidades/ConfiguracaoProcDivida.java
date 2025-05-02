/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Configuração de Procuradores da Dívida Pública")
public class ConfiguracaoProcDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Cargo")
    private Cargo cargo;

    public ConfiguracaoProcDivida() {

    }

    public ConfiguracaoProcDivida(Date inicioVigencia, Date fimVigencia, Cargo cargo) {
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        this.cargo = cargo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
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
        if (!(object instanceof ConfiguracaoProcDivida)) {
            return false;
        }
        ConfiguracaoProcDivida other = (ConfiguracaoProcDivida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "" + inicioVigencia;
    }
}
