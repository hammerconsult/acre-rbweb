/*
 * Codigo gerado automaticamente em Tue Mar 27 11:22:23 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.event.AjaxBehaviorEvent;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@ManagedBean(name = "dividaPublicaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-divida-publica", pattern = "/divida-publica/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/dividapublica/edita.xhtml"),
    @URLMapping(id = "editar-divida-publica", pattern = "/divida-publica/editar/#{dividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/dividapublica/edita.xhtml"),
    @URLMapping(id = "ver-divida-publica", pattern = "/divida-publica/ver/#{dividaPublicaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/dividapublica/visualizar.xhtml"),
    @URLMapping(id = "listar-divida-publica", pattern = "/divida-publica/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/dividapublica/lista.xhtml")
})
public class DividaPublicaControlador extends DividaPublicaSuperControlador {

    @URLAction(mappingId = "novo-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-divida-publica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/divida-publica/";
    }

    public void navegarParaCategoria() {
        FacesUtil.redirecionamentoInterno("/categoria-divida-publica/novo/");
    }

    @Override
    public List<ClasseCredor> completaClasseCredor(String parte) {
        DividaPublica ad = ((DividaPublica) selecionado);
        return dividaPublicaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte, ad.getPessoa(), TipoClasseCredor.DIVIDA_PUBLICA);
    }

}
