package br.com.webpublico.controle;

import br.com.webpublico.entidades.ProdutoFeira;
import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.CategoriaProdutoFeira;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProdutoFeiraFacade;
import br.com.webpublico.negocios.UnidadeMedidaFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-produto-feira", pattern = "/produto-feira/novo/", viewId = "/faces/administrativo/feiras/produto/edita.xhtml"),
    @URLMapping(id = "editar-produto-feira", pattern = "/produto-feira/editar/#{produtoFeiraControlador.id}/", viewId = "/faces/administrativo/feiras/produto/edita.xhtml"),
    @URLMapping(id = "listar-produto-feira", pattern = "/produto-feira/listar/", viewId = "/faces/administrativo/feiras/produto/lista.xhtml"),
    @URLMapping(id = "ver-produto-feira", pattern = "/produto-feira/ver/#{produtoFeiraControlador.id}/", viewId = "/faces/administrativo/feiras/produto/visualizar.xhtml"),
})
public class ProdutoFeiraControlador extends PrettyControlador<ProdutoFeira> implements Serializable, CRUD {
    @EJB
    private ProdutoFeiraFacade facade;
    @EJB
    private UnidadeMedidaFacade unidadeMedidaFacade;

    public ProdutoFeiraControlador() {
        super(ProdutoFeira.class);
    }

    @Override
    @URLAction(mappingId = "novo-produto-feira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-produto-feira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-produto-feira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/produto-feira/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        validarCampos();
        return super.validaRegrasEspecificas();
    }

    public List<SelectItem> getCategorias() {
        return Util.getListSelectItemSemCampoVazio(CategoriaProdutoFeira.values(), false);
    }

    public List<UnidadeMedida> completarUnidadeMedida(String parte) {
        return unidadeMedidaFacade.buscarUnidadeMedidaAtiva(parte.trim());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Valor deve ser maior que ZERO.");
        }
        ve.lancarException();
    }

}
