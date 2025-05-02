package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ZonaFiscal;
import br.com.webpublico.exception.ValidacaoException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ZonaFiscalFacade extends AbstractFacade<ZonaFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ZonaFiscalFacade() {
        super(ZonaFiscal.class);
    }

    @Override
    public void preSave(ZonaFiscal entidade) {
        entidade.realizarValidacoes();
        if (hasCodigoRegistrado(entidade)) {
            throw new ValidacaoException("O código informado já está registrado.");
        }
    }

    private boolean hasCodigoRegistrado(ZonaFiscal entidade) {
        Query q = em.createNativeQuery(" select * from zonafiscal zf " +
                " where zf.codigo = :codigo " +
                (entidade.getId() != null ? " and zf.id != :id " : ""))
            .setParameter("codigo", entidade.getCodigo());
        if (entidade.getId() != null) {
            q.setParameter("id", entidade.getId());
        }
        return !q.getResultList().isEmpty();
    }
}
