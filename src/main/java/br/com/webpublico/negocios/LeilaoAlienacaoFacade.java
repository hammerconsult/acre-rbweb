package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoParecer;
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
 * User: Wellington
 * Date: 12/11/14
 * Time: 15:18
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class LeilaoAlienacaoFacade extends AbstractFacade<LeilaoAlienacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SolicitacaoAlienacaoFacade solicitacaoAlienacaoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private AvaliacaoAlienacaoFacade avaliacaoAliencaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AprovacaoAlienacaoFacade aprovacaoAlienacaoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public LeilaoAlienacaoFacade() {
        super(LeilaoAlienacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LeilaoAlienacao recuperarComDependenciaArquivo(Object id) {
        LeilaoAlienacao leilaoAlienacao = super.recuperar(id);
        Hibernate.initialize(leilaoAlienacao.getLotesLeilaoAlienacao());
        for (LeilaoAlienacaoLote lote : leilaoAlienacao.getLotesLeilaoAlienacao()) {
            if (lote.getDetentDoctoFiscalLiquidacao() != null) {
                DetentorDoctoFiscalLiquidacao doct = em.find(DetentorDoctoFiscalLiquidacao.class, lote.getDetentDoctoFiscalLiquidacao().getId());
                Hibernate.initialize(doct.getDoctoLista());
                lote.setDetentDoctoFiscalLiquidacao(doct);
            }
        }
        if (leilaoAlienacao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(leilaoAlienacao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return leilaoAlienacao;
    }

    public LeilaoAlienacao recuperarComDependenciaDoLote(Object id) {
        LeilaoAlienacao leilaoAlienacao = super.recuperar(id);
        Hibernate.initialize(leilaoAlienacao.getLotesLeilaoAlienacao());
        return leilaoAlienacao;
    }

    @Override
    public LeilaoAlienacao recuperar(Object id) {
        LeilaoAlienacao leilaoAlienacao = super.recuperar(id);
        Hibernate.initialize(leilaoAlienacao.getLotesLeilaoAlienacao());
        for (LeilaoAlienacaoLote lote : leilaoAlienacao.getLotesLeilaoAlienacao()) {
            lote.setLoteAvaliacaoAlienacao(solicitacaoAlienacaoFacade.recuperarLoteSolicitacaoAlienacao(lote.getLoteAvaliacaoAlienacao().getId()));
            Hibernate.initialize(lote.getBensDoLoteDaAlienacao());
            DetentorDoctoFiscalLiquidacao doct = em.find(DetentorDoctoFiscalLiquidacao.class, lote.getDetentDoctoFiscalLiquidacao().getId());
            Hibernate.initialize(doct.getDoctoLista());
            lote.setDetentDoctoFiscalLiquidacao(doct);
        }
        if (leilaoAlienacao.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(leilaoAlienacao.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return leilaoAlienacao;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public void removerSelecionado(LeilaoAlienacao entity) {
        try {
            entity = recuperarComDependenciaArquivo(entity.getId());
            List<Number> idsItensLeilao = Lists.newArrayList();
            for (LeilaoAlienacaoLote loteEfetivacao : entity.getLotesLeilaoAlienacao()) {
                idsItensLeilao.addAll(buscarIdsItemLeilaoAlienacao(loteEfetivacao));
            }
            removerItemDoLote(idsItensLeilao);
            removerLote(entity.getLotesLeilaoAlienacao());
            em.remove(em.find(LeilaoAlienacao.class, entity.getId()));
        } catch (Exception ex) {
            logger.error("Erro ao remover Efetivação de Alienação {}", ex);
        }
    }

    private void removerLote(List<LeilaoAlienacaoLote> lotesRemovidos) {
        if (lotesRemovidos != null && !lotesRemovidos.isEmpty()) {
            for (LeilaoAlienacaoLote lote : lotesRemovidos) {
                Query update = em.createNativeQuery("delete from leilaoalienacaolote where id = " + lote.getId());
                update.executeUpdate();
            }
        }
    }

    private void removerItemDoLote(List<Number> itensLeilao) {
        if (itensLeilao != null && !itensLeilao.isEmpty()) {
            for (Number id : itensLeilao) {
                Query deleteItem = em.createNativeQuery(" delete from leilaoalienacaolotebem where id = " + id);
                deleteItem.executeUpdate();

                Query deleteEvento = em.createNativeQuery("delete from eventobem where id = " + id);
                deleteEvento.executeUpdate();
            }
        }
    }

    public List<LeilaoAlienacao> buscarLeilaoAlienacaoDisponiveisParaBaixa(String parte) {
        String sql = " select la.* " +
            "   from leilaoalienacao la " +
            "  where exists (select 1 from leilaoalienacaolote lal " +
            "                   where lal.leilaoalienacao_id = la.id " +
            "                   and lal.arrematado = 1" +
            "                 )" +
            "  and la.id not in (select sol.leilaoalienacao_id from solicitabaixapatrimonial sol  " +
            "                    left join parecerbaixapatrimonial par on par.solicitacaobaixa_id = sol.id " +
            "                     where sol.leilaoalienacao_id = la.id " +
            "                     and par.situacaoparecer = :Parecerdeferido)  " +
            "  and la.situacaoAlienacao = :situacaoFinalizada " +
            "  and (to_char(la.codigo) like :parte " +
            "      or to_char(la.descricao) like :parte   " +
            "      or (to_char(la.dataefetivacao, 'dd/MM/yyyy') like :parte)) ";
        Query q = em.createNativeQuery(sql, LeilaoAlienacao.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("situacaoFinalizada", SituacaoAlienacao.FINALIZADA.name());
        q.setParameter("Parecerdeferido", SituacaoParecer.DEFERIDO.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();

    }

    public List<DoctoFiscalLiquidacao> buscarDocumentoFiscalLoteLeilao(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        String sql = " " +
            " select " +
            "   docto.* " +
            " from detentordocto do " +
            "   inner join dentdocfiscliquidacao det on do.detdoctofiscalliquidacao_id = det.id " +
            "   inner join leilaoalienacaolote lote on lote.detentdoctofiscalliquidacao_id = det.id " +
            "   inner join doctofiscalliquidacao docto on docto.id = do.doctofiscalliquidacao_id " +
            " where lote.id = :idLote ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("idLote", leilaoAlienacaoLote.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    private void processarArremateLeilao(AssistenteMovimentacaoBens assistente, List<Number> idsItemLeilao, SituacaoEventoBem situacaoEventoBem) {
        for (Number idItem : idsItemLeilao) {
            atualizarItemLoteAlienacao(idItem.longValue(), situacaoEventoBem);
            assistente.conta();
        }
    }

    private void processarLeilaoAlienacaoLote(LeilaoAlienacaoLote leilaoAlienacaoLote, AssistenteMovimentacaoBens assistente, List<Number> idsItemLeilao) throws ExcecaoNegocioGenerica {
        if (leilaoAlienacaoLote.getArrematado()) {
            List<Number> idsBem = buscarIdsBemPorEfetivacaoAlienacao(leilaoAlienacaoLote.getLeilaoAlienacao(), true);
            processarArremateLeilao(assistente, idsItemLeilao, SituacaoEventoBem.ALIENADO);
            configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(), idsBem);
            configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), idsBem);
        } else {
            List<Number> idsBem = buscarIdsBemPorEfetivacaoAlienacao(leilaoAlienacaoLote.getLeilaoAlienacao(), false);
            processarArremateLeilao(assistente, idsItemLeilao, SituacaoEventoBem.FINALIZADO);
            configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(), idsBem);
        }
    }

    private void arrematarLotes(LeilaoAlienacao selecionado, AssistenteMovimentacaoBens assistente) {
        selecionado = recuperarComDependenciaDoLote(selecionado.getId());
        List<LoteAvaliacaoAlienacao> lotesAvaliacaoAlienacao = Lists.newArrayList();
        List<SolicitacaoAlienacao> solicitacoesAlienacao = Lists.newArrayList();

        for (LeilaoAlienacaoLote loteLeilao : selecionado.getLotesLeilaoAlienacao()) {
            loteLeilao = em.merge(loteLeilao);
            lotesAvaliacaoAlienacao.add(loteLeilao.getLoteAvaliacaoAlienacao());
            assistente.zerarContadoresProcesso();

            List<Number> idsItemLeilaoAlienacao = buscarIdsItemLeilaoAlienacao(loteLeilao);
            assistente.setDescricaoProcesso("Efetivando Itens do Lote: " + loteLeilao.toString() + "...");
            assistente.setTotal(idsItemLeilaoAlienacao.size());
            processarLeilaoAlienacaoLote(loteLeilao, assistente, idsItemLeilaoAlienacao);
        }
        solicitacoesAlienacao.addAll(solicitacaoAlienacaoFacade.buscarSolicitacaoPorAvaliacaoAlienacao(selecionado.getAvaliacaoAlienacao()));
        if (!solicitacoesAlienacao.isEmpty()) {
            movimentarSituacaoSolicitacaoAlienacao(solicitacoesAlienacao, assistente);
        }
        if (!lotesAvaliacaoAlienacao.isEmpty()) {
            avaliacaoAliencaoFacade.alterarSituacaoItemAvaliacao(lotesAvaliacaoAlienacao, assistente, SituacaoEventoBem.FINALIZADO);
        }
    }

    public void atualizarItemLoteAlienacao(Long idItem, SituacaoEventoBem situacaoEventoBem) {
        String sql = " update eventobem set situacaoeventobem = :situacao where id = :idEvento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEvento", idItem.longValue());
        q.setParameter("situacao", situacaoEventoBem.name());
        q.executeUpdate();
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<List<LeilaoAlienacaoLote>> pesquisarAndGerarLotesLeilaoAlienacao(LeilaoAlienacao selecionado, AssistenteMovimentacaoBens assistente) {
        List<LeilaoAlienacaoLote> lotesLeilaoAlienacao = Lists.newArrayList();
        for (LoteAvaliacaoAlienacao loteAlienacao : buscarLotesDaAvaliacao(selecionado.getAvaliacaoAlienacao())) {
            LeilaoAlienacaoLote loteLeilao = new LeilaoAlienacaoLote();
            loteLeilao.setLeilaoAlienacao(selecionado);
            loteLeilao.setLoteAvaliacaoAlienacao(loteAlienacao);
            loteLeilao.getItensAvaliados().addAll(buscarItensSolicitacaoAvaliadosParaLeilao(loteAlienacao, assistente));
            lotesLeilaoAlienacao.add(loteLeilao);
        }
        return new AsyncResult<List<LeilaoAlienacaoLote>>(lotesLeilaoAlienacao);
    }

    private void atualizarValorArrematadoProporcionamente(LeilaoAlienacaoLote loteLeilao, AssistenteMovimentacaoBens assistente) {
        for (VOItemSolicitacaoAlienacao itemAvaliado : loteLeilao.getItensAvaliados()) {
            LeilaoAlienacaoLoteBem itemLoteLeilao = em.find(LeilaoAlienacaoLoteBem.class, itemAvaliado.getIdItemLeilao());
            itemLoteLeilao.setValorProporcionalArrematado(loteLeilao.getArrematado() ? itemAvaliado.getValorArrematado() : BigDecimal.ZERO);
            em.merge(itemLoteLeilao);
            assistente.conta();
        }
    }

    private void criarItemLeilaoAlienacao(LeilaoAlienacaoLote loteLeilao, AssistenteMovimentacaoBens assistente) {
        List<Number> bens = Lists.newArrayList();
        for (VOItemSolicitacaoAlienacao itemAvaliado : loteLeilao.getItensAvaliados()) {
            ItemSolicitacaoAlienacao itemSolicitacao = em.find(ItemSolicitacaoAlienacao.class, itemAvaliado.getIdItem());
            EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(itemSolicitacao.getBem());
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));
            Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());

            LeilaoAlienacaoLoteBem novoItem = new LeilaoAlienacaoLoteBem(ultimoEstado, estadoResultante, itemSolicitacao);
            novoItem.setValorProporcionalArrematado(loteLeilao.getArrematado() ? itemAvaliado.getValorArrematado() : BigDecimal.ZERO);
            novoItem.setLeilaoAlienacaoLote(loteLeilao);
            novoItem.setDataLancamento(dataLancamento);
            em.merge(novoItem);
            bens.add(novoItem.getBem().getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBensPorSituacao(assistente.getConfigMovimentacaoBem(), bens, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
    }

    private List<LoteAvaliacaoAlienacao> buscarLotesDaAvaliacao(AvaliacaoAlienacao avaliacaoAlienacao) {
        String sql = " SELECT lot.* " +
            "            FROM AVALIACAOALIENACAO AV " +
            "           INNER JOIN LOTEAVALIACAOALIENACAO lot ON lot.AVALIACAOALIENACAO_ID = AV.id " +
            "           WHERE AV.id = :Idavaliacao ";
        Query q = em.createNativeQuery(sql, LoteAvaliacaoAlienacao.class);
        q.setParameter("Idavaliacao", avaliacaoAlienacao.getId());
        return q.getResultList();
    }

    private List<Number> buscarIdsItemLeilaoAlienacao(LeilaoAlienacaoLote loteEfetivacao) {
        String sql = " " +
            " select " +
            " item.id " +
            "from leilaoalienacaolotebem item " +
            " where item.leilaoalienacaolote_id = :idLoteEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLoteEfetivacao", loteEfetivacao.getId());
        return (List<Number>) q.getResultList();
    }


    public List<LeilaoAlienacaoLoteBem> buscarItemLeilaoAlienacao(List<Long> idsLotes, AssistenteMovimentacaoBens assistente) {
        String sql = " " +
            " select " +
            "   item.id " +
            " from leilaoalienacaolotebem item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.leilaoalienacaolote_id in (:idsLote) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idsLote", idsLotes);
        List<LeilaoAlienacaoLoteBem> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (ids != null && !ids.isEmpty()) {
                assistente.setDescricaoProcesso("Recuperando Evento Leilão Alienação...");
                assistente.zerarContadoresProcesso();
                assistente.setTotal(ids.size());
                for (BigDecimal id : ids) {
                    LeilaoAlienacaoLoteBem itemLeilao = em.find(LeilaoAlienacaoLoteBem.class, id.longValue());
                    if (itemLeilao != null) {
                        toReturn.add(itemLeilao);
                    }
                    assistente.conta();
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return toReturn;
    }

    public BigDecimal buscarValorTotalAvaliadoDosItens(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        String sql = " SELECT coalesce(sum(item.valorAvaliado), 0) " +
            "            FROM LOTEAVALIACAOALIENACAO lot " +
            "           INNER JOIN ITEMSOLICITACAOALIENACAO item ON item.loteAvaliacaoAlienacao_ID = lot.id " +
            "           WHERE lot.id = :idLote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", loteAvaliacaoAlienacao.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal buscarValorTotalLoteLeilao(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        String sql = "  " +
            " select " +
            "  coalesce(sum(itemsol.valoravaliado),0)  as valorTotalLote " +
            " from leilaoalienacaolote lote " +
            "  inner join leilaoalienacaolotebem item on item.leilaoalienacaolote_id = lote.id " +
            "  inner join itemsolicitacaoalienacao itemSol on itemSol.id = item.itemsolicitacaoalienacao_id " +
            " where lote.id = :idLote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", leilaoAlienacaoLote.getId());
        return (BigDecimal) q.getSingleResult();
    }


    private void movimentarSituacaoSolicitacaoAlienacao(List<SolicitacaoAlienacao> solicitacoesAlienacoes, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Finalizando Eventos da Solicitação e Aprovação...");
        assistente.setTotal(solicitacoesAlienacoes.size());
        for (SolicitacaoAlienacao solicitacaoAlienacao : solicitacoesAlienacoes) {

            List<Number> idsItemSolicitacao = solicitacaoAlienacaoFacade.buscarIdsItemSolicitacao(solicitacaoAlienacao);
            for (Number idItem : idsItemSolicitacao) {
                solicitacaoAlienacaoFacade.concluirItemSolicitacao(idItem, SituacaoEventoBem.FINALIZADO);
            }
            List<Number> idsItemAprovacao = aprovacaoAlienacaoFacade.buscarIdsItemAprovacao(solicitacaoAlienacao);
            for (Number idItem : idsItemAprovacao) {
                aprovacaoAlienacaoFacade.concluirItemAprovacao(idItem, SituacaoEventoBem.FINALIZADO);
            }
            solicitacaoAlienacaoFacade.concluirSolicitacaoAlienacao(solicitacaoAlienacao, SituacaoAlienacao.ALIENADO);
            assistente.conta();
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<LeilaoAlienacao> concluirEfetivacao(LeilaoAlienacao selecionado, AssistenteMovimentacaoBens assistente) {
        try {
            salvarAvaliacaoControleVersao(selecionado);
            if (selecionado.getCodigo() == null) {
                selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(LeilaoAlienacao.class, "codigo"));
            }
            selecionado.setSituacaoAlienacao(SituacaoAlienacao.FINALIZADA);
            selecionado = em.merge(selecionado);
            arrematarLotes(selecionado, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(selecionado);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir efetivação de alienação de bens móveis. " + ex.getMessage());
            throw ex;
        }
        return new AsyncResult<>(selecionado);
    }

    public LeilaoAlienacao salvarEfetivacaoAlienacao(LeilaoAlienacao selecionado) {
        try {
            salvarAvaliacaoControleVersao(selecionado);
            List<LeilaoAlienacaoLote> lotes = Lists.newArrayList(selecionado.getLotesLeilaoAlienacao());
            selecionado.getLotesLeilaoAlienacao().clear();
            LeilaoAlienacao selecionadoSalvo = em.merge(selecionado);
            selecionadoSalvo.setLotesLeilaoAlienacao(lotes);
            return selecionadoSalvo;
        } catch (Exception ex) {
            logger.error("Erro ao salvarEfetivacaoAlienacao{}", ex);
            throw ex;
        }
    }

    private void salvarAvaliacaoControleVersao(LeilaoAlienacao selecionado) {
        selecionado.getAvaliacaoAlienacao().setDataVersao(new Date());
        em.merge(selecionado.getAvaliacaoAlienacao());
    }

    public List<LeilaoAlienacaoLote> salvarLoteEfetivacaoAlienacao(LeilaoAlienacao selecionado) {
        List<LeilaoAlienacaoLote> lotes = Lists.newArrayList();
        try {
            for (LeilaoAlienacaoLote lote : selecionado.getLotesLeilaoAlienacao()) {
                List<VOItemSolicitacaoAlienacao> itensParaProcessar = lote.getItensAvaliados();
                lote.setLeilaoAlienacao(selecionado);
                LeilaoAlienacaoLote loteSalvo = em.merge(lote);

                loteSalvo.setItensAvaliados(itensParaProcessar);
                lotes.add(loteSalvo);
            }
        } catch (Exception ex) {
            logger.error("Erro ao salvarLoteEfetivacaoAlienacao{}", ex);
        }
        return lotes;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<LeilaoAlienacao> salvarItensEfetiacaoAlienacao(LeilaoAlienacao selecionado, AssistenteMovimentacaoBens assistente, List<LeilaoAlienacaoLote> lotesSalvos) {
        try {
            for (LeilaoAlienacaoLote loteLeilaoSalvo : lotesSalvos) {
                assistente.zerarContadoresProcesso();
                assistente.setTotal(loteLeilaoSalvo.getItensAvaliados().size());

                if (assistente.isOperacaoNovo()) {
                    assistente.setDescricaoProcesso("Criando Itens do Lote Alienação " + loteLeilaoSalvo + " ...");
                    criarItemLeilaoAlienacao(loteLeilaoSalvo, assistente);
                } else {
                    assistente.setDescricaoProcesso("Atualizando Valor Arrematado para os Itens do Lote " + loteLeilaoSalvo + "...");
                    atualizarValorArrematadoProporcionamente(loteLeilaoSalvo, assistente);
                }
            }
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            LeilaoAlienacao recuperado = recuperarComDependenciaArquivo(selecionado.getId());
            assistente.setSelecionado(recuperado);
            assistente.setDescricaoProcesso(" ");
            return new AsyncResult<LeilaoAlienacao>(recuperado);
        } catch (Exception ex) {
            logger.error("Erro ao salvarItensEfetiacaoAlienacao {}", ex);
            throw ex;
        }
    }

    public LeilaoAlienacaoLote recuperarLoteArrematadoNoLeilao(LeilaoAlienacao leilaoAlienacao, String lote) {
        if (leilaoAlienacao == null || (lote == null || lote.isEmpty())) {
            return null;
        }
        String sql = " SELECT " +
            "       LOTEALIENACAO.* " +
            "    FROM LEILAOALIENACAOLOTE LOTEALIENACAO " +
            " INNER JOIN LoteAvaliacaoAlienacao LOTE ON LOTE.ID = LOTEALIENACAO.LoteAvaliacaoAlienacao_id " +
            "    WHERE    LOTEALIENACAO.LEILAOALIENACAO_ID = :leilaoalienacao_id " +
            "         AND LOTEALIENACAO.ARREMATADO         = 1 " +
            "         AND lower(trim(LOTE.NUMERO))         = :lote ";
        Query q = em.createNativeQuery(sql, LeilaoAlienacaoLote.class);
        q.setParameter("leilaoalienacao_id", leilaoAlienacao.getId());
        q.setParameter("lote", lote.trim().toLowerCase());
        LeilaoAlienacaoLote loteRetorno = null;
        try {
            loteRetorno = (LeilaoAlienacaoLote) q.getSingleResult();
            loteRetorno.getDetentDoctoFiscalLiquidacao().getDoctoLista().size();
            return loteRetorno;
        } catch (NoResultException no) {
            return loteRetorno;
        }
    }

    public Boolean isLoteAlienacaoBaixado(LeilaoAlienacaoLote lote) {
        if (lote == null) {
            return Boolean.FALSE;
        }
        String hql = "select ef" +
            "      from EfetivacaoBaixaPatrimonial ef " +
            "      join  ef.listaLoteAlienacao lotes " +
            "    where " +
            "     lotes.id = :lote";
        Query q = em.createQuery(hql, EfetivacaoBaixaPatrimonial.class);
        q.setParameter("lote", lote.getId());
        return !q.getResultList().isEmpty();
    }

    public DetentorDoctoFiscalLiquidacao recuperarDetentorDoctoFiscalLiquidacao(DetentorDoctoFiscalLiquidacao
                                                                                    doctoFiscalLiquidacao) {
        String hql = " select d from DetentorDoctoFiscalLiquidacao  d " +
            " inner join d.doctoLista lista" +
            " where d.id = :doc ";
        Query q = em.createQuery(hql, DetentorDoctoFiscalLiquidacao.class);
        q.setParameter("doc", doctoFiscalLiquidacao.getId());
        try {
            doctoFiscalLiquidacao = (DetentorDoctoFiscalLiquidacao) q.getSingleResult();
            doctoFiscalLiquidacao.getDoctoLista().size();
            return doctoFiscalLiquidacao;
        } catch (NoResultException no) {
            return doctoFiscalLiquidacao;
        }
    }

    public List<Number> buscarIdsBemPorEfetivacaoAlienacao(LeilaoAlienacao entity, boolean arrematado) {
        String sql = "" +
            " select " +
            "  bem.id " +
            "  from leilaoalienacaolotebem item " +
            " inner join leilaoalienacaolote lote on lote.id = item.leilaoalienacaolote_id " +
            " inner join eventobem ev on ev.id = item.id " +
            " inner join bem on bem.id = ev.bem_id " +
            " where lote.leilaoalienacao_id = :idEfetivacao" +
            "   and lote.arrematado = :arrematado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", entity.getId());
        q.setParameter("arrematado", arrematado);
        return ((List<Number>) q.getResultList());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<VOItemSolicitacaoAlienacao> buscarItensSolicitacaoArrematadosNoLeilao(LeilaoAlienacaoLote lote) {
        String sql = "" +
            " select " +
            "  item.id as idItem, " +
            "  bem.id as idBem, " +
            "  lote.id as idLote, " +
            "  itemLeilao.id as idItemLeilao, " +
            "  est.detentoraorcamentaria_id as idUnidade, " +
            "  est.detentoraadministrativa_id as idUnidadeAdm, " +
            "  est.grupobem_id as idGrupoBem, " +
            "  bem.identificacao as registro, " +
            "  bem.registroAnterior as registroAnterior,  " +
            "  bem.descricao as descricao, " +
            "  ev.dataoperacao as dataOperacao, " +
            "  vw.codigo || ' - ' || vw.descricao as unidade, " +
            "  coalesce((select estOriginal.valororiginal  " +
            "            from estadobem estOriginal  " +
            "            inner join eventobem evOriginal on evOriginal.estadoresultante_id = estOriginal.id " +
            "            where evOriginal.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evOriginal.bem_id) " +
            "            and evOriginal.bem_id = bem.id),0) as valorOriginal,  " +
            "  coalesce((select estajuste.valoracumuladodaamortizacao + estajuste.valoracumuladodadepreciacao + estajuste.valoracumuladodeajuste " +
            "            from estadobem estajuste  " +
            "            inner join eventobem evAjuste on evAjuste.estadoresultante_id = estajuste.id " +
            "            where evAjuste.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evAjuste.bem_id) " +
            "            and evAjuste.bem_id = bem.id), 0) as valorAjuste,  " +
            "  coalesce(item.valoravaliado, 0) as valorAvaliadao, " +
            "  coalesce(itemleilao.valorproporcionalarrematado, 0) as valorArrematado, " +
            "  bem.dataaquisicao  " +
            " from leilaoalienacaolote lote " +
            "  inner join leilaoalienacaolotebem itemLeilao on itemleilao.leilaoalienacaolote_id = lote.id " +
            "  inner join itemsolicitacaoalienacao item on item.id = itemleilao.itemsolicitacaoalienacao_id " +
            "  inner join eventobem ev on ev.id = itemleilao.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = est.detentoraorcamentaria_id " +
            "    and ev.dataoperacao between vw.iniciovigencia and coalesce(vw.fimvigencia, ev.dataoperacao) " +
            "  inner join bem on bem.id = ev.bem_id   " +
            "  where lote.id = :idLote  " +
            " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", lote.getId());
        List<VOItemSolicitacaoAlienacao> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            toReturn.add(VOItemSolicitacaoAlienacao.criarObjetoVoItemSolicitacaoAlienacao(obj));
        }
        return toReturn;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<VOItemSolicitacaoAlienacao> buscarItensSolicitacaoAvaliadosParaLeilao(LoteAvaliacaoAlienacao lote, AssistenteMovimentacaoBens assistente) {
        String sql = "" +
            " select   " +
            "  item.id as idItem,  " +
            "  bem.id as idBem,  " +
            "  lote.id as idLote,  " +
            "  null as idItemLeilao, " +
            "  est.detentoraorcamentaria_id as idUnidade,  " +
            "  est.detentoraadministrativa_id as idUnidadeAdm, " +
            "  est.grupobem_id as idGrupoBem, " +
            "  bem.identificacao as registro,  " +
            "  bem.registroAnterior as registroAnterior,  " +
            "  bem.descricao as descricao,  " +
            "  ev.dataoperacao as dataOperacao,  " +
            "  vw.codigo || ' - ' || vw.descricao as unidade,  " +
            "  coalesce((select estOriginal.valororiginal  " +
            "            from estadobem estOriginal  " +
            "            inner join eventobem evOriginal on evOriginal.estadoresultante_id = estOriginal.id " +
            "            where evOriginal.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evOriginal.bem_id) " +
            "            and evOriginal.bem_id = bem.id),0) as valorOriginal,  " +
            "  coalesce((select estajuste.valoracumuladodaamortizacao + estajuste.valoracumuladodadepreciacao + estajuste.valoracumuladodeajuste " +
            "            from estadobem estajuste  " +
            "            inner join eventobem evAjuste on evAjuste.estadoresultante_id = estajuste.id " +
            "            where evAjuste.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evAjuste.bem_id) " +
            "            and evAjuste.bem_id = bem.id), 0) as valorAjuste,  " +
            "  coalesce(item.valoravaliado, 0) as valorAvaliadao,    " +
            "  0 as valorArrematado,  " +
            "  bem.dataaquisicao  " +
            " from loteavaliacaoalienacao lote " +
            "  inner join itemsolicitacaoalienacao item on item.loteavaliacaoalienacao_id = lote.id  " +
            "  inner join eventobem ev on ev.id = item.id  " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id  " +
            "  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = est.detentoraorcamentaria_id  " +
            "    and ev.dataoperacao between vw.iniciovigencia and coalesce(vw.fimvigencia, ev.dataoperacao)  " +
            "  inner join bem on bem.id = ev.bem_id  " +
            "  where lote.id = :idLote " +
            " order by to_number(bem.identificacao) ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", lote.getId());
        List<VOItemSolicitacaoAlienacao> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            toReturn.add(VOItemSolicitacaoAlienacao.criarObjetoVoItemSolicitacaoAlienacao(obj));
        }
        solicitacaoAlienacaoFacade.verificarBensDisponiveisParaMovimentacao(assistente, toReturn);
        return toReturn;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public IntegradorPatrimonialContabilFacade getIntegradorPatrimonialContabilFacade() {
        return integradorPatrimonialContabilFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public AvaliacaoAlienacaoFacade getAvaliacaoAliencaoFacade() {
        return avaliacaoAliencaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoAlienacaoFacade getSolicitacaoAlienacaoFacade() {
        return solicitacaoAlienacaoFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}


