package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteContabilizacaoBaixa;
import br.com.webpublico.entidadesauxiliares.VOItemBaixaPatrimonial;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoBaixaPatrimonialFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 08:51
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "efetivacaoBaixaPatrimonialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEfetivacaoBaixaBemMovel", pattern = "/efetivacao-baixa-bem-movel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/movel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoBaixaBemMovel", pattern = "/efetivacao-baixa-bem-movel/editar/#{efetivacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/movel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoBaixaBemMovel", pattern = "/efetivacao-baixa-bem-movel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/movel/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoBaixaBemMovel", pattern = "/efetivacao-baixa-bem-movel/ver/#{efetivacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/movel/visualizar.xhtml"),

    @URLMapping(id = "novoEfetivacaoBaixaBemImovel", pattern = "/efetivacao-baixa-bem-imovel/novo/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/imovel/edita.xhtml"),
    @URLMapping(id = "editarEfetivacaoBaixaBemImovel", pattern = "/efetivacao-baixa-bem-imovel/editar/#{efetivacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/imovel/edita.xhtml"),
    @URLMapping(id = "listarEfetivacaoBaixaBemImovel", pattern = "/efetivacao-baixa-bem-imovel/listar/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/imovel/lista.xhtml"),
    @URLMapping(id = "verEfetivacaoBaixaBemImovel", pattern = "/efetivacao-baixa-bem-imovel/ver/#{efetivacaoBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/efetivacaobaixa/imovel/visualizar.xhtml")
})
public class EfetivacaoBaixaPatrimonialControlador extends PrettyControlador<EfetivacaoBaixaPatrimonial> implements Serializable, CRUD {

    @EJB
    private EfetivacaoBaixaPatrimonialFacade facade;
    private String numeroLote;
    private LeilaoAlienacaoLote lotePesquisa;
    private AssistenteMovimentacaoBens assistenteMovimentoAdm;
    private AssistenteContabilizacaoBaixa assistenteContabilizacao;
    private Future<List<VOItemBaixaPatrimonial>> futurePesquisaBens;
    private Future<EfetivacaoBaixaPatrimonial> futureSalvar;
    private List<VOItemBaixaPatrimonial> bensLoteLeilaoPesquisa;
    private List<VOItemBaixaPatrimonial> bensParecerBaixa;
    private List<VOItemBaixaPatrimonial> bensLoteLeilao;
    private List<LogErroEfetivacaoBaixaBem> inconsistenciasEfetivacao;
    private List<DoctoFiscalLiquidacao> documentosFiscal;
    private List<EfetivacaoBaixaLote> lotesEfetetivacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public EfetivacaoBaixaPatrimonialControlador() {
        super(EfetivacaoBaixaPatrimonial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public void novo() {
        super.novo();
        selecionado.setDataEfetivacao(getDataOperacao());
        selecionado.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setSituacao(SituacaoBaixaPatrimonial.EM_ELABORACAO);
        inicializarLista();
        inconsistenciasEfetivacao = Lists.newArrayList();
    }

    private void novoAssistenteContabilizacao() {
        assistenteContabilizacao = new AssistenteContabilizacaoBaixa();
    }

    @URLAction(mappingId = "novoEfetivacaoBaixaBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEfetivacaoBemMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novoEfetivacaoBaixaBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEfetivacaoBemImovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.IMOVEIS);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        inconsistenciasEfetivacao = facade.buscarInconsistenciasEfetivacaoBaixa(selecionado);
        selecionado.setLogErrosEfetivacao(inconsistenciasEfetivacao);
        inicializarLista();
        novoAssistenteContabilizacao();
    }

    @URLAction(mappingId = "verEfetivacaoBaixaBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoBaixaBemMovel() {
        ver();
    }

    @URLAction(mappingId = "verEfetivacaoBaixaBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEfetivacaoBaixaBemImovel() {
        ver();
    }

    @URLAction(mappingId = "editarEfetivacaoBaixaBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoBaixaBemMovel() {
        editar();
    }

    @URLAction(mappingId = "editarEfetivacaoBaixaBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEfetivacaoBaixaBemImovel() {
        editar();
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS:
                return "/efetivacao-baixa-bem-movel/";
            case IMOVEIS:
                return "/efetivacao-baixa-bem-imovel/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependenciasArquivo(getId());
        inconsistenciasEfetivacao = facade.buscarInconsistenciasEfetivacaoBaixa(selecionado);
        selecionado.setLogErrosEfetivacao(inconsistenciasEfetivacao);
        inicializarLista();
        novoAssistenteContabilizacao();
        novoAssistenteMovimentacaoBem();
        AssistenteContabilizacaoBaixa assistenteDaSessao = (AssistenteContabilizacaoBaixa) Web.pegaDaSessao("ASSISTENTE");
        if (assistenteDaSessao != null) {
            assistenteContabilizacao = assistenteDaSessao;
        }
    }

    private void inicializarLista() {
        bensParecerBaixa = Lists.newArrayList();
        bensLoteLeilaoPesquisa = Lists.newArrayList();
        bensLoteLeilao = Lists.newArrayList();
        lotesEfetetivacao = Lists.newArrayList();
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                selecionado = (EfetivacaoBaixaPatrimonial) assistenteMovimentoAdm.getSelecionado();
                inconsistenciasEfetivacao = facade.buscarInconsistenciasEfetivacaoBaixa(this.selecionado);
                selecionado.setLogErrosEfetivacao(inconsistenciasEfetivacao);
                if (assistenteMovimentoAdm.getMensagensValidacaoFacesUtil().isEmpty()) {
                    if (isOperacaoNovo() && this.assistenteMovimentoAdm.getSimularContabilizacao()) {
                        FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                        futureSalvar = null;
                        verificarInconsistencia();
                    } else {
                        futureSalvar = null;
                        FacesUtil.executaJavaScript("finalizarSalvar()");
                    }
                } else {
                    FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                    FacesUtil.executaJavaScript("aguarde.hide()");
                    for (FacesMessage facesMessage : assistenteMovimentoAdm.getMensagensValidacaoFacesUtil()) {
                        FacesUtil.addOperacaoNaoRealizada(facesMessage.getDetail());
                    }
                    futureSalvar = null;
                }
                desbloquearMovimentoSingleton();
            } catch (Exception ex) {
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.executaJavaScript("aguarde.hide()");
                assistenteMovimentoAdm.setBloquearAcoesTela(true);
                futureSalvar = null;
                desbloquearMovimentoSingleton();
                assistenteMovimentoAdm.descobrirETratarException(ex);
            }
        }
    }

    public void consultarFutureIncosistencia() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                if (assistenteMovimentoAdm.getLogErrosEfetivacaoBaixa() != null
                    && !assistenteMovimentoAdm.getLogErrosEfetivacaoBaixa().isEmpty()) {
                    inconsistenciasEfetivacao = assistenteMovimentoAdm.getLogErrosEfetivacaoBaixa();
                }
                selecionado = (EfetivacaoBaixaPatrimonial) assistenteMovimentoAdm.getSelecionado();
                futureSalvar = null;
                desbloquearMovimentoSingleton();
                FacesUtil.executaJavaScript("finalizarInconsistencia()");
            } catch (Exception ex) {
                desbloquearMovimentoSingleton();
                descobrirETratarException(ex);
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.executaJavaScript("aguarde.hide()");
                assistenteMovimentoAdm.setBloquearAcoesTela(true);
                futureSalvar = null;

            }
        }
    }

    public void finalizarProcesssoSalvar() {
        if (selecionado.isFinalizada()) {
            redirecionarParaVer();
        } else {
            Web.poeNaSessao("ASSISTENTE", assistenteContabilizacao);
            redirecionarParaEditar();
        }
    }

    public void finalizarVefificarInconsistencia() {
        Web.poeNaSessao("ASSISTENTE", assistenteContabilizacao);
        redirecionarParaEditar();
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    private void redirecionarParaEditar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (futurePesquisaBens != null && futurePesquisaBens.isDone()) {
            if (!selecionado.isSolicitacaoBaixaPorAlienacao()) {
                popularListaFutureBaixaNormal();
            } else {
                popularListaFutureBaixaPorAlienacao();
            }
            futurePesquisaBens = null;
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    private void popularListaFutureBaixaPorAlienacao() throws InterruptedException, ExecutionException {
        if (bensLoteLeilaoPesquisa != null) {
            bensLoteLeilaoPesquisa.clear();
        }
        bensLoteLeilaoPesquisa = futurePesquisaBens.get();
        if (!isOperacaoNovo()) {
            if (assistenteContabilizacao.getBensParaContabilizar() != null) {
                assistenteContabilizacao.getBensParaContabilizar().clear();
            }
            if (!bensLoteLeilao.isEmpty()) {
                bensLoteLeilao.clear();
            }
            assistenteContabilizacao.setBensParaContabilizar(bensLoteLeilaoPesquisa);
            bensLoteLeilao = bensLoteLeilaoPesquisa;
        }
    }

    private void popularListaFutureBaixaNormal() throws InterruptedException, ExecutionException {
        if (bensParecerBaixa != null) {
            bensParecerBaixa.clear();
        }
        bensParecerBaixa = futurePesquisaBens.get();
        if (!isOperacaoNovo()) {
            if (assistenteContabilizacao.getBensParaContabilizar() != null) {
                assistenteContabilizacao.getBensParaContabilizar().clear();
            }
            assistenteContabilizacao.setBensParaContabilizar(bensParecerBaixa);
        }
    }

    public void finalizarPesquisa() {
        if (isOperacaoNovo()) {
            if (assistenteMovimentoAdm != null
                && assistenteMovimentoAdm.getLogErrosEfetivacaoBaixa() != null
                && !assistenteMovimentoAdm.getLogErrosEfetivacaoBaixa().isEmpty()) {
                inconsistenciasEfetivacao = assistenteMovimentoAdm.getLogErrosEfetivacaoBaixa();
            }
        } else {
            inconsistenciasEfetivacao = facade.buscarInconsistenciasEfetivacaoBaixa(selecionado);
        }
        FacesUtil.atualizarComponente("Formulario");
    }

    public void buscarBensAoVisualizar() {
        if (!isOperacaoNovo()) {
            if (!selecionado.isSolicitacaoBaixaPorAlienacao()) {
                pesquisarBensParecerBaixa();
            } else {
                pesquisarBensEfetivadosLoteAlienacaoAoVisualizar();
            }
        }
    }

    private void pesquisarBensEfetivadosLoteAlienacaoAoVisualizar() {
        futurePesquisaBens = facade.buscarItensEfetivadosPorLoteLeilaoAlienacao(selecionado);
        FacesUtil.executaJavaScript("iniciarPesquisa()");
    }

    public void pesquisarBensLoteAlienacao() {
        try {
            validarPesquisaLote();
            novoAssistenteMovimentacaoBem();
            validarRegrasConfiguracaoMovimentacaoBem();
            lotePesquisa = facade.getLeilaoAlienacaoFacade().recuperarLoteArrematadoNoLeilao(selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa().getLeilaoAlienacao(), numeroLote);
            validarResultadoPesquisaLote();
            futurePesquisaBens = facade.buscarItensSolicitacaoBaixaPorLoteLeilaoAlienacao(lotePesquisa, assistenteMovimentoAdm);
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void pesquisarBensParecerBaixa() {
        try {
            novoAssistenteMovimentacaoBem();
            if (!selecionado.isSolicitacaoBaixaPorAlienacao()) {
                inicializarLista();
                futurePesquisaBens = facade.buscarBensParecerBaixaParaEfetivacao(selecionado.getParecerBaixaPatrimonial(), assistenteMovimentoAdm);
                FacesUtil.executaJavaScript("iniciarPesquisa()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarEfetivacaoBaixa() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        ve.lancarException();
        if (selecionado.isEfetivacaoPorAlienacao()) {
            if (bensLoteLeilao == null || bensLoteLeilao.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A efetivação não possui bens do leilão adicionados ao lote.");
            }
            if ((selecionado.isSolicitacaoBaixaPorAlienacao())
                && (lotesEfetetivacao == null || lotesEfetetivacao.isEmpty()) && isOperacaoNovo()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos um lote para baixar.");
            }
        } else {
            if (bensParecerBaixa == null || bensParecerBaixa.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A efetivação não possui bens da solicitação adicionados.");
            }
        }
        if (inconsistenciasEfetivacao != null && !inconsistenciasEfetivacao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi possível salvar/concluir a efetivação, pois a mesma possui inconsistências.");
        }
        if (selecionado.getDataEfetivacao() != null
            && DataUtil.dataSemHorario(selecionado.getDataEfetivacao()).before(DataUtil.dataSemHorario(selecionado.getParecerBaixaPatrimonial().getDateParecer()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data da efetivação da baixa deve ser posterior ou igual a data do parecer da baixa: " + DataUtil.getDataFormatada(selecionado.getParecerBaixaPatrimonial().getDateParecer()) + ".");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarEfetivacaoBaixa();
            novoAssistenteMovimentacaoBem();
            validarRegrasConfiguracaoMovimentacaoBem();
            novoAssistenteContabilizacao();
            bloquearMovimentacaoBens();
            facade.salvarSolicitacaoBaixaControleVersao(selecionado);
            List<VOItemBaixaPatrimonial> itensParaProcessar = atribuirBensParaProcessamento();
            futureSalvar = facade.salvarEfetivacaoBaixa(selecionado, itensParaProcessar, assistenteMovimentoAdm, assistenteContabilizacao, lotesEfetetivacao);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            if (!isOperacaoNovo() || selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private List<VOItemBaixaPatrimonial> atribuirBensParaProcessamento() {
        assistenteMovimentoAdm.setSimularContabilizacao(Boolean.TRUE);
        List<VOItemBaixaPatrimonial> itensParaProcessar;
        if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
            itensParaProcessar = bensLoteLeilao;
        } else {
            itensParaProcessar = bensParecerBaixa;
        }
        return itensParaProcessar;
    }

    public void concluirEfetivacao() {
        try {
            validarConcluir();
            novoAssistenteMovimentacaoBem();
            validarRegrasConfiguracaoMovimentacaoBem();
            bloquearMovimentacaoBens();
            facade.salvarSolicitacaoBaixaControleVersao(selecionado);
            assistenteMovimentoAdm.setSimularContabilizacao(Boolean.FALSE);
            futureSalvar = facade.concluirEfetivacaoBaixa(selecionado, assistenteMovimentoAdm, assistenteContabilizacao);
            FacesUtil.executaJavaScript("acompanharSalvar()");
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    private void desbloquearMovimentoSingleton() {
        if (selecionado.getParecerBaixaPatrimonial() != null) {
            for (UnidadeOrganizacional unidade : assistenteMovimentoAdm.getUnidades()) {
                facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(EfetivacaoBaixaPatrimonial.class, unidade);
            }
        }
    }

    private void bloquearMovimentacaoBens() {
        for (UnidadeOrganizacional unidade : assistenteMovimentoAdm.getUnidades()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(EfetivacaoBaixaPatrimonial.class, unidade, assistenteMovimentoAdm);
        }
    }

    private void validarConcluir() {
        validarEfetivacaoBaixa();
        validarRegrasConfiguracaoMovimentacaoBem();
    }

    public void verificarInconsistencia() {
        try {
            novoAssistenteMovimentacaoBem();
            facade.salvarSolicitacaoBaixaControleVersao(selecionado);
            assistenteMovimentoAdm.setSimularContabilizacao(Boolean.TRUE);
            futureSalvar = facade.simularContabilizacao(selecionado, assistenteContabilizacao, assistenteMovimentoAdm);
            FacesUtil.executaJavaScript("openDialog(dlgInconsistencia)");
            FacesUtil.executaJavaScript("acompanharInconsistencia()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void novoAssistenteMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        assistenteMovimentoAdm = new AssistenteMovimentacaoBens(selecionado.getDataEfetivacao(), operacao);
        assistenteMovimentoAdm.zerarContadoresProcesso();
        assistenteMovimentoAdm.setSelecionado(selecionado);
        assistenteMovimentoAdm.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistenteMovimentoAdm.setLogErrosEfetivacaoBaixa(new ArrayList<LogErroEfetivacaoBaixaBem>());
        assistenteMovimentoAdm.setExercicio(facade.getExercicioFacade().getExercicioPorAno(DataUtil.getAno(getDataOperacao())));
        adicionarUnidadesAssistente();
    }

    private void adicionarUnidadesAssistente() {
        if (selecionado.getParecerBaixaPatrimonial() != null) {
            if (selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
                Set<HierarquiaOrganizacional> hierarquias = new HashSet<>();
                if (isOperacaoEditar()) {
                    lotesEfetetivacao = selecionado.getListaLoteAlienacao();
                }
                for (EfetivacaoBaixaLote lote : lotesEfetetivacao) {
                    hierarquias.addAll(facade.getSolicitacaoBaixaPatrimonialFacade().buscarHierarquiaAdministrativaItemLoteLeilao(lote.getLeilaoAlienacaoLote().getLeilaoAlienacao()));
                }
                for (HierarquiaOrganizacional ho : hierarquias) {
                    assistenteMovimentoAdm.getUnidades().add(ho.getSubordinada());
                }
            } else {
                assistenteMovimentoAdm.getUnidades().add(selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa().getUnidadeAdministrativa());
            }
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataEfetivacao(), OperacaoMovimentacaoBem.EFETIVACAO_BAIXA_PATRIMONIAL);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataEfetivacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public boolean renderizarBotaoEditar(EfetivacaoBaixaPatrimonial obj) {
        selecionado = obj;
        return selecionado.isAguardandoEfetivacao();
    }

    public boolean hasInconsistenciaEfetivacao() {
        return inconsistenciasEfetivacao != null && !inconsistenciasEfetivacao.isEmpty();
    }

    public boolean renderizarBotaoConcluir() {
        return selecionado.isAguardandoEfetivacao() && !hasInconsistenciaEfetivacao();
    }

    public boolean renderizarBotaoSimular() {
        return selecionado.isAguardandoEfetivacao() && hasInconsistenciaEfetivacao();
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public LeilaoAlienacaoLote getLotePesquisa() {
        return lotePesquisa;
    }

    public void setLotePesquisa(LeilaoAlienacaoLote lotePesquisa) {
        this.lotePesquisa = lotePesquisa;
    }

    public void atribuirLoteDocumentoFiscal(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        if (leilaoAlienacaoLote != null) {
            documentosFiscal = facade.getLeilaoAlienacaoFacade().buscarDocumentoFiscalLoteLeilao(leilaoAlienacaoLote);
            FacesUtil.executaJavaScript("dialogDocs.show()");
        }
    }

    private void validarPesquisaLote() {
        ValidacaoException ve = new ValidacaoException();
        if (numeroLote == null || numeroLote.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Número do Lote deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarResultadoPesquisaLote() {
        ValidacaoException ve = new ValidacaoException();
        if (lotePesquisa == null) {
            ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "Nenhum lote encontrado para com o número: " + numeroLote + ".");
            cancelarLotePesquisa();
        } else {
            if (facade.getLeilaoAlienacaoFacade().isLoteAlienacaoBaixado(lotePesquisa)) {
                ve.adicionarMensagemWarn(SummaryMessages.ATENCAO, "O lote " + numeroLote + " já foi baixado!");
                lotePesquisa = null;
            }
        }
        ve.lancarException();
    }

    public List<VOItemBaixaPatrimonial> getBensDoLotePesquisa() {
        if (!bensLoteLeilaoPesquisa.isEmpty()) {
            return bensLoteLeilaoPesquisa.get(0).getBensAgrupados();
        }
        return Lists.newArrayList();
    }

    public void adicionarLote() {
        try {
            validarAdicionarLote();
            bensLoteLeilao.addAll(bensLoteLeilaoPesquisa);
            lotesEfetetivacao.add(new EfetivacaoBaixaLote(selecionado, lotePesquisa));
            cancelarLotePesquisa();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void visualizarDocumentos() {
        try {
            validarVisualizarDocumentos();
            FacesUtil.executaJavaScript("dialogDocs.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarVisualizarDocumentos() {
        ValidacaoException ve = new ValidacaoException();
        if (lotePesquisa == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum lote encontrado para visualizar o(s) documento(s).");
        }
        ve.lancarException();
    }

    public BigDecimal getValorTotalArrematadoLote(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        BigDecimal total = BigDecimal.ZERO;
        if (leilaoAlienacaoLote != null) {
            total = facade.getLeilaoAlienacaoFacade().buscarValorTotalLoteLeilao(leilaoAlienacaoLote);
        }
        return total;
    }

    private void cancelarLotePesquisa() {
        lotePesquisa = null;
        numeroLote = null;
        bensLoteLeilaoPesquisa = Lists.newArrayList();
        bensParecerBaixa = Lists.newArrayList();
    }

    private void validarAdicionarLote() {
        ValidacaoException ve = new ValidacaoException();
        if (lotePesquisa == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum lote encontrado para adicionar.");
        }
        for (EfetivacaoBaixaLote loteBaixa : lotesEfetetivacao) {
            if (loteBaixa.getLeilaoAlienacaoLote().equals(lotePesquisa)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O lote n° " + lotePesquisa.getLoteAvaliacaoAlienacao().getNumero() + " já foi adicionado.");
            }
        }
        ve.lancarException();
    }

    private String getDescricaoHierarquia() {
        try {
            if (!selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
                UnidadeOrganizacional unidadeSolicitacao = selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa().getUnidadeAdministrativa();
                return facade.getHierarquiaOrganizacionalFacade().getDescricaoHierarquia(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeSolicitacao, selecionado.getDataEfetivacao());
            }
        } catch (NullPointerException ex) {
            return "";
        }
        return "";
    }

    public List<VOItemBaixaPatrimonial> getBensParecerBaixa() {
        return bensParecerBaixa;
    }

    public void setBensParecerBaixa(List<VOItemBaixaPatrimonial> bensParecerBaixa) {
        this.bensParecerBaixa = bensParecerBaixa;
    }

    public List<VOItemBaixaPatrimonial> getBensLoteLeilao() {
        return bensLoteLeilao;
    }

    public void setBensLoteLeilao(List<VOItemBaixaPatrimonial> bensLoteLeilao) {
        this.bensLoteLeilao = bensLoteLeilao;
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentoAdm() {
        return assistenteMovimentoAdm;
    }

    public void setAssistenteMovimentoAdm(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentoAdm = assistenteBarraProgresso;
    }

    public List<VOItemBaixaPatrimonial> getBensLoteLeilaoPesquisa() {
        return bensLoteLeilaoPesquisa;
    }

    public void setBensLoteLeilaoPesquisa(List<VOItemBaixaPatrimonial> bensLoteLeilaoPesquisa) {
        this.bensLoteLeilaoPesquisa = bensLoteLeilaoPesquisa;
    }

    public List<LogErroEfetivacaoBaixaBem> getInconsistenciasEfetivacao() {
        return inconsistenciasEfetivacao;
    }

    public void setInconsistenciasEfetivacao(List<LogErroEfetivacaoBaixaBem> inconsistenciasEfetivacao) {
        this.inconsistenciasEfetivacao = inconsistenciasEfetivacao;
    }

    public List<DoctoFiscalLiquidacao> getDocumentosFiscal() {
        return documentosFiscal;
    }

    public void setDocumentosFiscal(List<DoctoFiscalLiquidacao> documentosFiscal) {
        this.documentosFiscal = documentosFiscal;
    }

    public AssistenteContabilizacaoBaixa getAssistenteContabilizacao() {
        return assistenteContabilizacao;
    }

    public void setAssistenteContabilizacao(AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        this.assistenteContabilizacao = assistenteContabilizacao;
    }
}
