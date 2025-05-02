package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/12/14
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AprovacaoAlienacaoFacade extends AbstractFacade<AprovacaoAlienacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SolicitacaoAlienacaoFacade solicitacaoAlienacaoFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    public AprovacaoAlienacaoFacade() {
        super(AprovacaoAlienacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AprovacaoAlienacao recuperarComDependencia(Object id) {
        AprovacaoAlienacao entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            entity.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        entity.getItensAtoLegal().size();
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AprovacaoAlienacao salvarAprovacao(AprovacaoAlienacao entity, AssistenteMovimentacaoBens assistente,
                                                      List<VOItemSolicitacaoAlienacao> itensSolicitados) {
        try {
            movimentarSolicitacaoAlienacao(entity, entity.getSolicitacaoAlienacao(), assistente);
            entity = salvarSelecionado(entity);
            salvarProcessandoBem(entity, assistente, itensSolicitados);
            assistente.setDescricaoProcesso("Persistindo Registros...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao salvar solicitação de alienação de bens móveis. " + ex.getMessage());
            throw ex;
        }
        return entity;
    }

    private AprovacaoAlienacao salvarSelecionado(AprovacaoAlienacao entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(AprovacaoAlienacao.class, "codigo"));
        }
        entity = em.merge(entity);
        return entity;
    }

    private void salvarProcessandoBem(AprovacaoAlienacao entity, AssistenteMovimentacaoBens assistente, List<VOItemSolicitacaoAlienacao> itensSolicitados) {
        assistente.zerarContadoresProcesso();
        assistente.setTotal(itensSolicitados.size());
        assistente.setDescricaoProcesso(entity.getAprovada() ? "Aprovando Itens da Solicitação de Alienação..." : "Rejeitando Itens da Solicitação de Alienação...");
        criarItemAprovacao(entity, assistente, itensSolicitados);
    }

    private void criarItemAprovacao(AprovacaoAlienacao entity, AssistenteMovimentacaoBens assistente, List<VOItemSolicitacaoAlienacao> itensSolicitados) {
        List<Number> idsBem = Lists.newArrayList();
        for (VOItemSolicitacaoAlienacao item : itensSolicitados) {
            criarItemAprovacaoAlienacao(entity, item, assistente);
            idsBem.add(item.getIdBem());
            assistente.conta();
        }
        if (entity.getRejeitada()) {
            configMovimentacaoBemFacade.desbloquearBens(assistente.getConfigMovimentacaoBem(),idsBem);
        } else {
            configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), idsBem);
        }
    }

    private void criarItemAprovacaoAlienacao(AprovacaoAlienacao entity, VOItemSolicitacaoAlienacao item, AssistenteMovimentacaoBens assistente) {
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(item.getBem());
        EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));
        ItemAprovacaoAlienacao novoItem = new ItemAprovacaoAlienacao();
        novoItem.setBem(item.getBem());
        novoItem.setItemSolicitacaoAlienacao(new ItemSolicitacaoAlienacao(item.getIdItem(), BigDecimal.ZERO));
        novoItem.setDataLancamento(dataLancamento);
        novoItem.setSituacaoEventoBem(entity.getAprovada() ? SituacaoEventoBem.AGUARDANDO_EFETIVACAO : SituacaoEventoBem.RECUSADO);
        novoItem.setEstadoInicial(ultimoEstado);
        novoItem.setEstadoResultante(estadoResultante);
        novoItem.setAprovacaoAlienacao(entity);
        novoItem.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
        em.merge(novoItem);
    }

    private void movimentarSolicitacaoAlienacao(AprovacaoAlienacao entity, SolicitacaoAlienacao solicitacaoAlienacao, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Alterando Situação da Solicitação de Alienação...");
        if (entity.getAprovada()) {
            solicitacaoAlienacao.setSituacao(SituacaoAlienacao.AGUARDANDO_AVALIACAO);
        } else {
            solicitacaoAlienacao.setSituacao(SituacaoAlienacao.REJEITADA);
        }
        em.merge(solicitacaoAlienacao);
    }

    @Override
    public AprovacaoAlienacao recuperar(Object id) {
        AprovacaoAlienacao entity = em.find(AprovacaoAlienacao.class, id);
        Hibernate.initialize(entity.getItensAtoLegal());
        Hibernate.initialize(entity.getListaItensAprovacaoAlienacao());
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<VOItemSolicitacaoAlienacao> pesquisarBensParaAprovacao(SolicitacaoAlienacao selecionado, AssistenteMovimentacaoBens assistente) {
        String sql = "" +
            " select " +
            "  item.id as idItem,  " +
            "  bem.id as idBem,  " +
            "  null as idLote,  " +
            "  null as idItemLeilao, " +
            "  est.detentoraorcamentaria_id as idUnidade,  " +
            "  est.detentoraadministrativa_id as idUnidadeAdm, " +
            "  est.grupobem_id as idGrupoBem, " +
            "  bem.identificacao as registro,  " +
            "  bem.registroAnterior as registroAnterior,  " +
            "  bem.descricao as descricao,  " +
            "  ev.dataoperacao as dataOperacao,  " +
            "  vworc.codigo || ' - ' || vworc.descricao as unidade,  " +
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
            "  0 as valorAvaliadao,    " +
            "  0 as valorArrematado,  " +
            "  bem.dataaquisicao  " +
            " from itemsolicitacaoalienacao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join estadobem est on est.id = ev.estadoresultante_id " +
            "   inner join bem on bem.id = ev.bem_id " +
            "   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = est.detentoraorcamentaria_id " +
            " where item.solicitacaoalienacao_id = :idSolicitacao " +
            "   and ev.dataoperacao between vworc.iniciovigencia and coalesce(vworc.fimvigencia, ev.dataoperacao) " +
            " order by bem.identificacao, bem.descricao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", selecionado.getId());
        List<VOItemSolicitacaoAlienacao> toReturn = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            assistente.zerarContadoresProcesso();
            assistente.setTotal(resultado.size());
            for (Object[] obj : resultado) {
                VOItemSolicitacaoAlienacao voItem = VOItemSolicitacaoAlienacao.criarObjetoVoItemSolicitacaoAlienacao(obj);
                voItem.setBem(bemFacade.recuperarSemDependencias(voItem.getIdBem()));
                toReturn.add(voItem);
                assistente.conta();
            }
        }
        solicitacaoAlienacaoFacade.verificarBensDisponiveisParaMovimentacao(assistente, toReturn);
        if (assistente.getMensagens().isEmpty()) {
            for (VOItemSolicitacaoAlienacao item : toReturn) {
                item.setFonteRecurso(solicitacaoAlienacaoFacade.recuperarFonteRecurso(item.getIdBem()));
            }
        }
        return toReturn;
    }

    public List<Number> buscarIdsItemAprovacao(SolicitacaoAlienacao entity) {
        String sql = "" +
            " select " +
            "   item.id " +
            " from itemaprovacaoalienacao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join aprovacaoalienacao aprov on aprov.id = item.aprovacaoalienacao_id " +
            "   inner join solicitacaoalienacao sol on sol.id = aprov.solicitacaoalienacao_id " +
            " where sol.id = :idSolicitacao " +
            "   and ev.tipoeventobem = '" + TipoEventoBem.ITEMAPROVACAOALIENACAO.name() + "' ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public void concluirItemAprovacao(Number idItemAprovacao, SituacaoEventoBem situacaoEventoBem) {
        String sql = " " +
            "   update eventobem ev set ev.situacaoeventobem = :situacao " +
            "   where ev.id = :idItemSol " +
            "   and ev.tipoeventobem = '" + TipoEventoBem.ITEMAPROVACAOALIENACAO.name() + "'";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSol", idItemAprovacao.longValue());
        q.setParameter("situacao", situacaoEventoBem.name());
        q.executeUpdate();
    }

    public int quantidadeItens(AprovacaoAlienacao entity) {
        Criteria criteria = getCriteria(entity);
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    private Criteria getCriteria(AprovacaoAlienacao entity) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ItemAprovacaoAlienacao.class);
        criteria.add(Restrictions.eq("aprovacaoAlienacao", entity));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public List<ItemAprovacaoAlienacao> recuperarItemAprocaoAlienacaoCriteria(int primeiroRegistro, int qtdeRegistro, AprovacaoAlienacao entity) {
        Criteria criteria = getCriteria(entity);
        criteria.setFirstResult(primeiroRegistro);
        criteria.setMaxResults(qtdeRegistro);
        return criteria.list();
    }

    public BigDecimal getValorOriginalEstadoResultante(AprovacaoAlienacao entity) {
        String sql = " select " +
            "  coalesce(sum(est.valororiginal), 0) as valorOriginal " +
            "from ItemAprovacaoAlienacao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "where item.aprovacaoAlienacao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorAjusteEstadoResultante(AprovacaoAlienacao entity) {
        String sql = " select " +
            "  coalesce(sum(est.valoracumuladodaamortizacao), 0) + " +
            "  coalesce(sum(est.valoracumuladodadepreciacao), 0) + " +
            "  coalesce(sum(est.valoracumuladodeajuste), 0) as valorAjuste " +
            "from ItemAprovacaoAlienacao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "where item.aprovacaoAlienacao_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public List<AprovacaoAlienacao> buscarAprovacaoDosBensEmAvaliacao() {
        String sql = " select distinct ap.* from itemaprovacaoalienacao itemaprov " +
            "  inner join aprovacaoalienacao ap on ap.id = itemaprov.aprovacaoalienacao_id " +
            "  inner join itemsolicitacaoalienacao itemsol on itemsol.id = itemaprov.itemsolicitacaoalienacao_id " +
            "  where itemsol.loteavaliacaoalienacao_id is null " +
            "  and ap.situacaoefetivacao = :aprovada";
        Query q = em.createNativeQuery(sql, AprovacaoAlienacao.class);
        q.setParameter("aprovada", SituacaoAlienacao.APROVADA.name());
        return q.getResultList();
    }


    public SolicitacaoAlienacaoFacade getSolicitacaoAlienacaoFacade() {
        return solicitacaoAlienacaoFacade;
    }

    public IntegradorPatrimonialContabilFacade getIntegradorPatrimonialContabilFacade() {
        return integradorPatrimonialContabilFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
