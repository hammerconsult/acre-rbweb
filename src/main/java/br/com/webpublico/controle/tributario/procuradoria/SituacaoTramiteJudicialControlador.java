package br.com.webpublico.controle.tributario.procuradoria;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.procuradoria.SituacaoTramiteJudicial;
import br.com.webpublico.entidades.tributario.procuradoria.Vara;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.procuradoria.SituacaoTramiteJudicialFacade;
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
        @URLMapping(id = "situacao-tramite-novo", pattern = "/situacao-tramite/novo/", viewId = "/faces/tributario/dividaativa/situacaotramitejudicial/edita.xhtml"),
        @URLMapping(id = "situacao-tramite-edita", pattern = "/situacao-tramite/editar/#{situacaoTramiteJudicialControlador.id}/", viewId = "/faces/tributario/dividaativa/situacaotramitejudicial/edita.xhtml"),
        @URLMapping(id = "situacao-tramite-ver", pattern = "/situacao-tramite/ver/#{situacaoTramiteJudicialControlador.id}/", viewId = "/faces/tributario/dividaativa/situacaotramitejudicial/visualizar.xhtml"),
        @URLMapping(id = "situacao-tramite-listar", pattern = "/situacao-tramite/listar/", viewId = "/faces/tributario/dividaativa/situacaotramitejudicial/lista.xhtml")
})
public class SituacaoTramiteJudicialControlador extends PrettyControlador<SituacaoTramiteJudicial> implements Serializable, CRUD {
    @EJB
    private SituacaoTramiteJudicialFacade situacaoTramiteJudicialFacade;

    public SituacaoTramiteJudicialControlador() {
        super(SituacaoTramiteJudicial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return situacaoTramiteJudicialFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/situacao-tramite/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "situacao-tramite-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "situacao-tramite-edita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();

    }

    @Override
    @URLAction(mappingId = "situacao-tramite-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo Descrição é obrigatório");
            retorno = false;
        }
        if (selecionado.getEnviaEmail()) {
            if (selecionado.getConteudoEmail().trim().equals("")) {
                FacesUtil.addOperacaoNaoPermitida("O campo Conteúdo do Email é obrigatório");
                retorno = false;
            }

        }
        return retorno;
    }
}
