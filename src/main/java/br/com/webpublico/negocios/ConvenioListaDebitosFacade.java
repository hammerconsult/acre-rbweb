package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConvenioListaDebitos;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author octavio
 */
@Stateless
public class ConvenioListaDebitosFacade extends AbstractFacade<ConvenioListaDebitos> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    public ConvenioListaDebitosFacade() {
        super(ConvenioListaDebitos.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public List<ConvenioListaDebitos> buscarConvenioListaDebitos(String parte) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select C.* from CONVENIOLISTADEBITOS C ")
            .append(" inner join AGENCIA A ON A.ID = C.AGENCIA_ID ")
            .append(" where lower(A.NOMEAGENCIA) like :filtro ");
        Query q = em.createNativeQuery(sql.toString(), ConvenioListaDebitos.class);
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");

        return q.getResultList();
    }
}
