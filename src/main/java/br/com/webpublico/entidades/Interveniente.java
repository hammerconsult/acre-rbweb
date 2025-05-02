/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Intervenientes")
public class Interveniente implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Interveniente")
    @ManyToOne
    private TipoInterveniente tipoInterveniente;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    private ClasseCredor classeCredor;
    @Transient
    @Invisivel
    private Long criadoEm;

    public Interveniente() {
        criadoEm = System.nanoTime();
    }

    public Interveniente(TipoInterveniente tipoInterveniente, Pessoa pessoa, ClasseCredor classeCredor, ConvenioDespesa convenioDespesa) {
        this.tipoInterveniente = tipoInterveniente;
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public TipoInterveniente getTipoInterveniente() {
        return tipoInterveniente;
    }

    public void setTipoInterveniente(TipoInterveniente tipoInterveniente) {
        this.tipoInterveniente = tipoInterveniente;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        if (id == null) {
            return "";
        } else {
            return pessoa.getNome() + " - " + tipoInterveniente.getDescricao();
        }
    }
}
