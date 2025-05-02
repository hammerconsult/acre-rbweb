/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.tributario.procuradoria.PessoaProcesso;
import br.com.webpublico.entidades.tributario.procuradoria.TramiteProcessoJudicial;
import br.com.webpublico.enums.tributario.procuradoria.TipoAcaoProcessoJudicial;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Arthur
 */
@Audited
@Entity
@Etiqueta("Processo Judicial")
public class ProcessoJudicial implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Ajuizamento")
    @Tabelavel
    @Pesquisavel
    private Date dataAjuizamento;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Etiqueta("Número Processo Fórum")
    @Tabelavel
    @Pesquisavel
    private String numeroProcessoForum;
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "processoJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoJudicialCDA> processos;
    @OneToMany(mappedBy = "processoJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TramiteProcessoJudicial> listaTramites;
    @OneToMany(mappedBy = "processoJudicial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaProcesso> envolvidos;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Ação")
    @Pesquisavel
    @Tabelavel
    private TipoAcaoProcessoJudicial tipoAcaoProcessoJudicial;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Enumerated(value = EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private Situacao situacao;

    public ProcessoJudicial() {
        this.criadoEm = System.nanoTime();
        listaTramites = Lists.newArrayList();
        envolvidos = Lists.newArrayList();
        processos = Lists.newArrayList();
    }

    public Date getDataAjuizamento() {
        return dataAjuizamento;
    }

    public void setDataAjuizamento(Date dataAjuizamento) {
        this.dataAjuizamento = dataAjuizamento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getId() {
        return id;
    }

    public String getNumeroProcessoForum() {
        return numeroProcessoForum;
    }

    public void setNumeroProcessoForum(String numeroProcessoForum) {
        this.numeroProcessoForum = numeroProcessoForum;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProcessoJudicialCDA> getProcessos() {
        return processos;
    }

    public void setProcessos(List<ProcessoJudicialCDA> processos) {
        this.processos = processos;
    }

    public List<TramiteProcessoJudicial> getListaTramites() {
        return listaTramites;
    }

    public void setListaTramites(List<TramiteProcessoJudicial> listaTramites) {
        this.listaTramites = listaTramites;
    }

    public List<PessoaProcesso> getEnvolvidos() {
        return envolvidos;
    }

    public void setEnvolvidos(List<PessoaProcesso> envolvidos) {
        this.envolvidos = envolvidos;
    }

    public TipoAcaoProcessoJudicial getTipoAcaoProcessoJudicial() {
        return tipoAcaoProcessoJudicial;
    }

    public void setTipoAcaoProcessoJudicial(TipoAcaoProcessoJudicial tipoAcaoProcessoJudicial) {
        this.tipoAcaoProcessoJudicial = tipoAcaoProcessoJudicial;
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
        return numeroProcessoForum + " - " + Util.formatterDiaMesAno.format(dataAjuizamento) + " - " + situacao.getDescricao();
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public enum Situacao {
        ATIVO("Aberto"),
        INATIVO("Extinto");
        private String descricao;

        Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
