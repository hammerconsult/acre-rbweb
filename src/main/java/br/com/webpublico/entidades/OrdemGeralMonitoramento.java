package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoOrdemGeralMonitoramento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author octavio
 */
@GrupoDiagrama(nome = "Fiscalização")
@Etiqueta("Ordem Geral de Monitoramento")
@Entity
@Audited
public class OrdemGeralMonitoramento extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ordemGeralMonitoramento", orphanRemoval = true)
    private List<MonitoramentoFiscal> monitoramentosFiscais;
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Número")
    private Long numero;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano do Protocolo")
    private String anoProtocolo;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data Inicial da Ordem")
    private Date dataInicial;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta(value = "Data Final da Ordem")
    private Date dataFinal;
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta(value = "Data de Criação")
    private Date dataCriacao;
    @Pesquisavel
    @ManyToOne
    @Etiqueta(value = "Usuário Logado")
    private UsuarioSistema usuarioLogado;
    @Pesquisavel
    @ManyToOne
    @Etiqueta(value = "Auditor Fiscal")
    private UsuarioSistema auditorFiscal;
    @Pesquisavel
    @Etiqueta(value = "Descrição")
    private String descricao;
    @Transient
    @Tabelavel
    @Etiqueta(value = "Fiscais Designados")
    private Set<FiscalMonitoramento> fiscaisMonitoramento;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @ManyToOne
    private DocumentoOficial doctoiniciomonitoramento;
    @ManyToOne
    private DocumentoOficial doctorelatoriofinalogm;
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Situação")
    private SituacaoOrdemGeralMonitoramento situacaoOGM;

    public OrdemGeralMonitoramento() {
        monitoramentosFiscais = Lists.newArrayList();
        fiscaisMonitoramento = new HashSet<>();
    }

    public OrdemGeralMonitoramento(Long id, OrdemGeralMonitoramento ordemGeralMonitoramento) {
        this.setId(id);
        this.setDescricao(ordemGeralMonitoramento.getDescricao());
        this.setMonitoramentosFiscais(ordemGeralMonitoramento.getMonitoramentosFiscais());
        this.setDataCriacao(ordemGeralMonitoramento.getDataCriacao());
        this.setDataInicial(ordemGeralMonitoramento.getDataInicial());
        this.setDataFinal(ordemGeralMonitoramento.getDataFinal());
        this.setFiscaisMonitoramento(ordemGeralMonitoramento.getFiscaisMonitoramento());
        this.setSituacaoOGM(ordemGeralMonitoramento.getSituacaoOGM());
        this.setNumero(ordemGeralMonitoramento.getNumero());
        this.setAnoProtocolo(ordemGeralMonitoramento.getAnoProtocolo());
        this.setNumeroProtocolo(ordemGeralMonitoramento.getNumeroProtocolo());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public SituacaoOrdemGeralMonitoramento getSituacaoOGM() {
        return situacaoOGM;
    }

    public void setSituacaoOGM(SituacaoOrdemGeralMonitoramento situacaoOGM) {
        this.situacaoOGM = situacaoOGM;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public UsuarioSistema getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(UsuarioSistema usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public UsuarioSistema getAuditorFiscal() {
        return auditorFiscal;
    }

    public void setAuditorFiscal(UsuarioSistema auditorFiscal) {
        this.auditorFiscal = auditorFiscal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<MonitoramentoFiscal> getMonitoramentosFiscais() {
        return monitoramentosFiscais;
    }

    public void setMonitoramentosFiscais(List<MonitoramentoFiscal> monitoramentosFiscais) {
        this.monitoramentosFiscais = monitoramentosFiscais;
    }

    public Set<FiscalMonitoramento> getFiscaisMonitoramento() {
        return fiscaisMonitoramento;
    }

    public void setFiscaisMonitoramento(Set<FiscalMonitoramento> fiscaisMonitoramento) {
        this.fiscaisMonitoramento = fiscaisMonitoramento;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public DocumentoOficial getDoctoiniciomonitoramento() {
        return doctoiniciomonitoramento;
    }

    public void setDoctoiniciomonitoramento(DocumentoOficial doctoiniciomonitoramento) {
        this.doctoiniciomonitoramento = doctoiniciomonitoramento;
    }

    public DocumentoOficial getDoctorelatoriofinalogm() {
        return doctorelatoriofinalogm;
    }

    public void setDoctorelatoriofinalogm(DocumentoOficial doctorelatoriofinalogm) {
        this.doctorelatoriofinalogm = doctorelatoriofinalogm;
    }

    @Override
    public String toString() {
        return this.numero + " - " + (this.descricao != null ? this.descricao : "");
    }
}
