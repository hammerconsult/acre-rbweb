package br.com.webpublico.negocios.contabil.financeiro;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelatorioPesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.contabil.AcompanhamentoEventoContabil;
import br.com.webpublico.entidadesauxiliares.contabil.AcompanhamentoLancamentoContabil;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.contabil.reprocessamento.ContabilizadorFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.util.EntidadeMetaData;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by renatoromanini on 16/10/17.
 */
@Stateless
public class AcompanhamentoLancamentoContabilFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(AcompanhamentoLancamentoContabilFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContabilizadorFacade contabilizadorFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public AcompanhamentoLancamentoContabilFacade() {

    }

    public List<LancamentoContabil> recuperarLancamentosContabeis(EntidadeContabil entidadeContabil) {
        return contabilizacaoFacade.recuperarLancamentosContabeis(entidadeContabil);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void recuperarLancamentos(AcompanhamentoLancamentoContabil selecionado) {
        selecionado.setEventos(Lists.<AcompanhamentoEventoContabil>newArrayList());
        if (selecionado.getEventoContabil() == null) {
            List<EventoContabil> eventoContabils = eventoContabilFacade.listaEventoContabilPorTipoEvento("", selecionado.getTipoEventoContabil());
            for (EventoContabil eventoContabil : eventoContabils) {
                criarEventosReprocessar(selecionado, eventoContabil);
            }
        } else {
            criarEventosReprocessar(selecionado, selecionado.getEventoContabil());
        }
        ordenarPorEvento(selecionado);
    }

    public void ordenarPorEvento(AcompanhamentoLancamentoContabil selecionado) {
        Collections.sort(selecionado.getEventos(), new Comparator<AcompanhamentoEventoContabil>() {
            @Override
            public int compare(AcompanhamentoEventoContabil o1, AcompanhamentoEventoContabil o2) {
                return o1.getEventosReprocessar().getEventoContabil().getCodigo().compareTo(o2.getEventosReprocessar().getEventoContabil().getCodigo());
            }
        });
    }

    public void criarEventosReprocessar(AcompanhamentoLancamentoContabil selecionado, EventoContabil eventoContabil) {
        EventosReprocessar eventosReprocessar = new EventosReprocessar();
        eventosReprocessar.setEventoContabil(eventoContabil);
        eventosReprocessar.setDataInicial(selecionado.getDataInicial());
        eventosReprocessar.setDataFinal(selecionado.getDataFinal());
        List<EventoReprocessarUO> uos = Lists.newArrayList();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : selecionado.getHierarquias()) {
            EventoReprocessarUO uo = new EventoReprocessarUO(eventosReprocessar, hierarquiaOrganizacional.getSubordinada());
            uos.add(uo);
        }

        eventosReprocessar.setEventoReprocessarUOs(uos);
        List<FiltroConsultaMovimentoContabil> filtros = getFiltroConsultaMovimentoContabils(selecionado, eventoContabil);

        SuperFacadeContabil facade = contabilizadorFacade.getFacade(eventosReprocessar);
        if (facade == null) {
            logger.error("Não foi encontrado facade para o tipo de evento contábil " +
                eventoContabil.getTipoEventoContabil().name() +
                " em ContabilizadorFacade, método retornarFacade.");
            return;
        }
        List<ConsultaMovimentoContabil> consultas = buscarConsultasMovimentosContabeis(selecionado, eventoContabil, filtros, facade);
        montarOrdenacaoQuantidadeRegistro(consultas, selecionado);
        List<EntidadeContabil> entidadeContabils = contabilizadorFacade.recuperarObjetos(consultas);

        if (entidadeContabils != null && !entidadeContabils.isEmpty()) {
            eventosReprocessar.getObjetosParaReprocessar().addAll(entidadeContabils);
            try {
                EntidadeContabil objeto = entidadeContabils.get(0);
                EntidadeMetaData entidadeMetaData = new EntidadeMetaData(objeto.getClass());
                RelatorioPesquisaGenerico relatorioPesquisaGenerico = new RelatorioPesquisaGenerico(objeto, entidadeMetaData);
                selecionado.getEventos().add(new AcompanhamentoEventoContabil(eventosReprocessar, relatorioPesquisaGenerico));
            } catch (Exception e) {
                logger.error("Erro ao tentar recupear os lançamentos do evento " + eventoContabil.getCodigo() + ". Detalhes do eroro: " + e.getMessage());
            }
        }
    }

    private List<ConsultaMovimentoContabil> buscarConsultasMovimentosContabeis(AcompanhamentoLancamentoContabil selecionado, EventoContabil eventoContabil,
                                                                               List<FiltroConsultaMovimentoContabil> filtros, SuperFacadeContabil facade) {
        if (selecionado.getMostrarContabilizacao() || selecionado.getIdParametroEvento() != null) {
            return Lists.newArrayList(montarConsultaLancamentoContabil(eventoContabil, filtros));
        }
        return facade.criarConsulta(filtros);
    }

    private void montarOrdenacaoQuantidadeRegistro(List<ConsultaMovimentoContabil> consultas, AcompanhamentoLancamentoContabil selecionado) {
        for (ConsultaMovimentoContabil consulta : consultas) {
            consulta.alterarMaximoRegistros(selecionado.getQuantidadeDeRegistros());
            consulta.adicionarOrdenacao(selecionado.getCampoOrdenar());
        }
    }

    public List<FiltroConsultaMovimentoContabil> getFiltroConsultaMovimentoContabils(AcompanhamentoLancamentoContabil selecionado, EventoContabil eventoContabil) {
        List<FiltroConsultaMovimentoContabil> filtros = Lists.newArrayList();
        montarFiltrosComuns(selecionado, eventoContabil, filtros);
        montarFiltrosDespesa(selecionado, filtros);
        montarFiltrosReceita(selecionado, filtros);
        montarFiltrosFontes(selecionado, filtros);
        montarFiltrosCredor(selecionado, filtros);
        montarFiltrosFinanceiro(selecionado, filtros);
        montarFiltrosPatrimonioMaterial(selecionado, filtros);
        montarFiltrosDividaPublica(selecionado, filtros);
        return filtros;
    }

    public void montarFiltrosComuns(AcompanhamentoLancamentoContabil selecionado, EventoContabil eventoContabil, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getHierarquias() != null && !selecionado.getHierarquias().isEmpty()) {
            filtros.add(new FiltroConsultaMovimentoContabil(ConsultaMovimentoContabil.getUnidades(selecionado.getHierarquias()), ConsultaMovimentoContabil.Operador.IN, ConsultaMovimentoContabil.Campo.LISTA_UNIDADE));
        } else {
            filtros.add(new FiltroConsultaMovimentoContabil(null, ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.UNIDADE));
        }
        try {
            if (selecionado.getNumero() != null && !selecionado.getNumero().trim().isEmpty()) {
                filtros.add(new FiltroConsultaMovimentoContabil(Long.parseLong(selecionado.getNumero()), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.NUMERO));
            } else {
                filtros.add(new FiltroConsultaMovimentoContabil(null, ConsultaMovimentoContabil.Operador.LIKE, ConsultaMovimentoContabil.Campo.NUMERO));
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("O filtro número está em formato inválido!");
        }
        if (selecionado.getIdParametroEvento() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getIdParametroEvento(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.ID_PARAMETRO_EVENTO));
        } else {
            filtros.add(new FiltroConsultaMovimentoContabil(null, ConsultaMovimentoContabil.Operador.LIKE, ConsultaMovimentoContabil.Campo.ID_PARAMETRO_EVENTO));
        }
        if (selecionado.getContasDebitos() != null && !selecionado.getContasDebitos().isEmpty()) {
            filtros.add(new FiltroConsultaMovimentoContabil(ConsultaMovimentoContabil.getCodigosDasContas(selecionado.getContasDebitos()), ConsultaMovimentoContabil.Operador.IN, ConsultaMovimentoContabil.Campo.CONTAS_DEBITOS));
        }
        if (selecionado.getContasCreditos() != null && !selecionado.getContasCreditos().isEmpty()) {
            filtros.add(new FiltroConsultaMovimentoContabil(ConsultaMovimentoContabil.getCodigosDasContas(selecionado.getContasCreditos()), ConsultaMovimentoContabil.Operador.IN, ConsultaMovimentoContabil.Campo.CONTAS_CREDITOS));
        }
        if (selecionado.getFontesDebitos() != null && !selecionado.getFontesDebitos().isEmpty()) {
            filtros.add(new FiltroConsultaMovimentoContabil(ConsultaMovimentoContabil.getCodigosDasFontes(selecionado.getFontesDebitos()), ConsultaMovimentoContabil.Operador.IN, ConsultaMovimentoContabil.Campo.FONTES_DEBITOS));
        }
        if (selecionado.getFontesCreditos() != null && !selecionado.getFontesCreditos().isEmpty()) {
            filtros.add(new FiltroConsultaMovimentoContabil(ConsultaMovimentoContabil.getCodigosDasFontes(selecionado.getFontesCreditos()), ConsultaMovimentoContabil.Operador.IN, ConsultaMovimentoContabil.Campo.FONTES_CREDITOS));
        }
        if (selecionado.getValor() != null && selecionado.getValor().compareTo(BigDecimal.ZERO) != 0) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getValor(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.VALOR));
        } else {
            filtros.add(new FiltroConsultaMovimentoContabil(null, ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.VALOR));
        }
        if (selecionado.getHistorico() != null && !selecionado.getHistorico().trim().isEmpty()) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getHistorico(), ConsultaMovimentoContabil.Operador.LIKE, ConsultaMovimentoContabil.Campo.HISTORICO));
        }
        if (eventoContabil != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(eventoContabil.getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL));
        }
        filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getDataInicial(), ConsultaMovimentoContabil.Operador.MAIOR_IGUAL, ConsultaMovimentoContabil.Campo.DATA_INICIAL));
        filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getDataFinal(), ConsultaMovimentoContabil.Operador.MENOR_IGUAL, ConsultaMovimentoContabil.Campo.DATA_FINAL));
    }

    public void montarFiltrosCredor(AcompanhamentoLancamentoContabil selecionado, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getCredor() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getCredor().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.PESSOA));
        }
        if (selecionado.getClasseCredor() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getClasseCredor().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.CLASSE));
        }
    }

    public void montarFiltrosFontes(AcompanhamentoLancamentoContabil selecionado, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getFonteDeRecursos() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getFonteDeRecursos().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO));
        }
    }

    public void montarFiltrosDespesa(AcompanhamentoLancamentoContabil selecionado, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getContaDespesa() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getContaDespesa().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.CONTA_DESPESA));
        }
    }

    public void montarFiltrosReceita(AcompanhamentoLancamentoContabil selecionado, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getOperacaoReceita() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getOperacaoReceita(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.OPERACAO_RECEITA));
        }
        if (selecionado.getContaReceita() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getContaReceita().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.CONTA_RECEITA));
        }
    }

    public void montarFiltrosFinanceiro(AcompanhamentoLancamentoContabil selecionado, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getContaFinanceira() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getContaFinanceira().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA));
        }
    }

    public void montarFiltrosDividaPublica(AcompanhamentoLancamentoContabil selecionado, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getOperacaoMovimentoDividaPublica() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getOperacaoMovimentoDividaPublica(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.OPERACAO_DIVIDAPUBLICA));
        }
    }

    public void montarFiltrosPatrimonioMaterial(AcompanhamentoLancamentoContabil selecionado, List<FiltroConsultaMovimentoContabil> filtros) {
        if (selecionado.getTipoOperacaoBensEstoque() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getTipoOperacaoBensEstoque(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_ESTOQUE));
        }
        if (selecionado.getTipoEstoque() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getTipoEstoque(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.TIPO_ESTOQUE));
        }
        if (selecionado.getGrupoMaterial() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getGrupoMaterial().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.GRUPO_MATERIAL));
        }
        if (selecionado.getTipoOperacaoBensMoveis() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getTipoOperacaoBensMoveis(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_BENS_MOVEIS));
        }
        if (selecionado.getTipoOperacaoBensImoveis() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getTipoOperacaoBensImoveis(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_BENS_IMOVEIS));
        }
        if (selecionado.getTipoOperacaoBensIntangiveis() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getTipoOperacaoBensIntangiveis(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_BENS_INTANGIVEIS));
        }
        if (selecionado.getGrupoBem() != null) {
            filtros.add(new FiltroConsultaMovimentoContabil(selecionado.getGrupoBem().getId(), ConsultaMovimentoContabil.Operador.IGUAL, ConsultaMovimentoContabil.Campo.GRUPO_PATRIMONIAL));
        }
    }

    private ConsultaMovimentoContabil montarConsultaLancamentoContabil(EventoContabil eventoContabil, List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consultaLancamentoContabil = new ConsultaMovimentoContabil(LancamentoContabil.class, filtros);
        consultaLancamentoContabil.incluirJoinsComplementar(" inner join itemparametroevento itempe on obj.itemparametroevento_id = itempe.id " +
            " inner join parametroevento par on itempe.parametroevento_id = par.id " +
            " inner join eventocontabil eve on par.eventocontabil_id = eve.id and eve.tipoeventocontabil = '" + eventoContabil.getTipoEventoContabil().name() + "'" +
            " left join conta cDebito on cDebito.id = obj.contadebito_id " +
            " left join conta cCredito on cCredito.id = obj.contacredito_id " +
            " left join contaauxiliardetalhada cadDebito on cadDebito.id = obj.contaauxdebdetalhadasiconfi_id " +
            " left join contadedestinacao cddDebito on cddDebito.id = cadDebito.contadedestinacao_id " +
            " left join fontederecursos fonteDebito on fonteDebito.id = cddDebito.fontederecursos_id " +
            " left join contaauxiliardetalhada cadCredito on cadCredito.id = obj.contaauxcredetalhadasiconfi_id " +
            " left join contadedestinacao cddCredito on cddCredito.id = cadCredito.contadedestinacao_id " +
            " left join fontederecursos fonteCredito on fonteCredito.id = cddCredito.fontederecursos_id ");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "obj.dataLancamento");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "obj.dataLancamento");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "eve.id");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTAS_DEBITOS, "cDebito.codigo");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTAS_CREDITOS, "cCredito.codigo");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTES_DEBITOS, "fonteDebito.codigo");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTES_CREDITOS, "fonteCredito.codigo");
        consultaLancamentoContabil.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.ID_PARAMETRO_EVENTO, "par.idorigem");
        return consultaLancamentoContabil;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }


    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
