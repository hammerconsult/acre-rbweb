/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author andregustavo
 */
@Stateless
public class RelatorioReceitaOrcamentarioFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    public Boolean consultaFiltros(Long unidade, Integer exercicio) {
        String sql = "WITH rec (codigo, descricao, valorestimado, superior_id, tipo) AS"
                + " (SELECT c.codigo, c.descricao, re.valorestimado, c.superior_id, 'Filho'"
                + " FROM unidadeorganizacional uni"
                + " INNER JOIN programaPPA prog ON uni.id = prog.responsavel_id"
                + " INNER JOIN PPA ppa ON prog.ppa_id = ppa.id"
                + " INNER JOIN receitaexercicioppa re ON ppa.id = re.ppa_id"
                + " INNER JOIN conta c ON c.id = re.contadereceita_id"
                + " INNER JOIN conta cPai ON cPai.id = c.superior_id"
                + " INNER JOIN exercicio e ON e.id = re.exercicio_id"
                + " WHERE e.ano =:ex AND uni.id =:uni"
                + " AND re.valorestimado > 0"
                + " UNION ALL"
                + " SELECT c.codigo, c.descricao, rec.valorEstimado, c.superior_id, 'Pai'"
                + " FROM conta c, rec "
                + " WHERE c.id = rec.superior_id)"
                + " SELECT codigo, descricao, sum(valorEstimado) FROM rec GROUP BY codigo, descricao ORDER BY codigo";

        Query q = em.createNativeQuery(sql);
        q.setParameter("uni", unidade);
        q.setParameter("ex", exercicio);
        if (q.getResultList().isEmpty()) {
            return false;
        }
        return true;

    }
}
