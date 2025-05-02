/*
 * Codigo gerado automaticamente em Mon Apr 04 16:43:56 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ServicoUrbano;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ServicoUrbanoFacade extends AbstractFacade<ServicoUrbano> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ServicoUrbanoFacade() {
        super(ServicoUrbano.class);
    }

    public boolean jaExisteNome(ServicoUrbano servicoUrbano) {
        String hql = "from ServicoUrbano a where a.nome = :nome";
        if (servicoUrbano.getId() != null) {
            hql += " and a != :servico";
        }
        Query q = em.createQuery(hql);
        q.setParameter("nome", servicoUrbano.getNome());
        if (servicoUrbano.getId() != null) {
            q.setParameter("servico", servicoUrbano);
        }
        return (!q.getResultList().isEmpty());
    }

    public boolean jaExisteIdentificacao(ServicoUrbano servicoUrbano) {
        String hql = "from ServicoUrbano a where a.identificacao = :identificacao";
        if (servicoUrbano.getId() != null) {
            hql += " and a != :servico";
        }
        Query q = em.createQuery(hql);
        q.setParameter("identificacao", servicoUrbano.getIdentificacao());
        if (servicoUrbano.getId() != null) {
            q.setParameter("servico", servicoUrbano);
        }
        return (!q.getResultList().isEmpty());
    }

    public ServicoUrbano recuperaPorIdentificador(String identificacao) {
        String hql = "from ServicoUrbano a where a.identificacao = :identificacao";
        Query q = em.createQuery(hql);
        q.setParameter("identificacao", identificacao);
        if (!q.getResultList().isEmpty()) {
            return (ServicoUrbano) q.getResultList().get(0);
        }

        return null;
    }
}
