/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.EnceramentoTermoAdvertencia;
import br.com.webpublico.enums.TipoInfracaoFiscalizacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Entity

@Audited
@Etiqueta("Termo de Advertência")
@GrupoDiagrama(nome = "fiscalizacaogeral")
public class TermoAdvertencia implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    private String numero;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataLavratura;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoInfracaoFiscalizacao tipoInfracaoFiscalizacao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataCiencia;
    @Pesquisavel
    private String nomePessoaCiencia;
    private String descricaoDaInfracao;
    @Enumerated(EnumType.STRING)
    private EnceramentoTermoAdvertencia enceramentoTermoAdvertencia;
    @ManyToOne
    private DocumentoOficial documentoOficial;
    @Transient
    @Invisivel
    private Long criadoEm;
    private Integer prazoDiasCumprimento;


    public TermoAdvertencia() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescricaoDaInfracao() {
        return descricaoDaInfracao;
    }

    public void setDescricaoDaInfracao(String descricaoDaInfracao) {
        this.descricaoDaInfracao = descricaoDaInfracao;
    }

    public EnceramentoTermoAdvertencia getEnceramentoTermoAdvertencia() {
        return enceramentoTermoAdvertencia;
    }

    public void setEnceramentoTermoAdvertencia(EnceramentoTermoAdvertencia enceramentoTermoAdvertencia) {
        this.enceramentoTermoAdvertencia = enceramentoTermoAdvertencia;
    }

    public String getNomePessoaCiencia() {
        return nomePessoaCiencia;
    }

    public void setNomePessoaCiencia(String nomePessoaCiencia) {
        this.nomePessoaCiencia = nomePessoaCiencia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoInfracaoFiscalizacao getTipoInfracaoFiscalizacao() {
        return tipoInfracaoFiscalizacao;
    }

    public void setTipoInfracaoFiscalizacao(TipoInfracaoFiscalizacao tipoInfracaoFiscalizacao) {
        this.tipoInfracaoFiscalizacao = tipoInfracaoFiscalizacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public Integer getPrazoDiasCumprimento() {
        return prazoDiasCumprimento;
    }

    public void setPrazoDiasCumprimento(Integer prazoDiasCumprimento) {
        this.prazoDiasCumprimento = prazoDiasCumprimento;
    }

    public Date getDataRetorno() {
        if (getPrazoDiasCumprimento() != null &&
                getDataLavratura() != null) {
            DateTime dateTime = new DateTime(getDataLavratura());
            dateTime = dateTime.plusDays(getPrazoDiasCumprimento());
            return (dateTime.toDate());
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.TermoDeAdvertencia[ id=" + id + " ]";
    }
}
