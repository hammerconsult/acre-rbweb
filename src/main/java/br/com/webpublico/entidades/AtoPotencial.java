package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.TipoAtoPotencial;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoAtoPotencial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeMap;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
@Etiqueta("Ato Potencial")
public class AtoPotencial extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private Date data;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private String numero;
    @Etiqueta("Unidade Organizacional")
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Etiqueta("Evento Contábil")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Lançamento")
    @Obrigatorio
    @Tabelavel
    @ErroReprocessamentoContabil
    @Pesquisavel
    private TipoLancamento tipoLancamento;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Ato Potencial")
    @Tabelavel
    @ErroReprocessamentoContabil
    @Pesquisavel
    private TipoAtoPotencial tipoAtoPotencial;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Operação")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial;
    @Etiqueta("Número de Referência")
    @Tabelavel
    @Pesquisavel
    private String numeroReferencia;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício de Referência")
    private Exercicio exercicioReferencia;
    @ManyToOne
    @Etiqueta("Contrato")
    private Contrato contrato;
    @ManyToOne
    @Etiqueta("Convênio de Receita")
    private ConvenioReceita convenioReceita;
    @ManyToOne
    @Etiqueta("Convênio de Despesa")
    private ConvenioDespesa convenioDespesa;
    @Etiqueta("Histórico")
    @Length(maximo = 3000)
    @Obrigatorio
    private String historico;
    private String historicoRazao;
    private String historicoNota;
    @Monetario
    @Obrigatorio
    @Positivo(permiteZero = false)
    @Tabelavel
    @ErroReprocessamentoContabil
    private BigDecimal valor;

    public AtoPotencial() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoAtoPotencial getTipoAtoPotencial() {
        return tipoAtoPotencial;
    }

    public void setTipoAtoPotencial(TipoAtoPotencial tipoAtoPotencial) {
        this.tipoAtoPotencial = tipoAtoPotencial;
    }

    public TipoOperacaoAtoPotencial getTipoOperacaoAtoPotencial() {
        return tipoOperacaoAtoPotencial;
    }

    public void setTipoOperacaoAtoPotencial(TipoOperacaoAtoPotencial tipoOperacaoAtoPotencial) {
        this.tipoOperacaoAtoPotencial = tipoOperacaoAtoPotencial;
    }

    public String getNumeroReferencia() {
        return numeroReferencia;
    }

    public void setNumeroReferencia(String numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }

    public Exercicio getExercicioReferencia() {
        return exercicioReferencia;
    }

    public void setExercicioReferencia(Exercicio exercicioReferencia) {
        this.exercicioReferencia = exercicioReferencia;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    public void gerarHistoricoNota() {
        String historico = "";
        if (this.getNumero() != null) {
            historico += "Ato Potencial: " + numero + " - " + DataUtil.getDataFormatada(data) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }

        historico += " Tipo de Lançamento: " + tipoLancamento.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Tipo de Ato Potencial: " + tipoAtoPotencial.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Operação: " + tipoOperacaoAtoPotencial.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Evento Contábil: " + eventoContabil + UtilBeanContabil.SEPARADOR_HISTORICO;
        historico += " Número de Referência: " + numeroReferencia + UtilBeanContabil.SEPARADOR_HISTORICO;
        if (exercicioReferencia != null) {
            historico += " Exercício de Referência: " + exercicioReferencia.getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (convenioReceita != null) {
            historico += " Pessoa: " + convenioReceita.getPessoa() + ", ";
            historico += " Classe: " + convenioReceita.getClasseCredor() + UtilBeanContabil.SEPARADOR_HISTORICO;
        } else if (convenioDespesa != null) {
            if (convenioDespesa.getEntidadeBeneficiaria() != null && convenioDespesa.getEntidadeBeneficiaria().getPessoaJuridica() != null) {
                historico += " Pessoa: " + convenioDespesa.getEntidadeBeneficiaria().getPessoaJuridica() + ", ";
            }
            historico += " Classe: " + convenioDespesa.getClasseCredor() + UtilBeanContabil.SEPARADOR_HISTORICO;
        } else if (contrato != null) {
            historico += " Pessoa: " + contrato.getContratado() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }

        if (this.getHistorico() != null) {
            historico += " " + this.getHistorico();
        }
        this.historicoNota = Util.cortarString(historico, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = eventoContabil.getClpHistoricoContabil() != null ? eventoContabil.getClpHistoricoContabil().toString() : "";
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return "";
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return "";
    }

    @Override
    public String toString() {
        return numero + " - " + Util.formataValor(valor) + " - " + DataUtil.getDataFormatada(data) + " - " + tipoAtoPotencial.getDescricao();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional());
    }
}
