/*
 * Codigo gerado automaticamente em Wed Aug 03 16:16:03 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrauDeParentesco;
import br.com.webpublico.entidades.GrauParentTipoDepend;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class GrauDeParentescoFacade extends AbstractFacade<GrauDeParentesco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public GrauDeParentescoFacade() {
        super(GrauDeParentesco.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public GrauDeParentesco recuperar(Object id) {
        GrauDeParentesco gdp = em.find(GrauDeParentesco.class, id);
        gdp.getGrauParentTipoDepends().size();
        return gdp;
    }

    public void salvar(GrauDeParentesco entity, List<GrauParentTipoDepend> listaExcluidos) {
        try {
            for (GrauParentTipoDepend g : listaExcluidos) {
                g = em.find(GrauParentTipoDepend.class, g.getId());
                em.remove(g);
            }
            getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    public List<GrauDeParentesco> listaFiltrando(String parte) {

        String hql = "select g from GrauDeParentesco g where "
            + "lower(g.descricao) like :descricao ";
        if (!parte.isEmpty() && isNumerico(parte)) {
            //System.out.println("entrou aqui");
            hql += "or g.codigoRais = :codigo "
                + "order by g.codigoRais";
        }

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("descricao", "%" + parte.toLowerCase() + "%");

        if (!parte.isEmpty() && isNumerico(parte)) {
            q.setParameter("codigo", Integer.parseInt(parte));
        }
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<GrauDeParentesco> buscarGrauDeParentescoAtivo() {
        String sql = "select * from GRAUDEPARENTESCO where ativo = :ativo order by to_number (CODIGOESOCIAL)";
        Query q = em.createNativeQuery(sql, GrauDeParentesco.class);
        q.setParameter("ativo", Boolean.TRUE);
        return q.getResultList();
    }

    public GrauDeParentesco buscarGrauDeParentescoAtivoPorCodigo(Integer codigo) {
        String sql = "select * from GRAUDEPARENTESCO where ativo = :ativo and codigorais = :codigo";
        Query q = em.createNativeQuery(sql, GrauDeParentesco.class);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("codigo", codigo);
        List result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            return (GrauDeParentesco) result.get(0);
        }
        return null;
    }

    private boolean isNumerico(String valor) {
        return valor.matches("[0-9]*");
    }
}
