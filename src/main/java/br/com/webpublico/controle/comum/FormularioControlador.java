package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.entidades.comum.FormularioCampoOpcao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.comum.FormularioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-formulario",
        pattern = "/comum/formulario/novo/",
        viewId = "/faces/comum/formulario/edita.xhtml"),
    @URLMapping(id = "editar-formulario",
        pattern = "/comum/formulario/editar/#{formularioControlador.id}/",
        viewId = "/faces/comum/formulario/edita.xhtml"),
    @URLMapping(id = "ver-formulario",
        pattern = "/comum/formulario/ver/#{formularioControlador.id}/",
        viewId = "/faces/comum/formulario/visualizar.xhtml"),
    @URLMapping(id = "listar-formulario",
        pattern = "/comum/formulario/listar/",
        viewId = "/faces/comum/formulario/lista.xhtml")
})
public class FormularioControlador extends PrettyControlador<Formulario> implements Serializable, CRUD {

    @EJB
    private FormularioFacade facade;
    private FormularioCampo formularioCampo;
    private FormularioCampoOpcao formularioCampoOpcao;

    public FormularioControlador() {
        super(Formulario.class);
    }

    @Override
    public FormularioFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/comum/formulario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public FormularioCampo getFormularioCampo() {
        return formularioCampo;
    }

    public void setFormularioCampo(FormularioCampo formularioCampo) {
        this.formularioCampo = formularioCampo;
    }

    public FormularioCampoOpcao getFormularioCampoOpcao() {
        return formularioCampoOpcao;
    }

    public void setFormularioCampoOpcao(FormularioCampoOpcao formularioCampoOpcao) {
        this.formularioCampoOpcao = formularioCampoOpcao;
    }

    @URLAction(mappingId = "novo-formulario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-formulario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-formulario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> getTiposComponente() {
        return Util.getListSelectItem(FormularioCampo.TipoComponente.values(), false);
    }

    public List<Formulario> completarFormulario(String parte) {
        return facade.listaFiltrando(parte.trim(), "titulo");
    }

    public void validarFormularioCampo() {
        Util.validarCampos(formularioCampo);
        if (FormularioCampo.TipoComponente.ARQUIVO_DOWNLOAD.equals(formularioCampo.getTipoComponente()) &&
            formularioCampo.getDetentorArquivoComposicao().getArquivoComposicao() == null) {
            throw new ValidacaoException("O campo Arquivo para Download deve ser informado.");
        }
    }

    public void salvarFormularioCampo() {
        try {
            validarFormularioCampo();
            formularioCampo.setFormulario(selecionado);
            selecionado.getFormularioCampoList().add(formularioCampo);
            formularioCampo = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelarFormularioCampo() {
        formularioCampo = null;
    }

    public void novoFormularioCampo() {
        formularioCampo = new FormularioCampo();
    }

    public void alterarFormularioCampo(FormularioCampo formularioCampo) {
        this.formularioCampo = formularioCampo;
        this.selecionado.getFormularioCampoList().remove(formularioCampo);
    }

    public void novoFormularioCampoFilho(FormularioCampo formularioCampo) {
        this.formularioCampo = new FormularioCampo();
        this.formularioCampo.setFormularioCampo(formularioCampo);
    }

    public void removerFormularioCampo(FormularioCampo formularioCampo) {
        selecionado.getFormularioCampoList().remove(formularioCampo);
    }

    public void validarFormularioCampoOpcao() {
        Util.validarCampos(formularioCampoOpcao);
    }

    public void salvarFormularioCampoOpcao() {
        try {
            validarFormularioCampoOpcao();
            formularioCampoOpcao.setFormularioCampo(formularioCampo);
            formularioCampo.getFormularioCampoOpcaoList().add(formularioCampoOpcao);
            formularioCampoOpcao = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelarFormularioCampoOpcao() {
        formularioCampoOpcao = null;
    }

    public void novoFormularioCampoOpcao() {
        formularioCampoOpcao = new FormularioCampoOpcao();
    }

    public void alterarFormularioCampoOpcao(FormularioCampoOpcao formularioCampoOpcao) {
        this.formularioCampoOpcao = formularioCampoOpcao;
        this.formularioCampo.getFormularioCampoOpcaoList().remove(formularioCampoOpcao);
    }

    public void removerFormularioCampoOpcao(FormularioCampoOpcao formularioCampoOpcao) {
        formularioCampo.getFormularioCampoOpcaoList().remove(formularioCampoOpcao);
    }

    public void mudouTipoComponente() {
        formularioCampo.setObrigatorio(Boolean.FALSE);
        formularioCampo.setFormularioCampoOpcaoList(new ArrayList<FormularioCampoOpcao>());
        formularioCampo.setDetentorArquivoComposicao(null);
        if (formularioCampo.getTipoComponente().equals(FormularioCampo.TipoComponente.ARQUIVO_DOWNLOAD)) {
            formularioCampo.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }
    }
}
