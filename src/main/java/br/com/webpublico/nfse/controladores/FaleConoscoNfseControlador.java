/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DocumentoOficialFacade;
import br.com.webpublico.negocios.FaleConoscoNfseFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.FaleConoscoNfse;
import br.com.webpublico.nfse.util.ImprimeRelatorioNfse;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.mail.internet.AddressException;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarFaleConoscoNfse", pattern = "/nfse/fale-conosco/listar/", viewId = "/faces/tributario/nfse/faleconosco/lista.xhtml"),
    @URLMapping(id = "verFaleConoscoNfse", pattern = "/nfse/fale-conosco/ver/#{faleConoscoNfseControlador.id}/", viewId = "/faces/tributario/nfse/faleconosco/visualizar.xhtml")
})
public class FaleConoscoNfseControlador extends PrettyControlador<FaleConoscoNfse> implements Serializable, CRUD {

    @EJB
    private FaleConoscoNfseFacade faleConoscoNfseFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;

    public FaleConoscoNfseControlador() {
        super(FaleConoscoNfse.class);
    }

    @URLAction(mappingId = "verFaleConoscoNfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public AbstractFacade getFacede() {
        return faleConoscoNfseFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/fale-conosco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void imprimir() {
        try {
            byte[] bytes = new ImprimeRelatorioNfse().
                gerarRelatorio("FaleConoscoNfse.jasper",
                    Lists.newArrayList(selecionado), false);
            AbstractReport abstractReport = new AbstractReport();
            abstractReport.setGeraNoDialog(true);
            abstractReport.escreveNoResponse("FaleConoscoNfse", bytes);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void responder() {
        try {
            faleConoscoNfseFacade.responder(selecionado);
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
