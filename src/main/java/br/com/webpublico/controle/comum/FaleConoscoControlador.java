/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.FaleConosco;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FaleConoscoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.mail.internet.AddressException;
import java.io.Serializable;
import java.util.Date;

@ManagedBean(name = "faleConoscoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarFaleConosco", pattern = "/fale-conosco/listar/", viewId = "/faces/comum/faleconosco/lista.xhtml"),
    @URLMapping(id = "verFaleConosco", pattern = "/fale-conosco/ver/#{faleConoscoControlador.id}/", viewId = "/faces/comum/faleconosco/visualizar.xhtml")
})
public class FaleConoscoControlador extends PrettyControlador<FaleConosco> implements Serializable, CRUD {

    @EJB
    private FaleConoscoFacade faleConoscoFacade;

    public FaleConoscoControlador() {
        super(FaleConosco.class);
    }

    @URLAction(mappingId = "verFaleConosco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado.setVisualizado(Boolean.TRUE);
        selecionado.setDataResposta(new Date());
        selecionado.setUsuarioSistema(faleConoscoFacade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    public AbstractFacade getFacede() {
        return faleConoscoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fale-conosco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public void abrirDialogResponder() {
        try {
            faleConoscoFacade.validarCampoMensagemResponsta(selecionado);
            FacesUtil.executaJavaScript("responderFaleConosco.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void enviarEmail() {
        try {
            faleConoscoFacade.enviarEmail(selecionado);
            FacesUtil.executaJavaScript("responderFaleConosco.hide()");
            FacesUtil.addOperacaoRealizada("Resposta Enviada com Sucesso!");
            redireciona();
        } catch (AddressException e) {
            FacesUtil.addOperacaoNaoRealizada("O e-mail informado é invalido!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível enviar o e-mail: " + e);
        }
    }
}
