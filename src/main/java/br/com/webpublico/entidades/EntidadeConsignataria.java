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
import java.util.List;

/**
 * @author andre
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Entidade Consignatária")
@Entity

@Audited
public class EntidadeConsignataria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Integer codigo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Consignatário")
    @Obrigatorio
    @ManyToOne
    private PessoaJuridica pessoaJuridica;
    @Tabelavel
    @Etiqueta("Itens da Entidade Consignatária")
    @OneToMany(mappedBy = "entidadeConsignataria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemEntidadeConsignataria> itemEntidadeConsignatarias;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public List<ItemEntidadeConsignataria> getItemEntidadeConsignatarias() {
        return itemEntidadeConsignatarias;
    }

    public void setItemEntidadeConsignatarias(List<ItemEntidadeConsignataria> itemEntidadeConsignatarias) {
        this.itemEntidadeConsignatarias = itemEntidadeConsignatarias;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
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
        if (!(object instanceof EntidadeConsignataria)) {
            return false;
        }
        EntidadeConsignataria other = (EntidadeConsignataria) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return pessoaJuridica.toString();
    }
}
