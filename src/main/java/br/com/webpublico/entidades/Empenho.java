/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class Empenho extends SuperEntidade implements Serializable, EntidadeContabilComValor, IManadRegistro, IGeraContaAuxiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    @Tabelavel(campoSelecionado = false, TIPOCAMPO = Tabelavel.TIPOCAMPO.NUMERO_ORDENAVEL)
    @Pesquisavel
    @ErroReprocessamentoContabil
    private String numero;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private Date dataEmpenho;

    @ManyToOne
    @Etiqueta("Evento Contábil")
    @Tabelavel
    @Pesquisavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabil;

    @Obrigatorio
    @Etiqueta("Tipo de Empenho")
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private TipoEmpenho tipoEmpenho;

    @Obrigatorio
    @Etiqueta("Classificação Orçamentária")
    @ManyToOne
    @Tabelavel
    @ErroReprocessamentoContabil
    private FonteDespesaORC fonteDespesaORC;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Extensão da Fonte de Recurso")
    private ExtensaoFonteRecurso extensaoFonteRecurso;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Pessoa")
    @ErroReprocessamentoContabil
    private Pessoa fornecedor;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private ClasseCredor classeCredor;

    @Etiqueta("Valor (R$)")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    @ErroReprocessamentoContabil
    private BigDecimal valor;

    @Etiqueta("Saldo (R$)")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Monetario
    private BigDecimal saldo;

    @Etiqueta("Saldo Disponível (R$)")
    @Obrigatorio
    @Monetario
    private BigDecimal saldoDisponivel;

    @Etiqueta("Número Original")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String numeroOriginal;

    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private DespesaORC despesaORC;

    @Etiqueta("Usuário")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Unidade Organizacional")
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;

    @Etiqueta("Histórico Padrão")
    @ManyToOne
    @ErroReprocessamentoContabil
    private HistoricoContabil historicoContabil;

    @Invisivel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    private MovimentoDespesaORC movimentoDespesaORC;

    @Invisivel
    @OneToMany(mappedBy = "empenho")
    private List<Liquidacao> liquidacoes;

    @Obrigatorio
    @Etiqueta("Histórico")
    @Tabelavel(campoSelecionado = false)
    private String complementoHistorico;

    @Invisivel
    @OneToMany(mappedBy = "empenho")
    private List<EmpenhoEstorno> empenhoEstornos;

    @Invisivel
    @Etiqueta("Solicitação de Empenho")
    @OneToOne(mappedBy = "empenho")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private SolicitacaoEmpenho solicitacaoEmpenho;

    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @ReprocessamentoContabil
    private CategoriaOrcamentaria categoriaOrcamentaria;

    @Enumerated(EnumType.STRING)
    private TipoRestosProcessado tipoRestosProcessados;

    @Enumerated(EnumType.STRING)
    private TipoRestosInscritos tipoRestosInscritos;

    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta(value = "Sub-Tipo de Despesa")
    private SubTipoDespesa subTipoDespesa;

    @OneToOne
    private Empenho empenho;

    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Exercicio exercicio;

    @Etiqueta("Exercício Original")
    @Invisivel
    @ManyToOne
    private Exercicio exercicioOriginal;

    private Boolean importado;
    @Etiqueta("Tipo Integração")
    @Enumerated(EnumType.STRING)
    private TipoEmpenhoIntegracao tipoEmpenhoIntegracao;

    @Etiqueta("Dívida Pública")
    @Invisivel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private DividaPublica dividaPublica;

    @Etiqueta("Parcela da Dívida Pública")
    @Invisivel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    private ParcelaDividaPublica parcelaDividaPublica;

    @Etiqueta("Operação de Crédito")
    @Invisivel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private DividaPublica operacaoDeCredito;

    @Etiqueta("Convênio de Despesa")
    @Invisivel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private ConvenioDespesa convenioDespesa;

    @Etiqueta("Convênio de Receita")
    @Invisivel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private ConvenioReceita convenioReceita;

    @OneToMany(mappedBy = "empenho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoEmpenho> desdobramentos;

    @OneToMany(mappedBy = "empenho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<EmpenhoObrigacaoPagar> obrigacoesPagar;

    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta(value = "Folha de Pagamento")
    private FolhaDePagamento folhaDePagamento;

    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta(value = "Proposta de Concessão de Diária")
    private PropostaConcessaoDiaria propostaConcessaoDiaria;

    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;

    @Etiqueta("Contrato")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Contrato contrato;

    @ManyToOne
    private ExecucaoProcesso execucaoProcesso;

    @Obrigatorio
    @Etiqueta("Tipo de Despesa")
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private TipoContaDespesa tipoContaDespesa;

    private String historicoNota;
    private String historicoRazao;
    private Boolean semDisponibilidadeFinanceira;
    @Invisivel
    private Boolean transportado;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Modalidade")
    @Enumerated(EnumType.STRING)
    private ModalidadeLicitacaoEmpenho modalidadeLicitacao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Reconhecimento")
    private TipoReconhecimentoObrigacaoPagar tipoReconhecimento;

    @Etiqueta("Saldo Obrigação a Pagar Antes Empenho")
    @Monetario
    private BigDecimal saldoObrigacaoPagarAntesEmp;

    @Etiqueta("Saldo Obrigação a Pagar Depois Eempenho")
    @Monetario
    private BigDecimal saldoObrigacaoPagarDepoisEmp;

    @ManyToOne
    private ContaDespesa contaDespesa;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    private String codigoContaTCE;
    @Version
    private Long versao;
    @ManyToOne
    @Etiqueta("Reconhecimento de Dívida do Exercício")
    private ReconhecimentoDivida reconhecimentoDivida;
    @OneToMany(mappedBy = "empenho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmendaEmpenho> emendas;
    @ManyToOne
    private CodigoCO codigoCO;
    @ManyToOne
    private ItemIntegracaoRHContabil itemIntegracaoRHContabil;

    public Empenho() {
        empenhoEstornos = Lists.newArrayList();
        liquidacoes = Lists.newArrayList();
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        saldoObrigacaoPagarAntesEmp = new BigDecimal(BigInteger.ZERO);
        saldoObrigacaoPagarDepoisEmp = new BigDecimal(BigInteger.ZERO);
        saldoDisponivel = new BigDecimal(BigInteger.ZERO);
        importado = false;
        desdobramentos = Lists.newArrayList();
        obrigacoesPagar = Lists.newArrayList();
        transportado = false;
        emendas = Lists.newArrayList();
    }

    public Empenho(Long id, Date data, String numero, BigDecimal valor, BigDecimal saldo, Pessoa fornecedor, TipoEmpenho tipoEmpenho) {
        this.id = id;
        this.numero = numero;
        this.dataEmpenho = data;
        this.fornecedor = fornecedor;
        this.valor = valor;
        this.saldo = saldo;
        this.tipoEmpenho = tipoEmpenho;
    }

    public Empenho(Long id, String numero, Date dataEmpenho, BigDecimal valor, BigDecimal saldo, Pessoa fornecedor) {
        this.id = id;
        this.numero = numero;
        this.dataEmpenho = dataEmpenho;
        this.fornecedor = fornecedor;
        this.valor = valor;
        this.saldo = saldo;
    }

    public List<EmpenhoObrigacaoPagar> getObrigacoesPagar() {
        return obrigacoesPagar;
    }

    public void setObrigacoesPagar(List<EmpenhoObrigacaoPagar> obrigacoesPagar) {
        this.obrigacoesPagar = obrigacoesPagar;
    }

    public BigDecimal getSaldoObrigacaoPagarAntesEmp() {
        if (saldoObrigacaoPagarAntesEmp == null) {
            return BigDecimal.ZERO;
        }
        return saldoObrigacaoPagarAntesEmp;
    }

    public void setSaldoObrigacaoPagarAntesEmp(BigDecimal saldoObrigacaoPagarAntesEmp) {
        this.saldoObrigacaoPagarAntesEmp = saldoObrigacaoPagarAntesEmp;
    }

    public BigDecimal getSaldoObrigacaoPagarDepoisEmp() {
        if (saldoObrigacaoPagarDepoisEmp == null) {
            return BigDecimal.ZERO;
        }
        return saldoObrigacaoPagarDepoisEmp;
    }

    public void setSaldoObrigacaoPagarDepoisEmp(BigDecimal saldoObrigacaoPagarDepoisEmp) {
        this.saldoObrigacaoPagarDepoisEmp = saldoObrigacaoPagarDepoisEmp;
    }

    public TipoReconhecimentoObrigacaoPagar getTipoReconhecimento() {
        return tipoReconhecimento;
    }

    public void setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar tipoReconhecimento) {
        this.tipoReconhecimento = tipoReconhecimento;
    }

    public Empenho(Long id) {
        this.id = id;
    }

    public ParcelaDividaPublica getParcelaDividaPublica() {
        return parcelaDividaPublica;
    }

    public void setParcelaDividaPublica(ParcelaDividaPublica parcelaDividaPublica) {
        this.parcelaDividaPublica = parcelaDividaPublica;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public List<Liquidacao> getLiquidacoes() {
        return liquidacoes;
    }

    public void setLiquidacoes(List<Liquidacao> liquidacoes) {
        this.liquidacoes = liquidacoes;
    }

    public MovimentoDespesaORC getMovimentoDespesaORC() {
        return movimentoDespesaORC;
    }

    public void setMovimentoDespesaORC(MovimentoDespesaORC movimentoDespesaORC) {
        this.movimentoDespesaORC = movimentoDespesaORC;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public TipoEmpenho getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(TipoEmpenho tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public List<EmpenhoEstorno> getEmpenhoEstornos() {
        return empenhoEstornos;
    }

    public void setEmpenhoEstornos(List<EmpenhoEstorno> empenhoEstornos) {
        this.empenhoEstornos = empenhoEstornos;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public TipoRestosProcessado getTipoRestosProcessados() {
        return tipoRestosProcessados;
    }

    public void setTipoRestosProcessados(TipoRestosProcessado tipoRestosProcessados) {
        this.tipoRestosProcessados = tipoRestosProcessados;
    }

    public TipoRestosInscritos getTipoRestosInscritos() {
        return tipoRestosInscritos;
    }

    public void setTipoRestosInscritos(TipoRestosInscritos tipoRestosInscritos) {
        this.tipoRestosInscritos = tipoRestosInscritos;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getNumeroOriginal() {
        return numeroOriginal;
    }

    public void setNumeroOriginal(String numeroOriginal) {
        this.numeroOriginal = numeroOriginal;
    }

    public Exercicio getExercicioOriginal() {
        return exercicioOriginal;
    }

    public void setExercicioOriginal(Exercicio exercicioOriginal) {
        this.exercicioOriginal = exercicioOriginal;
    }

    public Boolean getImportado() {
        return importado;
    }

    public void setImportado(Boolean importado) {
        this.importado = importado;
    }

    public TipoEmpenhoIntegracao getTipoEmpenhoIntegracao() {
        return tipoEmpenhoIntegracao;
    }

    public void setTipoEmpenhoIntegracao(TipoEmpenhoIntegracao tipoEmpenhoIntegracao) {
        this.tipoEmpenhoIntegracao = tipoEmpenhoIntegracao;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public List<DesdobramentoEmpenho> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoEmpenho> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public BigDecimal getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(BigDecimal saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
    }

    public Boolean getSemDisponibilidadeFinanceira() {
        return semDisponibilidadeFinanceira;
    }

    public void setSemDisponibilidadeFinanceira(Boolean semDisponibilidadeFinanceira) {
        this.semDisponibilidadeFinanceira = semDisponibilidadeFinanceira;
    }

    public DividaPublica getOperacaoDeCredito() {
        return operacaoDeCredito;
    }

    public void setOperacaoDeCredito(DividaPublica operacaoDeCredito) {
        this.operacaoDeCredito = operacaoDeCredito;
    }

    @Override
    public String toString() {
        return "N° " + numero + " - " + fornecedor + " - " + new SimpleDateFormat("dd/MM/yyyy").format(dataEmpenho) + " - " + Util.formataValor(valor) + " - " + despesaORC.getContaDeDespesa().toString();
    }

    public String getComplementoContabil() {
        String toReturn = "";
        if (this.numero != null) {
            toReturn = toReturn + this.numero + ", ";
        }
        if (this.fornecedor != null) {
            toReturn = toReturn + this.fornecedor.getNome() + ", ";
        }
        if (this.unidadeOrganizacional != null) {
            toReturn = toReturn + this.unidadeOrganizacional.getDescricao() + ", ";
        }
        if (this.despesaORC != null) {
            toReturn = toReturn + this.despesaORC.getCodigoReduzido() + ", ";
        }
        if (this.fonteDespesaORC != null) {
            toReturn = toReturn + this.fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo() + " - " + this.fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursos().getDescricao() + ".";
        }
        return toReturn;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }

    public Boolean getTransportado() {
        return transportado != null ? transportado : false;
    }

    public void setTransportado(Boolean transportado) {
        this.transportado = transportado;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += getNumeroExercicioParaHistorico();
        }
        if (this.getPropostaConcessaoDiaria() != null) {
            historicoNota += " Concessão de Diária: " + this.getPropostaConcessaoDiaria().getTipoProposta().getDescricao() + ", " + this.getPropostaConcessaoDiaria().getCodigo() + "/" + Util.getAnoDaData(this.getPropostaConcessaoDiaria().getDataLancamento()) + ",";
        }
        if (this.getDividaPublica() != null) {
            if (this.getDividaPublica().getNaturezaDividaPublica().equals(NaturezaDividaPublica.PRECATORIO)) {
                historicoNota += " Precatório: " + this.getDividaPublica().getNumeroProtocolo() + "/" + Util.getAnoDaData(this.getDividaPublica().getDataHomologacao()) + ",";
            } else {
                historicoNota += " Dívida Pública: " + this.getDividaPublica().getNumero() + "/" + Util.getAnoDaData(this.getDividaPublica().getDataHomologacao()) + ",";
            }
        }
        if (this.getConvenioDespesa() != null) {
            historicoNota += " Convênio de Despesa: " + this.getConvenioDespesa().getNumConvenio() + "/" + Util.getAnoDaData(this.getConvenioDespesa().getDataVigenciaInicial()) + ",";
        }
        if (this.getConvenioReceita() != null) {
            historicoNota += " Convênio de Receita: " + this.getConvenioReceita().getNomeConvenio() + "/" + Util.getAnoDaData(this.getConvenioReceita().getVigenciaInicial()) + ",";
        }
        if (this.getDividaPublica() != null) {
            historicoNota += " " + this.getDividaPublica().getNaturezaDividaPublica().getDescricao() + ": " + this.getDividaPublica().getNumero() + "/" + Util.getAnoDaData(this.getDividaPublica().getDataInicio() == null ? this.dividaPublica.getDataHomologacao() : this.dividaPublica.getDataInicio()) + ",";
        }
        if (this.getContrato() != null) {
            if (this.getContrato().getLicitacao() != null) {
                historicoNota += " Contrato: " + this.getContrato().getNumeroProcesso() + "/" + this.getContrato().getLicitacao().getExercicio();
            } else {
                historicoNota += " Contrato: " + this.getContrato().getNumeroProcesso();
            }
        }
        if (this.getDespesaORC() != null) {
            historicoNota += " Elemento de Despesa: " + this.getDespesaORC() + "/" + this.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + ",";
        }
        if (isEmpenhoDeObrigacaoPagar() && this.getDesdobramentos() != null && !this.getDesdobramentos().isEmpty()) {
            for (DesdobramentoEmpenho desdobramento : this.getDesdobramentos()) {
                historicoNota += " Detalhamento: " + desdobramento.getConta().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getFonteDespesaORC() != null) {
            historicoNota += " Fonte de Recurso: " + this.getFonteDespesaORC().getDescricaoFonteDeRecurso().trim() + ",";
        }
        if (this.getFornecedor() != null) {
            historicoNota += " Pessoa: " + this.getFornecedor() + ",";
        }
        if (this.getClasseCredor() != null) {
            historicoNota += " Classe: " + this.getClasseCredor() + ",";
        }
        historicoNota = historicoNota + " " + complementoHistorico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public String getNumeroExercicioParaHistorico() {
        if (this.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            return "Empenho: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataEmpenho()) + ",";
        } else {
            return "Empenho: " + this.getEmpenho().getNumero() + "/" + Util.getAnoDaData(this.getEmpenho().getDataEmpenho()) + ",";
        }
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (!isEmpenhoDeObrigacaoPagar()) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
            }
        } else {
            for (DesdobramentoEmpenho desd : this.getDesdobramentos()) {
                if (desd.getEventoContabil() != null) {
                    if (desd.getEventoContabil().getClpHistoricoContabil() != null) {
                        historicoEvento += desd.getEventoContabil().getClpHistoricoContabil().toString() + " ";
                    }
                }
            }
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        try {
            gerarHistoricoNota();
            gerarHistoricoRazao();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getNumeroAnoCategoria() {
        try {
            return numero + "/" + exercicio.getAno() + " - " + DataUtil.getDataFormatada(dataEmpenho) + " - " + categoriaOrcamentaria.getDescricao();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public String getNumeroAnoCategoriaValor() {
        try {
            return numero + "/" + exercicio.getAno() + " - " + DataUtil.getDataFormatada(dataEmpenho) + " - " + categoriaOrcamentaria.getDescricao() + " - " + Util.formataValor(valor);
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + "/" + exercicio.getAno();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    public ModalidadeLicitacaoEmpenho getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacaoEmpenho modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public ExtensaoFonteRecurso getExtensaoFonteRecurso() {
        return extensaoFonteRecurso;
    }

    public void setExtensaoFonteRecurso(ExtensaoFonteRecurso extensaoFonteRecurso) {
        this.extensaoFonteRecurso = extensaoFonteRecurso;
    }

    public ContaDespesa getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(ContaDespesa contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public String getCodigoContaTCE() {
        return codigoContaTCE;
    }

    public void setCodigoContaTCE(String codigoContaTCE) {
        this.codigoContaTCE = codigoContaTCE;
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L050;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        HierarquiaOrganizacional unidade = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(this.getDataEmpenho(), this.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        HierarquiaOrganizacional orgao = ((ManadContabilFacade) facade).getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(this.getDataEmpenho(), unidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        AcaoPPA acaoPPA = this.getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L050.name(), conteudo);
        ManadUtil.criaLinha(2, orgao.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(3, unidade.getCodigoNo(), conteudo);
        ManadUtil.criaLinha(4, acaoPPA.getAcaoPrincipal().getFuncao().getCodigo(), conteudo);
        ManadUtil.criaLinha(5, acaoPPA.getAcaoPrincipal().getSubFuncao().getCodigo(), conteudo);
        ManadUtil.criaLinha(6, acaoPPA.getAcaoPrincipal().getPrograma().getCodigo(), conteudo);
        ManadUtil.criaLinha(7, acaoPPA.getAcaoPrincipal().getCodigo(), conteudo);
        ManadUtil.criaLinha(8, acaoPPA.getCodigo(), conteudo);
        ManadUtil.criaLinha(9, "", conteudo);
        ManadUtil.criaLinha(10, "", conteudo);
        ManadUtil.criaLinha(11, "", conteudo);
        ManadUtil.criaLinha(12, this.numero, conteudo);
        ManadUtil.criaLinha(13, ManadUtil.getDataSemBarra(this.dataEmpenho), conteudo);
        ManadUtil.criaLinha(14, ManadUtil.getValor(valor), conteudo);
        ManadUtil.criaLinha(15, "D", conteudo);
        ManadUtil.criaLinha(16, "", conteudo);
        ManadUtil.criaLinhaSemPipe(17, this.complementoHistorico, conteudo);
        ManadUtil.quebraLinha(conteudo);

    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "3": //FonteDespesaOrc
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarFonteDespesaOrc(fonteDespesaORC);
            case "4": //ProvisaoPPADespesa
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarProvisaoPPADespesa(despesaORC.getProvisaoPPADespesa());
            case "5"://Empenho
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarEmpenho(this);
            case "6"://FonteDeRecursos
            case "7"://FonteDeRecursos
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDestinacaoDeRecursos(fonteDespesaORC.getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao());
            case "9"://Pessoa
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarPessoa(fornecedor);
            case "12": //Conta de Despesa
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarConta(despesaORC.getProvisaoPPADespesa().getContaDeDespesa());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (codigoCO != null && contaDeDestinacao != null) {
            contaDeDestinacao.setCodigoCOEmenda(codigoCO.getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(), getContaDeDestinacao(), getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    getContaDeDestinacao(),
                    getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    getContaDeDestinacao(),
                    getContaDespesa(),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.NORMAL.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(unidadeOrganizacional,
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        getContaDeDestinacao(),
                        getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        exercicio.getAno(),
                        exercicio,
                        exercicio);
                }
                if (CategoriaOrcamentaria.RESTO.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        getContaDeDestinacao(),
                        getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        empenho.getExercicio().getAno(),
                        exercicio,
                        empenho.getExercicio());
                }
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (codigoCO != null && contaDeDestinacao != null) {
            contaDeDestinacao.setCodigoCOEmenda(codigoCO.getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    getContaDeDestinacao(),
                    (getContaDespesa().getCodigoSICONFI() != null ?
                        getContaDespesa().getCodigoSICONFI() :
                        getContaDespesa().getCodigo().replace(".", "")),
                    (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.NORMAL.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        getContaDeDestinacao(),
                        getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        exercicio.getAno(),
                        exercicio);
                }
                if (CategoriaOrcamentaria.RESTO.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        getContaDeDestinacao(),
                        getContaDespesa(),
                        (getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        empenho.getExercicio().getAno(),
                        exercicio);
                }
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    public String getCodigoExtensaoFonteRecursoAsString() {
        return getExtensaoFonteRecurso().getCodigo().toString();
    }

    public void validarTipoEmpenho(ValidacaoException va, String mensagem, BigDecimal valorMovimento, BigDecimal saldoEmpenho) {
        if (TipoEmpenho.ORDINARIO.equals(this.tipoEmpenho)
            && CategoriaOrcamentaria.NORMAL.equals(this.categoriaOrcamentaria)
            && saldoEmpenho.compareTo(valorMovimento) != 0) {
            va.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
    }

    public boolean isEmpenhoDeObrigacaoPagar() {
        return this.obrigacoesPagar != null && !this.obrigacoesPagar.isEmpty();
    }

    public boolean isEmpenhoRestoPagar() {
        return this.categoriaOrcamentaria != null && CategoriaOrcamentaria.RESTO.equals(this.categoriaOrcamentaria);
    }

    public boolean isEmpenhoNormal() {
        return this.categoriaOrcamentaria != null && CategoriaOrcamentaria.NORMAL.equals(this.categoriaOrcamentaria);
    }

    public boolean isEmpenhoNaoProcessado() {
        return TipoRestosProcessado.NAO_PROCESSADOS.equals(this.tipoRestosProcessados);
    }

    public boolean isEmpenhoProcessado() {
        return TipoRestosProcessado.PROCESSADOS.equals(this.tipoRestosProcessados);
    }

    public BigDecimal getValorTotalDetalhamento() {
        BigDecimal valor = BigDecimal.ZERO;
        for (DesdobramentoEmpenho desdobramento : desdobramentos) {
            valor = valor.add(desdobramento.getValor());
        }
        return valor;
    }

    public BigDecimal getSaldoTotalDetalhamento() {
        BigDecimal valor = BigDecimal.ZERO;
        for (DesdobramentoEmpenho desdobramento : desdobramentos) {
            valor = valor.add(desdobramento.getSaldo());
        }
        return valor;
    }

    public BigDecimal getValorLiquidoEmpenho() {
        BigDecimal valor = BigDecimal.ZERO;
        if (this.valor != null) {
            valor = this.valor.subtract(getValorTotalDetalhamento());
        }
        return valor;
    }

    public BigDecimal getSaldoDisponivelEmpenhoComObrigacao() {
        if (saldo != null
            && saldoObrigacaoPagarAntesEmp != null
            && saldoObrigacaoPagarDepoisEmp != null) {
            return this.saldo.subtract((this.saldoObrigacaoPagarAntesEmp.add(this.saldoObrigacaoPagarDepoisEmp)));
        }
        return BigDecimal.ZERO;
    }

    public void validarSaldoDisponivelPorContaDespesa(BigDecimal valor, Conta conta, ObrigacaoAPagar obrigacaoAPagar) {
        ValidacaoException ve = new ValidacaoException();
        for (DesdobramentoEmpenho desd : getDesdobramentos()) {
            if (desd.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoAPagar)) {
                if (desd.getConta().equals(conta) && valor.compareTo(desd.getSaldo()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do detalhamento deve ser menor ou igual ao saldo de: " + Util.formataValor(desd.getSaldo())
                        + " disponível para a conta: " + conta + ".");
                }
            }
        }
        ve.lancarException();
    }

    public boolean isEmpenhoIntegracaoDiaria() {
        return this.getPropostaConcessaoDiaria() != null;
    }

    public boolean isEmpenhoIntegracaoContrato() {
        return this.getContrato() != null;
    }

    public boolean isEmpenhoIntegracaoReconhecimentoDivida() {
        return this.getReconhecimentoDivida()!= null;
    }

    public boolean isEmpenhoIntegracaoExecucaoProcesso() {
        return this.getExecucaoProcesso()!= null;
    }

    public boolean isEmpenhoIntegracaoContratoObras() {
        return this.getContrato() != null && TipoContrato.OBRAS_ENGENHARIA.equals(this.getContrato().getTipoContrato());
    }

    public boolean isEmpenhoIntegracaoContratoServico() {
        return this.getContrato() != null && TipoContaDespesa.SERVICO_DE_TERCEIRO.equals(this.getTipoContaDespesa());
    }

    public ItemIntegracaoRHContabil getItemIntegracaoRHContabil() {
        return itemIntegracaoRHContabil;
    }

    public void setItemIntegracaoRHContabil(ItemIntegracaoRHContabil itemIntegracaoRHContabil) {
        this.itemIntegracaoRHContabil = itemIntegracaoRHContabil;
    }

    public BigDecimal getTotalObrigacaoPagar() {
        BigDecimal total = BigDecimal.ZERO;
        for (EmpenhoObrigacaoPagar op : this.getObrigacoesPagar()) {
            total = total.add(op.getObrigacaoAPagar().getValor());
        }
        return total;
    }

    public BigDecimal getSaldoTotalObrigacaoPagar() {
        BigDecimal total = BigDecimal.ZERO;
        for (EmpenhoObrigacaoPagar op : this.getObrigacoesPagar()) {
            total = total.add(op.getObrigacaoAPagar().getSaldo());
        }
        return total;
    }

    public void validarMesmoDetalhamentoEmListaPoObrigacaoPagar(DesdobramentoEmpenho desdSelecionado, ObrigacaoAPagar obrigacaoAPagar) {
        ValidacaoException ve = new ValidacaoException();
        for (DesdobramentoEmpenho desdEmLista : this.getDesdobramentos()) {
            if (!desdEmLista.equals(desdSelecionado)
                && desdEmLista.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoAPagar)
                && desdEmLista.getConta().equals(desdSelecionado.getConta())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" A conta " + desdSelecionado.getConta() + " já foi adicionada na lista para a obrigação: " + obrigacaoAPagar.getNumero() + ".");
            }
        }
        ve.lancarException();
    }

    public BigDecimal getValorTotalDesdobramentos() {
        BigDecimal valor = BigDecimal.ZERO;
        if (getDesdobramentos() != null && !getDesdobramentos().isEmpty()) {
            for (DesdobramentoEmpenho desdobramento : getDesdobramentos()) {
                valor = valor.add(desdobramento.getValor());
            }
        }
        return valor;
    }

    public BigDecimal getSaldoTotalDesdobramentos() {
        BigDecimal saldo = BigDecimal.ZERO;
        if (getDesdobramentos() != null && !getDesdobramentos().isEmpty()) {
            for (DesdobramentoEmpenho desdobramento : getDesdobramentos()) {
                saldo = saldo.add(desdobramento.getSaldo());
            }
        }
        return saldo;
    }

    public BigDecimal getValorTotalEmendas() {
        BigDecimal valor = BigDecimal.ZERO;
        if (getEmendas() != null && !getEmendas().isEmpty()) {
            for (EmendaEmpenho emendaEmpenho : getEmendas()) {
                valor = valor.add(emendaEmpenho.getValor());
            }
        }
        return valor;
    }

    public boolean hasDesdobramento(){
        return desdobramentos !=null && !desdobramentos.isEmpty();
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }

    public List<EmendaEmpenho> getEmendas() {
        return emendas;
    }

    public void setEmendas(List<EmendaEmpenho> emendas) {
        this.emendas = emendas;
    }

    public CodigoCO getCodigoCO() {
        return codigoCO;
    }

    public void setCodigoCO(CodigoCO codigoCO) {
        this.codigoCO = codigoCO;
    }
}
