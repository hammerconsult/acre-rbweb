package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaIsencaoIPTU;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.tributario.TipoCategoriaIsencaoIPTU;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Fabio
 */
@Stateless
public class CategoriaIsencaoIPTUFacade extends AbstractFacade<CategoriaIsencaoIPTU> {

    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public CategoriaIsencaoIPTUFacade() {
        super(CategoriaIsencaoIPTU.class);
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as codigo from CategoriaIsencaoIPTU");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Long ultimoCodigo() {
        String sql = "select max(codigo) from CategoriaIsencaoIPTU";
        Query q = em.createNativeQuery(sql);
        String resultado = (String) q.getResultList().get(0);
        return resultado != null ? new BigDecimal(resultado).longValue() : 1;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "select * from CategoriaIsencaoIPTU where codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean existeCodigoCategoriaIsencaoIPTU(CategoriaIsencaoIPTU categoriaIsencaoIPTU) {
        String sql = "select * from CategoriaIsencaoIPTU where codigo = :codigo and id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", categoriaIsencaoIPTU.getCodigo());
        q.setParameter("id", categoriaIsencaoIPTU.getId());
        return !q.getResultList().isEmpty();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public TipoDoctoOficialFacade getTipoDoctoOficialFacade() {
        return tipoDoctoOficialFacade;
    }

    public List<CategoriaIsencaoIPTU> listaPorFiltro(String filtro, Exercicio exercicio) {
        String hql = "select c from CategoriaIsencaoIPTU c "
                + " where (to_char(c.codigo) like :filtro or lower(c.descricao) like :filtro) "
                + " and (c.exercicioInicial.ano >= :ano and c.exercicioFinal.ano <= :ano)";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("ano", exercicio.getAno());
        return q.getResultList();
    }

    public List<CategoriaIsencaoIPTU> listaPorExercicio(Exercicio exercicio) {
        if (exercicio == null) {
            return Lists.newArrayList();
        }
        String hql = "select distinct c from CategoriaIsencaoIPTU c where "
                + " :exercicio between c.exercicioInicial and c.exercicioFinal"
                + " order by c.descricao ";

        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        return q.getResultList();
    }

    public List<CategoriaIsencaoIPTU> buscarCategoriasPorTipoCategoria(TipoCategoriaIsencaoIPTU tipoCategoriaIsencaoIPTU) {
        String hql = "select c from CategoriaIsencaoIPTU c "
            + " where c.tipoCategoriaIsencaoIPTU = :tipoCategoriaIsencaoIPTU "
            + " order by c.codigo desc ";
        Query q = em.createQuery(hql);
        q.setParameter("tipoCategoriaIsencaoIPTU", tipoCategoriaIsencaoIPTU);
        return q.getResultList();
    }
}
