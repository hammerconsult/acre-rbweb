package br.com.webpublico.negocios;

import br.com.webpublico.controle.DividaIPTUControlador;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoCalculo;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class ProcessoCalculoIPTUFacade extends AbstractFacade<ProcessoCalculoIPTU> {
    private static Logger logger = LoggerFactory.getLogger(ProcessoCalculoIPTUFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProcessoCalculoIPTUFacade() {
        super(ProcessoCalculoIPTU.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProcessoCalculoIPTU> listaProcessosPorDescricao(String descricao) {
        Query q = em.createQuery("select p from ProcessoCalculoIPTU p " +
            " where lower(p.descricao) like :descricao " +
            " order by p.id desc");
        q.setParameter("descricao", "%" + descricao.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<ProcessoCalculoIPTU> listaProcessosPorDividaExercicio(Divida divida, Exercicio exercicio) {
        String junct = " where ";
        String query = "FROM ProcessoCalculoIPTU p ";
        if ((divida != null) && (divida.getId() != null)) {
            query += junct + " divida = :divida";
            junct = " and ";
        }
        if ((exercicio != null) && (exercicio.getId() != null)) {
            query += junct + " exercicio = :exercicio";
            junct = " and ";
        }
        Query q = em.createQuery(query);
        if (divida.getId() != null) {
            q.setParameter("divida", divida);
        }
        if (exercicio.getId() != null) {
            q.setParameter("exercicio", exercicio);
        }
        return q.getResultList();
    }

    public List<ProcessoCalculoIPTU> listaProcessosPorDividaExercicioDataLancamento(DividaIPTUControlador.EfetivacaoIPTU efetivacao) {
        String junct = " where ";
        String query = "FROM ProcessoCalculoIPTU p ";
        if ((efetivacao.getDivida() != null) && (efetivacao.getDivida().getId() != null)) {
            query += junct + " divida = :divida";
            junct = " and ";
        }
        if ((efetivacao.getExercicio() != null) && (efetivacao.getExercicio().getId() != null)) {
            query += junct + " exercicio = :exercicio";
            junct = " and ";
        }
        if (efetivacao.getLancamentoInicial() != null) {
            query += junct + " trunc(dataLancamento) >= :lancamentoInicial";
            junct = " and ";
        }
        if (efetivacao.getLancamentoFinal() != null) {
            query += junct + " trunc(dataLancamento) <= :lancamentoFinal";
            junct = " and ";
        }
        Query q = em.createQuery(query);
        if (efetivacao.getDivida().getId() != null) {
            q.setParameter("divida", efetivacao.getDivida());
        }
        if (efetivacao.getExercicio().getId() != null) {
            q.setParameter("exercicio", efetivacao.getExercicio());
        }
        if (efetivacao.getLancamentoInicial() != null) {
            q.setParameter("lancamentoInicial", efetivacao.getLancamentoInicial());
        }
        if (efetivacao.getLancamentoFinal() != null) {
            q.setParameter("lancamentoFinal", efetivacao.getLancamentoFinal());
        }
        return q.getResultList();
    }

    public List listaIdCalculosPorProcesso(ProcessoCalculoIPTU processo) {
        return em.createQuery("select c.id from CalculoIPTU c where c.processoCalculoIPTU = :processo").setParameter("processo", processo).getResultList();
    }

    public void verificarEfefetivacao(ProcessoCalculoIPTU processo, BigDecimal meioUfm) {
        String sql = "select count(iptu.id), count(vd.id) from processocalculoiptu processo " +
            "inner join calculoiptu iptu on iptu.PROCESSOCALCULOIPTU_ID = processo.id " +
            "inner join calculo on calculo.id = iptu.id " +
            "left join valordivida vd on vd.calculo_id = iptu.id " +
            "where processo.id = :processo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("processo", processo.getId());
        List<Object[]> listaArrayObjetos = q.getResultList();
        BigDecimal quantidadeCalculos = (BigDecimal) listaArrayObjetos.get(0)[0];
        BigDecimal quantidadeEfetivacoes = (BigDecimal) listaArrayObjetos.get(0)[1];
        if (quantidadeCalculos != null) {
            processo.setQuantidadeCalculos(quantidadeCalculos.intValue());
        }
        if (quantidadeEfetivacoes != null && quantidadeEfetivacoes.compareTo(BigDecimal.ZERO) > 0) {
            processo.setEfetivado(ProcessoCalculoIPTU.Efetivado.TOTAL);
        }
    }

    public ProcessoCalculo buscarProcessoCalculoIptuPorIdCalculo(Long idCalculo) {
        Query q = em.createNativeQuery("select p.* " +
            "                             from processocalculo p " +
            "                            inner join calculoiptu c on c.processocalculoiptu_id = p.id" +
            "                           where c.id = :id", ProcessoCalculo.class);
        q.setParameter("id", idCalculo);
        q.setMaxResults(1);
        try {
            return (ProcessoCalculo) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
