/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FaixaAlvara;
import br.com.webpublico.entidades.ItemFaixaAlvara;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Heinz
 */
@Stateless
public class FaixaAlvaraFacade extends AbstractFacade<FaixaAlvara> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FaixaAlvaraFacade() {
        super(FaixaAlvara.class);
    }

    @Override
    public FaixaAlvara recuperar(Object id) {
        FaixaAlvara fa = em.find(FaixaAlvara.class, id);
        fa.itemFaixaAlvaras.size();
        return fa;
    }

    public ItemFaixaAlvara recuperarPorClassificacaoArea(ClassificacaoAtividade classificacaoAtividade, TipoAlvara tipoAlvara, Exercicio exercicio, BigDecimal areaUtil) {
        return recuperarPorClassificacaoArea(classificacaoAtividade, tipoAlvara, null, exercicio, areaUtil);
    }

    public ItemFaixaAlvara recuperarPorClassificacaoArea(ClassificacaoAtividade classificacaoAtividade, TipoAlvara tipoAlvara, GrauDeRiscoDTO grauDeRisco, Exercicio exercicio, BigDecimal areaUtil) {
        if (classificacaoAtividade == null) {
            return null;
        }
        StringBuilder hql = new StringBuilder("select ifa from ItemFaixaAlvara ifa join ifa.faixaAlvara fa ")
                .append(" where (fa.classificacaoAtividade = :classificacao and fa.exercicioVigencia = :exercicio) ")
                .append(" and (:areautil >= ifa.areaMetroInicial and :areautil <= ifa.areaMetroFinal) ")
                .append(" and ifa.tipoAlvara = :tipoAlvara ")
                .append(" and ifa.grauDeRisco = :grauDeRisco");
        Query q = em.createQuery(hql.toString(), ItemFaixaAlvara.class);
        q.setParameter("classificacao", classificacaoAtividade);
        q.setParameter("exercicio", exercicio);
        q.setParameter("areautil", areaUtil);
        q.setParameter("tipoAlvara", tipoAlvara);
        if (grauDeRisco != null) {
            q.setParameter("grauDeRisco", grauDeRisco);
        } else {
            q.setParameter("grauDeRisco", GrauDeRiscoDTO.BAIXO_RISCO_A);
        }
        List<ItemFaixaAlvara> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    public List<FaixaAlvara> buscarFaixasPorExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select * from FaixaAlvara where exercicioVigencia_id = :idExercicio", FaixaAlvara.class);
        q.setParameter("idExercicio", exercicio.getId());
        return q.getResultList();
    }
}
