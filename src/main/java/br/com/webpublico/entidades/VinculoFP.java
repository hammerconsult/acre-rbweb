package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.esocial.CategoriaTrabalhador;
import br.com.webpublico.enums.SituacaoVinculo;
import br.com.webpublico.enums.TipoContagemEspecial;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.util.anotacoes.*;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@CorEntidade(value = "#00FF00")
public class VinculoFP extends SuperEntidade implements Serializable, EntidadePagavelRH, Comparable<VinculoFP>, PossuidorHistorico, IHistoricoEsocial {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Matrícula")
    @ManyToOne
    @Tabelavel
    private MatriculaFP matriculaFP;
    @Pesquisavel
    @Etiqueta("Número de Contrato")
    private String numero;
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início da Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(TemporalType.DATE)
    private Date finalVigencia;
    @Etiqueta("Alteração de Vínculo")
    @Temporal(TemporalType.DATE)
    private Date alteracaoVinculo;
    @Pesquisavel
    @Etiqueta("CBO")
    @ManyToOne
    private CBO cbo;
    @ManyToOne
    @Etiqueta("Categoria do Trabalhador")
    private CategoriaTrabalhador categoriaTrabalhador;
    @Etiqueta("Sindicatos")
    @OrderBy("inicioVigencia desc")
    @OneToMany(mappedBy = "vinculoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SindicatoVinculoFP> sindicatosVinculosFPs;
    @Etiqueta("Natureza de Rendimento DIRF")
    @ManyToOne
    private NaturezaRendimento naturezaRendimento;
    @Etiqueta("Associações")
    @OrderBy("inicioVigencia desc")
    @OneToMany(mappedBy = "vinculoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssociacaoVinculoFP> associacaosVinculosFPs;
    @Etiqueta("Conta Corrente")
    @ManyToOne
    private ContaCorrenteBancaria contaCorrente;
    @Column(name = "sefip")
    @Etiqueta("SEFIP")
    private Boolean sefip;
    @ManyToOne
    @Invisivel
    private ProvimentoFP provimentoFP;
    @Invisivel
    @OneToMany(mappedBy = "vinculoFP")
    private List<FichaFinanceiraFP> fichasFinanceiraFP;
    @Invisivel
    @OrderBy("inicioVigencia desc")
    @OneToMany(mappedBy = "vinculoFP")
    private List<LotacaoOutrosVinculos> lotacaoOutrosVinculos;
    @Invisivel
    @Etiqueta("Lotação Funcional")
    @OneToMany(mappedBy = "vinculoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("inicioVigencia desc")
    private List<LotacaoFuncional> lotacaoFuncionals;
    @Enumerated(EnumType.STRING)
    private SituacaoVinculo situacaoVinculo;
    @OrderBy("inicioVigencia desc")
    @OneToMany(mappedBy = "vinculoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpcaoSalarialVinculoFP> opcaoSalarialVinculoFP;
    @OrderBy("inicioVigencia desc")
    @OneToMany(mappedBy = "vinculoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecursoDoVinculoFP> recursosDoVinculoFP;
    @Etiqueta("Isento de Previdência")
    private Boolean isentoPrevidencia;
    @Etiqueta("Isento de Imposto de Renda")
    private Boolean isentoIR;
    @Etiqueta("Data do Registro")
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Etiqueta("Data de Alteração")
    @Temporal(TemporalType.DATE)
    private Date dataAlteracao;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private VinculoFPHist vinculoFPHist;
    @Etiqueta("Descontar IR Multiplo Vinculo")
    private Boolean descontarIrMultiploVinculo;
    @Etiqueta("Não Acumular Multiplo Vínculo")
    private Boolean descontarIRRFMultiploVinculo;
    @Invisivel
    @Transient
    private String descricao;
    @Transient
    @Invisivel
    private transient boolean houveEstornoFerias = false;
    @Transient
    @Invisivel
    private FolhaCalculavel folha;
    @Invisivel
    @Transient
    private Integer ano;
    @Invisivel
    @Transient
    private Integer mes;
    @Invisivel
    @Transient
    private boolean primeiroContrato;
    @Transient
    private boolean calculandoRetroativo;
    @Transient
    private List<ItemValorPrevidencia> itensValorPrevidencia;
    @Invisivel
    @Transient
    private FichaFinanceiraFP ficha;
    @Etiqueta("Observação")
    private String observacaoAlterarLT;
    @Transient
    private List<EventoESocialDTO> eventosEsocial;
    @Enumerated(EnumType.STRING)
    private TipoCadastroInicialVinculoFP cadastroInicialEsocial;
    @Etiqueta("Servidor Não Efetivo")
    private Boolean servidorNaoEfetivo;

    @Enumerated
    @Transient
    private TipoOperacaoESocial tipoOperacaoESocial;

    public String getObservacaoAlterarLT() {
        return observacaoAlterarLT;
    }

    public void setObservacaoAlterarLT(String observacaoAlterarLT) {
        this.observacaoAlterarLT = observacaoAlterarLT;
    }

    public VinculoFP(Long id, MatriculaFP matriculaFP, String numero) {
        this.id = id;
        this.matriculaFP = matriculaFP;
        this.numero = numero;
    }

    public VinculoFP() {
        lotacaoFuncionals = new ArrayList<>();
        associacaosVinculosFPs = new ArrayList<>();
        itensValorPrevidencia = new ArrayList<>();
        fichasFinanceiraFP = new ArrayList<>();
        lotacaoOutrosVinculos = new ArrayList<>();
        opcaoSalarialVinculoFP = new ArrayList<>();
        recursosDoVinculoFP = new ArrayList<>();
        sindicatosVinculosFPs = new ArrayList<>();
    }

    public VinculoFP(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public VinculoFP(MatriculaFP matriculaFP, String numero) {
        this.matriculaFP = matriculaFP;
        this.numero = numero;
    }

    public VinculoFP(UnidadeOrganizacional unidadeOrganizacional, Date inicioVigencia, Date finalVigencia, MatriculaFP matriculaFP) {
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.matriculaFP = matriculaFP;
    }

    @Override
    public boolean isCalculandoRetroativo() {
        return calculandoRetroativo;
    }

    @Override
    public void setCalculandoRetroativo(boolean calculandoRetroativo) {
        this.calculandoRetroativo = calculandoRetroativo;
    }

    public TipoRegime getTipoRegime() {
        return null;
    }

    public boolean isHouveEstornoFerias() {
        return houveEstornoFerias;
    }

    public void setHouveEstornoFerias(boolean houveEstornoFerias) {
        this.houveEstornoFerias = houveEstornoFerias;
    }

    public Boolean getIsentoIR() {
        return isentoIR == null ? false : isentoIR;
    }

    public void setIsentoIR(Boolean isentoIR) {
        this.isentoIR = isentoIR;
    }

    public Boolean getIsentoPrevidencia() {
        return isentoPrevidencia == null ? Boolean.FALSE : isentoPrevidencia;
    }

    public void setIsentoPrevidencia(Boolean isentoPrevidencia) {
        this.isentoPrevidencia = isentoPrevidencia;
    }

    public List<ItemValorPrevidencia> getItensValorPrevidencia() {
        return itensValorPrevidencia;
    }

    public void setItensValorPrevidencia(List<? extends ItemValorPrevidencia> itensValorPrevidencia) {
        this.itensValorPrevidencia = (List<ItemValorPrevidencia>) itensValorPrevidencia;
    }

    public List<OpcaoSalarialVinculoFP> getOpcaoSalarialVinculoFP() {
        return opcaoSalarialVinculoFP;
    }

    public void setOpcaoSalarialVinculoFP(List<OpcaoSalarialVinculoFP> opcaoSalarialVinculoFP) {
        this.opcaoSalarialVinculoFP = opcaoSalarialVinculoFP;
    }

    public Date getAlteracaoVinculo() {
        return alteracaoVinculo;
    }

    public void setAlteracaoVinculo(Date alteracaoVinculo) {
        this.alteracaoVinculo = alteracaoVinculo;
    }

    public SituacaoVinculo getSituacaoVinculo() {
        return situacaoVinculo;
    }

    public void setSituacaoVinculo(SituacaoVinculo situacaoVinculo) {
        this.situacaoVinculo = situacaoVinculo;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public List<FichaFinanceiraFP> getFichasFinanceiraFP() {
        return fichasFinanceiraFP;
    }

    public void setFichasFinanceiraFP(List<FichaFinanceiraFP> fichasFinanceiraFP) {
        this.fichasFinanceiraFP = fichasFinanceiraFP;
    }

    public Boolean getDescontarIRRFMultiploVinculo() {
        return descontarIRRFMultiploVinculo;
    }

    public void setDescontarIRRFMultiploVinculo(Boolean descontarIRRFMultiploVinculo) {
        this.descontarIRRFMultiploVinculo = descontarIRRFMultiploVinculo;
    }

    public List<LotacaoOutrosVinculos> getLotacaoOutrosVinculos() {
        return lotacaoOutrosVinculos;
    }

    public void setLotacaoOutrosVinculos(List<LotacaoOutrosVinculos> lotacaoOutrosVinculos) {
        this.lotacaoOutrosVinculos = lotacaoOutrosVinculos;
    }

    public List<LotacaoFuncional> getLotacaoFuncionals() {
        return lotacaoFuncionals;
    }

    public void setLotacaoFuncionals(List<LotacaoFuncional> lotacaoFuncionals) {
        this.lotacaoFuncionals = lotacaoFuncionals;
    }

    public ContaCorrenteBancaria getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(ContaCorrenteBancaria contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    @Override
    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public void criarOrAtualizarAndAssociarHistorico(PossuidorHistorico original) {
        VinculoFPHist historico = temHistorico() ? this.vinculoFPHist : new VinculoFPHist();
        historico.setDataCadastro(new Date());
        historico.setInicioVigencia(original.getInicioVigencia());
        if (atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(original, historico)
            || atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(original)) {
            historico.setFinalVigencia(original.getFinalVigencia());
        }
        this.vinculoFPHist = historico;
    }

    @Override
    public void voltarHistorico() {
        if (temHistorico()) {
            setFinalVigencia(vinculoFPHist.getFinalVigencia());
        } else {
            setFinalVigencia(null);
        }
    }

    public boolean atualNaoTemFinalVigenciaAndOriginalTemFinalVigencia(PossuidorHistorico original) {
        return !this.temFinalVigencia() && original.temFinalVigencia();
    }

    public boolean atualTemFinalVigenciaAndFinalVigenciaAtualDiferenteFinalVigenciaOriginalOrHistoricoNaoTemId(PossuidorHistorico original, VinculoFPHist historico) {
        return this.temFinalVigencia() && finalVigenciaAtualDiferenteFinalVigenciaOriginal(original) || !historico.temId();
    }

    public boolean finalVigenciaAtualDiferenteFinalVigenciaOriginal(PossuidorHistorico original) {
        return !this.getFinalVigencia().equals(original.getFinalVigencia());
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getInicioVigenciaFormatado() {
        if (inicioVigencia != null) {
            SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
            return sf.format(inicioVigencia);
        }
        return "";
    }

    @Override
    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public CBO getCbo() {
        return cbo;
    }

    public void setCbo(CBO cbo) {
        this.cbo = cbo;
    }

    @Override
    public FolhaCalculavel getFolha() {
        return folha;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFolha(FolhaCalculavel folhaDePagamento) {
        this.folha = folhaDePagamento;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ContratoFP getContratoFP() {
        return null;
    }

    @Override
    public Integer getAno() {
        return ano;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAno(Integer ano) {
        this.ano = ano;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getMes() {
        return mes;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMes(Integer mes) {
        this.mes = mes;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FichaFinanceiraFP getFicha() {
        return ficha;
    }

    @Override
    public void setFicha(FichaFinanceiraFP ficha) {
        this.ficha = ficha;
    }

    @Override
    public boolean getPrimeiroContrato() {
        return primeiroContrato;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPrimeiroContrato(boolean primeiroContrato) {
        this.primeiroContrato = primeiroContrato;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<RecursoDoVinculoFP> getRecursosDoVinculoFP() {
        return recursosDoVinculoFP;
    }

    public void setRecursosDoVinculoFP(List<RecursoDoVinculoFP> recursosDoVinculoFP) {
        this.recursosDoVinculoFP = recursosDoVinculoFP;
    }

    public Boolean getSefip() {
        if (sefip == null) return Boolean.FALSE;
        return sefip;
    }

    public void setSefip(Boolean sefip) {
        this.sefip = sefip;
    }

    public List<SindicatoVinculoFP> getSindicatosVinculosFPs() {
        return sindicatosVinculosFPs;
    }

    public void setSindicatosVinculosFPs(List<SindicatoVinculoFP> sindicatosVinculosFPs) {
        this.sindicatosVinculosFPs = sindicatosVinculosFPs;
    }

    public NaturezaRendimento getNaturezaRendimento() {
        return naturezaRendimento;
    }

    public void setNaturezaRendimento(NaturezaRendimento naturezaRendimento) {
        this.naturezaRendimento = naturezaRendimento;
    }

    public List<AssociacaoVinculoFP> getAssociacaosVinculosFPs() {
        return associacaosVinculosFPs;
    }

    public void setAssociacaosVinculosFPs(List<AssociacaoVinculoFP> associacaosVinculosFPs) {
        this.associacaosVinculosFPs = associacaosVinculosFPs;
    }

    public Boolean getDescontarIrMultiploVinculo() {
        return descontarIrMultiploVinculo != null ? descontarIrMultiploVinculo : false;
    }

    public void setDescontarIrMultiploVinculo(Boolean descontarIrMultiploVinculo) {
        this.descontarIrMultiploVinculo = descontarIrMultiploVinculo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public VinculoFPHist getVinculoFPHist() {
        return vinculoFPHist;
    }

    public void setVinculoFPHist(VinculoFPHist vinculoFPHist) {
        this.vinculoFPHist = vinculoFPHist;
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
        if (descricao != null && !descricao.trim().isEmpty()) {
            return descricao;
        }
        return unidadeOrganizacional + " - " + matriculaFP;
    }

    public String getDescricao() {
        String matricula = "";
        String numeroMatricula = "";
        if (matriculaFP != null) {
            matricula = matriculaFP.getPessoa().getNome();
            numeroMatricula = matriculaFP.getMatricula();
        }
        return matricula + " - " + numeroMatricula + " - " + numero + " - " + unidadeOrganizacional;
    }

    @Override
    public Long getIdCalculo() {
        return this.id;
    }

    public DateTime getPrimeiroDiaDoMesCalculo() {
        DateTime primeiroDiaDoMesDoCalculo = new DateTime();
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withDayOfMonth(1);
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withHourOfDay(0);
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withMillisOfSecond(0);
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withMinuteOfHour(0);
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withSecondOfMinute(0);
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withMonthOfYear(mes);
        primeiroDiaDoMesDoCalculo = primeiroDiaDoMesDoCalculo.withYear(ano);
        return primeiroDiaDoMesDoCalculo;
    }

    public DateTime getUltimoDiaDoMesCalculo() {
        DateTime ultimoDiaDoMesDeCalculo = new DateTime();
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withHourOfDay(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withMillisOfSecond(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withMinuteOfHour(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withSecondOfMinute(0);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withMonthOfYear(mes);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withYear(ano);
        ultimoDiaDoMesDeCalculo = ultimoDiaDoMesDeCalculo.withDayOfMonth(ultimoDiaDoMesDeCalculo.dayOfMonth().getMaximumValue());
        return ultimoDiaDoMesDeCalculo;
    }

    @Override
    public int compareTo(VinculoFP vinculoFP) {
        try {
            return this.getMatriculaFP().compareTo(vinculoFP.getMatriculaFP());
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public Date getDataNascimento() throws NullPointerException {
        return this.getMatriculaFP().getDataNascimento();
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFPVigente(Date dataOperacao) {
        try {
            if (this.getRecursosDoVinculoFP() != null && !this.getRecursosDoVinculoFP().isEmpty()) {
                for (RecursoDoVinculoFP recursoDoVinculoFP : this.getRecursosDoVinculoFP()) {
                    if (recursoDoVinculoFP.getFinalVigencia() == null) {
                        return recursoDoVinculoFP;
                    }
                    if (new Interval(new DateTime(recursoDoVinculoFP.getInicioVigencia()), new DateTime(recursoDoVinculoFP.getFinalVigencia())).contains(new DateTime(dataOperacao))) {
                        return recursoDoVinculoFP;
                    }
                }
            }
            return null;
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public boolean temRecursoDoVinculoFP() {
        return !CollectionUtils.isEmpty(recursosDoVinculoFP);
    }

    public boolean temLotacaoFuncional() {
        return !CollectionUtils.isEmpty(lotacaoFuncionals);
    }

    public boolean temRecurso() {
        return !CollectionUtils.isEmpty(recursosDoVinculoFP);
    }

    public boolean temSindicatoVinculoFP() {
        return !CollectionUtils.isEmpty(sindicatosVinculosFPs);
    }

    public CategoriaTrabalhador getCategoriaTrabalhador() {
        return categoriaTrabalhador;
    }

    public void setCategoriaTrabalhador(CategoriaTrabalhador categoriaTrabalhador) {
        this.categoriaTrabalhador = categoriaTrabalhador;
    }

    @Override
    public boolean temHistorico() {
        return vinculoFPHist != null;
    }

    @Override
    public boolean temFinalVigencia() {
        return finalVigencia != null;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFPVigente() {
        if (temRecursoDoVinculoFP()) {
            ordenarRecursosDoVinculoFPPorInicioVigenciaDesc();
            return recursosDoVinculoFP.get(0);
        }
        return null;
    }

    public LotacaoFuncional getLotacaoFuncionalVigente() {
        if (temLotacaoFuncional()) {
            ordenarLotacoesFuncionalPorInicioVigenciaDesc();
            return lotacaoFuncionals.get(0);
        }
        return null;
    }

    public SindicatoVinculoFP getSindicatoVinculoFPVigente() {
        if (temSindicatoVinculoFP()) {
            ordenarSindicatosVinculoFPPorInicioVigenciaDesc();
            return sindicatosVinculosFPs.get(0);
        }
        return null;
    }

    public void ordenarRecursosDoVinculoFPPorInicioVigenciaDesc() {
        Collections.sort(recursosDoVinculoFP, new Comparator<RecursoDoVinculoFP>() {
            @Override
            public int compare(RecursoDoVinculoFP o1, RecursoDoVinculoFP o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void ordenarLotacoesFuncionalPorInicioVigenciaDesc() {
        Collections.sort(lotacaoFuncionals, new Comparator<LotacaoFuncional>() {
            @Override
            public int compare(LotacaoFuncional o1, LotacaoFuncional o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void ordenarSindicatosVinculoFPPorInicioVigenciaDesc() {
        Collections.sort(sindicatosVinculosFPs, new Comparator<SindicatoVinculoFP>() {
            @Override
            public int compare(SindicatoVinculoFP o1, SindicatoVinculoFP o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });
    }

    public void removerRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        if (recursosDoVinculoFP.contains(recursoDoVinculoFP)) {
            recursosDoVinculoFP.remove(recursoDoVinculoFP);
        }
    }

    public boolean todosRecursosDoVinculoFPEstaoPersistidos() {
        for (RecursoDoVinculoFP recursoDoVinculoFP : recursosDoVinculoFP) {
            if (!recursoDoVinculoFP.temId()) {
                return false;
            }
        }
        return true;
    }

    public LotacaoFuncional getLotacaoFuncionalPorHorarioContratoFP(HorarioContratoFP horarioContrato) {
        for (LotacaoFuncional lotacaoFuncional : lotacaoFuncionals) {
            if (lotacaoFuncional.temHorarioContratoFP(horarioContrato)) {
                return lotacaoFuncional;
            }
        }
        return null;
    }

    public void removerLotacaoFuncionalPorHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        LotacaoFuncional lotacaoFuncionalPorHorarioContratoFP = getLotacaoFuncionalPorHorarioContratoFP(horarioContratoFP);
        if (lotacaoFuncionalPorHorarioContratoFP != null && lotacaoFuncionals.contains(lotacaoFuncionalPorHorarioContratoFP)) {
            lotacaoFuncionals.remove(lotacaoFuncionalPorHorarioContratoFP);
        }
    }

    public List<EventoESocialDTO> getEventosEsocial() {
        return eventosEsocial;
    }

    public void setEventosEsocial(List<EventoESocialDTO> eventosEsocial) {
        this.eventosEsocial = eventosEsocial;
    }

    public boolean temSituacaoVinculoFP() {
        return situacaoVinculo != null;
    }

    public TipoCadastroInicialVinculoFP getCadastroInicialEsocial() {
        return cadastroInicialEsocial;
    }

    public void setCadastroInicialEsocial(TipoCadastroInicialVinculoFP cadastroInicialEsocial) {
        this.cadastroInicialEsocial = cadastroInicialEsocial;
    }

    public NivelEscolaridade getNivelEscolaridadePessoa() {
        PessoaFisica pessoa = matriculaFP.getPessoa();
        if (!pessoa.temNivelEscolaridade()) {
            throw new ExcecaoNegocioGenerica("Não foi encontrado nível de escolaridade da pessoa: " + pessoa);
        }
        return pessoa.getNivelEscolaridade();
    }

    public Integer getCarreira() {
        return TipoContagemEspecial.NAO.getCodigoAtuarial();
    }

    @Override
    public Cargo getCargo() {
        return null;
    }

    @Override
    public String getDescricaoCompleta() {
        return this.numero + "/" + this.getMatriculaFP().getMatricula() + " - " + this.getMatriculaFP().getPessoa().toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }

    public enum TipoCadastroInicialVinculoFP {
        SIM("Sim"),
        NAO("Não");

        private String descricao;

        TipoCadastroInicialVinculoFP(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public String getMatriculaVinculo() {
        return matriculaFP.getMatricula() + "/" + numero;
    }

    public Boolean getServidorNaoEfetivo() {
        return servidorNaoEfetivo == null ? false : servidorNaoEfetivo;
    }

    public void setServidorNaoEfetivo(Boolean servidorNaoEfetivo) {
        this.servidorNaoEfetivo = servidorNaoEfetivo;
    }

    public TipoOperacaoESocial getTipoOperacaoESocial() {
        return tipoOperacaoESocial;
    }

    public void setTipoOperacaoESocial(TipoOperacaoESocial tipoOperacaoESocial) {
        this.tipoOperacaoESocial = tipoOperacaoESocial;
    }
}
