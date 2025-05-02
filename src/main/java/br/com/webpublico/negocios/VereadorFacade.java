package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.Vereador;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 16/06/15
 * Time: 10:33
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class VereadorFacade extends AbstractFacade<Vereador> {

    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VereadorFacade() {
        super(Vereador.class);
    }

    public boolean hasVereadorVigente(Vereador vereador) {
        String sql = " select v.* from vereador v " +
            " where v.exercicio_id = :idExercicio " +
            " and v.pessoa_id = :idPessoa ";
        if (vereador.getId() != null) {
            sql += " and v.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", vereador.getExercicio().getId());
        q.setParameter("idPessoa", vereador.getPessoa().getId());
        if (vereador.getId() != null) {
            q.setParameter("id", vereador.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<Vereador> listaVereadorPorExercicio(String parte, Exercicio exercicio) {
        String sql = " select v.* from vereador v " +
            " inner join pessoa p on p.id = v.pessoa_id " +
            " left join pessoafisica pf on pf.id = p.id " +
            " left join pessoajuridica pj on pj.id = p.id " +
            "       where v.exercicio_id = :idExercicio " +
            "       and (lower(pf.nome) like :parte " +
            "         or pf.cpf like :parte " +
            "         or (lower(pj.nomefantasia) like :parte) " +
            "         or pj.cnpj like :parte) ";
        Query q = em.createNativeQuery(sql, Vereador.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        List<Vereador> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultado;
    }

    public Vereador buscarVereadorPorUsuarioAndExercicio(UsuarioSistema usuarioSistema, Exercicio exercicio) {
        String sql = " select v.* from vereador v " +
            " where v.pessoa_id = :pessoa and v.exercicio_id = :exercicio ";
        Query q = em.createNativeQuery(sql, Vereador.class);
        q.setParameter("pessoa", usuarioSistema.getPessoaFisica().getId());
        q.setParameter("exercicio", exercicio.getId());
        List<Vereador> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public void setPessoaFacade(PessoaFacade pessoaFacade) {
        this.pessoaFacade = pessoaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public void setExercicioFacade(ExercicioFacade exercicioFacade) {
        this.exercicioFacade = exercicioFacade;
    }
}
