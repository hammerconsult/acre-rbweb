/*
 * Codigo gerado automaticamente em Tue Jan 17 14:07:32 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class EntidadeConsignatariaFacade extends AbstractFacade<EntidadeConsignataria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadeConsignatariaFacade() {
        super(EntidadeConsignataria.class);
    }

    @Override
    public EntidadeConsignataria recuperar(Object id) {
        EntidadeConsignataria e = em.find(EntidadeConsignataria.class, id);
        e.getItemEntidadeConsignatarias().size();
        return e;
    }

    public boolean existeConsignatario(EntidadeConsignataria entidadeConsignataria) {
        String hql = " from EntidadeConsignataria e "
                + " where e.pessoaJuridica = :parametro ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametro", entidadeConsignataria.getPessoaJuridica());

        List<EntidadeConsignataria> lista = new ArrayList<EntidadeConsignataria>();
        lista = q.getResultList();

        if (lista.contains(entidadeConsignataria)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public boolean existeCodigo(EntidadeConsignataria entidade) {
        String hql = " from EntidadeConsignataria ec where ec.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", entidade.getCodigo());

        List<EntidadeConsignataria> lista = new ArrayList<EntidadeConsignataria>();
        lista = q.getResultList();
        if (lista.contains(entidade)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public EntidadeConsignataria recuperaEntidadeConsignatariaPorEvento(EventoFP evento) {
        Query createQuery = em.createQuery("from EntidadeConsignataria e inner join e.itemEntidadeConsignatarias item where item.eventoFP = :evento ");
        createQuery.setMaxResults(1);
        createQuery.setParameter("evento", evento);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return (EntidadeConsignataria) createQuery.getSingleResult();
    }

    public EntidadeConsignataria recuperaEntidadeConsignatariaPorEvento(EntidadeConsignataria ec, EventoFP evento) {
        Query createQuery = em.createQuery("select e from EntidadeConsignataria e inner join e.itemEntidadeConsignatarias item where item.eventoFP = :evento and e = :ec");
        createQuery.setMaxResults(1);
        createQuery.setParameter("evento", evento);
        createQuery.setParameter("ec", ec);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return (EntidadeConsignataria) createQuery.getSingleResult();
    }

    public EntidadeConsignataria recuperaEntidadeConsignatariaPorCodigo(String codigo) {
        Query createQuery = em.createQuery("from EntidadeConsignataria e where e.codigo = :codigo ");
        createQuery.setMaxResults(1);
        createQuery.setParameter("codigo", Integer.parseInt(codigo));
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return (EntidadeConsignataria) createQuery.getSingleResult();
    }

    //Muito lento
    @Deprecated
    public boolean recuperarVinculosPorUnidade(HierarquiaOrganizacional unidade, VinculoFP c) {
        List<LotacaoFuncional> lotacaoFuncional = new ArrayList<LotacaoFuncional>();
        Query q = em.createQuery(" from ContratoFP contrato inner join contrato.lotacaoFuncionals lotacao where "
                + " lotacao.unidadeOrganizacional = :unidade and contrato.id = :contrato ");
        q.setParameter("unidade", unidade.getSubordinada());
        q.setParameter("contrato", c.getId());
        lotacaoFuncional.addAll(q.getResultList());
        for (HierarquiaOrganizacional u : hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipoAdm(unidade)) {
            Query query = em.createQuery(" from ContratoFP contrato inner join contrato.lotacaoFuncionals lotacao where contrato.id = :contrato "
                    + " and lotacao.unidadeOrganizacional = :unidade ");
            query.setParameter("unidade", u.getSubordinada());
            query.setParameter("contrato", c.getId());
            lotacaoFuncional.addAll(query.getResultList());
        }
        return lotacaoFuncional.isEmpty();
    }

    public boolean recuperarVinculosPorUnidadeRecursive(HierarquiaOrganizacional unidade, VinculoFP c) {
        StringBuilder sb = new StringBuilder();
        sb.append(" with dados(ID,CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA,HIERARQUIAORC_ID,INICIOVIGENCIA,FIMVIGENCIA) as( ");
        sb.append(" select ");
        sb.append(" ho.ID,ho.CODIGO,ho.NIVELNAENTIDADE,ho.TIPOHIERARQUIAORGANIZACIONAL,ho.SUBORDINADA_ID ");
        sb.append(" ,ho.SUPERIOR_ID,ho.VALORESTIMADO,ho.INDICEDONO,ho.CODIGONO,ho.EXERCICIO_ID, HO.TIPOHIERARQUIA, HO.HIERARQUIAORC_ID, HO.INICIOVIGENCIA, HO.FIMVIGENCIA ");
        sb.append(" from ");
        sb.append(" unidadeorganizacional und ");
        sb.append(" inner join hierarquiaorganizacional ho ");
        sb.append(" on ho.subordinada_id = und.id ");
        sb.append(" inner join lotacaofuncional l on l.unidadeorganizacional_id = und.id ");
        sb.append(" where ho.exercicio_id= :exerc and l.vinculofp_id = :contratoid ");
        sb.append(" union all ");
        sb.append(" select ");
        sb.append(" horec.ID,horec.CODIGO,horec.NIVELNAENTIDADE,horec.TIPOHIERARQUIAORGANIZACIONAL,horec.SUBORDINADA_ID,horec.SUPERIOR_ID,horec.VALORESTIMADO,horec.INDICEDONO,horec.CODIGONO,horec.EXERCICIO_ID, horec.TIPOHIERARQUIA, horec.HIERARQUIAORC_ID, horec.INICIOVIGENCIA, horec.FIMVIGENCIA ");
        sb.append(" from ");
        sb.append(" hierarquiaorganizacional horec ");
        sb.append(" inner join dados d ");
        sb.append(" on horec.subordinada_id = d.superior_id ");
        sb.append(" inner join lotacaofuncional l on l.unidadeorganizacional_id = horec.subordinada_id");
        sb.append(" where horec.exercicio_id= :exerc and l.vinculofp_id = :contratoid and horec.id = :str");
        sb.append(" )select");
        sb.append(" DISTINCT(id),CODIGO,NIVELNAENTIDADE,TIPOHIERARQUIAORGANIZACIONAL,SUBORDINADA_ID,SUPERIOR_ID,VALORESTIMADO,INDICEDONO,CODIGONO,EXERCICIO_ID,TIPOHIERARQUIA, HIERARQUIAORC_ID, INICIOVIGENCIA, FIMVIGENCIA");
        sb.append(" from");
        sb.append(" dados where tipohierarquiaorganizacional= 'ADMINISTRATIVA' order by codigo, nivelnaentidade");
        Query q = getEntityManager().createNativeQuery(sb.toString(), HierarquiaOrganizacional.class);
        q.setParameter("str", unidade.getId());
        q.setParameter("exerc", unidade.getExercicio().getId());
        q.setParameter("contratoid", c.getId());
        return q.getResultList().isEmpty();
    }


}
