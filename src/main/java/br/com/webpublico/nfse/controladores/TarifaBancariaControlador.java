package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TarifaBancaria;
import br.com.webpublico.nfse.facades.TarifaBancariaFacade;
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
    @URLMapping(id = "listarTarifaBancaria", pattern = "/tributario/nfse/tarifa-bancaria/listar/", viewId = "/faces/tributario/nfse/tarifa-bancaria/lista.xhtml"),
    @URLMapping(id = "verTarifaBancaria", pattern = "/tributario/nfse/tarifa-bancaria/ver/#{tarifaBancariaControlador.id}/", viewId = "/faces/tributario/nfse/tarifa-bancaria/visualizar.xhtml")
})
public class TarifaBancariaControlador extends PrettyControlador<TarifaBancaria> implements Serializable, CRUD {

    @EJB
    private TarifaBancariaFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public TarifaBancariaControlador() {
        super(TarifaBancaria.class);
    }

    @URLAction(mappingId = "verTarifaBancaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/tarifa-bancaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
