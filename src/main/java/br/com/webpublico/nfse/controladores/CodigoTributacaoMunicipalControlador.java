package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.nfse.domain.CodigoTributacaoMunicipal;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.facades.CodigoTributacaoMunicipalFacade;
import com.beust.jcommander.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCodigoTributacaoMunicipal",
        pattern = "/tributario/nfse/codigo-tributacao-municipal/novo/",
        viewId = "/faces/tributario/nfse/codigo-tributacao-municipal/edita.xhtml"),
    @URLMapping(id = "editarCodigoTributacaoMunicipal",
        pattern = "/tributario/nfse/codigo-tributacao-municipal/editar/#{codigoTributacaoMunicipalControlador.id}/",
        viewId = "/faces/tributario/nfse/codigo-tributacao-municipal/edita.xhtml"),
    @URLMapping(id = "listarCodigoTributacaoMunicipal",
        pattern = "/tributario/nfse/codigo-tributacao-municipal/listar/",
        viewId = "/faces/tributario/nfse/codigo-tributacao-municipal/lista.xhtml"),
    @URLMapping(id = "verCodigoTributacaoMunicipal",
        pattern = "/tributario/nfse/codigo-tributacao-municipal/ver/#{codigoTributacaoMunicipalControlador.id}/",
        viewId = "/faces/tributario/nfse/codigo-tributacao-municipal/visualizar.xhtml")
})
public class CodigoTributacaoMunicipalControlador extends PrettyControlador<CodigoTributacaoMunicipal> implements Serializable, CRUD {

    @EJB
    private CodigoTributacaoMunicipalFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public CodigoTributacaoMunicipalControlador() {
        super(CodigoTributacaoMunicipal.class);
    }

    @URLAction(mappingId = "novoCodigoTributacaoMunicipal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarCodigoTributacaoMunicipal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verCodigoTributacaoMunicipal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/codigo-tributacao-municipal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void atribuirCodigo() {
        if (Strings.isStringEmpty(selecionado.getCodigo()) && selecionado.getCodigoTributacao() != null) {
            selecionado.setCodigo(selecionado.getCodigoTributacao().getCodigo());
        }
    }
}
