package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrauParentescoPensionista;
import br.com.webpublico.enums.TipoPensao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Claudio
 */
@Stateless
public class GrauParentescoPensionistaFacade extends AbstractFacade<GrauParentescoPensionista> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public GrauParentescoPensionistaFacade() {
        super(GrauParentescoPensionista.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public boolean existeCodigo(GrauParentescoPensionista gpp) {
        String sql = "select * from GrauParentescoPensionista "
                + "where codigo = :codigo ";
        if (gpp.getId() != null) {
            sql += " and id <> :id";
        }
        Query q = em.createNativeQuery(sql, GrauParentescoPensionista.class);
        q.setParameter("codigo", gpp.getCodigo());
        if (gpp.getId() != null) {
            q.setParameter("id", gpp.getId());
        }
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public List<GrauParentescoPensionista> filtrarGrauDeParentesco(String parte, TipoPensao tipo) {
        String sql = "from GrauParentescoPensionista g "
                + "where ( cast((g.codigo) as string) like :parte or lower(g.descricao) like :parte) ";

        if (tipo != null && (tipo.equals(TipoPensao.TEMPORARIA) || tipo.equals(TipoPensao.TEMPORARIA_INVALIDEZ))) {
            sql += " and g.temporario is true ";
        }
        if (tipo != null && (tipo.equals(TipoPensao.VITALICIA) || tipo.equals(TipoPensao.VITALICIA_INVALIDEZ))) {
            sql += " and g.vitalicio is true ";
        }
        Query q = em.createQuery(sql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }
}
