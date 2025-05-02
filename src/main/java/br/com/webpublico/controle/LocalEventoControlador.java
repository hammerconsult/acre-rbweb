package br.com.webpublico.controle;

import br.com.webpublico.entidades.LocalEvento;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LocalEventoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by carlos on 25/05/15.
 */
@ManagedBean(name = "localEventoControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarLocalEvento", pattern = "/local-evento/listar/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/localevento/lista.xhtml"),
                @URLMapping(id = "criarLocalEvento", pattern = "/local-evento/novo/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/localevento/edita.xhtml"),
                @URLMapping(id = "editarLocalEvento", pattern = "/local-evento/editar/#{localEventoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/localevento/edita.xhtml"),
                @URLMapping(id = "verLocalEvento", pattern = "/local-evento/ver/#{localEventoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/localevento/visualizar.xhtml")
        }
)
public class LocalEventoControlador extends PrettyControlador<LocalEvento> implements CRUD {
    @EJB
    private LocalEventoFacade localEventoFacade;

    @Override
    public String getCaminhoPadrao() {
        return "/local-evento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "criarLocalEvento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verLocalEvento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarLocalEvento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public LocalEventoControlador() {
        super(LocalEvento.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return localEventoFacade;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public boolean validaCampos() {
        boolean valida = true;
        if (selecionado.getCodigo().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O Código é um campo obrigatório!");
            valida = false;
        } else if (operacao.equals(Operacoes.NOVO) && localEventoFacade.existeLocalEventoPorCodigo(selecionado.getCodigo())) {
            FacesUtil.addOperacaoNaoPermitida("O Código informado já existe!");
            valida = false;
        }
        if (selecionado.getNome().trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("O Nome é um campo obrigatório!");
            valida = false;
        } else if (operacao.equals(Operacoes.NOVO) && localEventoFacade.existeLocalEventoPorNome(selecionado.getNome())) {
            FacesUtil.addOperacaoNaoPermitida("O Nome informado já existe!");
            valida = false;
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A Descrição é um campo obrigatório!");
            valida = false;
        }
        return valida;
    }
}
