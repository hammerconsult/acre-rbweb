/*
 * Codigo gerado automaticamente em Wed Apr 04 09:45:09 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MotivoAfastamentoRais;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MotivoAfastamentoRaisFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "motivoAfastamentoRaisControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMotivoAfastamentoRais", pattern = "/rh/motivo-afastamento-rais/novo/", viewId = "/faces/rh/administracaodepagamento/motivoafastamentorais/edita.xhtml"),
        @URLMapping(id = "listaMotivoAfastamentoRais", pattern = "/rh/motivo-afastamento-rais/listar/", viewId = "/faces/rh/administracaodepagamento/motivoafastamentorais/lista.xhtml"),
        @URLMapping(id = "verMotivoAfastamentoRais", pattern = "/rh/motivo-afastamento-rais/ver/#{motivoAfastamentoRaisControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivoafastamentorais/visualizar.xhtml"),
        @URLMapping(id = "editarMotivoAfastamentoRais", pattern = "/rh/motivo-afastamento-rais/editar/#{motivoAfastamentoRaisControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivoafastamentorais/edita.xhtml"),
})
public class MotivoAfastamentoRaisControlador extends PrettyControlador<MotivoAfastamentoRais> implements Serializable, CRUD {

    @EJB
    private MotivoAfastamentoRaisFacade motivoAfastamentoRaisFacade;

    public MotivoAfastamentoRaisControlador() {
        super(MotivoAfastamentoRais.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return motivoAfastamentoRaisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/motivo-afastamento-rais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoMotivoAfastamentoRais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verMotivoAfastamentoRais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarMotivoAfastamentoRais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }
}
