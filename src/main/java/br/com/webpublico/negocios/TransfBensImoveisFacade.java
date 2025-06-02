package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 17/02/14
 * Time: 15:27
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class TransfBensImoveisFacade extends SuperFacadeContabil<TransfBensImoveis> {

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private ConfigTransfBensImoveisFacade configTransfBensImoveisFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SaldoGrupoBemImovelFacade saldoGrupoBemImovelFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransfBensImoveisFacade() {
        super(TransfBensImoveis.class);
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(TRANSF.NUMERO)) +1 AS ULTIMONUMERO FROM TRANSFBENSIMOVEIS TRANSF";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public void salvar(TransfBensImoveis entity) {
        entity.gerarHistoricos(hierarquiaOrganizacionalFacade);
        em.merge(entity);
    }

    @Override
    public void salvarNovo(TransfBensImoveis entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroTransferenciaBensImoveis(entity.getExercicio()));
                    entity.gerarHistoricos(hierarquiaOrganizacionalFacade);
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos(hierarquiaOrganizacionalFacade);
                    entity = em.merge(entity);
                }
                gerarSaldoTransferenciaOrigem(entity);
                gerarSaldoTransferenciaDestino(entity);

                contabilizarTransferenciaBemImoveisOrigem(entity);
                contabilizarTransferenciaBemImoveisDestino(entity);
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void buscarEventoDestino(TransfBensImoveis entity) {
        try {
            if (entity.getOperacaoDestino() != null
                && entity.getTipoLancamento() != null) {
                ConfigTransfBensImoveis configuracao = configTransfBensImoveisFacade.recuperarConfiguracaoEvento(
                    entity.getTipoLancamento(),
                    entity.getOperacaoDestino(),
                    entity.getDataTransferencia());
                entity.setEventoContabilDestino(configuracao.getEventoContabil());
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void buscarEventoOrigem(TransfBensImoveis entity) {
        try {
            if (entity.getOperacaoOrigem() != null
                && entity.getTipoLancamento() != null) {
                ConfigTransfBensImoveis configuracao = configTransfBensImoveisFacade.recuperarConfiguracaoEvento(
                    entity.getTipoLancamento(),
                    entity.getOperacaoOrigem(),
                    entity.getDataTransferencia());
                entity.setEventoContabilOrigem(configuracao.getEventoContabil());
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoTransferenciaOrigem(TransfBensImoveis entity) {

        saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
            entity.getUnidadeOrigem(),
            entity.getGrupoBemOrigem(),
            entity.getValor(),
            entity.getTipoGrupoOrigem(),
            entity.getDataTransferencia(),
            entity.getOperacaoOrigem(),
            entity.getTipoLancamento(),
            TipoOperacao.CREDITO,
            true);

        saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
            entity.getUnidadeOrigem(),
            entity.getGrupoBemOrigem(),
            entity.getValor(),
            entity.getTipoGrupoOrigem(),
            entity.getDataTransferencia(),
            entity.getOperacaoOrigem(),
            entity.getTipoLancamento(),
            TipoOperacao.DEBITO,
            true);
    }

    private void gerarSaldoTransferenciaDestino(TransfBensImoveis entity) {

        saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
            entity.getUnidadeDestino(),
            entity.getGrupoBemDestino(),
            entity.getValor(),
            entity.getTipoGrupoDestino(),
            entity.getDataTransferencia(),
            entity.getOperacaoDestino(),
            entity.getTipoLancamento(),
            TipoOperacao.CREDITO,
            true);

        saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
            entity.getUnidadeDestino(),
            entity.getGrupoBemDestino(),
            entity.getValor(),
            entity.getTipoGrupoDestino(),
            entity.getDataTransferencia(),
            entity.getOperacaoDestino(),
            entity.getTipoLancamento(),
            TipoOperacao.DEBITO,
            true);
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void contabilizarTransferenciaBemImoveisOrigem(TransfBensImoveis entity) {
        ConfigTransfBensImoveis configuracao = configTransfBensImoveisFacade.recuperarConfiguracaoEvento(entity.getTipoLancamento(), entity.getOperacaoOrigem(), entity.getDataTransferencia());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabilOrigem(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência de Bens Imóveis. ");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos(hierarquiaOrganizacionalFacade);

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrigem());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            Calendar instance = Calendar.getInstance();
            instance.setTime(entity.getDataTransferencia());
            parametroEvento.setExercicio(contabilizacaoFacade.getExercicioFacade().getExercicioPorAno(instance.get(Calendar.YEAR)));

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetosParametro = Lists.newArrayList();
            objetosParametro.add(new ObjetoParametro(entity, item));
            objetosParametro.add(new ObjetoParametro(entity.getGrupoBemOrigem(), item));
            objetosParametro.add(new ObjetoParametro(entity.getTipoGrupoOrigem(), item));

            item.setObjetoParametros(objetosParametro);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração para a Transferência de Bens Imóveis.");
        }
    }

    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void contabilizarTransferenciaBemImoveisDestino(TransfBensImoveis entity) {
        ConfigTransfBensImoveis configuracao = configTransfBensImoveisFacade.recuperarConfiguracaoEvento(entity.getTipoLancamento(), entity.getOperacaoDestino(), entity.getDataTransferencia());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabilDestino(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência de Bens Imóveis. ");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos(hierarquiaOrganizacionalFacade);

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeDestino());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            Calendar instance = Calendar.getInstance();
            instance.setTime(entity.getDataTransferencia());
            parametroEvento.setExercicio(contabilizacaoFacade.getExercicioFacade().getExercicioPorAno(instance.get(Calendar.YEAR)));

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetosParametro = Lists.newArrayList();
            objetosParametro.add(new ObjetoParametro(entity, item));
            objetosParametro.add(new ObjetoParametro(entity.getGrupoBemDestino(), item));
            objetosParametro.add(new ObjetoParametro(entity.getTipoGrupoDestino(), item));

            item.setObjetoParametros(objetosParametro);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração para a Transferência de Bens Imóveis.");
        }
    }


    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }


    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        ParametroEvento.ComplementoId complementoId = ((SuperEntidadeContabilFinanceira) entidadeContabil).getComplementoId();
        if (ParametroEvento.ComplementoId.NORMAL.equals(complementoId)
            || ParametroEvento.ComplementoId.CONCEDIDO.equals(complementoId)) {
            contabilizarTransferenciaBemImoveisOrigem((TransfBensImoveis) entidadeContabil);
        }
        if (ParametroEvento.ComplementoId.NORMAL.equals(complementoId)
            || ParametroEvento.ComplementoId.RECEBIDO.equals(complementoId)) {
            contabilizarTransferenciaBemImoveisDestino((TransfBensImoveis) entidadeContabil);
        }
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(TransfBensImoveis.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataTransferencia)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataTransferencia)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrigem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrigem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabilOrigem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_BENS_IMOVEIS, "obj.operacaoOrigem");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.GRUPO_PATRIMONIAL, "obj.grupoBemOrigem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicorazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<TransfBensImoveis> recuperarObjetosReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, ParametroEvento.ComplementoId complementoId) {

        String sql = "select trans.* from TRANSFBENSIMOVEIS trans" +
            " where trans.DATATRANSFERENCIA between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql = sql + " and trans.UNIDADEORIGEM_ID in (:unidades) ";
        }
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql = sql + " and trans.UNIDADEDESTINO_ID in (:unidades) ";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql += " and trans.eventoContabilOrigem_ID = :evento";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql += " and trans.eventoContabilDestino_ID = :evento";
        }

        sql += " order by trans.DATATRANSFERENCIA asc";

        Query q = em.createNativeQuery(sql, TransfBensImoveis.class);
        try {
            q.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            q.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            List<TransfBensImoveis> retorno = q.getResultList();
            for (TransfBensImoveis entidade : retorno) {
                entidade.setComplementoId(complementoId);
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }
}
