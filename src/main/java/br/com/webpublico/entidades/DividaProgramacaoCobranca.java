package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 31/07/13
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "DividaProgramacaoCobranca")
@Entity
@Cacheable
@Audited
@Etiqueta("Dívida Programação Cobrança")
public class DividaProgramacaoCobranca implements Serializable{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private Divida divida;
    @ManyToOne
    private ProgramacaoCobranca programacaoCobranca;
    @Invisivel
    @Transient
    private Long criadoEm;

    public DividaProgramacaoCobranca() {
        this.criadoEm = System.currentTimeMillis();
    }

    public DividaProgramacaoCobranca(Divida divida, ProgramacaoCobranca programacaoCobranca) {
        this.criadoEm = System.currentTimeMillis();
        this.divida = divida;
        this.programacaoCobranca = programacaoCobranca;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public ProgramacaoCobranca getProgramacaoCobranca() {
        return programacaoCobranca;
    }

    public void setProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca) {
        this.programacaoCobranca = programacaoCobranca;
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
