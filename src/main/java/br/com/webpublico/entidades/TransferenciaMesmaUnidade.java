/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author juggernaut
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Orcamentario")
@Etiqueta("Tranferência Financeira Mesma Unidade")
public class TransferenciaMesmaUnidade extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    @ErroReprocessamentoContabil
    private String numero;
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Tabelavel
    @Etiqueta("Data")
    private Date dataTransferencia;
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @Etiqueta("Data Concedida")
    private Date dataConciliacao;
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @Etiqueta("Data Recebida")
    private Date recebida;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Transferência")
    @Enumerated(EnumType.STRING)
    @ErroReprocessamentoContabil
    private TipoTransferenciaMesmaUnidade tipoTransferencia;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Financeira Recebida")
    @ErroReprocessamentoContabil
    private SubConta subContaDeposito;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Financeira Concedida")
    @ErroReprocessamentoContabil
    private SubConta subContaRetirada;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Valor (R$)")
    @Monetario
    @ErroReprocessamentoContabil
    private BigDecimal valor;
    @Etiqueta("Saldo (R$)")
    @Obrigatorio
    @Tabelavel
    @Monetario
    @ErroReprocessamentoContabil
    private BigDecimal saldo;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosDeposito;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Destinação de Recurso Recebida")
    @ErroReprocessamentoContabil
    private ContaDeDestinacao contaDeDestinacaoDeposito;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosRetirada;
    @Obrigatorio
    @Etiqueta("Conta de Destinação de Recurso Concedida")
    @ErroReprocessamentoContabil
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacaoRetirada;
    @Obrigatorio
    @Etiqueta("Histórico")
    @ErroReprocessamentoContabil
    private String historico;
    @Etiqueta("Evento Contábil Recebido")
    @ManyToOne
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;
    @Etiqueta("Evento Contábil Concedido")
    @ManyToOne
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabilRetirada;
    @Obrigatorio
    @Etiqueta("Dependência da Execução Orçamentária")
    @Enumerated(EnumType.STRING)
    private ResultanteIndependente resultanteIndependente;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    private StatusPagamento statusPagamento;
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Tipo de Operação")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoOperacaoPagto tipoOperacaoPagto;
    @Etiqueta("Tipo de Documento")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoDocPagto tipoDocPagto;
    @ManyToOne
    @Etiqueta("Finalidade do Pagamento")
    private FinalidadePagamento finalidadePagamento;
    private String historicoNota;
    private String historicoRazao;
    private String uuid;
    @ManyToOne
    private Identificador identificador;

    public TransferenciaMesmaUnidade() {
        tipoTransferencia = TipoTransferenciaMesmaUnidade.APLICACAO;
        dataTransferencia = new Date();
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        statusPagamento = StatusPagamento.ABERTO;
        uuid = UUID.randomUUID().toString();
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

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public EventoContabil getEventoContabilRetirada() {
        return eventoContabilRetirada;
    }

    public void setEventoContabilRetirada(EventoContabil eventoContabilRetirada) {
        this.eventoContabilRetirada = eventoContabilRetirada;
    }

    public SubConta getSubContaDeposito() {
        return subContaDeposito;
    }

    public void setSubContaDeposito(SubConta subContaDeposito) {
        this.subContaDeposito = subContaDeposito;
    }

    public SubConta getSubContaRetirada() {
        return subContaRetirada;
    }

    public void setSubContaRetirada(SubConta subContaRetirada) {
        this.subContaRetirada = subContaRetirada;
    }

    public Date getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(Date dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
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

    public TipoTransferenciaMesmaUnidade getTipoTransferencia() {
        return tipoTransferencia;
    }

    public void setTipoTransferencia(TipoTransferenciaMesmaUnidade tipoTransferencia) {
        this.tipoTransferencia = tipoTransferencia;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public FonteDeRecursos getFonteDeRecursosDeposito() {
        return fonteDeRecursosDeposito;
    }

    public void setFonteDeRecursosDeposito(FonteDeRecursos fonteDeRecursosDeposito) {
        this.fonteDeRecursosDeposito = fonteDeRecursosDeposito;
    }

    public FonteDeRecursos getFonteDeRecursosRetirada() {
        return fonteDeRecursosRetirada;
    }

    public void setFonteDeRecursosRetirada(FonteDeRecursos fonteDeRecursosRetirada) {
        this.fonteDeRecursosRetirada = fonteDeRecursosRetirada;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public ResultanteIndependente getResultanteIndependente() {
        return resultanteIndependente;
    }

    public void setResultanteIndependente(ResultanteIndependente resultanteIndependente) {
        this.resultanteIndependente = resultanteIndependente;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public Date getRecebida() {
        return recebida;
    }

    public void setRecebida(Date recebida) {
        this.recebida = recebida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoOperacaoPagto getTipoOperacaoPagto() {
        return tipoOperacaoPagto;
    }

    public void setTipoOperacaoPagto(TipoOperacaoPagto tipoOperacaoPagto) {
        this.tipoOperacaoPagto = tipoOperacaoPagto;
    }

    public FinalidadePagamento getFinalidadePagamento() {
        return finalidadePagamento;
    }

    public void setFinalidadePagamento(FinalidadePagamento finalidadePagamento) {
        this.finalidadePagamento = finalidadePagamento;
    }

    public TipoDocPagto getTipoDocPagto() {
        return tipoDocPagto;
    }

    public void setTipoDocPagto(TipoDocPagto tipoDocPagto) {
        this.tipoDocPagto = tipoDocPagto;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public ContaDeDestinacao getContaDeDestinacaoDeposito() {
        return contaDeDestinacaoDeposito;
    }

    public void setContaDeDestinacaoDeposito(ContaDeDestinacao contaDeDestinacaoDeposito) {
        this.contaDeDestinacaoDeposito = contaDeDestinacaoDeposito;
    }

    public ContaDeDestinacao getContaDeDestinacaoRetirada() {
        return contaDeDestinacaoRetirada;
    }

    public void setContaDeDestinacaoRetirada(ContaDeDestinacao contaDeDestinacaoRetirada) {
        this.contaDeDestinacaoRetirada = contaDeDestinacaoRetirada;
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataTransferencia()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getSubContaDeposito() != null) {
            historicoNota += " Conta Financeira Recebida: " + this.getSubContaDeposito() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getFonteDeRecursosDeposito() != null) {
            historicoNota += " Fonte de Recursos Recebida: " + this.getFonteDeRecursosDeposito() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getSubContaRetirada() != null) {
            historicoNota += " Conta Financeira Concedida: " + this.getSubContaRetirada() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getFonteDeRecursosRetirada() != null) {
            historicoNota += " Fonte de Recursos Concedida: " + this.getFonteDeRecursosRetirada() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = "Evento Contábil Recebido: " + this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        if (this.getEventoContabilRetirada().getClpHistoricoContabil() != null) {
            historicoEvento = "Evento Contábil Concedido: " + this.getEventoContabilRetirada().getClpHistoricoContabil().toString();
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
        String retorno = "";
        if (numero != null && tipoTransferencia != null && valor != null) {
            return retorno + "Nº " + numero + " - " + " Operação: " + tipoTransferencia.getDescricao() + " - " + " Valor " + Util.formataValor(valor) + " - (" + DataUtil.getDataFormatada(dataTransferencia) + ")";
        }
        if (!"".equals(retorno)) {
            return retorno;
        }
        return "";
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return this.getNumero() + "/" + this.getExercicio().getAno();
    }

    public String getHistoricoArquivoOBN600() {
        String historico = this.historico.trim();
        historico = historico.replace("\n", "");
        historico = historico.replace("\r", "");
        historico = historico.replace("\t", "");
        return historico;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(),
            getContaDeDestinacaoDeposito(), getExercicio());
    }
}
