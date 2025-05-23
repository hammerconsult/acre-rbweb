/*
 * Codigo gerado automaticamente em Thu Sep 20 10:51:57 GMT-03:00 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.administrativo.dao.JdbcReducaoValorBemDAO;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Stateless
public class BensMoveisFacade extends SuperFacadeContabil<BensMoveis> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private TipoIngressoFacade tipoIngressoFacade;
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ConfigBensMoveisFacade configBensMoveisFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ConfigAlienacaoAtivosFacade configAlienacaoAtivosFacade;
    private JdbcReducaoValorBemDAO jdbcReducaoValorBemDAO;

    public BensMoveisFacade() {
        super(BensMoveis.class);
    }

    @PostConstruct
    public void init(){
        try {
        jdbcReducaoValorBemDAO = (JdbcReducaoValorBemDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcReducaoValorBemDAO");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM BENSMOVEIS CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    public void geraContabilizacaoBensMoveis(BensMoveis bensMoveis, boolean simulacao) {
        try {
            contabilizarBensMoveis(bensMoveis, simulacao);
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }

    }

    private void contabilizarBensMoveis(BensMoveis entity) {
        contabilizarBensMoveis(entity, false);
    }

    private EventoContabil buscarEventoContabilBensMoveis(BensMoveis entity) {
        EventoContabil eventoContabil = null;
        ConfigBensMoveis configuracao = configBensMoveisFacade.buscarEventoContabilPorOperacaoLancamentoAndDataMov(
            entity.getTipoOperacaoBemEstoque(),
            entity.getTipoLancamento(),
            entity.getDataBensMoveis());
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil, Boolean simulacao) {
        try {
            BensMoveis entity = (BensMoveis) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(entity, simulacao);
            ItemParametroEvento item = criarItemParametroEvento(entity.getValor(), parametroEvento);

            List<ObjetoParametro> listaObj = criarObjetosParametros(entity, item);
            item.setObjetoParametros(listaObj);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private ParametroEvento criarParametroEvento(BensMoveis entity, Boolean simulacao) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataBensMoveis());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(BensMoveis.class.getSimpleName());
        parametroEvento.setIdOrigem(simulacao ? null : entity.getId().toString());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private List<ObjetoParametro> criarObjetosParametros(BensMoveis entity, ItemParametroEvento item) {
        List<ObjetoParametro> objetosParametro = Lists.newArrayList();
        if (entity.getId() != null) {
            objetosParametro.add(new ObjetoParametro(entity.getId().toString(), BensMoveis.class.getSimpleName(), item));
        }
        objetosParametro.add(new ObjetoParametro(entity.getTipoOperacaoBemEstoque().toString(), TipoOperacaoBensMoveis.class.getSimpleName(), item));
        if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(entity.getTipoOperacaoBemEstoque())) {
            objetosParametro.add(new ObjetoParametro(entity.getGrupoBem().getId().toString(), GrupoBem.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.CREDITO));
            objetosParametro.add(new ObjetoParametro(entity.getTipoGrupo().name(), TipoGrupo.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.CREDITO));

            ConfigAlienacaoAtivos configAlienacao = buscarConfigAlienacaoAtivos(entity);
            objetosParametro.add(new ObjetoParametro(configAlienacao.getGrupo().getId().toString(), GrupoBem.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.DEBITO));
            objetosParametro.add(new ObjetoParametro(configAlienacao.getTipoGrupo().name(), TipoGrupo.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.DEBITO));
        } else {
            objetosParametro.add(new ObjetoParametro(entity.getGrupoBem().getId().toString(), GrupoBem.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.AMBOS));
            objetosParametro.add(new ObjetoParametro(entity.getTipoGrupo().name(), TipoGrupo.class.getSimpleName(), item, ObjetoParametro.TipoObjetoParametro.AMBOS));
        }
        return objetosParametro;
    }

    private void contabilizarBensMoveis(BensMoveis entity, boolean simulacao) {
        try {
            EventoContabil eventoContabil = buscarEventoContabilBensMoveis(entity);
            if (eventoContabil != null && eventoContabil.getId() != null) {
                entity.setEventoContabil(eventoContabil);
                gerarHistorico(entity);
                contabilizacaoFacade.contabilizar(getParametroEvento(entity, simulacao), simulacao);
            }
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }

    }

    public void salvarAndContabilizarIntegracaoNaoValidandoSaldoGrupo(BensMoveis bensMoveis, boolean simulacao) {
        try {
            BensMoveis entity = salvarNovoBensMoveis(bensMoveis, simulacao);
            if (!simulacao) {
                gerarSaldoBensMoveis(entity, false);
            }
            geraContabilizacaoBensMoveis(entity, simulacao);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void simularContabilizacaoIntegracao(BensMoveis bensMoveis) {
        try {
            if (bensMoveis.getEventoContabil() == null) {
                buscarEventoContabil(bensMoveis);
            }
            geraContabilizacaoBensMoveis(bensMoveis, true);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void salvarContabilizandoBemMovelIntegracaoReducaoValorBemNormal(List<ReducaoValorBemContabil> associacoes, BensMoveis bensMoveis, boolean simulacao) {
        try {
            BensMoveis entity = salvarAndContabilizarBemMovelIntegracao(bensMoveis, simulacao);
            if (!simulacao) {
                for (ReducaoValorBemContabil associacao : associacoes) {
                    associacao.setBensMoveis(entity);
                    associacao.setSituacao(SituacaoReducaoValorBem.CONCLUIDA);
                    jdbcReducaoValorBemDAO.atualizarReducaoValorBemContabil(associacao);
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            if (!simulacao) {
                for (ReducaoValorBemContabil associacao : associacoes) {
                    associacao.setSituacao(SituacaoReducaoValorBem.EM_ANDAMENTO);
                    jdbcReducaoValorBemDAO.atualizarReducaoValorBemContabil(associacao);
                }
            }
        }

    }

    public void salvarContabilizandoBemMovelIntegracaoReducaoValorEstorno(List<ReducaoValorBemEstornoContabil> associacoes, BensMoveis bensMoveis, boolean simulacao) {
        BensMoveis entity = salvarAndContabilizarBemMovelIntegracao(bensMoveis, simulacao);
        if (!simulacao) {
            for (ReducaoValorBemEstornoContabil associacao : associacoes) {
                associacao.setBensMoveis(entity);
                associacao.setSituacao(SituacaoReducaoValorBem.CONCLUIDA);
                jdbcReducaoValorBemDAO.atualizarReducaoValorBemEstornoContabil(associacao);
            }
        }
    }

    public BensMoveis salvarAndContabilizarBemMovelIntegracao(BensMoveis bensMoveis, boolean simulacao) {
        try {
            BensMoveis entity = salvarNovoBensMoveis(bensMoveis, simulacao);
            if (!simulacao) {
                gerarSaldoBensMoveis(entity, true);
            }
            geraContabilizacaoBensMoveis(entity, simulacao);
            return entity;
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void salvarNovo(BensMoveis bensMoveis) {
        try {
            BensMoveis entity = salvarNovoBensMoveis(bensMoveis, false);
            gerarSaldoBensMoveis(entity, true);
            geraContabilizacaoBensMoveis(entity, false);
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private BensMoveis salvarNovoBensMoveis(BensMoveis bensMoveis, Boolean simulacao) {
        if (bensMoveis.getId() == null) {
            if (bensMoveis.getEventoContabil() == null) {
                buscarEventoContabil(bensMoveis);
            }
            if (!simulacao) {
                bensMoveis.setNumero(singletonGeradorCodigoContabil.getNumeroBensMoveis(bensMoveis.getExercicio()));
                gerarHistorico(bensMoveis);
                em.persist(bensMoveis);
            }
        } else {
            if (!simulacao) {
                gerarHistorico(bensMoveis);
                bensMoveis = em.merge(bensMoveis);
            }
        }
        return bensMoveis;
    }

    private void gerarHistorico(BensMoveis entity) {
        TipoGrupo tipoGrupo = null;
        GrupoBem grupoBem = null;
        if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(entity.getTipoOperacaoBemEstoque())) {
            ConfigAlienacaoAtivos configAlienacaoAtivos = buscarConfigAlienacaoAtivos(entity);
            tipoGrupo = configAlienacaoAtivos.getTipoGrupo();
            grupoBem = configAlienacaoAtivos.getGrupo();
        }
        entity.gerarHistoricos(tipoGrupo, grupoBem, hierarquiaOrganizacionalFacade);
    }

    private void gerarSaldoBensMoveis(BensMoveis bensMoveis, Boolean validarSaldo) {
        if (!TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS.equals(bensMoveis.getTipoOperacaoBemEstoque())) {
            if (TipoOperacaoBensMoveis.getOperacoesParaUnicoSaldoDebito().contains(bensMoveis.getTipoOperacaoBemEstoque())) {
                gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.DEBITO, validarSaldo);

            } else if (TipoOperacaoBensMoveis.getOperacoesParaUnicoSaldoCredito().contains(bensMoveis.getTipoOperacaoBemEstoque())) {
                gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.CREDITO, validarSaldo);

            } else {
                gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.CREDITO, validarSaldo);

                if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(bensMoveis.getTipoOperacaoBemEstoque())) {
                    gerarSaldoBensMoveisPorConfiguracaoAlienacao(bensMoveis, TipoOperacao.DEBITO);
                } else {
                    gerarSaldoBensMoveisPorTipoOperacao(bensMoveis, TipoOperacao.DEBITO, validarSaldo);
                }
            }
        }
    }

    private void gerarSaldoBensMoveisPorConfiguracaoAlienacao(BensMoveis bensMoveis, TipoOperacao tipoOperacao) {
        ConfigAlienacaoAtivos configAlienacaoAtivos = buscarConfigAlienacaoAtivos(bensMoveis);
        saldoGrupoBemFacade.geraSaldoGrupoBemMoveis(
            bensMoveis.getUnidadeOrganizacional(),
            configAlienacaoAtivos.getGrupo(),
            bensMoveis.getValor(),
            configAlienacaoAtivos.getTipoGrupo(),
            bensMoveis.getDataBensMoveis(),
            bensMoveis.getTipoOperacaoBemEstoque(),
            bensMoveis.getTipoLancamento(),
            tipoOperacao,
            true);
    }

    private void gerarSaldoBensMoveisPorTipoOperacao(BensMoveis bensMoveis, TipoOperacao tipoOperacao, Boolean validarSaldo) {
        saldoGrupoBemFacade.geraSaldoGrupoBemMoveis(
            bensMoveis.getUnidadeOrganizacional(),
            bensMoveis.getGrupoBem(),
            bensMoveis.getValor(),
            bensMoveis.getTipoGrupo(),
            bensMoveis.getDataBensMoveis(),
            bensMoveis.getTipoOperacaoBemEstoque(),
            bensMoveis.getTipoLancamento(),
            tipoOperacao,
            validarSaldo);
    }

    private ConfigAlienacaoAtivos buscarConfigAlienacaoAtivos(BensMoveis bensMoveis) {
        ConfigAlienacaoAtivos configAlienacaoAtivos = null;
        if (TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS.equals(bensMoveis.getTipoOperacaoBemEstoque())) {
            ConfigBensMoveis configuracao = configBensMoveisFacade.buscarEventoContabilPorOperacaoLancamentoAndDataMov(bensMoveis.getTipoOperacaoBemEstoque(), bensMoveis.getTipoLancamento(), bensMoveis.getDataBensMoveis());
            if (configuracao == null) {
                throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Bens Móveis. ");
            }
            configAlienacaoAtivos = configAlienacaoAtivosFacade.buscarConfigVigente(configuracao.getEventoContabil(), bensMoveis.getDataBensMoveis());
            if (configAlienacaoAtivos == null) {
                throw new ExcecaoNegocioGenerica("Não foi encontrado uma configuração de alienação de ativos para o evento: " + configuracao.getEventoContabil().toString());
            }
        }
        return configAlienacaoAtivos;
    }

    @Override
    public void salvar(BensMoveis entity) {
        gerarHistorico(entity);
        em.merge(entity);
    }

    public void buscarEventoContabil(BensMoveis selecionado) {
        try {
            selecionado.setEventoContabil(null);
            if (selecionado.getTipoOperacaoBemEstoque() != null) {
                ConfigBensMoveis configBensMoveis = getConfigBensMoveisFacade().buscarEventoContabilPorOperacaoLancamentoAndDataMov(selecionado.getTipoOperacaoBemEstoque(), selecionado.getTipoLancamento(), selecionado.getDataBensMoveis());
                Preconditions.checkNotNull(configBensMoveis, "Configuração de Evento Contábil não encontrada para os parâmetros informados.");
                selecionado.setEventoContabil(configBensMoveis.getEventoContabil());
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarBensMoveis((BensMoveis) entidadeContabil);
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(BensMoveis.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataBensMoveis)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataBensMoveis)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_BENS_MOVEIS, "obj.tipoOperacaoBemEstoque");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.GRUPO_PATRIMONIAL, "obj.grupoBem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        return Arrays.asList(consulta);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public TipoIngressoFacade getTipoIngressoFacade() {
        return tipoIngressoFacade;
    }

    public TipoBaixaBensFacade getTipoBaixaBensFacade() {
        return tipoBaixaBensFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public ConfigBensMoveisFacade getConfigBensMoveisFacade() {
        return configBensMoveisFacade;
    }

    public SaldoGrupoBemMovelFacade getSaldoGrupoBemFacade() {
        return saldoGrupoBemFacade;
    }
}
