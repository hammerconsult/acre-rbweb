/*
 * Codigo gerado automaticamente em Mon Jun 13 17:10:09 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.LancamentoPadronizado;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class LancamentoPadronizadoFacade extends AbstractFacade<LancamentoPadronizado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoPadronizadoFacade() {
        super(LancamentoPadronizado.class);
    }

    public List<Conta> listaConta(Exercicio e) {
        List<Conta> lista = new ArrayList<Conta>();
        String hql = "select co from PlanoDeContas pc, Conta co where co.planoDeContas = pc.id ";
        hql += " and pc.id in (select pe.planoContabil from PlanoDeContasExercicio pe ";
        hql += " where pe.exercicio =:parametro) ";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", e);
        lista = q.getResultList();
        return lista;
    }

    public List<Conta> listaFiltrando(String s, Exercicio e, String... atributos) {

        String hql = "select distinct co from PlanoDeContas pc, Conta co where co.planoDeContas = pc.id ";
        hql += " and pc.id in (select pe.planoContabil from PlanoDeContasExercicio pe ";
        hql += " where pe.exercicio =:parametro) and ";
        for (String atributo : atributos) {
            hql += "lower(co." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametro", e);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }
}
