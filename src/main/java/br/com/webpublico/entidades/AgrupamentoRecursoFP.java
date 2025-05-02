/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "RECURSOS HUMANOS")
@Entity
@Audited
@Etiqueta("Agrupamento de Recurso FP")
public class AgrupamentoRecursoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    @Tabelavel
    private Long id;
    @ManyToOne
    private RecursoFP recursoFP;
    @ManyToOne
    private GrupoRecursoFP grupoRecursoFP;
    @Transient
    private Long criadoEm;

    public AgrupamentoRecursoFP() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecursoFP getRecursoFP() {
        return recursoFP;
    }

    public void setRecursoFP(RecursoFP recursoFP) {
        this.recursoFP = recursoFP;
    }

    public GrupoRecursoFP getGrupoRecursoFP() {
        return grupoRecursoFP;
    }

    public void setGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        this.grupoRecursoFP = grupoRecursoFP;
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
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public String toString() {
        return recursoFP.getCodigo();
    }
}
