package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClassificacaoCargo;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ClassificacaoCargoFacade extends AbstractFacade<ClassificacaoCargo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ClassificacaoCargoFacade() {
        super(ClassificacaoCargo.class);
    }

    public boolean hasCodigoCadastrado(ClassificacaoCargo classificacaoCargo) {
        String sql = " select * from classificacaocargo " +
            " where codigo = :codigo ";
        if (classificacaoCargo.getId() != null) {
            sql += " and id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", classificacaoCargo.getCodigo());
        if (classificacaoCargo.getId() != null) {
            q.setParameter("id", classificacaoCargo.getId());
        }
        return !q.getResultList().isEmpty();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
