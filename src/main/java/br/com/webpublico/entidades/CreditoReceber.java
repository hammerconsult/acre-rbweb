/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.enums.OperacaoCreditoReceber;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author major
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Créditos a Receber")
public class CreditoReceber extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private Date dataCredito;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private String numero;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta("Lançamento")
    private TipoLancamento tipoLancamento;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @ErroReprocessamentoContabil
    @Etiqueta("Operação")
    private OperacaoCreditoReceber operacaoCreditoReceber;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @ErroReprocessamentoContabil
    @Etiqueta("Conta de Receita")
    private ReceitaLOA receitaLOA;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Conta de Destinação de Recurso")
    private ContaDeDestinacao contaDeDestinacao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private Intervalo intervalo;
    @ManyToOne
    @Tabelavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    @Obrigatorio
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Referência")
    private Date dataReferencia;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @ErroReprocessamentoContabil
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @ErroReprocessamentoContabil
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Unidade Organizacional Administrativa")
    @ErroReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Histórico")
    @ErroReprocessamentoContabil
    private String historico;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @ErroReprocessamentoContabil
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Etiqueta("Configuração")
    @ManyToOne
    private ConfigCreditoReceber configCreditoReceber;
    @OneToOne(cascade = CascadeType.ALL)
    private LoteIntegracaoTributarioContabil integracaoTribCont;
    private String historicoNota;
    private String historicoRazao;
    @Etiqueta("Integração")
    private Boolean integracao;
    @Version
    private Long versao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private LoteBaixa loteBaixa;

    public CreditoReceber() {
        valor = new BigDecimal(BigInteger.ZERO);
        criadoEm = System.nanoTime();
        integracao = Boolean.FALSE;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Long getId() {
        return id;
    }

    public Date getData() {
        return dataCredito;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCredito() {
        return dataCredito;
    }

    public void setDataCredito(Date dataCredito) {
        this.dataCredito = dataCredito;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public OperacaoCreditoReceber getOperacaoCreditoReceber() {
        return operacaoCreditoReceber;
    }

    public void setOperacaoCreditoReceber(OperacaoCreditoReceber operacaoCreditoReceber) {
        this.operacaoCreditoReceber = operacaoCreditoReceber;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    @Override
    public String toString() {
        return "Nº:" + numero + " - Conta : " + receitaLOA + " - Valor: " + valor;
    }

    public String getComplementoContabil() {
        return "O crédito a receber é: " + this.numero + " " + this.receitaLOA.getContaDeReceita().getDescricao() + ", Valor: " + this.valor;
    }

    public ConfigCreditoReceber getConfigCreditoReceber() {
        return configCreditoReceber;
    }

    public void setConfigCreditoReceber(ConfigCreditoReceber configCreditoReceber) {
        this.configCreditoReceber = configCreditoReceber;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
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

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public Boolean getIntegracao() {
        return integracao;
    }

    public void setIntegracao(Boolean integracao) {
        this.integracao = integracao;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataCredito()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaLOA() != null) {
            historicoNota += " Conta de Receita: " + this.getReceitaLOA().getContaDeReceita().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoCreditoReceber() != null) {
            historicoNota += " Operação: " + this.getOperacaoCreditoReceber().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPessoa() != null) {
            historicoNota += " Pessoa: " + this.getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getClasseCredor() != null) {
            historicoNota += " Classe: " + this.getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null && this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero;
    }

    public boolean isOperacaoBaixaDeducaoReconhecimento() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.BAIXA_DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoBaixaReconhecimento() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.BAIXA_RECONHECIMENTO_CREDITO_A_RECEBER.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoDeducaoReconhecimento() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.DEDUCAO_RECONHECIMENTO_CREDITO_A_RECEBER.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoReconhecimento() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.RECONHECIMENTO_CREDITO_A_RECEBER.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoAjustePerdas() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.AJUSTE_PERDAS_CREDITOS_A_RECEBER.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoAjustePerdasLongoPrazo() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.AJUSTE_PERDAS_CREDITOS_A_RECEBER_LONGO_PRAZO.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoReversaoAjustePerdas() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoReversaoAjustePerdasLongoPrazo() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.REVERSAO_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PRAZO.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoRecebimento() {
        return this.operacaoCreditoReceber != null && OperacaoCreditoReceber.RECEBIMENTO.equals(this.operacaoCreditoReceber);
    }

    public boolean isOperacaoAtualizacao() {
        return OperacaoCreditoReceber.ATUALIZACAO_DE_CREDITOS_A_RECEBER.equals(operacaoCreditoReceber);
    }

    public boolean isOperacaoAumentativo() {
        return this.operacaoCreditoReceber != null
            && (OperacaoCreditoReceber.AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO.equals(this.operacaoCreditoReceber)
            || OperacaoCreditoReceber.AJUSTE_CREDITOS_A_RECEBER_AUMENTATIVO_EMPRESA_PUBLICA.equals(this.operacaoCreditoReceber));
    }

    public boolean isOperacaoDiminutivo() {
        return this.operacaoCreditoReceber != null
            && (OperacaoCreditoReceber.AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO.equals(this.operacaoCreditoReceber)
            || OperacaoCreditoReceber.AJUSTE_CREDITOS_A_RECEBER_DIMINUTIVO_EMPRESA_PUBLICA.equals(this.operacaoCreditoReceber));
    }

    public boolean isLancamentoNormal() {
        return this.tipoLancamento != null && TipoLancamento.NORMAL.equals(this.tipoLancamento);
    }

    public boolean isLancamentoEstorno() {
        return this.tipoLancamento != null && TipoLancamento.ESTORNO.equals(this.tipoLancamento);
    }

    public boolean isOperacaoTransferencias() {
        return this.operacaoCreditoReceber != null
            && (OperacaoCreditoReceber.TRANSFERENCIA_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO.equals(this.operacaoCreditoReceber)
            || OperacaoCreditoReceber.TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_CURTO_PARA_LONGO_PRAZO.equals(this.operacaoCreditoReceber)
            || OperacaoCreditoReceber.TRANSFERENCIA_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO.equals(this.operacaoCreditoReceber)
            || OperacaoCreditoReceber.TRANSFERENCIA_AJUSTE_PERDAS_CREDITO_A_RECEBER_LONGO_PARA_CURTO_PRAZO.equals(this.operacaoCreditoReceber)
        );
    }

    public String getCaminho() {
        return "/credito-receber/";
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public NaturezaDividaAtivaCreditoReceber getNaturezaCreditoReceber() {
        return naturezaCreditoReceber;
    }

    public void setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber naturezaCreditoReceber) {
        this.naturezaCreditoReceber = naturezaCreditoReceber;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional());
    }
}
