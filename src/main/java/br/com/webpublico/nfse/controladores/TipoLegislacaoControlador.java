package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TipoLegislacao;
import br.com.webpublico.nfse.facades.TipoLegislacaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "tipolegislacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "tipoLegislacaoNovo", pattern = "/nfse/tipo-legislacao/novo/", viewId = "/faces/tributario/nfse/tipo-legislacao/edita.xhtml"),
    @URLMapping(id = "tipoLegislacaoListar", pattern = "/nfse/tipo-legislacao/listar/", viewId = "/faces/tributario/nfse/tipo-legislacao/lista.xhtml"),
    @URLMapping(id = "tipoLegislacaoEditar", pattern = "/nfse/tipo-legislacao/editar/#{tipolegislacaoControlador.id}/", viewId = "/faces/tributario/nfse/tipo-legislacao/edita.xhtml"),
    @URLMapping(id = "tipoLegislacaoVer", pattern = "/nfse/tipo-legislacao/ver/#{tipolegislacaoControlador.id}/", viewId = "/faces/tributario/nfse/tipo-legislacao/visualizar.xhtml"),
})
public class TipoLegislacaoControlador extends PrettyControlador<TipoLegislacao> implements Serializable, CRUD {

    @EJB
    private TipoLegislacaoFacade tipoLegislacaoFacade;


    public TipoLegislacaoControlador() {
        super(TipoLegislacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/tipo-legislacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoLegislacaoFacade;
    }

    @Override
    @URLAction(mappingId = "tipoLegislacaoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setHabilitarExibicao(true);
    }

    @Override
    @URLAction(mappingId = "tipoLegislacaoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "tipoLegislacaoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}
