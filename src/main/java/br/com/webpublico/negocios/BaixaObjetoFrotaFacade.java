package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BaixaObjetoFrota;
import br.com.webpublico.entidades.ObjetoFrota;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 07/11/14
 * Time: 15:30
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class BaixaObjetoFrotaFacade extends AbstractFacade<BaixaObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public BaixaObjetoFrotaFacade() {
        super(BaixaObjetoFrota.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BaixaObjetoFrota recuperarBaixaDoObjetoFrota(ObjetoFrota objetoFrota) {
        String hql = " select baixa from BaixaObjetoFrota baixa where baixa.objetoFrota =:objetoFrota ";
        Query q = em.createQuery(hql);
        q.setParameter("objetoFrota", objetoFrota);
        return q.getResultList().size() > 0 ? (BaixaObjetoFrota) q.getResultList().get(0) : null;
    }

    @Override
    public void salvarNovo(BaixaObjetoFrota entity) {
        entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(BaixaObjetoFrota.class, "codigo"));
        super.salvarNovo(entity);
    }

    @Override
    public BaixaObjetoFrota recuperar(Object id) {
        BaixaObjetoFrota recuperar = super.recuperar(id);
        if (recuperar.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(recuperar.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return recuperar;
    }
}
