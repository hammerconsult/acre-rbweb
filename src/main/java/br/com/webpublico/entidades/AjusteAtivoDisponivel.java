/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.TipoAjusteDisponivel;
import br.com.webpublico.enums.TipoLancamento;
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
 * @author major
 */

@Audited
@Entity
@Etiqueta("Ajuste Ativo Disponível")
public class AjusteAtivoDisponivel extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Número")
    @Tabelavel
    @ErroReprocessamentoContabil
    private String numero;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Data de Referência")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Pesquisavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private Date dataAjuste;
    @Etiqueta("Data Conciliação")
    @Pesquisavel
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.CENTRO)
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    private Date dataConciliacao;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Lançamento")
    @Pesquisavel
    @Tabelavel
    @ErroReprocessamentoContabil
    private TipoLancamento tipoLancamento;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo de Ajuste")
    @Pesquisavel
    @Tabelavel
    @ErroReprocessamentoContabil
    private TipoAjusteDisponivel tipoAjusteDisponivel;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta Financeira")
    @Pesquisavel
    @Tabelavel
    @ErroReprocessamentoContabil
    private SubConta contaFinanceira;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Valor (R$)")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @ErroReprocessamentoContabil
    private BigDecimal valor;
    @Pesquisavel
    @Etiqueta("Saldo (R$)")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @ErroReprocessamentoContabil
    private BigDecimal saldo;
    @ManyToOne
    @Etiqueta("Fonte de Recursos")
    @ErroReprocessamentoContabil
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    @Etiqueta("Conta de Destinação de Recurso")
    private ContaDeDestinacao contaDeDestinacao;
    @ErroReprocessamentoContabil
    private String historico;
    @ManyToOne
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Tabelavel
    @Etiqueta(value = "Unidade Organizacional")
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    private Pessoa pessoa;
    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    @Etiqueta(value = "Classe")
    private ClasseCredor classeCredor;
    @ManyToOne
    @JoinColumn(name = "configajusteativodisp_id")
    private ConfigAjusteAtivoDisponivel configAjusteAtivoDisponivel;
    @ManyToOne
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    private String historicoNota;
    private String historicoRazao;
    private String uuid;
    @ManyToOne
    private Identificador identificador;

    public AjusteAtivoDisponivel() {
        dataAjuste = new Date();
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        tipoLancamento = TipoLancamento.NORMAL;
        uuid = UUID.randomUUID().toString();
    }

    public AjusteAtivoDisponivel(String numero, Date dataAjuste, TipoLancamento tipoLancamento, ConfigAjusteAtivoDisponivel configAjusteAtivoDisponivel, EventoContabil eventoContabil, TipoAjusteDisponivel tipoAjusteDisponivel, SubConta contaFinanceira, FonteDeRecursos fonteDeRecursos, Pessoa pessoa, ClasseCredor classeCredor, String historico, BigDecimal valor, UnidadeOrganizacional unidadeOrganizacional) {
        this.numero = numero;
        this.dataAjuste = dataAjuste;
        this.tipoLancamento = tipoLancamento;
        this.tipoAjusteDisponivel = tipoAjusteDisponivel;
        this.contaFinanceira = contaFinanceira;
        this.fonteDeRecursos = fonteDeRecursos;
        this.pessoa = pessoa;
        this.classeCredor = classeCredor;
        this.historico = historico;
        this.valor = valor;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.configAjusteAtivoDisponivel = configAjusteAtivoDisponivel;
        this.eventoContabil = eventoContabil;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubConta getContaFinanceira() {
        return contaFinanceira;
    }

    public void setContaFinanceira(SubConta contaFinanceira) {
        this.contaFinanceira = contaFinanceira;
    }

    public Date getDataAjuste() {
        return dataAjuste;
    }

    public void setDataAjuste(Date dataAjuste) {
        this.dataAjuste = dataAjuste;
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

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
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

    public TipoAjusteDisponivel getTipoAjusteDisponivel() {
        return tipoAjusteDisponivel;
    }

    public void setTipoAjusteDisponivel(TipoAjusteDisponivel tipoAjusteDisponivel) {
        this.tipoAjusteDisponivel = tipoAjusteDisponivel;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ConfigAjusteAtivoDisponivel getConfigAjusteAtivoDisponivel() {
        return configAjusteAtivoDisponivel;
    }

    public void setConfigAjusteAtivoDisponivel(ConfigAjusteAtivoDisponivel configAjusteAtivoDisponivel) {
        this.configAjusteAtivoDisponivel = configAjusteAtivoDisponivel;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
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

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°:" + this.getNumero() + "/" + Util.getAnoDaData(this.getDataAjuste()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getFonteDeRecursos() != null) {
            historicoNota += "Conta de Destinação de Recurso: " + this.getContaDeDestinacao().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getContaFinanceira() != null) {
            historicoNota += "Conta Financeira: " + this.getContaFinanceira() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPessoa() != null) {
            historicoNota += "Pessoa: " + this.getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getClasseCredor() != null) {
            historicoNota += "Classe: " + this.getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        return numero + " - " + contaFinanceira + " - " + Util.formataValor(valor) + " - " + DataUtil.getDataFormatada(dataAjuste);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero;
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(), null, getContaDeDestinacao(), null, null, null, null, getContaDeDestinacao().getExercicio(), null, null, 0, null, null);
    }
}
