/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.NaturezaDividaAtivaCreditoReceber;
import br.com.webpublico.enums.OperacaoDividaAtiva;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author juggernaut
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Dívida Ativa")
public class DividaAtivaContabil extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Lanç.")
    @Tabelavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Obrigatorio
    @Pesquisavel
    private Date dataDivida;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data de Referência")
    private Date dataReferencia;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Lançamento")
    private TipoLancamento tipoLancamento;
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Operação")
    private OperacaoDividaAtiva operacaoDividaAtiva;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Etiqueta("Conta de Receita")
    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    private ReceitaLOA receitaLOA;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Conta de Receita")
    private ContaReceita contaReceita;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Conta de Destinação de Recurso")
    @Obrigatorio
    private ContaDeDestinacao contaDeDestinacao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private Intervalo intervalo;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Pessoa")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private Pessoa pessoa;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta("Classe")
    private ClasseCredor classeCredorPessoa;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Obrigatorio
    @Etiqueta("Histórico")
    @ErroReprocessamentoContabil
    private String historico;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Configuração da Dívida Ativa")
    @ManyToOne
    private ConfigDividaAtivaContabil configDividaAtivaContabil;
    @ManyToOne
    private UnidadeOrganizacional UnidadeOrganizacionalAdm;
    @ManyToOne(cascade = CascadeType.ALL)
    private LoteIntegracaoTributarioContabil integracaoTribCont;
    private String historicoNota;
    private String historicoRazao;
    @Pesquisavel
    @Etiqueta("Integração")
    private Boolean integracao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Version
    private Long versao;
    @ManyToOne
    private LoteBaixa loteBaixa;

    public DividaAtivaContabil() {
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.valor = BigDecimal.ZERO;
        integracao = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public Date getData() {
        return dataDivida;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataDivida() {
        return dataDivida;
    }

    public void setDataDivida(Date dataDivida) {
        this.dataDivida = dataDivida;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public OperacaoDividaAtiva getOperacaoDividaAtiva() {
        return operacaoDividaAtiva;
    }

    public void setOperacaoDividaAtiva(OperacaoDividaAtiva operacaoDividaAtiva) {
        this.operacaoDividaAtiva = operacaoDividaAtiva;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ReceitaLOA getReceitaLOA() {
        return receitaLOA;
    }

    public void setReceitaLOA(ReceitaLOA receitaLOA) {
        this.receitaLOA = receitaLOA;
    }

    public ContaReceita getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(ContaReceita contaReceita) {
        this.contaReceita = contaReceita;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ClasseCredor getClasseCredorPessoa() {
        return classeCredorPessoa;
    }

    public void setClasseCredorPessoa(ClasseCredor classeCredorPessoa) {
        this.classeCredorPessoa = classeCredorPessoa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ConfigDividaAtivaContabil getConfigDividaAtivaContabil() {
        return configDividaAtivaContabil;
    }

    public void setConfigDividaAtivaContabil(ConfigDividaAtivaContabil configDividaAtivaContabil) {
        this.configDividaAtivaContabil = configDividaAtivaContabil;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return UnidadeOrganizacionalAdm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        UnidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public LoteIntegracaoTributarioContabil getIntegracaoTribCont() {
        return integracaoTribCont;
    }

    public void setIntegracaoTribCont(LoteIntegracaoTributarioContabil integracaoTribCont) {
        this.integracaoTribCont = integracaoTribCont;
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

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public NaturezaDividaAtivaCreditoReceber getNaturezaDividaAtiva() {
        return naturezaDividaAtiva;
    }

    public void setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber naturezaDividaAtiva) {
        this.naturezaDividaAtiva = naturezaDividaAtiva;
    }

    public Intervalo getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(Intervalo intervalo) {
        this.intervalo = intervalo;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataDivida()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaLOA() != null) {
            historicoNota += " Conta de Receita: " + this.getReceitaLOA().getContaDeReceita().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoDividaAtiva() != null) {
            historicoNota += " Operação: " + this.getOperacaoDividaAtiva().getDescricao().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPessoa() != null) {
            historicoNota += " Pessoa: " + this.getPessoa().getNomeCpfCnpj().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getClasseCredorPessoa() != null) {
            historicoNota += " Classe: " + this.getClasseCredorPessoa().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
            }
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        return numero + " - " + DataUtil.getDataFormatada(dataDivida) + " - " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero;
    }

    public boolean isInscricao() {
        return OperacaoDividaAtiva.INSCRICAO.equals(this.operacaoDividaAtiva);
    }

    public boolean isAtualizacao() {
        return OperacaoDividaAtiva.ATUALIZACAO.equals(this.operacaoDividaAtiva);
    }

    public boolean isRecebimento() {
        return OperacaoDividaAtiva.RECEBIMENTO.equals(this.operacaoDividaAtiva);
    }

    public boolean isBaixa() {
        return OperacaoDividaAtiva.BAIXA.equals(this.operacaoDividaAtiva);
    }

    public boolean isLancamentoNormal() {
        return TipoLancamento.NORMAL.equals(this.tipoLancamento);
    }

    public boolean isLancamentoEstorno() {
        return TipoLancamento.ESTORNO.equals(this.tipoLancamento);
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public String getCaminho() {
        return "/divida-ativa/";
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
