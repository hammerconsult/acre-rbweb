/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CEPLocalidadeFacade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean(name = "cidadeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-cidade", pattern = "/cidade/novo/", viewId = "/faces/tributario/cadastromunicipal/cidade/edita.xhtml"),
        @URLMapping(id = "editar-cidade", pattern = "/cidade/editar/#{cidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cidade/edita.xhtml"),
        @URLMapping(id = "ver-cidade", pattern = "/cidade/ver/#{cidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/cidade/visualizar.xhtml"),
        @URLMapping(id = "listar-cidade", pattern = "/cidade/listar/", viewId = "/faces/tributario/cadastromunicipal/cidade/lista.xhtml")
})

public class CidadeControlador extends PrettyControlador<Cidade> implements Serializable, CRUD {

    @EJB
    private CidadeFacade cidadeFacade;
    protected ConverterGenerico converterEstado;

    public CidadeControlador() {
        super(Cidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return cidadeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-cidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-cidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-cidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }


    public List<SelectItem> getUf() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (UF object : cidadeFacade.getuFFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getUf()));
        }
        return toReturn;
    }


    public List<Cidade> completa(String parte) {
        return cidadeFacade.listaFiltrando(parte.trim(), "nome");
    }

    public ConverterGenerico getConverterEstado() {
        if (converterEstado == null) {
            converterEstado = new ConverterGenerico(UF.class, cidadeFacade.getuFFacade());
        }
        return converterEstado;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean retorno = true;
        if (cidadeFacade.cidadeJaExiste(selecionado)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar:", "Já existe uma cidade cadastrada para o estado informado"));
            retorno = false;
        }
        return retorno;
    }

    public List<Cidade> completaCidade(String parte) {
        return cidadeFacade.listaFiltrando(parte.trim(), "nome");
    }
}
