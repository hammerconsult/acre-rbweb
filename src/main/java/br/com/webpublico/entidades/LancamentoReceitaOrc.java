/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.BloqueioMovimentoContabil;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Receita Realizada")
public class LancamentoReceitaOrc extends SuperEntidade implements EntidadeContabil, BloqueioMovimentoContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private Date dataLancamento;
    @Etiqueta("Data de Conciliação")
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    private Date dataConciliacao;
    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    @ErroReprocessamentoContabil
    private String numero;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    private OperacaoReceita operacaoReceitaRealizada;
    @Etiqueta("Conta de Receita")
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @ErroReprocessamentoContabil
    private ReceitaLOA receitaLOA;
    @Tabelavel
    @Etiqueta("Conta de Receita")
    @Transient
    /* Atributo transient para lista a descrição da conta receita*/
    private String receitaLOATabela;
    @Transient
    @Tabelavel
    @Etiqueta("Tipo Conta de Receita")
    /* Tipo credito transient para lista*/
    private String tiposCreditos;
    @ManyToOne
    @Tabelavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @ErroReprocessamentoContabil
    @Etiqueta("Conta Financeira")
    private SubConta subConta;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Pessoa")
    @Pesquisavel
    @ErroReprocessamentoContabil
    private Pessoa pessoa;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @ErroReprocessamentoContabil
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    @ErroReprocessamentoContabil
    private ClasseCredor classeCredor;
    @Enumerated(EnumType.STRING)
    @ErroReprocessamentoContabil
    private TipoOperacaoORC tipoOperacao;
    @ErroReprocessamentoContabil
    @Etiqueta("Histórico")
    @Obrigatorio
    private String complemento;
    @ErroReprocessamentoContabil
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;
    @OneToMany(mappedBy = "lancReceitaOrc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LancReceitaFonte> lancReceitaFonte;
    @ManyToOne
    @ErroReprocessamentoContabil
    private ConvenioReceita convenioReceita;
    @ManyToOne
    @ErroReprocessamentoContabil
    private DividaPublica dividaPublica;
    @ManyToOne
    //Fonte utilizada para geração de saldo da dívida pública
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private UnidadeOrganizacional UnidadeOrganizacionalAdm;
    @ManyToOne(cascade = CascadeType.ALL)
    private LoteIntegracaoTributarioContabil integracaoTribCont;
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Lote")
    @Pesquisavel
    private String lote;
    @Pesquisavel
    @Etiqueta("Integração")
    private Boolean receitaDeIntegracao;
    @Transient
    @Invisivel
    private BigDecimal valorEstorno;
    private String uuid;

    @Obrigatorio
    @Etiqueta("Data de Referência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataReferencia;
    @ManyToOne
    private Identificador identificador;

    public LancamentoReceitaOrc() {
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        valorEstorno = BigDecimal.ZERO;
        lancReceitaFonte = new ArrayList<LancReceitaFonte>();
        receitaDeIntegracao = Boolean.FALSE;
        uuid = UUID.randomUUID().toString();
    }

    public LancamentoReceitaOrc(String numero, ReceitaLOA receitaLOA, Date dataLancamento, Pessoa pessoa, ClasseCredor classeCredor, BigDecimal valor, SubConta subConta, TipoOperacaoORC tipoOperacao, String complemento, BigDecimal saldo, List<LancReceitaFonte> lancReceitaFonte, ConvenioReceita convenioReceita, DividaPublica dividaPublica, UnidadeOrganizacional unidadeOrganizacional) {
        this.numero = numero;
        this.receitaLOA = receitaLOA;
        this.dataLancamento = dataLancamento;
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
        this.valor = valor;
        this.subConta = subConta;
        this.tipoOperacao = tipoOperacao;
        this.complemento = complemento;
        this.saldo = saldo;
        this.lancReceitaFonte = lancReceitaFonte;
        this.convenioReceita = convenioReceita;
        this.dividaPublica = dividaPublica;
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public LancamentoReceitaOrc(LancamentoReceitaOrc lancamentoReceitaOrc) {
        this.setId(lancamentoReceitaOrc.getId());
        this.setDataLancamento(lancamentoReceitaOrc.getDataLancamento());
        this.setDataConciliacao(lancamentoReceitaOrc.getDataConciliacao());
        this.setNumero(lancamentoReceitaOrc.getNumero());
        this.setOperacaoReceitaRealizada(lancamentoReceitaOrc.getOperacaoReceitaRealizada());
        this.setValor(lancamentoReceitaOrc.getValor());
        this.setReceitaLOATabela(lancamentoReceitaOrc.getReceitaLOA().getContaDeReceita().toString());
        this.setTiposCreditos(((ContaReceita) lancamentoReceitaOrc.getReceitaLOA().getContaDeReceita()).getTiposCredito().getDescricao());
    }

    public String getTiposCreditos() {
        return tiposCreditos;
    }

    public void setTiposCreditos(String tiposCreditos) {
        this.tiposCreditos = tiposCreditos;
    }

    public String getReceitaLOATabela() {
        return receitaLOATabela;
    }

    public void setReceitaLOATabela(String receitaLOATabela) {
        this.receitaLOATabela = receitaLOATabela;
    }

    public BigDecimal getValorEstorno() {
        return valorEstorno;
    }

    public void setValorEstorno(BigDecimal valorEstorno) {
        this.valorEstorno = valorEstorno;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public List<LancReceitaFonte> getLancReceitaFonte() {
        return lancReceitaFonte;
    }

    public void setLancReceitaFonte(List<LancReceitaFonte> lancReceitaFonte) {
        this.lancReceitaFonte = lancReceitaFonte;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public TipoOperacaoORC getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoORC tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }


    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return UnidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional UnidadeOrganizacionalAdm) {
        this.UnidadeOrganizacionalAdm = UnidadeOrganizacionalAdm;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public LoteIntegracaoTributarioContabil getIntegracaoTribCont() {
        return integracaoTribCont;
    }

    public void setIntegracaoTribCont(LoteIntegracaoTributarioContabil integracaoTribCont) {
        this.integracaoTribCont = integracaoTribCont;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Boolean getReceitaDeIntegracao() {
        return receitaDeIntegracao;
    }

    public void setReceitaDeIntegracao(Boolean receitaDeIntegracao) {
        this.receitaDeIntegracao = receitaDeIntegracao;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public OperacaoReceita getOperacaoReceitaRealizada() {
        return operacaoReceitaRealizada;
    }

    public void setOperacaoReceitaRealizada(OperacaoReceita operacaoReceitaRealizada) {
        this.operacaoReceitaRealizada = operacaoReceitaRealizada;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
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

    @Override
    public String toString() {
        return this.numero + " - " + this.pessoa.getNome() + ", " + receitaLOA.getContaDeReceita().getCodigo() + " - " + Util.formataValor(valor) + " (" + DataUtil.getDataFormatada(dataLancamento) + ")";
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return this.numero + " - " + this.receitaLOA.getContaDeReceita().getCodigo() + " - " + this.receitaLOA.getContaDeReceita().getDescricao();
    }

    @Override
    public String getMensagemBloqueioMovimentoContabil() {
        return "A Receita Realizada " + toString() + " está sendo utilizada em outro processo, por favor tente novamente mais tarde.";
    }

    public List<Long> getConjuntos() {
        List<Long> retorno = new ArrayList<>();
        for (LancReceitaFonte fonte : this.getLancReceitaFonte()) {
            Util.adicionarObjetoEmLista(retorno, fonte.getItem());
        }
        return retorno;
    }

    public boolean isReceitaRealizadaNormal() {
        return OperacaoReceita.RECEITA_DIRETAMENTE_ARRECADADA_BRUTA.equals(this.operacaoReceitaRealizada)
            || OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA.equals(this.operacaoReceitaRealizada)
            || OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA.equals(this.operacaoReceitaRealizada);
    }

    public boolean isReceitaRealizadaDeducao() {
        return OperacaoReceita.DEDUCAO_RECEITA_DESCONTO.equals(this.operacaoReceitaRealizada)
            || OperacaoReceita.DEDUCAO_RECEITA_RENUNCIA.equals(this.operacaoReceitaRealizada)
            || OperacaoReceita.DEDUCAO_RECEITA_RESTITUICAO.equals(this.operacaoReceitaRealizada)
            || OperacaoReceita.DEDUCAO_RECEITA_FUNDEB.equals(this.operacaoReceitaRealizada)
            || OperacaoReceita.DEDUCAO_RECEITA_OUTRAS.equals(this.operacaoReceitaRealizada);
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }
}
