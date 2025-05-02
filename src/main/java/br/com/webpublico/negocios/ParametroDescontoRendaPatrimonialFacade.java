/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ItemParametroDescontoRendaPatrimonial;
import br.com.webpublico.entidades.ParametroDescontoRendaPatrimonial;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;

/**
 * @author Andre
 */
@Stateless
public class ParametroDescontoRendaPatrimonialFacade extends AbstractFacade<ParametroDescontoRendaPatrimonial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ParametroDescontoRendaPatrimonialFacade() {
        super(ParametroDescontoRendaPatrimonial.class);
    }

    @Override
    public ParametroDescontoRendaPatrimonial recuperar(Object id) {
        ParametroDescontoRendaPatrimonial p = em.find(ParametroDescontoRendaPatrimonial.class, id);
        p.getItensParametroDescontoRendaPatrimonial().size();
        return p;
    }

    public Long sugereSequenciaItemParametroDescontoRendaPatrimonial(ParametroDescontoRendaPatrimonial parametroDescontoRendaPatrimonial) {
        if (!parametroDescontoRendaPatrimonial.getItensParametroDescontoRendaPatrimonial().isEmpty()) {
            Collections.sort(parametroDescontoRendaPatrimonial.getItensParametroDescontoRendaPatrimonial());
            ItemParametroDescontoRendaPatrimonial i =
                    parametroDescontoRendaPatrimonial.getItensParametroDescontoRendaPatrimonial().get(parametroDescontoRendaPatrimonial.getItensParametroDescontoRendaPatrimonial().size() - 1);

            return i.getSequencia() + 1l;
        }
        return 1l;
    }

    public Long sugereCodigoParametroDescontoRendaPatrimonial() {
        Query q = em.createQuery(" select max(p.codigo) from ParametroDescontoRendaPatrimonial p");
        try {
            Long codigo = (Long) q.getSingleResult();
            codigo += 1l;
            return codigo;
        } catch (Exception e) {
            return 1l;
        }
    }

    public List<ParametroDescontoRendaPatrimonial> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from ParametroDescontoRendaPatrimonial p "
                + " where lower(cast(p.codigo as string)) like :filtro or lower(p.descricao) like:filtro ");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }
}
