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

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Entidade Concedente")
public class EntidadeRepassadora implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código do TCE")
    @Pesquisavel
    private String codigoTce;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Pessoa Jurídica")
    @ManyToOne
    @Pesquisavel
    private Pessoa pessoaJuridica;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Esfera de Governo")
    @ManyToOne
    @Pesquisavel
    private EsferaGoverno esferaGoverno;

    public EntidadeRepassadora() {
    }

    public EntidadeRepassadora(String codigoTce, Pessoa pessoaJuridica, EsferaGoverno esferaGoverno) {
        this.codigoTce = codigoTce;
        this.pessoaJuridica = pessoaJuridica;
        this.esferaGoverno = esferaGoverno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoTce() {
        return codigoTce;
    }

    public void setCodigoTce(String codigoTce) {
        this.codigoTce = codigoTce;
    }

    public EsferaGoverno getEsferaGoverno() {
        return esferaGoverno;
    }

    public void setEsferaGoverno(EsferaGoverno esferaGoverno) {
        this.esferaGoverno = esferaGoverno;
    }

    public Pessoa getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(Pessoa pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
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
        if (!(object instanceof EntidadeRepassadora)) {
            return false;
        }
        EntidadeRepassadora other = (EntidadeRepassadora) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return pessoaJuridica.getCpf_Cnpj() + " - " + pessoaJuridica.getNome() + " (" + esferaGoverno.getNome() + ")";
    }
}
