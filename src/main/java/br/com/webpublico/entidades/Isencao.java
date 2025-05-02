/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heinz
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

@Audited
public class Isencao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    private Long codigo;
    @Tabelavel
    private String descricao;
    @Tabelavel
    @ManyToOne
    private Divida divida;
    @Tabelavel
    @ManyToOne
    private AtoLegal atoLegal;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "isencao", orphanRemoval = true)
    private List<ItemIsencao> itens;

    public Isencao() {
        itens = new ArrayList<ItemIsencao>();
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public List<ItemIsencao> getItens() {
        return itens;
    }

    public void setItens(List<ItemIsencao> itens) {
        this.itens = itens;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal lei) {
        this.atoLegal = lei;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof Isencao)) {
            return false;
        }
        Isencao other = (Isencao) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.Isencao[ id=" + id + " ]";
    }

}
