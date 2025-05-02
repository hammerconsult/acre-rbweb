package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoAnexoLicitacaoTipoDocumento;
import br.com.webpublico.entidades.TipoDocumentoAnexo;
import br.com.webpublico.enums.TipoDocumentoAnexoPNCP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoLicitacaoFacade;
import br.com.webpublico.negocios.TipoDocumentoAnexoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hudson on 23/11/15.
 */

@ManagedBean(name = "tipoDocumentoAnexoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTipoDocumentoAnexo", pattern = "/tipo-documento-anexo/novo/", viewId = "/faces/administrativo/licitacao/tipodocumentoanexo/edita.xhtml"),
    @URLMapping(id = "editarTipoDocumentoAnexo", pattern = "/tipo-documento-anexo/editar/#{tipoDocumentoAnexoControlador.id}/", viewId = "/faces/administrativo/licitacao/tipodocumentoanexo/edita.xhtml"),
    @URLMapping(id = "listarTipoDocumentoAnexo", pattern = "/tipo-documento-anexo/listar/", viewId = "/faces/administrativo/licitacao/tipodocumentoanexo/lista.xhtml"),
    @URLMapping(id = "verTipoDocumentoAnexo", pattern = "/tipo-documento-anexo/ver/#{tipoDocumentoAnexoControlador.id}/", viewId = "/faces/administrativo/licitacao/tipodocumentoanexo/visualizar.xhtml")
})
public class TipoDocumentoAnexoControlador extends PrettyControlador<TipoDocumentoAnexo> implements Serializable, CRUD {

    @EJB
    private TipoDocumentoAnexoFacade tipoDocumentoAnexoFacade;

    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;

    private List<ConfiguracaoAnexoLicitacaoTipoDocumento> configuracoesAnexo;

    @Override
    public AbstractFacade getFacede() {
        return tipoDocumentoAnexoFacade;
    }

    public TipoDocumentoAnexoControlador() {
        super(TipoDocumentoAnexo.class);
    }

    @URLAction(mappingId = "novoTipoDocumentoAnexo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        this.selecionado.setAtivo(true);
    }

    @URLAction(mappingId = "verTipoDocumentoAnexo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarTipoDocumentoAnexo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        buscarConfiguracaoAnexoLicitacaoPorTipoDocumentoAnexo();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tipo-documento-anexo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTiposDocumentos() {
        List<TipoDocumentoAnexo> anexos = tipoDocumentoAnexoFacade.buscarTodosTipoDocumentosAnexo();
        return Util.getListSelectItem(anexos, true);
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(TipoDocumentoAnexoPNCP.values(), false);
    }

    public void buscarConfiguracaoAnexoLicitacaoPorTipoDocumentoAnexo(){
        configuracoesAnexo = new ArrayList<>();
        if(selecionado.getTipoAnexoPNCP() != null){
            configuracoesAnexo = configuracaoLicitacaoFacade.buscarConfiguracaoAnexoLicitacaoPorTipoDocumentoAnexo(selecionado.getTipoAnexoPNCP());
        }
    }

    public List<ConfiguracaoAnexoLicitacaoTipoDocumento> getConfiguracoesAnexo() {
        return configuracoesAnexo;
    }

    public void setConfiguracoesAnexo(List<ConfiguracaoAnexoLicitacaoTipoDocumento> configuracoesAnexo) {
        this.configuracoesAnexo = configuracoesAnexo;
    }

    public Boolean listaConfiguracoesAnexoVazia(){
        return Util.isListNullOrEmpty(configuracoesAnexo);
    }

    @Override
    public void salvar(){
        try {
            Util.validarCampos(selecionado);
            validarAnexoComMesmaDescricao();
            tipoDocumentoAnexoFacade.salvarRetornando(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarAnexoComMesmaDescricao(){
        ValidacaoException operacaoNaoPermitidaException = new ValidacaoException();
        if(tipoDocumentoAnexoFacade.verificarAnexoComMesmaDescricao(selecionado)){
            operacaoNaoPermitidaException.adicionarMensagemDeOperacaoNaoPermitida("Não foi permitido salvar o tipo de documento anexo, pois já existe esse anexo com essa descrição");
        }
        operacaoNaoPermitidaException.lancarException();
    }

}
