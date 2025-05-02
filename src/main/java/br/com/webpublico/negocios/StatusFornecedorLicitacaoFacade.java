package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemStatusFornecedorLicitacaoVo;
import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.enums.TipoSituacaoFornecedorLicitacao;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 21/02/14
 * Time: 10:46
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class StatusFornecedorLicitacaoFacade extends AbstractFacade<StatusFornecedorLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFacade contratoFacade;

    public StatusFornecedorLicitacaoFacade() {
        super(StatusFornecedorLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public StatusFornecedorLicitacao recuperar(Object id) {
        StatusFornecedorLicitacao entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        return entity;
    }

    public Long recuperaUltimoNumero(Licitacao licitacaoSelecionada) {
        String hql = " select coalesce(max(status.numero), 0) from StatusFornecedorLicitacao status "
            + "inner join status.licitacaoFornecedor fornecedor "
            + "inner join fornecedor.licitacao lic "
            + "     where lic = :licitacao ";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacaoSelecionada);
        q.setMaxResults(1);
        Long numero = (Long) q.getResultList().get(0);
        if (numero != null) {
            return numero + 1;
        } else {
            return Long.valueOf(1);
        }
    }

    public StatusFornecedorLicitacao salvarRetornando(StatusFornecedorLicitacao entity, List<ItemStatusFornecedorLicitacaoVo> itens) {
        SituacaoItemProcessoDeCompra situacaoIpc = SituacaoItemProcessoDeCompra.getSituacaoPorStatusFornecedor(entity.getTipoSituacao());
        for (ItemStatusFornecedorLicitacaoVo item : itens) {
            if (item.getSelecionado()) {
                ItemProcessoDeCompra itemProcessoCompra = item.getItemProcessoCompra();

                ItemStatusFornecedorLicitacao novoItem = new ItemStatusFornecedorLicitacao();
                novoItem.setStatusFornecedorLicitacao(entity);
                novoItem.setSituacao(situacaoIpc);
                novoItem.setItemProcessoCompra(itemProcessoCompra);
                novoItem.setValorUnitario(item.getValorUnitario());
                entity.getItens().add(novoItem);

                itemProcessoCompra.setSituacaoItemProcessoDeCompra(situacaoIpc);
                em.merge(itemProcessoCompra);
            }
        }
        entity = em.merge(entity);
        if (entity.isHomologada()) {
            licitacaoFacade.salvarPortal(entity.getLicitacaoFornecedor().getLicitacao());
        }
        return entity;
    }

    @Override
    public void remover(StatusFornecedorLicitacao entity) {
        if (entity.isHomologada()) {
            alterarStatusItemProcessoCompra(entity, SituacaoItemProcessoDeCompra.ADJUDICADO);
        }
        if (entity.isAdjudicada()) {
            alterarStatusItemProcessoCompra(entity, SituacaoItemProcessoDeCompra.AGUARDANDO);
        }
        super.remover(entity);
    }


    private void alterarStatusItemProcessoCompra(StatusFornecedorLicitacao entity, SituacaoItemProcessoDeCompra situacaoDestino) {
        for (ItemStatusFornecedorLicitacao item : entity.getItens()) {
            item.getItemProcessoCompra().setSituacaoItemProcessoDeCompra(situacaoDestino);
            em.merge(item.getItemProcessoCompra());
        }
    }

    public List<ItemPropostaFornecedor> buscarItensVencidosFornecedorPorStatus(Licitacao licitacao, Pessoa fornecedor, List<TipoClassificacaoFornecedor> status, SituacaoItemProcessoDeCompra situacao) {
        if (licitacao.isPregao() || licitacao.isRDCPregao()) {
            return buscarItensVencidosFornecedorPorStatusNoPregao(licitacao, fornecedor, status, situacao);
        }
        List<ItemPropostaFornecedor> retorno = Lists.newArrayList();
        retorno = licitacaoFacade.getItensVencidosPeloFornecedorPorStatusNoCertame(licitacao, fornecedor, status, situacao); // MAPA COMPARATIVO NORMAL
        if (retorno != null && !retorno.isEmpty()) {
            return retorno;
        }
        retorno = licitacaoFacade.getItensVencidosPeloFornecedorPorStatusNoMapaComparativoTecnicaPreco(licitacao, fornecedor, status, situacao); // MAPA COMPARATIVO TECNICA PREÇO
        return retorno;
    }

    public List<ItemStatusFornecedorLicitacao> buscarItensPorStatus(Licitacao licitacao, Pessoa fornecedor, SituacaoItemProcessoDeCompra situacao) {
        String sql = " select * from itemstatusfornecedorlicit item " +
            "           inner join itemprocessodecompra ipc on ipc.id = item.itemprocessocompra_id " +
            "           inner join statusfornecedorlicitacao sf on sf.id = item.statusfornecedorlicitacao_id " +
            "           inner join licitacaofornecedor lf on lf.id = sf.licitacaofornecedor_id " +
            "         where lf.licitacao_id = :idLicitacao " +
            "           and lf.empresa_id = :idFornecedor  " +
            "           and item.situacao = :situacaoIpc  " +
            "           and ipc.situacaoitemprocessodecompra = :situacaoIpc ";
        Query q = em.createNativeQuery(sql, ItemStatusFornecedorLicitacao.class);
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("idFornecedor", fornecedor.getId());
        q.setParameter("situacaoIpc", situacao.name());
        return q.getResultList();
    }

    public List<ItemStatusFornecedorLicitacao> buscarItens(StatusFornecedorLicitacao status) {
        String sql = " select * from itemstatusfornecedorlicit item where item.statusfornecedorlicitacao_id = :idStatus ";
        Query q = em.createNativeQuery(sql, ItemStatusFornecedorLicitacao.class);
        q.setParameter("idStatus", status.getId());
        return q.getResultList();
    }

    public List<StatusFornecedorLicitacao> buscarPorLicitacao(Licitacao licitacao) {
        return em.createQuery("from StatusFornecedorLicitacao status where status.licitacaoFornecedor.licitacao = :licitacao").setParameter("licitacao", licitacao).getResultList();
    }

    public Boolean isStatusLicitacaoAdjudicado(Licitacao lic) {
        String sql = " select 1 from STATUSFORNECEDORLICITACAO status " +
            "inner join LICITACAOFORNECEDOR licfor on status.LICITACAOFORNECEDOR_ID = licfor.ID " +
            "inner join LICITACAO lic on licfor.LICITACAO_ID = lic.ID " +
            "where lic.id = :licitacaoId and status.TIPOSITUACAO = :adjudicado ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("licitacaoId", lic.getId());
        q.setParameter("adjudicado", TipoSituacaoFornecedorLicitacao.ADJUDICADA.name());
        return !q.getResultList().isEmpty();
    }

    public Boolean isStatusLicitacaoHomologada(Licitacao lic) {
        try {
            String sql = " select 1 from STATUSFORNECEDORLICITACAO status " +
                "inner join LICITACAOFORNECEDOR licfor on status.LICITACAOFORNECEDOR_ID = licfor.ID " +
                "inner join LICITACAO lic on licfor.LICITACAO_ID = lic.ID " +
                "where lic.id = :licitacaoId and status.TIPOSITUACAO = :adjudicado ";

            Query q = em.createNativeQuery(sql);
            q.setParameter("licitacaoId", lic.getId());
            q.setParameter("adjudicado", TipoSituacaoFornecedorLicitacao.HOMOLOGADA.name());

            return !q.getResultList().isEmpty();
        } catch (Exception e) {
            logger.error("Erro ao verificar licitação homologada ", e);
            return Boolean.FALSE;
        }
    }

    public List<StatusFornecedorLicitacao> buscarPorFornecedorAndStatus(LicitacaoFornecedor licitacaoFornecedor, TipoSituacaoFornecedorLicitacao status) {
        String sql = " select status.* from STATUSFORNECEDORLICITACAO status " +
            "           inner join LICITACAOFORNECEDOR licfor on status.LICITACAOFORNECEDOR_ID = licfor.ID " +
            "           inner join LICITACAO lic on licfor.LICITACAO_ID = lic.ID " +
            "         where lic.id = :idLicitacao " +
            "           and licfor.id = :idFornedcedor " +
            "           and status.TIPOSITUACAO = :status ";
        Query q = em.createNativeQuery(sql, StatusFornecedorLicitacao.class);
        q.setParameter("idLicitacao", licitacaoFornecedor.getLicitacao().getId());
        q.setParameter("idFornedcedor", licitacaoFornecedor.getId());
        q.setParameter("status", status.name());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<StatusFornecedorLicitacao> buscarPorFornecedor(LicitacaoFornecedor fornecedor) {
        String sql = " select status.* from STATUSFORNECEDORLICITACAO status " +
            "           inner join LICITACAOFORNECEDOR licfor on status.LICITACAOFORNECEDOR_ID = licfor.ID " +
            "           inner join LICITACAO lic on licfor.LICITACAO_ID = lic.ID " +
            "         where lic.id = :idLicitacao " +
            "           and licfor.id = :idFornedcedor ";
        Query q = em.createNativeQuery(sql, StatusFornecedorLicitacao.class);
        q.setParameter("idLicitacao", fornecedor.getLicitacao().getId());
        q.setParameter("idFornedcedor", fornecedor.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<ItemPropostaFornecedor> buscarItensVencidosFornecedorPorStatusNoPregao(Licitacao l, Pessoa p, List<TipoClassificacaoFornecedor> status, SituacaoItemProcessoDeCompra situacaoIpc) {
        String sql = "" +
            " select ipf.* from itempregao ip" +
            "   inner join pregao p on p.id = ip.pregao_id" +
            "   inner join licitacao lic on lic.id = p.licitacao_id" +
            "   left join itpreitpro ipip on ipip.itempregao_id = ip.id" +
            "   left join itprelotpro iplp on iplp.itempregao_id = ip.id" +
            "   left join itempregaoloteitemprocesso itemlote on itemlote.itempregaoloteprocesso_id = iplp.id" +
            "   inner join itemprocessodecompra ipc on ipc.id = coalesce(ipip.itemprocessodecompra_id, itemlote.itemprocessodecompra_id)" +
            "   inner join itempropfornec ipf on ipf.itemprocessodecompra_id = ipc.id" +
            "   inner join propostafornecedor pf on pf.id = ipf.propostafornecedor_id" +
            "   inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id" +
            "   inner join lancepregao lanc on lanc.id = iplv.lancepregao_id" +
            " where p.licitacao_id = :idLicitacao" +
            "  and pf.id = lanc.propostafornecedor_id " +
            "  and pf.fornecedor_id = :idFornecedor " +
            "  and ipc.situacaoitemprocessodecompra = :situacaoIpc " +
            "  and ip.statusfornecedorvencedor in (:statusFornecedor) " +
            " union all" +
            " select ipf.*from proximovencedorlicitem item" +
            "  inner join proximovencedorlicitacao pv on pv.id = item.proximovencedorlicitacao_id" +
            "  inner join itempregao ip on ip.id = item.itempregao_id" +
            "  left join itpreitpro ipip on ipip.itempregao_id = ip.id" +
            "  left join itprelotpro iplp on iplp.itempregao_id = ip.id" +
            "  left join itempregaoloteitemprocesso itemlote on itemlote.itempregaoloteprocesso_id = iplp.id" +
            "  inner join itemprocessodecompra ipc on ipc.id = coalesce(ipip.itemprocessodecompra_id, itemlote.itemprocessodecompra_id)" +
            "  inner join itempropfornec ipf on ipf.itemprocessodecompra_id = ipc.id" +
            "  inner join propostafornecedor pf on pf.id = ipf.propostafornecedor_id" +
            "  inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id" +
            "  inner join lancepregao lanc on lanc.id = iplv.lancepregao_id " +
            " where pv.licitacao_id = :idLicitacao " +
            "  and pf.id = lanc.propostafornecedor_id " +
            "  and pf.fornecedor_id = :idFornecedor " +
            "  and ip.statusfornecedorvencedor in (:statusFornecedor) ";
        Query q = em.createNativeQuery(sql, ItemPropostaFornecedor.class);
        q.setParameter("idLicitacao", l.getId());
        q.setParameter("idFornecedor", p.getId());
        q.setParameter("statusFornecedor", TipoClassificacaoFornecedor.listStringStatus(status));
        q.setParameter("situacaoIpc", situacaoIpc.name());
        return q.getResultList();
    }

    public List<StatusFornecedorLicitacao> buscarPorNumero(String part) {
        String sql = " select status.* from STATUSFORNECEDORLICITACAO status " +
            "           inner join LICITACAOFORNECEDOR licfor on status.LICITACAOFORNECEDOR_ID = licfor.ID " +
            "           inner join LICITACAO lic on licfor.LICITACAO_ID = lic.ID " +
            "          where (status.numero like :parte or lic.numerolicitacao like :parte)";
        Query q = em.createNativeQuery(sql, StatusFornecedorLicitacao.class);
        q.setParameter("parte", "%" + part.trim() + "%");
        return q.getResultList();
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public ItemPropostaFornecedorFacade getItemPropostaFornecedorFacade() {
        return itemPropostaFornecedorFacade;
    }
}
