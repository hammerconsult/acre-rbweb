package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TipoInstituicaoFinanceira;
import br.com.webpublico.nfse.facades.TipoInstituicaoFinanceiraFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarTipoInstituicaoFinanceira", pattern = "/tributario/nfse/tipo-instituicao-financeira/listar/", viewId = "/faces/tributario/nfse/tipo-instituicao-financeira/lista.xhtml"),
    @URLMapping(id = "verTipoInstituicaoFinanceira", pattern = "/tributario/nfse/tipo-instituicao-financeira/ver/#{tipoInstituicaoFinanceiraControlador.id}/", viewId = "/faces/tributario/nfse/tipo-instituicao-financeira/visualizar.xhtml")
})
public class TipoInstituicaoFinanceiraControlador extends PrettyControlador<TipoInstituicaoFinanceira> implements Serializable, CRUD {

    @EJB
    private TipoInstituicaoFinanceiraFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public TipoInstituicaoFinanceiraControlador() {
        super(TipoInstituicaoFinanceira.class);
    }

    @URLAction(mappingId = "verTipoInstituicaoFinanceira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/tipo-instituicao-financeira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void redireciona() {
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }
}
