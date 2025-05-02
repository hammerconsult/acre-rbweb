/*
 * Codigo gerado automaticamente em Mon Nov 14 16:00:21 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PrevidenciaVinculoFP;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class PrevidenciaVinculoFPFacade extends AbstractFacade<PrevidenciaVinculoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PrevidenciaVinculoFPFacade() {
        super(PrevidenciaVinculoFP.class);
    }

    @Override
    public void salvar(PrevidenciaVinculoFP entity) {
        entity = getPrevidenciaVinculoFPComHistorico(entity);
        super.salvar(entity);
    }

    public PrevidenciaVinculoFP getPrevidenciaVinculoFPComHistorico(PrevidenciaVinculoFP previdencia) {
        PrevidenciaVinculoFP previdenciaVinculoFPAtualmentePersistido = getPrevidenciaVinculoFPAtualmentePersistido(previdencia);
        previdencia.criarOrAtualizarAndAssociarHistorico(previdenciaVinculoFPAtualmentePersistido);
        return previdencia;
    }

    private PrevidenciaVinculoFP getPrevidenciaVinculoFPAtualmentePersistido(PrevidenciaVinculoFP pv) {
        if (pv.getId() != null) {
            return recuperar(pv.getId());
        }
        return pv;
    }

    public List<PrevidenciaVinculoFP> recuperaPrevidenciaVinculoFPProvimento(ContratoFP contratoFP, Date dataProvimento) {
        Query q = em.createQuery(" select distinct previdencia from PrevidenciaVinculoFP previdencia "
            + " where previdencia.contratoFP = :contratoFP and "
            + " :dataProvimento >= previdencia.inicioVigencia ");
        q.setMaxResults(1);
        q.setParameter("dataProvimento", Util.getDataHoraMinutoSegundoZerado(dataProvimento));
        q.setParameter("contratoFP", contratoFP);
        return q.getResultList();
    }

    public PrevidenciaVinculoFP buscarPrevidenciaVinculoFPVigentePorContratoFP(ContratoFP contratoFP) {
        String hql = "select p from PrevidenciaVinculoFP p where p.contratoFP = :contrato and :dataOperacao between p.inicioVigencia and coalesce(p.finalVigencia, :dataOperacao) ";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataOperacao", new Date());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (PrevidenciaVinculoFP) q.getSingleResult();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public boolean existePrevidenciaVinculoFPIsentoPorContratoFPData(ContratoFP contratoFP, Date data) {
        String hql = "select 1 from PrevidenciaVinculoFP p " +
            "where p.contratoFP = :contrato and :data between p.inicioVigencia and coalesce(p.finalVigencia, :data) " +
            " and p.tipoPrevidenciaFP.codigo = 5 ";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contratoFP);
        q.setParameter("data", data);
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }
}
