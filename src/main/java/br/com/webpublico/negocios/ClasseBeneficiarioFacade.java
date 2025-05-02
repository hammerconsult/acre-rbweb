/*
 * Codigo gerado automaticamente em Thu Apr 05 08:47:08 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ClasseBeneficiario;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;

@Stateless
public class ClasseBeneficiarioFacade extends AbstractFacade<ClasseBeneficiario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClasseBeneficiarioFacade() {
        super(ClasseBeneficiario.class);
    }

    public int codigoSequencial() {
        String sql = "SELECT coalesce(max(to_number(codigo)),0)+1 FROM classebeneficiario";
        Query q = getEntityManager().createNativeQuery(sql);
        try {
            BigDecimal b = (BigDecimal) q.getSingleResult();
            return b.intValue();
        } catch (Exception e) {
            return 1;
        }
    }
}
