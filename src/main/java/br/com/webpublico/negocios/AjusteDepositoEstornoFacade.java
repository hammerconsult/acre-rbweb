package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mga on 24/08/2017.
 */
@Stateless
public class AjusteDepositoEstornoFacade extends SuperFacadeContabil<AjusteDepositoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AjusteDepositoFacade ajusteDepositoFacade;
    @EJB
    private ConfigAjusteDepositoFacade configAjusteDepositoFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private ReceitaExtraEstornoFacade receitaExtraEstornoFacade;
    @EJB
    private PagamentoExtraEstornoFacade pagamentoExtraEstornoFacade;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton singletonReprocessamento;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public AjusteDepositoEstornoFacade() {
        super(AjusteDepositoEstorno.class);
    }

    @Override
    public AjusteDepositoEstorno recuperar(Object id) {
        AjusteDepositoEstorno ajuste = super.recuperar(id);
        ajuste.getEstornosReceitasExtra().size();
        return ajuste;
    }

    public EventoContabil recuperarEventoContabil(AjusteDepositoEstorno entity) {
        try {
            EventoContabil eventoContabil = null;
            ConfigAjusteDeposito configAjusteDeposito = configAjusteDepositoFacade.buscarConfiguracaoCDE(entity.getAjusteDeposito(), TipoLancamento.ESTORNO);
            if (configAjusteDeposito != null && configAjusteDeposito.getEventoContabil() != null) {
                eventoContabil = configAjusteDeposito.getEventoContabil();
            }
            return eventoContabil;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarEstornoAjuste((AjusteDepositoEstorno) entidadeContabil);
    }

    private void contabilizarEstornoAjuste(AjusteDepositoEstorno entity) throws ExcecaoNegocioGenerica {

        EventoContabil eventoContabil = recuperarEventoContabil(entity);
        Preconditions.checkNotNull(eventoContabil, "Evento contábil não encontrado.");
        entity.setEventoContabil(eventoContabil);
        entity.gerarHistoricos();
        ParametroEvento parametroEvento = criarParametroEvento(entity);
        parametroEvento.setTipoBalancete(entity.getEventoContabil().getTipoBalancete());
        ItemParametroEvento item = criarItemParametroEvento(entity, parametroEvento);

        item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getAjusteDeposito().getPessoa(), entity.getAjusteDeposito().getClasseCredor(), entity.getDataEstorno()));
        item.setObjetoParametros(criarObjetosParametros(entity, item));
        parametroEvento.getItensParametrosEvento().add(item);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil) {
        try {
            AjusteDepositoEstorno entity = (AjusteDepositoEstorno) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(entity);
            ItemParametroEvento item = criarItemParametroEvento(entity, parametroEvento);
            List<ObjetoParametro> objetos = criarObjetosParametros(entity, item);
            item.setObjetoParametros(objetos);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private ParametroEvento criarParametroEvento(AjusteDepositoEstorno entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataEstorno());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
        parametroEvento.setExercicio(entity.getAjusteDeposito().getFonteDeRecurso().getExercicio());
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(AjusteDepositoEstorno entity, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private List<ObjetoParametro> criarObjetosParametros(AjusteDepositoEstorno entity, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity, item));
        objetos.add(new ObjetoParametro(entity.getAjusteDeposito().getContaExtraorcamentaria(), item));
        objetos.add(new ObjetoParametro(entity.getAjusteDeposito().getClasseCredor(), item));
        objetos.add(new ObjetoParametro(entity.getAjusteDeposito().getFonteDeRecurso(), item));
        return objetos;
    }

    private void gerarSaldoContaExtra(AjusteDepositoEstorno selecionado) {

        if (selecionado.getAjusteDeposito().isAjusteDiminutivo()) {
            gerarSaldoExtraPorTipoOperacao(selecionado, TipoOperacao.DEBITO);
        } else {
            if (validarSaldoContaExtra(selecionado)) {
                gerarSaldoExtraPorTipoOperacao(selecionado, TipoOperacao.CREDITO);
            }
        }
    }

    private void gerarSaldoExtraPorTipoOperacao(AjusteDepositoEstorno selecionado, TipoOperacao tipoOperacao) {
        if (selecionado.getAjusteDeposito() != null) {
            saldoExtraorcamentarioFacade.gerarSaldoExtraorcamentario(
                selecionado.getDataEstorno(),
                tipoOperacao,
                selecionado.getValor(),
                selecionado.getAjusteDeposito().getContaExtraorcamentaria(),
                selecionado.getAjusteDeposito().getContaDeDestinacao(),
                selecionado.getUnidadeOrganizacional(),
                selecionado.getId().toString(), selecionado.getClass().getSimpleName());
        }
    }

    private Boolean validarSaldoContaExtra(AjusteDepositoEstorno selecionado) {
        SaldoExtraorcamentario saldoExtraorcamentario = saldoExtraorcamentarioFacade.recuperaUltimoSaldoPorData(
            selecionado.getDataEstorno(),
            selecionado.getAjusteDeposito().getContaExtraorcamentaria(),
            selecionado.getAjusteDeposito().getContaDeDestinacao(),
            selecionado.getUnidadeOrganizacional());
        BigDecimal valor = saldoExtraorcamentario.getValor().subtract(selecionado.getValor());
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica(" O Saldo da Conta Extraorçamentária: " + selecionado.getAjusteDeposito().getContaExtraorcamentaria()
                + ", ficará negativo em " + "<b>" + Util.formataValor(valor)
                + "</b>" + " na data " + DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()) + ".");
        }
        return true;
    }

    public AjusteDepositoEstorno salvarNovo(AjusteDepositoEstorno entity, List<ReceitaExtra> receitaExtras) throws ExcecaoNegocioGenerica {
        try {
            if (getSingletonReprocessamento().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getSingletonReprocessamento().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
                entity = salvarGerandoHistorico(entity);
                movimentarReceitaExtraAndAjusteDeposito(entity, receitaExtras);
                gerarSaldoContaExtra(entity);
                contabilizarEstornoAjuste(entity);
            }
            return entity;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private AjusteDepositoEstorno salvarGerandoHistorico(AjusteDepositoEstorno entity) {
        if (entity.getId() == null) {
            entity.setNumero(singletonGeradorCodigoContabil.getNumeroAjusteDeposito(entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
            entity.gerarHistoricos();
            em.persist(entity);
        } else {
            entity.gerarHistoricos();
            entity = em.merge(entity);
        }
        return entity;
    }

    private void movimentarReceitaExtraAndAjusteDeposito(AjusteDepositoEstorno entity, List<ReceitaExtra> receitasExtras) {
        if (entity.getAjusteDeposito() != null) {
            AjusteDeposito ajusteDeposito = ajusteDepositoFacade.recuperar(entity.getAjusteDeposito().getId());
            for (ReceitaExtra receitaExtra : receitasExtras) {
                ReceitaExtraEstorno receitaExtraEstorno = criarObjetoReceitaExtraEstorno(entity, receitaExtra);
                adicionarEstornoReceitaNoEstornoAjuste(entity, receitaExtraEstorno);
                movimentarReceitaExtra(receitaExtra, receitaExtraEstorno, ajusteDeposito);
                em.merge(receitaExtra);
            }
            movimentarAjusteDeposito(entity, ajusteDeposito);
        }
    }

    private void movimentarReceitaExtra(ReceitaExtra receitaExtra, ReceitaExtraEstorno receitaExtraEstorno, AjusteDeposito ajusteDeposito) {
        if (ajusteDeposito.isAjusteDiminutivo()) {
            receitaExtraEstornoFacade.estornarReceitaExtra(receitaExtraEstorno, receitaExtra);
        } else {
            pagamentoExtraEstornoFacade.estornarReceitaExtra(receitaExtra);
        }
    }

    private void adicionarEstornoReceitaNoEstornoAjuste(AjusteDepositoEstorno entity, ReceitaExtraEstorno receitaExtraEstorno) {
        if (receitaExtraEstorno != null) {
            AjusteDepositoReceitaExtraEstorno ajusteReceitaEstorno = new AjusteDepositoReceitaExtraEstorno();
            ajusteReceitaEstorno.setReceitaExtraEstorno(receitaExtraEstorno);
            ajusteReceitaEstorno.setAjusteDepositoEstorno(entity);
            entity.getEstornosReceitasExtra().add(ajusteReceitaEstorno);
        }
    }

    private ReceitaExtraEstorno criarObjetoReceitaExtraEstorno(AjusteDepositoEstorno entity, ReceitaExtra receitaExtra) {
        ReceitaExtraEstorno novoEstorno = new ReceitaExtraEstorno();
        novoEstorno.setExercicio(receitaExtra.getExercicio());
        novoEstorno.setEventoContabil(null);
        novoEstorno.setReceitaExtra(receitaExtra);
        novoEstorno.setUsuarioSistema(receitaExtra.getUsuarioSistema());
        novoEstorno.setDataEstorno(entity.getDataEstorno());
        novoEstorno.setValor(receitaExtra.getValor());
        novoEstorno.setComplementoHistorico(getHistorico(entity));
        novoEstorno.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        novoEstorno.setUnidadeOrganizacionalAdm(entity.getUnidadeOrganizacionalAdm());
        novoEstorno.setNumero(getNumeroReceitaExtra(novoEstorno));
        novoEstorno.setStatusPagamento(StatusPagamento.EFETUADO);
        novoEstorno = receitaExtraEstornoFacade.salvarRetornando(novoEstorno);
        return novoEstorno;
    }

    private String getNumeroReceitaExtra(ReceitaExtraEstorno receitaExtraEstorno) {
        return receitaExtraFacade.getSingletonGeradorCodigoContabil().getNumeroReceitaExtra(sistemaFacade.getExercicioCorrente(), receitaExtraEstorno.getUnidadeOrganizacional(), receitaExtraEstorno.getDataEstorno());
    }

    private String getHistorico(AjusteDepositoEstorno entity) {
        return "Ajuste " + entity.getAjusteDeposito().getTipoAjuste().getDescricao() + ", N° " + entity.getNumero() + "/" + DataUtil.getDataFormatada(entity.getDataEstorno()) + ", " + entity.getHistorico();
    }

    private void movimentarAjusteDeposito(AjusteDepositoEstorno entity, AjusteDeposito ajusteDeposito) {
        ajusteDeposito.setSaldo(ajusteDeposito.getSaldo().subtract(entity.getValor()));
        if (ajusteDeposito.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            ajusteDeposito.setSituacao(SituacaoAjusteDeposito.ESTORNADA);
        }
        em.merge(ajusteDeposito);
    }

    @Override
    public void salvar(AjusteDepositoEstorno entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public AjusteDepositoFacade getAjusteDepositoFacade() {
        return ajusteDepositoFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getSingletonReprocessamento() {
        return singletonReprocessamento;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(AjusteDepositoEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join AjusteDeposito ajuste on obj.ajusteDeposito_id = ajuste.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "obj.dataEstorno");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "obj.dataEstorno");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "ajuste.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "ajuste.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "ajuste.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "ajuste.contaFinanceira_id");
        return Arrays.asList(consulta);
    }
}
