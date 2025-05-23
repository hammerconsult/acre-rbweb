package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoTransferencia;
import br.com.webpublico.entidadesauxiliares.VOTransferenciaEfetivadaBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 26/12/13
 * Time: 16:13
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class LoteEfetivacaoTransferenciaBemFacade extends AbstractFacade<LoteEfetivacaoTransferenciaBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LoteEfetivacaoLevantamentoBemFacade loteEfetivacaoLevantamentoBemFacade;
    @EJB
    private LoteTransferenciaFacade loteTransferenciaFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private AprovacaoTransferenciaBemFacade aprovacaoTransferenciaBemFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FaseFacade faseFacade;

    public LoteEfetivacaoTransferenciaBemFacade() {
        super(LoteEfetivacaoTransferenciaBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LoteEfetivacaoTransferenciaBem recuperar(Object id) {
        LoteEfetivacaoTransferenciaBem lote = super.recuperar(id);
        if (TipoBem.IMOVEIS.equals(lote.getTipoBem())) {
            Hibernate.initialize(lote.getEfetivacoesRecebidas());
        }
        if (lote.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(lote.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return lote;
    }

    public LoteEfetivacaoTransferenciaBem recuperarSimples(Object id) {
        return super.recuperar(id);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<VOItemSolicitacaoTransferencia> buscarBensEfetivadosPorSolicitacaoTransferencia(LoteTransferenciaBem solicitacao) {
        String sql = " select bem.identificacao                            as registro, " +
            "       bem.descricao                                          as descricao, " +
            "       estresultante.estadobem                                as estadobem, " +
            "       vworcorigem.codigo || ' - ' || vworcorigem.descricao   as unidadeorcorigem, " +
            "       vworcdestino.codigo || ' - ' || vworcdestino.descricao as unidadeorcdestino, " +
            "       coalesce(estResultante.valororiginal, 0)               as valorOriginal, " +
            "       coalesce(estInicial.valoracumuladodaamortizacao " +
            "                    + estInicial.valoracumuladodadepreciacao " +
            "                    + estInicial.valoracumuladodaexaustao " +
            "                    + estInicial.valoracumuladodeajuste, 0)   as valorAjuste," +
            "       bem.id as idBem " +
            "       from efetivacaotransferenciabem item " +
            "         inner join loteefettransfbem lote on lote.id = item.lote_id " +
            "         inner join transferenciabem itemsol on itemsol.id = item.transferenciabem_id " +
            "         inner join eventobem ev on ev.id = item.id " +
            "         inner join estadobem estinicial on estinicial.id = ev.estadoinicial_id " +
            "         inner join estadobem estresultante on estresultante.id = ev.estadoresultante_id " +
            "         inner join bem on bem.id = ev.bem_id " +
            "         inner join vwhierarquiaorcamentaria vworcorigem on vworcorigem.subordinada_id = estinicial.detentoraorcamentaria_id " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vworcorigem.iniciovigencia and coalesce(vworcorigem.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "         inner join vwhierarquiaorcamentaria vworcdestino on vworcdestino.subordinada_id = estresultante.detentoraorcamentaria_id " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vworcdestino.iniciovigencia and coalesce(vworcdestino.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where itemsol.lotetransferenciabem_id = :idSolicitacao " +
            "order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacao.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(solicitacao.getDataHoraCriacao()));
        return preencherArrayObjetoVOItemSolicitacaoTransferencia(q);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<VOItemSolicitacaoTransferencia> buscarBensNaoEfetivadosPorSolicitacaoTransferencia(LoteTransferenciaBem solicitacao) {
        String sql = " select bem.identificacao                            as registro, " +
            "       bem.descricao                                          as descricao, " +
            "       estresultante.estadobem                                as estadobem, " +
            "       vworcorigem.codigo || ' - ' || vworcorigem.descricao   as unidadeorcorigem, " +
            "       vworcdestino.codigo || ' - ' || vworcdestino.descricao as unidadeorcdestino, " +
            "       coalesce(estResultante.valororiginal, 0)               as valorOriginal, " +
            "       coalesce(estInicial.valoracumuladodaamortizacao " +
            "                    + estInicial.valoracumuladodadepreciacao " +
            "                    + estInicial.valoracumuladodaexaustao " +
            "                    + estInicial.valoracumuladodeajuste, 0)   as valorAjuste," +
            "       bem.id as idBem " +
            "       from lotetransferenciabem sol " +
            "         inner join transferenciabem item on item.LOTETRANSFERENCIABEM_ID = sol.id " +
            "         inner join eventobem ev on ev.id = item.id " +
            "         inner join estadobem estinicial on estinicial.id = ev.estadoinicial_id " +
            "         inner join estadobem estresultante on estresultante.id = ev.estadoresultante_id " +
            "         inner join bem on bem.id = ev.bem_id " +
            "         inner join vwhierarquiaorcamentaria vworcorigem on vworcorigem.subordinada_id = estinicial.detentoraorcamentaria_id " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vworcorigem.iniciovigencia and coalesce(vworcorigem.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "         inner join vwhierarquiaorcamentaria vworcdestino on vworcdestino.subordinada_id = estresultante.detentoraorcamentaria_id " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between vworcdestino.iniciovigencia and coalesce(vworcdestino.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where sol.id = :idSolicitacao " +
            "order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacao.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(solicitacao.getDataHoraCriacao()));
        return preencherArrayObjetoVOItemSolicitacaoTransferencia(q);
    }

    private List<VOItemSolicitacaoTransferencia> preencherArrayObjetoVOItemSolicitacaoTransferencia(Query q) {
        List<VOItemSolicitacaoTransferencia> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            VOItemSolicitacaoTransferencia item = new VOItemSolicitacaoTransferencia();
            item.setRegistroPatrimonial((String) obj[0]);
            item.setDescricao((String) obj[1]);
            item.setEstadoConservacaoBem(EstadoConservacaoBem.valueOf((String) obj[2]));
            item.setUnidadeOrcOrigem((String) obj[3]);
            item.setUnidadeOrcDestino((String) obj[4]);
            item.setValorOriginal((BigDecimal) obj[5]);
            item.setValorAjuste((BigDecimal) obj[6]);
            item.setEstadoBem(bemFacade.recuperarUltimoEstadoDoBem(((BigDecimal) obj[7]).longValue()));
            toReturn.add(item);
        }
        return toReturn;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<VOItemSolicitacaoTransferencia>> pesquisarBens(LoteTransferenciaBem solicitacao, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Recuperando bens da solicitação " + solicitacao);

        String sql = " select item.id                     as idItem, " +
            "       bem.id                                as idBem, " +
            "       bem.identificacao                     as registro, " +
            "       bem.descricao                         as descricao, " +
            "       estResultante.estadobem               as estadoBem, " +
            "       estResultante.situacaoconservacaobem  as situacaoConservacao, " +
            "       vworcOrigem.id                        as idUnidadeOrcOrigem, " +
            "       vworcDestino.id                       as idUnidadeOrcDestino, " +
            "       vwadmOrigem.id                        as idUnidadeAdmOrigem, " +
            "       vwadmDestino.id                       as idUnidadeAdmDestino, " +
            "       coalesce((select estOriginal.valororiginal " +
            "                 from estadobem estOriginal " +
            "                          inner join eventobem evOriginal on evOriginal.estadoresultante_id = estOriginal.id " +
            "                 where evOriginal.dataoperacao = " +
            "                       (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evOriginal.bem_id) " +
            "                   and evOriginal.bem_id = bem.id), 0) as valorOriginal, " +
            "       coalesce((select estajuste.valoracumuladodaamortizacao + estajuste.valoracumuladodadepreciacao + " +
            "                        estajuste.valoracumuladodaexaustao + estajuste.valoracumuladodeajuste " +
            "                 from estadobem estajuste " +
            "                          inner join eventobem evAjuste on evAjuste.estadoresultante_id = estajuste.id " +
            "                 where evAjuste.dataoperacao = " +
            "                       (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evAjuste.bem_id) " +
            "                   and evAjuste.bem_id = bem.id), 0)  as valorAjuste " +
            " from transferenciabem item " +
            "  inner join lotetransferenciabem sol on sol.id = item.LOTETRANSFERENCIABEM_ID " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem estInicial on estInicial.id = ev.estadoinicial_id " +
            "  inner join estadobem estResultante on estResultante.id = ev.estadoresultante_id " +
            "  inner join bem on bem.id = ev.bem_id " +
            "  inner join vwhierarquiaorcamentaria vworcOrigem on vworcOrigem.subordinada_id = estInicial.detentoraorcamentaria_id " +
            "        and trunc(sol.DATAHORACRIACAO) between vworcOrigem.INICIOVIGENCIA and coalesce(vworcOrigem.FIMVIGENCIA, trunc(sol.DATAHORACRIACAO)) " +
            "  inner join vwhierarquiaorcamentaria vworcDestino on vworcDestino.subordinada_id = estResultante.detentoraorcamentaria_id " +
            "        and trunc(sol.DATAHORACRIACAO) between vworcDestino.INICIOVIGENCIA and coalesce(vworcDestino.FIMVIGENCIA, trunc(sol.DATAHORACRIACAO)) " +
            "  inner join VWHIERARQUIAADMINISTRATIVA vwadmOrigem on vwadmOrigem.subordinada_id = estInicial.DETENTORAADMINISTRATIVA_ID " +
            "        and trunc(sol.DATAHORACRIACAO) between vwadmOrigem.INICIOVIGENCIA and coalesce(vwadmOrigem.FIMVIGENCIA, trunc(sol.DATAHORACRIACAO)) " +
            "  inner join VWHIERARQUIAADMINISTRATIVA vwadmDestino on vwadmDestino.subordinada_id = estResultante.DETENTORAADMINISTRATIVA_ID " +
            "        and trunc(sol.DATAHORACRIACAO) between vwadmDestino.INICIOVIGENCIA and coalesce(vwadmDestino.FIMVIGENCIA, trunc(sol.DATAHORACRIACAO))" +
            " where sol.situacaotransferenciabem = :situacaoTransferencia " +
            "  and sol.tipoBem = :tipobem " +
            "  and sol.id = :idSolicitacao " +
            " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", solicitacao.getId());
        q.setParameter("tipobem", TipoBem.MOVEIS.name());
        q.setParameter("situacaoTransferencia", SituacaoDaSolicitacao.AGUARDANDO_EFETIVACAO.name());
        List<VOItemSolicitacaoTransferencia> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            VOItemSolicitacaoTransferencia item = new VOItemSolicitacaoTransferencia();
            item.setIdItem(((BigDecimal) obj[0]).longValue());
            item.setIdBem(((BigDecimal) obj[1]).longValue());
            item.setRegistroPatrimonial((String) obj[2]);
            item.setDescricao((String) obj[3]);
            item.setEstadoConservacaoBem(EstadoConservacaoBem.valueOf((String) obj[4]));
            item.setSituacaoConservacaoBem(SituacaoConservacaoBem.valueOf((String) obj[5]));
            item.setUnidadeOrcamentariaOrigem(em.find(HierarquiaOrganizacional.class, (((BigDecimal) obj[6]).longValue())));
            item.setUnidadeAdministrativaOrigem(em.find(HierarquiaOrganizacional.class, (((BigDecimal) obj[8]).longValue())));
            item.setUnidadeAdministrativaDestino(em.find(HierarquiaOrganizacional.class, (((BigDecimal) obj[9]).longValue())));
            item.setValorOriginal((BigDecimal) obj[10]);
            item.setValorAjuste((BigDecimal) obj[11]);
            toReturn.add(item);
        }
        if (assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            for (VOItemSolicitacaoTransferencia item : toReturn) {
                String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(item.getIdBem(), assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), assistente.getDataLancamento());
                if (!mensagem.isEmpty()) {
                    assistente.getMensagens().add(mensagem);
                }
            }
        }
        return new AsyncResult<>(toReturn);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<LoteEfetivacaoTransferenciaBem> salvarEfetivacaoAsync(LoteEfetivacaoTransferenciaBem entity, List<VOItemSolicitacaoTransferencia> bensSolicitacao, AssistenteMovimentacaoBens assistente) {
        entity = salvarEfetivacao(entity, bensSolicitacao, assistente);
        return new AsyncResult<>(entity);
    }

    public LoteEfetivacaoTransferenciaBem salvarEfetivacao(LoteEfetivacaoTransferenciaBem entity, List<VOItemSolicitacaoTransferencia> bensSolicitacao, AssistenteMovimentacaoBens assistente) {
        List<EventoBem> eventosContabilizacao = new ArrayList<>();
        List<TransferenciaBem> itensSolicitacaoTransferencia = new ArrayList<>();

        LoteTransferenciaBem solicitacaoTransferencia = entity.getSolicitacaoTransferencia();
        entity = salvarEfetivacaoGerandoCodigo(entity);
        assistente.setSelecionado(entity);

        salvarSolicitacaoTransferencia(solicitacaoTransferencia);

        for (VOItemSolicitacaoTransferencia itemSolicitacao : bensSolicitacao) {
            TransferenciaBem transferenciaBem = em.find(TransferenciaBem.class, itemSolicitacao.getIdItem());
            itensSolicitacaoTransferencia.add(transferenciaBem);

            Bem bem = transferenciaBem.getBem();
            EstadoBem estadoOriginal = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoConcedido;
            EventoBem ultimoEvento;
            EstadoBem estadoRecebido;
            EfetivacaoTransferenciaBem itemEfetivacao;

            TransferenciaBemConcedida transferenciaBemConcedida = criarTransferenciaConcedida(estadoOriginal, bem, assistente);
            estadoConcedido = transferenciaBemConcedida.getEstadoResultante();

            /*Depreciação Concedida*/
            if (estadoOriginal.getValorAcumuladoDaDepreciacao().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaDepreciacaoConcedida depreciacaoConcedida = criarTransferenciaDepreciacaoConcedida(estadoConcedido, bem, assistente);
                estadoConcedido = depreciacaoConcedida.getEstadoResultante();
            }
            /*Amortização Concedida*/
            if (estadoOriginal.getValorAcumuladoDaAmortizacao().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaAmortizacaoConcedida amortizacaoConcedida = criarTransferenciaAmortizacaoConcedida(estadoConcedido, bem, assistente);
                estadoConcedido = amortizacaoConcedida.getEstadoResultante();
            }
            /*Exaustão Concedida*/
            if (estadoOriginal.getValorAcumuladoDaExaustao().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaExaustaoConcedida exaustaoConcedida = criarTransferenciaExaustaoConcedida(estadoConcedido, bem, assistente);
                estadoConcedido = exaustaoConcedida.getEstadoResultante();
            }
            /*Ajuste Concedido*/
            if (estadoOriginal.getValorAcumuladoDeAjuste().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaReducaoConcedida transferenciaReducaoConcedida = criarTransferenciaReducaoConcedida(estadoConcedido, bem, assistente);
            }

            itemEfetivacao = criarTransferenciaRecebida(entity, itemSolicitacao, transferenciaBem, solicitacaoTransferencia, estadoOriginal, assistente);
            estadoRecebido = itemEfetivacao.getEstadoResultante();
            ultimoEvento = itemEfetivacao;

            /*Depreciação Recebida*/
            if (estadoOriginal.getValorAcumuladoDaDepreciacao().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaDepreciacaoRecebida transferenciaDepreciacaoRecebida = criarTransferenciaDepreciacaoRecebida(estadoOriginal, estadoRecebido, bem, assistente);
                estadoRecebido = transferenciaDepreciacaoRecebida.getEstadoResultante();
                ultimoEvento = transferenciaDepreciacaoRecebida;
            }
            /*Exaustão Recebida*/
            if (estadoOriginal.getValorAcumuladoDaExaustao().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaExaustaoRecebida transferenciaExaustaoRecebida = criarTransferenciaExaustaoRecebida(estadoOriginal, estadoRecebido, bem, assistente);
                estadoRecebido = transferenciaExaustaoRecebida.getEstadoResultante();
                ultimoEvento = transferenciaExaustaoRecebida;
            }
            /*Amortização Recebida*/
            if (estadoOriginal.getValorAcumuladoDaAmortizacao().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaAmortizacaoRecebida transferenciaAmortizacaoRecebida = criarTransferenciaAmortizacaoRecebida(estadoOriginal, estadoRecebido, bem, assistente);
                estadoRecebido = transferenciaAmortizacaoRecebida.getEstadoResultante();
                ultimoEvento = transferenciaAmortizacaoRecebida;
            }
            /*Ajustes Concedido e Recebido*/
            if (estadoOriginal.getValorAcumuladoDeAjuste().compareTo(BigDecimal.ZERO) > 0) {
                TransferenciaReducaoRecebida transferenciaReducaoRecebida = criarTransferenciaReducaoRecebida(estadoOriginal, estadoRecebido, bem, assistente);
                ultimoEvento = transferenciaReducaoRecebida;
            }
            if (!itemSolicitacao.getUnidadeOrcamentariaOrigem().getSubordinada().equals(itemSolicitacao.getUnidadeOrcamentariaDestino().getSubordinada())) {
                eventosContabilizacao.add(ultimoEvento);
            }
            atualizarUnidadeEstadoBemTransfHierarquiaEncerrada(entity, bem, itemEfetivacao);
            assistente.conta();
        }
        finalizarSolicitacao(itensSolicitacaoTransferencia, solicitacaoTransferencia, assistente);
        aprovacaoTransferenciaBemFacade.finalizarAprovcacao(solicitacaoTransferencia);
        contabilizarOperacao(entity, eventosContabilizacao, assistente);

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso(" ");
        assistente.setSelecionado(entity);

        desbloquearBem(bensSolicitacao, assistente.getConfigMovimentacaoBem());
        return entity;
    }

    private void atualizarUnidadeEstadoBemTransfHierarquiaEncerrada(LoteEfetivacaoTransferenciaBem entity, Bem bem, EfetivacaoTransferenciaBem itemEfetivacao) {
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            List<EstadoBem> estados = bemFacade.buscarEstadoBemComHierarquiaEncerrada(bem, entity.getDataEfetivacao());
            for (EstadoBem est : estados) {
                est.setDetentoraAdministrativa(itemEfetivacao.getEstadoResultante().getDetentoraAdministrativa());
                em.merge(est);
            }
        }
    }

    private LoteEfetivacaoTransferenciaBem salvarEfetivacaoGerandoCodigo(LoteEfetivacaoTransferenciaBem entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(getSingletonGeradorCodigo().getProximoCodigo(LoteEfetivacaoTransferenciaBem.class, "codigo"));
        }
        entity = em.merge(entity);
        return entity;
    }

    private void desbloquearBem(List<VOItemSolicitacaoTransferencia> bensSolicitacao, ConfigMovimentacaoBem configuracao) {
        List<Number> bensRecuperados = Lists.newArrayList();
        for (VOItemSolicitacaoTransferencia item : bensSolicitacao) {
            bensRecuperados.add(item.getIdBem());
        }
        if (!bensRecuperados.isEmpty()) {
            configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
        }
    }

    private void finalizarSolicitacao(List<TransferenciaBem> itensSolicitacaoTransferencia, LoteTransferenciaBem solicitacao, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Atualizando Evento da Solicitação de Transferência...");
        assistente.setTotal(itensSolicitacaoTransferencia.size());
        for (TransferenciaBem transferenciaBem : itensSolicitacaoTransferencia) {
            transferenciaBem.setLoteEfetivacao((LoteEfetivacaoTransferenciaBem) assistente.getSelecionado());
            transferenciaBem.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
            em.merge(transferenciaBem);
            assistente.conta();
        }
    }

    private LoteTransferenciaBem salvarSolicitacaoTransferencia(LoteTransferenciaBem solicitacao) {
        try {
            solicitacao.setDataVersao(new Date());
            solicitacao.setSituacaoTransferenciaBem(SituacaoDaSolicitacao.FINALIZADA);
            return em.merge(solicitacao);
        } catch (Exception e) {
            logger.debug("salvarSolicitacaoTransferencia {}", e);
            throw e;
        }
    }

    private void contabilizarOperacao(LoteEfetivacaoTransferenciaBem efetivacao, List<EventoBem> itensEfetivacao, AssistenteMovimentacaoBens assistenteBarraProgresso) throws ExcecaoNegocioGenerica {
        integradorPatrimonialContabilFacade.transferirBens(itensEfetivacao, efetivacao.getHistoricoRazao(), assistenteBarraProgresso);
    }

    private EfetivacaoTransferenciaBem criarTransferenciaRecebida(LoteEfetivacaoTransferenciaBem efetivacao, VOItemSolicitacaoTransferencia itemSolicitacao,
                                                                  TransferenciaBem transferenciaBem, LoteTransferenciaBem solicitacao,
                                                                  EstadoBem estadoBem, AssistenteMovimentacaoBens assistente) {

        EfetivacaoTransferenciaBem transferenciaRecebida = new EfetivacaoTransferenciaBem();
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferenciaRecebida.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 3, TipoPrazo.SEGUNDOS));
        transferenciaRecebida.setDataOperacao(DataUtil.somaPeriodo(transferenciaRecebida.getDataOperacao(), 4, TipoPrazo.SEGUNDOS));
        if (efetivacao.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferenciaRecebida.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 4, TipoPrazo.SEGUNDOS));
        }

        transferenciaRecebida.setEstadoInicial(estadoBem);
        transferenciaRecebida.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        EstadoBem resultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(transferenciaRecebida.getEstadoInicial());

        resultante.setDetentoraAdministrativa(solicitacao.getUnidadeDestino());
        resultante.setDetentoraOrcamentaria(itemSolicitacao.getUnidadeOrcamentariaDestino().getSubordinada());

        resultante.setValorAcumuladoDaAmortizacao(BigDecimal.ZERO);
        resultante.setValorAcumuladoDaDepreciacao(BigDecimal.ZERO);
        resultante.setValorAcumuladoDaExaustao(BigDecimal.ZERO);
        resultante.setValorAcumuladoDeAjuste(BigDecimal.ZERO);
        resultante = em.merge(resultante);

        transferenciaRecebida.setEstadoResultante(resultante);
        transferenciaRecebida.setBem(transferenciaBem.getBem());
        transferenciaRecebida.setLote(efetivacao);

        transferenciaRecebida.setTransferenciaBem(transferenciaBem);
        transferenciaRecebida.setValorDoLancamento(resultante.getValorOriginal());
        return em.merge(transferenciaRecebida);
    }

    private TransferenciaBemConcedida criarTransferenciaConcedida(EstadoBem estadoBem, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoBem);
        novoEstado.setValorOriginal(BigDecimal.ZERO);

        TransferenciaBemConcedida transferencia = new TransferenciaBemConcedida(em.merge(novoEstado), bem, estadoBem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(dataLancamento);
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 2, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }

    private TransferenciaDepreciacaoConcedida criarTransferenciaDepreciacaoConcedida(EstadoBem estadoBem, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem efetivacao = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoBem);
        novoEstado.setValorAcumuladoDaDepreciacao(BigDecimal.ZERO);
        TransferenciaDepreciacaoConcedida transferencia = new TransferenciaDepreciacaoConcedida(em.merge(novoEstado), bem, estadoBem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 1, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 3, TipoPrazo.SEGUNDOS));
        if (efetivacao.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 3, TipoPrazo.SEGUNDOS));
        }

        transferencia.setEfetivacaoTransferencia(efetivacao);
        return em.merge(transferencia);
    }

    private TransferenciaAmortizacaoConcedida criarTransferenciaAmortizacaoConcedida(EstadoBem estadoBem, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoBem);
        novoEstado.setValorAcumuladoDaAmortizacao(BigDecimal.ZERO);
        TransferenciaAmortizacaoConcedida transferencia = new TransferenciaAmortizacaoConcedida(em.merge(novoEstado), bem, estadoBem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 1, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 3, TipoPrazo.SEGUNDOS));
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 3, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }

    private TransferenciaExaustaoConcedida criarTransferenciaExaustaoConcedida(EstadoBem estadoBem, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoBem);
        novoEstado.setValorAcumuladoDaExaustao(BigDecimal.ZERO);
        TransferenciaExaustaoConcedida transferencia = new TransferenciaExaustaoConcedida(em.merge(novoEstado), bem, estadoBem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 1, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 3, TipoPrazo.SEGUNDOS));
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 3, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }

    private TransferenciaReducaoConcedida criarTransferenciaReducaoConcedida(EstadoBem estadoBem, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoBem);
        novoEstado.setValorAcumuladoDeAjuste(BigDecimal.ZERO);
        TransferenciaReducaoConcedida transferencia = new TransferenciaReducaoConcedida(em.merge(novoEstado), bem, estadoBem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 1, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 3, TipoPrazo.SEGUNDOS));
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 3, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }

    private TransferenciaDepreciacaoRecebida criarTransferenciaDepreciacaoRecebida(EstadoBem estadoOriginal, EstadoBem estadoRecebido, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoRecebido);
        novoEstado.setValorAcumuladoDaDepreciacao(estadoOriginal.getValorAcumuladoDaDepreciacao());
        TransferenciaDepreciacaoRecebida transferencia = new TransferenciaDepreciacaoRecebida(estadoOriginal, em.merge(novoEstado), bem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 4, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 5, TipoPrazo.SEGUNDOS));
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 5, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }

    private TransferenciaAmortizacaoRecebida criarTransferenciaAmortizacaoRecebida(EstadoBem estadoOriginal, EstadoBem estadoRecebido, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoRecebido);
        novoEstado.setValorAcumuladoDaAmortizacao(estadoOriginal.getValorAcumuladoDaAmortizacao());
        TransferenciaAmortizacaoRecebida transferencia = new TransferenciaAmortizacaoRecebida(estadoOriginal, em.merge(novoEstado), bem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 4, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 5, TipoPrazo.SEGUNDOS));
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 5, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }

    private TransferenciaExaustaoRecebida criarTransferenciaExaustaoRecebida(EstadoBem estadoOriginal, EstadoBem estadoRecebido, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoRecebido);
        novoEstado.setValorAcumuladoDaExaustao(estadoOriginal.getValorAcumuladoDaExaustao());
        TransferenciaExaustaoRecebida transferencia = new TransferenciaExaustaoRecebida(estadoOriginal, em.merge(novoEstado), bem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 4, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 5, TipoPrazo.SEGUNDOS));
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 5, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }

    private TransferenciaReducaoRecebida criarTransferenciaReducaoRecebida(EstadoBem estadoOriginal, EstadoBem estadoRecebido, Bem bem, AssistenteMovimentacaoBens assistente) {
        LoteEfetivacaoTransferenciaBem entity = (LoteEfetivacaoTransferenciaBem) assistente.getSelecionado();
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoRecebido);
        novoEstado.setValorAcumuladoDeAjuste(estadoOriginal.getValorAcumuladoDeAjuste());
        TransferenciaReducaoRecebida transferencia = new TransferenciaReducaoRecebida(estadoOriginal, em.merge(novoEstado), bem);

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        transferencia.setDataLancamento(DataUtil.somaPeriodo(dataLancamento, 4, TipoPrazo.SEGUNDOS));
        transferencia.setDataOperacao(DataUtil.somaPeriodo(transferencia.getDataOperacao(), 5, TipoPrazo.SEGUNDOS));
        if (entity.getSolicitacaoTransferencia().getTransfHierarquiaEncerrada()) {
            transferencia.setDataOperacao(DataUtil.somaPeriodo(dataLancamento, 5, TipoPrazo.SEGUNDOS));
        }
        transferencia.setEfetivacaoTransferencia(entity);
        return em.merge(transferencia);
    }


    public List<LoteEfetivacaoTransferenciaBem> completaLoteEfetivacaoPorUsuarioExercicio(String filtro, TipoBem tipoBem) {
        String sql = "SELECT " +
            "            DISTINCT LT.* " +
            "     FROM LOTEEFETTRANSFBEM LT " +
            "     INNER JOIN EFETIVACAOTRANSFERENCIABEM EF ON EF.LOTE_ID = LT.ID " +
            "     INNER JOIN EVENTOBEM EV ON EV.ID = EF.ID " +
            "        WHERE   LT.USUARIOSISTEMA_ID  = :USU    " +
            "           AND  EV.SITUACAOEVENTOBEM  = :SITUACAO " +
            "           AND  EXTRACT(YEAR FROM LT.DATAEFETIVACAO) = :ANO" +
            "           AND  LT.TIPOBEM = :tipobem " +
            "           AND TO_CHAR(LT.CODIGO) LIKE :FILTRO ";
        sql += " ORDER BY LT.ID DESC";

        Query q = em.createNativeQuery(sql, LoteEfetivacaoTransferenciaBem.class);
        q.setParameter("USU", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("ANO", sistemaFacade.getExercicioCorrente().getAno().toString());
        q.setParameter("SITUACAO", SituacaoEventoBem.FINALIZADO.name());
        q.setParameter("tipobem", tipoBem.name());
        q.setParameter("FILTRO", "%" + filtro.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<EfetivacaoTransferenciaBem> recuperarEfetivacoesFinalizadasDoLoteEfetivacao(LoteEfetivacaoTransferenciaBem lote) {
        if (lote == null) return new ArrayList<>();
        String sql = "" +
            " select  " +
            "  ev.*,  " +
            "  ef.* " +
            " from efetivacaotransferenciabem ef " +
            "  inner join eventobem ev on ev.id = ef.id" +
            "  inner join bem on bem.id = ev.bem_id " +
            "  inner join movimentobloqueiobem mov on mov.bem_id = bem.id " +
            " where ef.lote_id = :loteID " +
            "  and ev.situacaoeventobem = :situacao " +
            "  and mov.movimentoum = :tipoUmLiberado " +
            "  and mov.movimentodois = :tipoDoisLiberado " +
            "  and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where  bem.id = mov2.bem_id) " +
            "  and not exists( " +
            "                select  " +
            "                evEst.id " +
            "                from solicitestornotransfer sol " +
            "                  inner join loteefettransfbem lote on lote.id = sol.loteefetivacaotransferencia_id " +
            "                  inner join efetivacaoestornotransf efet on efet.solicitacaoestorno_id = sol.id " +
            "                  inner join itemefetiestortransf item on item.efetivacaoestorno_id = efet.id " +
            "                  inner join eventobem evEst on evEst.id = item.id " +
            "                where evEst.situacaoeventobem = :situacao " +
            "                  and evEst.bem_id = ev.bem_id " +
            "                  and lote.id = :loteID " +
            "                  ) ";
        Query q = em.createNativeQuery(sql, EfetivacaoTransferenciaBem.class);
        q.setParameter("loteID", lote.getId());
        q.setParameter("situacao", SituacaoEventoBem.FINALIZADO.name());
        q.setParameter("tipoUmLiberado", Boolean.FALSE);
        q.setParameter("tipoDoisLiberado", Boolean.FALSE);
        return q.getResultList();
    }

    public Long recuperarIdUltimoLoteEfetivacaoTransferenciaDoBemQueNaoFoiEstornado(Bem bem, AssistenteMovimentacaoBens assistente) {
        String sql = "SELECT" +
            "     MAX(LT.ID)" +
            "     FROM LOTEEFETTRANSFBEM LT" +
            "     INNER JOIN EFETIVACAOTRANSFERENCIABEM EF ON EF.LOTE_ID = LT.ID" +
            "     INNER JOIN EVENTOBEM EV ON EV.ID = EF.ID" +
            "     WHERE LT.USUARIOSISTEMA_ID = :USU " +
            "         AND EV.SITUACAOEVENTOBEM = :SITUACAO" +
            "         AND EV.BEM_ID = :BEM" +
            "         AND  EXTRACT(YEAR FROM LT.DATAEFETIVACAO) = :ANO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("USU", assistente.getUsuarioSistema().getId());
        q.setParameter("ANO", assistente.getExercicio().getAno().toString());
        q.setParameter("SITUACAO", SituacaoEventoBem.FINALIZADO.name());
        q.setParameter("BEM", bem.getId());
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<VOTransferenciaEfetivadaBem> buscarTransferenciaEfetivada(String condicao) {
        String sql = " SELECT " +
            "  efetivacao.DATAlancamento                AS dataEfetivacao, " +
            "  loteefetivacao.CODIGO                    AS codigoEfetivacao, " +
            "  lt.DESCRICAO                             AS descricaoTransf, " +
            "  admDest.codigo                           AS codigoAdmDestino, " +
            "  admDest.descricao                        AS descricaoAdmDestino, " +
            "  admOrigem.codigo                         AS codigoAdmOrigem, " +
            "  admOrigem.descricao                      AS descricaoAdmOrigem, " +
            "  bem.IDENTIFICACAO                        AS codigoBem, " +
            "  bem.DESCRICAO                            AS descricaoBem, " +
            "  grupo.CODIGO || ' - ' || grupo.DESCRICAO AS grupoBem, " +
            "  coalesce(resultante.VALORORIGINAL, 0)    AS valorbem, " +
            "  coalesce(resultante.VALORACUMULADODAAMORTIZACAO + resultante.VALORACUMULADODADEPRECIACAO + resultante.VALORACUMULADODAEXAUSTAO " +
            "  + resultante.VALORACUMULADODEAJUSTE, 0)  AS reajuste, " +
            "  coalesce(resultante.VALORORIGINAL - (resultante.VALORACUMULADODAAMORTIZACAO + resultante.VALORACUMULADODADEPRECIACAO + resultante.VALORACUMULADODAEXAUSTAO " +
            "   + resultante.VALORACUMULADODEAJUSTE), 0) AS valorAtual " +
            " FROM LOTETRANSFERENCIABEM lt " +
            "  INNER JOIN TRANSFERENCIABEM transf ON transf.LOTETRANSFERENCIABEM_ID = lt.id " +
            "  INNER JOIN efetivacaotransferenciabem ef ON ef.TRANSFERENCIABEM_ID = transf.id " +
            "  INNER JOIN LOTEEFETTRANSFBEM loteefetivacao ON loteefetivacao.ID = ef.LOTE_ID " +
            "  INNER JOIN EVENTOBEM efetivacao ON efetivacao.id = transf.id " +
            "  INNER JOIN bem ON bem.id = efetivacao.BEM_ID " +
            "  INNER JOIN EstadoBem resultante ON resultante.id = efetivacao.ESTADORESULTANTE_ID " +
            "  INNER JOIN estadobem inicial ON inicial.id = efetivacao.ESTADOINICIAL_ID " +
            "  INNER JOIN GRUPOBEM grupo ON grupo.id = resultante.GRUPOBEM_ID " +
            "  INNER JOIN VWHIERARQUIAADMINISTRATIVA admDest ON admDest.SUBORDINADA_ID = lt.UNIDADEDESTINO_ID " +
            "  INNER JOIN VWHIERARQUIAADMINISTRATIVA admOrigem ON admOrigem.SUBORDINADA_ID = inicial.DETENTORAADMINISTRATIVA_ID " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA orcDest ON orcDest.SUBORDINADA_ID = resultante.DETENTORAORCAMENTARIA_ID " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA orcOrigem ON orcOrigem.SUBORDINADA_ID = inicial.DETENTORAORCAMENTARIA_ID " +
            " WHERE cast(efetivacao.DATALANCAMENTO as Date) BETWEEN admDest.INICIOVIGENCIA AND coalesce(admDest.FIMVIGENCIA, efetivacao.DATALANCAMENTO) " +
            "      AND cast(efetivacao.DATALANCAMENTO as Date) BETWEEN admOrigem.INICIOVIGENCIA AND coalesce(admOrigem.FIMVIGENCIA, efetivacao.DATALANCAMENTO) " +
            "      AND cast(efetivacao.DATALANCAMENTO as Date) BETWEEN orcDest.INICIOVIGENCIA AND coalesce(orcDest.FIMVIGENCIA, efetivacao.DATALANCAMENTO) " +
            "      AND cast(efetivacao.DATALANCAMENTO as Date) BETWEEN orcOrigem.INICIOVIGENCIA AND coalesce(orcOrigem.FIMVIGENCIA, efetivacao.DATALANCAMENTO) " +
            "      AND NOT exists(" +
            "                     SELECT 1 FROM ITEMEFETIESTORTRANSF estorno " +
            "                       INNER JOIN ItemEstornoTransferencia sol_estorno ON sol_estorno.ID = estorno.ITEMESTORNOTRANSFERENCIA_ID " +
            "                     WHERE sol_estorno.EFETIVACAOTRANSFERENCIA_ID = ef.id " +
            "      ) ";
        sql += condicao != null ? condicao : "";
        sql += " ORDER BY efetivacao.DATAlancamento, admDest.CODIGO, grupo.CODIGO, TO_NUMBER(BEM.IDENTIFICACAO) ";
        Query q = em.createNativeQuery(sql);
        return VOTransferenciaEfetivadaBem.preencherDados(q.getResultList());
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public List<Bem> buscarBensEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacao) {
        String sql = "    " +
            " select  " +
            "   bem.* " +
            " from loteefettransfbem efet " +
            "   inner join efetivacaotransferenciabem itemEf on itemef.lote_id = efet.id " +
            "   inner join eventobem ev on ev.id = itemef.id " +
            "   inner join bem on bem.id = ev.bem_id " +
            "   inner join transferenciabem itemSol on itemsol.id = itemef.transferenciabem_id  " +
            "   inner join lotetransferenciabem loteSol on loteSol.id = itemsol.lotetransferenciabem_id " +
            " where efet.id = :idEfetivacao " +
            " order by to_number(bem.identificacao)";

        Query q = em.createNativeQuery(sql, Bem.class);
        q.setParameter("idEfetivacao", efetivacao.getId());
        return q.getResultList();
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public LoteEfetivacaoLevantamentoBemFacade getLoteEfetivacaoLevantamentoBemFacade() {
        return loteEfetivacaoLevantamentoBemFacade;
    }

    public LoteTransferenciaFacade getLoteTransferenciaFacade() {
        return loteTransferenciaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public IntegradorPatrimonialContabilFacade getIntegradorPatrimonialContabilFacade() {
        return integradorPatrimonialContabilFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public String buscarUnidadeDestinoTransferencia(TransferenciaBem transferenciaBem) {
        String sql = "    " +
            " select vw.codigo || ' - ' || vw.descricao as unidade " +
            "  from EfetivacaoTransferenciaBem t " +
            " inner join eventobem ev on ev.id = t.id " +
            " inner join estadobem est on est.id  = ev.estadoresultante_id " +
            " inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = est.detentoraorcamentaria_id " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between vw.iniciovigencia and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " where t.transferenciabem_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", transferenciaBem.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        try {
            return (String) q.getSingleResult();
        } catch (NoResultException e) {
            return transferenciaBem.getEstadoResultante().getDetentoraOrcamentaria().getDescricao();
        }
    }
}
