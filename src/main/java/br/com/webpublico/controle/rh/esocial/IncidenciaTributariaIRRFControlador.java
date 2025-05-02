package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.IncidenciaTributariaIRRF;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.IncidenciaTributariaIRRFFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by William on 25/05/2018.
 */
@ManagedBean(name = "incidenciaTributariaIRRFControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-incidencia-tributaria-irrf", pattern = "/e-social/incidencia-tributaria-irrf/novo/", viewId = "/faces/rh/esocial/incidencia-tributaria-irrf/edita.xhtml"),
    @URLMapping(id = "editar-incidencia-tributaria-irrf", pattern = "/e-social/incidencia-tributaria-irrf/editar/#{incidenciaTributariaIRRFControlador.id}/", viewId = "/faces/rh/esocial/incidencia-tributaria-irrf/edita.xhtml"),
    @URLMapping(id = "ver-incidencia-tributaria-irrf", pattern = "/e-social/incidencia-tributaria-irrf/ver/#{incidenciaTributariaIRRFControlador.id}/", viewId = "/faces/rh/esocial/incidencia-tributaria-irrf/visualizar.xhtml"),
    @URLMapping(id = "listar-incidencia-tributaria-irrf", pattern = "/e-social/incidencia-tributaria-irrf/listar/", viewId = "/faces/rh/esocial/incidencia-tributaria-irrf/lista.xhtml")
})
public class IncidenciaTributariaIRRFControlador extends PrettyControlador<IncidenciaTributariaIRRF> implements CRUD {

    @EJB
    private IncidenciaTributariaIRRFFacade incidenciaTributariaIRRFFacade;

    public IncidenciaTributariaIRRFControlador() {
        super(IncidenciaTributariaIRRF.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return incidenciaTributariaIRRFFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/e-social/incidencia-tributaria-irrf/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "nova-incidencia-tributaria-irrf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editar-incidencia-tributaria-irrf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-incidencia-tributaria-irrf", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCodigo();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCodigo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() != null) {
            if (incidenciaTributariaIRRFFacade.hasCodigoCadastrado(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Cadastro de Incidência Tributária para IRRF com o código <b>" + selecionado.getCodigo() + "</b>");
            }
            if (selecionado.getCodigo() != null && (selecionado.getCodigo().trim().length() < 2 || Integer.parseInt(selecionado.getCodigo()) < 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O código deve ter dois digitos e deve ser maior que zero");
            }
            ve.lancarException();
        }
    }
}
