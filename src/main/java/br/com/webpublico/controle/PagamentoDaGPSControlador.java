/*
 * Codigo gerado automaticamente em Tue Jan 10 14:33:58 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.PagamentoDaGPS;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PagamentoDaGPSFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "pagamentoDaGPSControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoPagamentoDaGPS", pattern = "/rh/pagamento-da-gps/novo/", viewId = "/faces/rh/administracaodepagamento/pagamentodagps/edita.xhtml"),
        @URLMapping(id = "listaPagamentoDaGPS", pattern = "/rh/pagamento-da-gps/listar/", viewId = "/faces/rh/administracaodepagamento/pagamentodagps/lista.xhtml"),
        @URLMapping(id = "verPagamentoDaGPS", pattern = "/rh/pagamento-da-gps/ver/#{pagamentoDaGPSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pagamentodagps/visualizar.xhtml"),
        @URLMapping(id = "editarPagamentoDaGPS", pattern = "/rh/pagamento-da-gps/editar/#{pagamentoDaGPSControlador.id}/", viewId = "/faces/rh/administracaodepagamento/pagamentodagps/edita.xhtml"),
})
public class PagamentoDaGPSControlador extends PrettyControlador<PagamentoDaGPS> implements Serializable, CRUD {

    @EJB
    private PagamentoDaGPSFacade pagamentoDaGPSFacade;

    public PagamentoDaGPSControlador() {
        super(PagamentoDaGPS.class);
    }

    public PagamentoDaGPSFacade getFacade() {
        return pagamentoDaGPSFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pagamentoDaGPSFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/pagamento-da-gps/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (pagamentoDaGPSFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O Código informado já está cadastrado em outro Pagamento da GPS!");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoPagamentoDaGPS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verPagamentoDaGPS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarPagamentoDaGPS", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }
}
