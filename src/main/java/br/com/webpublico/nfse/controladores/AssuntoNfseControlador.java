/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.nfse.controladores;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.perguntasrespostas.AssuntoNfse;
import br.com.webpublico.nfse.facades.AssuntoNfseFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "assuntoNfseControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "assuntoNfseNovo", pattern = "/nfse/assunto/novo/", viewId = "/faces/tributario/nfse/perguntasrespostas/assunto/edita.xhtml"),
    @URLMapping(id = "assuntoNfseListar", pattern = "/nfse/assunto/listar/", viewId = "/faces/tributario/nfse/perguntasrespostas/assunto/lista.xhtml"),
    @URLMapping(id = "assuntoNfseEditar", pattern = "/nfse/assunto/editar/#{assuntoNfseControlador.id}/", viewId = "/faces/tributario/nfse/perguntasrespostas/assunto/edita.xhtml"),
    @URLMapping(id = "assuntoNfseVer", pattern = "/nfse/assunto/ver/#{assuntoNfseControlador.id}/", viewId = "/faces/tributario/nfse/perguntasrespostas/assunto/visualizar.xhtml"),
})
public class AssuntoNfseControlador extends PrettyControlador<AssuntoNfse> implements Serializable, CRUD {

    @EJB
    private AssuntoNfseFacade assuntoNfseFacade;


    public AssuntoNfseControlador() {
        super(AssuntoNfse.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/assunto/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return assuntoNfseFacade;
    }

    @Override
    @URLAction(mappingId = "assuntoNfseNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setOrdem(assuntoNfseFacade.getProximaOrdem());
        selecionado.setHabilitarExibicao(true);
    }

    @Override
    @URLAction(mappingId = "assuntoNfseEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "assuntoNfseVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
