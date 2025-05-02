package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParametroFiscalizacao;
import br.com.webpublico.entidades.TipoDoctoOficial;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author fabio
 */
@Stateless
public class ParametroFiscalizacaoFacade extends AbstractFacade<ParametroFiscalizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DividaFacade dividaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public IndiceEconomicoFacade getIndiceEconomicoFacade() {
        return indiceEconomicoFacade;
    }

    public ParametroFiscalizacaoFacade() {
        super(ParametroFiscalizacao.class);
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    @Override
    public ParametroFiscalizacao recuperar(Object id) {
        ParametroFiscalizacao p = super.recuperar(id);
        p.getDividasIssqn().size();
        return p;
    }

    public boolean exercicioJaExiste(Exercicio exercicio) {
        String hql = "from ParametroFiscalizacao where exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        return !q.getResultList().isEmpty();
    }

    public TipoDoctoOficial recuperarTipoDoctoOrdemPorExercicio(Exercicio exercicio) {
        String hql = "select p.tipoDoctoOrdemServico from ParametroFiscalizacao p where p.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDoctoOficial) q.getSingleResult();
        } else {
            return null;
        }
    }

    public TipoDoctoOficial recuperarTipoDoctoTermoFiscalizacaoPorExercicio(Exercicio exercicio) {
        String hql = "select p.tipoDoctoTermoInicio from ParametroFiscalizacao p where p.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDoctoOficial) q.getSingleResult();
        } else {
            return null;
        }
    }

    public TipoDoctoOficial recuperarTipoDoctoAutoInfracaoPorExercicio(Exercicio exercicio) {
        String hql = "select p.tipoDoctoAutoInfracao from ParametroFiscalizacao p where p.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDoctoOficial) q.getSingleResult();
        } else {
            return null;
        }
    }

    public TipoDoctoOficial recuperarTipoDoctoHomologacaoPorExercicio(Exercicio exercicio) {
        String hql = "select p.tipoDoctoTermoHomologacao from ParametroFiscalizacao p where p.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDoctoOficial) q.getSingleResult();
        } else {
            return null;
        }
    }

    public TipoDoctoOficial recuperarTipoDoctoRelatorioFiscalPorExercicio(Exercicio exercicio) {
        String hql = "select p.tipoDoctoRelatorioFiscal from ParametroFiscalizacao p where p.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDoctoOficial) q.getSingleResult();
        } else {
            return null;
        }
    }

    public TipoDoctoOficial recuperarTipoDoctoTermoFinalizacaoPorExercicio(Exercicio exercicio) {
        String hql = "select p.tipoDoctoTermoFim from ParametroFiscalizacao p where p.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (TipoDoctoOficial) q.getSingleResult();
        } else {
            return null;
        }
    }

    public ParametroFiscalizacao recuperarParametroFiscalizacao(Exercicio exercicio) {
        String hql = "select p from ParametroFiscalizacao p where p.exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ParametroFiscalizacao) q.getSingleResult();
        } else {
            return null;
        }
    }

    public ParametroFiscalizacao recuperarParametroFiscalizacao(Integer ano) {
        String hql = "select p from ParametroFiscalizacao p where p.exercicio.ano = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", ano);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (ParametroFiscalizacao) q.getSingleResult();
        } else {
            return null;
        }
    }
}
