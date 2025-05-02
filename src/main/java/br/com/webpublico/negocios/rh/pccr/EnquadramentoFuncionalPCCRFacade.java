package br.com.webpublico.negocios.rh.pccr;

import br.com.webpublico.entidades.rh.pccr.EnquadramentoFuncionalPCCR;
import br.com.webpublico.entidades.rh.pccr.ValorProgressaoPCCR;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VinculoFPFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class EnquadramentoFuncionalPCCRFacade extends AbstractFacade<EnquadramentoFuncionalPCCR> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ValorProgressaoPCCRFacade valorProgressaoPCCRFacade;

    public EnquadramentoFuncionalPCCRFacade() {
        super(EnquadramentoFuncionalPCCR.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void migrarEnquadramentosFuncionaisPCCRTeste() {

    }

    @Override
    public EnquadramentoFuncionalPCCR recuperar(Object id) {
        EnquadramentoFuncionalPCCR e = em.find(EnquadramentoFuncionalPCCR.class, id);
        ValorProgressaoPCCR refe = valorProgressaoPCCRFacade.findValorByProgresaoAndCategoria(e.getReferenciaProgressaoPCCR(), e.getCategoriaPCCR());
        e.setVencimentoBase(refe != null ? refe.getValor() : null);
        return e;    //To change body of overridden methods use File | Settings | File Templates.
    }
}
