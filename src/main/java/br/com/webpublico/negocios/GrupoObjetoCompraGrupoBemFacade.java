package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ROBSONLUIS-MGA
 * Date: 27/11/13
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class GrupoObjetoCompraGrupoBemFacade extends AbstractFacade<GrupoObjetoCompraGrupoBem> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;

    public GrupoObjetoCompraGrupoBemFacade() {
        super(GrupoObjetoCompraGrupoBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvar(List<GrupoObjetoCompraGrupoBem> gruposAssociados) {
        for (GrupoObjetoCompraGrupoBem g : gruposAssociados) {
            super.salvar(g);
        }
    }

    public GrupoObjetoCompraGrupoBem recuperarAssociacaoDoGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra, Date dataOperacao) {
        String sql = "" +
            " select " +
            "   associacao.* " +
            " from grupoobjcompragrupobem associacao " +
            "  inner join grupoobjetocompra grupo on grupo.id = associacao.grupoobjetocompra_id " +
            " where grupo.codigo = :codigogrupoobjetocompra " +
            "  and to_date(:dataoperacao, 'dd/MM/yyyy') between trunc(associacao.iniciovigencia) and coalesce(trunc(associacao.fimvigencia), to_date(:dataoperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, GrupoObjetoCompraGrupoBem.class);
        q.setParameter("codigogrupoobjetocompra", grupoObjetoCompra.getCodigo().trim());
        q.setParameter("dataoperacao", DataUtil.getDataFormatada(dataOperacao));
        List result = q.getResultList();
        if (result == null || result.isEmpty()) {
            return null;
        }
        return (GrupoObjetoCompraGrupoBem) result.get(0);
    }

    public Boolean jaExisteEstaAssociacao(GrupoObjetoCompraGrupoBem grupo) {
        String sql = " select grupo.*" +
            " from GRUPOOBJCOMPRAGRUPOBEM grupo " +
            " where  grupo.GRUPOBEM_ID = :ID_GRUPOBEM " +
            "        AND GRUPO.GRUPOOBJETOCOMPRA_ID = :ID_GRUPOOBJETOCOMPRA" +
            "        and sysdate between grupo.iniciovigencia and coalesce(grupo.fimvigencia,sysdate)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ID_GRUPOBEM", grupo.getGrupoBem().getId());
        q.setParameter("ID_GRUPOOBJETOCOMPRA", grupo.getGrupoObjetoCompra().getId());
        return !q.getResultList().isEmpty();
    }

    public Boolean grupoBemJaEstaEmUso(GrupoBem grupoBem) {
        String sql = "SELECT B.* " +
            "       FROM BEM B" +
            " INNER JOIN EVENTOBEM EVENTO ON EVENTO.ID = (SELECT MAX(ULTIMO.ID) FROM EVENTOBEM ULTIMO WHERE ULTIMO.BEM_ID = B.ID)" +
            " INNER JOIN ESTADOBEM ESTADO ON ESTADO.ID = EVENTO.ESTADORESULTANTE_ID" +
            "      WHERE ESTADO.GRUPOBEM_ID = :ID_GRUPOBEM " +
            "        AND EVENTO.SITUACAOEVENTOBEM <> :SITUACAO";

        Query q = em.createNativeQuery(sql, Bem.class);

        q.setParameter("ID_GRUPOBEM", grupoBem.getId());
        q.setParameter("SITUACAO", "'" + SituacaoEventoBem.BAIXADO.name() + "'");

        q.setMaxResults(1);

        return !q.getResultList().isEmpty();
    }

    public List<GrupoObjetoCompraGrupoBem> recuperarGrupoObjetoCompraGrupoBem(GrupoBem grupoBem, GrupoObjetoCompra grupoObjetoCompra) {
        String hql = "select grupo from GrupoObjetoCompraGrupoBem grupo " +
            "         where grupo.grupoBem = :grupoBem and grupo.grupoObjetoCompra = :grupoObjetoCompra";
        Query q = em.createQuery(hql);
        q.setParameter("grupoBem", grupoBem);
        q.setParameter("grupoObjetoCompra", grupoObjetoCompra);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public GrupoObjetoCompraGrupoBem buscarAssociacaoPorGrupoObjetoCompraVigente(GrupoObjetoCompra grupoObjetoCompra, Date dataOperacao) {
        try {
            String sql = "" +
                " select associacao.* from grupoobjcompragrupobem associacao " +
                "  inner join grupoobjetocompra grupo on grupo.id = associacao.grupoobjetocompra_id " +
                " where grupo.id = :idGoc " +
                "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(associacao.iniciovigencia) and coalesce(trunc(associacao.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ";
            Query q = em.createNativeQuery(sql, GrupoObjetoCompraGrupoBem.class);
            q.setParameter("idGoc", grupoObjetoCompra.getId());
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
            return (GrupoObjetoCompraGrupoBem) q.getSingleResult();
        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Nenhuma associação de grupo patrimonial encontrada para grupo de objeto de compra: " + grupoObjetoCompra.getCodigo());
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma Associação Grupo de Objeto de Compra e Grupo Patrimonial vigente para o grupo " + grupoObjetoCompra.getCodigo() + ", por favor, efetue a correção das associações e tente novamente.");
        }
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }
}
