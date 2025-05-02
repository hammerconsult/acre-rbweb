/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Atributo;
import br.com.webpublico.entidades.ValorPossivel;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author Munif
 */
@Stateless
public class ValorPossivelFacade extends AbstractFacade<ValorPossivel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValorPossivelFacade() {
        super(ValorPossivel.class);
    }

    public ValorPossivel findByAtributoAndValor(Atributo atributo, String valor) {
        List resultList = em.createQuery(" select vp from ValorPossivel vp " +
                        " where vp.atributo.id = :atributo_id" +
                        "   and vp.valor = :valor ")
                .setParameter("atributo_id", atributo.getId())
                .setParameter("valor", valor)
                .setMaxResults(1)
                .getResultList();
        return !resultList.isEmpty() ? (ValorPossivel) resultList.get(0) : null;
    }
}
