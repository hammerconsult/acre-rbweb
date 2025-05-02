/*
 * Codigo gerado automaticamente em Thu Aug 04 09:29:19 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SindicatoVinculoFP;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class SindicatoVinculoFPFacade extends AbstractFacade<SindicatoVinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SindicatoVinculoFPFacade() {
        super(SindicatoVinculoFP.class);
    }

    @Override
    public void salvar(SindicatoVinculoFP entity) {
        entity = getSindicatoVinculoFPComHistorico(entity);
        super.salvar(entity);
    }

    public SindicatoVinculoFP getSindicatoVinculoFPComHistorico(SindicatoVinculoFP sindicatoVinculoFP) {
        SindicatoVinculoFP sindicatoVinculoFPAtualmentePersistido = getSindicatoVinculoFPAtualmentePersistido(sindicatoVinculoFP);
        sindicatoVinculoFP.criarOrAtualizarAndAssociarHistorico(sindicatoVinculoFPAtualmentePersistido);
        return sindicatoVinculoFP;
    }

    private SindicatoVinculoFP getSindicatoVinculoFPAtualmentePersistido(SindicatoVinculoFP sindicatoVinculoFP) {
        if (sindicatoVinculoFP.getId() != null) {
            return recuperar(sindicatoVinculoFP.getId());
        }
        return sindicatoVinculoFP;
    }

    public SindicatoVinculoFP recuperaSindicatoVinculoFPVigente(ContratoFP contratoFP, Date dataProvimento) {
        Query q = em.createQuery(" select distinct sindicato from SindicatoVinculoFP sindicato "
            + " where sindicato.vinculoFP = :contratoFP and "
            + " :dataProvimento >= sindicato.inicioVigencia and "
            + " :dataProvimento <= coalesce(sindicato.finalVigencia,:dataProvimento) ");
        q.setMaxResults(1);
        q.setParameter("dataProvimento", Util.getDataHoraMinutoSegundoZerado(dataProvimento));
        q.setParameter("contratoFP", contratoFP);
        if (!q.getResultList().isEmpty()) {
            return (SindicatoVinculoFP) q.getSingleResult();
        }
        return null;
    }

    public SindicatoVinculoFP retornaUltimoSindicato(ContratoFP contratoFP) {
        try {
            String hql = "select sindicato from SindicatoVinculoFP sindicato " +
                " where sindicato.vinculoFP = :contratoFP " +
                " and sindicato.inicioVigencia = (select max(sind.inicioVigencia) from SindicatoVinculoFP sind where sind.vinculoFP = :contratoFP)";
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            return (SindicatoVinculoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
