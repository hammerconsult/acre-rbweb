/*
 * Codigo gerado automaticamente em Tue Feb 07 09:41:47 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MovimentoCAGED;
import br.com.webpublico.enums.TipoCAGED;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MovimentoCAGEDFacade extends AbstractFacade<MovimentoCAGED> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimentoCAGEDFacade() {
        super(MovimentoCAGED.class);
    }

    public boolean existeCodigo(MovimentoCAGED movimentoCAGED) {
        String hql = " from MovimentoCAGED movimento where movimento.codigo = :parametroCodigo "
                + " and movimento.tipoCAGED = :parametroTipo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", movimentoCAGED.getCodigo());
        q.setParameter("parametroTipo", movimentoCAGED.getTipoCAGED());

        List<MovimentoCAGED> lista = new ArrayList<MovimentoCAGED>();
        lista = q.getResultList();

        if (lista.contains(movimentoCAGED)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<MovimentoCAGED> listaMovimentoPorTipo(TipoCAGED tipoCAGED) {
        try {

            Query q = em.createQuery(" from MovimentoCAGED movimento "
                    + " where movimento.tipoCAGED = :parametroTipo "
                    + " order by movimento.codigo, movimento.descricao ");
            q.setParameter("parametroTipo", tipoCAGED);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
