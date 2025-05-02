/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.LazyInitializationException;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class SugestaoFerias implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SugestaoFerias.class);

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Inicial")
    @Tabelavel
    private Date dataInicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data Final")
    @Tabelavel
    private Date dataFim;
    @Etiqueta("Abono Pecúnia")
    @Tabelavel
    private Boolean abonoPecunia;
    @Tabelavel
    @Etiqueta("Dias de Abono")
    private Integer diasAbono;
    @OneToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    @Etiqueta("Período Aquisitivo FL")
    @Tabelavel
    private PeriodoAquisitivoFL periodoAquisitivoFL;
    @OneToMany(mappedBy = "sugestaoFerias", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<AprovacaoFerias> listAprovacaoFerias;
    @Transient
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Reprogramação de Férias")
    private SugestaoFerias sugestaoReprogramacao;
    @Transient
    @Invisivel
    private AprovacaoFerias aprovacaoFerias;
    @Transient
    @Invisivel
    private Long criadoEm;


    public SugestaoFerias() {
        listAprovacaoFerias = Lists.newArrayList();
        this.criadoEm = System.nanoTime();
        abonoPecunia = Boolean.FALSE;
    }

    public SugestaoFerias(Date dataInicio, Date dataFim, Boolean abonoPecunia, PeriodoAquisitivoFL periodoAquisitivoFL, UnidadeOrganizacional unidadeOrganizacional) {
        this.criadoEm = System.nanoTime();
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.abonoPecunia = abonoPecunia;
        this.periodoAquisitivoFL = periodoAquisitivoFL;
        this.unidadeOrganizacional = unidadeOrganizacional;
        listAprovacaoFerias = Lists.newArrayList();
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

    public Boolean getAbonoPecunia() {
        return abonoPecunia != null ? abonoPecunia : false;
    }

    public void setAbonoPecunia(Boolean abonoPecunia) {
        this.abonoPecunia = abonoPecunia;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public PeriodoAquisitivoFL getPeriodoAquisitivoFL() {
        return periodoAquisitivoFL;
    }

    public void setPeriodoAquisitivoFL(PeriodoAquisitivoFL periodoAquisitivoFL) {
        this.periodoAquisitivoFL = periodoAquisitivoFL;
    }

    public List<AprovacaoFerias> getListAprovacaoFerias() {
        return listAprovacaoFerias;
    }

    public void setListAprovacaoFerias(List<AprovacaoFerias> listAprovacaoFerias) {
        this.listAprovacaoFerias = listAprovacaoFerias;
    }

    public Integer getDiasAbono() {
        return diasAbono;
    }

    public void setDiasAbono(Integer diasAbono) {
        this.diasAbono = diasAbono;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
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
        return dataInicio + " - " + dataFim;
    }

    public SugestaoFerias getSugestaoReprogramacao() {
        return sugestaoReprogramacao;
    }

    public void setSugestaoReprogramacao(SugestaoFerias sugestaoReprogramacao) {
        this.sugestaoReprogramacao = sugestaoReprogramacao;
    }

    public Boolean estaAprovada() {
        try {
            if (getAprovacaoFerias() != null && getAprovacaoFerias().isAprovada()) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (ExcecaoNegocioGenerica eng) {
            return Boolean.FALSE;
        }
    }

    public AprovacaoFerias getAprovacaoFerias() throws ExcecaoNegocioGenerica {
        try {
            return listAprovacaoFerias == null ? null : listAprovacaoFerias.isEmpty() ? null : listAprovacaoFerias.get(0);
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("A lista de aprovações não foi instanciada!");
        } catch (LazyInitializationException lie) {
            logger.debug("Erro, a lista de aprovação de férias não foi instanciada.");
            return null;
        } catch (IndexOutOfBoundsException lie) {
            logger.debug("Erro, ao tentar acessar um indice da lista inexistente.");
            return null;
        }

    }

    public void setAprovacaoFerias(AprovacaoFerias aprovacaoFerias) {
        this.aprovacaoFerias = aprovacaoFerias;
    }

    public Boolean estaConcedido() {
        if (getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.CONCEDIDO)) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public Boolean estaProgramada() {
        return getDataInicio() != null;
    }

    public void zerarDiasAbono() {
        this.setDiasAbono(null);
    }
}
