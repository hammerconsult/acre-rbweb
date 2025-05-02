/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RecursoSistemaFacade;
import br.com.webpublico.seguranca.menu.LeitorMenuFacade;
import br.com.webpublico.seguranca.service.RecursoSistemaService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fabio
 */
@ManagedBean(name = "recursoSistemaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRecursoSistema", pattern = "/admin/recursosistema/novo/", viewId = "/faces/admin/controleusuario/recursosistema/edita.xhtml"),
        @URLMapping(id = "editarRecursoSistema", pattern = "/admin/recursosistema/editar/#{recursoSistemaControlador.id}/", viewId = "/faces/admin/controleusuario/recursosistema/edita.xhtml"),
        @URLMapping(id = "listarRecursoSistema", pattern = "/admin/recursosistema/listar/", viewId = "/faces/admin/controleusuario/recursosistema/lista.xhtml"),
        @URLMapping(id = "verRecursoSistema", pattern = "/admin/recursosistema/ver/#{recursoSistemaControlador.id}/", viewId = "/faces/admin/controleusuario/recursosistema/visualizar.xhtml")
})
public class RecursoSistemaControlador extends PrettyControlador<RecursoSistema> implements Serializable, CRUD {

    @EJB
    private RecursoSistemaFacade recursoSistemaFacade;
    @ManagedProperty(name = "recursoSistemaService", value = "#{recursoSistemaService}")
    private RecursoSistemaService recursoSistemaService;

    public RecursoSistemaControlador() {
        super(RecursoSistema.class);
    }

    @URLAction(mappingId = "novoRecursoSistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.selecionado.setCadastro(false);
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                if (this.selecionado.getId() == null) {
                    this.recursoSistemaFacade.salvarNovo(selecionado);
                } else {
                    this.recursoSistemaFacade.salvar(selecionado);
                }
                recursoSistemaService.getSingletonRecursosSistema().getRecursos().clear();
                LeitorMenuFacade leitorMenuFacade = (LeitorMenuFacade) Util.getSpringBeanPeloNome("leitorMenuFacade");
                leitorMenuFacade.limparTodosMenus();
                FacesUtil.addInfo("", "Salvo com sucesso!");
                redireciona();
            } catch (Exception e) {
                FacesUtil.addError("Não foi possível continuar!", "Erro ao salvar " + e.getMessage());
            }
        }
    }

    @URLAction(mappingId = "verRecursoSistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRecursoSistema", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void excluirSelecionado() {
        try {
            this.recursoSistemaFacade.remover(selecionado);
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível continuar!", "Este registro não pode ser excluído porque possui dependências.");
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (this.selecionado.getNome() == null || "".equals(this.selecionado.getNome())) {
            FacesUtil.addCampoObrigatorio("O Campo Nome deve ser informado.");
            retorno = false;
        }
        if (this.selecionado.getCaminho() == null || "".equals(this.selecionado.getCaminho())) {
            FacesUtil.addCampoObrigatorio("O Campo Caminho deve ser informado.");
            retorno = false;
        }
        if (this.selecionado.getId() == null) {
            if (recursoSistemaService.jaExiste(this.selecionado.getCaminho())) {
                FacesUtil.addOperacaoNaoRealizada("O Caminho já existe!");
                retorno = false;
            }
        }
        return retorno;
    }

    public List<SelectItem> getCarregaModulos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ModuloSistema object : ModuloSistema.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public RecursoSistemaService getRecursoSistemaService() {
        return recursoSistemaService;
    }

    public void setRecursoSistemaService(RecursoSistemaService recursoSistemaService) {
        this.recursoSistemaService = recursoSistemaService;
    }

    @Override
    public AbstractFacade getFacede() {
        return recursoSistemaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/admin/recursosistema/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void renomearTodosRecursos() {
        try {
            recursoSistemaFacade.renomeiaTodosRecursos();
            FacesUtil.addOperacaoRealizada("Todos os recursos foram renomeados e os módulos definidos corretamente.");
        }catch (Exception e){
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public List<RecursoSistema> completarRecursos(String parte) {
        return recursoSistemaFacade.listaRecursos(parte.trim(), null);
    }
}
