/*
 * Codigo gerado automaticamente em Fri Aug 26 14:08:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CelulaDeInformacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CelulaDeInformacaoFacade extends AbstractFacade<CelulaDeInformacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CelulaDeInformacaoFacade() {
        super(CelulaDeInformacao.class);
    }

    @Override
    public CelulaDeInformacao recuperar(Object id) {
        CelulaDeInformacao celula = getEntityManager().find(CelulaDeInformacao.class, id);
        celula.getCamposCelulaDeInformacao().size();
        return celula;
    }
}
