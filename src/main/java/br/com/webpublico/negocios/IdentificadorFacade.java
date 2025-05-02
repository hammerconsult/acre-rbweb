package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Identificador;
import br.com.webpublico.enums.Situacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by mateus on 31/07/17.
 */
@Stateless
public class IdentificadorFacade extends AbstractFacade<Identificador> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public IdentificadorFacade() {
        super(Identificador.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Identificador> buscarIdentificadoresPorNumeroOrDescricao(String filtro) {
        String sql = " select ident.* from identificador ident " +
            " where (ident.numero like :filtro or lower(ident.descricao) like :filtro) " +
            "   and ident.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, Identificador.class);
        q.setParameter("situacao", Situacao.ATIVO.name());
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
