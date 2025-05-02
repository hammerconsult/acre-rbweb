package br.com.webpublico.nfse.facades;

import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ArquivoDesif;
import br.com.webpublico.nfse.domain.ArquivoDesifRegistro1000;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ArquivoDesifRegistro1000Facade extends AbstractFacade<ArquivoDesifRegistro1000> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ArquivoDesifRegistro1000Facade() {
        super(ArquivoDesifRegistro1000.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ArquivoDesifRegistro1000> buscarRegistros(ArquivoDesif arquivoDesif,
                                                          int firstResult, int maxResult) {
        return em.createNativeQuery(" select reg.* from arquivodesifregistro1000 reg " +
                " where reg.arquivodesif_id = :idArquivo ", ArquivoDesifRegistro1000.class)
            .setParameter("idArquivo", arquivoDesif.getId())
            .setFirstResult(firstResult)
            .setMaxResults(maxResult)
            .getResultList();
    }

    public List<ArquivoDesifRegistro1000> buscarRegistros(ArquivoDesif arquivoDesif) {
        return em.createNativeQuery(" select reg.* from arquivodesifregistro1000 reg " +
                " where reg.arquivodesif_id = :idArquivo ", ArquivoDesifRegistro1000.class)
            .setParameter("idArquivo", arquivoDesif.getId())
            .getResultList();
    }

    public Integer contarRegistros(ArquivoDesif arquivoDesif) {
        return ((Number) em.createNativeQuery(" select count(1) from arquivodesifregistro1000 reg " +
                " where reg.arquivodesif_id = :idArquivo ")
            .setParameter("idArquivo", arquivoDesif.getId())
            .getSingleResult()).intValue();
    }
}
