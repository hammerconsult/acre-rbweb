package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ArquivoDesif;
import br.com.webpublico.nfse.domain.ArquivoDesifRegistro0300;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ArquivoDesifRegistro0300Facade extends AbstractFacade<ArquivoDesifRegistro0300> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ArquivoDesifRegistro0300Facade() {
        super(ArquivoDesifRegistro0300.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ArquivoDesifRegistro0300> buscarRegistros(ArquivoDesif arquivoDesif,
                                                          int firstResult, int maxResult) {
        return em.createNativeQuery(" select reg.* from arquivodesifregistro0300 reg " +
                " where reg.arquivodesif_id = :idArquivo ", ArquivoDesifRegistro0300.class)
            .setParameter("idArquivo", arquivoDesif.getId())
            .setFirstResult(firstResult)
            .setMaxResults(maxResult)
            .getResultList();
    }

    public List<ArquivoDesifRegistro0300> buscarRegistros(ArquivoDesif arquivoDesif) {
        return em.createNativeQuery(" select reg.* from arquivodesifregistro0300 reg " +
                " where reg.arquivodesif_id = :idArquivo ", ArquivoDesifRegistro0300.class)
            .setParameter("idArquivo", arquivoDesif.getId())
            .getResultList();
    }

    public Integer contarRegistros(ArquivoDesif arquivoDesif) {
        return ((Number) em.createNativeQuery(" select count(1) from arquivodesifregistro0300 reg " +
                " where reg.arquivodesif_id = :idArquivo ")
            .setParameter("idArquivo", arquivoDesif.getId())
            .getSingleResult()).intValue();
    }
}
