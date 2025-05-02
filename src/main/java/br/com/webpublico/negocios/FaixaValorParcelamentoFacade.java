package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FaixaValorParcelamento;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: ROBSONLUIS-MGA
 * Date: 10/10/13
 * Time: 18:51
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class FaixaValorParcelamentoFacade extends AbstractFacade<FaixaValorParcelamento> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public FaixaValorParcelamentoFacade() {
        super(FaixaValorParcelamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
