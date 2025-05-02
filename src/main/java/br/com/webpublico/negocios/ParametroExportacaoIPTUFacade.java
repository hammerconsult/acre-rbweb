package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroExportacaoIPTU;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class ParametroExportacaoIPTUFacade extends AbstractFacade<ParametroExportacaoIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametroExportacaoIPTUFacade() {
        super(ParametroExportacaoIPTU.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
