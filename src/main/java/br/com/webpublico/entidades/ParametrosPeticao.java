/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author gustavo
 */
@Entity

@Audited
@Etiqueta("Parâmetros de Petição")
public class ParametrosPeticao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Funcionário Responsável")
    private Pessoa funcionarioResponsavel;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Tipo de Docto Oficial PF")
    private TipoDoctoOficial doctoPessoaFisica;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Tipo de Docto Oficial PJ")
    private TipoDoctoOficial doctoPessoaJuridica;
    @ManyToOne
    private TipoDoctoOficial doctoImobiliario;
    @ManyToOne
    private TipoDoctoOficial doctoEconomico;
    @ManyToOne
    private TipoDoctoOficial doctoRural;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Criação")
    private Date dataCriacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public TipoDoctoOficial getDoctoPessoaFisica() {
        return doctoPessoaFisica;
    }

    public void setDoctoPessoaFisica(TipoDoctoOficial dctoPessoaFisica) {
        this.doctoPessoaFisica = dctoPessoaFisica;
    }

    public TipoDoctoOficial getDoctoPessoaJuridica() {
        return doctoPessoaJuridica;
    }

    public void setDoctoPessoaJuridica(TipoDoctoOficial dctoPessoaJuridica) {
        this.doctoPessoaJuridica = dctoPessoaJuridica;
    }

    public Pessoa getFuncionarioResponsavel() {
        return funcionarioResponsavel;
    }

    public void setFuncionarioResponsavel(Pessoa funcionarioResponsavel) {
        this.funcionarioResponsavel = funcionarioResponsavel;
    }

    public TipoDoctoOficial getDoctoEconomico() {
        return doctoEconomico;
    }

    public void setDoctoEconomico(TipoDoctoOficial doctoEconomico) {
        this.doctoEconomico = doctoEconomico;
    }

    public TipoDoctoOficial getDoctoImobiliario() {
        return doctoImobiliario;
    }

    public void setDoctoImobiliario(TipoDoctoOficial doctoImobiliario) {
        this.doctoImobiliario = doctoImobiliario;
    }

    public TipoDoctoOficial getDoctoRural() {
        return doctoRural;
    }

    public void setDoctoRural(TipoDoctoOficial doctoRural) {
        this.doctoRural = doctoRural;
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
        if (!(object instanceof ParametrosPeticao)) {
            return false;
        }
        ParametrosPeticao other = (ParametrosPeticao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ParametrosPeticao[ id=" + id + " ]";
    }
}
