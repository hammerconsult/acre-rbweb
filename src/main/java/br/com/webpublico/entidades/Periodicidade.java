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
 * Representa um determinado período de tempo (meses, quinzenas, semanas, etc.)
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Periodicidade")
public class Periodicidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Descrição desta periodicidade: semestral, quinzenal, mensal, semanal,
     * diária, etc...
     */
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    /**
     * Número de dias correspondente a esta periodicidade: 180, 15, 30, 7, 1,
     * etc...
     */
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Dias")
    @Pesquisavel
    private Integer dias;

    public Periodicidade(String descricao, Integer dias) {
        this.descricao = descricao;
        this.dias = dias;
    }

    public Periodicidade() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDias() {
        return dias;
    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Periodicidade other = (Periodicidade) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
