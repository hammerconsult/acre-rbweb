/*
 * Codigo gerado automaticamente em Wed Apr 04 09:45:09 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotivoAfastamentoRais;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MotivoAfastamentoRaisFacade extends AbstractFacade<MotivoAfastamentoRais> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotivoAfastamentoRaisFacade() {
        super(MotivoAfastamentoRais.class);
    }
}
