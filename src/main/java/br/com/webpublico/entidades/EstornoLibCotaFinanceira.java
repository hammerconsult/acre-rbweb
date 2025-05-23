/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author juggernaut
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Orcamentario")
@Etiqueta("Estorno de Liberação Financeira")
public class EstornoLibCotaFinanceira extends SuperEntidadeContabilFinanceira implements IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data")
    @Pesquisavel
    @Tabelavel
    @ReprocessamentoContabil
    private Date dataEstorno;
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
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
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Liberação Financeira")
    private LiberacaoCotaFinanceira liberacao;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor Estornado (R$)")
    private BigDecimal valor;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
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
    private String historicoNota;
    private String historicoRazao;
    private String uuid;
    @ManyToOne
    private Identificador identificador;

    public EstornoLibCotaFinanceira() {
        dataEstorno = new Date();
        valor = BigDecimal.ZERO;
        uuid = UUID.randomUUID().toString();
    }

    public EstornoLibCotaFinanceira(Date dataEstorno, String numero, UnidadeOrganizacional unidadeOrganizacional, LiberacaoCotaFinanceira liberacao, String historico, BigDecimal valor) {
        this.dataEstorno = dataEstorno;
        this.numero = numero;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.liberacao = liberacao;
        this.historico = historico;
        this.valor = valor;
    }

    public Date getRecebida() {
        return recebida;
    }

    public void setRecebida(Date recebida) {
        this.recebida = recebida;
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

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public LiberacaoCotaFinanceira getLiberacao() {
        return liberacao;
    }

    public void setLiberacao(LiberacaoCotaFinanceira liberacao) {
        this.liberacao = liberacao;
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

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataEstorno()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiberacao().getContaFinanceiraRecebida() != null) {
            historicoNota += " Conta Financeira Recebeida: " + this.getLiberacao().getContaFinanceiraRecebida() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiberacao().getFonteDeRecursoRecebida() != null) {
            historicoNota += " Fonte de Recursos Recebeida: " + this.getLiberacao().getFonteDeRecursoRecebida() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiberacao().getContaFinanceiraRetirada() != null) {
            historicoNota += " Conta Financeira Concedida: " + this.getLiberacao().getContaFinanceiraRetirada() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiberacao().getFonteDeRecursoRetirada() != null) {
            historicoNota += " Fonte de Recursos Concedida: " + this.getLiberacao().getFonteDeRecursoRetirada() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
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
        return "Número: " + this.numero + " - " + this.liberacao + " - Valor " + Util.formataValor(this.valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return this.getNumero() + "/" + ((EntidadeContabil) liberacao).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(), contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liberacao.getSolicitacaoCotaFinanceira().getContaDeDestinacao(),
                    getLiberacao().getSolicitacaoCotaFinanceira().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(),
                    liberacao.getSolicitacaoCotaFinanceira().getContaDeDestinacao(),
                    getLiberacao().getSolicitacaoCotaFinanceira().getExercicio());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liberacao.getContaDeDestinacao(),
                    getLiberacao().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    liberacao.getContaDeDestinacao(),
                    getLiberacao().getExercicio());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liberacao.getSolicitacaoCotaFinanceira().getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(liberacao.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(),
                    liberacao.getSolicitacaoCotaFinanceira().getContaDeDestinacao());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liberacao.getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    liberacao.getContaDeDestinacao());
        }
        return null;
    }
}
