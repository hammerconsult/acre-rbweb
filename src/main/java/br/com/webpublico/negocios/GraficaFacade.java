/*
 * Codigo gerado automaticamente em Tue Feb 22 10:48:40 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Grafica;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class GraficaFacade extends AbstractFacade<Grafica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GraficaFacade() {
        super(Grafica.class);
    }

    public List<Grafica> listaPorPessoa(String parte){
        Query q;
        q = getEntityManager().createQuery("from Grafica g " +
                "  where lower(g.responsavel.nomeFantasia) " +
                "  like :parte " +
                "  or lower(g.codigo) " +
                "  like :parte"  +
                "  or lower(replace(replace(replace(g.responsavel.cnpj, '/', ''), '-', ''),'.','')) " +
                "  like :parte") ;
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Grafica> listaPorCodigoECMC(String parte) {
        String hql = "select  g from Grafica g, CadastroEconomico c where c.pessoa = g.responsavel"
                + " and (g.codigo like :codigo"
                + " or c.inscricaoCadastral like :inscricao"
                + " or lower(g.responsavel.razaoSocial) like :nome)";
        Query q = em.createQuery(hql, Grafica.class);
        q.setParameter("codigo", "%" + parte.toLowerCase() + "%");
        q.setParameter("inscricao", "%" + parte.toLowerCase() + "%");
        q.setParameter("nome", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

}
