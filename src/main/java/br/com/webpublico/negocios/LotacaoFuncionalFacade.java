/*
 * Codigo gerado automaticamente em Fri Aug 05 08:57:52 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class LotacaoFuncionalFacade extends AbstractFacade<LotacaoFuncional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public LotacaoFuncionalFacade() {
        super(LotacaoFuncional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void salvar(LotacaoFuncional entity) {
        entity = getLotacaoFuncionalComHistorico(entity);
        super.salvar(entity);
    }

    public LotacaoFuncional getLotacaoFuncionalComHistorico(LotacaoFuncional lf) {
        LotacaoFuncional lotacaoFuncionalAtualmentePersistida = getLotacaoFuncionalAtualmentePersistida(lf);
        lf.criarOrAtualizarAndAssociarHistorico(lotacaoFuncionalAtualmentePersistida);
        return lf;
    }

    private LotacaoFuncional getLotacaoFuncionalAtualmentePersistida(LotacaoFuncional lf) {
        if (lf.getId() != null) {
            return recuperar(lf.getId());
        }
        return lf;
    }

    public List<LotacaoFuncional> recuperaLotacaoFuncionalPorContratoFPComparandoDataCessao(ContratoFP contratoFP, Date dataCessao) {
        Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
            + " where lotacao.vinculoFP = :contratoFP and "
            + " (lotacao.inicioVigencia <= :dataCessao and coalesce(lotacao.finalVigencia,:dataCessao) >= :dataCessao) ");
        q.setParameter("dataCessao", dataCessao);
        q.setParameter("contratoFP", contratoFP);
        return q.getResultList();
    }

    public LotacaoFuncional recuperarLotacaoFuncionalVigentePorContratoFP(VinculoFP contratoFP, Date dataOperacao) {
        try {
            Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
                + " where lotacao.vinculoFP.id = :contratoFP and "
                + " (lotacao.inicioVigencia <= :dataCessao and coalesce(lotacao.finalVigencia,:dataCessao) >= :dataCessao) ");
            q.setMaxResults(1);
            q.setParameter("dataCessao", Util.getDataHoraMinutoSegundoZerado(dataOperacao));
            q.setParameter("contratoFP", contratoFP.getId());

            return (LotacaoFuncional) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<LotacaoFuncional> recuperaLotacaosFuncionalVigentePorContratoFP(VinculoFP contratoFP) {
        Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
            + " where lotacao.vinculoFP.id = :contratoFP and "
            + " (lotacao.inicioVigencia <= :dataCessao and coalesce(lotacao.finalVigencia,:dataCessao) >= :dataCessao) ");
        q.setParameter("dataCessao", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("contratoFP", contratoFP.getId());
        return q.getResultList();
    }

    public List<LotacaoFuncional> recuperaLotacaoFuncionalProvimento(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append("  from LotacaoFuncional lotacao");
        hql.append(" where lotacao.vinculoFP = :contrato");
        hql.append("   and :dataProvimento >= lotacao.inicioVigencia");
        hql.append(" order by lotacao.inicioVigencia");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);
        return q.getResultList();
    }

    public LotacaoFuncional recuperaLotacaoFuncionalVigente(ContratoFP contratoFP, Date data) {
        Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
            + " where lotacao.vinculoFP = :contratoFP and "
            + " (lotacao.inicioVigencia <= :dataCessao "
            + " and coalesce(lotacao.finalVigencia, :dataCessao) >= :dataCessao) ");
        q.setMaxResults(1);
        q.setParameter("dataCessao", data);
        q.setParameter("contratoFP", contratoFP);
        List<LotacaoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new LotacaoFuncional();
        }
        return lista.get(0);
    }

    public LotacaoFuncional recuperaLotacaoFuncionalInstituidor(ContratoFP contratoFP) {
        Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
            + " where lotacao.vinculoFP = :contratoFP order by lotacao.id desc");
        q.setMaxResults(1);
        q.setParameter("contratoFP", contratoFP);
        List<LotacaoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return new LotacaoFuncional();
        }
        return lista.get(0);
    }

    public String recuperaHierarquiaDaLotacao(UnidadeOrganizacional uo) {
        String sql = "SELECT concat(concat(codigo, ' - '), descricao) AS teste FROM vwhierarquiaadministrativa WHERE subordinada_id = :uo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("uo", uo.getId());

        if (q.getResultList() == null || q.getResultList().isEmpty()) {
            return "";
        }
        return (String) q.getResultList().get(0);
    }


    public LotacaoFuncional buscarLotacaoFuncionalVigente(VinculoFP vinculo, Date data) {
        Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
            + " where lotacao.vinculoFP = :contratoFP and "
            + " :dataReferencia between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia, :dataReferencia)  ");
        q.setMaxResults(1);
        q.setParameter("dataReferencia", data);
        q.setParameter("contratoFP", vinculo);
        List<LotacaoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return buscarUltimaLotacaoVigentePorVinculoFP(vinculo);
        }
        return lista.get(0);
    }

    public List<LotacaoFuncional> buscarLotacaoFuncionalPorExercicio(VinculoFP vinculo, Exercicio exercicio) {
        Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
            + " where lotacao.vinculoFP = :contratoFP and "
            + " :exercicio between extract(year from lotacao.inicioVigencia) and extract(year from (coalesce(lotacao.finalVigencia, sysdate))) " +
            " order by lotacao.inicioVigencia");

        q.setParameter("exercicio", exercicio.getAno());
        q.setParameter("contratoFP", vinculo);
        List<LotacaoFuncional> lista = q.getResultList();
        if (lista == null || lista.isEmpty()) {
            return Lists.newArrayList(buscarUltimaLotacaoVigentePorVinculoFP(vinculo));
        }
        return lista;
    }


    public LotacaoFuncional buscarUltimaLotacaoVigentePorVinculoFP(VinculoFP contratoFP) {
        try {
            String hql = "select lotacao from LotacaoFuncional lotacao " +
                " where lotacao.vinculoFP = :contratoFP " +
                " and lotacao.inicioVigencia = (select max(lot.inicioVigencia) from LotacaoFuncional lot where lot.vinculoFP = :contratoFP)";
            Query q = em.createQuery(hql);
            q.setMaxResults(1);
            q.setParameter("contratoFP", contratoFP);
            return (LotacaoFuncional) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public LotacaoFuncional buscarUltimaLotacaoPorVinculoFPOderById(VinculoFP contratoFP) {
        try {
            String hql = "select lotacao from LotacaoFuncional lotacao " +
                " where lotacao.vinculoFP = :contratoFP order by lotacao.id " ;
            Query q = em.createQuery(hql);
            q.setMaxResults(1);
            q.setParameter("contratoFP", contratoFP);
            return (LotacaoFuncional) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<LotacaoFuncional> recuperaLotacaoFuncionalPorContratoFP(VinculoFP contratoFP) {
        try {
            String hql = "select lotacao from LotacaoFuncional lotacao " +
                " where lotacao.vinculoFP = :contratoFP order by lotacao.inicioVigencia asc";
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            return q.getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public LotacaoFuncional recuperaLotacaoFuncionalVigentePorContratoFPMes(VinculoFP contratoFP, Date data) {
        Query q = em.createQuery(" select lotacao from LotacaoFuncional lotacao "
            + " where lotacao.vinculoFP.id = :contratoFP and "
            + "  to_date(to_char(:data,'mm/yyyy'),'mm/yyyy') between to_date(to_char(lotacao.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            "   and to_date(to_char(coalesce(lotacao.finalVigencia,:data),'mm/yyyy'),'mm/yyyy') order by lotacao.inicioVigencia desc");
        q.setMaxResults(1);
        q.setParameter("data", data);
        q.setParameter("contratoFP", contratoFP.getId());
        if (!q.getResultList().isEmpty()) {
            return (LotacaoFuncional) q.getSingleResult();
        }
        return null;
    }

    public HierarquiaOrganizacional recuperarHieraquiaPelaLotacaoFuncional(VinculoFP vinculofp, Date data) {
        Query q = em.createQuery(" select vw from LotacaoFuncional lotacao, HierarquiaOrganizacional  vw inner join lotacao.unidadeOrganizacional un "
            + " where lotacao.vinculoFP.id = :id and un.id = vw.subordinada.id " +
            "and vw.tipoHierarquiaOrganizacional = :tipo "
            + "  and :data between lotacao.inicioVigencia " +
            "   and coalesce(lotacao.finalVigencia,:data) and :data between  vw.inicioVigencia and coalesce(vw.fimVigencia,:data) ");
        q.setMaxResults(1);
        q.setParameter("data", data);
        q.setParameter("tipo", TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        q.setParameter("id", vinculofp.getId());
        if (!q.getResultList().isEmpty()) {
            return (HierarquiaOrganizacional) q.getResultList().get(0);
        }
        return null;
    }

    public LotacaoFuncional recuperaLotacaoFuncionalVigenteNoAno(VinculoFP vinculoFP, Integer ano) {
        Date dataFinal = DataUtil.getDia(31, 12, ano);
        String hql = "select lotacao from VinculoFP vinculo"
            + " inner join vinculo.lotacaoFuncionals lotacao"
            + " where vinculo = :vinculoFP"
            + " and (extract(year from lotacao.inicioVigencia) <= :ano" +
            "   and extract(year from coalesce(lotacao.finalVigencia, :dataFinal)) >= :ano)"
            + " order by lotacao.inicioVigencia desc ";
        Query q = em.createQuery(hql);
        q.setParameter("ano", ano);
        q.setParameter("dataFinal", dataFinal);
        q.setParameter("vinculoFP", vinculoFP);
        q.setMaxResults(1);

        try {
            return (LotacaoFuncional) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Date buscarDataUltimaLotacaoFuncionalPorDataEVinculoFP(Date data, VinculoFP vinculo) {
        String sql = "select v.FINALVIGENCIA from LOTACAOFUNCIONAL lot " +
            " inner join vinculoFP v on lot.VINCULOFP_ID = v.ID " +
            " where " +
            " v.id= :vinculo and " +
            " to_date(:data, 'dd/MM/yyyy') BETWEEN lot.INICIOVIGENCIA AND COALESCE(lot.finalvigencia, to_date(:data, 'dd/MM/yyyy')) " +
            " order by lot.FINALVIGENCIA";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("vinculo", vinculo.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (Date) resultList.get(0);
        }
        return buscarDataUltimaLotacaoFuncionalPorData(vinculo);
    }

    public Date buscarDataUltimaLotacaoFuncionalPorData(VinculoFP vinculo) {
        String sql = "select max (lot.FINALVIGENCIA) from LOTACAOFUNCIONAL lot " +
            " inner join vinculoFP v on lot.VINCULOFP_ID = v.ID " +
            " where v.ID = :vinculo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculo", vinculo.getId());
        return (Date) q.getResultList().get(0);
    }

    public boolean existeLotacaoFuncionalVigenteNaData(Date data, HierarquiaOrganizacional hierarquiaOrganizacional) {
        String sql = " select l.id from lotacaofuncional l " +
            " inner join vinculofp v on v.id = l.vinculofp_id " +
            " inner join hierarquiaorganizacional ho on ho.subordinada_id = l.unidadeorganizacional_id " +
            " where to_date(:data, 'dd/mm/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            " and to_date(:data, 'dd/mm/yyyy') between v.iniciovigencia and coalesce(v.finalvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            " and to_date(:data, 'dd/mm/yyyy') between l.iniciovigencia and coalesce(l.finalvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            " and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            " and ho.id = :hierarquiaId ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("hierarquiaId", hierarquiaOrganizacional.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(1);
        List resultado = q.getResultList();
        if (resultado != null) {
            return !resultado.isEmpty();
        }
        return false;
    }
}
