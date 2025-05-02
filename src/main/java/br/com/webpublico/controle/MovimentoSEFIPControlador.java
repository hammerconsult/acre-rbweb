/*
 * Codigo gerado automaticamente em Wed Apr 04 11:08:47 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MovimentoSEFIP;
import br.com.webpublico.enums.TipoMovimentoSEFIP;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MovimentoSEFIPFacade;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.interfaces.CRUD;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMovimentoSEFIP", pattern = "/movimento-sefip/novo/", viewId = "/faces/rh/administracaodepagamento/movimentosefip/edita.xhtml"),
        @URLMapping(id = "editarMovimentoSEFIP", pattern = "/movimento-sefip/editar/#{movimentoSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/movimentosefip/edita.xhtml"),
        @URLMapping(id = "listarMovimentoSEFIP", pattern = "/movimento-sefip/listar/", viewId = "/faces/rh/administracaodepagamento/movimentosefip/lista.xhtml"),
        @URLMapping(id = "verMovimentoSEFIP", pattern = "/movimento-sefip/ver/#{movimentoSEFIPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/movimentosefip/visualizar.xhtml")
})
public class MovimentoSEFIPControlador extends PrettyControlador<MovimentoSEFIP> implements Serializable, CRUD {

    @EJB
    private MovimentoSEFIPFacade movimentoSEFIPFacade;

    public MovimentoSEFIPControlador() {
        metadata = new EntidadeMetaData(MovimentoSEFIP.class);
    }

    @URLAction(mappingId = "novoMovimentoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "verMovimentoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarMovimentoSEFIP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void salvar() {
        super.salvar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<SelectItem> getListaTipoMovimentoSEFIP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoMovimentoSEFIP tipo : TipoMovimentoSEFIP.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public MovimentoSEFIPFacade getFacade() {
        return movimentoSEFIPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return movimentoSEFIPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/movimento-sefip/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
