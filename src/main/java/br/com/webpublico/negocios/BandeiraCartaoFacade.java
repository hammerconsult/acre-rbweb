package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BandeiraCartao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by William on 15/03/2017.
 */
@Stateless
public class BandeiraCartaoFacade extends AbstractFacade<BandeiraCartao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public BandeiraCartaoFacade() {
        super(BandeiraCartao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasBandeiraCadastrada(BandeiraCartao bandeiraCartao) {
        String sql = "select * from BANDEIRACARTAO where lower(descricao)  = :descricao ";
        if (bandeiraCartao.getId() != null) {
            sql += " and id <> :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("descricao", bandeiraCartao.getDescricao().trim().toLowerCase());
        if (bandeiraCartao.getId() != null) {
            q.setParameter("id", bandeiraCartao.getId());
        }
        return !q.getResultList().isEmpty();
    }


}
