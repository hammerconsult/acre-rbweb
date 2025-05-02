/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AgenteAutuador;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AgenteAutuadorFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean(name = "agenteAutuadorControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAgenteAutuador",
        pattern = "/rbtrans/agente-autuador/novo/",
        viewId = "/faces/tributario/rbtrans/agenteautuador/edita.xhtml"),

    @URLMapping(id = "editarAgenteAutuador",
        pattern = "/rbtrans/agente-autuador/editar/#{agenteAutuadorControlador.id}/",
        viewId = "/faces/tributario/rbtrans/agenteautuador/edita.xhtml"),

    @URLMapping(id = "listarAgenteAutuador",
        pattern = "/rbtrans/agente-autuador/listar/",
        viewId = "/faces/tributario/rbtrans/agenteautuador/lista.xhtml"),

    @URLMapping(id = "verAgenteAutuador",
        pattern = "/rbtrans/agente-autuador/ver/#{agenteAutuadorControlador.id}/",
        viewId = "/faces/tributario/rbtrans/agenteautuador/visualizar.xhtml")})
public class AgenteAutuadorControlador extends PrettyControlador<AgenteAutuador> implements Serializable, CRUD {

    @EJB
    private AgenteAutuadorFacade agenteAutuadorFacade;

    public AgenteAutuadorControlador() {
        super(AgenteAutuador.class);
        metadata = new EntidadeMetaData(AgenteAutuador.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return agenteAutuadorFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rbtrans/agente-autuador/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoAgenteAutuador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = (AgenteAutuador) Web.pegaDaSessao(AgenteAutuador.class);
        if (selecionado == null) {
            super.novo();
        }
    }

    @URLAction(mappingId = "verAgenteAutuador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAgenteAutuador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        selecionado = (AgenteAutuador) Web.pegaDaSessao(AgenteAutuador.class);
        if (selecionado == null) {
            super.editar();
        }
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getPessoaFisica() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar a Pessoa!");
        }
        ve.lancarException();
    }

    public List<PessoaFisica> completarPessoaFisica(String parte) {
        return agenteAutuadorFacade.completarPessoasComContratoFP(parte);
    }

}
