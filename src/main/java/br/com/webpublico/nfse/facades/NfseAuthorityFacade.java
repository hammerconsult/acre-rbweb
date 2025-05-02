package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.NfseAuthority;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class NfseAuthorityFacade extends AbstractFacade<NfseAuthority> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public NfseAuthorityFacade() {
        super(NfseAuthority.class);
    }


    public NfseAuthority buscarPorTipo(String tipo) {
        Query q = em.createQuery(" select a from NfseAuthority a where a.name = :tipo");
        q.setParameter("tipo", tipo);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) return (NfseAuthority) q.getResultList().get(0);
        return null;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
