/*
 * Codigo gerado automaticamente em Fri Oct 14 20:25:32 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.OcorrenciaObjetoFrota;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class OcorrenciaObjetoFrotaFacade extends AbstractFacade<OcorrenciaObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OcorrenciaObjetoFrotaFacade() {
        super(OcorrenciaObjetoFrota.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public OcorrenciaObjetoFrota recuperar(Object id) {
        OcorrenciaObjetoFrota ocorrenciaObjetoFrota = super.recuperar(id);
        if (ocorrenciaObjetoFrota.getDetentorArquivoComposicao() != null &&
            ocorrenciaObjetoFrota.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            ocorrenciaObjetoFrota.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return ocorrenciaObjetoFrota;
    }
}
