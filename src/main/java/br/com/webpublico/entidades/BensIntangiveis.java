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
import br.com.webpublico.enums.TipoOperacaoBensIntangiveis;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Claudio
 */
@Entity
@Audited
@Etiqueta("Bens Intangíveis")

public class BensIntangiveis extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

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
    @Obrigatorio
    @Etiqueta("Data")
    @ReprocessamentoContabil
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataBem;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Lançamento")
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Operação")
    private TipoOperacaoBensIntangiveis tipoOperacaoBemEstoque;
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @ReprocessamentoContabil
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Obrigatorio
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
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Histórico")
    private String historico;
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Grupo")
    private TipoGrupo tipoGrupo;
    @ManyToOne
    @Obrigatorio
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Tabelavel
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;
    private String historicoNota;
    private String historicoRazao;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;


    public BensIntangiveis() {
        this.dataBem = new Date();
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.valor = BigDecimal.ZERO;
        criadoEm = System.nanoTime();
    }

    public Exercicio getExercicio() {
        return exercicio;
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

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataBem() {
        return dataBem;
    }

    public void setDataBem(Date dataBem) {
        this.dataBem = dataBem;
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

    public TipoOperacaoBensIntangiveis getTipoOperacaoBemEstoque() {
        return tipoOperacaoBemEstoque;
    }

    public void setTipoOperacaoBemEstoque(TipoOperacaoBensIntangiveis tipoOperacaoBemEstoque) {
        this.tipoOperacaoBemEstoque = tipoOperacaoBemEstoque;
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

    public void gerarHistoricoNota(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        historicoNota = "";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataBem()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (TipoOperacaoBensIntangiveis.ALIENACAO_BENS_INTANGIVEIS.equals(getTipoOperacaoBemEstoque())) {
            HierarquiaOrganizacional hierarquia = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataBem, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
            if (hierarquia != null) {
                historicoNota += " Unidade Orçamentária de Origem: " + hierarquia.getCodigo() + " - " + hierarquia.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
                historicoNota += " Unidade Orçamentária de Destino: " + hierarquia.getCodigo() + " - " + hierarquia.getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }
        if (this.getTipoGrupo() != null) {
            historicoNota += " Tipo de Bem: " + this.getTipoGrupo().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getGrupoBem() != null) {
            historicoNota += " Grupo Patrimonial: " + this.getGrupoBem() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getTipoOperacaoBemEstoque() != null) {
            historicoNota += " Operação: " + this.getTipoOperacaoBemEstoque().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
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

    public void gerarHistoricos(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        gerarHistoricoNota(hierarquiaOrganizacionalFacade);
        gerarHistoricoRazao();
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return numero;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
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
