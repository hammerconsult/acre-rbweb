/*
 * Codigo gerado automaticamente em Wed Feb 08 11:13:38 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemLaudo;
import br.com.webpublico.entidades.Laudo;
import br.com.webpublico.entidades.ValorUnidadeOrganizacional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class LaudoFacade extends AbstractFacade<Laudo> {

    private static final Logger logger = LoggerFactory.getLogger(LaudoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LaudoFacade() {
        super(Laudo.class);
    }

    @Override
    public Laudo recuperar(Object id) {
        Laudo laudo = em.find(Laudo.class, id);
        laudo.getItensLaudos().size();

        for (ItemLaudo item : laudo.getItensLaudos()) {
            item.getValoresUnidadesOrganizacionals().size();
        }

        return laudo;
    }

    public boolean temUnidadeMesmoNaturezaAtividade(ValorUnidadeOrganizacional valorUo) {
        Query q = em.createQuery("from ValorUnidadeOrganizacional uo where uo.unidadeOrganizacional = :unidade and uo.tipoNaturezaAtividadeFP = :tipo");
        q.setParameter("unidade", valorUo.getUnidadeOrganizacional());
        q.setParameter("tipo", valorUo.getTipoNaturezaAtividadeFP());
        return q.getResultList().isEmpty();
    }

    public Laudo find(String codigo) {
        Query q = em.createQuery("from Laudo l where l.numeroLaudo = :codigo");
        q.setParameter("codigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (Laudo) q.getSingleResult();
    }

    @Override
    public void salvar(Laudo entity) {
        try {

            getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    @Override
    public void salvarNovo(Laudo entity) {
        getEntityManager().persist(entity);
    }
}
