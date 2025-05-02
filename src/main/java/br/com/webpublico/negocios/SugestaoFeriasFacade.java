/*
 * Codigo gerado automaticamente em Fri Oct 28 14:20:16 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.SugestaoFerias;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.*;

@Stateless
public class SugestaoFeriasFacade extends AbstractFacade<SugestaoFerias> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SugestaoFeriasFacade() {
        super(SugestaoFerias.class);
    }

    @Override
    public SugestaoFerias recuperar(Object id) {
        SugestaoFerias sf = super.recuperar(id);
        sf.getListAprovacaoFerias().size();
        return sf;
    }

    public SugestaoFerias buscaFiltrandoPeriodoAquisitivoFL(PeriodoAquisitivoFL periodo) {
        String hql = " from SugestaoFerias sf where sf.periodoAquisitivoFL = :periodo and sf.sugestaoReprogramacao is null";

        try {
            Query q = em.createQuery(hql);
            q.setParameter("periodo", periodo);
            SugestaoFerias sf = (SugestaoFerias) q.getSingleResult();
            try {
                sf.getListAprovacaoFerias().size();
            } catch (NullPointerException npe) {
                return sf;
            }

            return sf;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<SugestaoFerias> findSugestoesPorPeriodoAquisitivoFl(PeriodoAquisitivoFL periodo) {
        String hql = " from SugestaoFerias sf where sf.periodoAquisitivoFL = :periodo";
        Query q = em.createQuery(hql);
        q.setParameter("periodo", periodo);
        return q.getResultList();

    }

    public List<PeriodoAquisitivoFL> recuperarPeriodosAquisitivos(ContratoFP c, HierarquiaOrganizacional hierarquia, Date aPartirDe, Date finalFerias, StatusPeriodoAquisitivo situacao, Boolean status, Boolean programadas) {
        String where = "";
        String join = "";

        if (status != null) {
            if (status.equals(Boolean.TRUE)) {
                join += " inner join p.sugestaoFerias sf ";
                join += " inner join sf.listAprovacaoFerias af ";
                where += " and af.aprovado is true";
            }
            if (status.equals(Boolean.FALSE)) {
                join += " left join p.sugestaoFerias sf ";
                join += " left join sf.listAprovacaoFerias af ";
                where += " and (sf is empty or sf.listAprovacaoFerias is empty or af.aprovado is false)";
            }
        }

        if (programadas != null) {
            if (programadas.equals(Boolean.TRUE)) {
                join += " inner join p.sugestaoFerias sf ";
                where += " and sf.dataInicio >= :aPartirDe ";
            }
            if (programadas.equals(Boolean.FALSE)) {
                join += " left join p.sugestaoFerias sf";
                where += " and (p.sugestaoFerias is empty or sf.dataInicio is null)";
            }
        }

        if (hierarquia != null) {
            where += "        and ho.codigo like :codigo";
        }


        String hql = "select p from PeriodoAquisitivoFL p, VwHierarquiaAdministrativa ho" +
            " inner join p.contratoFP c" +
            " inner join c.lotacaoFuncionals lot " +
            join +
            "      where ho.subordinadaId = lot.unidadeOrganizacional.id" +
            "        and coalesce(c.finalVigencia, :dtOperacao) between coalesce(lot.inicioVigencia, :dtOperacao)" +
            "        and coalesce(lot.finalVigencia, coalesce(c.finalVigencia, :dtOperacao))" +
            where;

        hql += c != null ? " and c = :contrato" : "";
        hql += situacao != null ? " and p.status = :situacaoFerias" : "";
        hql += finalFerias != null ? " and sf.dataInicio <= :finalFerias" : "";

        hql += " order by  p.contratoFP.matriculaFP.pessoa.nome asc, p.inicioVigencia desc, p.finalVigencia desc";

        Query q = em.createQuery(hql);

        q.setParameter("dtOperacao", UtilRH.getDataOperacao());
        if (hierarquia != null) {
            q.setParameter("codigo", hierarquia.getCodigoSemZerosFinais() + "%");
        }

        if (programadas != null && programadas.equals(Boolean.TRUE)) {
            q.setParameter("aPartirDe", aPartirDe);
        }

        if (c != null) {
            q.setParameter("contrato", c);
        }

        if (situacao != null) {
            q.setParameter("situacaoFerias", situacao);
        }

        if (finalFerias != null) {
            q.setParameter("finalFerias", finalFerias);
        }

        List<PeriodoAquisitivoFL> periodos = q.getResultList();
        periodos = new ArrayList(new HashSet(periodos));
        for (PeriodoAquisitivoFL p : periodos) {
            try {
                p.getSugestaoFerias().getListAprovacaoFerias().size();
            } catch (NullPointerException npe) {
            }
        }

        Collections.sort(periodos, new Comparator<PeriodoAquisitivoFL>() {
            @Override
            public int compare(PeriodoAquisitivoFL o1, PeriodoAquisitivoFL o2) {
                return o2.getInicioVigencia().compareTo(o1.getInicioVigencia());
            }
        });

        return periodos;
    }

    public List<SugestaoFerias> recuperarSugestaoFeriasPorContrato(ContratoFP contrato, HierarquiaOrganizacional ho, Boolean aprovada) {
        String where = contrato != null ? " and c = :contrato" : "";
        String join = "";

        if (aprovada != null) {
            if (aprovada.equals(Boolean.TRUE)) {
                join += " inner join sf.listAprovacaoFerias af ";
                where += " and af.aprovado is true";
            }
            if (aprovada.equals(Boolean.FALSE)) {
                join += " left join sf.listAprovacaoFerias af ";
                where += " and (sf.listAprovacaoFerias is empty or af.aprovado is false)";
            }
        }

        if (ho != null) {
            where += " and ho.subordinadaId = l.unidadeOrganizacional.id" +
                " and ho.codigo like :codigo";
        }

        String hql = " select distinct sf from SugestaoFerias sf, VwHierarquiaAdministrativa ho" +
            " inner join sf.periodoAquisitivoFL p " +
            " inner join p.contratoFP c" +
            " inner join c.lotacaoFuncionals l"
            + join +
            "      where sf.sugestaoReprogramacao is null"
            + where +
            "        and sf.dataInicio != null" +
            "        and :dtOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia, :dtOperacao)" +
            "   ";
        TypedQuery q = em.createQuery(hql, SugestaoFerias.class);
        if (ho != null) {
            q.setParameter("codigo", ho.getCodigoSemZerosFinais() + "%");
        }

        q.setParameter("dtOperacao", UtilRH.getDataOperacao());
        if (contrato != null) {
            q.setParameter("contrato", contrato);
        }

        List<SugestaoFerias> sf = q.getResultList();

        Collections.sort(sf, new Comparator<SugestaoFerias>() {
            @Override
            public int compare(SugestaoFerias o1, SugestaoFerias o2) {
                return o2.getPeriodoAquisitivoFL().getInicioVigencia().compareTo(o1.getPeriodoAquisitivoFL().getInicioVigencia());
            }
        });

        for (SugestaoFerias s : sf) {
            s.getListAprovacaoFerias().size();
        }
        return sf;
    }

    public SugestaoFerias merge(SugestaoFerias sf) {
        sf = em.merge(sf);
        return sf;
    }

}
