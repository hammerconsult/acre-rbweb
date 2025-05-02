package br.com.webpublico.controle;

import br.com.webpublico.entidades.EfetivacaoUnidadeRequerente;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerente;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerenteUnidade;
import br.com.webpublico.enums.SituacaoSolicitacaoUnidadeRequerente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoUnidadeRequerenteFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mga on 02/08/19.
 */

@ManagedBean(name = "efetivacaoUnidadeRequerenteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEfetivacaoRequerente", pattern = "/efetivacao-unidade-requerente/novo/", viewId = "/faces/administrativo/materiais/efetivacao-unidade-requerente/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoRequerente", pattern = "/efetivacao-unidade-requerente/editar/#{efetivacaoUnidadeRequerenteControlador.id}/", viewId = "/faces/administrativo/materiais/efetivacao-unidade-requerente/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoRequerente", pattern = "/efetivacao-unidade-requerente/listar/", viewId = "/faces/administrativo/materiais/efetivacao-unidade-requerente/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoRequerente", pattern = "/efetivacao-unidade-requerente/ver/#{efetivacaoUnidadeRequerenteControlador.id}/", viewId = "/faces/administrativo/materiais/efetivacao-unidade-requerente/visualizar.xhtml")
})
public class EfetivacaoUnidadeRequerenteControlador extends PrettyControlador<EfetivacaoUnidadeRequerente> implements Serializable, CRUD {

    @EJB
    private EfetivacaoUnidadeRequerenteFacade facade;
    private List<SolicitacaoUnidadeRequerenteUnidade> unidadesRequerentes;

    public EfetivacaoUnidadeRequerenteControlador() {
        super(EfetivacaoUnidadeRequerente.class);
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            selecionado = facade.salvarEfetivacao(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue() + "/");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    @Override
    @URLAction(mappingId = "novoEfetivacaoRequerente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataEfetivacao(facade.getSistemaFacade().getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "editarEfetivacaoRequerente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verEfetivacaoRequerente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<SolicitacaoUnidadeRequerente> completarSolicitacao(String filtro) {
        return facade.getSolicitacaoUnidadeRequerenteFacade().burcarSolicitacaoPorSituacao(SituacaoSolicitacaoUnidadeRequerente.EM_ELABORACAO, filtro);
    }

    public void atribuirNullSolicitacao() {
        selecionado.setSolicitacao(null);
    }

    public void recuperarDadosSolicitcao() {
        if (selecionado.getSolicitacao() != null) {
            selecionado.setSolicitacao(facade.getSolicitacaoUnidadeRequerenteFacade().recuperar(selecionado.getSolicitacao().getId()));
            unidadesRequerentes = Lists.newArrayList(selecionado.getSolicitacao().getUnidadesRequerentes());
        }
    }

    public List<SelectItem> situacoesParaEfetivacao() {
        return Util.getListSelectItem(SituacaoSolicitacaoUnidadeRequerente.situacoesParaEfetivacao());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetivacao-unidade-requerente/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<SolicitacaoUnidadeRequerenteUnidade> getUnidadesRequerentes() {
        return unidadesRequerentes;
    }

    public void setUnidadesRequerentes(List<SolicitacaoUnidadeRequerenteUnidade> unidadesRequerentes) {
        this.unidadesRequerentes = unidadesRequerentes;
    }
}
