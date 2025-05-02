package br.com.webpublico.controle.tributario.procuradoria;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.procuradoria.Vara;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.procuradoria.VaraFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 20/08/15
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "vara-procuradoria-novo", pattern = "/vara-procuradoria/novo/", viewId = "/faces/tributario/dividaativa/vara/edita.xhtml"),
        @URLMapping(id = "vara-procuradoria-edita", pattern = "/vara-procuradoria/editar/#{varaControlador.id}/", viewId = "/faces/tributario/dividaativa/vara/edita.xhtml"),
        @URLMapping(id = "vara-procuradoria-ver", pattern = "/vara-procuradoria/ver/#{varaControlador.id}/", viewId = "/faces/tributario/dividaativa/vara/visualizar.xhtml"),
        @URLMapping(id = "vara-procuradoria-listar", pattern = "/vara-procuradoria/listar/", viewId = "/faces/tributario/dividaativa/vara/lista.xhtml")
})
public class VaraControlador extends PrettyControlador<Vara> implements Serializable, CRUD {
    @EJB
    private VaraFacade varaFacade;

    public VaraControlador() {
        super(Vara.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return varaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/vara-procuradoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "vara-procuradoria-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "vara-procuradoria-edita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();

    }

    @Override
    @URLAction(mappingId = "vara-procuradoria-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (valida()) {
            super.salvar();
        }
    }

    public boolean valida() {
        boolean retorno = true;
        if (selecionado.getNumero() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe o número da Vara");
            retorno = false;
        }
        if (selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("Informe a descrição da Vara");
            retorno = false;
        }
        return retorno;
    }
}
