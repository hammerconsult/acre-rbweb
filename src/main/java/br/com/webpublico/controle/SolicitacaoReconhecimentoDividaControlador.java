package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.ReconhecimentoDivida;
import br.com.webpublico.entidades.SolicitacaoReconhecimentoDivida;
import br.com.webpublico.enums.SituacaoSolicitacaoReconhecimentoDivida;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoReconhecimentoDividaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-solicitacao-reconhecimento-divida", pattern = "/solicitacao-reconhecimento-divida/novo/", viewId = "/faces/financeiro/solicitacao-reconhecimento-divida/edita.xhtml"),
    @URLMapping(id = "editar-solicitacao-reconhecimento-divida", pattern = "/solicitacao-reconhecimento-divida/editar/#{solicitacaoReconhecimentoDividaControlador.id}/", viewId = "/faces/financeiro/solicitacao-reconhecimento-divida/edita.xhtml"),
    @URLMapping(id = "listar-solicitacao-reconhecimento-divida", pattern = "/solicitacao-reconhecimento-divida/listar/", viewId = "/faces/financeiro/solicitacao-reconhecimento-divida/lista.xhtml"),
    @URLMapping(id = "ver-solicitacao-reconhecimento-divida", pattern = "/solicitacao-reconhecimento-divida/ver/#{solicitacaoReconhecimentoDividaControlador.id}/", viewId = "/faces/financeiro/solicitacao-reconhecimento-divida/visualizar.xhtml")
})
public class SolicitacaoReconhecimentoDividaControlador extends PrettyControlador<SolicitacaoReconhecimentoDivida> implements Serializable, CRUD {

    @EJB
    private SolicitacaoReconhecimentoDividaFacade facade;

    public SolicitacaoReconhecimentoDividaControlador() {
        super(SolicitacaoReconhecimentoDivida.class);
    }

    @URLAction(mappingId = "novo-solicitacao-reconhecimento-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setSituacao(SituacaoSolicitacaoReconhecimentoDivida.EM_ELABORACAO);
        selecionado.setDataCadastro(DataUtil.getDataComHoraAtual(facade.getSistemaFacade().getDataOperacao()));
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "ver-solicitacao-reconhecimento-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-solicitacao-reconhecimento-divida", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<ReconhecimentoDivida> completarReconhecimentosDaDivida(String filtro) {
        return facade.buscarReconhecimentosDaDivida(filtro);
    }

    public List<ClasseCredor> completarClassesCredores(String filtro) {
        if (selecionado.getReconhecimentoDivida() != null) {
            return facade.getClasseCredorFacade().buscarClassesPorPessoa(filtro, selecionado.getReconhecimentoDivida().getFornecedor());
        }
        return Lists.newArrayList();
    }

    public void selecionarReconhecimento() {
        if (selecionado.getReconhecimentoDivida() != null) {
            selecionado.setReconhecimentoDivida(facade.getReconhecimentoDividaFacade().recuperar(selecionado.getReconhecimentoDivida().getId()));
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            selecionarReconhecimento();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void concluir() {
        try {
            Util.validarCampos(selecionado);
            facade.salvarGerandoSolicitacao(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica eng) {
            FacesUtil.addOperacaoNaoRealizada(eng.getMessage());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }

    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-reconhecimento-divida/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
