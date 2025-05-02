package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exame;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by carlos on 18/06/15.
 */
@Stateless
public class ExameFacade extends AbstractFacade<Exame> {

    @EJB
    private RiscoFacade riscoFacade;
    @EJB
    private CargoFacade cargoFacade;
    @EJB
    private SingletonGeradorCodigoRH singletonGeradorCodigoRH;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExameFacade() {
        super(Exame.class);
    }

    public RiscoFacade getRiscoFacade() {
        return riscoFacade;
    }

    public CargoFacade getCargoFacade() {
        return cargoFacade;
    }

    @Override
    public void salvar(Exame entity) {
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(Exame entity) {
        super.salvarNovo(entity);
    }

    public List<Exame> completaExame(String filtro) {
        String hql = "from Exame where lower(trim(descricao)) like :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public Boolean existePorCodigo(Integer codigo) {
        String hql = " from Exame where codigo = :codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public SingletonGeradorCodigoRH getSingletonGeradorCodigoRH() {
        return singletonGeradorCodigoRH;
    }

}
