/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.DataUtil;
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
@Etiqueta("Estorno de Transferência Financeira")
public class EstornoTransferencia extends SuperEntidadeContabilFinanceira implements IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data")
    @Tabelavel
    private Date dataEstorno;
    @Etiqueta("Data Concedida")
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    private Date dataConciliacao;
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    @Etiqueta("Data Recebida")
    private Date recebida;
    @ErroReprocessamentoContabil
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Transferência Financeira")
    private TransferenciaContaFinanceira transferencia;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;
    @ManyToOne
    private Exercicio exercicio;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Obrigatorio
    @Etiqueta("Evento Contábil Depósito")
    @ManyToOne
    private EventoContabil eventoContabil;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Obrigatorio
    @Etiqueta("Evento Contábil Retirada")
    @ManyToOne
    private EventoContabil eventoContabilRetirada;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    private String historicoNota;
    private String historicoRazao;
    private String uuid;
    @ManyToOne
    private Identificador identificador;

    public EstornoTransferencia() {
        dataEstorno = new Date();
        valor = BigDecimal.ZERO;
        uuid = UUID.randomUUID().toString();
    }

    public EstornoTransferencia(String numero, Date dataEstorno, TransferenciaContaFinanceira transferencia, BigDecimal valor, UnidadeOrganizacional unidadeOrganizacional, String historico, Exercicio exercicio) {
        this.numero = numero;
        this.dataEstorno = dataEstorno;
        this.transferencia = transferencia;
        this.valor = valor;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.historico = historico;
        this.exercicio = exercicio;
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

    public EventoContabil getEventoContabilRetirada() {
        return eventoContabilRetirada;
    }

    public void setEventoContabilRetirada(EventoContabil eventoContabilRetirada) {
        this.eventoContabilRetirada = eventoContabilRetirada;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TransferenciaContaFinanceira getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(TransferenciaContaFinanceira transferencia) {
        this.transferencia = transferencia;
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
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataEstorno()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTransferencia().getSubContaDeposito() != null) {
            historicoNota += " Conta Financeira Recebida: " + this.getTransferencia().getSubContaDeposito() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTransferencia().getFonteDeRecursosDeposito() != null) {
            historicoNota += " Fonte de Recursos Recebida: " + this.getTransferencia().getFonteDeRecursosDeposito() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTransferencia().getSubContaRetirada() != null) {
            historicoNota += " Conta Financeira Concedida: " + this.getTransferencia().getSubContaRetirada() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTransferencia().getFonteDeRecursosRetirada() != null) {
            historicoNota += " Fonte de Recursos Concedida: " + this.getTransferencia().getFonteDeRecursosRetirada() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = "Evento Contábil Recebido: " + this.getEventoContabil().getClpHistoricoContabil().toString();
            }
        }
        if (this.getEventoContabilRetirada() != null) {
            if (this.getEventoContabilRetirada().getClpHistoricoContabil() != null) {
                historicoEvento = "Evento Contábil Concedido: " + this.getEventoContabilRetirada().getClpHistoricoContabil().toString();
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
        return numero + " - " + Util.formataValor(valor) + "(" + DataUtil.getDataFormatada(dataEstorno) + ")";
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return this.getNumero() + "/" + this.getExercicio().getAno() + " - " + ((EntidadeContabil) transferencia).getReferenciaArquivoPrestacaoDeContas();
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
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(transferencia.getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(transferencia.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    transferencia.getContaDeDestinacaoDeposito(),
                    getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(transferencia.getUnidadeOrganizacional(),
                    transferencia.getContaDeDestinacaoDeposito(),
                    getExercicio());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(transferencia.getUnidadeOrgConcedida());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(transferencia.getUnidadeOrgConcedida(),
                    contaContabil.getSubSistema(),
                    transferencia.getContaDeDestinacaoRetirada(),
                    getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(transferencia.getUnidadeOrgConcedida(),
                    transferencia.getContaDeDestinacaoRetirada(),
                    getExercicio());
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
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(transferencia.getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(transferencia.getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    transferencia.getContaDeDestinacaoDeposito());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(transferencia.getUnidadeOrganizacional(),
                    transferencia.getContaDeDestinacaoDeposito());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(transferencia.getUnidadeOrgConcedida());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(transferencia.getUnidadeOrgConcedida(),
                    contaContabil.getSubSistema(),
                    transferencia.getContaDeDestinacaoRetirada());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(transferencia.getUnidadeOrgConcedida(),
                    transferencia.getContaDeDestinacaoRetirada());
        }
        return null;
    }
}
