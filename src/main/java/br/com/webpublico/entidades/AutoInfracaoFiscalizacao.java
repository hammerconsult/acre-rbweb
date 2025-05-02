/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@Audited
@Entity

@Etiqueta("Auto Infração Fiscalização")
@GrupoDiagrama(nome = "fiscalizacaogeral")
public class AutoInfracaoFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String numero;
    private String numeroFormulario;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataLavratura;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataInfracao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataCiencia;
    private String pessoaCiencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date prazoRecurso;
    private String descricaoInfracao;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private DocumentoOficial documentoOficial;

    private String migracaoChave;

    public AutoInfracaoFiscalizacao() {
        this.criadoEm = System.nanoTime();
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public String getDescricaoInfracao() {
        return descricaoInfracao;
    }

    public void setDescricaoInfracao(String descricaoInfracao) {
        this.descricaoInfracao = descricaoInfracao;
    }

    public Date getPrazoRecurso() {
        return prazoRecurso;
    }

    public void setPrazoRecurso(Date prazoRecurso) {
        this.prazoRecurso = prazoRecurso;
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

    public Date getDataCiencia() {
        return dataCiencia;
    }

    public void setDataCiencia(Date dataCiencia) {
        this.dataCiencia = dataCiencia;
    }

    public Date getDataLavratura() {
        return dataLavratura;
    }

    public void setDataLavratura(Date dataLavratura) {
        this.dataLavratura = dataLavratura;
    }


    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataInfracao() {
        return dataInfracao;
    }

    public void setDataInfracao(Date dataInfracao) {
        this.dataInfracao = dataInfracao;
    }

    public String getNumeroFormulario() {
        return numeroFormulario;
    }

    public void setNumeroFormulario(String numeroFormulario) {
        this.numeroFormulario = numeroFormulario;
    }

     public String getPessoaCiencia() {
        return pessoaCiencia;
    }

    public void setPessoaCiencia(String pessoaCiencia) {
        this.pessoaCiencia = pessoaCiencia;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
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
        return "br.com.webpublico.entidades.AutoInfracaoFiscalizacao[ id=" + id + " ]";
    }
}
