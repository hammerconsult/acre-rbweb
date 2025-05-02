package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ProdutoServicoBancario;
import br.com.webpublico.nfse.facades.ProdutoServicoBancarioFacade;
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
    @URLMapping(id = "listarProdutoServicoBancario", pattern = "/tributario/nfse/produto-servico-bancario/listar/", viewId = "/faces/tributario/nfse/produto-servico-bancario/lista.xhtml"),
    @URLMapping(id = "verProdutoServicoBancario", pattern = "/tributario/nfse/produto-servico-bancario/ver/#{produtoServicoBancarioControlador.id}/", viewId = "/faces/tributario/nfse/produto-servico-bancario/visualizar.xhtml")
})
public class ProdutoServicoBancarioControlador extends PrettyControlador<ProdutoServicoBancario> implements Serializable, CRUD {

    @EJB
    private ProdutoServicoBancarioFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ProdutoServicoBancarioControlador() {
        super(ProdutoServicoBancario.class);
    }

    @URLAction(mappingId = "verProdutoServicoBancario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/produto-servico-bancario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
