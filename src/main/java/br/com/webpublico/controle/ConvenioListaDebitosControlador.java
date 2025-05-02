package br.com.webpublico.controle;

import br.com.webpublico.entidades.Agencia;
import br.com.webpublico.entidades.ConvenioListaDebitos;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AgenciaFacade;
import br.com.webpublico.negocios.ConvenioListaDebitosFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "convenioListaDebitosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConvenioListaDebitos", pattern = "/tributario/convenio-lista-debitos/novo/", viewId = "/faces/tributario/conveniolistadebitos/edita.xhtml"),
    @URLMapping(id = "editarConvenioListaDebitos", pattern = "/tributario/convenio-lista-debitos/editar/#{convenioListaDebitosControlador.id}/", viewId = "/faces/tributario/conveniolistadebitos/edita.xhtml"),
    @URLMapping(id = "verConvenioListaDebitos", pattern = "/tributario/convenio-lista-debitos/ver/#{convenioListaDebitosControlador.id}/", viewId = "/faces/tributario/conveniolistadebitos/visualizar.xhtml"),
    @URLMapping(id = "listarConvenioListaDebitos", pattern = "/tributario/convenio-lista-debitos/listar/", viewId = "/faces/tributario/conveniolistadebitos/lista.xhtml")
})
public class ConvenioListaDebitosControlador extends PrettyControlador<ConvenioListaDebitos> implements Serializable, CRUD {

    @EJB
    private ConvenioListaDebitosFacade convenioListaDebitosFacade;
    @EJB
    private AgenciaFacade agenciaFacade;

    public ConvenioListaDebitosControlador() {
        super(ConvenioListaDebitos.class);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @URLAction(mappingId = "novoConvenioListaDebitos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verConvenioListaDebitos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarConvenioListaDebitos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public AbstractFacade getFacede() {
        return convenioListaDebitosFacade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/convenio-lista-debitos/";
    }

    public List<Agencia> completarAgencia(String parte) {
        return agenciaFacade.listaFiltrandoAtributosAgenciaBanco(parte.trim());
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getExercicio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo exercício é obrigatório.");
        }
        if (selecionado.getAgencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo agência é obrigatório.");
        }
        if (selecionado.getNumeroConvenio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo número do convênio é obrigatório.");
        }
        if (selecionado.getCodigoConvenioMCI() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código do convênio MCI é obrigatório.");
        }
        if (selecionado.getDataInicialDispDebitos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data inicial para disponibilização de débitos é obrigatório.");
        }
        if (selecionado.getDataFinalDispDebitos() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final para disponibilização de débitos é obrigatório.");
        }
        if (selecionado.getDataInicialDispDebitos() != null && selecionado.getDataFinalDispDebitos() != null &&
            selecionado.getDataInicialDispDebitos().after(selecionado.getDataFinalDispDebitos())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial não pode ser maior que a data final.");
        }
        if (selecionado.getExercicio() != null && selecionado.getExercicio().equals(Util.getSistemaControlador().getExercicioCorrente()) &&
            selecionado.getDataInicialDispDebitos() != null && selecionado.getDataInicialDispDebitos().before(new Date())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial para disponibilização dos débitos deve ser posterior a data atual.");
        }
        ve.lancarException();
    }
}
