/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.emendagoverno.EmendaGoverno;
import br.com.webpublico.entidades.contabil.emendagoverno.ProgramaGoverno;
import br.com.webpublico.enums.DestinacaoRecurso;
import br.com.webpublico.enums.OrigemRecurso;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoConvenioReceita;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Convênio de Receita")
public class ConvenioReceita extends SuperEntidade implements Serializable, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private String numero;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Nome do Projeto")
    @Pesquisavel
    private String nomeConvenio;
    @Obrigatorio
    @Etiqueta("Tipo de Convênio")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoConvenioReceita tipoConvenioReceita;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Entidade Concedente")
    @ManyToOne
    @Pesquisavel
    private EntidadeRepassadora entidadeRepassadora;
    @Obrigatorio
    @Etiqueta("Convenente")
    @OneToOne
    @Tabelavel(campoSelecionado = false)
    private Entidade entidadeConvenente;
    @Etiqueta("Objeto do Convênio")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String objetoConvenio;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Vigência Inicial")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date vigenciaInicial;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data da Assinatura")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAssinatura;
    @Etiqueta("Data da Prestação de Contas")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    private Date dataPrestacaoContas;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Vigência Final")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    private Date vigenciaFinal;
    @Obrigatorio
    @Etiqueta("Número do Termo")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String numeroTermo;
    @Etiqueta("Número Diário Oficial")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String numeroDiarioOficial;
    @Etiqueta("Data Diário Oficial")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date dataDiarioOficial;
    @Etiqueta("Código Cadastro no TCE")
    private String codigoCadTce;
    @Etiqueta("Origem do Recurso")
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private OrigemRecurso origemRecurso;
    @Etiqueta("Destinação do Recurso")
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private DestinacaoRecurso destinacaoRecurso;
    @Obrigatorio
    @Etiqueta("Valor do Convênio (R$)")
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal valorConvenio;
    @Obrigatorio
    @Etiqueta("Valor da Contrapartida  (R$)")
    @Tabelavel
    @Monetario
    private BigDecimal valorContrapartida;
    @Etiqueta("Valor de Repasse (R$)")
    @Tabelavel
    @Monetario
    private BigDecimal valorRepasse;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Qtde de Dias Para Prorrogação")
    private Date qtdeDiaPro;
    @Etiqueta("Qtde de Dias Para Prestação de Contas")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Integer qntDiaPrestCont;
    @Etiqueta("Periodicidade")
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Periodicidade periodicidade;
    @ManyToOne
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @ManyToOne
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;
    //    @Invisivel
//    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Etiqueta(value = "Andamentos")
//    @Tabelavel(campoSelecionado = false)
//    private List<AndamentoConvenioReceita> andamentoConvenioReceitas;
    @Invisivel
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Intervenientes")
    @Tabelavel(campoSelecionado = false)
    private List<ConvenioRecInterveniente> convenioRecIntervenientes;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Tramites")
    @Tabelavel(campoSelecionado = false)
    private List<TramiteConvenioRec> tramitesConvenioRec;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Unidades")
    @Tabelavel(campoSelecionado = false)
    private List<ConvenioReceitaUnidade> convenioReceitaUnidades;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Contas Financeiras")
    @Tabelavel(campoSelecionado = false)
    private List<ConvenioReceitaSubConta> convenioReceitaSubContas;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Aditivos")
    @Tabelavel(campoSelecionado = false)
    private List<AditivosConvenioReceita> aditivosConvenioReceitas;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Contas de Receita")
    @Tabelavel(campoSelecionado = false)
    private List<ConvenioRecConta> convenioRecConta;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta(value = "Unidades de Medida")
    @Tabelavel(campoSelecionado = false)
    private List<ConvenioReceitaUnidMedida> convenioRecUnidMedida;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Prestação de Contas")
    private List<PrestacaoContas> prestacaoContas;
    @OneToMany(mappedBy = "convenioReceita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Liberação")
    private List<ConvenioReceitaLiberacao> convenioReceitaLiberacoes;
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
    @Invisivel
    private Long criadoEm;
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
    @ManyToOne
    private EmendaGoverno emendaGoverno;
    @ManyToOne
    private ProgramaGoverno programaGoverno;

    public ConvenioReceita() {
        criadoEm = System.nanoTime();
        valorContrapartida = BigDecimal.ZERO;
        valorConvenio = BigDecimal.ZERO;
        valorRepasse = BigDecimal.ZERO;
        valorContrapartidaAditivada = BigDecimal.ZERO;
        valorConvenioAditivado = BigDecimal.ZERO;
        valorRepasseAditivado = BigDecimal.ZERO;
        convenioRecIntervenientes = new ArrayList<>();
        tramitesConvenioRec = new ArrayList<>();
        convenioReceitaUnidades = new ArrayList<>();
        convenioReceitaSubContas = new ArrayList<>();
        aditivosConvenioReceitas = new ArrayList<>();
        convenioReceitaLiberacoes = new ArrayList<>();
        convenioRecConta = new ArrayList<>();
        convenioRecUnidMedida = new ArrayList<>();
        prestacaoContas = new ArrayList<>();
        vigenciaInicial = new Date();
    }

    public ConvenioReceita(String numero, String nomeConvenio, TipoConvenioReceita tipoConvenioReceita, EntidadeRepassadora entidadeRepassadora, Entidade unidadeConvenente, String objetoConvenio, Date vigenciaInicial, Date dataAssinatura, Date dataPrestacaoContas, Date vigenciaFinal, String numeroTermo, String numeroDiarioOficial, Date dataDiarioOficial, String codigoCadTce, OrigemRecurso origemRecurso, DestinacaoRecurso destinacaoRecurso, BigDecimal valorConvenio, BigDecimal valorContrapartida, Date qntDiaPro, Integer qntDiaPrestCont, Periodicidade periodicidade, List<ConvenioRecInterveniente> convenioRecIntervenientes, List<TramiteConvenioRec> tramitesConvenioRec, List<ConvenioReceitaUnidade> convenioReceitaUnidades, List<ConvenioReceitaSubConta> convenioReceitaSubContas, List<AditivosConvenioReceita> aditivosConvenioReceitas, List<ConvenioRecConta> convenioRecConta, List<ConvenioReceitaUnidMedida> convenioRecUnidMedida, BigDecimal valorRepasse, List<PrestacaoContas> prestacaoContas) {
        criadoEm = System.nanoTime();
        this.numero = numero;
        this.nomeConvenio = nomeConvenio;
        this.tipoConvenioReceita = tipoConvenioReceita;
        this.entidadeRepassadora = entidadeRepassadora;
        this.entidadeConvenente = unidadeConvenente;
        this.objetoConvenio = objetoConvenio;
        this.vigenciaInicial = vigenciaInicial;
        this.dataAssinatura = dataAssinatura;
        this.dataPrestacaoContas = dataPrestacaoContas;
        this.vigenciaFinal = vigenciaFinal;
        this.numeroTermo = numeroTermo;
        this.numeroDiarioOficial = numeroDiarioOficial;
        this.dataDiarioOficial = dataDiarioOficial;
        this.codigoCadTce = codigoCadTce;
        this.origemRecurso = origemRecurso;
        this.destinacaoRecurso = destinacaoRecurso;
        this.valorConvenio = valorConvenio;
        this.valorContrapartida = valorContrapartida;
        this.qtdeDiaPro = qntDiaPro;
        this.qntDiaPrestCont = qntDiaPrestCont;
        this.periodicidade = periodicidade;
        this.convenioRecIntervenientes = convenioRecIntervenientes;
        this.tramitesConvenioRec = tramitesConvenioRec;
        this.convenioReceitaUnidades = convenioReceitaUnidades;
        this.convenioReceitaSubContas = convenioReceitaSubContas;
        this.aditivosConvenioReceitas = aditivosConvenioReceitas;
        this.convenioRecConta = convenioRecConta;
        this.prestacaoContas = prestacaoContas;
        this.convenioRecUnidMedida = convenioRecUnidMedida;
        this.valorRepasse = valorRepasse;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoCadTce() {
        return codigoCadTce;
    }

    public void setCodigoCadTce(String codigoCadTce) {
        this.codigoCadTce = codigoCadTce;
    }

    public Date getDataDiarioOficial() {
        return dataDiarioOficial;
    }

    public void setDataDiarioOficial(Date dataDiarioOficial) {
        this.dataDiarioOficial = dataDiarioOficial;
    }

    public DestinacaoRecurso getDestinacaoRecurso() {
        return destinacaoRecurso;
    }

    public void setDestinacaoRecurso(DestinacaoRecurso destinacaoRecurso) {
        this.destinacaoRecurso = destinacaoRecurso;
    }

    public EntidadeRepassadora getEntidadeRepassadora() {
        return entidadeRepassadora;
    }

    public void setEntidadeRepassadora(EntidadeRepassadora entidadeRepassadora) {
        this.entidadeRepassadora = entidadeRepassadora;
    }

    public String getNomeConvenio() {
        return nomeConvenio;
    }

    public void setNomeConvenio(String nomeConvenio) {
        this.nomeConvenio = nomeConvenio;
    }

    public String getNumeroDiarioOficial() {
        return numeroDiarioOficial;
    }

    public void setNumeroDiarioOficial(String numeroDiarioOficial) {
        this.numeroDiarioOficial = numeroDiarioOficial;
    }

    public String getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(String numeroTermo) {
        this.numeroTermo = numeroTermo;
    }

    public String getObjetoConvenio() {
        return objetoConvenio;
    }

    public void setObjetoConvenio(String objetoConvenio) {
        this.objetoConvenio = objetoConvenio;
    }

    public OrigemRecurso getOrigemRecurso() {
        return origemRecurso;
    }

    public void setOrigemRecurso(OrigemRecurso origemRecurso) {
        this.origemRecurso = origemRecurso;
    }

    public TipoConvenioReceita getTipoConvenioReceita() {
        return tipoConvenioReceita;
    }

    public void setTipoConvenioReceita(TipoConvenioReceita tipoConvenioReceita) {
        this.tipoConvenioReceita = tipoConvenioReceita;
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

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Date getQtdeDiaPro() {
        return qtdeDiaPro;
    }

    public void setQtdeDiaPro(Date qtdeDiaPro) {
        this.qtdeDiaPro = qtdeDiaPro;
    }

    public Integer getQntDiaPrestCont() {
        return qntDiaPrestCont;
    }

    public void setQntDiaPrestCont(Integer qntDiaPrestCont) {
        this.qntDiaPrestCont = qntDiaPrestCont;
    }

    public List<ConvenioRecInterveniente> getConvenioRecIntervenientes() {
        return convenioRecIntervenientes;
    }

    public void setConvenioRecIntervenientes(List<ConvenioRecInterveniente> convenioRecIntervenientes) {
        this.convenioRecIntervenientes = convenioRecIntervenientes;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public Date getDataPrestacaoContas() {
        return dataPrestacaoContas;
    }

    public void setDataPrestacaoContas(Date dataPrestacaoContas) {
        this.dataPrestacaoContas = dataPrestacaoContas;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<TramiteConvenioRec> getTramitesConvenioRec() {
        return tramitesConvenioRec;
    }

    public void setTramitesConvenioRec(List<TramiteConvenioRec> tramitesConvenioRec) {
        this.tramitesConvenioRec = tramitesConvenioRec;
    }

    public Periodicidade getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(Periodicidade periodicidade) {
        this.periodicidade = periodicidade;
    }

    public List<ConvenioReceitaUnidade> getConvenioReceitaUnidades() {
        return convenioReceitaUnidades;
    }

    public void setConvenioReceitaUnidades(List<ConvenioReceitaUnidade> convenioReceitaUnidades) {
        this.convenioReceitaUnidades = convenioReceitaUnidades;
    }

    public List<ConvenioReceitaSubConta> getConvenioReceitaSubContas() {
        return convenioReceitaSubContas;
    }

    public void setConvenioReceitaSubContas(List<ConvenioReceitaSubConta> convenioReceitaSubContas) {
        this.convenioReceitaSubContas = convenioReceitaSubContas;
    }

    public List<AditivosConvenioReceita> getAditivosConvenioReceitas() {
        return aditivosConvenioReceitas;
    }

    public void setAditivosConvenioReceitas(List<AditivosConvenioReceita> aditivosConvenioReceitas) {
        this.aditivosConvenioReceitas = aditivosConvenioReceitas;
    }

    public List<ConvenioRecConta> getConvenioRecConta() {
        return convenioRecConta;
    }

    public void setConvenioRecConta(List<ConvenioRecConta> convenioRecConta) {
        this.convenioRecConta = convenioRecConta;
    }

    public List<ConvenioReceitaUnidMedida> getConvenioRecUnidMedida() {
        return convenioRecUnidMedida;
    }

    public void setConvenioRecUnidMedida(List<ConvenioReceitaUnidMedida> convenioRecUnidMedida) {
        this.convenioRecUnidMedida = convenioRecUnidMedida;
    }

    public BigDecimal getValorRepasse() {
        return valorRepasse;
    }

    public void setValorRepasse(BigDecimal valorRepasse) {
        this.valorRepasse = valorRepasse;
    }

    public List<PrestacaoContas> getPrestacaoContas() {
        return prestacaoContas;
    }

    public void setPrestacaoContas(List<PrestacaoContas> prestacaoContas) {
        this.prestacaoContas = prestacaoContas;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public List<ConvenioReceitaLiberacao> getConvenioReceitaLiberacoes() {
        return convenioReceitaLiberacoes;
    }

    public void setConvenioReceitaLiberacoes(List<ConvenioReceitaLiberacao> convenioReceitaLiberacoes) {
        this.convenioReceitaLiberacoes = convenioReceitaLiberacoes;
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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Entidade getEntidadeConvenente() {
        return entidadeConvenente;
    }

    public void setEntidadeConvenente(Entidade entidadeConvenente) {
        this.entidadeConvenente = entidadeConvenente;
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
        if (numero != null && nomeConvenio != null) {
            return numero + " - " + nomeConvenio;
        }
        return nomeConvenio;
    }

    public String toStringAutoComplete() {
        String retorno = "";
        if (numero != null) {
            retorno += numero + " - ";
        }
        if (numeroTermo != null) {
            retorno += numeroTermo + " - ";
        }
        if (nomeConvenio != null) {
            retorno += nomeConvenio;
        }
        return retorno;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public EmendaGoverno getEmendaGoverno() {
        return emendaGoverno;
    }

    public void setEmendaGoverno(EmendaGoverno emendaGoverno) {
        this.emendaGoverno = emendaGoverno;
    }

    public ProgramaGoverno getProgramaGoverno() {
        return programaGoverno;
    }

    public void setProgramaGoverno(ProgramaGoverno programaGoverno) {
        this.programaGoverno = programaGoverno;
    }
}
