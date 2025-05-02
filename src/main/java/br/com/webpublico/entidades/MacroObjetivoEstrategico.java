/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Descreve um objetivo macro do planejamento estratégico do poder público.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Etiqueta("Programa do PPA")
@Audited
public class MacroObjetivoEstrategico extends SuperEntidade implements Serializable  {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Descrição")
    @Obrigatorio
    private String descricao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private PlanejamentoEstrategico planejamentoEstrategico;

    public MacroObjetivoEstrategico() {
        dataRegistro = new Date();
    }

    public MacroObjetivoEstrategico(String descricao, PlanejamentoEstrategico planejamentoEstrategico) {
        this.descricao = descricao;
        this.planejamentoEstrategico = planejamentoEstrategico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public PlanejamentoEstrategico getPlanejamentoEstrategico() {
        return planejamentoEstrategico;
    }

    public void setPlanejamentoEstrategico(PlanejamentoEstrategico planejamentoEstrategico) {
        this.planejamentoEstrategico = planejamentoEstrategico;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MacroObjetivoEstrategico)) {
            return false;
        }
        MacroObjetivoEstrategico other = (MacroObjetivoEstrategico) object;
        if ((this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
