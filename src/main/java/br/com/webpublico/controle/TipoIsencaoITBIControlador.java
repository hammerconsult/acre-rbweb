package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "tipoIsencaoITBIControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoIsencaoITBI", pattern = "/tipo-isencao-itbi/novo/", viewId = "/faces/tributario/itbi/tipoisencao/edita.xhtml"),
        @URLMapping(id = "editarTipoIsencaoITBI", pattern = "/tipo-isencao-itbi/editar/#{tipoIsencaoITBIControlador.id}/", viewId = "/faces/tributario/itbi/tipoisencao/edita.xhtml"),
        @URLMapping(id = "listarTipoIsencaoITBI", pattern = "/tipo-isencao-itbi/listar/", viewId = "/faces/tributario/itbi/tipoisencao/lista.xhtml"),
        @URLMapping(id = "verTipoIsencaoITBI", pattern = "/tipo-isencao-itbi/ver/#{tipoIsencaoITBIControlador.id}/", viewId = "/faces/tributario/itbi/tipoisencao/visualizar.xhtml")
})
public class TipoIsencaoITBIControlador extends PrettyControlador<TipoIsencaoITBI> implements Serializable, CRUD {

    @EJB
    private TipoIsencaoITBIFacade tipoIsencaoITBIFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;

    public TipoIsencaoITBIControlador() {
        super(TipoIsencaoITBI.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-isencao-itbi/";
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoIsencaoITBIFacade;
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @URLAction(mappingId = "novoTipoIsencaoITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(tipoIsencaoITBIFacade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId = "verTipoIsencaoITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoIsencaoITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        List<AtoLegal> lista = atoLegalFacade.listaFiltrando(parte, "nome");
        return lista;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }
}
