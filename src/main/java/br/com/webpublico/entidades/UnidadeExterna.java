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
 * @author andre
 */
@Entity
@Audited
@Etiqueta("Unidade Externa")
@GrupoDiagrama(nome = "RecursosHumanos")

public class UnidadeExterna extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Pessoa Jurídica")
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    private PessoaJuridica pessoaJuridica;
    @Pesquisavel
    @Etiqueta("Esfera do Governo")
    @Tabelavel
    @ManyToOne
    private EsferaGoverno esferaGoverno;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EsferaGoverno getEsferaGoverno() {
        return esferaGoverno;
    }

    public void setEsferaGoverno(EsferaGoverno esferaGoverno) {
        this.esferaGoverno = esferaGoverno;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
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
        if (!(object instanceof UnidadeExterna)) {
            return false;
        }
        UnidadeExterna other = (UnidadeExterna) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return pessoaJuridica + " - " + esferaGoverno;
    }
}
