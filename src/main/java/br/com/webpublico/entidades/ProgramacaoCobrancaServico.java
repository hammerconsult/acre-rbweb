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
 * Time: 10:03
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
public class ProgramacaoCobrancaServico implements Serializable{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @ManyToOne
    private ProgramacaoCobranca programacaoCobranca;
    @ManyToOne
    private Servico servico;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ProgramacaoCobrancaServico() {
        this.criadoEm =System.nanoTime();
    }

    public ProgramacaoCobrancaServico(ProgramacaoCobranca programacaoCobranca, Servico servico) {
        this.programacaoCobranca = programacaoCobranca;
        this.servico = servico;
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

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
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
