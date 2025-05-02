package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 31/07/13
 * Time: 11:20
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "SituacaoCobraca")
@Entity
@Cacheable
@Audited
@Table(name = "SITUACAO_PROG_COBRANCA")
@Etiqueta("Situação da Cobrança")
public class SituacaoProgramacaoCobranca implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProgramacaoCobranca programacaoCobranca;
    @Temporal(TemporalType.DATE)
    private Date dataSituacao;
    @Enumerated(EnumType.STRING)
    private TipoSituacaoProgramacaoCobranca situacaoProgramacaoCobranca;
    @Invisivel
    @Transient
    private Long criadoEm;

    public SituacaoProgramacaoCobranca() {
        this.criadoEm = System.currentTimeMillis();
    }

    //usar este Construtor para que a ultima situação da programação fique sempre atualizada
    public SituacaoProgramacaoCobranca(ProgramacaoCobranca programacaoCobranca, TipoSituacaoProgramacaoCobranca situacaoProgramacaoCobranca, Date dataCriacao) {
        this.programacaoCobranca = programacaoCobranca;
        this.situacaoProgramacaoCobranca = situacaoProgramacaoCobranca;
        this.programacaoCobranca.setUltimaSituacao(situacaoProgramacaoCobranca);
        this.dataSituacao = dataCriacao;
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

    public TipoSituacaoProgramacaoCobranca getSituacaoProgramacaoCobranca() {
        return situacaoProgramacaoCobranca;
    }

    public void setSituacaoProgramacaoCobranca(TipoSituacaoProgramacaoCobranca situacaoProgramacaoCobranca) {
        this.situacaoProgramacaoCobranca = situacaoProgramacaoCobranca;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataSituacao() {
        return dataSituacao;
    }

    public void setDataSituacao(Date dataSituacao) {
        this.dataSituacao = dataSituacao;
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
