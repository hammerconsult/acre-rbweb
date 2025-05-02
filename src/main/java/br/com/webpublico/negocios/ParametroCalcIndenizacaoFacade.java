/*
 * Codigo gerado automaticamente em Wed Feb 15 16:56:26 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroCalcIndenizacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ParametroCalcIndenizacaoFacade extends AbstractFacade<ParametroCalcIndenizacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroCalcIndenizacaoFacade() {
        super(ParametroCalcIndenizacao.class);
    }

    public boolean existeCodigo(ParametroCalcIndenizacao parametroCalcIndenizacao) {
        String hql = " from ParametroCalcIndenizacao p where p.codigo = :codigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigo", parametroCalcIndenizacao.getCodigo());

        List<ParametroCalcIndenizacao> lista = new ArrayList<ParametroCalcIndenizacao>();
        lista = q.getResultList();

        if (lista.contains(parametroCalcIndenizacao)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }
}
