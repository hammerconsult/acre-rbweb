package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ArquivoDesif;
import br.com.webpublico.nfse.domain.ArquivoDesifRegistro0430;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ArquivoDesifRegistro0430Facade extends AbstractFacade<ArquivoDesifRegistro0430> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ArquivoDesifRegistro0430Facade() {
        super(ArquivoDesifRegistro0430.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ArquivoDesifRegistro0430> buscarRegistros(ArquivoDesif arquivoDesif,
                                                          int firstResult, int maxResult) {
        return em.createNativeQuery(" select reg.* from arquivodesifregistro0430 reg " +
                " where reg.arquivodesif_id = :idArquivo ", ArquivoDesifRegistro0430.class)
            .setParameter("idArquivo", arquivoDesif.getId())
            .setFirstResult(firstResult)
            .setMaxResults(maxResult)
            .getResultList();
    }

    public List<ArquivoDesifRegistro0430> buscarRegistros(ArquivoDesif arquivoDesif) {
        return em.createNativeQuery(" select reg.* from arquivodesifregistro0430 reg " +
                " where reg.arquivodesif_id = :idArquivo ", ArquivoDesifRegistro0430.class)
            .setParameter("idArquivo", arquivoDesif.getId())
            .getResultList();
    }

    public Integer contarRegistros(ArquivoDesif arquivoDesif) {
        return ((Number) em.createNativeQuery(" select count(1) from arquivodesifregistro0430 reg " +
                " where reg.arquivodesif_id = :idArquivo ")
            .setParameter("idArquivo", arquivoDesif.getId())
            .getSingleResult()).intValue();
    }
}
