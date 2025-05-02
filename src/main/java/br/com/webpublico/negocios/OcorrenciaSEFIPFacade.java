/*
 * Codigo gerado automaticamente em Tue Jan 03 15:23:30 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.OcorrenciaSEFIP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class OcorrenciaSEFIPFacade extends AbstractFacade<OcorrenciaSEFIP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcorrenciaSEFIPFacade() {
        super(OcorrenciaSEFIP.class);
    }

    public List<OcorrenciaSEFIP> listaFiltrandoLong(String s, String... atributos) {
        Long cod;
        try {
            cod = Long.parseLong(s);
        } catch (Exception e) {
            cod = 0l;
        }

        String hql = "from OcorrenciaSEFIP ocorrencia where "
                + " ocorrencia.codigo = :codigo or ";
        for (String atributo : atributos) {
            hql += "lower(ocorrencia." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", cod);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public OcorrenciaSEFIP recuperarOcorrenciaSEFIPPorCodigo(Long codigo){
        String hql = "select oc from OcorrenciaSEFIP oc where oc.codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("codigo", codigo);
        try{
            return (OcorrenciaSEFIP) q.getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }
}
