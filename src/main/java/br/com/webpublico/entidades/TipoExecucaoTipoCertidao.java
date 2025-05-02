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
public class TipoExecucaoTipoCertidao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TipoDoctoHabilitacao tipoDoctoHabilitacao;
    @ManyToOne
    private TipoExecucao tipoExecucao;
    @Transient
    private Long criadoEm;

    public TipoExecucaoTipoCertidao() {
        criadoEm = System.nanoTime();
    }

    public TipoExecucaoTipoCertidao(TipoDoctoHabilitacao tipoDoctoHabilitacao, TipoExecucao tipoExecucao) {
        this.tipoDoctoHabilitacao = tipoDoctoHabilitacao;
        this.tipoExecucao = tipoExecucao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoDoctoHabilitacao getTipoDoctoHabilitacao() {
        return tipoDoctoHabilitacao;
    }

    public void setTipoDoctoHabilitacao(TipoDoctoHabilitacao tipoDoctoHabilitacao) {
        this.tipoDoctoHabilitacao = tipoDoctoHabilitacao;
    }

    public TipoExecucao getTipoExecucao() {
        return tipoExecucao;
    }

    public void setTipoExecucao(TipoExecucao tipoExecucao) {
        this.tipoExecucao = tipoExecucao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return this.tipoDoctoHabilitacao + " - " + this.tipoExecucao;
    }
}
