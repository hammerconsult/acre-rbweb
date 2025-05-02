/*
 * Codigo gerado automaticamente em Wed Jun 08 11:23:49 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.LDO;
import br.com.webpublico.entidades.RiscoFiscalLDO;
import br.com.webpublico.enums.TipoRiscoFiscalLDO;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LDOFacade;
import br.com.webpublico.negocios.RiscoFiscalLDOFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.MoneyConverter;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "riscoFiscalLDOControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-risco-fiscal-ldo", pattern = "/risco-fiscal-ldo/novo/", viewId = "/faces/financeiro/ppa/riscofiscalldo/edita.xhtml"),
        @URLMapping(id = "editar-risco-fiscal-ldo", pattern = "/risco-fiscal-ldo/editar/#{riscoFiscalLDOControlador.id}/", viewId = "/faces/financeiro/ppa/riscofiscalldo/edita.xhtml"),
        @URLMapping(id = "ver-risco-fiscal-ldo", pattern = "/risco-fiscal-ldo/ver/#{riscoFiscalLDOControlador.id}/", viewId = "/faces/financeiro/ppa/riscofiscalldo/visualizar.xhtml"),
        @URLMapping(id = "listar-risco-fiscal-ldo", pattern = "/risco-fiscal-ldo/listar/", viewId = "/faces/financeiro/ppa/riscofiscalldo/lista.xhtml")
})
public class RiscoFiscalLDOControlador extends PrettyControlador<RiscoFiscalLDO> implements Serializable, CRUD {

    @EJB
    private RiscoFiscalLDOFacade riscoFiscalLDOFacade;
    @EJB
    private LDOFacade ldoFacade;
    private ConverterGenerico converterLdo;
    private MoneyConverter moneyConverter;

    public RiscoFiscalLDOControlador() {
        super(RiscoFiscalLDO.class);
    }

    public RiscoFiscalLDOFacade getFacade() {
        return riscoFiscalLDOFacade;
    }

    public List<LDO> getListaLDOs() {
        return ldoFacade.lista();
    }

    public List<SelectItem> getListaLDO() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (LDO obj : ldoFacade.lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterLdo() {
        if (converterLdo == null) {
            converterLdo = new ConverterGenerico(LDO.class, ldoFacade);
        }
        return converterLdo;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public List<SelectItem> getTipoRiscoFiscalLDO() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRiscoFiscalLDO object : TipoRiscoFiscalLDO.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    @Override
    public AbstractFacade getFacede() {
        return riscoFiscalLDOFacade;
    }

    @URLAction(mappingId = "novo-risco-fiscal-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoRiscoFiscalLDO(TipoRiscoFiscalLDO.PASSIVO_CONTINGENTE);
    }

    @URLAction(mappingId = "ver-risco-fiscal-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-risco-fiscal-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/risco-fiscal-ldo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
