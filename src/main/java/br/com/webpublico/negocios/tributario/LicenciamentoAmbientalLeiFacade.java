package br.com.webpublico.negocios.tributario;


import br.com.webpublico.entidades.tributario.LicenciamentoAmbientalLei;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SistemaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class LicenciamentoAmbientalLeiFacade extends AbstractFacade<LicenciamentoAmbientalLei> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;

    public LicenciamentoAmbientalLeiFacade() {
        super(LicenciamentoAmbientalLei.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public boolean jaExisteLeiComMesmaDescricao(String descricaoReduzida) {
        Query q = em.createNativeQuery("select id from licenciamentoAmbientalLei where trim(lower(descricaoreduzida)) = :descricao ");
        q.setParameter("descricao", descricaoReduzida.toLowerCase().trim());
        return !q.getResultList().isEmpty();
    }
}
