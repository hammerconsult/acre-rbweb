package br.com.webpublico.controle.rh.faltas;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Faltas;
import br.com.webpublico.entidades.rh.ponto.SolicitacaoFaltas;
import br.com.webpublico.enums.rh.ponto.StatusSolicitacaoFaltas;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.ponto.SolicitacaoFaltasFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "solicitacaoFaltasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarFaltasFaltas", pattern = "/solicitacao-faltas/listar/", viewId = "/faces/rh/administracaodepagamento/solicitacao-faltas/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoFaltas", pattern = "/solicitacao-faltas/ver/#{solicitacaoFaltasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/solicitacao-faltas/visualizar.xhtml"),
    @URLMapping(id = "aprovarSolicitacaoFaltas", pattern = "/solicitacao-faltas/aprovar/#{solicitacaoFaltasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/solicitacao-faltas/aprovar.xhtml"),
    @URLMapping(id = "reprovarSolicitacaoFaltas", pattern = "/solicitacao-faltas/reprovar/#{solicitacaoFaltasControlador.id}/", viewId = "/faces/rh/administracaodepagamento/solicitacao-faltas/reprovar.xhtml")
})
public class SolicitacaoFaltasControlador extends PrettyControlador<SolicitacaoFaltas> implements Serializable, CRUD {

    @EJB
    private SolicitacaoFaltasFacade solicitacaoFaltasFacade;

    public SolicitacaoFaltasControlador() {
        super(SolicitacaoFaltas.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoFaltasFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-faltas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verSolicitacaoFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        System.out.println("ver ");

        super.ver();
    }

    @URLAction(mappingId = "aprovarSolicitacaoFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void telaAprovar() {
        super.ver();
        if (!selecionado.getStatus().isEmAnalise()) {
            FacesUtil.addAtencao("Não é possível aprovar esta solicitação de faltas, já está " + selecionado.getStatus().getDescricao());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
        Faltas faltas = new Faltas();
        faltas.setContratoFP(selecionado.getContratoFP());
        faltas.setTipoFalta(selecionado.getTipoFalta());
        faltas.setInicio(selecionado.getDataInicio());
        faltas.setFim(selecionado.getDataFim());
        faltas.setDias(Long.valueOf(Util.diferencaDeDiasEntreData(selecionado.getDataInicio(), selecionado.getDataFim())).intValue() + 1);
        faltas.setDataCadastro(UtilRH.getDataOperacao());
        selecionado.setFaltas(faltas);
    }

    @URLAction(mappingId = "reprovarSolicitacaoFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void telaReprovar() {
        super.ver();
        if (!selecionado.getStatus().isEmAnalise()) {
            FacesUtil.addAtencao("Não é possível reprovar esta solicitação de afastamento, já está " + selecionado.getStatus().getDescricao());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    public void redirecionarTelaAprovar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "aprovar/" + selecionado.getId() + "/");
    }

    public void redirecionarTelaReprovar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "reprovar/" + selecionado.getId() + "/");
    }


    public void aprovar() {
        try {
            Util.validarCampos(selecionado);
            Util.validarCampos(selecionado.getFaltas());
            validarDatas();
            selecionado.setStatus(StatusSolicitacaoFaltas.APROVADO);
            solicitacaoFaltasFacade.salvarSolicitacaoFaltas(selecionado);
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFaltas().getInicio().after(selecionado.getFaltas().getFim())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Término deve ser posterior à Data de Início.");
        }
        ve.lancarException();
    }

    public void reprovar() {
        selecionado.setStatus(StatusSolicitacaoFaltas.REPROVADO);
        salvar();
        redireciona();
    }
}
