/*
 * Codigo gerado automaticamente em Wed Aug 03 16:05:53 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoRegime;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoRegimeFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipoRegimeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoTipoRegime", pattern = "/tipo-de-regime/novo/", viewId = "/faces/rh/administracaodepagamento/tiporegime/edita.xhtml"),
        @URLMapping(id = "listaTipoRegime", pattern = "/tipo-de-regime/listar/", viewId = "/faces/rh/administracaodepagamento/tiporegime/lista.xhtml"),
        @URLMapping(id = "verTipoRegime", pattern = "/tipo-de-regime/ver/#{tipoRegimeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tiporegime/visualizar.xhtml"),
        @URLMapping(id = "editarTipoRegime", pattern = "/tipo-de-regime/editar/#{tipoRegimeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/tiporegime/edita.xhtml"),
})
public class TipoRegimeControlador extends PrettyControlador<TipoRegime> implements Serializable, CRUD {

    @EJB
    private TipoRegimeFacade tipoRegimeFacade;

    public TipoRegimeControlador() {
        super(TipoRegime.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoRegimeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-de-regime/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoTipoRegime", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        getSelecionado().setCodigo(tipoRegimeFacade.retornaUltimoCodigoLong());
    }

    @URLAction(mappingId = "verTipoRegime", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoRegime", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (operacao == Operacoes.NOVO) {
            Long novoCodigo = tipoRegimeFacade.retornaUltimoCodigoLong();
            if (!novoCodigo.equals(getSelecionado().getCodigo())) {
                FacesUtil.addInfo("Atenção!","O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                getSelecionado().setCodigo(novoCodigo);
            }
        }
        super.salvar();
    }
}
