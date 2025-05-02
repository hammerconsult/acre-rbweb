/*
 * Codigo gerado automaticamente em Fri Oct 07 09:23:57 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ReferenciaFP;
import br.com.webpublico.entidades.ValorReferenciaFP;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class ValorReferenciaFPFacade extends AbstractFacade<ValorReferenciaFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ValorReferenciaFPFacade() {
        super(ValorReferenciaFP.class);
    }

    public ValorReferenciaFP recuperaValorReferenciaFPVigente(ReferenciaFP referenciaFP) {
        try {
            Query q = em.createQuery("from ValorReferenciaFP valor "
                    + " where :dataVigencia >= valor.inicioVigencia "
                    + " and :dataVigencia <= coalesce(valor.finalVigencia,:dataVigencia) "
                    + " and valor.referenciaFP = :parametroReferencia ");
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
            q.setParameter("parametroReferencia", referenciaFP);
            q.setMaxResults(1);
            if (!q.getResultList().isEmpty()) {
                return (ValorReferenciaFP) q.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ValorReferenciaFP recuperaValorReferenciaFPVigente(ReferenciaFP referenciaFP, Date dataOperacao) {
        try {
            Query q = em.createQuery("from ValorReferenciaFP valor "
                    + " where :dataVigencia >= valor.inicioVigencia "
                    + " and :dataVigencia <= coalesce(valor.finalVigencia,:dataVigencia) "
                    + " and valor.referenciaFP = :parametroReferencia ");
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataOperacao));
            q.setParameter("parametroReferencia", referenciaFP);
            q.setMaxResults(1);
            if (!q.getResultList().isEmpty()) {
                return (ValorReferenciaFP) q.getSingleResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
