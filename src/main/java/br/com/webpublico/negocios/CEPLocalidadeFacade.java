package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CEPLocalidade;
import br.com.webpublico.ws.model.ConsultaCEP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Terminal-2
 */
@Stateless
public class CEPLocalidadeFacade extends AbstractFacade<CEPLocalidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CEPLocalidadeFacade() {
        super(CEPLocalidade.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public CEPLocalidade salvarCEPLocalidade(CEPLocalidade cepLocalidade){
        return em.merge(cepLocalidade);
    }

    public CEPLocalidade buscarCEPLocalidadePeloPorLocalidadeAndUF(ConsultaCEP consultaCEP) {

        String sql = " select ceplocal.* from ceplocalidade ceplocal " +
            " where lower(ceplocal.nome) = :localidade " +
            " and uf_id = (select id from cepuf where lower(sigla) = :uf )";

        Query q = em.createNativeQuery(sql, CEPLocalidade.class);
        q.setParameter("localidade", consultaCEP.getLocalidade().toLowerCase().trim());
        q.setParameter("uf", consultaCEP.getUf().toLowerCase().trim());

        if (!q.getResultList().isEmpty()) {
            return (CEPLocalidade) q.getResultList().get(0);
        }
        return null;
    }


}
