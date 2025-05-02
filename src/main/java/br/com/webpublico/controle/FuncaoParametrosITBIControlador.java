package br.com.webpublico.controle;

import br.com.webpublico.entidades.FuncaoParametrosITBI;
import br.com.webpublico.enums.TipoFuncaoParametrosITBI;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FuncaoParametroITBIFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaFuncaoParametrosITBI", pattern = "/parametrosITBI/funcao/novo/", viewId = "/faces/tributario/itbi/parametrositbi/funcao/edita.xhtml"),
    @URLMapping(id = "editarFuncaoParametrosITBI", pattern = "/parametrosITBI/funcao/editar/#{funcaoParametrosITBIControlador.id}/", viewId = "/faces/tributario/itbi/parametrositbi/funcao/edita.xhtml"),
    @URLMapping(id = "verFuncaoParametrosITBI", pattern = "/parametrosITBI/funcao/ver/#{funcaoParametrosITBIControlador.id}/", viewId = "/faces/tributario/itbi/parametrositbi/funcao/visualizar.xhtml"),
    @URLMapping(id = "listarFuncaoParametrosITBI", pattern = "/parametrosITBI/funcao/listar/", viewId = "/faces/tributario/itbi/parametrositbi/funcao/lista.xhtml")
})
public class FuncaoParametrosITBIControlador extends PrettyControlador<FuncaoParametrosITBI> implements CRUD {

    @EJB
    private FuncaoParametroITBIFacade funcaoParametroITBIFacade;

    public FuncaoParametrosITBIControlador() {
        super(FuncaoParametrosITBI.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return funcaoParametroITBIFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametrosITBI/funcao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaFuncaoParametrosITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarFuncaoParametrosITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verFuncaoParametrosITBI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SelectItem> buscarTiposFuncao() {
        return Util.getListSelectItem(TipoFuncaoParametrosITBI.values());
    }

    @Override
    public void salvar() {
        try {
            validarFuncao();
            funcaoParametroITBIFacade.salvar(selecionado);
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao salvar Função dos Parâmetros de ITBI. Detalhes: " + e.getMessage());
            logger.error("Erro ao salvar Função dos Parâmetros de ITBI.", e);
        }
    }

    private void validarFuncao() {
        ValidacaoException ve = new ValidacaoException();
        if (StringUtils.isBlank(selecionado.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição deve ser informado.");
        }
        if (selecionado.getFuncao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Função deve ser informado.");
        }
        ve.lancarException();
    }
}
