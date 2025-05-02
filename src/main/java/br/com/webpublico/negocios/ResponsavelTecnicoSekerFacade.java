/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EspecializacaoSeker;
import br.com.webpublico.entidades.ResponsavelTecnicoSeker;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ResponsavelTecnicoSekerFacade extends AbstractFacade<ResponsavelTecnicoSeker> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ResponsavelTecnicoSekerFacade() {
        super(ResponsavelTecnicoSeker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<ResponsavelTecnicoSeker> buscarResponsavelSeker() {
        return em.createNativeQuery("select * from RESPONSAVELTECNICOSEKER", ResponsavelTecnicoSeker.class).getResultList();
    }
}
