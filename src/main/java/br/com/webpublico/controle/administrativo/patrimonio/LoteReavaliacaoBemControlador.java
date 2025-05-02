package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.administrativo.patrimonio.LoteReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ReavaliacaoBem;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoDaSolicitacao;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoOperacaoReavaliacaoBens;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.administrativo.patrimonio.LoteReavaliacaoBemFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by William on 22/09/2015.
 */

@ManagedBean(name = "loteReavaliacaoBemControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoReavaliacaoBemMovel", pattern = "/solicitacao-reavaliacao-bens-moveis/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/movel/edita.xhtml"),
    @URLMapping(id = "novoaSolicitacaoReavaliacaoBemMovelRecusada", pattern = "/solicitacao-reavaliacao-bens-moveis-recusada/novo/#{loteReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/movel/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoReavaliacaoBemMovel", pattern = "/solicitacao-reavaliacao-bens-moveis/editar/#{loteReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/movel/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoReavaliacaoBemMovel", pattern = "/solicitacao-reavaliacao-bens-moveis/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/movel/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoReavaliacaoBemMovel", pattern = "/solicitacao-reavaliacao-bens-moveis/ver/#{loteReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/movel/visualizar.xhtml"),

    @URLMapping(id = "novaSolicitacaoReavaliacaoBemImovel", pattern = "/solicitacao-reavaliacao-bens-imoveis/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/imovel/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoReavaliacaoBemImovel", pattern = "/solicitacao-reavaliacao-bens-imoveis/editar/#{loteReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/imovel/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoReavaliacaoBemImovel", pattern = "/solicitacao-reavaliacao-bens-imoveis/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/imovel/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoReavaliacaoBemImovel", pattern = "/solicitacao-reavaliacao-bens-imoveis/ver/#{loteReavaliacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoreavaliacaobem/imovel/visualizar.xhtml")
})

public class LoteReavaliacaoBemControlador extends PrettyControlador<LoteReavaliacaoBem> implements CRUD {

    @EJB
    private LoteReavaliacaoBemFacade facade;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private FiltroPesquisaBem filtroPesquisaBem;
    private AssistenteMovimentacaoBens assistente;
    private Future futureSalvar;
    private Future<List<Bem>> futurePesquisaBens;
    private List<Bem> bensDisponiveis;
    private List<Bem> bensSelecionados;
    private Map<Bem, BigDecimal> valorAjustado;

    public LoteReavaliacaoBemControlador() {
        super(LoteReavaliacaoBem.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS:
                return "/solicitacao-reavaliacao-bens-moveis/";
            case IMOVEIS:
                return "/solicitacao-reavaliacao-bens-imoveis/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaSolicitacaoReavaliacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaReavaliacaoMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            inicializarAtributosOperacaoNovo();
            inicializarListas();
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novoaSolicitacaoReavaliacaoBemMovelRecusada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaReavaliacaoBemMovelRecusada() {
        try {
            novaSolicitacaoApartirRecusada();
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novaSolicitacaoReavaliacaoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaReavaliacaoImovel() {
        novo();
        selecionado.setTipoBem(TipoBem.IMOVEIS);
        inicializarAtributosOperacaoNovo();
    }

    @URLAction(mappingId = "verSolicitacaoReavaliacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verReavalicaoBemMovel() {
        ver();
    }

    @URLAction(mappingId = "verSolicitacaoReavaliacaoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verReavalicaoBemImovel() {
        ver();
    }


    @URLAction(mappingId = "editarSolicitacaoReavaliacaoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarReavaliacaoMovel() {
        editar();
    }

    @URLAction(mappingId = "editarSolicitacaoReavaliacaoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarReavaliacaoImovel() {
        editar();
    }

    @Override
    public void editar() {
        super.editar();
        novoFiltroPesquisaBem();
        inicializarListas();
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        bensSelecionados = facade.pesquisarBensVinculadoAoItemReavaliacaoBem(selecionado, null);
        for (Bem bem : bensSelecionados) {
            bem.setValorAjustadoBem(getResultadoDaReavaliacao(bem));
        }
    }

    public void novaSolicitacaoApartirRecusada() {
        LoteReavaliacaoBem loteRecusado = facade.recuperarSimples(super.getId());
        selecionado = new LoteReavaliacaoBem();
        selecionado.setHierarquiaOrganizacional(loteRecusado.getHierarquiaOrganizacional());
        selecionado.setDetentorArquivoComposicao(loteRecusado.getDetentorArquivoComposicao());
        selecionado.setDescricao(loteRecusado.getDescricao());
        selecionado.setTipoOperacaoBem(loteRecusado.getTipoOperacaoBem());
        selecionado.setTipoBem(loteRecusado.getTipoBem());
        selecionado.setResponsavel(loteRecusado.getResponsavel());
        selecionado.setMotivoRecusa(null);
        selecionado.setReavaliacaoBens(new ArrayList<ReavaliacaoBem>());
        inicializarAtributosOperacaoNovo();
        inicializarListas();
        definirUnidadeFiltroPesquisa();
    }

    private void inicializarListas() {
        bensDisponiveis = Lists.newArrayList();
        bensSelecionados = Lists.newArrayList();
    }

    private void inicializarAtributosOperacaoNovo() {
        selecionado.setSituacaoReavaliacaoBem(SituacaoDaSolicitacao.EM_ELABORACAO);
        selecionado.setDataHoraCriacao(getDataOperacao());
        novoFiltroPesquisaBem();
    }

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado.getTipoBem(), selecionado);
    }

    public void concluirReavalicao() {
        try {
            iniciarAssistenteMovimentacao();
            validarSalvar();
            validarConclusaoDeAvaliacao();
            validarDataLancamentoAndDataOperacaoBem();
            bloquearMovimentacaoBens();
            futureSalvar = facade.concluirReavaliacao(selecionado, bensSelecionados, assistente);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    @Override
    public void salvar() {
        try {
            iniciarAssistenteMovimentacao();
            validarSalvar();
            bloquearMovimentacaoBens();
            futureSalvar = facade.salvarReavaliacao(selecionado, bensSelecionados, assistente);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(e);
        }
    }

    public void pesquisarBens() {
        try {
            Util.validarCampos(selecionado);
            iniciarAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            adicionarMapValor();
            inicializarListas();
            futurePesquisaBens = facade.pesquisarBens(filtroPesquisaBem, assistente);
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (MovimentacaoBemException me) {
            FacesUtil.printAllFacesMessages(me.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private boolean verificarBensBloqueadoSingletonAoPesquisar() {
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistente)) {
            if (isOperacaoNovo()) {
                FacesUtil.executaJavaScript("aguarde.hide()");
                FacesUtil.atualizarComponente("Formulario");
                assistente.lancarMensagemBensBloqueioSingleton();
            } else {
                FacesUtil.addOperacaoNaoPermitida(assistente.getMensagens().get(0));
                redireciona();
            }
        }
        return false;
    }

    private void bloquearMovimentacaoBens() {
        if (isOperacaoVer()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(LoteReavaliacaoBem.class, selecionado.getUnidadeOrigem(), assistente);
        }
        if (facade.getSingletonBloqueioPatrimonio().verificarBensBloqueadoSingletonAoPesquisar(assistente)) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.atualizarComponente("Formulario");
            assistente.lancarMensagemBensBloqueioSingleton();
        }
        if (isOperacaoNovo() && !assistente.hasInconsistencias()) {
            facade.getConfigMovimentacaoBemFacade().verificarBensSelecionadosSeDisponivelConfiguracaoBloqueio(assistente);
        }
        if (!isOperacaoVer()) {
            for (Bem bem : bensSelecionados) {
                facade.getSingletonBloqueioPatrimonio().bloquearBem(bem, assistente);
            }
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
        FacesUtil.atualizarComponente("Formulario");
        assistente.lancarMensagemBensBloqueioSingleton();
    }

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistente, LoteReavaliacaoBem.class);
    }

    @Override
    public void excluir() {
        try {
            recuperarConfiguracaoMovimentacaoBem();
            facade.remover(selecionado, configMovimentacaoBem);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }


    private void validarSalvar() {
        selecionado.realizarValidacoes();
        validarReavaliacaoBemAoSalvar();
        validarDataLancamentoAndDataOperacaoBem();
    }

    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            return bensDisponiveis.size() != bensSelecionados.size();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public void desmarcarTodos() {
        for (Bem bem : bensDisponiveis) {
            bem.setValorAjuste(BigDecimal.ZERO);
            bem.setValorAjustadoBem(bem.getValorLiquido());
            valorAjustado.remove(bem);
        }
        bensSelecionados.clear();
    }

    public Boolean itemSelecionado(Bem bem) {
        return bensSelecionados.contains(bem);
    }

    public void desmarcarItem(Bem bem, int linha) {
        bem.setValorAjustadoBem(bem.getValorLiquido());
        bem.setValorAjuste(BigDecimal.ZERO);
        valorAjustado.remove(bem);
        bensSelecionados.remove(bem);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabViewPrincipal:tabelaBens:" + linha + ":btnSelecionar')");
    }

    public void selecionarItem(Bem bem, int linha) {
        bensSelecionados.add(bem);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tabViewPrincipal:tabelaBens:" + linha + ":btnDesmarcar')");
    }

    public void selecionarTodos() {
        try {
            desmarcarTodos();
            bensSelecionados.addAll(bensDisponiveis);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (futurePesquisaBens != null && futurePesquisaBens.isDone()) {
            List<Bem> toReturn = new ArrayList<>();

            for (Bem bemPesquisa : futurePesquisaBens.get()) {

                if (filtroPesquisaBem.getHierarquiaAdministrativa() != null && filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada().equals(bemPesquisa.getUltimoEstado().getDetentoraAdministrativa())) {
                    toReturn.add(bemPesquisa);

                } else if (filtroPesquisaBem.getHierarquiaOrcamentaria() != null && filtroPesquisaBem.getHierarquiaOrcamentaria().getSubordinada().equals(bemPesquisa.getUltimoEstado().getDetentoraOrcamentaria())) {
                    toReturn.add(bemPesquisa);

                } else if (filtroPesquisaBem.getGrupoBem() != null && filtroPesquisaBem.getGrupoBem().equals(bemPesquisa.getGrupoBem())) {
                    toReturn.add(bemPesquisa);

                } else if (filtroPesquisaBem.getBem() != null && filtroPesquisaBem.getBem().equals(bemPesquisa)) {
                    toReturn.add(bemPesquisa);

                } else {
                    toReturn.add(bemPesquisa);
                }
            }
            bensDisponiveis = toReturn;
            futurePesquisaBens = null;
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    private void adicionarMapValor() {
        for (Bem bensSelecionado : bensSelecionados) {
            for (Bem bemDaLista : bensDisponiveis) {
                if (bemDaLista.equals(bensSelecionado)) {
                    valorAjustado.put(bemDaLista, bemDaLista.getValorAjuste());
                }
            }
        }
    }

    public void buscarBensAoEditar() {
        if (isOperacaoEditar()) {
            definirUnidadeFiltroPesquisa();
            pesquisarBens();
        }
    }

    public void definirUnidadeFiltroPesquisa() {
        novoFiltroPesquisaBem();
        if (selecionado.getHierarquiaOrganizacional() != null) {
            filtroPesquisaBem.setHierarquiaAdministrativa(selecionado.getHierarquiaOrganizacional());
            try {
                selecionado.setResponsavel(facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(selecionado.getUnidadeOrigem(), getDataOperacao()).getResponsavel());
            } catch (ExcecaoNegocioGenerica ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
        inicializarListas();
    }

    public void finalizarProcesssoSalvar() {
        redirecionarParaVer();
        FacesUtil.executaJavaScript("clearInterval(timerAcompanhaSalvar)");
        if (selecionado.estaEmElaboracao()) {
            FacesUtil.addOperacaoRealizada(" Solicitação de reavaliação de bens salva com sucesso.");
        } else {
            FacesUtil.addOperacaoRealizada(" Solicitação de reavaliação de bens concluída com sucesso.");
        }
    }

    protected void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public boolean hasBensSelecinados() {
        return bensSelecionados != null && !bensSelecionados.isEmpty();
    }

    private void selecionarMesmoBemAoPesquisar() {
        if (isOperacaoEditar()
            && selecionado.getUnidadeOrigem() != null
            && selecionado.getUnidadeOrigem().equals(filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada())) {
            List<ReavaliacaoBem> reavaliacoes = facade.buscarItensDaSolicitacao(selecionado);
            List<Bem> bensSalvos = assistente.getBensSalvos();

            if (!bensSalvos.isEmpty()) {
                bensDisponiveis.addAll(bensSalvos);
                for (Bem bem : bensDisponiveis) {
                    for (ReavaliacaoBem reavaliacao : reavaliacoes) {
                        if (bem.equals(reavaliacao.getBem())) {
                            bem.setValorAjuste(reavaliacao.getValor());
                            bem.setValorAjustadoBem(getResultadoDaReavaliacao(bem));
                            if (!bensSelecionados.contains(bem)) {
                                bensSelecionados.add(bem);
                            }
                        }
                    }
                }
            }
        }
    }

    public void finalizarPesquisa() {
        if (valorAjustado == null) {
            selecionarMesmoBemAoPesquisar();
        } else {
            if (isOperacaoEditar()) {
                bensDisponiveis.addAll(assistente.getBensSalvos());
            }
            if (!valorAjustado.isEmpty()) {
                for (Bem bem : valorAjustado.keySet()) {
                    bem.setValorAjuste(valorAjustado.get(bem));
                    bem.setValorAjustadoBem(getResultadoDaReavaliacao(bem));
                    bensSelecionados.add(bem);

                    if (bensDisponiveis.contains(bem)) {
                        bensDisponiveis.get(bensDisponiveis.indexOf(bem)).setValorAjuste(bem.getValorAjuste());
                        bensDisponiveis.get(bensDisponiveis.indexOf(bem)).setValorAjustadoBem(getResultadoDaReavaliacao(bem));
                    }
                }
            }
        }
        valorAjustado = Maps.newHashMap();
        Collections.sort(bensDisponiveis);
        Collections.sort(bensSelecionados);
        FacesUtil.atualizarComponente("Formulario");
    }

    private void iniciarAssistenteMovimentacao() {
        recuperarConfiguracaoMovimentacaoBem();
        assistente = new AssistenteMovimentacaoBens(selecionado.getDataHoraCriacao(), operacao);
        assistente.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistente.setSelecionado(selecionado);
        if (selecionado.getUnidadeOrigem() != null) {
            assistente.setUnidadeOrganizacional(selecionado.getUnidadeOrigem());
        }
        assistente.setBensSelecionados(bensSelecionados);
    }

    public String formatarValor(BigDecimal valor) {
        return Util.formataValor(valor);
    }

    private void validarReavaliacaoBemAoSalvar() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (bensSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Marque ao menos um bem para ser reavaliado.");
        }
        ve.lancarException();
        for (Bem bem : bensSelecionados) {
            validarReavaliacaoBem(bem);
        }
    }

    private void validarConclusaoDeAvaliacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        for (Bem bem : bensSelecionados) {
            if (bem.getValorAjuste() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O bem de Registro Patrimonial " + bem.getIdentificacao() + " não tem valor para reavaliação.");
            }
            if (selecionado.isReavaliacaoBemDiminutiva() && bem.getValorAjuste().compareTo(bem.getUltimoEstado().getValorOriginal()) >= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A reavaliação de bens móveis diminutiva não pode deixar o bem com valor zero ou menor que zero, verifique o" +
                    " bem de Registro Patrimonial " + bem.getIdentificacao() + ".");
            }
        }
        ve.lancarException();
    }


    public BigDecimal getValorTotalOriginal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        try {
            if (!bensSelecionados.isEmpty()) {
                for (Bem bem : bensSelecionados) {
                    valorTotal = valorTotal.add(bem.getValorOriginal());
                }
            }
            return valorTotal;
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalAjuste() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        try {
            if (!bensSelecionados.isEmpty()) {
                for (Bem bem : bensSelecionados) {
                    valorTotal = valorTotal.add(bem.getValorDosAjustes());
                }
            }
            return valorTotal;
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalLiquido() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        try {
            if (!bensSelecionados.isEmpty()) {
                for (Bem bem : bensSelecionados) {
                    valorTotal = valorTotal.add(bem.getValorLiquido());
                }
            }
            return valorTotal;
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalReavaliacao() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        try {
            if (!bensSelecionados.isEmpty()) {
                for (Bem bem : bensSelecionados) {
                    valorTotal = valorTotal.add(bem.getValorAjuste());
                }
            }
            return valorTotal;
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }


    public BigDecimal getValorTotalFinal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        try {
            if (!bensSelecionados.isEmpty()) {
                for (Bem bem : bensSelecionados) {
                    valorTotal = valorTotal.add(bem.getValorAjustadoBem());
                }
            }
            return valorTotal;
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }


    public List<SelectItem> getTipoOperacaoBens() {
        if (selecionado.getTipoBem().isMovel()) {
            return Util.getListSelectItem(TipoOperacaoReavaliacaoBens.getOperacoesBemMovel());
        } else {
            return Util.getListSelectItem(TipoOperacaoReavaliacaoBens.getOperacoesBemImovel());
        }
    }

    public void calcularValorFinalReavaliacao(Bem bem) {
        try {
            validarReavaliacaoBem(bem);
            bem.setValorAjustadoBem(getResultadoDaReavaliacao(bem));
        } catch (ValidacaoException ve) {
            bem.setValorAjustadoBem(bem.getValorOriginal());
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public BigDecimal getResultadoDaReavaliacao(Bem bem) {
        try {
            BigDecimal valorTotal = BigDecimal.ZERO;
            if (selecionado.isReavaliacaoBemDiminutiva()) {
                BigDecimal resultado = bem.getValorLiquido().subtract(bem.getValorAjuste());
                valorTotal = valorTotal.add(resultado);
            } else {
                BigDecimal resultado = bem.getValorLiquido().add(bem.getValorAjuste());
                valorTotal = valorTotal.add(resultado);
            }
            return valorTotal;
        } catch (Exception ex) {
            descobrirETratarException(ex);
            return bem.getValorOriginal();
        }
    }

    private void validarReavaliacaoBem(Bem bem) {
        ValidacaoException ve = new ValidacaoException();
        if (bem.getValorAjuste() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Valor da Reavaliação deve ser informado.");
        } else {
            if (selecionado.isReavaliacaoBemDiminutiva()) {
                if (bem.getValorAjuste().compareTo(bem.getValorLiquido()) > 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reavaliação do bem <b>" + bem.getIdentificacao() + "</b> deve ser menor ou igual ao valor líquido. ");
                }
            }
            if (bem.getValorAjuste().compareTo(BigDecimal.ZERO) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor da reavaliação do bem <b> " + bem.getIdentificacao() + "</b> deve ser maior que zero(0).");
                bem.setValorAjuste(BigDecimal.ZERO);
            }
        }
        ve.lancarException();
    }


    public List<SelectItem> retornarHierarquiaOrcamentaria() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (selecionado.getHierarquiaOrganizacional() != null && selecionado.getHierarquiaOrganizacional().getSubordinada() != null) {
            toReturn.add(new SelectItem(null, ""));
            for (HierarquiaOrganizacional obj : facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(selecionado.getHierarquiaOrganizacional().getSubordinada(),
                getDataOperacao())) {
                toReturn.add(new SelectItem(obj, obj.toString()));
            }
        }
        return toReturn;
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        if (configMovimentacaoBem == null) {
            ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataHoraCriacao(), OperacaoMovimentacaoBem.SOLICITACAO_REAVALIACAO_BEM);
            if (configMovimentacaoBem != null) {
                this.configMovimentacaoBem = configMovimentacaoBem;
            }
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataHoraCriacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                LoteReavaliacaoBem loteSalvo = (LoteReavaliacaoBem) futureSalvar.get();
                if (loteSalvo != null) {
                    selecionado = loteSalvo;
                    desbloquearMovimentacaoSingleton();
                    finalizarProcesssoSalvar();
                    futureSalvar = null;
                }
            } catch (Exception ex) {
                assistente.setBloquearAcoesTela(true);
                assistente.descobrirETratarException(ex);
                futureSalvar = null;
                FacesUtil.executaJavaScript("clearInterval(timerAcompanhaSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.atualizarComponente("Formulario");
                desbloquearMovimentacaoSingleton();
            }
        }
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }

    public void setAssistente(AssistenteMovimentacaoBens assistente) {
        this.assistente = assistente;
    }

    public List<Bem> getBensDisponiveis() {
        return bensDisponiveis;
    }

    public void setBensDisponiveis(List<Bem> bensDisponiveis) {
        this.bensDisponiveis = bensDisponiveis;
    }

    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public AssistenteMovimentacaoBens getAssistente() {
        return assistente;
    }

    public Boolean mostrarBotaoExcluir() {
        return !selecionado.foiAceito() && !selecionado.foiRecusado();
    }

    public Boolean mostrarBotaoEditar() {
        return selecionado.estaEmElaboracao();
    }

    public Boolean mostrarBotaoConcluir() {
        return selecionado.estaEmElaboracao();
    }

    public Boolean mostrarBotaoCriarNova() {
        return selecionado.foiRecusado();
    }

    public List<Bem> getBensSelecionados() {
        return bensSelecionados;
    }

    public void setBensSelecionados(List<Bem> bensSelecionados) {
        this.bensSelecionados = bensSelecionados;
    }
}
