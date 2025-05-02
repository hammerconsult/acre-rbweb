/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.controle;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.entidades.TipoLogradouro;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoLogradouroFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

@ManagedBean(name = "tipoLogradouroControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoLogradouro", pattern = "/tributario/cadastromunicipal/tipologradouro/novo/",
                viewId = "/faces/tributario/cadastromunicipal/tipologradouro/edita.xhtml"),

        @URLMapping(id = "editarTipoLogradouro", pattern = "/tributario/cadastromunicipal/tipologradouro/editar/#{tipoLogradouroControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/tipologradouro/edita.xhtml"),

        @URLMapping(id = "listarTipoLogradouro", pattern = "/tributario/cadastromunicipal/tipologradouro/listar/",
                viewId = "/faces/tributario/cadastromunicipal/tipologradouro/lista.xhtml"),

        @URLMapping(id = "verTipoLogradouro", pattern = "/tributario/cadastromunicipal/tipologradouro/ver/#{tipoLogradouroControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/tipologradouro/visualizar.xhtml")})
public class TipoLogradouroControlador extends PrettyControlador<TipoLogradouro> implements Serializable, CRUD {

    @EJB
    private TipoLogradouroFacade facade;

    public TipoLogradouroControlador() {
        super(TipoLogradouro.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/cadastromunicipal/tipologradouro/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoTipoLogradouro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoLogradouro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoLogradouro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                if (operacao == Operacoes.NOVO) {
                    getFacede().salvarNovo(selecionado);
                } else {
                    getFacede().salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);

            }
            redireciona();
        }
    }

    public boolean validaCampos() {
        boolean valida = true;
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "A Descrição é um campo obrigatório.");
        } else if (facade.existeValorCampo("descricao", selecionado.getDescricao(), selecionado.getId())) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "Descrição já existe em outro Tipo de Logradouro.");
        }

        if (selecionado.getSigla() == null || selecionado.getSigla().trim().equals("")) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "A Sigla é um campo obrigatório.");
        } else if (facade.existeValorCampo("sigla", selecionado.getSigla(), selecionado.getId())) {
            valida = false;
            FacesUtil.addWarn("Atenção!", "A Sigla já existe em outro Tipo de Logradouro.");
        }
        return valida;
    }
}







