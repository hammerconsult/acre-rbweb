/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoAreaRural;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoAreaRuralFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Heinz
 */
@ManagedBean(name = "tipoAreaRuralControle")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoareaRural", pattern = "/tipo-de-area-rural/novo/", viewId = "/faces/tributario/cadastromunicipal/tipoarearural/edita.xhtml"),
        @URLMapping(id = "editarTipoareaRural", pattern = "/tipo-de-area-rural/editar/#{tipoAreaRuralControle.id}/", viewId = "/faces/tributario/cadastromunicipal/tipoarearural/edita.xhtml"),
        @URLMapping(id = "listarTipoareaRural", pattern = "/tipo-de-area-rural/listar/", viewId = "/faces/tributario/cadastromunicipal/tipoarearural/lista.xhtml"),
        @URLMapping(id = "verTipoareaRural", pattern = "/tipo-de-area-rural/ver/#{tipoAreaRuralControle.id}/", viewId = "/faces/tributario/cadastromunicipal/tipoarearural/visualizar.xhtml")
})
public class TipoAreaRuralControle extends PrettyControlador<TipoAreaRural> implements Serializable, CRUD {

    @EJB
    private TipoAreaRuralFacade tipoAreaRuralFacade;

    public TipoAreaRuralControle() {
        super(TipoAreaRural.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAreaRuralFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-area-rural/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoTipoareaRural", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarTipoareaRural", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTipoareaRural", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() != null && selecionado.getId() == null) {
            if (tipoAreaRuralFacade.existeCodigoCategoria(selecionado.getCodigo())) {
                FacesUtil.addError("Impossível continuar. ", "Este codigo já existe!");
                retorno = false;
            }
        }
        if (selecionado.getCodigo() == null) {
            FacesUtil.addError("Impossível continuar. ", "O código deve ser preenchido");
            retorno = false;
        }
        if (selecionado.getCodigo() != null && selecionado.getCodigo() < 0) {
            FacesUtil.addError("Impossível continuar. ", "O código não pode ser negativo");
            retorno = false;
        }
        if (selecionado.getDescricao() == null || "".equals(selecionado.getDescricao().trim())) {
            FacesUtil.addError("Impossível continuar. ", "Descricao obrigatória");
            retorno = false;
        }
        return retorno;
    }

    public String caminhoAtual() {
        if (operacao.equals(Operacoes.NOVO)) {
            return getCaminhoPadrao() + "novo/";
        } else if (operacao.equals(Operacoes.EDITAR)) {
            return getCaminhoPadrao() + "editar/" + this.getId() + "/";
        }
        return "";
    }

}
