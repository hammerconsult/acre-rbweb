/*
 * Codigo gerado automaticamente em Mon Feb 14 09:47:52 BRST 2011
 * Gerador de Facace
*/

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EsferaGoverno;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class EsferaGovernoFacade extends AbstractFacade<EsferaGoverno> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EsferaGovernoFacade() {
        super(EsferaGoverno.class);
    }

    public EsferaGoverno findEsferaGovernoByNome(String nome){
        if (nome == null || nome.trim().isEmpty()) return null;
        Query e = em.createQuery("from EsferaGoverno where upper(nome) = :nome");
        e.setParameter("nome", nome.toUpperCase().trim());
        List<EsferaGoverno> resultado = e.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }
}
