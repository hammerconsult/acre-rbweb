/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MedidaAdministrativa;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author java
 */
@Stateless
public class MedidaAdministrativaFacade extends AbstractFacade<MedidaAdministrativa> {

    @PersistenceContext (unitName="webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    public  MedidaAdministrativaFacade(){
        super(MedidaAdministrativa.class);
    }
}
