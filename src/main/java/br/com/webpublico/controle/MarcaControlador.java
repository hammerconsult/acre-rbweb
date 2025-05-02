/*
 * Codigo gerado automaticamente em Tue Oct 18 14:33:52 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Marca;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoMarca;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MarcaFacade;
import br.com.webpublico.negocios.MaterialFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ManagedBean(name = "marcaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMarca", pattern = "/marca/novo/", viewId = "/faces/administrativo/materiais/marca/edita.xhtml"),
        @URLMapping(id = "editarMarca", pattern = "/marca/editar/#{marcaControlador.id}/", viewId = "/faces/administrativo/materiais/marca/edita.xhtml"),
        @URLMapping(id = "listarMarca", pattern = "/marca/listar/", viewId = "/faces/administrativo/materiais/marca/lista.xhtml"),
        @URLMapping(id = "verMarca", pattern = "/marca/ver/#{marcaControlador.id}/", viewId = "/faces/administrativo/materiais/marca/visualizar.xhtml")
})
public class MarcaControlador extends PrettyControlador<Marca> implements Serializable, CRUD {

    @EJB
    private MarcaFacade marcaFacade;
    @EJB
    private MaterialFacade materialFacade;
    private Boolean permitirAlterarCategoria;

    public MarcaControlador() {
        super(Marca.class);
    }

    public MarcaFacade getFacade() {
        return marcaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return marcaFacade;
    }

    @Override
    public void excluir() {
        if (validaExclusao()) {
            super.excluir();
        }
    }

    private boolean validaExclusao() {
        boolean validou = true;

        if (!validaSeMarcaEstaSendoUtilizada()) {
            validou = false;
        }

        return validou;
    }

    private boolean validaSeMarcaEstaSendoUtilizada() {
        if (selecionadoPossuiVinculoComMaterial()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A marca selecionada está vinculada a um ou mais materiais.");
            return false;
        }

        if (selecionadoPossuiVinculoComModelo()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A marca selecionada está vinculada a um ou mais modelos.");
            return false;
        }

        return true;
    }

    private boolean selecionadoPossuiVinculoComMaterial() {
        if (materialFacade.buscarMateriaisPorMarca(selecionado).isEmpty()) {
            return false;
        }

        return true;
    }

    private boolean selecionadoPossuiVinculoComModelo() {
        if (marcaFacade.recuperarModelos(selecionado).isEmpty()) {
            return false;
        }

        return true;
    }

    public List<SelectItem> tiposDeMarca() {
        return Util.getListSelectItem(Arrays.asList(TipoMarca.values()));
    }

    public List<Marca> completarMarca(String parte) {
        return marcaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<Marca> completarMarcaTipoMaterialPermanente(String parte) {
        return marcaFacade.recuperarMarcasPeloTipo(parte, TipoMarca.MARCA_MATERIAL_PERMANENTE);
    }

    public List<Marca> completarMarcaTipoMaterialConsumo(String parte) {
        return marcaFacade.recuperarMarcasPeloTipo(parte, TipoMarca.MARCA_MATERIAL_CONSUMO);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/marca/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoMarca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        permitirAlterarCategoria = Boolean.TRUE;
        TipoMarca tipoMarca = (TipoMarca) Web.pegaDaSessao(TipoMarca.class);
        if (tipoMarca != null) {
            selecionado.setTipoMarca(tipoMarca);
            permitirAlterarCategoria = Boolean.FALSE;
        }
    }

    @URLAction(mappingId = "editarMarca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        permitirAlterarCategoria = Boolean.TRUE;
    }

    @URLAction(mappingId = "verMarca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public Boolean getPermitirAlterarCategoria() {
        return permitirAlterarCategoria;
    }

    public void setPermitirAlterarCategoria(Boolean permitirAlterarCategoria) {
        this.permitirAlterarCategoria = permitirAlterarCategoria;
    }

    public List<Marca> completarPorTipoCarro(String parte) {
        return marcaFacade.recuperarMarcasPeloTipo(parte, TipoMarca.CARRO);
    }
}
