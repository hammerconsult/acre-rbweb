/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlteracaoGrupoOC;
import br.com.webpublico.entidades.ItemAlteracaoGrupoOC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AlteracaoGrupoOCFacade extends AbstractFacade<AlteracaoGrupoOC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    public AlteracaoGrupoOC recuperar(Object id) {
        AlteracaoGrupoOC agoc = em.find(AlteracaoGrupoOC.class, id);
        agoc.getItensAlteracaoGrupoOCS().size();
        return agoc;
    }

    public void salvarItem(ItemAlteracaoGrupoOC item){
        em.merge(item);
    }

    public AlteracaoGrupoOCFacade() {
        super(AlteracaoGrupoOC.class);
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() { return grupoObjetoCompraFacade; }

    public ObjetoCompraFacade getObjetoCompraFacade() { return objetoCompraFacade; }

    public SistemaFacade getSistemaFacade() { return sistemaFacade; }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
