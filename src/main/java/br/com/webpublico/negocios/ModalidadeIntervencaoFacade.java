package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ModalidadeIntervencao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ModalidadeIntervencaoFacade extends AbstractFacade<ModalidadeIntervencao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ModalidadeIntervencaoFacade() {
        super(ModalidadeIntervencao.class);
    }
}
