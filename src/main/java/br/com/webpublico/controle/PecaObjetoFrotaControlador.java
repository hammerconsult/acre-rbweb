/*
 * Codigo gerado automaticamente em Mon Oct 24 17:56:04 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidades.PecaObjetoFrota;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MaterialFacade;
import br.com.webpublico.negocios.PecaObjetoFrotaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "pecaObjetoFrotaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "pecaObjetoFrotaNovo", pattern = "/frota/peca/novo/", viewId = "/faces/administrativo/frota/peca/edita.xhtml"),
    @URLMapping(id = "pecaObjetoFrotaListar", pattern = "/frota/peca/listar/", viewId = "/faces/administrativo/frota/peca/lista.xhtml"),
    @URLMapping(id = "pecaObjetoFrotaEditar", pattern = "/frota/peca/editar/#{pecaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/peca/edita.xhtml"),
    @URLMapping(id = "pecaObjetoFrotaVer", pattern = "/frota/peca/ver/#{pecaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/peca/visualizar.xhtml"),
})
public class PecaObjetoFrotaControlador extends PrettyControlador<PecaObjetoFrota> implements Serializable, CRUD {

    @EJB
    private PecaObjetoFrotaFacade pecaObjetoFrotaFacade;
    @EJB
    private MaterialFacade materialFacade;

    public PecaObjetoFrotaControlador() {
        super(PecaObjetoFrota.class);
    }


    @Override
    public String getCaminhoPadrao() {
        return "/frota/peca/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "pecaObjetoFrotaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "pecaObjetoFrotaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "pecaObjetoFrotaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public PecaObjetoFrotaFacade getFacade() {
        return pecaObjetoFrotaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return pecaObjetoFrotaFacade;
    }

    public void processaSelecaoTipoObjetoFrota() {
        selecionado.setKmVidaUtil(BigDecimal.ZERO);
        selecionado.setHorasUsoVidaUtil(BigDecimal.ZERO);
    }
}
