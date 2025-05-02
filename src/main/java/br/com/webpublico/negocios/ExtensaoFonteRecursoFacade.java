package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ExtensaoFonteRecurso;
import br.com.webpublico.enums.TipoContaCorrenteTCE;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by Edi on 19/04/2016.
 */
@Stateless
public class ExtensaoFonteRecursoFacade extends AbstractFacade<ExtensaoFonteRecurso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExtensaoFonteRecursoFacade() {
        super(ExtensaoFonteRecurso.class);
    }

    public ExtensaoFonteRecurso buscarPorTipo(TipoContaCorrenteTCE tipo) {
        String sql = "select * from EXTENSAOFONTERECURSO where TIPOCONTACORRENTETCE = :tipo";
        Query q = em.createNativeQuery(sql, ExtensaoFonteRecurso.class);
        q.setParameter("tipo", tipo.name());
        q.setMaxResults(1);
        try {
            return (ExtensaoFonteRecurso) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
