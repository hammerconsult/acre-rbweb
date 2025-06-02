package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoRequisicaoCompra;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Stateless
public class RequisicaoCompraEstornoFacade extends AbstractFacade<RequisicaoCompraEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public RequisicaoCompraEstornoFacade() {
        super(RequisicaoCompraEstorno.class);
    }

    @Override
    public RequisicaoCompraEstorno recuperar(Object id) {
        RequisicaoCompraEstorno rce = em.find(RequisicaoCompraEstorno.class, id);
        Hibernate.initialize(rce.getItens());
        return super.recuperar(id);
    }

    public RequisicaoCompraEstorno salvarEstorno(RequisicaoCompraEstorno entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(RequisicaoCompraEstorno.class, "numero"));
        }
        movimentarSituacaoRequisicaoCompra(entity);
        return super.salvarRetornando(entity);
    }

    private void movimentarSituacaoRequisicaoCompra(RequisicaoCompraEstorno entity) {
        BigDecimal quantidadeTotalRequisicao = requisicaoDeCompraFacade.buscarQuantidadeTotal(entity.getRequisicaoDeCompra());
        BigDecimal quantidadeTotalEstornada = buscarQuantidadeTotal(entity.getRequisicaoDeCompra());
        for (ItemRequisicaoCompraEstorno itemEstorno : entity.getItens()) {
            quantidadeTotalEstornada = quantidadeTotalEstornada.add(itemEstorno.getQuantidade());
        }
        boolean hasQuantidadeRestante = quantidadeTotalRequisicao.compareTo(quantidadeTotalEstornada) != 0;
        SituacaoRequisicaoCompra situacao = !hasQuantidadeRestante ? SituacaoRequisicaoCompra.ESTORNADA : SituacaoRequisicaoCompra.ESTORNADA_PARCIAL;
        requisicaoDeCompraFacade.movimentarSituacaoRequisicaoCompra(entity.getRequisicaoDeCompra(), situacao);
    }

    public BigDecimal buscarQuantidadeEstornadaRequisicao(ItemRequisicaoCompraExecucao itemReqExec) {
        String sql = " select coalesce(sum(item.quantidade), 0) as quantidade " +
            "           from itemrequisicaocompraest item " +
            "           where item.itemrequisicaocompraexec_id = :idItemReqExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemReqExecucao", itemReqExec.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public BigDecimal buscarQuantidadeTotal(RequisicaoDeCompra requisicaoCompra) {
        String sql = "  select coalesce(sum(item.quantidade), 0) as quantidade " +
            "           from itemrequisicaocompraest item " +
            "           inner join requisicaocompraestorno rce on rce.id = item.requisicaocompraestorno_id" +
            "           where rce.requisicaodecompra_id = :idRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", requisicaoCompra.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (BigDecimal) resultList.get(0);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorEstornadoRequisicaoContrato(ExecucaoContratoItem item) {
        String sql =
            " select coalesce(sum(est.quantidade * irce.valorunitario), 0) as valortotal " +
                " from itemrequisicaocompraest est " +
                "   inner join itemrequisicaocompraexec irce on irce.id = est.itemRequisicaoCompraExec_id     " +
                " where irce.execucaocontratoitem_id = :idItemExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemExecucao", item.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public List<RequisicaoCompraEstorno> buscarPorRequisicaoCompra(RequisicaoDeCompra requisicaoDeCompra) {
        String sql = " select est.* from requisicaocompraestorno est where est.requisicaodecompra_id = :idRequisicao ";
        Query q = em.createNativeQuery(sql, RequisicaoCompraEstorno.class);
        q.setParameter("idRequisicao", requisicaoDeCompra.getId());
        try {
            List<RequisicaoCompraEstorno> resultado = q.getResultList();
            for (RequisicaoCompraEstorno requisicaoCompraEstorno : resultado) {
                Hibernate.initialize(requisicaoCompraEstorno.getItens());
                Collections.sort(requisicaoCompraEstorno.getItens());
            }
            return resultado;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
