/*
 * Codigo gerado automaticamente em Thu Oct 06 09:05:34 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AtoDePessoal;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AtoDePessoalFacade extends AbstractFacade<AtoDePessoal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtoDePessoalFacade() {
        super(AtoDePessoal.class);
    }

    public List<AtoDePessoal> listaFiltrandoAtoLegal(String s) {
        try {
            Query q = em.createNativeQuery(" SELECT atoDePessoal.id FROM ATODEPESSOAL atoDePessoal INNER JOIN ATOLEGAL "
                    + " atoLegal ON atoDePessoal.ATOLEGAL_ID = atoLegal.ID"
                    + " WHERE lower(atoLegal.nome) LIKE :filtro "
                    + " OR lower(atoLegal.numero) LIKE :filtro OR "
                    + " lower(atoLegal.TIPOATOLEGAL) LIKE :filtro "
                    + " AND rownum <=10 ");
            q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
            q.setMaxResults(9);
            List<BigDecimal> ids = q.getResultList();
            List<AtoDePessoal> atos = new ArrayList<>();
            for (BigDecimal id : ids) {
                atos.add(em.find(AtoDePessoal.class, id.longValue()));
            }
            return atos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
