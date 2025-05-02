/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoLiberacaoFinanceira;
import br.com.webpublico.enums.TipoOperacaoPagto;
import br.com.webpublico.geradores.GrupoDiagrama;
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
 * @author major
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Orcamentario")
@Etiqueta("Liberação Financeira")
public class LiberacaoCotaFinanceira extends SuperEntidadeContabilFinanceira {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data")
    @ReprocessamentoContabil
    private Date dataLiberacao;
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
    @Etiqueta("Solicitação Financeira")
    @Obrigatorio
    @ManyToOne
    private SolicitacaoCotaFinanceira solicitacaoCotaFinanceira;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Solicitante")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Valor Liberado (R$)")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    private BigDecimal valor;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Saldo (R$)")
    @Obrigatorio
    private BigDecimal saldo;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String observacoes;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Conta Financeira")
    private SubConta subConta;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Liberação Financeira")
    private TipoLiberacaoFinanceira tipoLiberacaoFinanceira;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Destinação de Recurso")
    private ContaDeDestinacao contaDeDestinacao;
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    private StatusPagamento statusPagamento;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Operação")
    private TipoOperacaoPagto tipoOperacaoPagto;
    @Invisivel
    private String migracaoChave;
    @ManyToOne
    @Pesquisavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Evento Contábil Recebido")
    private EventoContabil eventoContabilDeposito;
    @ManyToOne
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Evento Contábil Concedido")
    private EventoContabil eventoContabilRetirada;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo de Documento")
    private TipoDocPagto tipoDocumento;
    @ManyToOne
    @Etiqueta("Finalidade do Pagamento")
    private FinalidadePagamento finalidadePagamento;
    private String historicoNota;
    private String historicoRazao;
    private String uuid;
    @ManyToOne
    private Identificador identificador;
    @Version
    private Long versao;

    public LiberacaoCotaFinanceira() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        dataLiberacao = new Date();
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

    public EventoContabil getEventoContabilDeposito() {
        return eventoContabilDeposito;
    }

    public void setEventoContabilDeposito(EventoContabil eventoContabilDeposito) {
        this.eventoContabilDeposito = eventoContabilDeposito;
    }

    public EventoContabil getEventoContabilRetirada() {
        return eventoContabilRetirada;
    }

    public void setEventoContabilRetirada(EventoContabil eventoContabilRetirada) {
        this.eventoContabilRetirada = eventoContabilRetirada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLiberacao() {
        return dataLiberacao;
    }

    public void setDataLiberacao(Date dataLiberacao) {
        this.dataLiberacao = dataLiberacao;
    }

    public Date getRecebida() {
        return recebida;
    }

    public void setRecebida(Date recebida) {
        this.recebida = recebida;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }


    public SolicitacaoCotaFinanceira getSolicitacaoCotaFinanceira() {
        return solicitacaoCotaFinanceira;
    }

    public void setSolicitacaoCotaFinanceira(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        this.solicitacaoCotaFinanceira = solicitacaoCotaFinanceira;
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

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
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

    public TipoLiberacaoFinanceira getTipoLiberacaoFinanceira() {
        return tipoLiberacaoFinanceira;
    }

    public void setTipoLiberacaoFinanceira(TipoLiberacaoFinanceira tipoLiberacaoFinanceira) {
        this.tipoLiberacaoFinanceira = tipoLiberacaoFinanceira;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public TipoDocPagto getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocPagto tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataLiberacao()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getContaFinanceiraRecebida() != null) {
            historicoNota += " Conta Financeira Recebida: " + getContaFinanceiraRecebida() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getFonteDeRecursoRecebida() != null) {
            historicoNota += " Fonte de Recursos Recebida: " + getFonteDeRecursoRecebida().toString().trim() + (solicitacaoCotaFinanceira.getContaDeDestinacao() != null ?
                " (" + solicitacaoCotaFinanceira.getContaDeDestinacao().getCodigo() + ")" : "") + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getContaFinanceiraRetirada() != null) {
            historicoNota += " Conta Financeira Concedida: " + getContaFinanceiraRetirada().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getFonteDeRecursoRetirada() != null) {
            historicoNota += " Fonte de Recursos Concedida: " + getFonteDeRecursoRetirada().toString().trim() + (contaDeDestinacao != null ?
                " (" + contaDeDestinacao.getCodigo() + ")" : "") + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + observacoes;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabilRetirada() != null) {
            if (this.getEventoContabilRetirada().getClpHistoricoContabil() != null) {
                historicoEvento = "Evento Contábil Concedido: " + this.getEventoContabilRetirada().getClpHistoricoContabil().toString();
            }
        }
        if (this.getEventoContabilDeposito() != null) {
            if (this.getEventoContabilDeposito().getClpHistoricoContabil() != null) {
                historicoEvento = "Evento Contábil Recebido: " + this.getEventoContabilDeposito().getClpHistoricoContabil().toString();
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
        try {
            return "N° " + this.numero + " - " + this.solicitacaoCotaFinanceira.getUsuarioSolicitante().getPessoaFisica().getNome() +
                " - Valor " + Util.formataValor(this.valor) + " (" + DataUtil.getDataFormatada(dataLiberacao) + ")";
        } catch (Exception e) {
            return "N° " + this.numero + " - " + " Valor " + Util.formataValor(this.valor) + " (" + DataUtil.getDataFormatada(dataLiberacao) + ")";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return this.getNumero() + " - " + subConta.getCodigo() + " " + subConta.getDescricao() + " - FR: " + fonteDeRecursos.getCodigo() + " " + fonteDeRecursos.getDescricao();
    }

    public FonteDeRecursos getFonteDeRecursoRecebida() {
        return this.solicitacaoCotaFinanceira.getFonteDeRecursos();
    }

    public FonteDeRecursos getFonteDeRecursoRetirada() {
        return this.fonteDeRecursos;
    }

    public ContaDeDestinacao getContaDeDestinacaoRetirada() {
        return contaDeDestinacao;
    }

    public ContaDeDestinacao getContaDeDestinacaoRecebida() {
        return solicitacaoCotaFinanceira.getContaDeDestinacao();
    }

    public SubConta getContaFinanceiraRetirada() {
        return this.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada();
    }

    public SubConta getContaFinanceiraRecebida() {
        return this.getSolicitacaoCotaFinanceira().getContaFinanceira();
    }

    public UnidadeOrganizacional getUnidadeRetirada() {
        return this.getUnidadeOrganizacional();
    }

    public UnidadeOrganizacional getUnidadeRecebida() {
        return this.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional();
    }

    public String getHistoricoArquivoOBN600() {
        String historico = this.observacoes.trim();
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
        if (ParametroEvento.ComplementoId.CONCEDIDO.equals(complementoId)) {
            return new GeradorContaAuxiliarDTO(getUnidadeRetirada(),
                getContaDeDestinacaoRetirada(), getExercicio());
        } else if (ParametroEvento.ComplementoId.RECEBIDO.equals(complementoId)) {
            return new GeradorContaAuxiliarDTO(getUnidadeRecebida(),
                getContaDeDestinacaoRecebida(), getExercicio());
        }
        return null;
    }
}
