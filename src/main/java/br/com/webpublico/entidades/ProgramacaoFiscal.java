/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAcaoFiscal;
import br.com.webpublico.enums.TipoSituacaoProgramacaoFiscal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fabio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Etiqueta("Programação Fiscal")
@Entity

@Audited
public class ProgramacaoFiscal implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta(value = "Número")
    @Pesquisavel
    private Long numero;
    @Tabelavel
    @Etiqueta(value = "Descrição")
    @Pesquisavel
    private String descricao;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data Inicial da Prog.")
    @Pesquisavel
    private Date dataInicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta(value = "Data Final da Prog.")
    @Pesquisavel
    private Date dataFinal;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta(value = "Data de Criação")
    @Pesquisavel
    private Date dataCriacao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "programacaoFiscal")
    private List<AcaoFiscal> acoesFiscais;
    @Transient
    private Long criadoEm;
    private String migracaoChave;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta(value = "Situacao da Programação")
    private TipoSituacaoProgramacaoFiscal situacao;
    private Boolean denunciaEspontanea;
    @Transient
    @Tabelavel
    @Etiqueta(value = "Fiscais Designados")
    private Set<FiscalDesignado> fiscaisDesignados;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ProgramacaoFiscal() {
        criadoEm = System.nanoTime();
        acoesFiscais = new ArrayList<>();
        fiscaisDesignados = new HashSet<>();
        denunciaEspontanea = false;
    }

    public ProgramacaoFiscal(Long id , ProgramacaoFiscal programacaoFiscal){
        this.setId(id);
        this.setDescricao(programacaoFiscal.getDescricao());
        this.setAcoesFiscais(programacaoFiscal.getAcoesFiscais());
        this.setDataCriacao(programacaoFiscal.getDataCriacao());
        this.setDataInicio(programacaoFiscal.getDataInicio());
        this.setDataFinal(programacaoFiscal.getDataFinal());
        this.setDenunciaEspontanea(programacaoFiscal.getDenunciaEspontanea());
        this.setFiscaisDesignados(programacaoFiscal.getFiscaisDesignados());
        this.setSituacao(programacaoFiscal.getSituacao());
        this.setNumero(programacaoFiscal.getNumero());
    }

    public ProgramacaoFiscal(Long id, List<AcaoFiscal> acoesFiscais) {
        this.id = id;
        this.acoesFiscais = acoesFiscais;
    }

    public Set<FiscalDesignado> getFiscaisDesignados() {
        return fiscaisDesignados;
    }

    public void setFiscaisDesignados(Set<FiscalDesignado> fiscaisDesignados) {
        this.fiscaisDesignados = fiscaisDesignados;
    }

    public Boolean getDenunciaEspontanea() {
        if (denunciaEspontanea == null) {
            denunciaEspontanea = false;
        }
        return denunciaEspontanea;
    }

    public void setDenunciaEspontanea(Boolean denunciaEspontanea) {
        this.denunciaEspontanea = denunciaEspontanea;
    }

    public ProgramacaoFiscal(Long id) {
        this.id = id;
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

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public List<AcaoFiscal> getAcoesFiscais() {
        return acoesFiscais;
    }

    public void setAcoesFiscais(List<AcaoFiscal> acoesFiscais) {
        this.acoesFiscais = acoesFiscais;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public TipoSituacaoProgramacaoFiscal getSituacao() {
        return situacao;
    }

    public void setSituacao(TipoSituacaoProgramacaoFiscal situacao) {
        this.situacao = situacao;
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
        return this.numero + "  " + (this.descricao != null ? this.descricao : "");
    }

    public boolean definirSituacaoConcluida() {
        for (AcaoFiscal af : acoesFiscais) {
            if (!af.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.CONCLUIDO)) {
                return false;
            }
        }

        if (getSituacao().equals(TipoSituacaoProgramacaoFiscal.CONCLUIDO)) {
            return false;
        } else {
            setSituacao(TipoSituacaoProgramacaoFiscal.CONCLUIDO);
            return true;
        }
    }

    public boolean definirSituacaoExecutando() {
        if (getSituacao().equals(TipoSituacaoProgramacaoFiscal.EXECUTANDO)) {
            return false;
        }

        for (AcaoFiscal af : acoesFiscais) {
            if (af.getSituacaoAcaoFiscal().equals(SituacaoAcaoFiscal.DESIGNADO)) {
                setSituacao(TipoSituacaoProgramacaoFiscal.EXECUTANDO);
                return true;
            }
        }

        return false;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
