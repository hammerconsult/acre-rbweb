package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.PlanoAnualContratacaoObjetoCompra;
import br.com.webpublico.entidades.UnidadeOrganizacional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PlanoAnualContratacaoObjetoCompraFacade extends AbstractFacade<PlanoAnualContratacaoObjetoCompra> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlanoAnualContratacaoObjetoCompraFacade() {
        super(PlanoAnualContratacaoObjetoCompra.class);
    }

    public List<PlanoAnualContratacaoObjetoCompra> buscarPacObjetosComprasPorExercicioEUndiadeAdm(String parte, Exercicio ex, UnidadeOrganizacional unidadeAdm, ObjetoCompra oc) {
        String sql = " select pacObjC.* " +
            " from PLANOANUALCONTOBJETOCOMPRA pacObjC " +
            "   inner join objetocompra oc on oc.id = pacObjC.objetoCompra_id " +
            "   inner join PLANOANUALCONTGRUPO pacGrupoOc on pacGrupoOc.id = pacObjC.planoAnualContratacaoGrupo_id " +
            "   inner join PlanoAnualContratacao pac on pac.id = pacGrupoOc.planoAnualContratacao_id " +
            " where (lower(oc.descricao) like :parte or cast(pacObjC.numero as varchar(19)) like :parte)" +
            "   and pac.exercicio_id = :idExercicio " +
            "   and pac.unidadeOrganizacional_id = :idUnidadeAdm ";
        if (oc != null) {
            sql += " and pacObjC.objetoCompra_id = :idOc ";
        }
        sql += " order by pacObjC.numero ";
        Query q = em.createNativeQuery(sql, PlanoAnualContratacaoObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("idUnidadeAdm", unidadeAdm.getId());
        if (oc != null) {
            q.setParameter("idOc", oc.getId());
        }
        return q.getResultList();
    }
}
