package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoAutonomo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoAutonomoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author daniel
 */
@ManagedBean(name = "tipoAutonomoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoAutonomo", pattern = "/tipo-de-autonomo/novo/", viewId = "/faces/tributario/cadastromunicipal/tiposautonomo/edita.xhtml"),
    @URLMapping(id = "editarTipoAutonomo", pattern = "/tipo-de-autonomo/editar/#{tipoAutonomoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/tiposautonomo/edita.xhtml"),
    @URLMapping(id = "listarTipoAutonomo", pattern = "/tipo-de-autonomo/listar/", viewId = "/faces/tributario/cadastromunicipal/tiposautonomo/lista.xhtml"),
    @URLMapping(id = "verTipoAutonomo", pattern = "/tipo-de-autonomo/ver/#{tipoAutonomoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/tiposautonomo/visualizar.xhtml")
})
public class TipoAutonomoControlador extends PrettyControlador<TipoAutonomo> implements Serializable, CRUD {

    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;

    public TipoAutonomoControlador() {
        super(TipoAutonomo.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-autonomo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAutonomoFacade;
    }

    @URLAction(mappingId = "novoTipoAutonomo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarTipoAutonomo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verTipoAutonomo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        if (selecionado.getCodigo() == null) {
            retorno = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o Código");
        } else if (selecionado.getCodigo().intValue() <= 0) {
            retorno = false;
            FacesUtil.addError("Atenção!", "O Código deve ser Maior que Zero");
        } else if (tipoAutonomoFacade.jaExisteCodigo(selecionado)) {
            retorno = false;
            FacesUtil.addError("Atenção", "Código já Existente");
        }
        if (selecionado.getDescricao() == null || selecionado.getDescricao().isEmpty()) {
            retorno = false;
            FacesUtil.addError("Campo Obrigatório", "Informe a descrição");
        } else if (tipoAutonomoFacade.jaExisteDescricao(selecionado)) {
            retorno = false;
            FacesUtil.addError("Atenção", "Descrição já Existente");
        }
        if (selecionado.getGeraISS() == null) {
            retorno = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o Gera ISS");
        }
        if (selecionado.getEditaEnquadramento() == null) {
            retorno = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe se esse tipo de autônomo pode editar enquadramento fiscal");
        }
        if ((selecionado.getValorUFMRB() == null) || (selecionado.getValorUFMRB().compareTo(BigDecimal.ZERO) < 0)) {
            retorno = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o Valor");
        }
        if (selecionado.getNecessitaPlacas() == null) {
            retorno = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o Necessita Placa");
        } else if (selecionado.getNecessitaPlacas() && (selecionado.getQtdPlacas() == null || selecionado.getQtdPlacas() <= 0)) {
            retorno = false;
            FacesUtil.addError("Atenção!", "A Quantidade de placas deve ser maior que ZERO");
        }
        if (selecionado.getGeraISS() != null && selecionado.getGeraISS()) {
            if (selecionado.getValorUFMRB().compareTo(BigDecimal.ZERO) <= 0) {
                retorno = false;
                FacesUtil.addError("Atenção!", "Para Tipos de Autônomo que Geram ISS o Campo Valor deve ser Maior que Zero");
            }
        }

        if (selecionado.getQuantidadeCadastroRBTrans() == null) {
            retorno = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o campo taxista.");
        }
        return retorno;
    }
}
