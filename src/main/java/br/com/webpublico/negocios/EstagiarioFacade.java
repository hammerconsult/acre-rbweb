/*
 * Codigo gerado automaticamente em Fri Feb 17 11:15:20 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Estagiario;
import br.com.webpublico.entidades.LotacaoFuncional;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.esocial.service.S2300Service;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;

@Stateless
public class EstagiarioFacade extends AbstractFacade<Estagiario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private ESocialService eSocialService;
    private S2300Service s2300Service;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public EstagiarioFacade() {
        super(Estagiario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    private ConfiguracaoEmpregadorESocial getConfiguracaoEmpregadorESocial(Estagiario entity) {
        LotacaoFuncional lotacao = new LotacaoFuncional();
        for (LotacaoFuncional lotacaoFuncional : entity.getLotacaoFuncionals()) {
            if (lotacaoFuncional.getFinalVigencia() == null || lotacaoFuncional.getFinalVigencia().after(new Date())) {
                lotacao = lotacaoFuncional;
                break;
            }
            lotacao = lotacaoFuncional;
        }
        return buscarEmpregadorPorLotacaoFuncional(lotacao);
    }

    @PostConstruct
    public void init() {
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
        s2300Service = (S2300Service) Util.getSpringBeanPeloNome("s2300Service");
    }

    @Override
    public Estagiario recuperar(Object id) {
        Estagiario e = em.find(Estagiario.class, id);
        e.getAssociacaosVinculosFPs().size();
        e.getLotacaoFuncionals().size();
        e.getSituacaoFuncionals().size();
        e.getSindicatosVinculosFPs().size();
        e.getPrevidenciaVinculoFPs().size();
        e.getPeriodosAquisitivosFLs().size();
        e.getOpcaoValeTransporteFPs().size();
        e.getPastasContratosFP().size();
        e.getOpcaoSalarialVinculoFP().size();
        e.getRecursosDoVinculoFP().size();
        e.getCargos().size();
        return e;
    }

    public ConfiguracaoEmpregadorESocial buscarEmpregadorPorLotacaoFuncional(LotacaoFuncional lotacao) {
        String sql = "  select empregador.* from lotacaofuncional lotacao " +
            " INNER JOIN UNIDADEORGANIZACIONAL unidade ON lotacao.UNIDADEORGANIZACIONAL_ID = unidade.id " +
            " INNER JOIN VWHIERARQUIAADMINISTRATIVA ho ON ho.SUBORDINADA_ID = unidade.id " +
            " INNER JOIN ITEMEMPREGADORESOCIAL item ON item.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " INNER JOIN CONFIGEMPREGADORESOCIAL empregador ON item.CONFIGEMPREGADORESOCIAL_ID = empregador.id " +
            " WHERE lotacao.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " AND lotacao.id = :lotacaoId " +
            " AND :dataOperacao between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, :dataOperacao)";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("lotacaoId", lotacao.getId());
        q.setParameter("dataOperacao", UtilRH.getDataOperacao());
        if (!q.getResultList().isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) q.getResultList().get(0);
        }
        return null;
    }
}
