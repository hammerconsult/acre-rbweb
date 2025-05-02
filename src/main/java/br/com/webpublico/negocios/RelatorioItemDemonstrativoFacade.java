/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.RelatorioGrupoItemDemons;
import br.com.webpublico.entidades.RelatorioItemDemonstrativo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author juggernaut
 */
@Stateless
public class RelatorioItemDemonstrativoFacade extends AbstractFacade<RelatorioItemDemonstrativo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RelatorioItemDemonstrativoFacade() {
        super(RelatorioItemDemonstrativo.class);
    }

    public List<RelatorioGrupoItemDemons> listaRelatorioGrupoItensPorItem(RelatorioItemDemonstrativo rel) {
        String hql = "select rel from RelatorioGrupoItemDemons rel left join rel.relatorioItemDemonstrativo it where it=:param";

        Query q = em.createQuery(hql);
        q.setParameter("param", rel);

        return q.getResultList();
    }

    public List<RelatorioItemDemonstrativo> listaExercicio(Exercicio exercicioCorrente, String filtro) {
        String hql = "from RelatorioItemDemonstrativo obj where obj.exercicio = :exerc ";
        //System.out.println("Hql: " + hql);
        if (!filtro.equals("")) {
            hql += " and obj.descricao like \'%" + filtro.toLowerCase() + "%\'";
        }
        hql += " order by obj.id";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("exerc", exercicioCorrente);
        return q.getResultList();
    }
}
