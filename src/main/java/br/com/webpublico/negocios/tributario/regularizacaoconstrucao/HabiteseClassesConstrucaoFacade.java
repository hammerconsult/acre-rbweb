package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseClassesConstrucao;
import br.com.webpublico.negocios.AbstractFacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class HabiteseClassesConstrucaoFacade extends AbstractFacade<HabiteseClassesConstrucao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public HabiteseClassesConstrucaoFacade() {
        super(HabiteseClassesConstrucao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(HabiteseClassesConstrucao entity) {
        entity.setCodigo(recuperarUltimoCodigo(entity));
        super.salvarNovo(entity);
    }

    public Integer recuperarUltimoCodigo(HabiteseClassesConstrucao entity) {
        String sql = " select COALESCE (max(hcc.codigo), 0) from HabiteseClassesConstrucao hcc ";
        Query q = em.createNativeQuery(sql);
        return (((BigDecimal) q.getSingleResult()).intValue() + 1);
    }

    public List<HabiteseClassesConstrucao> listarFiltrando(String parte) {
        String sql = "select classe.* from HABITESECLASSESCONSTRUCAO classe where (lower(classe.descricao) like :parte or classe.codigo like :parte)";
        Query q = em.createNativeQuery(sql, HabiteseClassesConstrucao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<HabiteseClassesConstrucao> buscarClassesPorExercicio(Exercicio ex) {
        Query q = em.createNativeQuery("select hcc.* from HABITESECLASSESCONSTRUCAO hcc where hcc.EXERCICIO_ID = :ex", HabiteseClassesConstrucao.class);
        q.setParameter("ex", ex.getId());
        return q.getResultList();
    }
}
