/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEndereco;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal1
 */
@ManagedBean(name = "graficaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaGrafica", pattern = "/grafica/novo/", viewId = "/faces/tributario/cadastromunicipal/grafica/edita.xhtml"),
        @URLMapping(id = "editarGrafica", pattern = "/grafica/editar/#{graficaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/grafica/edita.xhtml"),
        @URLMapping(id = "listarGrafica", pattern = "/grafica/listar/", viewId = "/faces/tributario/cadastromunicipal/grafica/lista.xhtml"),
        @URLMapping(id = "verGrafica", pattern = "/grafica/ver/#{graficaControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/grafica/visualizar.xhtml")
})
public class GraficaControlador extends PrettyControlador<Grafica> implements Serializable, CRUD {

    @EJB
    private GraficaFacade facade;
    @EJB
    private EnderecoFacade enderecoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaJuridicaFacade pessoaJuridicaFacade;
    private List<EnderecoCorreio> listaEndereco;
    private List<Telefone> listaTelefone;
    private ConverterAutoComplete converterPessoa;

    public GraficaControlador() {
        super(Grafica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<Telefone> getListaTelefone() {
        return listaTelefone;
    }

    public void setListaTelefone(List<Telefone> listaTelefone) {
        this.listaTelefone = listaTelefone;
    }

    public List<EnderecoCorreio> getListaEndereco() {
        return listaEndereco;
    }

    public void setListaEndereco(List<EnderecoCorreio> listaEndereco) {
        this.listaEndereco = listaEndereco;
    }

    public EnderecoFacade getEnderecoFacade() {
        return enderecoFacade;
    }

    public void setEnderecoFacade(EnderecoFacade enderecoFacade) {
        this.enderecoFacade = enderecoFacade;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(PessoaJuridica.class, pessoaJuridicaFacade);
        }
        return converterPessoa;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grafica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novaGrafica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        listaEndereco = new ArrayList<EnderecoCorreio>();
        listaTelefone = new ArrayList<Telefone>();
    }

    @Override
    @URLAction(mappingId = "editarGrafica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        listaEndereco = enderecoFacade.enderecoPorPessoa(selecionado.getResponsavel());
        listaTelefone = pessoaFacade.telefonePorPessoa(selecionado.getResponsavel());
    }

    @Override
    @URLAction(mappingId = "verGrafica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void recuperaEnderecoTelefone() {
        listaEndereco = enderecoFacade.enderecoPorPessoa(selecionado.getResponsavel());
        listaTelefone = pessoaFacade.telefonePorPessoa(selecionado.getResponsavel());
        FacesUtil.atualizarComponente("Formulario");
    }

    public List<PessoaJuridica> completaPessoa(String parte) {
        return pessoaJuridicaFacade.listaFiltrando(parte.trim(), "nomeFantasia");
    }

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoEndereco t : TipoEndereco.values()) {
            toReturn.add(new SelectItem(t, t.getDescricao()));
        }
        return toReturn;
    }
}
