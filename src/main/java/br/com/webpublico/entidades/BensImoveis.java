package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeMap;

/**
 * @author Fabio
 */
@Entity
@Audited
@Etiqueta("Bens Imóveis")

public class BensImoveis extends SuperEntidade implements EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;

    @Etiqueta("Data")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ReprocessamentoContabil
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataBem;

    @Etiqueta("Tipo de Lançamento")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipoLancamento;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Operação")
    private TipoOperacaoBensImoveis tipoOperacaoBemEstoque;

    @Etiqueta("Unidade organizacional")
    @ReprocessamentoContabil
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Obrigatorio
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Etiqueta("Tipo de Ingresso")
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private TipoIngresso tipoIngresso;

    @Etiqueta("Tipo de Baixa")
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private TipoBaixaBens tipoBaixaBens;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo Patrimonial")
    private GrupoBem grupoBem;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Histórico")
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


    public BensImoveis() {
        super();
        this.tipoLancamento = TipoLancamento.NORMAL;
        this.valor = BigDecimal.ZERO;
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

    public TipoOperacaoBensImoveis getTipoOperacaoBemEstoque() {
        return tipoOperacaoBemEstoque;
    }

    public void setTipoOperacaoBemEstoque(TipoOperacaoBensImoveis tipoOperacaoBemEstoque) {
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

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public void gerarHistoricoNota(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N°: " + this.getNumero() + "/" + Util.getAnoDaData(this.getDataBem()) + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (TipoOperacaoBensImoveis.ALIENACAO_BENS_IMOVEIS.equals(getTipoOperacaoBemEstoque())) {
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
            historicoNota += " Grupo Patrimonial: " + this.getGrupoBem().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
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
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(), contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }
}
