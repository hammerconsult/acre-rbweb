/*
 * Codigo gerado automaticamente em Fri Sep 28 09:34:49 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ProvisaoSalario;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ProvisaoSalarioFacade extends AbstractFacade<ProvisaoSalario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public ProvisaoSalarioFacade() {
        super(ProvisaoSalario.class);
    }

    public String getUltimoNumero() {
        String sql = "SELECT max(to_number(cr.numero)) AS ultimoNumero FROM ProvisaoSalario cr";
        Query q = em.createNativeQuery(sql);
        List<BigDecimal> resultado = q.getResultList();
        return resultado.get(0) == null ? "1" : resultado.get(0).toString();
    }

    public boolean hasLancamentoNoExercicio(ProvisaoSalario provisaoSalario) {
        String sql = "SELECT *  FROM PROVISAOSALARIO WHERE TO_CHAR(TO_DATE(:dataProvisao, 'DD/MM/YYYY'), 'yyyy') = :ano";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", provisaoSalario.getExercicio().getAno());
        q.setParameter("dataProvisao", DataUtil.getDataFormatada(provisaoSalario.getDataProvisao()));
        return !q.getResultList().isEmpty();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
