/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.CacheableTributario;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author terminal3
 */
@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
@Etiqueta("Exercício")
public class Exercicio extends CacheableTributario implements Serializable,  Comparable<Exercicio> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Tabelavel
    @Etiqueta("Ano")
    @Obrigatorio
    @Pesquisavel
    private Integer ano;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta("Data Registro")
    private Date dataRegistro;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "exercicio")
    private ParametrosExercicio parametros;

    public Exercicio() {
        dataRegistro = new Date();
    }

    public Exercicio(Long id, Integer ano) {
        this.id = id;
        this.ano = ano;
    }

    public Exercicio(Integer ano) {
        this.ano = ano;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public ParametrosExercicio getParametros() {
        return parametros;
    }

    public void setParametros(ParametrosExercicio parametros) {
        this.parametros = parametros;
    }

    @Override
    public String toString() {
        if (ano != null) {
            return String.valueOf(ano);
        }
        return "";
    }

    public int compareTo(Exercicio o) {
        if (this.ano == null) {
            return o.getAno();
        } else if (o.getAno() == null) {
            return this.ano;
        } else {
            return this.ano.compareTo(o.getAno());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Exercicio exercicio = (Exercicio) o;

        return !(ano != null ? !ano.equals(exercicio.ano) : exercicio.ano != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (ano != null ? ano.hashCode() : 0);
        return result;
    }

    @Override
    public Object getIdentificacao() {
        return this.ano;
    }
}
