package br.com.webpublico.negocios.rh.esocial;


import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.util.FacesUtil;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class RegistroS1207Facade extends AbstractFacade<RegistroEventoEsocial> {

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    public ConfiguracaoEmpregadorESocialFacade getConfiguracaoEmpregadorESocialFacade() {
        return configuracaoEmpregadorESocialFacade;
    }

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public RegistroS1207Facade() {
        super(RegistroEventoEsocial.class);
    }

    @Override
    public RegistroEventoEsocial recuperar(Object id) {
        RegistroEventoEsocial registro = super.recuperar(id);
        Hibernate.initialize(registro.getItemVinculoFP());
        return registro;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public FolhaDePagamentoFacade getFolhaDePagamentoFacade() {
        return folhaDePagamentoFacade;
    }

    public List<VinculoFPEventoEsocial> getVinculoFPEventoEsocial(RegistroEventoEsocial registroEventoEsocial, VinculoFP vinculoFP, boolean apenasNaoEnviados) {
        try {
            ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperarPorEntidade(registroEventoEsocial.getEntidade());
            validarConfigEmpregadorESocial(configuracaoEmpregadorESocial, registroEventoEsocial);
            configuracaoEmpregadorESocial = getConfiguracaoEmpregadorESocialFacade().recuperar(configuracaoEmpregadorESocial.getId());

            List<Long> idsUnidade = getContratoFPFacade().montarIdOrgaoEmpregador(configuracaoEmpregadorESocial);

            List<VinculoFPEventoEsocial> vinculoFPS = folhaDePagamentoFacade.buscarAposentadosPorTipoFolhaMesEAnoAndFolhaEfetivadaAndUnidades(registroEventoEsocial, idsUnidade, apenasNaoEnviados);
            if (vinculoFPS != null && !vinculoFPS.isEmpty()) {
                return vinculoFPS;
            }
            FacesUtil.addOperacaoNaoRealizada("Nenhum Servidor encontrado para os filtros informados.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return null;
    }

    private void validarConfigEmpregadorESocial(ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial, RegistroEventoEsocial registroEventoEsocial) {
        ValidacaoException ve = new ValidacaoException();
        if (ve.getMensagens().isEmpty()) {
            if (configuracaoEmpregadorESocial == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração para a Entidade " + registroEventoEsocial.getEntidade());
            }
        }
        ve.lancarException();
    }
}
