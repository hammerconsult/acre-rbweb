package br.com.webpublico.controle;


import br.com.webpublico.entidades.BloqueioTransferenciaProprietario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.BloqueioTransferenciaProprietarioFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoBloqueioTransferenciaProprietario",
        pattern = "/bloqueio-transferencia-proprietario/novo/",
        viewId = "/faces/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/edita.xhtml"),
    @URLMapping(id = "editarBloqueioTransferenciaProprietario",
        pattern = "/bloqueio-transferencia-proprietario/editar/#{bloqueioTransferenciaProprietarioControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/edita.xhtml"),
    @URLMapping(id = "listarBloqueioTransferenciaProprietario",
        pattern = "/bloqueio-transferencia-proprietario/listar/",
        viewId = "/faces/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/lista.xhtml"),
    @URLMapping(id = "verBloqueioTransferenciaProprietario",
        pattern = "/bloqueio-transferencia-proprietario/ver/#{bloqueioTransferenciaProprietarioControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/bloqueiotransferenciaproprietario/visualizar.xhtml")
})
public class BloqueioTransferenciaProprietarioControlador extends PrettyControlador<BloqueioTransferenciaProprietario>
    implements CRUD {

    @EJB
    private BloqueioTransferenciaProprietarioFacade facade;

    public BloqueioTransferenciaProprietarioControlador() {
        super(BloqueioTransferenciaProprietario.class);
    }

    @Override
    public BloqueioTransferenciaProprietarioFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bloqueio-transferencia-proprietario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoBloqueioTransferenciaProprietario",
        phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setDataLancamento(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioLancamento(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    @URLAction(mappingId = "editarBloqueioTransferenciaProprietario",
        phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verBloqueioTransferenciaProprietario",
        phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void confirmarFinalizacao() {
        try {
            selecionado.validarFinalizacao();
            selecionado.setUsuarioFinalizacao(facade.getSistemaFacade().getUsuarioCorrente());
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.executaJavaScript("dlgFinalizacao.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.debug("Erro ao confirmar finalização de bloqueio de transferência de proprietário.", e);
            logger.error("Erro ao confirmar finalização de bloqueio de transferência de proprietário.");
        }
    }

    public void cancelarFinalizacaoBloqueio() {
        selecionado.setDataFinal(null);
        selecionado.setUsuarioFinalizacao(null);
    }

    public void mudouDataFinal() {
        selecionado.setUsuarioFinalizacao(null);
        selecionado.setMotivoFinalizacao(null);
        if (selecionado.getDataFinal() != null) {
            selecionado.setUsuarioFinalizacao(facade.getSistemaFacade().getUsuarioCorrente());
        }
    }
}

