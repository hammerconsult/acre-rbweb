package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ImportacaoDebitosIPTU;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * Created by Filipe
 * On 17, Abril, 2019,
 * At 11:41
 */

@Stateless
public class ImportacaoDebitosIPTUFacade extends AbstractFacade<ImportacaoDebitosIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public ImportacaoDebitosIPTUFacade() {
        super(ImportacaoDebitosIPTU.class);
    }

    public void salvarNovo(ImportacaoDebitosIPTU entity) {
        em.merge(entity);
    }

    @Override
    public ImportacaoDebitosIPTU recuperar(Object id) {
        ImportacaoDebitosIPTU imp = super.recuperar(id);
        imp.getItemImportacaoDebitosIPTU().size();
        return imp;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
