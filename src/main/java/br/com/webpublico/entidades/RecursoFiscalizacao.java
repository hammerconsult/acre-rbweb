/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ResultadoParecerFiscalizacao;
import br.com.webpublico.enums.TipoRecursoFiscalizacao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Leonardo
 */
@Entity
@Audited
public class RecursoFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoRecursoFiscalizacao tipoRecursoFiscalizacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEntrada;
    private String teorRecurso;
    private String parecerRecurso;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataParecer;
    @Enumerated(EnumType.STRING)
    private ResultadoParecerFiscalizacao resultadoParecerFiscalizacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataNovoPrazo;
    @ManyToOne
    private ProcessoFiscalizacao processoFiscalizacao;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    private Long processoProtocolo;
    @Transient
    @Invisivel
    private Long criadoEm;

    public RecursoFiscalizacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(Date dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public Date getDataNovoPrazo() {
        return dataNovoPrazo;
    }

    public void setDataNovoPrazo(Date dataNovoPrazo) {
        this.dataNovoPrazo = dataNovoPrazo;
    }

    public Date getDataParecer() {
        return dataParecer;
    }

    public void setDataParecer(Date dataParecer) {
        this.dataParecer = dataParecer;
    }

    public String getParecerRecurso() {
        return parecerRecurso;
    }

    public void setParecerRecurso(String parecerRecurso) {
        this.parecerRecurso = parecerRecurso;
    }

    public ResultadoParecerFiscalizacao getResultadoParecerFiscalizacao() {
        return resultadoParecerFiscalizacao;
    }

    public void setResultadoParecerFiscalizacao(ResultadoParecerFiscalizacao resultadoParecerFiscalizacao) {
        this.resultadoParecerFiscalizacao = resultadoParecerFiscalizacao;
    }

    public String getTeorRecurso() {
        return teorRecurso;
    }

    public void setTeorRecurso(String teorRecurso) {
        this.teorRecurso = teorRecurso;
    }

    public TipoRecursoFiscalizacao getTipoRecursoFiscalizacao() {
        return tipoRecursoFiscalizacao;
    }

    public void setTipoRecursoFiscalizacao(TipoRecursoFiscalizacao tipoRecursoFiscalizacao) {
        this.tipoRecursoFiscalizacao = tipoRecursoFiscalizacao;
    }

    public ProcessoFiscalizacao getProcessoFiscalizacao() {
        return processoFiscalizacao;
    }

    public void setProcessoFiscalizacao(ProcessoFiscalizacao processoFiscalizacao) {
        this.processoFiscalizacao = processoFiscalizacao;
    }


    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public Long getProcessoProtocolo() {
        return processoProtocolo;
    }

    public void setProcessoProtocolo(Long processoProtocolo) {
        this.processoProtocolo = processoProtocolo;
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
        return "br.com.webpublico.entidades.RecursoFiscalizacao[ id=" + id + " ]";
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }
}
