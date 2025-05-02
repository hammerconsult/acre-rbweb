package br.com.webpublico.controle;

import br.com.webpublico.entidades.DocumentoComprobatorioLevantamentoBemImovel;
import br.com.webpublico.entidades.EmpenhoLevantamentoImovel;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ParametroPatrimonio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DocumentoComprobatorioLevantamentoBemImovelFacade;
import br.com.webpublico.util.EntidadeMetaData;
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

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 15/09/14
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "documentoComprobatorioLevantamentoBemImovelControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDocumentoComprobatorioLevantamentoBemImovel", pattern = "/documento-comprobatorio/novo/", viewId = "/faces/administrativo/patrimonio/documentoComprobatorioLevantamentoBemImovel/edita.xhtml"),
    @URLMapping(id = "editarDocumentoComprobatorioLevantamentoBemImovel", pattern = "/documento-comprobatorio/editar/#{documentoComprobatorioLevantamentoBemImovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/documentoComprobatorioLevantamentoBemImovel/edita.xhtml"),
    @URLMapping(id = "listarDocumentoComprobatorioLevantamentoBemImovel", pattern = "/documento-comprobatorio/listar/", viewId = "/faces/administrativo/patrimonio/documentoComprobatorioLevantamentoBemImovel/lista.xhtml"),
    @URLMapping(id = "verDocumentoComprobatorioLevantamentoBemImovel", pattern = "/documento-comprobatorio/ver/#{documentoComprobatorioLevantamentoBemImovelControlador.id}/", viewId = "/faces/administrativo/patrimonio/documentoComprobatorioLevantamentoBemImovel/visualizar.xhtml")
})
public class DocumentoComprobatorioLevantamentoBemImovelControlador extends PrettyControlador<DocumentoComprobatorioLevantamentoBemImovel> implements Serializable, CRUD {

    @EJB
    private DocumentoComprobatorioLevantamentoBemImovelFacade facade;
    private EmpenhoLevantamentoImovel empenhoLevantamentoImovelSelecionado;

    public DocumentoComprobatorioLevantamentoBemImovelControlador() {
        metadata = new EntidadeMetaData(DocumentoComprobatorioLevantamentoBemImovel.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/documento-comprobatorio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoDocumentoComprobatorioLevantamentoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataVinculoLevantamento(facade.getSistemaFacade().getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "editarDocumentoComprobatorioLevantamentoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verDocumentoComprobatorioLevantamentoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            ParametroPatrimonio parametroPatrimonio = facade.getParametroPatrimonioFacade().recuperarParametro();
            selecionado.validarRegrasDeNegocio(parametroPatrimonio != null ? parametroPatrimonio.getDataDeReferencia() : null);
            selecionado.setLevantamentoBemImovel(facade.getLevantamentoBemImovelFacade().recuperar(selecionado.getLevantamentoBemImovel().getId()));
            selecionado.getLevantamentoBemImovel().podeAdicionarDocumento(selecionado);
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public EmpenhoLevantamentoImovel getEmpenhoLevantamentoImovelSelecionado() {
        return empenhoLevantamentoImovelSelecionado;
    }

    public void setEmpenhoLevantamentoImovelSelecionado(EmpenhoLevantamentoImovel empenhoLevantamentoImovelSelecionado) {
        this.empenhoLevantamentoImovelSelecionado = empenhoLevantamentoImovelSelecionado;
    }

    public List<SelectItem> retornaHierarquiaOrcamentaria() {
        if (selecionado.getUnidadeAdministrativa() != null) {
            List<SelectItem> toReturn = new ArrayList<>();
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(selecionado.getUnidadeAdministrativa(), facade.getSistemaFacade().getDataOperacao())) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }
            return toReturn;
        }
        return null;
    }

    public void regarregarLevantamentoSelecionado() {
        selecionado.setLevantamentoBemImovel(facade.getLevantamentoBemImovelFacade().recuperar(selecionado.getLevantamentoBemImovel().getId()));
    }

    public void novoEmpenho() {
        empenhoLevantamentoImovelSelecionado = new EmpenhoLevantamentoImovel();
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:numerodoempenho')");
    }

    public void adicionarEmpenho() {
        try {
            selecionado.adicionarEmpenho(empenhoLevantamentoImovelSelecionado);
            cancelarEmpenho();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerEmpenho(EmpenhoLevantamentoImovel eli) {
        selecionado.getEmpenhos().remove(eli);
    }

    public void editarEmpenho(EmpenhoLevantamentoImovel eli) {
        empenhoLevantamentoImovelSelecionado = (EmpenhoLevantamentoImovel) Util.clonarObjeto(eli);
    }

    public void cancelarEmpenho() {
        empenhoLevantamentoImovelSelecionado = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabGeral:novoEmpenho')");
    }
}
