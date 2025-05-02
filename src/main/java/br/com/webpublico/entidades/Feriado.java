/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author terminal4
 */
@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
public class Feriado implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    @Etiqueta("Código")
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data do Feriado")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFeriado;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Column(length = 255)
    @Etiqueta("Descrição")
    private String descricao;

    public Feriado(Date dataFeriado, String descricao) {
        this.dataFeriado = dataFeriado;
        this.descricao = descricao;
    }

    public Feriado() {
    }


    public Date getDataFeriado() {
        return dataFeriado;
    }

    public void setDataFeriado(Date dataFeriado) {
        this.dataFeriado = dataFeriado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
        if (!(object instanceof Feriado)) {
            return false;
        }
        Feriado other = (Feriado) object;
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
