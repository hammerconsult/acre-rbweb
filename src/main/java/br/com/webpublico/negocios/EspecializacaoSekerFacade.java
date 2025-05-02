/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EspecializacaoSeker;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class EspecializacaoSekerFacade extends AbstractFacade<EspecializacaoSeker> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EspecializacaoSekerFacade() {
        super(EspecializacaoSeker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<EspecializacaoSeker> buscarEspecializacaoSeker() {
        return em.createNativeQuery("select * from ESPECIALIZACAOSEKER", EspecializacaoSeker.class).getResultList();
    }
}
