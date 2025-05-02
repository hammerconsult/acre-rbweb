/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.PaginaPrefeituraPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.UsuarioPaginaPortal;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class UsuarioPaginaPrefeituraPortalFacade extends AbstractFacade<UsuarioPaginaPortal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public UsuarioPaginaPrefeituraPortalFacade() {
        super(UsuarioPaginaPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<UsuarioPaginaPortal> buscarUsuarios(PaginaPrefeituraPortal pagina) {
        String sql = "select u.* from UsuarioPaginaPortal u";
        if (pagina != null) {
            sql += " where u.pagina_id = :pagina ";
        }
        Query q = em.createNativeQuery(sql, UsuarioPaginaPortal.class);
        if (pagina != null) {
            q.setParameter("pagina", pagina.getId());
        }
        try {
            List<UsuarioPaginaPortal> resultList = (List<UsuarioPaginaPortal>) q.getResultList();
            return resultList;
        } catch (Exception e) {
            logger.error("Erro ao recuperar usuários da paginaportal ", e);
            return Lists.newArrayList();
        }
    }

}
