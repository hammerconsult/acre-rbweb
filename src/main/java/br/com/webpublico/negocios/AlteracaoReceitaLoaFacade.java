/*
 * Codigo gerado automaticamente em Mon Jul 09 15:47:01 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlteracaoReceitaLoa;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AlteracaoReceitaLoaFacade extends AbstractFacade<AlteracaoReceitaLoa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlteracaoReceitaLoaFacade() {
        super(AlteracaoReceitaLoa.class);
    }

    public ReceitaLOAFacade getReceitaLOAFacade() {
        return receitaLOAFacade;
    }
}
