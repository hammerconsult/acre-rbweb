package br.com.webpublico.controle.administrativo;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.RepresentanteLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.administrativo.RepresentanteLicitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by carlos on 25/04/17.
 */
@ManagedBean(name = "representanteLicitacaoControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "listarRepresentanteLicitacao", pattern = "/representante-licitacao/listar/", viewId = "/faces/administrativo/licitacao/representantelicitacao/lista.xhtml"),
        @URLMapping(id = "criarRepresentanteLicitacao", pattern = "/representante-licitacao/novo/", viewId = "/faces/administrativo/licitacao/representantelicitacao/edita.xhtml"),
        @URLMapping(id = "editarRepresentanteLicitacao", pattern = "/representante-licitacao/editar/#{representanteLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/representantelicitacao/edita.xhtml"),
        @URLMapping(id = "verRepresentanteLicitacao", pattern = "/representante-licitacao/ver/#{representanteLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/representantelicitacao/visualizar.xhtml")
    }
)
public class RepresentanteLicitacaoControlador extends PrettyControlador<RepresentanteLicitacao> implements Serializable, CRUD {

    @EJB
    private RepresentanteLicitacaoFacade representanteLicitacaoFacade;

    public RepresentanteLicitacaoControlador() {
        super(RepresentanteLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return representanteLicitacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/representante-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "criarRepresentanteLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarRepresentanteLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verRepresentanteLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCpf();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void validarCpf() {
        ValidacaoException ve = new ValidacaoException();
        if (!Util.validarCpf((selecionado).getCpf())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado é inválido.");
        }
        if (!representanteLicitacaoFacade.verificarDuplicidadeCpf(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CPF digitado já está cadastrado para o representante "
                + representanteLicitacaoFacade.buscarRepresentanteLicitacaoPorCpf(selecionado.getCpf()).getNome());
        }

        ve.lancarException();
    }
}
