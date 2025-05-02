package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.nfse.domain.CodigoTributacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.facades.CodigoTributacaoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "codigoTributacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarCodigoTributacao", pattern = "/tributario/nfse/codigo-tributacao/listar/", viewId = "/faces/tributario/nfse/codigo-tributacao/lista.xhtml"),
    @URLMapping(id = "verCodigoTributacao", pattern = "/tributario/nfse/codigo-tributacao/ver/#{codigoTributacaoControlador.id}/", viewId = "/faces/tributario/nfse/codigo-tributacao/visualizar.xhtml")
})
public class CodigoTributacaoControlador extends PrettyControlador<CodigoTributacao> implements Serializable, CRUD {

    @EJB
    private CodigoTributacaoFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public CodigoTributacaoControlador() {
        super(CodigoTributacao.class);
    }

    @URLAction(mappingId = "verCodigoTributacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/codigo-tributacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    public List<CodigoTributacao> completarCodigoTributacao(String parte) {
        return facade.completarCodigoTributacao(parte);
    }

}
