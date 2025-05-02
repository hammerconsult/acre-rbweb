/*
 * Codigo gerado automaticamente em Fri Apr 15 09:13:56 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.TipoConta;
import br.com.webpublico.enums.ClasseDaConta;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoContaFacade extends AbstractFacade<TipoConta> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoContaFacade() {
        super(TipoConta.class);
    }

    public TipoConta buscaTipoContaPorConta(Conta c) {
        TipoConta tc = new TipoConta();
        String sql = "SELECT tc.* FROM tipoconta tc "
            + "INNER JOIN planodecontas pc ON tc.id = pc.tipoconta_id "
            + "INNER JOIN conta c ON pc.id = c.planodecontas_id "
            + "WHERE c.id =:param";
        Query q = em.createNativeQuery(sql, TipoConta.class);
        q.setParameter("param", c.getId());
        q.setMaxResults(1);
        return (TipoConta) q.getSingleResult();
    }

    public boolean validaRelacionamentoComPlanoDeContas(TipoConta tipoConta) {
        Query consulta = em.createNativeQuery("SELECT p.* FROM planodecontas p WHERE p.tipoconta_id = :tipo");
        consulta.setParameter("tipo", tipoConta.getId());
        try {
            if (consulta.getResultList().isEmpty()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void verificarExistente(TipoConta tipoConta) {
        String sql = " SELECT tipo.* FROM TipoConta tipo " +
            " WHERE tipo.ClasseDaConta = :classe " +
            " and tipo.exercicio_id = :exercicio ";
        if (tipoConta.getId() != null) {
            sql += " and tipo.id <> :id ";
        }
        Query consulta = em.createNativeQuery(sql, TipoConta.class);
        consulta.setParameter("classe", tipoConta.getClasseDaConta().name());
        consulta.setParameter("exercicio", tipoConta.getExercicio().getId());
        if (tipoConta.getId() != null) {
            consulta.setParameter("id", tipoConta.getId());
        }
        if (!consulta.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica(" Já existe um tipo de conta para a classe <b> " + tipoConta.getClasseDaConta().getDescricao() + "</b> e exercício <b>" + tipoConta.getExercicio().getAno() + " </b>.");
        }
    }

    public List<TipoConta> buscarPorExercicio(Exercicio exercicio) {
        String sql = " SELECT tipo.* FROM TipoConta tipo " +
            " WHERE tipo.exercicio_id = :exercicio ";
        Query consulta = em.createNativeQuery(sql, TipoConta.class);
        consulta.setParameter("exercicio", exercicio.getId());
        return consulta.getResultList();
    }

    public TipoConta buscarTipoContaPorClassesExercicio(ClasseDaConta classeDaConta, Exercicio exercicio) {
        String sql = "select * " +
            "from tipoconta " +
            "where classedaconta = :classeConta " +
            "and exercicio_id = :idExercicio";
        Query q = em.createNativeQuery(sql, TipoConta.class);
        q.setParameter("classeConta", classeDaConta.name());
        q.setParameter("idExercicio", exercicio.getId());
        q.setMaxResults(1);
        List<TipoConta> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return null;
        }
        return resultado.get(0);
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
