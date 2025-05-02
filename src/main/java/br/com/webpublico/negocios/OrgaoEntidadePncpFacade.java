package br.com.webpublico.negocios;

import br.com.webpublico.entidades.OrgaoEntidadePncp;
import br.com.webpublico.entidades.OrgaoEntidadeUnidadePncp;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class OrgaoEntidadePncpFacade extends AbstractFacade<OrgaoEntidadePncp> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public OrgaoEntidadePncpFacade() {
        super(OrgaoEntidadePncp.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public OrgaoEntidadePncp recuperar(Object id) {
        OrgaoEntidadePncp entity = super.recuperar(id);
        Hibernate.initialize(entity.getUnidades());
        return entity;
    }

    public Boolean hasEntidadePncpCadastradaParaEntidade(OrgaoEntidadePncp orgaoEntidadePncp) {
        String sql = "select ent.* from orgaoentidadepncp ent where ent.entidade_id = :idEntidade ";
        sql += orgaoEntidadePncp.getId() != null ? " and ent.id <> :idEntidadePncp " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEntidade", orgaoEntidadePncp.getEntidade().getId());
        if (orgaoEntidadePncp.getId() != null) {
            q.setParameter("idEntidadePncp", orgaoEntidadePncp.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public OrgaoEntidadeUnidadePncp buscarUnidadePncp(OrgaoEntidadePncp entity, UnidadeOrganizacional unidade) {
        String sql = " select unid.* from orgaoentidadeunidadepncp unid " +
            "          where unid.unidadeorganizacional_id = :idUnidade ";
        sql += entity.getId() != null ? " and unid.orgaoentidadepncp_id <> :idEntidadePncp " : "";
        Query q = em.createNativeQuery(sql, OrgaoEntidadeUnidadePncp.class);
        q.setParameter("idUnidade", unidade.getId());
        if (entity.getId() != null) {
            q.setParameter("idEntidadePncp", entity.getId());
        }
        try {
            return (OrgaoEntidadeUnidadePncp) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Boolean isOrgaoEntidadeIntegradoPncp(Long idOrigem) {
        String sql = " select entpncp.* from orgaoentidadepncp entpncp " +
            "       inner join pessoajuridica pj on pj.id = entpncp.pessoajuridica_id " +
            "       inner join entidade ent on ent.pessoajuridica_id = pj.id " +
            "       inner join unidadeorganizacional unid on unid.entidade_id = ent.id " +
            "       inner join hierarquiaorganizacional vw on vw.subordinada_id = unid.id " +
            "      where sysdate between trunc(vw.iniciovigencia) and coalesce(trunc(vw.fimvigencia), sysdate) " +
            "       and vw.tipohierarquiaorganizacional = :tipoHoAdm " +
            "       and entpncp.integrado = :integrado " +
            "       and ent.id = :idOrigem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoHoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("idOrigem", idOrigem);
        q.setParameter("integrado", Boolean.TRUE);
        return !q.getResultList().isEmpty();
    }

    public Boolean isUnidadeIntegradoPncp(Long idOrigem) {
        String sql = " select unidpncp.* from orgaoentidadeunidadepncp unidpncp " +
            "           inner join hierarquiaorganizacional vw on vw.subordinada_id = unidpncp.unidadeorganizacional_id " +
            "          where sysdate between trunc(vw.iniciovigencia) and coalesce(trunc(vw.fimvigencia), sysdate) " +
            "           and vw.tipohierarquiaorganizacional = :tipoHoAdm " +
            "           and unidpncp.integrado = :integrado " +
            "           and vw.id = :idOrigem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoHoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("idOrigem", idOrigem);
        q.setParameter("integrado", Boolean.TRUE);
        return !q.getResultList().isEmpty();
    }
}
