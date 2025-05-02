/*
 * Codigo gerado automaticamente em Thu Oct 06 11:09:26 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.FaixaReferenciaFP;
import br.com.webpublico.entidades.ReferenciaFP;
import br.com.webpublico.entidades.ValorReferenciaFP;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.enums.TipoReferenciaFP;
import br.com.webpublico.exception.FuncoesFolhaFacadeException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Stateless
public class ReferenciaFPFacade extends AbstractFacade<ReferenciaFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ReferenciaFPFacade() {
        super(ReferenciaFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ReferenciaFP recuperar(Object id) {
        ReferenciaFP rfp = em.find(ReferenciaFP.class, id);
        Hibernate.initialize(rfp.getValoresReferenciasFPs());
        Hibernate.initialize(rfp.getFaixasReferenciasFPs());
        Collections.sort(rfp.getValoresReferenciasFPs());
        Collections.sort(rfp.getFaixasReferenciasFPs());
        for (FaixaReferenciaFP faixaRef : rfp.getFaixasReferenciasFPs()) {
            Hibernate.initialize(faixaRef.getItensFaixaReferenciaFP());
            Collections.sort(faixaRef.getItensFaixaReferenciaFP());
        }
        return rfp;
    }

    public boolean existeCodigo(ReferenciaFP referenciaFP) {
        String hql = " from ReferenciaFP rfp where lower(trim(rfp.codigo)) = :codigoParametro ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("codigoParametro", referenciaFP.getCodigo().trim().toLowerCase());

        List<ReferenciaFP> lista = q.getResultList();

        if (lista.contains(referenciaFP)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }


    public List<ReferenciaFP> listaFiltrandoPorTipoDescricao(TipoReferenciaFP tipoReferenciaFP, String descricao) {
        Query q = em.createQuery(" from ReferenciaFP " +
            " where (LOWER(descricao) like :parametroDescricao " +
            "           or codigo like :parametroDescricao) " +
            " and tipoReferenciaFP = :parametroTipo  " +
            " order by codigo");
        q.setParameter("parametroTipo", tipoReferenciaFP);
        q.setParameter("parametroDescricao", "%" + descricao.toLowerCase() + "%");
        return q.getResultList();
    }

    public BigDecimal recuperaSalarioMinimoVigente() {
        String sql = "SELECT v.valor FROM valorreferenciafp v "
            + "INNER JOIN referenciafp r ON v.referenciafp_id = r.id "
            + "WHERE sysdate BETWEEN iniciovigencia "
            + "AND coalesce(FINALVIGENCIA, sysdate) "
            + "AND r.codigo = 5";

        Query q = em.createNativeQuery(sql);

        BigDecimal salarioMinimo = (BigDecimal) q.getSingleResult();

        if (salarioMinimo != null) {
            return salarioMinimo;
        }

        return new BigDecimal("0");
    }

    public ReferenciaFP listaFiltrandoPorTipoECodigo(TipoReferenciaFP tipoReferenciaFP, Integer codigo) {
        try {
            Query q = em.createQuery("from ReferenciaFP "
                + " where codigo = :codigo "
                + " and tipoReferenciaFP = :tipo", ReferenciaFP.class);

            q.setParameter("codigo", codigo + "");
            q.setParameter("tipo", tipoReferenciaFP);
            q.setMaxResults(1);
            ReferenciaFP r = (ReferenciaFP) q.getSingleResult();
            if (r == null) {
                //System.out.println("referencia null");
                return null;
            }
            return r;
        } catch (NoResultException nre) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ReferenciaFP> listaFiltrandoPorTipoECodigo(TipoReferenciaFP tipoReferenciaFP, String parte) {
        Query q = em.createQuery(" from ReferenciaFP where ((descricao like :parametroDescricao) or (codigo like :parametroDescricao)) and "
            + " tipoReferenciaFP = :parametroTipo ");
        q.setParameter("parametroTipo", tipoReferenciaFP);
        q.setParameter("parametroDescricao", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public BigDecimal getValorReferenciaFPPorCodigoVigente(String codigo, Date dataOperacao) {
        String hql = "select vr.valor                   " +
            "       from ReferenciaFP  r            " +
            " inner join r.valoresReferenciasFPs vr " +
            "      where r.codigo = :codigo         " +
            "        and :data between vr.inicioVigencia and coalesce(vr.finalVigencia, :data)";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        q.setParameter("data", dataOperacao);
        q.setMaxResults(1);
        BigDecimal resultado = BigDecimal.ZERO;
        try {
            resultado = (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
        }
        return resultado;
    }

    public BigDecimal recuperaSalarioMinimoVigente(Date inicioVigencia, Date finalVigencia) {
        String hql = "SELECT v.valor FROM ValorReferenciaFP v "
            + " INNER JOIN v.referenciaFP r  "
            + " WHERE coalesce(v.finalVigencia, :finalVigencia) = (select max(vr.finalVigencia) from ValorReferenciaFP vr " +
            "                where coalesce(vr.finalVigencia, :finalVigencia) between :inicioVigencia and :finalVigencia " +
            "                and v.id = vr.id) "
            + " AND r.codigo = 5 "
            + " order by v.inicioVigencia desc";

        Query q = em.createQuery(hql);
        q.setParameter("inicioVigencia", inicioVigencia);
        q.setParameter("finalVigencia", finalVigencia);

        List<BigDecimal> salarioMinimo = q.getResultList();

        if (salarioMinimo != null && !salarioMinimo.isEmpty()) {
            return salarioMinimo.get(0);
        }

        return new BigDecimal("0");
    }


    public BigDecimal valorRefenciaFP(int codigo) {
        String sql = "SELECT REFERENCIA.VALOR FROM REFERENCIAFP REFERENCIAFP" +
            " INNER JOIN ValorReferenciaFP REFERENCIA ON REFERENCIA.REFERENCIAFP_ID = REFERENCIAFP.ID" +
            " WHERE CODIGO = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public ValorReferenciaFP buscarValorRefenciaFPPorCodigo(int codigo) {
        String sql = "SELECT REFERENCIA.* FROM REFERENCIAFP REFERENCIAFP" +
            " INNER JOIN ValorReferenciaFP REFERENCIA ON REFERENCIA.REFERENCIAFP_ID = REFERENCIAFP.ID" +
            " WHERE CODIGO = :codigo";
        Query q = em.createNativeQuery(sql, ValorReferenciaFP.class);
        q.setParameter("codigo", codigo);
        if (!q.getResultList().isEmpty()) {
            return (ValorReferenciaFP) q.getResultList().get(0);
        }
        return null;
    }


    public ReferenciaFPFuncao obterReferenciaValorFP(EntidadePagavelRH ep, String codigo, Date dataReferencia) {
        ReferenciaFPFuncao obterValorReferencia = new ReferenciaFPFuncao();
        obterValorReferencia.setPercentual(0.0);
        obterValorReferencia.setValor(0.0);

        ValorReferenciaFP valorReferenciaFP;
        Query query = em.createQuery(" select valor from ValorReferenciaFP valor  "
            + " where valor.referenciaFP.codigo = :codigo "
            + " and (valor.referenciaFP.tipoReferenciaFP = :tipoReferenciaVP OR "
            + " valor.referenciaFP.tipoReferenciaFP = :tipoReferenciaVV) and "
            + " :dataVigencia >= valor.inicioVigencia and "
            + " :dataVigencia <= coalesce(valor.finalVigencia,:dataVigencia) "
            + " order by valor.valor asc ");
        try {
            query.setParameter("dataVigencia", dataReferencia);
            query.setParameter("tipoReferenciaVP", TipoReferenciaFP.VALOR_PERCENTUAL);
            query.setParameter("tipoReferenciaVV", TipoReferenciaFP.VALOR_VALOR);
            query.setParameter("codigo", codigo);

            List resultList = query.getResultList();
            if (resultList.isEmpty()) {
                return obterValorReferencia;
            }

            valorReferenciaFP = (ValorReferenciaFP) resultList.get(0);
            obterValorReferencia.setValor(valorReferenciaFP.getValor().doubleValue());
            obterValorReferencia.setTipoReferenciaFP(valorReferenciaFP.getReferenciaFP().getTipoReferenciaFP());

        } catch (Exception re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método obterReferenciaValorFP", re);
        }
        return obterValorReferencia;
    }

    public BigDecimal valorRefenciaFPVigente(int codigo, Date dataOperacao) {
        String sql = " SELECT VALOR.VALOR FROM REFERENCIAFP REFERENCIA " +
            " INNER JOIN VALORREFERENCIAFP VALOR ON VALOR.REFERENCIAFP_ID = REFERENCIA.ID " +
            " WHERE REFERENCIA.TIPOREFERENCIAFP = 'VALOR_PERCENTUAL' AND REFERENCIA.CODIGO = :codigo " +
            " and :data BETWEEN VALOR.INICIOVIGENCIA and coalesce(VALOR.FINALVIGENCIA, sysdate) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        q.setParameter("data", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public Date dataInicioAliquota(int codigo, Date dataOperacao) {
        String sql = " SELECT VALOR.INICIOVIGENCIA FROM REFERENCIAFP REFERENCIA " +
            " INNER JOIN VALORREFERENCIAFP VALOR ON VALOR.REFERENCIAFP_ID = REFERENCIA.ID " +
            " WHERE REFERENCIA.TIPOREFERENCIAFP = 'VALOR_PERCENTUAL' AND REFERENCIA.CODIGO = :codigo " +
            " and :data BETWEEN VALOR.INICIOVIGENCIA and coalesce(VALOR.FINALVIGENCIA, sysdate) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        q.setParameter("data", dataOperacao);
        if (!q.getResultList().isEmpty()) {
            return (Date) q.getResultList().get(0);
        }
        return null;
    }

    public List<ReferenciaFP> buscarReferenciasOrdenadasPorCodigo() {
        String sql = "select r.* from ReferenciaFP r order by to_number(r.codigo)";
        Query q = em.createNativeQuery(sql, ReferenciaFP.class);
        return q.getResultList();
    }
}
