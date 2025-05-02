/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Forma de Cobrança")
@Entity

@Audited
public class FormaCobranca implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Nº Convênio")
    private Integer numeroConvenio;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    @Etiqueta("Início Vigência")
    @Pesquisavel
    private Date inicioVigencia;
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Final Vigência")
    private Date finalVigencia;
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Conta Bancária Entidade")
    private ContaBancariaEntidade contaBancariaEntidade;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
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

    public Integer getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(Integer numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
        if (!(object instanceof FormaCobranca)) {
            return false;
        }
        FormaCobranca other = (FormaCobranca) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
