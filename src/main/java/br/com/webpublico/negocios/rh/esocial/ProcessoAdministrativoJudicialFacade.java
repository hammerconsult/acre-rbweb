package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ProcessoAdministrativoJudicial;
import br.com.webpublico.enums.rh.esocial.TipoIntegracaoEsocial;
import br.com.webpublico.esocial.service.S1070Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.UFFacade;
import br.com.webpublico.util.Util;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by mateus on 18/06/18.
 */
@Stateless
public class ProcessoAdministrativoJudicialFacade extends AbstractFacade<ProcessoAdministrativoJudicial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    private S1070Service s1070Service;

    public ProcessoAdministrativoJudicialFacade() {
        super(ProcessoAdministrativoJudicial.class);
    }
    @PostConstruct
    public void init() {
        s1070Service = (S1070Service) Util.getSpringBeanPeloNome("s1070Service");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    public List<ProcessoAdministrativoJudicial> buscarProcessosPorEmpregador(ConfiguracaoEmpregadorESocial empregador, TipoIntegracaoEsocial tipoIntegracaoEsocial) {
        String sql = "select processo.* from PROCESSOADMJUDICIAL processo where configuracaoEmpregador_id = :empregador";
        if(tipoIntegracaoEsocial != null){
            sql += " and processo.tipoIntegracao = :tipoIntegracao ";
        }
        Query q = em.createNativeQuery(sql, ProcessoAdministrativoJudicial.class);
        q.setParameter("empregador", empregador.getId());
        if(tipoIntegracaoEsocial != null){
            q.setParameter("tipoIntegracao", tipoIntegracaoEsocial.name());
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public void enviarProcessoJudicialAdministrativo(ConfiguracaoEmpregadorESocial empregador, ProcessoAdministrativoJudicial processoAdministrativoJudicial) throws ValidacaoException {
        s1070Service.enviarS1070(empregador, processoAdministrativoJudicial);
    }
}
