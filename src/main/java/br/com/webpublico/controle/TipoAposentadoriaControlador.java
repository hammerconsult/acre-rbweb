/*
 * Codigo gerado automaticamente em Tue Feb 14 08:43:52 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MotivoExoneracaoRescisao;
import br.com.webpublico.entidades.TipoAposentadoria;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MotivoExoneracaoRescisaoFacade;
import br.com.webpublico.negocios.TipoAposentadoriaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "tipoAposentadoriaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoAposentadoria", pattern = "/rh/tipo-de-aposentadoria/novo/", viewId = "/faces/rh/administracaodepagamento/tipoaposentadoria/edita.xhtml"),
        @URLMapping(id = "listaTipoAposentadoria", pattern = "/rh/tipo-de-aposentadoria/listar/", viewId = "/faces/rh/administracaodepagamento/tipoaposentadoria/lista.xhtml"),
        @URLMapping(id = "verTipoAposentadoria", pattern = "/rh/tipo-de-aposentadoria/ver/#{tipoAposentadoriaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoaposentadoria/visualizar.xhtml"),
        @URLMapping(id = "editarTipoAposentadoria", pattern = "/rh/tipo-de-aposentadoria/editar/#{tipoAposentadoriaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tipoaposentadoria/edita.xhtml"),
})
public class TipoAposentadoriaControlador extends PrettyControlador<TipoAposentadoria> implements Serializable, CRUD {

    @EJB
    private TipoAposentadoriaFacade tipoAposentadoriaFacade;
    @EJB
    private MotivoExoneracaoRescisaoFacade motivoExoneracaoRescisaoFacade;

    public TipoAposentadoriaControlador() {
        super(TipoAposentadoria.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoAposentadoriaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/tipo-de-aposentadoria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean validaCampos() {
        if (tipoAposentadoriaFacade.existeCodigo(selecionado)) {
            FacesUtil.addWarn("Atenção !", "O código digitado já é utilizado por outro registro.");
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoTipoAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        Integer codigo = tipoAposentadoriaFacade.getMaxCodigo();
        selecionado.setCodigo(codigo + "");
    }

    @URLAction(mappingId = "verTipoAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoAposentadoria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public List<MotivoExoneracaoRescisao> completarMotivoExoneracaoRescisao(String parte) {
        return motivoExoneracaoRescisaoFacade.listaFiltrandoCodigoDescricao(parte);
    }
}
