/*
 * Codigo gerado automaticamente em Tue Aug 23 01:49:40 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoMultaVeiculo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoMultaVeiculoFacade;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;


@ManagedBean(name = "tipoMultaVeiculoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "tipoMultaNovo", pattern = "/frota/tipo-de-multa/novo/", viewId = "/faces/administrativo/frota/tipomultaveiculo/edita.xhtml"),
        @URLMapping(id = "tipoMultaListar", pattern = "/frota/tipo-de-multa/listar/", viewId = "/faces/administrativo/frota/tipomultaveiculo/lista.xhtml"),
        @URLMapping(id = "tipoMultaEditar", pattern = "/frota/tipo-de-multa/editar/#{tipoMultaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/tipomultaveiculo/edita.xhtml"),
        @URLMapping(id = "tipoMultaVer", pattern = "/frota/tipo-de-multa/ver/#{tipoMultaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/tipomultaveiculo/visualizar.xhtml"),
})
public class TipoMultaVeiculoControlador extends PrettyControlador<TipoMultaVeiculo> implements Serializable, CRUD {

    @EJB
    private TipoMultaVeiculoFacade tipoMultaVeiculoFacade;
    private ConverterAutoComplete converterTipoMulta;

    public TipoMultaVeiculoControlador() {
        super(TipoMultaVeiculo.class);
    }

    public TipoMultaVeiculoFacade getFacade() {
        return tipoMultaVeiculoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoMultaVeiculoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/tipo-de-multa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "tipoMultaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }
    @URLAction(mappingId = "tipoMultaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }
    @URLAction(mappingId = "tipoMultaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<TipoMultaVeiculo> completaTipoMulta(String parte) {
        return tipoMultaVeiculoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterTipoMulta() {
        if (converterTipoMulta == null) {
            converterTipoMulta = new ConverterAutoComplete(TipoMultaVeiculo.class, tipoMultaVeiculoFacade);
        }
        return converterTipoMulta;
    }

}
