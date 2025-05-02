/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal1
 */
@GrupoDiagrama(nome = "Protocolo")
@Audited
@Etiqueta("Subassunto")
@Entity

public class SubAssunto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Column(length = 250)
    @Etiqueta("Nome")
    private String nome;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    @Obrigatorio
    @Column(length = 250)
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Assunto assunto;
//    @ManyToOne
//    @Etiqueta("Tipo de Processo")
//    private TipoProcesso tipoProcesso;
    @Etiqueta("Itens de Rota")
    @OneToMany(cascade = CascadeType.ALL)
    private List<ItemRota> itensRota;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "subAssunto")
    private List<SubAssuntoDocumento> subAssuntoDocumentos;

    public SubAssunto() {
        setSubAssuntoDocumentos(new ArrayList<SubAssuntoDocumento>());
    }

    public SubAssunto(String nome, String descricao, Assunto assunto, List<ItemRota> itensRota) {
        this.nome = nome;
        this.descricao = descricao;
        this.assunto = assunto;
        this.subAssuntoDocumentos = subAssuntoDocumentos;
        this.itensRota = itensRota;
    }

    public List<SubAssuntoDocumento> getSubAssuntoDocumentos() {
        return subAssuntoDocumentos;
    }

    public void setSubAssuntoDocumentos(List<SubAssuntoDocumento> subAssuntoDocumentos) {
        this.subAssuntoDocumentos = subAssuntoDocumentos;
    }


//    public TipoProcesso getTipoProcesso() {
//        return tipoProcesso;
//    }
//
//    public void setTipoProcesso(TipoProcesso tipoProcesso) {
//        this.tipoProcesso = tipoProcesso;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assunto getAssunto() {
        return assunto;
    }

    public void setAssunto(Assunto assunto) {
        this.assunto = assunto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<ItemRota> getItensRota() {
        return itensRota;
    }

    public void setItensRota(List<ItemRota> itensRota) {
        this.itensRota = itensRota;
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
        if (!(object instanceof SubAssunto)) {
            return false;
        }
        SubAssunto other = (SubAssunto) object;
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
