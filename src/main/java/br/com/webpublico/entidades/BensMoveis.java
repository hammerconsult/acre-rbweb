/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author major
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Bens Móveis")
public class BensMoveis extends SuperEntidadeContabilGerarContaAuxiliar implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;

    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ReprocessamentoContabil
    private Date dataBensMoveis;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Lançamento")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private TipoLancamento tipoLancamento;

    @Etiqueta("Operação")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoOperacaoBensMoveis tipoOperacaoBemEstoque;

    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;

    @Obrigatorio
    @Monetario
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Ingresso")
    private TipoIngresso tipoIngresso;

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Baixa")
    private TipoBaixaBens tipoBaixaBens;

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Grupo Patrimonial")
    @Obrigatorio
    private GrupoBem grupoBem;

    @Etiqueta("Histórico")
    @Obrigatorio
    @Tabelavel
    private String historico;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Grupo")
    private TipoGrupo tipoGrupo;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    private String historicoNota;
    private String historicoRazao;

    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    /**
     * Campo utilizado na efetivação da baixa para identificar o bem que gerou um execption no contábil
     */
    @Transient
    private Bem bem;

    public BensMoveis() {
        super();
        this.valor = BigDecimal.ZERO;
    }

    public BensMoveis(String numero, Date dataBensMoveis, TipoLancamento tipoLancamento, UnidadeOrganizacional unidadeOrganizacional, TipoOperacaoBensMoveis tipoOperacaoBemEstoque, TipoIngresso tipoIngresso, TipoBaixaBens tipoBaixaBens, GrupoBem grupoBem, Pessoa pessoa, ClasseCredor classeCredor, String historico, BigDecimal valor) {
        this.numero = numero;
        this.dataBensMoveis = dataBensMoveis;
        this.tipoLancamento = tipoLancamento;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoOperacaoBemEstoque = tipoOperacaoBemEstoque;
        this.tipoIngresso = tipoIngresso;
        this.tipoBaixaBens = tipoBaixaBens;
        this.grupoBem = grupoBem;
        this.historico = historico;
        this.valor = valor;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getDataBensMoveis() {
        return dataBensMoveis;
    }

    public void setDataBensMoveis(Date dataBensMoveis) {
        this.dataBensMoveis = dataBensMoveis;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
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


    public TipoBaixaBens getTipoBaixaBens() {
        return tipoBaixaBens;
    }

    public void setTipoBaixaBens(TipoBaixaBens tipoBaixaBens) {
        this.tipoBaixaBens = tipoBaixaBens;
    }

    public TipoIngresso getTipoIngresso() {
        return tipoIngresso;
    }

    public void setTipoIngresso(TipoIngresso tipoIngresso) {
        this.tipoIngresso = tipoIngresso;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public TipoOperacaoBensMoveis getTipoOperacaoBemEstoque() {
        return tipoOperacaoBemEstoque;
    }

    public void setTipoOperacaoBemEstoque(TipoOperacaoBensMoveis tipoOperacaoBemEstoque) {
        this.tipoOperacaoBemEstoque = tipoOperacaoBemEstoque;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
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

    public void gerarHistoricoNota(TipoGrupo tipoGrupoIntegracao, GrupoBem grupoIntegracao, HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataBensMoveis()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(getTipoOperacaoBemEstoque())) {
            HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataBensMoveis, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
            if (hierarquia != null) {
                historicoNota += " Unidade Orçamentária de Origem: " + hierarquia.getCodigo() + " - " + hierarquia.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
                historicoNota += " Unidade Orçamentária de Destino: " + hierarquia.getCodigo() + " - " + hierarquia.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            gerarGrupoAndTipoGrupoIntegracao(tipoGrupoIntegracao, grupoIntegracao);
        } else {
            if (this.getTipoGrupo() != null) {
                historicoNota += " Tipo de Bem: " + this.getTipoGrupo().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (this.getGrupoBem() != null) {
                historicoNota += " Grupo Patrimonial: " + this.getGrupoBem().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getTipoOperacaoBemEstoque() != null) {
            historicoNota += " Operação: " + this.getTipoOperacaoBemEstoque().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + " " + historico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    private void gerarGrupoAndTipoGrupoIntegracao(TipoGrupo tipoGrupoIntegracao, GrupoBem grupoIntegracao) {
        if (this.getTipoGrupo() != null) {
            historicoNota += " Tipo Grupo Origem: " + this.getTipoGrupo().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getGrupoBem() != null) {
            historicoNota += " Grupo Patrimonial Origem: " + this.getGrupoBem().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (tipoGrupoIntegracao != null) {
            historicoNota += " Tipo Grupo Destino: " + tipoGrupoIntegracao.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (grupoIntegracao != null) {
            historicoNota += " Grupo Patrimonial Destino: " + grupoIntegracao.toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos(TipoGrupo tipoGrupoIntegracao, GrupoBem grupoIntegracao, HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        gerarHistoricoNota(tipoGrupoIntegracao, grupoIntegracao, hierarquiaOrganizacionalFacade);
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        return numero;
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + grupoBem.toString();
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
