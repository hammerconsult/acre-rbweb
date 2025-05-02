package br.com.webpublico.controle.rh.esocial;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.TipoBeneficioPrevidenciario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.TipoBeneficioPrevidenciarioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoBeneficioPrevidenciarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoBeneficioPrevidenciario", pattern = "/tipo-beneficio-previdenciario/novo/",
        viewId = "/faces/rh/esocial/tipo-beneficio-previdenciario/edita.xhtml"),
    @URLMapping(id = "editarTipoBeneficioPrevidenciario", pattern = "/tipo-beneficio-previdenciario/editar/#{tipoBeneficioPrevidenciarioControlador.id}/",
        viewId = "/faces/rh/esocial/tipo-beneficio-previdenciario/edita.xhtml"),
    @URLMapping(id = "listarTipoBeneficioPrevidenciario", pattern = "/tipo-beneficio-previdenciario/listar/",
        viewId = "/faces/rh/esocial/tipo-beneficio-previdenciario/lista.xhtml"),
    @URLMapping(id = "verTipoBeneficioPrevidenciario", pattern = "/tipo-beneficio-previdenciario/ver/#{tipoBeneficioPrevidenciarioControlador.id}/",
        viewId = "/faces/rh/esocial/tipo-beneficio-previdenciario/visualizar.xhtml")
})
public class TipoBeneficioPrevidenciarioControlador extends PrettyControlador<TipoBeneficioPrevidenciario> implements Serializable, CRUD {

    @EJB
    private TipoBeneficioPrevidenciarioFacade tipoBeneficioPrevidenciarioFacade;

    public TipoBeneficioPrevidenciarioControlador() {
        super(TipoBeneficioPrevidenciario.class);
    }

    @URLAction(mappingId = "novoTipoBeneficioPrevidenciario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verTipoBeneficioPrevidenciario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoBeneficioPrevidenciario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-beneficio-previdenciario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoBeneficioPrevidenciarioFacade;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if(tipoBeneficioPrevidenciarioFacade.validarCodigoBeneficio(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código informado já existe em nossos registros.");
        }
        ve.lancarException();
    }
}
