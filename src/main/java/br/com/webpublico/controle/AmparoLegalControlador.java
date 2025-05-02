package br.com.webpublico.controle;

import br.com.webpublico.entidades.AmparoLegal;
import br.com.webpublico.enums.LeiLicitacao;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AmparoLegalFacade;
import br.com.webpublico.util.FacesUtil;
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

@ManagedBean(name = "amparoLegalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAmparoLegal", pattern = "/amparo-legal/novo/", viewId = "/faces/administrativo/licitacao/amparo-legal/edita.xhtml"),
    @URLMapping(id = "editarAmparoLegal", pattern = "/amparo-legal/editar/#{amparoLegalControlador.id}/", viewId = "/faces/administrativo/licitacao/amparo-legal/edita.xhtml"),
    @URLMapping(id = "verAmparoLegal", pattern = "/amparo-legal/ver/#{amparoLegalControlador.id}/", viewId = "/faces/administrativo/licitacao/amparo-legal/visualizar.xhtml"),
    @URLMapping(id = "listarAmparoLegal", pattern = "/amparo-legal/listar/", viewId = "/faces/administrativo/licitacao/amparo-legal/lista.xhtml")
})
public class AmparoLegalControlador extends PrettyControlador<AmparoLegal> implements Serializable, CRUD {

    @EJB
    private AmparoLegalFacade facade;


    public AmparoLegalControlador() {
        super(AmparoLegal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/amparo-legal/" ;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoAmparoLegal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verAmparoLegal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAmparoLegal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar(Redirecionar.VER);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (ModalidadeLicitacao object : ModalidadeLicitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getLeis() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (LeiLicitacao object : LeiLicitacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() != null && facade.existeCodigo(selecionado.getId(), selecionado.getCodigo())){
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código informado já existe no sistema.");
        }
        if (selecionado.getFimVigencia()!= null) {
            if (selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O fim da vigência deve ser posterior ao início.");
            }
        }
        ve.lancarException();
    }

}
