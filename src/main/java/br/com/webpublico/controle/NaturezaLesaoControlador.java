package br.com.webpublico.controle;

import br.com.webpublico.entidades.rh.saudeservidor.NaturezaLesao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.saudeservidor.NaturezaLesaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "naturezaLesaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoNaturezaLesao", pattern = "/naturezaLesao/novo/", viewId = "/faces/rh/administracaodepagamento/naturezaLesao/edita.xhtml"),
    @URLMapping(id = "editarnaturezaLesao", pattern = "/naturezaLesao/editar/#{naturezaLesaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/naturezaLesao/edita.xhtml"),
    @URLMapping(id = "listarnaturezaLesao", pattern = "/naturezaLesao/listar/", viewId = "/faces/rh/administracaodepagamento/naturezaLesao/lista.xhtml"),
    @URLMapping(id = "vernaturezaLesao", pattern = "/naturezaLesao/ver/#{naturezaLesaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/naturezaLesao/visualizar.xhtml")
})
public class NaturezaLesaoControlador extends PrettyControlador<NaturezaLesao> implements Serializable, CRUD {

    @EJB
    private NaturezaLesaoFacade naturezaLesaoFacade;

    private ConverterAutoComplete converterNaturezaLesao;



    @Override
    public AbstractFacade getFacede() {
        return naturezaLesaoFacade;
    }

    @URLAction(mappingId = "novoNaturezaLesao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "vernaturezaLesao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarnaturezaLesao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }


    @Override
    public String getCaminhoPadrao() {
        return "/naturezaLesao/";
    }


    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ConverterAutoComplete getConverterNaturezaLesao() {
        if (converterNaturezaLesao == null) {
            converterNaturezaLesao = new ConverterAutoComplete(NaturezaLesao.class, naturezaLesaoFacade);
        }
        return converterNaturezaLesao;
    }
}
