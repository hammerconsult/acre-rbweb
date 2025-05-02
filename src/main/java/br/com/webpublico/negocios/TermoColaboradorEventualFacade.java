package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.TermoColaboradorEventual;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 24/08/14
 * Time: 14:50
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class TermoColaboradorEventualFacade extends AbstractFacade<TermoColaboradorEventual> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TermoColaboradorEventualFacade() {
        super(TermoColaboradorEventual.class);
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }
}
