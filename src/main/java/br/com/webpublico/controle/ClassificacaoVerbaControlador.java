package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClassificacaoVerba;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClassificacaoVerbaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-classificacao-verba", pattern = "/classificacao-verba/novo/", viewId = "/faces/rh/administracaodepagamento/classificacaoverba/edita.xhtml"),
    @URLMapping(id = "editar-classificacao-verba", pattern = "/classificacao-verba/editar/#{classificacaoVerbaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/classificacaoverba/edita.xhtml"),
    @URLMapping(id = "ver-classificacao-verba", pattern = "/classificacao-verba/ver/#{classificacaoVerbaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/classificacaoverba/visualizar.xhtml"),
    @URLMapping(id = "listar-classificacao-verba", pattern = "/classificacao-verba/listar/", viewId = "/faces/rh/administracaodepagamento/classificacaoverba/lista.xhtml"),
})
public class ClassificacaoVerbaControlador extends PrettyControlador<ClassificacaoVerba> implements Serializable, CRUD {

    @EJB
    private ClassificacaoVerbaFacade facade;

    public ClassificacaoVerbaControlador() {
        super(ClassificacaoVerba.class);
    }

    @URLAction(mappingId = "novo-classificacao-verba", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-classificacao-verba", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-classificacao-verba", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasCodigoCadastrado(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código informado já está cadastrado.");
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/classificacao-verba/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
