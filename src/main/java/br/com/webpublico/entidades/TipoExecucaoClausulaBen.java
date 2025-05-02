/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author major
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")

public class TipoExecucaoClausulaBen implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ClausulaBenificiario clausulaBenificiario;
    @ManyToOne
    private TipoExecucao tipoExecucao;
    @Transient
    private Long criadoEm;

    public TipoExecucaoClausulaBen() {
        criadoEm = System.nanoTime();
    }

    public TipoExecucaoClausulaBen(ClausulaBenificiario clausulaBenificiario, TipoExecucao tipoExecucao) {
        this.clausulaBenificiario = clausulaBenificiario;
        this.tipoExecucao = tipoExecucao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClausulaBenificiario getClausulaBenificiario() {
        return clausulaBenificiario;
    }

    public void setClausulaBenificiario(ClausulaBenificiario clausulaBenificiario) {
        this.clausulaBenificiario = clausulaBenificiario;
    }

    public TipoExecucao getTipoExecucao() {
        return tipoExecucao;
    }

    public void setTipoExecucao(TipoExecucao tipoExecucao) {
        this.tipoExecucao = tipoExecucao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(id);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.TipoExecucaoClausulaBen[ id=" + id + " ]";
    }
}
