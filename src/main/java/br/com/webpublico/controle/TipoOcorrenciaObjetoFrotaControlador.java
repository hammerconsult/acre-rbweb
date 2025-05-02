/*
 * Codigo gerado automaticamente em Thu May 26 15:46:57 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoOcorrenciaObjetoFrota;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoOcorrenciaObjetoFrotaFacade;
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


@ManagedBean(name = "tipoOcorrenciaObjetoFrotaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "tipoOcorrenciaObjetoFrotaNovo", pattern = "/frota/tipo-ocorrencia/novo/", viewId = "/faces/administrativo/frota/tipoocorrenciaobjetofrota/edita.xhtml"),
        @URLMapping(id = "tipoOcorrenciaObjetoFrotaListar", pattern = "/frota/tipo-ocorrencia/listar/", viewId = "/faces/administrativo/frota/tipoocorrenciaobjetofrota/lista.xhtml"),
        @URLMapping(id = "tipoOcorrenciaObjetoFrotaEditar", pattern = "/frota/tipo-ocorrencia/editar/#{tipoOcorrenciaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/tipoocorrenciaobjetofrota/edita.xhtml"),
        @URLMapping(id = "tipoOcorrenciaObjetoFrotaVer", pattern = "/frota/tipo-ocorrencia/ver/#{tipoOcorrenciaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/tipoocorrenciaobjetofrota/visualizar.xhtml"),
})
public class TipoOcorrenciaObjetoFrotaControlador extends PrettyControlador<TipoOcorrenciaObjetoFrota> implements Serializable, CRUD {

    @EJB
    private TipoOcorrenciaObjetoFrotaFacade tipoOcorrenciaObjetoFrotaFacade;
    private ConverterAutoComplete converterTipoOcorrenciaObjetoFrota;

    public TipoOcorrenciaObjetoFrotaControlador() {
        super(TipoOcorrenciaObjetoFrota.class);
    }

    public TipoOcorrenciaObjetoFrotaFacade getFacade() {
        return tipoOcorrenciaObjetoFrotaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoOcorrenciaObjetoFrotaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/tipo-ocorrencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "tipoOcorrenciaObjetoFrotaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "tipoOcorrenciaObjetoFrotaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "tipoOcorrenciaObjetoFrotaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public ConverterAutoComplete getConverterTipoOcorrenciaObjetoFrota() {
        if (converterTipoOcorrenciaObjetoFrota == null) {
            converterTipoOcorrenciaObjetoFrota = new ConverterAutoComplete(TipoOcorrenciaObjetoFrota.class, getFacade());
        }
        return converterTipoOcorrenciaObjetoFrota;
    }

    public List<TipoOcorrenciaObjetoFrota> completaTipoOcorrenciaObjetoFrota(String parte) {
        return tipoOcorrenciaObjetoFrotaFacade.listaFiltrando(parte.trim(), "descricao");
    }


}
