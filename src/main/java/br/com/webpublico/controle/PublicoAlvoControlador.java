/*
 * Codigo gerado automaticamente em Wed Apr 13 11:10:12 BRT 2011
 * Gerador de Controlador
*/

package br.com.webpublico.controle;


import br.com.webpublico.entidades.PublicoAlvo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PublicoAlvoFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "publicoAlvoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-publico-alvo",   pattern = "/publico-alvo/novo/",                                viewId = "/faces/financeiro/ppa/publicoalvo/edita.xhtml"),
        @URLMapping(id = "editar-publico-alvo", pattern = "/publico-alvo/editar/#{publicoAlvoControlador.id}/", viewId = "/faces/financeiro/ppa/publicoalvo/edita.xhtml"),
        @URLMapping(id = "ver-publico-alvo",    pattern = "/publico-alvo/ver/#{publicoAlvoControlador.id}/",    viewId = "/faces/financeiro/ppa/publicoalvo/visualizar.xhtml"),
        @URLMapping(id = "listar-publico-alvo", pattern = "/publico-alvo/listar/",                              viewId = "/faces/financeiro/ppa/publicoalvo/lista.xhtml")
})
public class PublicoAlvoControlador extends PrettyControlador<PublicoAlvo> implements Serializable, CRUD {

    @EJB
    private PublicoAlvoFacade publicoAlvoFacade;

    public PublicoAlvoControlador() {
        super(PublicoAlvo.class);
    }

    public PublicoAlvoFacade getFacade() {
        return publicoAlvoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return publicoAlvoFacade;
    }


    @URLAction(mappingId = "novo-publico-alvo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-publico-alvo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-publico-alvo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/publico-alvo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
