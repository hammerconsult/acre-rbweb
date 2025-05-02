package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 14/08/14
 * Time: 11:01
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConfiguracaoChamadoFacade extends AbstractFacade<ConfiguracaoChamado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ConfiguracaoChamadoFacade() {
        super(ConfiguracaoChamado.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return super.recuperar(entidade, id);
    }

    @Override
    public ConfiguracaoChamado recuperar(Object id) {
        ConfiguracaoChamado cc = getEntityManager().find(super.getClasse(), id);
        cc.getUsuarios().size();
        return cc;
    }

    public void salvarNovoChamado(Chamado chamado) {
        em.persist(chamado);
    }

    public void salvarChamado(Chamado chamado) {
        chamado = em.merge(chamado);
    }

    public Chamado recuperarChamado(Object id) {
        Chamado c = getEntityManager().find(Chamado.class, id);
        c.getRespostas().size();
        return c;
    }

    public void salvarNovoRespostaChamado(RespostaChamado resposta) {
        em.persist(resposta);
    }
}
