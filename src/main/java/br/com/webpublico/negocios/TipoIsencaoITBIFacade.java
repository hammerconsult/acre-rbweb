package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Agencia;
import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.TipoIsencaoITBI;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoIsencaoITBIFacade extends AbstractFacade<TipoIsencaoITBI> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoIsencaoITBIFacade() {
        super(TipoIsencaoITBI.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(TipoIsencaoITBI entity) {
        Query q = em.createQuery(" from TipoIsencaoITBI where codigo = :codigo").setParameter("codigo", entity.getCodigo());
        if (!q.getResultList().isEmpty()) {
            entity.setCodigo(retornaUltimoCodigoLong());
        }
        super.salvarNovo(entity);
    }
}
