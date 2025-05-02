package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroCobrancaSPC;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroCobrancaSPCFacade extends AbstractFacade<ParametroCobrancaSPC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametroCobrancaSPCFacade() {
        super(ParametroCobrancaSPC.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroCobrancaSPC recuperarUltimo() {
        String sql = "select param.* from parametrocobrancaspc param ";
        Query q = em.createNativeQuery(sql, ParametroCobrancaSPC.class);
        List<ParametroCobrancaSPC> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }
}
