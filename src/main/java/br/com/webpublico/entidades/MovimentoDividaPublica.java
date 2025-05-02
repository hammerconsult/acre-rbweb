/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Publica")
@Etiqueta("Movimento da Dívida Pública ")
public class MovimentoDividaPublica extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.NUMERO_ORDENAVEL)
    @Pesquisavel
    @Etiqueta("Número")
    @ErroReprocessamentoContabil
    private Long numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    @Obrigatorio
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private Date data;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Lançamento")
    @Pesquisavel
    @Tabelavel
    @ErroReprocessamentoContabil
    private TipoLancamento tipoLancamento;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @Pesquisavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Operação")
    @Pesquisavel
    @ErroReprocessamentoContabil
    @Tabelavel
    private OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Dívida Pública")
    @Obrigatorio
    @ErroReprocessamentoContabil
    private DividaPublica dividaPublica;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Obrigatorio
    @Etiqueta("Conta de Destinação de Recurso")
    @ManyToOne
    @Pesquisavel
    private ContaDeDestinacao contaDeDestinacao;
    @Etiqueta("Histórico")
    @Obrigatorio
    @ErroReprocessamentoContabil
    private String historico;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @ErroReprocessamentoContabil
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Monetario
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;
    @Version
    private Long versao;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    @ErroReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @ManyToOne
    private ConfigMovDividaPublica configMovDividaPublica;
    @Etiqueta("Evento Contábil")
    @Pesquisavel
    @ManyToOne
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Exercicio exercicio;
    private String historicoNota;
    private String historicoRazao;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public MovimentoDividaPublica() {
        super();
        this.saldo = BigDecimal.ZERO;
        this.valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public OperacaoMovimentoDividaPublica getOperacaoMovimentoDividaPublica() {
        return operacaoMovimentoDividaPublica;
    }

    public void setOperacaoMovimentoDividaPublica(OperacaoMovimentoDividaPublica operacaoMovimentoDividaPublica) {
        this.operacaoMovimentoDividaPublica = operacaoMovimentoDividaPublica;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public ConfigMovDividaPublica getConfigMovDividaPublica() {
        return configMovDividaPublica;
    }

    public void setConfigMovDividaPublica(ConfigMovDividaPublica configMovDividaPublica) {
        this.configMovDividaPublica = configMovDividaPublica;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    public String getCaminho() {
        return "/movimento-divida-publica/";
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (numero != null) {
            historicoNota += "N°: " + numero + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDividaPublica().getNumero() != null) {
            historicoNota += " N° da Dívida: " + this.getDividaPublica().getNumero() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDividaPublica().getNumeroProtocolo() != null) {
            historicoNota += " Nº Proc.: " + this.getDividaPublica().getNumeroProtocolo() + "/" + Util.getAnoDaData(this.getDividaPublica().getDataHomologacao()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDividaPublica().getCategoriaDividaPublica() != null) {
            historicoNota += " Natureza: " + this.getDividaPublica().getCategoriaDividaPublica() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getDividaPublica().getNaturezaDividaPublica() != null) {
            historicoNota += " Categoria: " + this.getDividaPublica().getNaturezaDividaPublica() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getOperacaoMovimentoDividaPublica() != null) {
            historicoNota += " Operação: " + this.getOperacaoMovimentoDividaPublica().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
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
    public String toString() {
        return numero + " - " + new SimpleDateFormat("dd/MM/yyyy").format(data);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + dividaPublica.toString();
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacional;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(), contaDeDestinacao, getExercicio());
    }
}
