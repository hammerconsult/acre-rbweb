package br.com.webpublico.negocios.contabil.reprocessamento;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.ReprocessamentoContabil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 24/10/14
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ContabilizadorFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BensEstoqueFacade bensEstoqueFacade;
    @EJB
    private BensMoveisFacade bensMoveisFacade;
    @EJB
    private BensImoveisFacade bensImoveisFacade;
    @EJB
    private BensIntangiveisFacade bensIntangiveisFacade;
    @EJB
    private TransferenciaBensEstoqueFacade transferenciaBensEstoqueFacade;
    @EJB
    private TransfBensImoveisFacade transfBensImoveisFacade;
    @EJB
    private TransfBensMoveisFacade transfBensMoveisFacade;
    @EJB
    private TransfBensIntangiveisFacade transfBensIntangiveisFacade;
    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @EJB
    private EstornoLibCotaFinanceiraFacade estornoLibCotaFinanceiraFacade;
    @EJB
    private MovimentoDividaPublicaFacade movimentoDividaPublicaFacade;
    @EJB
    private ResponsabilidadeVTBFacade responsabilidadeVTBFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private AjusteAtivoDisponivelFacade ajusteAtivoDisponivelFacade;
    @EJB
    private AjusteDepositoFacade ajusteDepositoFacade;
    @EJB
    private TransferenciaContaFinanceiraFacade transferenciaContaFinanceiraFacade;
    @EJB
    private TransferenciaMesmaUnidadeFacade transferenciaMesmaUnidadeFacade;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ProvAtuarialMatematicaFacade provAtuarialMatematicaFacade;
    @EJB
    private DiariaContabilizacaoFacade diariaContabilizacaoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private EstornoTransferenciaFacade estornoTransferenciaFacade;
    @EJB
    private EstornoTransfMesmaUnidadeFacade estornoTransfMesmaUnidadeFacade;
    @EJB
    private PagamentoExtraEstornoFacade pagamentoExtraEstornoFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private LiquidacaoEstornoFacade liquidacaoEstornoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private ReceitaExtraEstornoFacade receitaExtraEstornoFacade;
    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private EstornoAlteracaoOrcFacade estornoAlteracaoOrcFacade;
    @EJB
    private CreditoReceberFacade creditoReceberFacade;
    @EJB
    private DividaAtivaContabilFacade dividaAtivaContabilFacade;
    @EJB
    private AberturaFechamentoExercicioFacade aberturaFechamentoExercicioFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;
    @EJB
    private ObrigacaoAPagarEstornoFacade obrigacaoAPagarEstornoFacade;
    @EJB
    private InvestimentoFacade investimentoFacade;
    @EJB
    private PatrimonioLiquidoFacade patrimonioLiquidoFacade;
    @EJB
    private AtoPotencialFacade atoPotencialFacade;
    @EJB
    private TransferenciaSaldoContaAuxiliarFacade transferenciaSaldoContaAuxiliarFacade;
    @EJB
    private LancamentoContabilManualFacade lancamentoContabilManualFacade;

    public void limparEM() {
        em.clear();
        reprocessamentoLancamentoContabilSingleton.getMapa().clear();
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void contabilizar(EventosReprocessar eventosReprocessar, EntidadeContabil entidadeContabil) {
        getFacade(eventosReprocessar).contabilizarReprocessamento(entidadeContabil);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void contabilizarTransferenciaSaldoContaAuxiliar(TransferenciaSaldoContaAuxiliar transferenciaSaldoContaAuxiliar) {
        transferenciaSaldoContaAuxiliarFacade.gerarSaldoContabil(transferenciaSaldoContaAuxiliar);
    }

    public SuperFacadeContabil getFacade(EventosReprocessar eventosReprocessar) {
        String key = eventosReprocessar.getTipoEventoContabil().name() + eventosReprocessar.getEventoContabil().getCodigo();
        if (reprocessamentoLancamentoContabilSingleton.getMapa().containsKey(key)) {
            return reprocessamentoLancamentoContabilSingleton.getMapa().get(key);
        }
        SuperFacadeContabil superFacadeContabil = retornarFacade(eventosReprocessar);
        reprocessamentoLancamentoContabilSingleton.getMapa().put(key, superFacadeContabil);
        return superFacadeContabil;
    }

    public List<EntidadeContabil> recuperarObjetosDoEvento(Exercicio exercicio, EventosReprocessar eventosReprocessar, Boolean isReprocessamentoInicial) {
        return recuperarObjetosPorClasse(exercicio, eventosReprocessar, isReprocessamentoInicial);
    }

    public String getNomeAtributo(Class classe, Class classeAtributo) {
        for (Field field : classe.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ReprocessamentoContabil.class)) {
                if (field.getType().equals(classeAtributo)) {
                    return field.getName();
                }
            }
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private List<EntidadeContabil> recuperarEstornoLiquidacaoReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, CategoriaOrcamentaria categoriaOrcamentaria) {
        String hql = "select d from LiquidacaoEstorno l "
            + " inner join l.desdobramentos d "
            + " where l.categoriaOrcamentaria = :categoria "
            + " and trunc(l.dataEstorno) between :di and :df  ";
        if (categoriaOrcamentaria.isResto()) {
            hql += "   and l.liquidacao.transportado = false";
        }
        if (er.hasUnidades()) {
            hql += " and l.unidadeOrganizacional.id in (:unidades) ";
        }
        if (!isReprocessamentoInicial) {
            hql += " and d.eventoContabil = :evento";
        }
        hql += " order by trunc(l.dataEstorno) asc";

        Query q = em.createQuery(hql, DesdobramentoLiquidacaoEstorno.class);
        try {
            q.setParameter("di", getDataCerta(er.getDataInicial()));
            q.setParameter("df", getDataCerta(er.getDataFinal()));
            q.setParameter("categoria", categoriaOrcamentaria);
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private List<Object> recuperarObjetosReceitaRealizadaEstorno(EventosReprocessar er, Boolean isReprocessamentoInicial) {
        String hql = "select fonte from ReceitaORCEstorno l "
            + " inner join l.receitaORCFonteEstorno fonte "
            + " where trunc(l.dataEstorno) between :di and :df  ";
        if (er.hasUnidades()) {
            hql = hql + " and l.unidadeOrganizacionalOrc.id in (:unidades) ";
        }
        if (!isReprocessamentoInicial) {
            hql += " and l.eventoContabil = :evento";
        }

        hql += " order by trunc(l.dataEstorno) asc";

        Query q = em.createQuery(hql);
        try {
            q.setParameter("di", getDataCerta(er.getDataInicial()));
            q.setParameter("df", getDataCerta(er.getDataFinal()));

            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private List<Object> recuperarObjetosReceitaRealizadaNormal(EventosReprocessar er, Boolean isReprocessamentoInicial) {
        String hql = "select fonte from LancamentoReceitaOrc l "
            + " inner join l.lancReceitaFonte fonte "
            + " where trunc(l.dataLancamento) between :di and :df  ";
        if (er.hasUnidades()) {
            hql = hql + " and l.unidadeOrganizacional.id in (:unidades) ";
        }
        if (!isReprocessamentoInicial) {
            hql += " and l.eventoContabil = :evento";
        }

        hql += " order by trunc(l.dataLancamento) asc";

        Query q = em.createQuery(hql);
        try {
            q.setParameter("di", getDataCerta(er.getDataInicial()));
            q.setParameter("df", getDataCerta(er.getDataFinal()));

            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private List<EntidadeContabil> recuperarLiquidacoesReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, CategoriaOrcamentaria categoriaOrcamentaria) {
        String hql = "select d from Liquidacao l "
            + " inner join l.desdobramentos d "
            + " where l.categoriaOrcamentaria = :categoria "
            + " and trunc(l.dataLiquidacao) between :di and :df  ";
        if (categoriaOrcamentaria.equals(CategoriaOrcamentaria.RESTO)) {
            hql += "   and l.transportado = false";
        }

        if (er.hasUnidades()) {
            hql = hql + " and l.unidadeOrganizacional.id in (:unidades) ";
        }
        if (!isReprocessamentoInicial) {
            hql += " and d.eventoContabil = :evento";
        }

        hql += " order by trunc(l.dataLiquidacao) asc";

        Query q = em.createQuery(hql, Desdobramento.class);
        try {
            q.setParameter("di", getDataCerta(er.getDataInicial()));
            q.setParameter("df", getDataCerta(er.getDataFinal()));
            q.setParameter("categoria", categoriaOrcamentaria);
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private List<EntidadeContabil> recuperarObrigacoesPagarReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial) {
        String hql = "select d from ObrigacaoAPagar l "
            + " inner join l.desdobramentos d "
            + " where trunc(l.dataLancamento) between :di and :df  ";
        if (er.hasUnidades()) {
            hql = hql + " and l.unidadeOrganizacional.id in (:unidades) ";
        }
        if (!isReprocessamentoInicial) {
            hql += " and d.eventoContabil = :evento";
        }

        hql += " order by trunc(l.dataLancamento) asc";

        Query q = em.createQuery(hql, DesdobramentoObrigacaoPagar.class);
        try {
            q.setParameter("di", getDataCerta(er.getDataInicial()));
            q.setParameter("df", getDataCerta(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private List<EntidadeContabil> recuperarEstornoObrigacoesPagarReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial) {
        String hql = "select d from ObrigacaoAPagarEstorno l "
            + " inner join l.desdobramentos d "
            + " where trunc(l.dataEstorno) between :di and :df  ";
        if (er.hasUnidades()) {
            hql = hql + " and l.unidadeOrganizacional.id in (:unidades) ";
        }
        if (!isReprocessamentoInicial) {
            hql += " and d.eventoContabil = :evento";
        }

        hql += " order by trunc(l.dataEstorno) asc";

        Query q = em.createQuery(hql, DesdobramentoObrigacaoAPagarEstorno.class);
        try {
            q.setParameter("di", getDataCerta(er.getDataInicial()));
            q.setParameter("df", getDataCerta(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List recuperarObjetosReceitaRealizada(EventosReprocessar er, Boolean isReprocessamentoInicial, Class classe) {
        if (classe.equals(LancReceitaFonte.class)) {
            return recuperarObjetosReceitaRealizadaNormal(er, isReprocessamentoInicial);
        } else if (classe.equals(ReceitaORCFonteEstorno.class)) {
            return recuperarObjetosReceitaRealizadaEstorno(er, isReprocessamentoInicial);
        } else {
            return new ArrayList<>();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private List<EntidadeContabil> recuperarObjetos(Exercicio exercicio, EventosReprocessar eventosReprocessar, Boolean
        isReprocessamentoInicial, Class classe, CategoriaOrcamentaria categoriaOrcamentaria) {
        return recuperarObjetos(eventosReprocessar, isReprocessamentoInicial, classe, categoriaOrcamentaria);
    }

    private List<EntidadeContabil> recuperarObjetos(EventosReprocessar eventosReprocessar, Boolean isReprocessamentoInicial, Class classe, CategoriaOrcamentaria categoriaOrcamentaria) {
        List<EntidadeContabil> objetos = Lists.newArrayList();
        String nomeAtributoDaUnidadeOrganizacional = getNomeAtributo(classe, UnidadeOrganizacional.class);
        String nomeAtributoEvento = getNomeAtributo(classe, EventoContabil.class);
        String nomeAtributoData = getNomeAtributo(classe, Date.class);

        String nomeAtributoCategoriaOrcamentaria = "";
        if (categoriaOrcamentaria != null) {
            nomeAtributoCategoriaOrcamentaria = getNomeAtributo(classe, CategoriaOrcamentaria.class);
        }

        String hql = montarHQL(isReprocessamentoInicial, classe, categoriaOrcamentaria, nomeAtributoDaUnidadeOrganizacional, nomeAtributoEvento, nomeAtributoData, nomeAtributoCategoriaOrcamentaria, eventosReprocessar.getUnidades());
        hql = montarParametrosHQL(eventosReprocessar, hql);
        hql += " order by obj." + nomeAtributoData + " asc";
        Query q = em.createQuery(hql);

        q.setParameter("di", getDataCerta(eventosReprocessar.getDataInicial()));
        q.setParameter("df", getDataCerta(eventosReprocessar.getDataFinal()));
        if (!eventosReprocessar.getUnidades().isEmpty()) {
            q.setParameter("unidades", eventosReprocessar.getIdUnidades());
        }
        if (!isReprocessamentoInicial) {
            q.setParameter("evento", eventosReprocessar.getEventoContabil());
        }
        if (categoriaOrcamentaria != null) {
            q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria);
        }
        objetos.addAll(q.getResultList());
        return objetos;
    }

    private String montarHQL(Boolean isReprocessamentoInicial, Class classe, CategoriaOrcamentaria categoriaOrcamentaria, String nomeAtributoDaUnidadeOrganizacional, String nomeAtributoEvento, String nomeAtributoData, String nomeAtributoCategoriaOrcamentaria, List<UnidadeOrganizacional> listaUO) {
        String hql = "select obj from " + classe.getSimpleName() + " obj where trunc(obj." + nomeAtributoData + ") between trunc(:di) and trunc(:df)  ";
        if (!listaUO.isEmpty()) {
            hql = hql + "and obj." + nomeAtributoDaUnidadeOrganizacional + ".id in (:unidades) ";
        }
        if (!isReprocessamentoInicial) {
            hql += " and obj." + nomeAtributoEvento + " = :evento";
        }
        if (categoriaOrcamentaria != null) {
            hql += " and obj." + nomeAtributoCategoriaOrcamentaria + " = :categoriaOrcamentaria";
        }
        return hql;
    }

    private String montarParametrosHQL(EventosReprocessar eventosReprocessar, String hql) {
        if (TipoEventoContabil.DIARIAS_CIVIL.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())) {
            hql += " and obj.propostaConcessaoDiaria.tipoProposta = 'CONCESSAO_DIARIA'";
        } else if (TipoEventoContabil.DIARIA_CAMPO.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())) {
            hql += " and obj.propostaConcessaoDiaria.tipoProposta = 'CONCESSAO_DIARIACAMPO'";
        } else if (TipoEventoContabil.SUPRIMENTO_FUNDO.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())) {
            hql += " and obj.propostaConcessaoDiaria.tipoProposta = 'SUPRIMENTO_FUNDO'";
        }


        if (TipoEventoContabil.RESTO_PAGAR.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())
            && !TipoBalancete.APURACAO.equals(eventosReprocessar.getEventoContabil().getTipoBalancete())) {
            if (TipoLancamento.NORMAL.equals(eventosReprocessar.getEventoContabil().getTipoLancamento())) {
                hql += " and obj.transportado = false";
            }
        }


        if (TipoEventoContabil.EMPENHO_DESPESA.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())) {
            if (TipoLancamento.ESTORNO.equals(eventosReprocessar.getEventoContabil().getTipoLancamento())) {
                hql += " and obj.tipoEmpenhoEstorno is null";
            }
        }
        if (TipoEventoContabil.PAGAMENTO_DESPESA.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())) {
            if (TipoLancamento.NORMAL.equals(eventosReprocessar.getEventoContabil().getTipoLancamento())) {
                hql += " and obj.dataPagamento is not null";
            }
        }

        if (TipoEventoContabil.CANCELAMENTO_RESTO_PAGAR.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())
            && !TipoBalancete.APURACAO.equals(eventosReprocessar.getEventoContabil().getTipoBalancete())) {
            hql += " and obj.tipoEmpenhoEstorno = 'CANCELAMENTO'";
        }

        if (TipoEventoContabil.APURACAO.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())) {
            hql += " and obj.tipoEmpenhoEstorno = 'PRESCRICAO'";
        }

        if (TipoEventoContabil.RECEITA_EXTRA_ORCAMENTARIA.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil())) {
            if (TipoLancamento.NORMAL.equals(eventosReprocessar.getEventoContabil().getTipoLancamento())) {
                hql += " and (obj.transportado is null or obj.transportado = 0)";
            }
        }
        if (TipoEventoContabil.OBRIGACAO_APAGAR.equals(eventosReprocessar.getEventoContabil().getTipoEventoContabil()) && TipoLancamento.NORMAL.equals(eventosReprocessar.getEventoContabil().getTipoLancamento())) {
            hql += " and (obj.transportado is null or obj.transportado = 0)";
        }
        return hql;
    }

    private List<EntidadeContabil> recuperarObjetosAberturaExercicio(Class classe, EventosReprocessar eventosReprocessar, Exercicio exercicio) {
        List<EntidadeContabil> objetos = Lists.newArrayList();
        if (classe.equals(InscricaoEmpenho.class)) {
            objetos.addAll(aberturaFechamentoExercicioFacade.recuperarMovimentosApuracaoReprocessamento(exercicio, eventosReprocessar));
        }
        if (classe.equals(EmpenhoEstorno.class) && eventosReprocessar.getEventoContabil().isNormal()) {
            objetos.addAll(aberturaFechamentoExercicioFacade.recuperarMovimentosApuracaoPrescricaoReprocessamento(exercicio, eventosReprocessar));
        }
        if (classe.equals(TransporteDeSaldoAbertura.class)) {
            objetos.addAll(aberturaFechamentoExercicioFacade.recuperarMovimentosTransporteDeSaldoAbertura(exercicio, eventosReprocessar.getDataInicial(), eventosReprocessar.getDataFinal()));
        }

        return objetos;
    }


    private Date getDataCerta(Date data) {
        try {
            String string = new SimpleDateFormat("dd/MM/yyyy").format(data);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dataInicial = dateFormat.parse(string);
            return dataInicial;
        } catch (Exception e) {
            return null;
        }

    }

    private SuperFacadeContabil retornarFacade(EventosReprocessar er) {
        EventoContabil eventoContabil = er.getEventoContabil();
        TipoEventoContabil tipoEventoContabil = eventoContabil.getTipoEventoContabil();
        switch (tipoEventoContabil) {
            case ATO_POTENCIAL:
                return atoPotencialFacade;
            case CREDITO_RECEBER:
                return creditoReceberFacade;
            case DIVIDA_ATIVA:
                return dividaAtivaContabilFacade;
            case AJUSTE_ATIVO_DISPONIVEL:
                return ajusteAtivoDisponivelFacade;
            case AJUSTE_DEPOSITO:
                return ajusteDepositoFacade;
            case TRANSFERENCIA_FINANCEIRA:
                if (eventoContabil.isNormal()) {
                    return transferenciaContaFinanceiraFacade;
                } else {
                    return estornoTransferenciaFacade;
                }
            case TRANSFERENCIA_MESMA_UNIDADE:
                if (eventoContabil.isNormal()) {
                    return transferenciaMesmaUnidadeFacade;
                } else {
                    return estornoTransfMesmaUnidadeFacade;
                }
            case DESPESA_EXTRA_ORCAMENTARIA:
                if (eventoContabil.isNormal()) {
                    return pagamentoExtraFacade;
                } else {
                    return pagamentoExtraEstornoFacade;
                }
            case RECEITA_REALIZADA:
                if (eventoContabil.isNormal()) {
                    return lancamentoReceitaOrcFacade;
                } else {
                    return receitaORCEstornoFacade;
                }
            case PROVISAO_MATEMATICA_PREVIDENCIARIA:
                return provAtuarialMatematicaFacade;
            case DIARIA_CAMPO:
                return diariaContabilizacaoFacade;
            case DIARIAS_CIVIL:
                return diariaContabilizacaoFacade;
            case SUPRIMENTO_FUNDO:
                return diariaContabilizacaoFacade;
            case OBRIGACAO_APAGAR:
                if (eventoContabil.isNormal()) {
                    return obrigacaoAPagarFacade;
                } else {
                    return obrigacaoAPagarEstornoFacade;
                }
            case EMPENHO_DESPESA:
                if (eventoContabil.isNormal()) {
                    return empenhoFacade;
                } else {
                    return empenhoEstornoFacade;
                }
            case RESTO_PAGAR:
                if (!TipoBalancete.APURACAO.equals(eventoContabil.getTipoBalancete()) && eventoContabil.isNormal()) {
                    return empenhoFacade;
                } else {
                    return aberturaFechamentoExercicioFacade;
                }
            case CANCELAMENTO_RESTO_PAGAR:
                if (!TipoBalancete.APURACAO.equals(eventoContabil.getTipoBalancete()) && eventoContabil.isNormal()) {
                    return empenhoEstornoFacade;
                } else {
                    return aberturaFechamentoExercicioFacade;
                }
            case LIQUIDACAO_DESPESA:
                if (eventoContabil.isNormal()) {
                    return liquidacaoFacade;
                } else {
                    return liquidacaoEstornoFacade;
                }
            case LIQUIDACAO_RESTO_PAGAR:
                if (eventoContabil.isNormal()) {
                    return liquidacaoFacade;
                } else {
                    return liquidacaoEstornoFacade;
                }
            case PAGAMENTO_DESPESA:
                if (eventoContabil.isNormal()) {
                    return pagamentoFacade;
                } else {
                    return pagamentoEstornoFacade;
                }
            case PAGAMENTO_RESTO_PAGAR:
                if (eventoContabil.isNormal()) {
                    return pagamentoFacade;
                } else {
                    return pagamentoEstornoFacade;
                }
            case CREDITO_ADICIONAL:
                if (eventoContabil.isNormal()) {
                    return alteracaoORCFacade;
                } else {
                    return estornoAlteracaoOrcFacade;
                }
            case PREVISAO_ADICIONAL_RECEITA:
                if (eventoContabil.isNormal()) {
                    return alteracaoORCFacade;
                } else {
                    return estornoAlteracaoOrcFacade;
                }
            case PREVISAO_INICIAL_RECEITA:
                return loaFacade;
            case CREDITO_INICIAL:
                return loaFacade;
            case RECEITA_EXTRA_ORCAMENTARIA:
                if (eventoContabil.isNormal()) {
                    return receitaExtraFacade;
                } else {
                    return receitaExtraEstornoFacade;
                }
            case BENS_ESTOQUE:
                return bensEstoqueFacade;
            case BENS_IMOVEIS:
                return bensImoveisFacade;
            case BENS_MOVEIS:
                return bensMoveisFacade;
            case BENS_INTANGIVEIS:
                return bensIntangiveisFacade;
            case TRANSFERENCIA_BENS_ESTOQUE:
                return transferenciaBensEstoqueFacade;
            case TRANSFERENCIA_BENS_MOVEIS:
                return transfBensMoveisFacade;
            case TRANSFERENCIA_BENS_IMOVEIS:
                return transfBensImoveisFacade;
            case TRANSFERENCIA_BENS_INTANGIVEIS:
                return transfBensIntangiveisFacade;
            case LIBERACAO_FINANCEIRA:
                if (eventoContabil.isNormal()) {
                    return liberacaoCotaFinanceiraFacade;
                } else {
                    return estornoLibCotaFinanceiraFacade;
                }
            case DIVIDA_PUBLICA:
                return movimentoDividaPublicaFacade;
            case RESPONSABILIDADE_VTB:
                return responsabilidadeVTBFacade;
            case APURACAO:
                return aberturaFechamentoExercicioFacade;
            case TRANSFERENCIA_RESULTADO:
                return aberturaFechamentoExercicioFacade;
            case ABERTURA:
                return aberturaFechamentoExercicioFacade;
            case INVESTIMENTO:
                return investimentoFacade;
            case PATRIMONIO_LIQUIDO:
                return patrimonioLiquidoFacade;
            case AJUSTE_CONTABIL_MANUAL:
                return lancamentoContabilManualFacade;
        }
        return null;
    }

    private List<EntidadeContabil> recuperarObjetosPorClasse(Exercicio exercicio, EventosReprocessar er, Boolean isReprocessamentoInicial) {
        TipoEventoContabil tipoEventoContabil = er.getEventoContabil().getTipoEventoContabil();
        switch (tipoEventoContabil) {
            case ATO_POTENCIAL:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, AtoPotencial.class, null);
            case CREDITO_RECEBER:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, CreditoReceber.class, null);
            case DIVIDA_ATIVA:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, DividaAtivaContabil.class, null);
            case AJUSTE_ATIVO_DISPONIVEL:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, AjusteAtivoDisponivel.class, null);
            case AJUSTE_DEPOSITO:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, AjusteDeposito.class, null);
            case LIBERACAO_FINANCEIRA:
                if (er.getEventoContabil().isNormal()) {
                    List<EntidadeContabil> objetos = Lists.newArrayList();
                    objetos.addAll(liberacaoCotaFinanceiraFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                    objetos.addAll(liberacaoCotaFinanceiraFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                    return objetos;
                } else {
                    List<EntidadeContabil> objetos = Lists.newArrayList();
                    objetos.addAll(estornoLibCotaFinanceiraFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                    objetos.addAll(estornoLibCotaFinanceiraFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                    return objetos;
                }
            case TRANSFERENCIA_FINANCEIRA:
                if (er.getEventoContabil().isNormal()) {
                    List<EntidadeContabil> retorno = Lists.newArrayList();
                    retorno.addAll(transferenciaContaFinanceiraFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                    retorno.addAll(transferenciaContaFinanceiraFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                    return retorno;
                } else {
                    List<EntidadeContabil> retorno = Lists.newArrayList();
                    retorno.addAll(estornoTransferenciaFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                    retorno.addAll(estornoTransferenciaFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                    return retorno;
                }
            case TRANSFERENCIA_MESMA_UNIDADE:
                if (er.getEventoContabil().getTipoLancamento().equals(TipoLancamento.NORMAL)) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, TransferenciaMesmaUnidade.class, null);
                } else {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, EstornoTransfMesmaUnidade.class, null);
                }
            case DESPESA_EXTRA_ORCAMENTARIA:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, PagamentoExtra.class, null);
                } else {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, PagamentoExtraEstorno.class, null);
                }
            case RECEITA_REALIZADA:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarObjetosReceitaRealizada(er, isReprocessamentoInicial, LancReceitaFonte.class);
                } else {
                    return recuperarObjetosReceitaRealizada(er, isReprocessamentoInicial, ReceitaORCFonteEstorno.class);
                }
            case PROVISAO_MATEMATICA_PREVIDENCIARIA:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, ProvAtuarialMatematica.class, null);
            case DIARIA_CAMPO:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, DiariaContabilizacao.class, null);
            case DIARIAS_CIVIL:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, DiariaContabilizacao.class, null);
            case SUPRIMENTO_FUNDO:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, DiariaContabilizacao.class, null);
            case EMPENHO_DESPESA:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, Empenho.class, CategoriaOrcamentaria.NORMAL);
                } else {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, EmpenhoEstorno.class, CategoriaOrcamentaria.NORMAL);
                }
            case OBRIGACAO_APAGAR:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarObrigacoesPagarReprocessamento(er, isReprocessamentoInicial);
                } else {
                    return recuperarEstornoObrigacoesPagarReprocessamento(er, isReprocessamentoInicial);
                }
            case RESTO_PAGAR:
                if (!TipoBalancete.APURACAO.equals(er.getEventoContabil().getTipoBalancete()) && er.getEventoContabil().isNormal()) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, Empenho.class, CategoriaOrcamentaria.RESTO);
                } else {
                    if (er.getEventoContabil().isNormal()) {
                        return recuperarObjetosAberturaExercicio(InscricaoEmpenho.class, er, exercicio);
                    }
                }
            case CANCELAMENTO_RESTO_PAGAR:
                if (!TipoBalancete.APURACAO.equals(er.getEventoContabil().getTipoBalancete()) && er.getEventoContabil().isNormal()) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, EmpenhoEstorno.class, CategoriaOrcamentaria.RESTO);
                } else {
                    return recuperarObjetosAberturaExercicio(EmpenhoEstorno.class, er, exercicio);
                }
            case LIQUIDACAO_DESPESA:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarLiquidacoesReprocessamento(er, isReprocessamentoInicial, CategoriaOrcamentaria.NORMAL);
                } else {
                    return recuperarEstornoLiquidacaoReprocessamento(er, isReprocessamentoInicial, CategoriaOrcamentaria.NORMAL);
                }
            case LIQUIDACAO_RESTO_PAGAR:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarLiquidacoesReprocessamento(er, isReprocessamentoInicial, CategoriaOrcamentaria.RESTO);
                } else {
                    return recuperarEstornoLiquidacaoReprocessamento(er, isReprocessamentoInicial, CategoriaOrcamentaria.RESTO);
                }
            case PAGAMENTO_DESPESA:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, Pagamento.class, CategoriaOrcamentaria.NORMAL);
                } else {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, PagamentoEstorno.class, CategoriaOrcamentaria.NORMAL);
                }
            case PAGAMENTO_RESTO_PAGAR:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, Pagamento.class, CategoriaOrcamentaria.RESTO);
                } else {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, PagamentoEstorno.class, CategoriaOrcamentaria.RESTO);
                }
            case CREDITO_INICIAL:
                try {
                    return loaFacade.recuperarLoaDespesa(exercicio, er);
                } catch (ExcecaoNegocioGenerica e) {
                    throw new ExcecaoNegocioGenerica(e.getMessage());
                }
            case CREDITO_ADICIONAL:
                if (er.getEventoContabil().isNormal()) {
                    return alteracaoORCFacade.recuperarAlteracaoORCDespesa(exercicio, er);
                } else {
                    return estornoAlteracaoOrcFacade.buscarEstornoAlteracaoORCDespesa(exercicio, er);
                }
            case PREVISAO_ADICIONAL_RECEITA:
                if (er.getEventoContabil().isNormal()) {
                    return alteracaoORCFacade.recuperarAlteracaoORCReceita(exercicio, er);
                } else {
                    return estornoAlteracaoOrcFacade.buscarAlteracaoORCReceita(exercicio, er);
                }
            case PREVISAO_INICIAL_RECEITA:
                return loaFacade.recuperarLoaReceita(exercicio, er.getDataInicial(), er.getDataFinal());
            case RECEITA_EXTRA_ORCAMENTARIA:
                if (er.getEventoContabil().isNormal()) {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, ReceitaExtra.class, null);
                } else {
                    return recuperarObjetos(exercicio, er, isReprocessamentoInicial, ReceitaExtraEstorno.class, null);
                }
            case BENS_ESTOQUE:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, BensEstoque.class, null);
            case BENS_IMOVEIS:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, BensImoveis.class, null);
            case BENS_MOVEIS:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, BensMoveis.class, null);
            case BENS_INTANGIVEIS:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, BensIntangiveis.class, null);
            case TRANSFERENCIA_BENS_ESTOQUE:
                List<EntidadeContabil> retornarTransfBensEstoque = Lists.newArrayList();
                retornarTransfBensEstoque.addAll(transferenciaBensEstoqueFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                retornarTransfBensEstoque.addAll(transferenciaBensEstoqueFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                return retornarTransfBensEstoque;
            case TRANSFERENCIA_BENS_IMOVEIS:
                List<EntidadeContabil> retornarTransfBensImoveis = Lists.newArrayList();
                retornarTransfBensImoveis.addAll(transfBensImoveisFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                retornarTransfBensImoveis.addAll(transfBensImoveisFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                return retornarTransfBensImoveis;
            case TRANSFERENCIA_BENS_MOVEIS:
                List<EntidadeContabil> retornarTransfBensMoveis = Lists.newArrayList();
                retornarTransfBensMoveis.addAll(transfBensMoveisFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                retornarTransfBensMoveis.addAll(transfBensMoveisFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                return retornarTransfBensMoveis;
            case TRANSFERENCIA_BENS_INTANGIVEIS:
                List<EntidadeContabil> retornarTransfBensIntangiveis = Lists.newArrayList();
                retornarTransfBensIntangiveis.addAll(transfBensIntangiveisFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.RECEBIDO));
                retornarTransfBensIntangiveis.addAll(transfBensIntangiveisFacade.recuperarObjetosReprocessamento(er, isReprocessamentoInicial, ParametroEvento.ComplementoId.CONCEDIDO));
                return retornarTransfBensIntangiveis;
            case DIVIDA_PUBLICA:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, MovimentoDividaPublica.class, null);
            case RESPONSABILIDADE_VTB:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, ResponsabilidadeVTB.class, null);
            case TRANSFERENCIA_RESULTADO:
                return recuperarObjetos(exercicio, er, isReprocessamentoInicial, TransporteDeSaldoAbertura.class, null);
            case INVESTIMENTO:
                return recuperarObjetos(er, isReprocessamentoInicial, Investimento.class, null);
            case PATRIMONIO_LIQUIDO:
                return recuperarObjetos(er, isReprocessamentoInicial, PatrimonioLiquido.class, null);
            case AJUSTE_CONTABIL_MANUAL:
                return recuperarObjetos(er, isReprocessamentoInicial, LancamentoContabilManual.class, null);
            case ABERTURA:
                List<EntidadeContabil> aberturas = Lists.newArrayList();
                aberturas.addAll(recuperarObjetos(er, isReprocessamentoInicial, AberturaInscricaoResto.class, null));
                return aberturas;
            default:
                break;
        }
        return Lists.newArrayList();
    }


    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }


    public List<EntidadeContabil> recuperarObjetos(List<ConsultaMovimentoContabil> consultas) {
        if (consultas == null) {
            return Lists.newArrayList();
        }
        List<EntidadeContabil> retorno = Lists.newArrayList();
        for (ConsultaMovimentoContabil consultaMovimentoContabil : consultas) {
            String sql = consultaMovimentoContabil.getSql();
            Query q = em.createNativeQuery(sql, consultaMovimentoContabil.getClasse());
            for (FiltroConsultaMovimentoContabil filtro : consultaMovimentoContabil.getParametroPorValor().keySet()) {
                for (String parametro : consultaMovimentoContabil.getParametroPorValor().get(filtro).keySet()) {
                    if (consultaMovimentoContabil.getParametroPorValor().get(filtro).get(parametro) != null) {
                        q.setParameter(parametro.replace(":", ""), consultaMovimentoContabil.getValor(consultaMovimentoContabil.getParametroPorValor().get(filtro).get(parametro), filtro));
                    }
                }
            }
            if (consultaMovimentoContabil.getMaximoRegistros() != null) {
                q.setMaxResults(consultaMovimentoContabil.getMaximoRegistros());
            }
            if (consultaMovimentoContabil.getImprimirSql()) {
                Util.imprimeSQL(sql, q);
            }
            retorno.addAll(q.getResultList());
        }
        return retorno;
    }
}
