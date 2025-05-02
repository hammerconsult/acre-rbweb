package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.PlanoContratacaoAnualObjetoCompra;
import br.com.webpublico.entidades.UnidadeOrganizacional;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PlanoContratacaoAnualObjetoCompraFacade extends AbstractFacade<PlanoContratacaoAnualObjetoCompra> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlanoContratacaoAnualObjetoCompraFacade() {
        super(PlanoContratacaoAnualObjetoCompra.class);
    }

    public List<PlanoContratacaoAnualObjetoCompra> buscarPcaObjetosComprasPorExercicioEUndiadeAdm(String parte, Exercicio ex, UnidadeOrganizacional unidadeAdm, ObjetoCompra oc) {
        String sql = " select pcaObjC.* from planocontratanualobjcomp pcaObjC " +
            "   inner join objetocompra oc on oc.id = pcaObjC.objetoCompra_id " +
            "   inner join planocontratanualgrupo pacgrupooc on pacgrupooc.id = pcaObjC.planocontratacaoanualgrupo_id " +
            "   inner join planocontratacaoanual pac on pac.id = pacgrupooc.planocontratacaoanual_id " +
            " where (lower(oc.descricao) like :parte or cast(pcaObjC.numero as varchar(19)) like :parte)" +
            "   and pac.exercicio_id = :idExercicio " +
            "   and pac.unidadeOrganizacional_id = :idUnidadeAdm ";
        if (oc != null) {
            sql += " and pcaObjC.objetoCompra_id = :idOc ";
        }
        sql += " order by pcaObjC.numero ";
        Query q = em.createNativeQuery(sql, PlanoContratacaoAnualObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("idUnidadeAdm", unidadeAdm.getId());
        if (oc != null) {
            q.setParameter("idOc", oc.getId());
        }
        return q.getResultList();
    }
}
