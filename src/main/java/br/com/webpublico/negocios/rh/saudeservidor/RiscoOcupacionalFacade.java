package br.com.webpublico.negocios.rh.saudeservidor;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


@Stateless
public class RiscoOcupacionalFacade extends AbstractFacade<RiscoOcupacional> {

    public RiscoOcupacionalFacade() {
        super(RiscoOcupacional.class);
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public RiscoOcupacional recuperar(Object id) {
        RiscoOcupacional risco = super.recuperar(id);
        Hibernate.initialize(risco.getItemAgenteNovico());
        Hibernate.initialize(risco.getItemRegistroAmbiental());
        return risco;
    }

    public List<RiscoOcupacional> buscarRiscoOcupacionalPorEntidade(ConfiguracaoEmpregadorESocial empregadorESocial, String parte) {
        String sql = "select RISCOOCUPACIONAL.* from RISCOOCUPACIONAL " +
            " inner join vinculofp on RISCOOCUPACIONAL.VINCULOFP_ID = VINCULOFP.ID " +
            " inner join matriculafp on VINCULOFP.MATRICULAFP_ID = MATRICULAFP.ID" +
            " inner join pessoafisica pf on MATRICULAFP.PESSOA_ID = pf.ID" +
            " where (lower(pf.nome) like :filtro) " +
            " and vinculofp.unidadeOrganizacional_id in (:unidades) ";

        Query q = em.createNativeQuery(sql, RiscoOcupacional.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<RiscoOcupacional> buscarRiscoOcupacionalPorVinculoFP(VinculoFP vinculoFP, String parte) {
        String sql = "select RISCOOCUPACIONAL.* from RISCOOCUPACIONAL " +
            " inner join vinculofp on RISCOOCUPACIONAL.VINCULOFP_ID = VINCULOFP.ID " +
            " inner join matriculafp on VINCULOFP.MATRICULAFP_ID = MATRICULAFP.ID" +
            " inner join pessoafisica pf on MATRICULAFP.PESSOA_ID = pf.ID" +
            " where vinculofp.id = :idVInculo and (lower(pf.nome) like :filtro) ";

        Query q = em.createNativeQuery(sql, RiscoOcupacional.class);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("idVInculo", vinculoFP.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }
}
