package br.com.webpublico.nfse.controladores;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.Legislacao;
import br.com.webpublico.nfse.domain.TipoLegislacao;
import br.com.webpublico.nfse.facades.LegislacaoFacade;
import br.com.webpublico.nfse.facades.TipoLegislacaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "legislacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "legislacaoNovo", pattern = "/nfse/legislacao/novo/", viewId = "/faces/tributario/nfse/legislacao/edita.xhtml"),
    @URLMapping(id = "legislacaoListar", pattern = "/nfse/legislacao/listar/", viewId = "/faces/tributario/nfse/legislacao/lista.xhtml"),
    @URLMapping(id = "legislacaoEditar", pattern = "/nfse/legislacao/editar/#{legislacaoControlador.id}/", viewId = "/faces/tributario/nfse/legislacao/edita.xhtml"),
    @URLMapping(id = "legislacaoVer", pattern = "/nfse/legislacao/ver/#{legislacaoControlador.id}/", viewId = "/faces/tributario/nfse/legislacao/visualizar.xhtml"),
})
public class LegislacaoControlador extends PrettyControlador<Legislacao> implements Serializable, CRUD {

    @EJB
    private LegislacaoFacade legislacaoFacade;
    @EJB
    private TipoLegislacaoFacade tipoLegislacaoFacade;


    public LegislacaoControlador() {
        super(Legislacao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/legislacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return legislacaoFacade;
    }

    @Override
    @URLAction(mappingId = "legislacaoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setHabilitarExibicao(true);
    }

    @Override
    @URLAction(mappingId = "legislacaoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "legislacaoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<TipoLegislacao> buscarTiposLegislacoes(String partes) {
        return tipoLegislacaoFacade.listaFiltrando(partes.trim(), "descricao");
    }
}
