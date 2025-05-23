package br.com.webpublico.negocios.contabil;

import br.com.webpublico.entidades.ReprocessamentoContabilHistorico;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Stateless
public class ReprocessamentoContabilHistoricoFacade extends AbstractFacade<ReprocessamentoContabilHistorico> implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ReprocessamentoContabilHistoricoFacade() {
        super(ReprocessamentoContabilHistorico.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (Exception nre) {
            return 0L;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Object[] filtrarComContadorDeRegistros(String sql, String sqlCount, int inicio, int max) throws Exception {
        Query consulta = em.createNativeQuery(sql);
        Query consultaCount = em.createNativeQuery(sqlCount);
        Long count = 0L;
        List<ReprocessamentoContabilHistorico> resultado = Lists.newArrayList();
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }
            List<Object[]> lista = consulta.getResultList();
            for (Object[] object : lista) {
                ReprocessamentoContabilHistorico reprocessamento = new ReprocessamentoContabilHistorico();
                reprocessamento.setId(((BigDecimal) object[0]).longValue());
                reprocessamento.setDataHistorico((Date) object[1]);
                reprocessamento.setDataInicial((Date) object[2]);
                reprocessamento.setDataFinal((Date) object[3]);
                reprocessamento.setProcessadosSemErro(object[4] != null ? ((BigDecimal) object[4]).intValue() : null);
                reprocessamento.setTotal(((BigDecimal) object[5]).intValue());
                reprocessamento.setDataHoraInicio((Date) object[6]);
                reprocessamento.setDataHoraTermino((Date) object[7]);
                reprocessamento.setDecorrido(((BigDecimal) object[8]).longValue());
                reprocessamento.setUsuario((String) object[9]);
                resultado.add(reprocessamento);
            }
        } catch (NoResultException nre) {
            logger.error("Erro ao montar a busca de reprocessamento contabil {} ", nre);
        }
        Object[] retorno = new Object[2];
        retorno[0] = resultado;
        retorno[1] = count;
        return retorno;
    }

    @Override
    public ReprocessamentoContabilHistorico recuperar(Object id) {
        ReprocessamentoContabilHistorico contabilHistorico = super.recuperar(id);
        contabilHistorico.getMensagens().size();
        return contabilHistorico;
    }
}
