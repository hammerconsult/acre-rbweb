package br.com.webpublico.controle.rh.portal;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.portal.ProgramacaoFeriasPortal;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.portal.ProgramacaoFeriasPortalFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @Author peixe on 09/11/2016  09:10.
 */
@ManagedBean(name = "programacaoFeriasPortalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-programacao-ferias-portal", pattern = "/programacao-ferias-portal/novo/", viewId = "/faces/rh/portal/programacaoferiasportal/edita.xhtml"),
    @URLMapping(id = "notificacao-programacao-ferias-portal", pattern = "/programacao-ferias-portal/notificacoes/", viewId = "/faces/rh/portal/programacaoferiasportal/notificacoes.xhtml"),
    @URLMapping(id = "editar-programacao-ferias-portal", pattern = "/programacao-ferias-portal/editar/#{programacaoFeriasPortalControlador.id}/", viewId = "/faces/rh/portal/programacaoferiasportal/edita.xhtml"),
    @URLMapping(id = "listar-programacao-ferias-portal", pattern = "/programacao-ferias-portal/listar/", viewId = "/faces/rh/portal/programacaoferiasportal/lista.xhtml"),
    @URLMapping(id = "ver-programacao-ferias-portal", pattern = "/programacao-ferias-portal/ver/#{programacaoFeriasPortalControlador.id}/", viewId = "/faces/rh/portal/programacaoferiasportal/visualizar.xhtml")
})
public class ProgramacaoFeriasPortalControlador extends PrettyControlador<ProgramacaoFeriasPortal> implements CRUD {


    @EJB
    private ProgramacaoFeriasPortalFacade facade;

    public ProgramacaoFeriasPortalControlador() {
        super(ProgramacaoFeriasPortal.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/programacao-ferias-portal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "nova-programacao-ferias-portal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-programacao-ferias-portal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-programacao-ferias-portal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "notificacao-programacao-ferias-portal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void carregarNotificacoes() {
        super.novo();
    }

    @Override
    public void excluir() {
        if (selecionado != null) {
            Long id = selecionado.getId();
            NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(id);
        }
        super.excluir();
    }

    @Override
    public void cancelar() {
        String origem = "/notificacao/" + TipoNotificacao.PROGRAMACAO_FERIAS_PORTAL + "/";
        FacesUtil.redirecionamentoInterno(origem);
    }

    public void efetivarSugestaoPortal() {
        if (selecionado != null) {
            try {
                Long id = selecionado.getId();
                facade.tornarProgramacaoProtalEfetivaParaSugestaoFerias(selecionado);
                NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(id);
                FacesUtil.addInfo("", "Programaçao de Férias Salva com Sucesso!");
                cancelar();
            } catch (ValidacaoException val) {
                logger.error("Erro de Validação", val);
                FacesUtil.printAllFacesMessages(val.getAllMensagens());
            } catch (Exception e) {
                logger.error("Erro: ", e);
                FacesUtil.addError("Atenção", e.getMessage());
            }

        }
    }

    public void rejeitarSugestaoPortal() {
        if (selecionado != null) {
            try {
                Long id = selecionado.getId();
                facade.rejeitarSugestaoPortal(selecionado);
                NotificacaoService.getService().marcarNotificaoComoLidaPorIdDoLink(id);
                if(facade.getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente().getNotificarFeriasPortal()) {
                    FacesUtil.addInfo("", "Foi enviado um email para notificar ao servidor(a) sobre a rejeição do pedido.");
                }
                FacesUtil.addInfo("", "O registro foi excluído.");
                cancelar();
            } catch (ValidacaoException val) {
                logger.error("Erro de Validação", val);
                FacesUtil.printAllFacesMessages(val.getAllMensagens());
            } catch (Exception e) {
                logger.error("Erro: ", e);
                FacesUtil.addError("Atenção", e.getMessage());
            }

        }
    }

}
