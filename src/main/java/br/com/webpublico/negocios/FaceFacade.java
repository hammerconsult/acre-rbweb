/*
 * Codigo gerado automaticamente em Tue Mar 01 14:57:55 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.List;

@Stateless
public class FaceFacade extends AbstractFacade<Face> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private CEPFacade cepFacade;
    @EJB
    private ServicoUrbanoFacade servicoUrbanoFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private SetorFacade setorFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FaceFacade() {
        super(Face.class);
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public ServicoUrbanoFacade getServicoUrbanoFacade() {
        return servicoUrbanoFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public QuadraFacade getQuadraFacade() {
        return quadraFacade;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    @Override
    public Face recuperar(Object id) {
        Face f = em.find(Face.class, id);
        f.getFaceServicos().size();
        f.getFaceValores().size();
        return f;
    }

    public List<Face> recuperarPorLogradouro(Logradouro l) {
        String hql = "select f from Face f " +
            "inner join f.logradouroBairro lb where lb.logradouro = :logradouro";
        Query query = em.createQuery(hql).setParameter("logradouro", l);

        try {
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Face> listaFiltrandoX(String s, int inicio, int max, Field... atributos) {
        String hql = "from Face obj ";
        hql += " where ";
        hql += " to_char(lower(obj.largura)) like :filtro ";
        hql += " or lower(obj.logradouro.nome) like :filtro ";
        hql += " or lower(obj.codigoFace) like :filtro ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public Face mergeFace(Face face) {
        return em.merge(face);
    }

    public List<FaceValor> buscarFaceValorPorExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select * from FaceValor where exercicio_id = :idExercicio", FaceValor.class);
        q.setParameter("idExercicio", exercicio.getId());
        return q.getResultList();
    }

    public boolean possuiFaceValorNoExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select * from FaceValor where exercicio_id = :idExercicio", FaceValor.class);
        q.setParameter("idExercicio", exercicio.getId());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public List<FaceServico> buscarFaceServicoPorExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select * from FaceServico where ano = :exercicio", FaceServico.class);
        q.setParameter("exercicio", exercicio.getAno());
        return q.getResultList();
    }

    public boolean possuiFaceServicoNoExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select * from FaceServico where ano = :exercicio", FaceServico.class);
        q.setParameter("exercicio", exercicio.getAno());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }


}
