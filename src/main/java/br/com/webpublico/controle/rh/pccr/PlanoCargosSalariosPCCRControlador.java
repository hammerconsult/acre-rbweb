package br.com.webpublico.controle.rh.pccr;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.rh.pccr.CargoCategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.PlanoCargosSalariosPCCR;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.pccr.CategoriaPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.PlanoCargosSalariosPCCRFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 15:05
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoPlanoCargosSalariosPCCR", pattern = "/plano-cargos-salarios-pccr/novo/", viewId = "/faces/rh/pccr/planocargossalariospccr/edita.xhtml"),
        @URLMapping(id = "editarPlanoCargosSalariosPCCR", pattern = "/plano-cargos-salarios-pccr/editar/#{planoCargosSalariosPCCRControlador.id}/", viewId = "/faces/rh/pccr/planocargossalariospccr/edita.xhtml"),
        @URLMapping(id = "listarPlanoCargosSalariosPCCR", pattern = "/plano-cargos-salarios-pccr/listar/", viewId = "/faces/rh/pccr/planocargossalariospccr/lista.xhtml"),
        @URLMapping(id = "verPlanoCargosSalariosPCCR", pattern = "/plano-cargos-salarios-pccr/ver/#{planoCargosSalariosPCCRControlador.id}/", viewId = "/faces/rh/pccr/planocargossalariospccr/visualizar.xhtml")
})
public class PlanoCargosSalariosPCCRControlador extends PrettyControlador<PlanoCargosSalariosPCCR> implements Serializable, CRUD {

    @EJB
    private PlanoCargosSalariosPCCRFacade planoCargosSalariosPCCRFacade;
    private ConverterAutoComplete converterPlanoCargosPCCR;
    private CargoCategoriaPCCR cargoCategoriaPCCR;
    @EJB
    private CargoFacade cargoFacade;

    public PlanoCargosSalariosPCCRControlador() {
        super(PlanoCargosSalariosPCCR.class);
    }


    @Override
    public String getCaminhoPadrao() {
        return "/plano-cargos-salarios-pccr/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Converter getConverterPlanoPCCR() {
        if (converterPlanoCargosPCCR == null) {
            converterPlanoCargosPCCR = new ConverterAutoComplete(PlanoCargosSalariosPCCR.class, planoCargosSalariosPCCRFacade);
        }
        return converterPlanoCargosPCCR;
    }

    public List<SelectItem> getTipoPCCs() {
        return Util.getListSelectItem(Arrays.asList(TipoPCS.values()));
    }

    @Override
    public AbstractFacade getFacede() {
        return planoCargosSalariosPCCRFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "novoPlanoCargosSalariosPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "verPlanoCargosSalariosPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarPlanoCargosSalariosPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }

}
