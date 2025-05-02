/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Distrito;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DistritoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author terminal4
 */
@ManagedBean(name = "distritoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-distrito", pattern = "/distrito/novo/", viewId = "/faces/tributario/cadastromunicipal/distrito/edita.xhtml"),
    @URLMapping(id = "editar-distrito", pattern = "/distrito/editar/#{distritoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/distrito/edita.xhtml"),
    @URLMapping(id = "ver-distrito", pattern = "/distrito/ver/#{distritoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/distrito/visualizar.xhtml"),
    @URLMapping(id = "listar-distrito", pattern = "/distrito/listar/", viewId = "/faces/tributario/cadastromunicipal/distrito/lista.xhtml")
})
public class DistritoControlador extends PrettyControlador<Distrito> implements Serializable, CRUD {

    @EJB
    private DistritoFacade distritoFacade;
    protected ConverterAutoComplete converterMunicipio;
    private FormDistrito form;
    private ConfiguracaoTributario conf;

    public DistritoControlador() {
        super(Distrito.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return distritoFacade;
    }

    public DistritoFacade getFacade() {
        return distritoFacade;
    }

    public ConfiguracaoTributario getConf() {
        return conf;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/distrito/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-distrito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        conf = distritoFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (conf != null) {
            super.novo();
            form = (FormDistrito) Web.pegaDaSessao(FormDistrito.class);
            if (form == null) {
                novoGeral();
            }
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    @URLAction(mappingId = "editar-distrito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        conf = distritoFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (conf != null) {
            super.editar();
            recuperaEditaVer();
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    @URLAction(mappingId = "ver-distrito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    public void novoGeral() {
        form = new FormDistrito();
        form.setSelecionado(this.selecionado);
    }

    public void recuperaEditaVer() {
        form = new FormDistrito();
        form.selecionado = distritoFacade.recuperar(super.getId());
    }

    public String getCaminhoOrigem() {
        if (form.selecionado.getId() == null) {
            return getCaminhoPadrao() + "novo/";
        }
        return getCaminhoPadrao() + "editar/" + form.selecionado.getId();
    }


    public List<Distrito> getLista() {
        if (form.lista == null) {
            form.lista = distritoFacade.lista();
        }
        return form.lista;
    }

    public void salvar() {

        if (validaCampos()) {
            try {
                distritoFacade.salvar(form.selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, " Salvo com sucesso ", " O Distrito " + form.selecionado.getNome() + " foi salvo com sucesso "));
                redireciona();
            } catch (Exception e) {
                logger.debug(e.getMessage());
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Exceção do Sistema ", e.getMessage()));
            }
        } else {
            return;
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;

        if (form.selecionado.getCodigo() == null || form.selecionado.getCodigo().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O código do distrito é um campo obrigatório.");
        } else if (form.selecionado.getCodigo().replaceAll(" ", "").length() < conf.getNumDigitosDistrito()) {
            retorno = false;
            FacesUtil.addWarn("Código inválido.", "A configuração vigente exige um código com " + conf.getNumDigitosDistrito() + " dígitos.");
        } else if (distritoFacade.existeDistritoComMesmoCodigo(form.selecionado.getId(), form.getSelecionado().getCodigo())) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "Já existe um Distrito com o código " + form.selecionado.getCodigo());
        }
        if (form.selecionado.getNome() == null || form.selecionado.getNome().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Nome do Distrito é um campo obrigatório.");
        } else if (distritoFacade.existeDistritoComMesmoNome(form.selecionado.getId(), form.selecionado.getNome())) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "Já existe um Distrito cadastrado com o mesmo nome.");
        }
        return retorno;
    }


    public class FormDistrito implements Serializable {

        private Distrito selecionado;
        private String caminho;
        private List<Distrito> lista;

        public List<Distrito> getLista() {
            return lista;
        }

        public void setLista(List<Distrito> lista) {
            this.lista = lista;
        }

        public String getCaminho() {
            return caminho;
        }

        public void setCaminho(String caminho) {
            this.caminho = caminho;
        }

        public Distrito getSelecionado() {
            return selecionado;
        }

        public void setSelecionado(Distrito selecionado) {
            this.selecionado = selecionado;
        }
    }

    public FormDistrito getForm() {
        return form;
    }

    public void setForm(FormDistrito form) {
        this.form = form;
    }
}
