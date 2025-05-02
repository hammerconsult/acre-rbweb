/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CBO;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.rh.cbo.TipoCBO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CBOFacade;
import br.com.webpublico.util.ConverterAutoComplete;
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
import java.util.List;

/**
 * @author Usuário
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-cbo", pattern = "/cbo/novo/", viewId = "/faces/rh/cbo/edita.xhtml"),
    @URLMapping(id = "listar-cbo", pattern = "/cbo/listar/", viewId = "/faces/rh/cbo/lista.xhtml"),
    @URLMapping(id = "ver-cbo", pattern = "/cbo/ver/#{cBOControlador.id}/", viewId = "/faces/rh/cbo/visualizar.xhtml"),
    @URLMapping(id = "editar-cbo", pattern = "/cbo/editar/#{cBOControlador.id}/", viewId = "/faces/rh/cbo/edita.xhtml"),
})
public class CBOControlador extends PrettyControlador<CBO> implements Serializable, CRUD {

    @EJB
    private CBOFacade facade;
    private ConverterAutoComplete converterEstaEntidade;

    public CBOControlador() {
        super(CBO.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cbo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-cbo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setAtivo(Boolean.TRUE);
    }

    @URLAction(mappingId = "ver-cbo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-cbo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public ConverterAutoComplete getConverterEstaEntidade() {
        if (converterEstaEntidade == null) {
            converterEstaEntidade = new ConverterAutoComplete(CBO.class, facade);
        }
        return converterEstaEntidade;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar CBO", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDescricao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo descrição deve ser informado.");
        }
        if (selecionado.getCodigo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo código deve ser informado.");
        }
        if (selecionado.getTipoCBO() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo tipo de CBO deve ser informado.");
        }
        if (Operacoes.EDITAR.equals(operacao) && !selecionado.getAtivo() && facade.cboVinculadoCargoVigente(selecionado.getId())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido inativar um CBO vinculado à um cargo vigente.");
        }
        ve.lancarException();
    }

    public List<CBO> completarEstaEntidade(String param) {
        return facade.listaFiltrando(param, "descricao", "codigo");
    }

    public List<SelectItem> getTiposCbo() {
        return Util.getListSelectItem(TipoCBO.values(), false);
    }
}
