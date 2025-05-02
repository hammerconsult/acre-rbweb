package br.com.webpublico.negocios;

import br.com.webpublico.entidades.InfracaoSaud;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class InfracaoSaudFacade extends AbstractFacade<InfracaoSaud> {

    @PersistenceContext
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;

    public InfracaoSaudFacade() {
        super(InfracaoSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }
}
