/*
 * Codigo gerado automaticamente em Fri Sep 16 09:00:47 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.OpcaoValeTransporteFP;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class OpcaoValeTransporteFPFacade extends AbstractFacade<OpcaoValeTransporteFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OpcaoValeTransporteFPFacade() {
        super(OpcaoValeTransporteFP.class);
    }

    @Override
    public void salvar(OpcaoValeTransporteFP entity) {
        entity = getOpcaoValeTransporteFPComHistorico(entity);
        super.salvar(entity);
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteFPComHistorico(OpcaoValeTransporteFP ovt) {
        OpcaoValeTransporteFP opcaoValeTransporteFPAtualmentePersistido = getOpcaoValeTransporteFPAtualmentePersistido(ovt);
        ovt.criarOrAtualizarAndAssociarHistorico(opcaoValeTransporteFPAtualmentePersistido);
        return ovt;
    }

    private OpcaoValeTransporteFP getOpcaoValeTransporteFPAtualmentePersistido(OpcaoValeTransporteFP ovt) {
        if (ovt.getId() != null) {
            return recuperar(ovt.getId());
        }
        return ovt;
    }

    public OpcaoValeTransporteFP recuperaOpcaoValeTransporteFPVigente(ContratoFP contratoFP, Date dataProvimento) {
        Query q = em.createQuery(" select distinct vale from OpcaoValeTransporteFP vale "
            + " where vale.contratoFP = :contratoFP and "
            + " :dataProvimento >= vale.inicioVigencia and "
            + " :dataProvimento <= coalesce(vale.finalVigencia,:dataProvimento) ");
        q.setMaxResults(1);
        q.setParameter("dataProvimento", Util.getDataHoraMinutoSegundoZerado(dataProvimento));
        q.setParameter("contratoFP", contratoFP);
        if (!q.getResultList().isEmpty()) {
            return (OpcaoValeTransporteFP) q.getSingleResult();
        }
        return null;
    }
}
