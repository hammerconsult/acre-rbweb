package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Capacitacao;
import br.com.webpublico.entidades.Modelo;
import br.com.webpublico.entidades.ModuloCapacitacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by carlos on 12/06/15.
 */
@Stateless
public class ModuloCapacitacaoFacade extends AbstractFacade<ModuloCapacitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    public ModuloCapacitacaoFacade() {
        super(ModuloCapacitacao.class);
    }

    @Override
    public ModuloCapacitacao recuperar(Object id) {
        ModuloCapacitacao modulo = em.find(ModuloCapacitacao.class, id);
        modulo.getPresencas().size();
        return modulo;
    }



    public Capacitacao recuperarCapacitcao(Long id) {
        Capacitacao capacitacao = em.find(Capacitacao.class, id);
        capacitacao.getInscricoes().size();
        return capacitacao;
    }
}
