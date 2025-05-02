/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusProcessoFiscalizacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Claudio
 */
@Entity

@Audited
@Etiqueta("Situação processo fiscal")
@GrupoDiagrama(nome = "fiscalizacaogeral")
public class SituacaoProcessoFiscal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date data;
    @ManyToOne
    private ProcessoFiscalizacao processoFiscalizacao;
    @Enumerated(EnumType.STRING)
    private StatusProcessoFiscalizacao statusProcessoFiscalizacao;
    @Transient
    private Long criadoEm;

    public SituacaoProcessoFiscal() {
        this.criadoEm = System.nanoTime();
        processoFiscalizacao = new ProcessoFiscalizacao();
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public ProcessoFiscalizacao getProcessoFiscalizacao() {
        return processoFiscalizacao;
    }

    public void setProcessoFiscalizacao(ProcessoFiscalizacao processoFiscalizacao) {
        this.processoFiscalizacao = processoFiscalizacao;
    }

    public StatusProcessoFiscalizacao getStatusProcessoFiscalizacao() {
        return statusProcessoFiscalizacao;
    }

    public void setStatusProcessoFiscalizacao(StatusProcessoFiscalizacao statusProcessoFiscalizacao) {
        this.statusProcessoFiscalizacao = statusProcessoFiscalizacao;
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
        return statusProcessoFiscalizacao.getDescricao();
    }
}
