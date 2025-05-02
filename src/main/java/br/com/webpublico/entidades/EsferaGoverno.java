/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
@Etiqueta("Esfera de Governo")
public class EsferaGoverno extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @NotNull
    @Column(length = 30)
    @Obrigatorio
    private String nome;

    public EsferaGoverno() {
    }

    public EsferaGoverno(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
        if (!(object instanceof EsferaGoverno)) {
            return false;
        }
        EsferaGoverno other = (EsferaGoverno) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }
}
