/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Pais;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UFFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.enums.Operacoes;
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
import java.util.List;

/**
 * @author Daniel
 */
@ManagedBean(name = "uFControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-estado", pattern = "/estado/novo/", viewId = "/faces/tributario/cadastromunicipal/uf/edita.xhtml"),
        @URLMapping(id = "editar-estado", pattern = "/estado/editar/#{uFControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/uf/edita.xhtml"),
        @URLMapping(id = "ver-estado", pattern = "/estado/ver/#{uFControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/uf/visualizar.xhtml"),
        @URLMapping(id = "listar-estado", pattern = "/estado/listar/", viewId = "/faces/tributario/cadastromunicipal/uf/lista.xhtml")
})
public class UFControlador extends PrettyControlador<UF> implements Serializable, CRUD {

    @EJB
    private UFFacade uFFacade;
    private FormUF form;
    protected ConverterAutoComplete converterPais;

    public UFControlador() {
        super(UF.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return uFFacade;
    }

    public UFFacade getFacade() {
        return uFFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/estado/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-estado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        form = (FormUF) Web.pegaDaSessao(FormUF.class);
        if (form == null) {
            novoGeral();
        }
    }

    @URLAction(mappingId = "editar-estado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "ver-estado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public void recuperaEditaVer() {
        form = new FormUF();
        form.selecionado = uFFacade.recuperar(super.getId());
    }

    public void novoGeral() {
        form = new FormUF();
        form.setSelecionado(this.selecionado);
    }

    public String getCaminhoOrigem() {
        if (form.selecionado.getId() == null) {
            return getCaminhoPadrao() + "novo/";
        }
        return getCaminhoPadrao() + "editar/" + form.selecionado.getId();
    }


    public void salvar() {
        if (Util.validaCampos(form.selecionado)) {
            try {
                if (operacao.equals(Operacoes.NOVO)) {
                    uFFacade.salvarNovo(form.selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada ", "O estado: " + form.selecionado.getNome() + " foi salvo com sucesso"));
                } else {
                    uFFacade.salvar(form.selecionado);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada ", "O estado: " + form.selecionado.getNome() + " foi alterado com sucesso"));
                }
                redireciona();
            } catch (Exception ex) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exceção do sistema ", ex.getMessage()));
            }
        }
    }

    public List<Pais> completaPais(String parte) {
        return uFFacade.getPaisFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<UF> completa(String parte) {
        return uFFacade.listaFiltrando(parte.trim(), "uf");

    }

    public Converter getConverterPais() {
        if (converterPais == null) {
            converterPais = new ConverterAutoComplete(Pais.class, uFFacade.getPaisFacade());
        }
        return converterPais;
    }

    public List<SelectItem> getListaSelectItemUf(){
        return Util.getListSelectItem(getFacade().lista());
    }

    public class FormUF {

        private UF selecionado;

        public UF getSelecionado() {
            return selecionado;
        }

        public void setSelecionado(UF selecionado) {
            this.selecionado = selecionado;
        }
    }

    public FormUF getForm() {
        return form;
    }

    public void setForm(FormUF form) {
        this.form = form;
    }
}
