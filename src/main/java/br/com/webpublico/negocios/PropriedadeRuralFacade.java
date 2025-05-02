/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

/**
 *
 * @author claudio
 */

import br.com.webpublico.entidades.CadastroRural;
import br.com.webpublico.entidades.PropriedadeRural;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PropriedadeRuralFacade extends AbstractFacade<PropriedadeRural> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PropriedadeRuralFacade() {
        super(PropriedadeRural.class);
    }

    public List<PropriedadeRural> listaPropriedadesPorImovel(List<CadastroRural> imoveis) {
        if (imoveis == null || imoveis.isEmpty()) {
            return null;
        }
        Query q = em.createQuery("from PropriedadeRural p where p.imovel in (:imoveis)");
        q.setParameter("imoveis", imoveis);
        return q.getResultList();

    }

}
