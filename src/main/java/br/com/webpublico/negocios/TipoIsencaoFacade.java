/*
 * Codigo gerado automaticamente em Wed Jul 04 10:24:50 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoIsencao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class TipoIsencaoFacade extends AbstractFacade<TipoIsencao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoIsencaoFacade() {
        super(TipoIsencao.class);
    }

    public boolean verificaExistenciaNome(TipoIsencao selecionado) {
        String hql = "select ti from TipoIsencao ti where lower(ti.nome) = :nome";
        Query q = em.createQuery(hql, TipoIsencao.class);
        q.setParameter("nome", selecionado.getNome().toLowerCase());
        if (q.getResultList().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void remover(TipoIsencao entity) {
//        super.remover(entity);
        //System.out.println("veio aqui");
        entity = em.merge(entity);
        em.remove(entity);

    }


}
