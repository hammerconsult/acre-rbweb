/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPrazo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;


/**
 * @author Munif
 */
@GrupoDiagrama(nome = "Protocolo")
@Audited
@Etiqueta("Trâmite")
@Entity

public class Tramite extends SuperEntidade implements Comparable<Object> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Parecer")
    private String parecer;

    @ManyToOne
    @Etiqueta("Processo")
    private Processo processo;

    @ManyToOne
    @Etiqueta("Unidade de Destino")
    private UnidadeOrganizacional unidadeOrganizacional;

    @ManyToOne
    @Etiqueta("Situação")
    private SituacaoTramite situacaoTramite;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Etiqueta("Data")
    private Date dataRegistro;

    private int indice;
    @ManyToOne
    @Etiqueta("Responsável")
    private UsuarioSistema usuarioTramite;

    @Etiqueta("Status")
    private Boolean status;

    @Etiqueta("Data de Aceite")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataAceite;

    @Etiqueta("Data de Conclusão")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataConclusao;

    @Etiqueta("Data de Continuada")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataContinuacaoPausa;

    @Obrigatorio
    @Etiqueta("Prazo")
    private Double prazo;

    @Etiqueta("Tipo de Prazo")
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazo;

    @Etiqueta("Aceito")
    private Boolean aceito;

    @Etiqueta("Responsável")
    private String responsavel;

    @Etiqueta("Observações")
    private String observacoes;

    @Etiqueta("Motivo")
    private String motivo;

    @ManyToOne
    @Etiqueta("Unidade de Origem")
    private UnidadeOrganizacional origem;

    @Etiqueta("Responsável Parecer")
    private String responsavelParecer;

    @Etiqueta("Destino Externo")
    private String destinoExterno;

    @Transient
    private Boolean protocoloImpresso;
    private Boolean encaminhamentoMultiplo;
    @Transient
    private String nomeUnidadeOrganizacional;

    public Tramite() {
        dataRegistro = new Date();
        aceito = Boolean.FALSE;
        protocoloImpresso = Boolean.FALSE;
        status = Boolean.TRUE;
        prazo = Double.valueOf(0);
        encaminhamentoMultiplo = Boolean.FALSE;
        this.tipoPrazo = TipoPrazo.HORAS;
    }

    public Tramite(String parecer, Processo processo, UnidadeOrganizacional unidadeOrganizacional, SituacaoTramite situacaoTramite, Date dataRegistro, int indice, Boolean status, Double prazo, Boolean aceito, TipoPrazo tipoPrazo, UnidadeOrganizacional origem, Boolean encaminhamentoMultiplo) {
        this.parecer = parecer;
        this.processo = processo;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.situacaoTramite = situacaoTramite;
        this.dataRegistro = dataRegistro;
        this.indice = indice;
        this.status = status;
        this.prazo = prazo;
        this.aceito = aceito;
        this.tipoPrazo = tipoPrazo;
        this.origem = origem;
        this.encaminhamentoMultiplo = encaminhamentoMultiplo;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public UnidadeOrganizacional getOrigem() {
        return origem;
    }

    public void setOrigem(UnidadeOrganizacional origem) {
        this.origem = origem;
    }

    public Date getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(Date dataAceite) {
        this.dataAceite = dataAceite;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public UsuarioSistema getUsuarioTramite() {
        return usuarioTramite;
    }

    public void setUsuarioTramite(UsuarioSistema usuarioTramite) {
        this.usuarioTramite = usuarioTramite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SituacaoTramite getSituacaoTramite() {
        return situacaoTramite;
    }

    public void setSituacaoTramite(SituacaoTramite situacaoTramite) {
        this.situacaoTramite = situacaoTramite;
    }

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getDestinoTramite() {
        if (this.unidadeOrganizacional == null) {
            return "Externo: " + getDestinoExterno();
        }
        return this.unidadeOrganizacional.getDescricao();
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public Boolean getAceito() {
        return aceito != null ? aceito : false;
    }

    public void setAceito(Boolean aceito) {
        this.aceito = aceito;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Double getPrazo() {
        return prazo;
    }

    public void setPrazo(Double prazo) {
        this.prazo = prazo;
    }


    public String getResponsavelParecer() {
        return responsavelParecer;
    }

    public void setResponsavelParecer(String responsavelParecer) {
        this.responsavelParecer = responsavelParecer;
    }

    public Boolean getProtocoloImpresso() {
        return protocoloImpresso;
    }

    public void setProtocoloImpresso(Boolean protocoloImpresso) {
        this.protocoloImpresso = protocoloImpresso;
    }

    @Override
    public String toString() {
        return "Prazo Estimado: " + prazo + " " + (tipoPrazo != null ? tipoPrazo.getDescricao() : " ") + "- " + getDestinoTramite() + " - " + (situacaoTramite != null ? situacaoTramite.getNome() : "");
    }

    @Override
    public int compareTo(Object o) {
        return this.indice - ((Tramite) o).indice;
    }

    public TipoPrazo getTipoPrazo() {
        return tipoPrazo;
    }

    public void setTipoPrazo(TipoPrazo tipoPrazo) {
        this.tipoPrazo = tipoPrazo;
    }

    public String getDestinoExterno() {
        return destinoExterno != null ? destinoExterno.toUpperCase() : "";
    }

    public void setDestinoExterno(String destinoExterno) {
        if (destinoExterno != null) {
            this.destinoExterno = destinoExterno.toUpperCase();
        }
    }

    public Boolean getEncaminhamentoMultiplo() {
        return encaminhamentoMultiplo;
    }

    public void setEncaminhamentoMultiplo(Boolean encaminhamentoMultiplo) {
        this.encaminhamentoMultiplo = encaminhamentoMultiplo;
    }

    public String getNomeUnidadeOrganizacional() {
        if (nomeUnidadeOrganizacional != null && !nomeUnidadeOrganizacional.isEmpty()) {
            return nomeUnidadeOrganizacional;
        }
        if (unidadeOrganizacional != null) {
            return unidadeOrganizacional.getDescricao();
        }
        return "";
    }

    public void setNomeUnidadeOrganizacional(String nomeUnidadeOrganizacional) {
        this.nomeUnidadeOrganizacional = nomeUnidadeOrganizacional;
    }

    public Date getDataContinuacaoPausa() {
        return dataContinuacaoPausa;
    }

    public void setDataContinuacaoPausa(Date dataContinuacaoPausa) {
        this.dataContinuacaoPausa = dataContinuacaoPausa;
    }
}
