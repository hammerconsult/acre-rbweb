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
import br.com.webpublico.nfse.domain.perguntasrespostas.PerguntasRespostas;
import br.com.webpublico.nfse.enums.TipoPerguntasRespostas;
import br.com.webpublico.nfse.facades.AssuntoNfseFacade;
import br.com.webpublico.nfse.facades.PerguntasRespostasFacade;
import br.com.webpublico.util.Util;
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

@ManagedBean(name = "perguntasRespostasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "perguntasRespostasNovo", pattern = "/nfse/perguntas-respostas/novo/", viewId = "/faces/tributario/nfse/perguntasrespostas/perguntas-respostas/edita.xhtml"),
    @URLMapping(id = "perguntasRespostasListar", pattern = "/nfse/perguntas-respostas/listar/", viewId = "/faces/tributario/nfse/perguntasrespostas/perguntas-respostas/lista.xhtml"),
    @URLMapping(id = "perguntasRespostasEditar", pattern = "/nfse/perguntas-respostas/editar/#{perguntasRespostasControlador.id}/", viewId = "/faces/tributario/nfse/perguntasrespostas/perguntas-respostas/edita.xhtml"),
    @URLMapping(id = "perguntasRespostasVer", pattern = "/nfse/perguntas-respostas/ver/#{perguntasRespostasControlador.id}/", viewId = "/faces/tributario/nfse/perguntasrespostas/perguntas-respostas/visualizar.xhtml"),
})
public class PerguntasRespostasControlador extends PrettyControlador<PerguntasRespostas> implements Serializable, CRUD {

    @EJB
    private PerguntasRespostasFacade perguntasRespostasFacade;
    @EJB
    private AssuntoNfseFacade assuntoNfseFacade;

    private List<SelectItem> assuntosSelectItem;


    public PerguntasRespostasControlador() {
        super(PerguntasRespostas.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/perguntas-respostas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return perguntasRespostasFacade;
    }


    public List<SelectItem> getAssuntosSelectItem() {
        if (assuntosSelectItem == null) {
            assuntosSelectItem = new ArrayList<SelectItem>();
            assuntosSelectItem.add(new SelectItem(null, ""));
            for (AssuntoNfse assunto : assuntoNfseFacade.recuperarAssuntosOrdenadoPorOrdem()) {
                assuntosSelectItem.add(new SelectItem(assunto, assunto.toString()));
            }
        }
        return assuntosSelectItem;
    }

    public List<SelectItem> getTiposPerguntas() {
        return Util.getListSelectItem(TipoPerguntasRespostas.values());
    }

    @Override
    @URLAction(mappingId = "perguntasRespostasNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setHabilitarExibicao(true);
    }

    @Override
    @URLAction(mappingId = "perguntasRespostasEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "perguntasRespostasVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
