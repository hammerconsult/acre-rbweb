/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.DividaLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.tributario.DividaLicenciamentoAmbientalFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarDividaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/divida/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/divida/lista.xhtml"),
    @URLMapping(id = "editarDividaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/divida/editar/#{dividaLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/divida/edita.xhtml"),
    @URLMapping(id = "verDividaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/divida/ver/#{dividaLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/divida/visualizar.xhtml"),
    @URLMapping(id = "novaDividaLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/divida/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/divida/edita.xhtml")
})
public class DividaLicenciamentoAmbientalControlador extends PrettyControlador<DividaLicenciamentoAmbiental> implements Serializable, CRUD {

    @EJB
    private DividaLicenciamentoAmbientalFacade facade;
    private List<DividaLicenciamentoAmbiental> lista;

    public DividaLicenciamentoAmbientalControlador() {
        super(DividaLicenciamentoAmbiental.class);
    }

    @Override
    public DividaLicenciamentoAmbientalFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/divida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaDividaLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarDividaLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verDividaLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "listarDividaLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        lista = facade.lista();
    }

    public List<DividaLicenciamentoAmbiental> getLista() {
        return lista;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a dívida para geração de débito do licenciamento ambiental!");
        }
        if (ve.temMensagens()) {
            ve.lancarException();
            return false;
        }
        return true;
    }
}
