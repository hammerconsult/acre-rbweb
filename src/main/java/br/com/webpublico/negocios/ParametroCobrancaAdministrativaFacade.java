package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 09/08/13
 * Time: 17:49
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ParametroCobrancaAdministrativaFacade extends AbstractFacade<ParametrosCobrancaAdministrativa> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public ParametroCobrancaAdministrativaFacade() {
        super(ParametrosCobrancaAdministrativa.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialPorTipo(String parte, TipoCadastroDoctoOficial tipo, ModuloTipoDoctoOficial moduloTipoDoctoOficial) {
        String hql = " select tipo from TipoDoctoOficial tipo where tipo.moduloTipoDoctoOficial = :moduloTipoDoctoOficial"
                + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)"
                + " and tipo.tipoCadastroDoctoOficial = :tipo";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("tipo", tipo);
        consulta.setParameter("moduloTipoDoctoOficial", moduloTipoDoctoOficial);
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    public ParametrosCobrancaAdministrativa parametrosCobrancaAdministrativaPorExercicio(Exercicio exercicio) {

        String hql = "from ParametrosCobrancaAdministrativa where exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        List<ParametrosCobrancaAdministrativa> listaReturn = q.getResultList();
        if (!listaReturn.isEmpty()) {
            return listaReturn.get(0);
        }
        return null;
    }
}
