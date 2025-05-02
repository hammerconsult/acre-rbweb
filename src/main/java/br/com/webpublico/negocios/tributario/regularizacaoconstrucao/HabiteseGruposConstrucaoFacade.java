package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseGruposConstrucao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class HabiteseGruposConstrucaoFacade extends AbstractFacade<HabiteseGruposConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public HabiteseGruposConstrucaoFacade() {
        super(HabiteseGruposConstrucao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(HabiteseGruposConstrucao entity) {
        entity.setCodigo(recuperarUltimoCodigo(entity));
        super.salvarNovo(entity);
    }

    public Integer recuperarUltimoCodigo(HabiteseGruposConstrucao entity) {
        String sql = " select COALESCE (max(hgc.codigo), 0) from HabiteseGruposConstrucao hgc ";
        Query q = em.createNativeQuery(sql);

        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    public List<HabiteseGruposConstrucao> listarFiltrando(String parte) {
        String sql = "select grupo.* from HABITESEGRUPOSCONSTRUCAO grupo where grupo.descricao like :parte";
        Query q = em.createNativeQuery(sql, HabiteseGruposConstrucao.class);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public List<HabiteseGruposConstrucao> buscarGruposPorExercicio(Exercicio ex) {
        Query q = em.createNativeQuery("select hgc.* from HABITESEGRUPOSCONSTRUCAO hgc where hgc.EXERCICIO_ID = :ex", HabiteseGruposConstrucao.class);
        q.setParameter("ex", ex.getId());
        return q.getResultList();
    }
}
