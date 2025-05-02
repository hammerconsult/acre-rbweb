package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConstrucaoFacade;
import br.com.webpublico.negocios.HabiteseConstrucaoFacade;
import br.com.webpublico.negocios.ProcRegularizaConstrucaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcessoRegularizaConstrucao",
        pattern = "/processo-regularizacao-construcao/novo/",
        viewId = "/faces/tributario/processoregularizaconstrucao/edita.xhtml"),
    @URLMapping(id = "editarProcessoRegularizaConstrucao",
        pattern = "/processo-regularizacao-construcao/editar/#{procRegularizaConstrucaoControlador.id}/",
        viewId = "/faces/tributario/processoregularizaconstrucao/edita.xhtml"),
    @URLMapping(id = "listarProcessoRegularizaConstrucao",
        pattern = "/processo-regularizacao-construcao/listar/",
        viewId = "/faces/tributario/processoregularizaconstrucao/lista.xhtml"),
    @URLMapping(id = "verProcessoRegularizaConstrucao",
        pattern = "/processo-regularizacao-construcao/ver/#{procRegularizaConstrucaoControlador.id}/",
        viewId = "/faces/tributario/processoregularizaconstrucao/visualizar.xhtml"),
})
public class ProcRegularizaConstrucaoControlador extends PrettyControlador<ProcRegularizaConstrucao> implements CRUD {

    @EJB
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    @EJB
    private ConstrucaoFacade construcaoFacade;
    @EJB
    private HabiteseConstrucaoFacade habiteseConstrucaoFacade;
    private List<Habitese> habitesesDisponiveis;


    public ProcRegularizaConstrucaoControlador() {
        super(ProcRegularizaConstrucao.class);
    }

    public void selecionarCadastroPesquisaGenerica(ActionEvent event) {
        Object obj = event.getComponent().getAttributes().get("objeto");
        selecionado.setCadastroImobiliario((CadastroImobiliario) obj);
    }

    public void recuperarCadastroImobiliario(SelectEvent evento) {
        selecionado.setCadastroImobiliario(procRegularizaConstrucaoFacade.getCadastroImobiliarioFacade().recuperar(((CadastroImobiliario) evento.getObject()).getId()));
    }

    public void processar() {
        selecionado.setSituacao(ProcRegularizaConstrucao.Situacao.AGUARDANDO_ALVARA_CONSTRUCAO);
        salvar(Redirecionar.VER);
    }

    public void finalizar() {
        selecionado.setSituacao(ProcRegularizaConstrucao.Situacao.FINALIZADO);
        Construcao construcao = selecionado.getUltimoAlvara().getConstrucaoAlvara().getConstrucao();
        CaracteristicaConstrucaoHabitese caracteristicaConstrucao = habiteseConstrucaoFacade.recuperarCaracteristicaConstrucaoHabitese(selecionado.getId());
        if (construcao == null) {
            construcao = new Construcao();
            construcao.setImovel(selecionado.getCadastroImobiliario());
            construcao.setDescricao("1");
            construcao.setCodigo(1);
        }
        construcao.setQuantidadePavimentos(caracteristicaConstrucao.getQuantidadeDePavimentos());
        construcao.setAreaConstruida(caracteristicaConstrucao.getAreaConstruida().doubleValue());
        construcao.setHabitese(caracteristicaConstrucao.getHabitese().getCodigo() + "/" + caracteristicaConstrucao.getHabitese().getExercicio());
        construcao.setDataHabitese(caracteristicaConstrucao.getHabitese().getDataLancamento());
        construcaoFacade.salvar(construcao);
        salvar(Redirecionar.VER);
    }

    public void redirecionarAlvara() {
        AlvaraConstrucao alvaraConstrucao = procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().buscarUltimoAlvaraParaProcesso(true, selecionado);
        if (alvaraConstrucao == null) {
            Web.navegacao(getUrlAtual(), "/alvara-construcao/novo/", "processoRegularizacaoConstrucao", selecionado);
        } else {
            Web.navegacao(getUrlAtual(), "/alvara-construcao/ver/" + alvaraConstrucao.getId() + "/");
        }
    }

    public void emitirTaxaVistoria() {
        try {
            procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().emitirDAM(selecionado.getUltimoAlvara());
            selecionado = procRegularizaConstrucaoFacade.recarregar(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            e = Util.getRootCauseEJBException(e);
            if (e instanceof ValidacaoException) {
                FacesUtil.printAllFacesMessages(((ValidacaoException) e).getMensagens());
                logger.error("Erro ao emitirTaxaVistoria {} ", e);
            }
        }
    }

    public void redirecionarHabitese() {
        habitesesDisponiveis = selecionado.getUltimoAlvara().getHabiteses();
        if (habitesesDisponiveis == null || habitesesDisponiveis.isEmpty()) {
            redirecionarNovoHabitese();
        } else {
            FacesUtil.atualizarComponente("dialogEscolherHabitese");
            FacesUtil.executaJavaScript("dlgEscolherHabitese.show();");
        }
    }

    public void redirecionarHabitese(Habitese habitese) {
        Web.navegacao(getUrlAtual(), "/habitese-construcao/ver/" + habitese.getId() + "/");
    }

    public void redirecionarNovoHabitese() {
        Web.navegacao(getUrlAtual(), "/habitese-construcao/novo/", selecionado);
    }

    public boolean canEmitirTaxaVistoria() {
        return ProcRegularizaConstrucao.Situacao.ALVARA_CONSTRUCAO.equals(selecionado.getSituacao()) ||
            ProcRegularizaConstrucao.Situacao.AGUARDANDO_TAXA_VISTORIA.equals(selecionado.getSituacao());
    }

    public boolean isAguardandoTaxaVistoria() {
        return ProcRegularizaConstrucao.Situacao.AGUARDANDO_TAXA_VISTORIA.equals(selecionado.getSituacao());
    }

    public boolean canProcessar() {
        return ProcRegularizaConstrucao.Situacao.EM_ABERTO.equals(selecionado.getSituacao());
    }

    public boolean canIrParaAlvaraDeConstrucao() {
        return !ProcRegularizaConstrucao.Situacao.EM_ABERTO.equals(selecionado.getSituacao());
    }

    public boolean canIrParaHabitese() {
        if (selecionado.getUltimoAlvara() != null && (ProcRegularizaConstrucao.Situacao.TAXA_VISTORIA.equals(selecionado.getSituacao()) || ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE.equals(selecionado.getSituacao()) || ProcRegularizaConstrucao.Situacao.HABITESE.equals(selecionado.getSituacao())
            || ProcRegularizaConstrucao.Situacao.FINALIZADO.equals(selecionado.getSituacao()))) {
            return true;
        }
        return false;
    }

    public boolean canFinalizar() {
        return ProcRegularizaConstrucao.Situacao.HABITESE.equals(selecionado.getSituacao());
    }

    public List<Habitese> getHabitesesDisponiveis() {
        return habitesesDisponiveis;
    }

    public void setHabitesesDisponiveis(List<Habitese> habitesesDisponiveis) {
        this.habitesesDisponiveis = habitesesDisponiveis;
    }

    @Override
    public boolean validaRegrasEspecificas() {
        ValidacaoException ve = new ValidacaoException();
        if (isOperacaoEditar() && !ProcRegularizaConstrucao.Situacao.EM_ABERTO.equals(selecionado.getSituacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível editar um processo que não esteja com a situação em aberto");
        }
        if (selecionado.getDataFimObra() != null && selecionado.getDataCriacao().compareTo(selecionado.getDataFimObra()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conclusão da obra deve ser maior que a data atual.");
        }
        if (ve.temMensagens()) {
            ve.lancarException();
            return false;
        }
        return true;
    }

    @URLAction(mappingId = "novoProcessoRegularizaConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(procRegularizaConstrucaoFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUsuarioIncluiu(procRegularizaConstrucaoFacade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setDataCriacao(procRegularizaConstrucaoFacade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "editarProcessoRegularizaConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verProcessoRegularizaConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        super.salvar(Redirecionar.VER);
    }

    @Override
    public AbstractFacade getFacede() {
        return procRegularizaConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo-regularizacao-construcao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
