/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ParametroMalaDireta;
import br.com.webpublico.enums.TipoMalaDireta;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParametroMalaDiretaFacade extends AbstractFacade<ParametroMalaDireta> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ParametroMalaDiretaFacade() {
        super(ParametroMalaDireta.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ParametroMalaDireta> completarParametroPorTipo(String parte, TipoMalaDireta tipoMalaDireta) {
        if (tipoMalaDireta == null) {
            return Lists.newArrayList();
        }
        String hql = "select obj from ParametroMalaDireta obj " +
            " where lower(obj.descricao) like :filtro and obj.tipoMalaDireta = :tipoMalaDireta ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoMalaDireta", tipoMalaDireta);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();

    }
}
