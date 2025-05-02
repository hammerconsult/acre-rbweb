package br.com.webpublico.controle;

import br.com.webpublico.entidades.ReferenciaFP;
import br.com.webpublico.entidades.rh.ParametroFerias;
import br.com.webpublico.enums.TipoReferenciaFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametroFeriasFacade;
import br.com.webpublico.negocios.ReferenciaFPFacade;
import br.com.webpublico.negocios.TipoRegimeFacade;
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
import java.util.List;

@ManagedBean(name = "parametroFeriasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroFerias", pattern = "/parametro-ferias/novo/", viewId = "/faces/rh/administracaodepagamento/parametroferias/edita.xhtml"),
    @URLMapping(id = "editarParametroFerias", pattern = "/parametro-ferias/editar/#{parametroFeriasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametroferias/edita.xhtml"),
    @URLMapping(id = "listarParametroFerias", pattern = "/parametro-ferias/listar/", viewId = "/faces/rh/administracaodepagamento/parametroferias/lista.xhtml"),
    @URLMapping(id = "verParametroFerias", pattern = "/parametro-ferias/ver/#{parametroFeriasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/parametroferias/visualizar.xhtml")
})
public class ParametroFeriasControlador extends PrettyControlador<ParametroFerias> implements Serializable, CRUD {

    @EJB
    private ParametroFeriasFacade facade;
    @EJB
    private TipoRegimeFacade tipoRegimeFacade;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;

    public ParametroFeriasControlador() {
        super(ParametroFerias.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-ferias/";
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoParametroFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verParametroFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar o parâmetro de férias {}", ex);
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoRegime() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Regime deve ser informado.");
        }
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Vigência deve ser informado.");
        } else {
            if (selecionado.getFinalVigencia() != null && selecionado.getFinalVigencia().before(selecionado.getInicioVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O final da vigência deve ser posterior ao início da vigência.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> tiposRegime() {
        return Util.getListSelectItem(tipoRegimeFacade.lista());
    }

    public List<ReferenciaFP> completarReferenciaFP(String s) {
        return referenciaFPFacade.listaFiltrandoPorTipoDescricao(TipoReferenciaFP.FAIXA, s);
    }
}
