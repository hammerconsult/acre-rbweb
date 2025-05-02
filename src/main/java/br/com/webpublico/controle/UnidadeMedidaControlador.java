/*
 * Codigo gerado automaticamente em Thu Mar 31 11:54:43 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UnidadeMedidaFacade;
import br.com.webpublico.util.ConverterMascaraUnidadeMedida;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novounidademedida", pattern = "/unidademedida/novo/", viewId = "/faces/financeiro/ppa/unidademedida/edita.xhtml"),
    @URLMapping(id = "editarunidademedida", pattern = "/unidademedida/editar/#{unidadeMedidaControlador.id}/", viewId = "/faces/financeiro/ppa/unidademedida/edita.xhtml"),
    @URLMapping(id = "verunidademedida", pattern = "/unidademedida/ver/#{unidadeMedidaControlador.id}/", viewId = "/faces/financeiro/ppa/unidademedida/visualizar.xhtml"),
    @URLMapping(id = "listarunidademedida", pattern = "/unidademedida/listar/", viewId = "/faces/financeiro/ppa/unidademedida/lista.xhtml")
})
public class UnidadeMedidaControlador extends PrettyControlador<UnidadeMedida> implements Serializable, CRUD {

    @EJB
    private UnidadeMedidaFacade facade;

    public UnidadeMedidaControlador() {
        super(UnidadeMedida.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/unidademedida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novounidademedida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarunidademedida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "verunidademedida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void recuperarEditarVer() {
        selecionado = facade.recuperar(selecionado.getId());
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarSiglaRepetida();
            facade.salvarRetornando(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarSiglaRepetida() {
        ValidacaoException ve = new ValidacaoException();
        List<UnidadeMedida> unidades = facade.verificarSeExisteDescricaoOrSiglaRepetida(
            selecionado.getSigla(),
            selecionado.getDescricao(),
            selecionado.getId());
        if (!Util.isListNullOrEmpty(unidades)) {
            unidades.stream()
                .map(unid -> "A descrição ou sigla já esta sendo utilizada na unidade de medida " + unid.getDescricaoAndSigla()
                    + ". " + Util.linkBlack("/unidademedida/ver/" + unid.getId() + "/", "Clique aqui para visualizar."))
                .forEach(ve::adicionarMensagemDeOperacaoNaoPermitida);
        }
        ve.lancarException();
    }

    public ConverterMascaraUnidadeMedida getConverterMascaraUnidadeMedida(String mascara) {
        if (!Strings.isNullOrEmpty(mascara)) {
            return new ConverterMascaraUnidadeMedida(mascara);
        }
        return new ConverterMascaraUnidadeMedida(TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL.getMascara());
    }

    public List<SelectItem> getUnidadesMedida() {
        List<UnidadeMedida> list = facade.buscarUnidadeMedidaAtiva();
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (UnidadeMedida un : list) {
            String sigla = !Strings.isNullOrEmpty(un.getSigla()) ? " - " + un.getSigla() : "";
            retorno.add(new SelectItem(un, un.getDescricao() + sigla));
        }
        return retorno;
    }

    public List<SelectItem> getUnidadesMedidas(TipoObjetoCompra tipoObjetoCompra) {
        List<UnidadeMedida> list = facade.buscarUnidadeMedidaAtiva(tipoObjetoCompra);
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (UnidadeMedida un : list) {
            String sigla = !Strings.isNullOrEmpty(un.getSigla()) ? " - " + un.getSigla() : "";
            retorno.add(new SelectItem(un, un.getDescricao() + sigla));
        }
        return retorno;
    }

    public List<SelectItem> getMascarasUnidadeMedidaQuantidade() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoMascaraUnidadeMedida value : TipoMascaraUnidadeMedida.values()) {
            retorno.add(new SelectItem(value, value.getExemploDescricaoLonga()));
        }
        return retorno;
    }

    public List<SelectItem> getMascarasUnidadeMedidaValorUnitario() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoMascaraUnidadeMedida value : TipoMascaraUnidadeMedida.getMascarasValorUnitario()) {
            retorno.add(new SelectItem(value, value.getExemploDescricaoLonga()));
        }
        return retorno;
    }

}
