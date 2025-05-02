/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
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
@Etiqueta("Estorno de Receita Realizada")
public class ReceitaORCEstorno extends SuperEntidade implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;
    @Etiqueta("Data de Conciliacão")
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    private Date dataConciliacao;
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    private OperacaoReceita operacaoReceitaRealizada;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Conta de Receita")
    private ReceitaLOA receitaLOA;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @ManyToOne
    @Tabelavel
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Conta Financeira")
    private SubConta ContaFinanceira;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Pessoa")
    @Pesquisavel
    @ErroReprocessamentoContabil
    private Pessoa pessoa;
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Etiqueta("Valor (R$)")
    @Monetario
    private BigDecimal valor;
    @ErroReprocessamentoContabil
    @Pesquisavel
    @Etiqueta("Receita Realizada")
    @Invisivel
    @ManyToOne
    private LancamentoReceitaOrc lancamentoReceitaOrc;
    @Etiqueta("Histórico")
    @Obrigatorio
    private String complementoHistorico;
    @ManyToOne
    @JoinColumn(name = "UNIDADEORGANIZACIONALADM")
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @ManyToOne
    @Etiqueta("Unidade")
    @JoinColumn(name = "UNIDADEORGANIZACIONALORC")
    private UnidadeOrganizacional unidadeOrganizacionalOrc;
    @OneToMany(mappedBy = "receitaORCEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaORCFonteEstorno> receitaORCFonteEstorno;
    @ManyToOne(cascade = CascadeType.ALL)
    private LoteIntegracaoTributarioContabil integracaoTribCont;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    @ErroReprocessamentoContabil
    private ClasseCredor classeCredor;
    @ManyToOne
    @Etiqueta("Convênio de Receita")
    @ErroReprocessamentoContabil
    private ConvenioReceita convenioReceita;
    @ManyToOne
    @Etiqueta("Dívida Pública")
    @ErroReprocessamentoContabil
    private DividaPublica dividaPublica;
    @Etiqueta("Lote")
    private String lote;
    @Pesquisavel
    @Etiqueta("Integração")
    private Boolean integracao;
    private String uuid;
    @Obrigatorio
    @Etiqueta("Data de Referência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataReferencia;
    @ManyToOne
    private Identificador identificador;

    public ReceitaORCEstorno() {
        receitaORCFonteEstorno = new ArrayList<>();
        valor = BigDecimal.ZERO;
        integracao = Boolean.FALSE;
        uuid = UUID.randomUUID().toString();
    }

    public ReceitaORCEstorno(String numero, Date dataEstorno, LancamentoReceitaOrc lancamentoReceitaOrc, BigDecimal valor, String complementoHistorico, EventoContabil eventoContabil, UnidadeOrganizacional unidadeOrganizacionalAdm, UnidadeOrganizacional unidadeOrganizacionalOrc, ReceitaLOA receitaLOA, List<ReceitaORCFonteEstorno> receitaORCFonteEstorno) {
        this.numero = numero;
        this.dataEstorno = dataEstorno;
        this.lancamentoReceitaOrc = lancamentoReceitaOrc;
        this.valor = valor;
        this.complementoHistorico = complementoHistorico;
        this.eventoContabil = eventoContabil;
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
        this.unidadeOrganizacionalOrc = unidadeOrganizacionalOrc;
        this.receitaLOA = receitaLOA;
        this.receitaORCFonteEstorno = receitaORCFonteEstorno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public LancamentoReceitaOrc getLancamentoReceitaOrc() {
        return lancamentoReceitaOrc;
    }

    public void setLancamentoReceitaOrc(LancamentoReceitaOrc lancamentoReceitaOrc) {
        this.lancamentoReceitaOrc = lancamentoReceitaOrc;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacionalOrc;
    }

    public void setUnidadeOrganizacionalOrc(UnidadeOrganizacional unidadeOrganizacionalOrc) {
        this.unidadeOrganizacionalOrc = unidadeOrganizacionalOrc;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public List<ReceitaORCFonteEstorno> getReceitaORCFonteEstorno() {
        return receitaORCFonteEstorno;
    }

    public void setReceitaORCFonteEstorno(List<ReceitaORCFonteEstorno> receitaORCFonteEstorno) {
        this.receitaORCFonteEstorno = receitaORCFonteEstorno;
    }

    public SubConta getContaFinanceira() {
        return ContaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        ContaFinanceira = contaFinanceira;
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

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
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

    public Boolean getIntegracao() {
        return integracao;
    }

    public void setIntegracao(Boolean integracao) {
        this.integracao = integracao;
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

    @Override
    public String toString() {
        return this.numero + " - " + Util.formataValor(valor) + " (" + DataUtil.getDataFormatada(dataEstorno) + ")";
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return this.getNumero() + ((EntidadeContabil) lancamentoReceitaOrc).getReferenciaArquivoPrestacaoDeContas();
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
