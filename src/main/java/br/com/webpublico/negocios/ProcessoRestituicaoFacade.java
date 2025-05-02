package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by andregustavo on 19/05/2015.
 */
@Stateless
public class ProcessoRestituicaoFacade extends AbstractFacade<ProcessoRestituicao> {
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ProcessoFacade processoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private PagamentoJudicialFacade pagamentoJudicialFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private MoedaFacade moedaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProcessoRestituicaoFacade() {
        super(ProcessoRestituicao.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ProcessoRestituicao recuperar(Object id) {
        ProcessoRestituicao pr = super.recuperar(id);
        for (PessoaRestituicao p : pr.getPessoas()) {
            p.setPessoa(getPessoaFacade().recuperar(p.getPessoa().getId()));
        }
        pr.getPagamentos().size();
        pr.getParcelas().size();

        return pr;
    }

    public ProcessoRestituicao salvaProcessoRestituicao(ProcessoRestituicao pr) {
        return em.merge(pr);
    }

    public Long recuperaProximoCodigoPorExercicio(Exercicio exercicio) {
        String sql = " select max(coalesce(obj.codigo,0)) from ProcessoRestituicao obj "
                + " where obj.exercicio = :exercicio";
        Query query = em.createQuery(sql);
        query.setParameter("exercicio", exercicio);
        query.setMaxResults(1);
        try {
            Long resultado = (Long) query.getSingleResult();
            if (resultado == null) {
                resultado = 0l;
            }

            return resultado + 1;
        } catch (Exception e) {
            return 1l;
        }
    }

    public ParcelaValorDivida recuperaParcelaValorDivida(Long id) {
        String hql = "select pvd from ParcelaValorDivida pvd where pvd.id = :id";
        Query q = em.createQuery(hql, ParcelaValorDivida.class);
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            ParcelaValorDivida parcela = (ParcelaValorDivida) q.getSingleResult();
            parcela.getItensParcelaValorDivida().size();
            parcela.getSituacoes().size();
            return parcela;
        } else {
            return null;
        }
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ProcessoFacade getProcessoFacade() {
        return processoFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public PagamentoJudicialFacade getPagamentoJudicialFacade() {
        return pagamentoJudicialFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }
}
