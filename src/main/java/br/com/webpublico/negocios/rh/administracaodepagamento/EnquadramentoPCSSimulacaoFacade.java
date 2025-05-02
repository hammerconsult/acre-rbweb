/*
 * Codigo gerado automaticamente em Sat Jul 02 11:00:55 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.EnquadramentoPCS;
import br.com.webpublico.entidades.rh.administracaodepagamento.EnquadramentoPCSSimulacao;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.interfaces.ValidadorVigenciaFolha;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CategoriaPCSFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ProgressaoPCSFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class EnquadramentoPCSSimulacaoFacade extends AbstractFacade<EnquadramentoPCSSimulacao> {

    private static final Logger logger = LoggerFactory.getLogger(EnquadramentoPCSSimulacaoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnquadramentoPCSSimulacaoFacade() {
        super(EnquadramentoPCSSimulacao.class);
    }

    public List<EnquadramentoPCSSimulacao> lista() {
        return super.lista();
    }

    public List<EnquadramentoPCS> listaEnquadramentosVigentes(Date dataVigencia) {
        Query q = em.createQuery(" from EnquadramentoPCS e "
            + " where :dataVigencia >= e.inicioVigencia "
            + " and :dataVigencia <= coalesce(e.finalVigencia,:dataVigencia) ");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public EnquadramentoPCSSimulacao salvarComRetorno(EnquadramentoPCSSimulacao entity) {
        try {
            return getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
        return null;
    }

    public Double salarioBaseContratoServidor(EntidadePagavelRH ep, Date dataReferencia) {
        String hql = "select sum(coalesce(pcs.vencimentoBase,0)) from EnquadramentoFuncional ef, EnquadramentoPCSSimulacao pcs" +
            " where ef.contratoServidor.id = :parametro and pcs.categoriaPCS = ef.categoriaPCS and pcs.progressaoPCS = ef.progressaoPCS" +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between to_date(to_char(ef.inicioVigencia,'mm/yyyy'),'mm/yyyy') and to_date(to_char(coalesce(ef.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy')  " +
            " and :dataVigencia between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigencia) order by ef.inicioVigencia ";

        double salarioBase = 0;
        try {
            Query q = em.createQuery(hql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setParameter("dataVigencia", dataReferencia, TemporalType.DATE);
            List<ValidadorVigenciaFolha> resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            salarioBase = ((BigDecimal) resultList.get(0)).doubleValue();
        } catch (Exception re) {
            throw new ExcecaoNegocioGenerica("Erro ao tentar executar método salarioBaseContratoServidor Simulção", re);
        }
        return salarioBase;
    }

    public List<EnquadramentoPCSSimulacao> buscarCategoriasReajustadas(Date dataReferencia) {
        String hql = "select distinct new br.com.webpublico.entidades.rh.administracaodepagamento.EnquadramentoPCSSimulacao(cat.superior, pcs.percentualReajuste) from EnquadramentoPCSSimulacao pcs join pcs.categoriaPCS cat" +

            " where :dataVigencia between pcs.inicioVigencia and coalesce(pcs.finalVigencia,:dataVigencia) ";

        try {
            Query q = em.createQuery(hql);
            q.setParameter("dataVigencia", dataReferencia, TemporalType.DATE);
            return q.getResultList();
        } catch (Exception re) {
            throw new ExcecaoNegocioGenerica("Erro ao tentar executar método salarioBaseContratoServidor Simulção", re);
        }
    }


}
