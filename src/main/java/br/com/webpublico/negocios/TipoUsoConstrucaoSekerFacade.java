/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ResponsavelTecnicoSeker;
import br.com.webpublico.entidades.TipoUsoConstrucaoSeker;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TipoUsoConstrucaoSekerFacade extends AbstractFacade<TipoUsoConstrucaoSeker> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoUsoConstrucaoSekerFacade() {
        super(TipoUsoConstrucaoSeker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<TipoUsoConstrucaoSeker> buscarTipoUsoConstrucaoSeker() {
        return em.createNativeQuery("select * from TIPOUSOCONSTRUCAOSEKER", TipoUsoConstrucaoSeker.class).getResultList();
    }
}
