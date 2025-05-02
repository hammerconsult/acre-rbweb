package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlteracaoNFSAvulsa;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 10/03/14
 * Time: 15:14
 */
@Stateless
public class AlteracaoNFSAvulsaFacade extends AbstractFacade<AlteracaoNFSAvulsa> {
    @EJB
    private NFSAvulsaFacade nfsAvulsaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public AlteracaoNFSAvulsaFacade() {
        super(AlteracaoNFSAvulsa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AlteracaoNFSAvulsa recuperar(Object id) {
        AlteracaoNFSAvulsa alteracao = super.recuperar(id);
        alteracao.getNFSAvulsa();
        return alteracao;
    }

    public NFSAvulsaFacade getNfsAvulsaFacade() {
        return nfsAvulsaFacade;
    }
}
