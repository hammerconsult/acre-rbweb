/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@Etiqueta("Diaria de Campo")
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

public class DiariaDeCampo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Codigo")
    @Pesquisavel
    private String codigo;
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Invisivel
    @Etiqueta("Concessão de Diarias")
    @OneToMany
    @JoinTable(name = "DIARIACAMPO_PROPOSTA", joinColumns = {
            @JoinColumn(name = "DIARIACAMPO_ID", unique = true)
    },
            inverseJoinColumns = {
                    @JoinColumn(name = "PROPOSTA_ID")
            })
    private List<PropostaConcessaoDiaria> porpostasConcoesDiarias;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Data da Diaria")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataDiaria;
    @ManyToOne
    private AtoLegal atoLegal;

    public DiariaDeCampo() {
        this.porpostasConcoesDiarias = new ArrayList<PropostaConcessaoDiaria>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<PropostaConcessaoDiaria> getPorpostasConcoesDiarias() {
        return porpostasConcoesDiarias;
    }

    public void setPorpostasConcoesDiarias(List<PropostaConcessaoDiaria> porpostasConcoesDiarias) {
        this.porpostasConcoesDiarias = porpostasConcoesDiarias;
    }

    public Date getDataDiaria() {
        return dataDiaria;
    }

    public void setDataDiaria(Date dataDiaria) {
        this.dataDiaria = dataDiaria;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
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
        if (!(object instanceof DiariaDeCampo)) {
            return false;
        }
        DiariaDeCampo other = (DiariaDeCampo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    private String formataData(Date d) {
        if (d != null) {
            return Util.dateToString(d);
        } else {
            return " ";
        }
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}
