package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CalculoContribuicaoMelhoria;
import br.com.webpublico.entidades.ProcessoCalculoContribuicaoMelhoria;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by William on 25/01/2017.
 */
@Stateless
public class CalculoContribuicaoMelhoriaFacade extends AbstractFacade<CalculoContribuicaoMelhoria> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public CalculoContribuicaoMelhoriaFacade() {
        super(CalculoContribuicaoMelhoria.class);
    }

    public void salvarProcesso(ProcessoCalculoContribuicaoMelhoria processo) {
        em.persist(processo);
    }


    public CalculoContribuicaoMelhoria recuperar(Long id) {
        if (id == null) {
            return null;
        }
        CalculoContribuicaoMelhoria calculo = em.find(CalculoContribuicaoMelhoria.class, id);
        if (calculo != null) {
            calculo.getItensCalculoContribuicaoMelhorias().size();
        }
        return calculo;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
