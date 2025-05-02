/*
 * Codigo gerado automaticamente em Tue Mar 15 15:53:12 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CEP;
import br.com.webpublico.entidades.Logradouro;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class CEPFacade extends AbstractFacade<CEP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CEPFacade() {
        super(CEP.class);
    }

    public List<CEP> listaCepPorLogradouro(Logradouro logradouro) {
        String sql = " select cep.* " +
                     "    from cep cep " +
                     "   inner join logradouro_cep l_cep on l_cep.cep_id = cep.id " +
                     " where l_cep.logradouro_id = :logradouro_id ";
        Query q = getEntityManager().createNativeQuery(sql, CEP.class);
        q.setParameter("logradouro_id", logradouro.getId());
        if(!q.getResultList().isEmpty() && q.getResultList().size() > 0){
            //System.out.println("RETORNOU CEPS");
            return q.getResultList();
        }
        return new ArrayList();
    }
}
