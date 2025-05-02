package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AcaoPPA;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SubAcaoPPA;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 20/09/13
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SubProjetoAtividadeFacade extends AbstractFacade<SubAcaoPPA> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubProjetoAtividadeFacade() {
        super(SubAcaoPPA.class);
    }

    @Override
    public SubAcaoPPA recuperar(Object id) {
        SubAcaoPPA subAcaoPPA = em.find(SubAcaoPPA.class, id);
        subAcaoPPA.getProvisaoPPADespesas().size();
        return subAcaoPPA;
    }

    public List<SubAcaoPPA> recuperarSubAcaoPPA(AcaoPPA ac) {
        List<SubAcaoPPA> listaSubAcao = new ArrayList<SubAcaoPPA>();
        String hql = " from SubAcaoPPA s where s.acaoPPA = :acao ";
        if (ac != null) {
            Query q = em.createQuery(hql);
            q.setParameter("acao", ac);
            listaSubAcao = q.getResultList();
        }
        return listaSubAcao;
    }

    public List<SubAcaoPPA> recuperarSubAcaoPPAPorExercicio(Exercicio ex) {
        String hql = "select sub.* from SubAcaoPPA sub where sub.exercicio_id = :ex";
        Query q = em.createNativeQuery(hql, SubAcaoPPA.class);
        q.setParameter("ex", ex.getId());
        return q.getResultList();
    }

    public List<SubAcaoPPA> buscarSubPorProjetoAtividades(String parte, AcaoPPA acaoPPA) {
        String sql = "select sub.* from subacaoppa sub " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id" +
            " inner join tipoacaoppa t on t.id = acao.tipoacaoppa_id " +
            " where acao.id = :acao" +
            " and (t.codigo || acao.codigo || '.' || sub.codigo like :parte or lower(sub.descricao) like :parte)";
        Query consulta = em.createNativeQuery(sql, SubAcaoPPA.class);
        consulta.setParameter("acao", acaoPPA.getId());
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<SubAcaoPPA>();
        }
    }

    public List<SubAcaoPPA> buscarSubPorProjetoAtividades(String parte, List<AcaoPPA> acoes) {
        List<Long> ids = acoes.stream().map(AcaoPPA::getId).collect(Collectors.toList());
        String sql = "select sub.* from subacaoppa sub " +
            " inner join acaoppa acao on sub.acaoppa_id = acao.id" +
            " inner join tipoacaoppa t on t.id = acao.tipoacaoppa_id " +
            " where acao.id in :acoes " +
            " and (t.codigo || acao.codigo || '.' || sub.codigo like :parte or lower(sub.descricao) like :parte)";
        Query consulta = em.createNativeQuery(sql, SubAcaoPPA.class);
        consulta.setParameter("acoes", ids);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<SubAcaoPPA>();
        }
    }

    public List<SubAcaoPPA> buscarSubProjetoAtividadePorExercicio(String parte, Exercicio ex) {
        String sql = "  select s.* " +
            "           from subacaoppa s " +
            "           inner join acaoppa a on a.id = s.acaoppa_id " +
            "           inner join programappa prog on a.programa_id = prog.id " +
            "           inner join tipoacaoppa t on t.id = a.tipoacaoppa_id " +
            "               where (t.codigo || a.codigo || '.' || s.codigo like :parte " +
            "                or replace(t.codigo || a.codigo || '.' || s.codigo, '.', '') like :parte" +
            "                or lower(a.descricao) like :parte" +
            "                or a.codigo like :parte)       " +
            "               and a.exercicio_id = :idExercicio " +
            "          order by a.codigo ";
        Query q = em.createNativeQuery(sql, SubAcaoPPA.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", ex.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public SubAcaoPPA buscarSubAcaoPPAPeloCodigoEDescricaoEExercicio(String codigo, String descricao, Exercicio exercicio) {
        if (codigo == null || descricao == null || exercicio == null) return null;
        String sql = "select sub.* " +
            "  from subacaoppa sub " +
            " where sub.exercicio_id = :exercicio " +
            "   and trim(upper(sub.descricao)) like :descricao " +
            "   and trim(sub.codigo) like :codigo " +
            " order by sub.id desc ";
        Query q = em.createNativeQuery(sql, SubAcaoPPA.class);
        q.setParameter("codigo", codigo.trim());
        q.setParameter("descricao", descricao.toUpperCase().trim());
        q.setParameter("exercicio", exercicio.getId());
        List<SubAcaoPPA> retorno = q.getResultList();
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }
}
