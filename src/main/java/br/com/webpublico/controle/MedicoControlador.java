/*
 * Codigo gerado automaticamente em Mon Sep 05 09:56:38 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Medico;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MedicoFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "medicoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMedico", pattern = "/medico/novo/", viewId = "/faces/rh/administracaodepagamento/medico/edita.xhtml"),
        @URLMapping(id = "editarMedico", pattern = "/medico/editar/#{medicoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/medico/edita.xhtml"),
        @URLMapping(id = "listarMedico", pattern = "/medico/listar/", viewId = "/faces/rh/administracaodepagamento/medico/lista.xhtml"),
        @URLMapping(id = "verMedico", pattern = "/medico/ver/#{medicoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/medico/visualizar.xhtml")
})
public class MedicoControlador extends PrettyControlador<Medico> implements Serializable,CRUD {

    @EJB
    private MedicoFacade atestadoMedicoFacade;

    private ConverterGenerico medicoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;

    private ConverterAutoComplete converterMedico;

    public MedicoControlador() {
        metadata = new EntidadeMetaData(Medico.class);
    }

    public MedicoFacade getFacade() {
        return atestadoMedicoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return atestadoMedicoFacade;
    }

    public List<SelectItem> getMedico() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PessoaFisica object : pessoaFisicaFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterMedico() {
        if (converterMedico == null) {
            converterMedico = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterMedico;
    }

    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        Medico a = (Medico) evento.getComponent().getAttributes().get("objeto");
        selecionado = atestadoMedicoFacade.recuperar(a.getId());
    }

    public List<PessoaFisica> completaMedico(String parte) {
        return pessoaFisicaFacade.listaFiltrandoTodasPessoasByNomeAndCpf(parte.trim());
    }

    @URLAction(mappingId = "novoMedico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "verMedico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarMedico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }


    public String getCaminhoPadrao() {
        return "/medico/";
    }


    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
