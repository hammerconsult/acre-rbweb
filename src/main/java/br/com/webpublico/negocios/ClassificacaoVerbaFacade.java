package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClassificacaoVerba;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ClassificacaoVerbaFacade extends AbstractFacade<ClassificacaoVerba> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ClassificacaoVerbaFacade() {
        super(ClassificacaoVerba.class);
    }

    public boolean hasCodigoCadastrado(ClassificacaoVerba classificacaoVerba) {
        String sql = " select * from classificacaoverba " +
            " where codigo = :codigo ";
        if (classificacaoVerba.getId() != null) {
            sql += " and id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", classificacaoVerba.getCodigo());
        if (classificacaoVerba.getId() != null) {
            q.setParameter("id", classificacaoVerba.getId());
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
