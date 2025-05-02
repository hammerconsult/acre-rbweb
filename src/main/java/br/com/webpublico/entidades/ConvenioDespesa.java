/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.DestinacaoRecurso;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoConvenioDespesa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Convênio de Despesa")
public class ConvenioDespesa extends SuperEntidade implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Artigo LDO")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String artLdo;
    @Etiqueta("Artigo LOA")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String artLoa;
    @Etiqueta("Número do Convênio")
    @Pesquisavel
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.NUMERO_ORDENAVEL)
    private String numConvenio;
    @Etiqueta("Número do Termo")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Obrigatorio
    @Etiqueta("Nome do Projeto")
    @Tabelavel
    @Pesquisavel
    private String nomeProjeto;
    @Obrigatorio
    @Etiqueta("Objeto")
    private String objeto;
    @Etiqueta("Valor de Repasse (R$)")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal valorRepasse;
    @Etiqueta("Data da Periodicidade")
    private Integer dataPeriodicidade;
    @Etiqueta("Descrição da Meta")
    private String descricaoMeta;
    @Etiqueta("Valor do Convênio (R$)")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Monetario
    private BigDecimal valorConvenio;
    @Etiqueta("Valor da Contrapartida (R$)")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal valorContrapartida;
    @Etiqueta("Data de Assinatura")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Date dataAss;
    @Etiqueta("Data de Publicação")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(campoSelecionado = false)
    private Date dataPublicacao;
    @Etiqueta("Data da Vigência Inicial")
    @Obrigatorio
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    private Date dataVigenciaInicial;
    @Etiqueta("Data da Vigência Final")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    private Date dataVigenciaFinal;
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Destinação de Recursos")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private DestinacaoRecurso destinacaoRecurso;
    @ManyToOne
    @Etiqueta("Ato Legal")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private AtoLegal atoLegal;
    @ManyToOne
    @Etiqueta("Tipo de Execução")
    @Tabelavel(campoSelecionado = false)
    private TipoExecucao tipoExecucao;
    @ManyToOne
    @Etiqueta("Entidade Beneficiária")
    @Pesquisavel
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    private EntidadeBeneficiaria entidadeBeneficiaria;
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Conta Corrente Bancaria")
    private ContaCorrenteBancaria contaCorrenteBanc;
    @Etiqueta("Órgão de Publicação")
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private VeiculoDePublicacao veiculoDePublicacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta(value = "Data Final da Prestação de Conta")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date prestacaoContaFinal;
    @ManyToOne
    @Etiqueta(value = "Periodicidade")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Periodicidade periodicidade;
    @ManyToOne
    @Obrigatorio
    @Etiqueta(value = "Classe")
    @Tabelavel(campoSelecionado = false)
    private ClasseCredor classeCredor;
    @Invisivel
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Tramites")
    @Tabelavel(campoSelecionado = false)
    private List<TramiteConvenioDesp> tramites;
    @Invisivel
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Plano de Aplicações")
    @Tabelavel(campoSelecionado = false)
    private List<PlanoAplicacao> planoAplicacoes;
    @Invisivel
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Cronograma de Desembolsos")
    @Tabelavel(campoSelecionado = false)
    private List<CronogramaDesembolso> cronogramaDesembolsos;
    @Invisivel
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Despesas")
    @Tabelavel(campoSelecionado = false)
    private List<DespesaExercConvenio> despesaExercConvenios;
    @Invisivel
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Intervenientes")
    @Tabelavel(campoSelecionado = false)
    private List<ConvenioDespInterveniente> convenioDespIntervenientes;
    @Invisivel
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Andamentos")
    @Tabelavel(campoSelecionado = false)
    private List<AndamentoConvenioDespesa> andamentoConvenioDespesa;
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Prestação de Contas")
    @Pesquisavel
    private List<PrestacaoContas> prestacaoContas;
    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Liberação")
    private List<ConvenioDespesaLiberacao> convenioDespesaLiberacoes;

    @OneToMany(mappedBy = "convenioDespesa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Aditivo")
    private List<AditivosConvenioDespesa> aditivosConvenioDespesas;

    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private SituacaoCadastralContabil situacaoCadastralContabil;
    //atributo exercicio criado para migração
    @Invisivel
    @ManyToOne
    private Exercicio exercicio;
    @Transient
    private Long criadoEm;
    @Obrigatorio
    @Etiqueta("Órgão Concedente")
    @OneToOne
    @Tabelavel(campoSelecionado = false)
    private Entidade orgaoConvenente;
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor do Convênio Aditivado (R$)")
    private BigDecimal valorConvenioAditivado;
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor Contrapartida Aditivada (R$)")
    private BigDecimal valorContrapartidaAditivada;
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor de Repasse Aditivado(R$)")
    private BigDecimal valorRepasseAditivado;
    @Invisivel
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Convênio")
    private TipoConvenioDespesa tipoConvenio;
    @ManyToOne
    private Emenda emenda;

    public ConvenioDespesa() {
        valorContrapartida = new BigDecimal(BigInteger.ZERO);
        valorConvenio = new BigDecimal(BigInteger.ZERO);
        valorRepasse = new BigDecimal(BigInteger.ZERO);
        valorConvenioAditivado = new BigDecimal(BigInteger.ZERO);
        valorContrapartidaAditivada = new BigDecimal(BigInteger.ZERO);
        valorRepasseAditivado = new BigDecimal(BigInteger.ZERO);
        tramites = new ArrayList<TramiteConvenioDesp>();
        planoAplicacoes = new ArrayList<PlanoAplicacao>();
        cronogramaDesembolsos = new ArrayList<CronogramaDesembolso>();
        despesaExercConvenios = new ArrayList<DespesaExercConvenio>();
        convenioDespIntervenientes = new ArrayList<ConvenioDespInterveniente>();
        andamentoConvenioDespesa = new ArrayList<AndamentoConvenioDespesa>();
        convenioDespesaLiberacoes = new ArrayList<>();
        aditivosConvenioDespesas = new ArrayList<>();
        prestacaoContas = new ArrayList<>();
        criadoEm = System.nanoTime();
        dataVigenciaInicial = new Date();
    }

    public ConvenioDespesa(String artLdo, String artLoa, String numConvenio, String numero, String objeto, BigDecimal valorConvenio, BigDecimal valorContrapartida, Date dataAss, Date dataPublicacao, Date dataVigenciaInicial, Date dataVigenciaFinal, DestinacaoRecurso destinacaoRecurso, AtoLegal atoLegal, TipoExecucao tipoExecucao, EntidadeBeneficiaria entidadeBeneficiaria, VeiculoDePublicacao veiculoDePublicacao, Date prestacaoContaFinal, Periodicidade periodicidade, List<TramiteConvenioDesp> tramites, List<PlanoAplicacao> planoAplicacoes, List<CronogramaDesembolso> cronogramaDesembolsos, List<DespesaExercConvenio> despesaExercConvenios, List<ConvenioDespInterveniente> convenioDespIntervenientes, List<AndamentoConvenioDespesa> andamentoConvenioDespesa) {
        this.artLdo = artLdo;
        this.artLoa = artLoa;
        this.numConvenio = numConvenio;
        this.numero = numero;
        this.objeto = objeto;
        this.valorConvenio = valorConvenio;
        this.valorContrapartida = valorContrapartida;
        this.dataAss = dataAss;
        this.dataPublicacao = dataPublicacao;
        this.dataVigenciaInicial = dataVigenciaInicial;
        this.dataVigenciaFinal = dataVigenciaFinal;
        this.destinacaoRecurso = destinacaoRecurso;
        this.atoLegal = atoLegal;
        this.tipoExecucao = tipoExecucao;
        this.entidadeBeneficiaria = entidadeBeneficiaria;
        this.veiculoDePublicacao = veiculoDePublicacao;
        this.prestacaoContaFinal = prestacaoContaFinal;
        this.periodicidade = periodicidade;
        this.tramites = tramites;
        this.planoAplicacoes = planoAplicacoes;
        this.cronogramaDesembolsos = cronogramaDesembolsos;
        this.despesaExercConvenios = despesaExercConvenios;
        this.convenioDespIntervenientes = convenioDespIntervenientes;
        this.andamentoConvenioDespesa = andamentoConvenioDespesa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtLdo() {
        return artLdo;
    }

    public void setArtLdo(String artLdo) {
        this.artLdo = artLdo;
    }

    public String getArtLoa() {
        return artLoa;
    }

    public void setArtLoa(String artLoa) {
        this.artLoa = artLoa;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getDataAss() {
        return dataAss;
    }

    public void setDataAss(Date dataAss) {
        this.dataAss = dataAss;
    }

    public Date getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(Date dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Date getDataVigenciaFinal() {
        return dataVigenciaFinal;
    }

    public void setDataVigenciaFinal(Date dataVigenciaFinal) {
        this.dataVigenciaFinal = dataVigenciaFinal;
    }

    public Date getDataVigenciaInicial() {
        return dataVigenciaInicial;
    }

    public void setDataVigenciaInicial(Date dataVigenciaInicial) {
        this.dataVigenciaInicial = dataVigenciaInicial;
    }


    public String getNumConvenio() {
        return numConvenio;
    }

    public void setNumConvenio(String numConvenio) {
        this.numConvenio = numConvenio;
    }

    public BigDecimal getValorContrapartida() {
        return valorContrapartida;
    }

    public void setValorContrapartida(BigDecimal valorContrapartida) {
        this.valorContrapartida = valorContrapartida;
    }

    public BigDecimal getValorConvenio() {
        return valorConvenio;
    }

    public void setValorConvenio(BigDecimal valorConvenio) {
        this.valorConvenio = valorConvenio;
    }

    public List<CronogramaDesembolso> getCronogramaDesembolsos() {
        return cronogramaDesembolsos;
    }

    public void setCronogramaDesembolsos(List<CronogramaDesembolso> cronogramaDesembolsos) {
        this.cronogramaDesembolsos = cronogramaDesembolsos;
    }

    public List<DespesaExercConvenio> getDespesaExercConvenios() {
        return despesaExercConvenios;
    }

    public void setDespesaExercConvenios(List<DespesaExercConvenio> despesaExercConvenios) {
        this.despesaExercConvenios = despesaExercConvenios;
    }

    public List<PlanoAplicacao> getPlanoAplicacoes() {
        return planoAplicacoes;
    }

    public void setPlanoAplicacoes(List<PlanoAplicacao> planoAplicacoes) {
        this.planoAplicacoes = planoAplicacoes;
    }

    public List<TramiteConvenioDesp> getTramites() {
        return tramites;
    }

    public void setTramites(List<TramiteConvenioDesp> tramites) {
        this.tramites = tramites;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public TipoExecucao getTipoExecucao() {
        return tipoExecucao;
    }

    public void setTipoExecucao(TipoExecucao tipoExecucao) {
        this.tipoExecucao = tipoExecucao;
    }

    public EntidadeBeneficiaria getEntidadeBeneficiaria() {
        return entidadeBeneficiaria;
    }

    public void setEntidadeBeneficiaria(EntidadeBeneficiaria entidadeBeneficiaria) {
        this.entidadeBeneficiaria = entidadeBeneficiaria;
    }

    public VeiculoDePublicacao getVeiculoDePublicacao() {
        return veiculoDePublicacao;
    }

    public void setVeiculoDePublicacao(VeiculoDePublicacao veiculoDePublicacao) {
        this.veiculoDePublicacao = veiculoDePublicacao;
    }

    public DestinacaoRecurso getDestinacaoRecurso() {
        return destinacaoRecurso;
    }

    public void setDestinacaoRecurso(DestinacaoRecurso destinacaoRecurso) {
        this.destinacaoRecurso = destinacaoRecurso;
    }

    public Periodicidade getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(Periodicidade periodicidade) {
        this.periodicidade = periodicidade;
    }

    public Date getPrestacaoContaFinal() {
        return prestacaoContaFinal;
    }

    public void setPrestacaoContaFinal(Date prestacaoContaFinal) {
        this.prestacaoContaFinal = prestacaoContaFinal;
    }

    public List<ConvenioDespInterveniente> getConvenioDespIntervenientes() {
        return convenioDespIntervenientes;
    }

    public void setConvenioDespIntervenientes(List<ConvenioDespInterveniente> convenioDespIntervenientes) {
        this.convenioDespIntervenientes = convenioDespIntervenientes;
    }

    public List<AndamentoConvenioDespesa> getAndamentoConvenioDespesa() {
        return andamentoConvenioDespesa;
    }

    public void setAndamentoConvenioDespesa(List<AndamentoConvenioDespesa> andamentoConvenioDespesa) {
        this.andamentoConvenioDespesa = andamentoConvenioDespesa;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getValorRepasse() {
        return valorRepasse;
    }

    public void setValorRepasse(BigDecimal valorRepasse) {
        this.valorRepasse = valorRepasse;
    }

    public Integer getDataPeriodicidade() {
        return dataPeriodicidade;
    }

    public void setDataPeriodicidade(Integer dataPeriodicidade) {
        this.dataPeriodicidade = dataPeriodicidade;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricaoMeta() {
        return descricaoMeta;
    }

    public void setDescricaoMeta(String descricaoMeta) {
        this.descricaoMeta = descricaoMeta;
    }

    public List<PrestacaoContas> getPrestacaoContas() {
        return prestacaoContas;
    }

    public void setPrestacaoContas(List<PrestacaoContas> prestacaoContas) {
        this.prestacaoContas = prestacaoContas;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public ContaCorrenteBancaria getContaCorrenteBanc() {
        return contaCorrenteBanc;
    }

    public void setContaCorrenteBanc(ContaCorrenteBancaria contaCorrenteBanc) {
        this.contaCorrenteBanc = contaCorrenteBanc;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public List<ConvenioDespesaLiberacao> getConvenioDespesaLiberacoes() {
        return convenioDespesaLiberacoes;
    }

    public void setConvenioDespesaLiberacoes(List<ConvenioDespesaLiberacao> convenioDespesaLiberacoes) {
        this.convenioDespesaLiberacoes = convenioDespesaLiberacoes;
    }

    public SituacaoCadastralContabil getSituacaoCadastralContabil() {
        return situacaoCadastralContabil;
    }

    public void setSituacaoCadastralContabil(SituacaoCadastralContabil situacaoCadastralContabil) {
        this.situacaoCadastralContabil = situacaoCadastralContabil;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public List<AditivosConvenioDespesa> getAditivosConvenioDespesas() {
        return aditivosConvenioDespesas;
    }

    public void setAditivosConvenioDespesas(List<AditivosConvenioDespesa> aditivosConvenioDespesas) {
        this.aditivosConvenioDespesas = aditivosConvenioDespesas;
    }

    public Entidade getOrgaoConvenente() {
        return orgaoConvenente;
    }

    public void setOrgaoConvenente(Entidade orgaoConvenente) {
        this.orgaoConvenente = orgaoConvenente;
    }

    public BigDecimal getValorConvenioAditivado() {
        return valorConvenioAditivado;
    }

    public void setValorConvenioAditivado(BigDecimal valorConvenioAditivado) {
        this.valorConvenioAditivado = valorConvenioAditivado;
    }

    public BigDecimal getValorContrapartidaAditivada() {
        return valorContrapartidaAditivada;
    }

    public void setValorContrapartidaAditivada(BigDecimal valorContrapartidaAditivada) {
        this.valorContrapartidaAditivada = valorContrapartidaAditivada;
    }

    public BigDecimal getValorRepasseAditivado() {
        return valorRepasseAditivado;
    }

    public void setValorRepasseAditivado(BigDecimal valorRepasseAditivado) {
        this.valorRepasseAditivado = valorRepasseAditivado;
    }

    public String getNomeProjeto() {
        return nomeProjeto;
    }

    public void setNomeProjeto(String nomeProjeto) {
        this.nomeProjeto = nomeProjeto;
    }

    public TipoConvenioDespesa getTipoConvenio() {
        return tipoConvenio;
    }

    public void setTipoConvenio(TipoConvenioDespesa tipoConvenio) {
        this.tipoConvenio = tipoConvenio;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Emenda getEmenda() {
        return emenda;
    }

    public void setEmenda(Emenda emenda) {
        this.emenda = emenda;
    }

    @Override
    public String toString() {
        return numConvenio + " - " + objeto;
    }
}
