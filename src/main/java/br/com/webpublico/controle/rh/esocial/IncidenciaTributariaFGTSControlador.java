package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.IncidenciaTributariaFGTS;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.IncidenciaTributariaFGTSFacade;
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
@ManagedBean(name = "incidenciaTributariaFGTSControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-incidencia-tributaria-fgts", pattern = "/e-social/incidencia-tributaria-fgts/novo/", viewId = "/faces/rh/esocial/incidencia-tributaria-fgts/edita.xhtml"),
    @URLMapping(id = "editar-incidencia-tributaria-fgts", pattern = "/e-social/incidencia-tributaria-fgts/editar/#{incidenciaTributariaFGTSControlador.id}/", viewId = "/faces/rh/esocial/incidencia-tributaria-fgts/edita.xhtml"),
    @URLMapping(id = "ver-incidencia-tributaria-fgts", pattern = "/e-social/incidencia-tributaria-fgts/ver/#{incidenciaTributariaFGTSControlador.id}/", viewId = "/faces/rh/esocial/incidencia-tributaria-fgts/visualizar.xhtml"),
    @URLMapping(id = "listar-incidencia-tributaria-fgts", pattern = "/e-social/incidencia-tributaria-fgts/listar/", viewId = "/faces/rh/esocial/incidencia-tributaria-fgts/lista.xhtml")
})
public class IncidenciaTributariaFGTSControlador extends PrettyControlador<IncidenciaTributariaFGTS> implements CRUD {

    @EJB
    private IncidenciaTributariaFGTSFacade incidenciaTributariaFGTSFacade;

    public IncidenciaTributariaFGTSControlador() {
        super(IncidenciaTributariaFGTS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return incidenciaTributariaFGTSFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/e-social/incidencia-tributaria-fgts/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "nova-incidencia-tributaria-fgts", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editar-incidencia-tributaria-fgts", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-incidencia-tributaria-fgts", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
            if (incidenciaTributariaFGTSFacade.hasCodigoCadastrado(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Cadastro de Incidência Tributária para FGTS com o código <b>" + selecionado.getCodigo() + "</b>");
            }
            if (selecionado.getCodigo() != null && (selecionado.getCodigo().trim().length() < 2 || Integer.parseInt(selecionado.getCodigo()) < 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O código deve ter dois digitos e deve ser maior que zero");
            }
            ve.lancarException();
        }
    }
}
