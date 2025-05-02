package br.com.webpublico.negocios.rh.saudeservidor;

import br.com.webpublico.entidades.rh.saudeservidor.NaturezaLesao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class NaturezaLesaoFacade extends AbstractFacade<NaturezaLesao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public NaturezaLesaoFacade() {
        super(NaturezaLesao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
