/*
 * Codigo gerado automaticamente em Wed Jan 18 11:33:14 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.NaturezaRendimento;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.NaturezaRendimentoFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "naturezaRendimentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoNaturezaRendimento", pattern = "/rh/natureza-de-rendimento-da-dirf/novo/", viewId = "/faces/rh/administracaodepagamento/naturezarendimento/edita.xhtml"),
        @URLMapping(id = "listaNaturezaRendimento", pattern = "/rh/natureza-de-rendimento-da-dirf/listar/", viewId = "/faces/rh/administracaodepagamento/naturezarendimento/lista.xhtml"),
        @URLMapping(id = "verNaturezaRendimento", pattern = "/rh/natureza-de-rendimento-da-dirf/ver/#{naturezaRendimentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/naturezarendimento/visualizar.xhtml"),
        @URLMapping(id = "editarNaturezaRendimento", pattern = "/rh/natureza-de-rendimento-da-dirf/editar/#{naturezaRendimentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/naturezarendimento/edita.xhtml"),
})
public class NaturezaRendimentoControlador extends PrettyControlador<NaturezaRendimento> implements Serializable, CRUD {

    @EJB
    private NaturezaRendimentoFacade naturezaRendimentoFacade;

    public NaturezaRendimentoControlador() {
        super(NaturezaRendimento.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/natureza-de-rendimento-da-dirf/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return naturezaRendimentoFacade;
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    @URLAction(mappingId = "novoNaturezaRendimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verNaturezaRendimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarNaturezaRendimento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }
}
