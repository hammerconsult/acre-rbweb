package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClassificacaoUso;
import br.com.webpublico.entidades.ClassificacaoUsoItem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClassificacaoUsoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
                id = "inserirClassificacaoUso",
                pattern = "/cadastro-imobiliario/alvara-imediato/classificacao-uso/novo/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/edita.xhtml"),
        @URLMapping(
                id = "editarClassificacaoUso",
                pattern = "/cadastro-imobiliario/alvara-imediato/classificacao-uso/editar/#{classificacaoUsoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/edita.xhtml"),
        @URLMapping(
                id = "verClassificacaoUso",
                pattern = "/cadastro-imobiliario/alvara-imediato/classificacao-uso/ver/#{classificacaoUsoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/visualiza.xhtml"),
        @URLMapping(
                id = "listarClassificacaoUso",
                pattern = "/cadastro-imobiliario/alvara-imediato/classificacao-uso/listar/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/classificacaouso/lista.xhtml")
})
public class ClassificacaoUsoControlador extends PrettyControlador<ClassificacaoUso> implements CRUD {

    @EJB
    private ClassificacaoUsoFacade facade;
    private ClassificacaoUsoItem classificacaoUsoItem;

    public ClassificacaoUsoControlador() {
        super(ClassificacaoUso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cadastro-imobiliario/alvara-imediato/classificacao-uso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ClassificacaoUsoItem getClassificacaoUsoItem() {
        return classificacaoUsoItem;
    }

    public void setClassificacaoUsoItem(ClassificacaoUsoItem classificacaoUsoItem) {
        this.classificacaoUsoItem = classificacaoUsoItem;
    }

    @URLAction(mappingId = "inserirClassificacaoUso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarClassificacaoUso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verClassificacaoUso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void inserirItem() {
        this.classificacaoUsoItem = new ClassificacaoUsoItem();
    }

    public void editarItem(ClassificacaoUsoItem classificacaoUsoItem) {
        this.classificacaoUsoItem = classificacaoUsoItem;
        this.selecionado.getItemList().remove(classificacaoUsoItem);
    }

    public void removerItem(ClassificacaoUsoItem classificacaoUsoItem) {
        this.selecionado.getItemList().remove(classificacaoUsoItem);
    }

    private void validarItem(ClassificacaoUsoItem classificacaoUsoItem) {
        classificacaoUsoItem.realizarValidacoes();
        if (selecionado.hasItem(classificacaoUsoItem)) {
            throw new ValidacaoException("O Código do Item já está adicionado.");
        }
    }

    public void salvarItem() {
        try {
            validarItem(classificacaoUsoItem);
            classificacaoUsoItem.setClassificacaoUso(selecionado);
            selecionado.getItemList().add(classificacaoUsoItem);
            classificacaoUsoItem = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelarItem() {
        this.classificacaoUsoItem = null;
    }
}
