package br.com.webpublico.negocios.rh.cadastrofuncional;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.service.ESocialService;
import br.com.webpublico.esocial.service.S2250Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by William on 01/10/2018.
 */
@Stateless
public class AvisoPrevioFacade extends AbstractFacade<AvisoPrevio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private ESocialService eSocialService;
    private S2250Service s2250Service;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public AvisoPrevioFacade() {
        super(AvisoPrevio.class);
    }

    @PostConstruct
    public void init() {
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
        s2250Service = (S2250Service) Util.getSpringBeanPeloNome("s2250Service");
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void enviarS2250ESocial(ConfiguracaoEmpregadorESocial empregador, AvisoPrevio avisoPrevio) throws ValidacaoException {
        s2250Service.enviarS2250(empregador, avisoPrevio);
    }

    @Override
    public void salvarNovo(AvisoPrevio entity) {
        ConfiguracaoEmpregadorESocial empregador = contratoFPFacade.buscarEmpregadorPorVinculoFP(entity.getContratoFP());
        entity = em.merge(entity);
    }

    @Override
    public void salvar(AvisoPrevio entity) {
        ConfiguracaoEmpregadorESocial empregador = contratoFPFacade.buscarEmpregadorPorVinculoFP(entity.getContratoFP());
        entity = em.merge(entity);
    }

    public List<AvisoPrevio> getAvisoPrevioPorContratoFP(ContratoFP contratoFP) {
        String sql = "select * from avisoprevio where CONTRATOFP_ID = :idContrato";
        Query q = em.createNativeQuery(sql, AvisoPrevio.class);
        q.setParameter("idContrato", contratoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
