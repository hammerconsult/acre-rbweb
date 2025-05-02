/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PastaGavetaContratoFP;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Alex
 * @since 23/03/2017 09:45
 */
@Stateless
public class PastaGavetaContratoFPFacade extends AbstractFacade<PastaGavetaContratoFP> {

    @EJB
    private GavetaFicharioFacade gavetaFicharioFacade;
    @EJB
    private PastaGavetaFacade pastaGavetaFacade;
    @EJB
    private FicharioFacade ficharioFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    public PastaGavetaContratoFPFacade() {
        super(PastaGavetaContratoFP.class);
    }

    @Override
    public PastaGavetaContratoFP recuperar(Object id) {
        PastaGavetaContratoFP pasta = em.find(PastaGavetaContratoFP.class,id);
        if (pasta.getPastaGaveta() != null) {
            pasta.getPastaGaveta().getPastasContratosFP().size();
        }
        if (pasta.getPastaGaveta().getGavetaFichario() != null) {
            pasta.getPastaGaveta().getGavetaFichario().getPastasGavetas().size();
        }
        return pasta;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<BigDecimal> buscarContratosAnteriores(ContratoFP contratoFP) {
        String sql =
                " SELECT vin.id as vinculo_id " +
                        "        FROM vinculofp vin " +
                        "        where vin.MATRICULAFP_ID = :matricula_id " +
                        "        ORDER BY vin.numero desc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("matricula_id", contratoFP.getMatriculaFP().getId());
        return q.getResultList();
    }

    public PastaGavetaContratoFP buscarPastaDoContratoAnterior(ContratoFP contratoFP) {
        String sql = " select * " +
                "      from PastaGavetaContratoFP pasta " +
                "      where pasta.CONTRATOFP_ID = :contratoAnterior " +
                "      and pasta.INICIOVIGENCIA = " +
                "         (" +
                "           select max(p.INICIOVIGENCIA) from PastaGavetaContratoFP p where p.CONTRATOFP_ID = :contratoAnterior" +
                "         ) ";

        for (BigDecimal contratoID : buscarContratosAnteriores(contratoFP)) {
            Query q = em.createNativeQuery(sql, PastaGavetaContratoFP.class);
            q.setParameter("contratoAnterior",contratoID.longValue());
            if (!q.getResultList().isEmpty()) {
                return (PastaGavetaContratoFP) q.getSingleResult();
            }
        }
        return null;
    }

    public void excluirPastaDoContratoFP(PastaGavetaContratoFP p) {
        em.remove(em.getReference(PastaGavetaContratoFP.class,p.getId()));
    }

    public GavetaFicharioFacade getGavetaFicharioFacade() {
        return gavetaFicharioFacade;
    }

    public PastaGavetaFacade getPastaGavetaFacade() {
        return pastaGavetaFacade;
    }

    public FicharioFacade getFicharioFacade() {
        return ficharioFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }
}
