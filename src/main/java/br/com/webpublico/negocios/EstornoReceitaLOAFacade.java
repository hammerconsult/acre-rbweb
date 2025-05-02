/*
 * Codigo gerado automaticamente em Wed Sep 05 10:41:50 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EstornoReceitaLOA;
import br.com.webpublico.entidades.ReceitaLOA;
import br.com.webpublico.entidades.ReceitaLOAFonte;
import br.com.webpublico.enums.TipoSaldoReceitaORC;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class EstornoReceitaLOAFacade extends AbstractFacade<EstornoReceitaLOA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private SaldoReceitaORCFacade saldoReceitaORCFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EstornoReceitaLOAFacade() {
        super(EstornoReceitaLOA.class);
    }

    public ReceitaLOAFacade getReceitaLOAFacade() {
        return receitaLOAFacade;
    }

    public String getUltimoNumero() {
        String sql = "SELECT ERL.NUMERO FROM ESTORNORECEITALOA ERL ORDER BY TO_NUMBER(ERL.NUMERO) DESC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (String) q.getSingleResult();
        } else {
            return "0";
        }
    }

    public boolean existeEstornoReceitaLOAComNumero(String numero) {
        String sql = "SELECT * FROM estornoreceitaloa WHERE numero = :numero";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", numero);
        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public SaldoReceitaORCFacade getSaldoReceitaORCFacade() {
        return saldoReceitaORCFacade;
    }

    @Override
    public void salvarNovo(EstornoReceitaLOA entity) {
        entity.setReceitaLOA((ReceitaLOA) receitaLOAFacade.recuperar(entity.getReceitaLOA().getId()));
        for (ReceitaLOAFonte receitaLOAFonte : entity.getReceitaLOA().getReceitaLoaFontes()) {
            BigDecimal cem = new BigDecimal("100");
            //System.out.println("cem..: " + cem);
            //System.out.println("per..: " + receitaLOAFonte.getPercentual());
            //System.out.println("valor: " + entity.getValor());
            BigDecimal valor = entity.getValor().multiply(receitaLOAFonte.getPercentual().divide(cem));
            saldoReceitaORCFacade.geraSaldo(TipoSaldoReceitaORC.ESTORNORECEITALOA, entity.getDataEstorno(), entity.getReceitaLOA().getEntidade(), entity.getReceitaLOA().getContaDeReceita(), receitaLOAFonte.getDestinacaoDeRecursos().getFonteDeRecursos(), valor);
        }
        super.salvarNovo(entity);
    }
}
