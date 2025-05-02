package br.com.webpublico.controle.rh.esocial;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.NaturezaRubrica;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.NaturezaRubricaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by William on 24/05/2018.
 */
@ManagedBean(name = "naturezaRubricaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-natureza-rubrica", pattern = "/e-social/natureza-rubrica/novo/", viewId = "/faces/rh/esocial/natureza-rubrica/edita.xhtml"),
    @URLMapping(id = "editar-natureza-rubrica", pattern = "/e-social/natureza-rubrica/editar/#{naturezaRubricaControlador.id}/", viewId = "/faces/rh/esocial/natureza-rubrica/edita.xhtml"),
    @URLMapping(id = "ver-natureza-rubrica", pattern = "/e-social/natureza-rubrica/ver/#{naturezaRubricaControlador.id}/", viewId = "/faces/rh/esocial/natureza-rubrica/visualizar.xhtml"),
    @URLMapping(id = "listar-natureza-rubrica", pattern = "/e-social/natureza-rubrica/listar/", viewId = "/faces/rh/esocial/natureza-rubrica/lista.xhtml")
})
public class NaturezaRubricaControlador extends PrettyControlador<NaturezaRubrica> implements CRUD {

    @EJB
    private NaturezaRubricaFacade naturezaRubricaFacade;

    public NaturezaRubricaControlador() {
        super(NaturezaRubrica.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return naturezaRubricaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/e-social/natureza-rubrica/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "nova-natureza-rubrica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editar-natureza-rubrica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-natureza-rubrica", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCodigo();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarCodigo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() != null) {
            if (naturezaRubricaFacade.hasCodigoCadastrado(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Cadastro de Natureza da Rubrica com o código <b>" + selecionado.getCodigo() + "</b>");
            }
            if (selecionado.getCodigo() != null && (selecionado.getCodigo().trim().length() < 4 || Integer.parseInt(selecionado.getCodigo()) < 0)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O código deve ter quatro digitos e deve ser maior que zero");
            }
            ve.lancarException();
        }
    }
}
