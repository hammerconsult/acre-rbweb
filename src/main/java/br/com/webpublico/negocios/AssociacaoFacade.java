/*
 * Codigo gerado automaticamente em Wed Feb 08 17:21:31 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Associacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AssociacaoFacade extends AbstractFacade<Associacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AssociacaoFacade() {
        super(Associacao.class);
    }

    @Override
    public Associacao recuperar(Object id) {
        Associacao associacao = em.find(Associacao.class, id);
        associacao.getItensValoresAssociacoes().size();
        return associacao;
    }

    public PessoaJuridicaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }
}
