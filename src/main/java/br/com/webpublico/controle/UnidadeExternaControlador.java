/*
 * Codigo gerado automaticamente em Mon Dec 12 14:41:06 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EsferaGoverno;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.UnidadeExterna;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EsferaGovernoFacade;
import br.com.webpublico.negocios.PessoaJuridicaFacade;
import br.com.webpublico.negocios.UnidadeExternaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "unidadeExternaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoUnidadeExterna", pattern = "/unidade-externa/novo/", viewId = "/faces/rh/administracaodepagamento/unidadeexterna/edita.xhtml"),
        @URLMapping(id = "editarUnidadeExterna", pattern = "/unidade-externa/editar/#{unidadeExternaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/unidadeexterna/edita.xhtml"),
        @URLMapping(id = "listarUnidadeExterna", pattern = "/unidade-externa/listar/", viewId = "/faces/rh/administracaodepagamento/unidadeexterna/lista.xhtml"),
        @URLMapping(id = "verUnidadeExterna", pattern = "/unidade-externa/ver/#{unidadeExternaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/unidadeexterna/visualizar.xhtml")
})
public class UnidadeExternaControlador extends PrettyControlador<UnidadeExterna> implements Serializable, CRUD {

    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    private ConverterAutoComplete converterPessoaJuridica;
    @EJB
    private EsferaGovernoFacade esferaGovernoFacade;
    private ConverterGenerico converterEsferaGoverno;

    public UnidadeExternaControlador() {
        super(UnidadeExterna.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return unidadeExternaFacade;
    }
    @URLAction(mappingId = "novoUnidadeExterna", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }
    @URLAction(mappingId = "verUnidadeExterna", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarUnidadeExterna", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<SelectItem> getPessoaJuridica() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (PessoaJuridica object : pessoaJuridicaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, pessoaJuridicaFacade);
        }
        return converterPessoaJuridica;
    }

    public List<SelectItem> getEsferaGoverno() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaGoverno object : esferaGovernoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEsferaGoverno() {
        if (converterEsferaGoverno == null) {
            converterEsferaGoverno = new ConverterGenerico(EsferaGoverno.class, esferaGovernoFacade);
        }
        return converterEsferaGoverno;
    }

    public List<PessoaJuridica> completaPessoaJuridica(String parte) {
        return pessoaJuridicaFacade.listaFiltrando(parte.trim(), "razaoSocial", "cnpj");
    }

    public Boolean validaCampos() {
        boolean valido = Util.validaCampos(selecionado);
        if (valido) {
            if (unidadeExternaFacade.existeUnidadeExterna((UnidadeExterna) selecionado)) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção !", "Já foi cadastrada uma unidade externa com a mesma pessoa jurídica e esfera do governo !"));
                valido = false;
            }
        }

        return valido;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/unidade-externa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
