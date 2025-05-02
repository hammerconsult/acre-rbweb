/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Andamento do Convênio")
public class AndamentoConvenio implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAndamento;
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Etiqueta("Interveniente")
    @ManyToOne
    private Interveniente interveniente;
    @Obrigatorio
    @Etiqueta("Tipo de Interveniente")
    @ManyToOne
    private TipoInterveniente tipoInterveniente;

    public AndamentoConvenio() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAndamento() {
        return dataAndamento;
    }

    public void setDataAndamento(Date dataAndamento) {
        this.dataAndamento = dataAndamento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Interveniente getInterveniente() {
        return interveniente;
    }

    public void setInterveniente(Interveniente interveniente) {
        this.interveniente = interveniente;
    }

    public TipoInterveniente getTipoInterveniente() {
        return tipoInterveniente;
    }

    public void setTipoInterveniente(TipoInterveniente tipoInterveniente) {
        this.tipoInterveniente = tipoInterveniente;
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
        if (!(object instanceof AndamentoConvenio)) {
            return false;
        }
        AndamentoConvenio other = (AndamentoConvenio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao + " - " + interveniente.toString() + " - " + DataUtil.getDataFormatada(dataAndamento);
    }
}
