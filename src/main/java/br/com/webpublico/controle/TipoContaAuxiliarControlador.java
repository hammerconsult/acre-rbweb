/*
 * Codigo gerado automaticamente em Mon Jul 02 14:00:30 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoContaAuxiliar;
import br.com.webpublico.enums.ClassificacaoContaAuxiliar;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TipoContaAuxiliarFacade;
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
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "tipoContaAuxiliarControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novotipocontaauxiliar", pattern = "/tipo-conta-auxiliar/novo/", viewId = "/faces/financeiro/clp/tipocontaauxiliar/edita.xhtml"),
        @URLMapping(id = "editartipocontaauxiliar", pattern = "/tipo-conta-auxiliar/editar/#{tipoContaAuxiliarControlador.id}/", viewId = "/faces/financeiro/clp/tipocontaauxiliar/edita.xhtml"),
        @URLMapping(id = "vertipocontaauxiliar", pattern = "/tipo-conta-auxiliar/ver/#{tipoContaAuxiliarControlador.id}/", viewId = "/faces/financeiro/clp/tipocontaauxiliar/visualizar.xhtml"),
        @URLMapping(id = "listartipocontaauxiliar", pattern = "/tipo-conta-auxiliar/listar/", viewId = "/faces/financeiro/clp/tipocontaauxiliar/lista.xhtml")
})
public class TipoContaAuxiliarControlador extends PrettyControlador<TipoContaAuxiliar> implements Serializable, CRUD {

    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;

    public TipoContaAuxiliarControlador() {
        super(TipoContaAuxiliar.class);
    }

    public TipoContaAuxiliarFacade getFacade() {
        return tipoContaAuxiliarFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return tipoContaAuxiliarFacade;
    }

    @URLAction(mappingId = "novotipocontaauxiliar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "vertipocontaauxiliar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        Util.validarCamposObrigatorios(selecionado, ve);

        if (tipoContaAuxiliarFacade.verificaCodigoExistente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código " + selecionado.getCodigo() + " já foi cadastrado!");
        }

        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();

            if (isOperacaoNovo()) {
                this.getFacede().salvarNovo(selecionado);
                FacesUtil.addInfo("Salvo com Sucesso!", "A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi salvo com sucesso!");
            } else {
                this.getFacede().salvar(selecionado);
                FacesUtil.addInfo("Alterado com Sucesso!", "A " + metadata.getNomeEntidade() + " " + selecionado.toString() + " foi alterada com sucesso!");
            }
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @URLAction(mappingId = "editartipocontaauxiliar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-conta-auxiliar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getClassificacoesContaAuxiliar() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ClassificacaoContaAuxiliar object : ClassificacaoContaAuxiliar.values()) {
            toReturn.add(new SelectItem(object, object.getCodigoDescricao()));
        }
        return toReturn;
    }

    public List<TipoContaAuxiliar> completarTiposContaAuxSiconfi(String parte) {
        return tipoContaAuxiliarFacade.listaTodos(parte, ClassificacaoContaAuxiliar.SICONFI);
    }
}
