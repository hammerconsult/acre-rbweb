package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseGruposConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabitesePadroesConstrucao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class HabitesePadroesConstrucaoFacade extends AbstractFacade<HabitesePadroesConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public HabitesePadroesConstrucaoFacade() {
        super(HabitesePadroesConstrucao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(HabitesePadroesConstrucao entity) {
        entity.setCodigo(recuperarUltimoCodigo(entity));
        super.salvarNovo(entity);
    }

    public Integer recuperarUltimoCodigo(HabitesePadroesConstrucao entity) {
        String sql = " select COALESCE (max(hpc.codigo), 0) from HabitesePadroesConstrucao hpc ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    public List<HabitesePadroesConstrucao> listarFiltrando(String parte) {
        String sql = "select padroes.* from HABITESEPADROESCONSTRUCAO padroes where (lower(padroes.DESCRICAO) like :parte or padroes.codigo like :parte)";
        Query q = em.createNativeQuery(sql, HabitesePadroesConstrucao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<HabitesePadroesConstrucao> listarFiltrandoPorGrupo(HabiteseGruposConstrucao grupo,  String parte) {
        String sql = "select padroes.* from HABITESEPADROESCONSTRUCAO padroes where padroes.HABITESEGRUPOSCONSTRUCAO_ID = :grupo and padroes.DESCRICAO like :parte";
        Query q = em.createNativeQuery(sql, HabitesePadroesConstrucao.class);
        q.setParameter("grupo", grupo.getId());
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public List<HabitesePadroesConstrucao> buscarPadroesPorExercicio(Exercicio ex) {
        Query q = em.createNativeQuery("select hpc.* from HABITESEPADROESCONSTRUCAO hpc where hpc.EXERCICIO_ID = :ex", HabitesePadroesConstrucao.class);
        q.setParameter("ex", ex.getId());
        return q.getResultList();
    }
}
