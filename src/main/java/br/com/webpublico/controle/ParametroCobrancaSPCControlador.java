package br.com.webpublico.controle;

import br.com.webpublico.entidades.ParametroCobrancaSPC;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ParametroCobrancaSPCFacade;
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
    @URLMapping(id = "novoParametroCobrancaSpc",
        pattern = "/tributario/parametro-cobranca-spc/novo/",
        viewId = "/faces/tributario/parametrocobrancaspc/edita.xhtml"),
    @URLMapping(id = "listarParametroCobrancaSpc",
        pattern = "/tributario/parametro-cobranca-spc/listar/",
        viewId = "/faces/tributario/parametrocobrancaspc/lista.xhtml"),
    @URLMapping(id = "editarParametroCobrancaSpc",
        pattern = "/tributario/parametro-cobranca-spc/editar/#{parametroCobrancaSPCControlador.id}/",
        viewId = "/faces/tributario/parametrocobrancaspc/edita.xhtml"),
    @URLMapping(id = "verParametroCobrancaSpc",
        pattern = "/tributario/parametro-cobranca-spc/ver/#{parametroCobrancaSPCControlador.id}/",
        viewId = "/faces/tributario/parametrocobrancaspc/visualizar.xhtml")
})
public class ParametroCobrancaSPCControlador extends PrettyControlador<ParametroCobrancaSPC> implements CRUD {

    @EJB
    private ParametroCobrancaSPCFacade parametroCobrancaSPCFacade;

    public ParametroCobrancaSPCControlador() {
        super(ParametroCobrancaSPC.class);
    }

    @Override
    public ParametroCobrancaSPCFacade getFacede() {
        return parametroCobrancaSPCFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/parametro-cobranca-spc/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoParametroCobrancaSpc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarParametroCobrancaSpc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verParametroCobrancaSpc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "listarParametroCobrancaSpc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        selecionado = parametroCobrancaSPCFacade.recuperarUltimo();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ParametroCobrancaSPC p = parametroCobrancaSPCFacade.recuperarUltimo();
        if (p != null && !p.getId().equals(selecionado.getId())) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um parametro cadastrado.");
            return false;
        }
        return true;
    }
}
