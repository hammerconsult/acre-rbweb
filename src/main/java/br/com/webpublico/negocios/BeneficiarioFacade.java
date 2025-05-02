/*
 * Codigo gerado automaticamente em Thu Feb 16 10:29:01 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class BeneficiarioFacade extends AbstractFacade<Beneficiario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ValorReferenciaFPFacade valorReferenciaFPFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BeneficiarioFacade() {
        super(Beneficiario.class);
    }

    @Override
    public Beneficiario recuperar(Object id) {
        Beneficiario b = em.find(Beneficiario.class, id);
        for (ItemBeneficiario item : b.getItensBeneficiarios()) {
            ParametroCalcIndenizacao parametro = item.getParametroCalcIndenizacao();
            ValorReferenciaFP valorReferenciaFP = valorReferenciaFPFacade.recuperaValorReferenciaFPVigente(parametro.getReferenciaFP());
            item.setValor(valorReferenciaFP.getValor());
        }
        Hibernate.initialize(b.getLotacaoFuncionals());
        Hibernate.initialize(b.getSindicatosVinculosFPs());
        Hibernate.initialize(b.getAssociacaosVinculosFPs());
        return b;
    }

    public List<Beneficiario> buscarBeneficiarioPorInstituidor(ContratoFP contratoFP) {
        String hql = " from Beneficiario benef  "
                + " where benef.contratoFP = :contratoFP";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);
        List<Beneficiario> beneficiarios = q.getResultList();
        if (beneficiarios != null && !beneficiarios.isEmpty()) {
            return beneficiarios;
        }
        return Lists.newArrayList();
    }
}
