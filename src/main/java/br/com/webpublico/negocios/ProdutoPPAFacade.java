/*
 * Codigo gerado automaticamente em Tue Oct 25 09:10:49 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProdutoPPAFacade extends AbstractFacade<ProdutoPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProdutoPPAFacade() {
        super(ProdutoPPA.class);
    }

    public List<ProdutoPPA> recuperarProdutosPPA(AcaoPrincipal ac) {
        List<ProdutoPPA> listaSubAcao = new ArrayList<ProdutoPPA>();
        String hql = " from ProdutoPPA s where s.acaoPrincipal = :acao ";
        if (ac != null) {
            Query q = em.createQuery(hql);
            q.setParameter("acao", ac);
            listaSubAcao = q.getResultList();
        }
        return listaSubAcao;
    }

    public List<ProdutoPPA> listaFiltrandoPorAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        Query consulta = em.createQuery("select p from ProdutoPPA p where p.acaoPrincipal = :acao", ProdutoPPA.class);
        consulta.setParameter("acao", acaoPrincipal);
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ProdutoPPA> buscarProdutosPPAPorAcoesPrincipais(String parte, AcaoPrincipal acaoPrincipal){
        String sql = " select produto.* from PRODUTOPPA produto where produto.ACAOPRINCIPAL_ID = :idAcao AND (lower(produto.DESCRICAO) LIKE :parte or produto.CODIGO LIKE :parte) order by produto.CODIGO ";
        Query q = em.createNativeQuery(sql, ProdutoPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idAcao", acaoPrincipal.getId());
        try{
            return q.getResultList();
        } catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public List<ProdutoPPA> listaFiltrandoPorAcaoPrincipalPriozadaLDO(AcaoPrincipal acaoPrincipal, String parte, Exercicio exercicio) {
        String sql = " SELECT DISTINCT PRO.*" +
                " FROM PRODUTOPPA PRO " +
                " INNER JOIN provisaoppaldo PROV ON PRO.ID = prov.produtoppa_id " +
                " INNER JOIN LDO ON prov.ldo_id = LDO.id "+
                " WHERE ((PRO.CODIGO LIKE :parte) OR (LOWER(PRO.descricao) LIKE :parte)) " +
                " AND pro.acaoprincipal_id = :acaoPrincipal" +
                " AND LDO.EXERCICIO_ID = :exercicio" +
                " AND PRO.SOMENTELEITURA = 0 " +
                " ORDER BY PRO.CODIGO";
        Query q = em.createNativeQuery(sql, ProdutoPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("acaoPrincipal", acaoPrincipal.getId());
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    @Override
    public ProdutoPPA recuperar(Object id) {
        ProdutoPPA produtoPPA = getEntityManager().find(super.getClasse(), id);
        produtoPPA.getProvisoes().size();
        return produtoPPA;
    }

    public List<ProdutoPPA> listaProdutoPPAPorExercicioEPPA(Exercicio e, PPA ppa) {
        String hql = "select s from ProdutoPPA s "
            + "inner join fetch s.provisoes provisoes "
                + "inner join s.acaoPrincipal ac "
                + "inner join ac.programa p "
                + "inner join p.ppa z "
                + "where z = :ppa " +
                "  and p.exercicio = :e "
                + "order by p.id";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("e", e);
        q.setParameter("ppa", ppa);

        return q.getResultList();
    }

    public ProdutoPPA getProdutoPPANovoDaOrigem(ProdutoPPA produtoPPA) {
        Query consulta = em.createNativeQuery("select p.* from produtoppa p where p.origem_id = :id", ProdutoPPA.class);
        consulta.setParameter("id", produtoPPA.getId());
        consulta.setMaxResults(1);
        try {
            return (ProdutoPPA) consulta.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
