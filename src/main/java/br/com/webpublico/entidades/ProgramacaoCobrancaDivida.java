package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 30/01/14
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ProgramacaoCobrancaDivida implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private ProgramacaoCobranca programacaoCobranca;
    @ManyToOne
    private Divida divida;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ProgramacaoCobrancaDivida() {
        this.criadoEm =System.nanoTime();
    }

    public ProgramacaoCobrancaDivida(Divida divida, ProgramacaoCobranca programacaoCobranca) {
        this.divida = divida;
        this.programacaoCobranca = programacaoCobranca;
        this.criadoEm =System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProgramacaoCobranca getProgramacaoCobranca() {
        return programacaoCobranca;
    }

    public void setProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca) {
        this.programacaoCobranca = programacaoCobranca;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
