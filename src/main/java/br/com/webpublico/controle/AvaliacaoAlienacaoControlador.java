package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteConsultaAvaliacaoAlienacao;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AvaliacaoAlienacaoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Desenvolvimento on 14/09/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAvaliacaoAlienacao", pattern = "/avaliacao-de-alienacao/novo/", viewId = "/faces/administrativo/patrimonio/avaliacao-alienacao/edita.xhtml"),
    @URLMapping(id = "editarAvaliacaoAlienacao", pattern = "/avaliacao-de-alienacao/editar/#{avaliacaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaliacao-alienacao/edita.xhtml"),
    @URLMapping(id = "listarAvaliacaoAlienacao", pattern = "/avaliacao-de-alienacao/listar/", viewId = "/faces/administrativo/patrimonio/avaliacao-alienacao/lista.xhtml"),
    @URLMapping(id = "verAvaliacaoAlienacao", pattern = "/avaliacao-de-alienacao/ver/#{avaliacaoAlienacaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/avaliacao-alienacao/visualizar.xhtml")
})
public class AvaliacaoAlienacaoControlador extends PrettyControlador<AvaliacaoAlienacao> implements Serializable, CRUD {

    @EJB
    private AvaliacaoAlienacaoFacade facade;
    private CompletableFuture<AssistenteConsultaAvaliacaoAlienacao> futureConsultaItensLote;
    private CompletableFuture<AssistenteMovimentacaoBens> futureValidar;
    private CompletableFuture<AvaliacaoAlienacao> futureSalvarAvaliacao;
    private CompletableFuture<List<LoteAvaliacaoAlienacao>> futureSalvarLotes;
    private CompletableFuture<List<ItemSolicitacaoAlienacao>> futureSalvarItens;
    private List<LoteAvaliacaoAlienacao> lotesRemovidos;
    private List<VOItemSolicitacaoAlienacao> bensSelecionados;
    private List<VOItemSolicitacaoAlienacao> bensAprovados;
    private List<VOItemSolicitacaoAlienacao> bensAprovadosPesquisa;
    private LoteAvaliacaoAlienacao loteSelecionado;
    private BigDecimal valorAvaliacao;
    private Boolean editandoLote;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private FiltroPesquisaBem filtroPesquisaBem;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public AvaliacaoAlienacaoControlador() {
        super(AvaliacaoAlienacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/avaliacao-de-alienacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novaAvaliacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
        inicializarListas();
        novoFiltroPesquisaBem();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataAvaliacao(), OperacaoMovimentacaoBem.AVALIACAO_ALIENACAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void inicializarListas() {
        bensSelecionados = Lists.newArrayList();
        lotesRemovidos = Lists.newArrayList();
        bensAprovados = Lists.newArrayList();
        bensAprovadosPesquisa = Lists.newArrayList();
    }

    @URLAction(mappingId = "editarAvaliacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarDependenciasArquivo(getId());
        novoFiltroPesquisaBem();
        for (LoteAvaliacaoAlienacao lote : selecionado.getLotes()) {
            lote.getItensSolicitacao().addAll(facade.buscarItensAprovadosAdicionadosLote(lote, filtroPesquisaBem));
        }
        inicializarListas();
        lotesRemovidos.addAll(selecionado.getLotes());
    }

    @URLAction(mappingId = "verAvaliacaoAlienacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarDependenciasArquivo(getId());
        novoFiltroPesquisaBem();
        for (LoteAvaliacaoAlienacao lote : selecionado.getLotes()) {
            lote.getItensSolicitacao().addAll(facade.buscarItensAprovadosAdicionadosLote(lote, filtroPesquisaBem));
        }
        inicializarListas();
    }

    public void concluir() {
        try {
            assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
            assistenteMovimentacao.setExecutando(true);
            futureSalvarAvaliacao = AsyncExecutor.getInstance().execute(assistenteMovimentacao, () ->
                    facade.concluirAvaliacao(selecionado,assistenteMovimentacao));
            FacesUtil.executaJavaScript("iniciarConcluirAlienacao()");
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentoSingleton();
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            facade.removerSelecionado(selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            bloquearMovimentoSingleton();
            assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
            assistenteMovimentacao.setExecutando(true);
            futureSalvarAvaliacao = AsyncExecutor.getInstance().execute(assistenteMovimentacao, () ->
                    facade.salvarAvaliacao(selecionado,assistenteMovimentacao,lotesRemovidos));
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("iniciarSalvarAlienacao()");
        } catch (MovimentacaoBemException ex) {
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

    private void bloquearMovimentoSingleton() {
        for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(AvaliacaoAlienacao.class, unidade, assistenteMovimentacao);
        }
    }

    private void desbloquearMovimentoSingleton() {
        if (!assistenteMovimentacao.getUnidades().isEmpty()) {
            for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
                facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(AvaliacaoAlienacao.class, unidade);
            }
        }
        FacesUtil.executaJavaScript("aguarde.hide()");
    }


    private void validarSalvar() {
        selecionado.realizarValidacoes();
        validarSeExisteLoteAdicionado();
        validarDataLancamentoAndDataOperacaoBem();
    }

    public void consultarFutureValidacao() {
        if (futureValidar != null && futureValidar.isDone()) {
            try {
                AssistenteMovimentacaoBens assistente = futureValidar.get();
                if (assistente != null) {
                    if (assistente.getMensagens() != null && !assistente.getMensagens().isEmpty()) {
                        for (String mensagem : assistente.getMensagens()) {
                            FacesUtil.addOperacaoNaoPermitida(mensagem);
                        }
                        assistente.getMensagens().clear();
                        FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                        desbloquearMovimentoSingleton();
                    } else {
                        concluir();
                    }
                }
                futureValidar = null;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        }
    }

    public void consultarFutureConcluir() {
        if (futureSalvarAvaliacao != null && futureSalvarAvaliacao.isDone()) {
            try {
                selecionado = futureSalvarAvaliacao.get();
                if (selecionado != null) {
                    FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                    redirecionarParaVer();
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Não foi possível concluir a avaliação");
                }
                desbloquearMovimentoSingleton();
                futureSalvarAvaliacao = null;
            } catch (Exception e) {
                assistenteMovimentacao.descobrirETratarException(e);
                assistenteMovimentacao.setBloquearAcoesTela(true);
                assistenteMovimentacao.setExecutando(false);
                FacesUtil.executaJavaScript("clearInterval(timer)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.atualizarComponente("Formulario");
                futureSalvarAvaliacao = null;
                desbloquearMovimentoSingleton();
            }
        }
    }

    public void consultarFutureSalvarAlienacao() {
        if (futureSalvarAvaliacao != null && futureSalvarAvaliacao.isDone()) {
            try {
                selecionado = futureSalvarAvaliacao.get();
                if (selecionado != null) {
                    assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
                    assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
                    assistenteMovimentacao.setExecutando(true);

                    futureSalvarLotes = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                        () -> facade.salvarLotes(selecionado, selecionado.getLotes(), assistenteMovimentacao));
                    FacesUtil.executaJavaScript("iniciarSalvarLotes()");
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar a avaliação");
                }
                futureSalvarAvaliacao = null;
            } catch (Exception e) {
                assistenteMovimentacao.descobrirETratarException(e);
                assistenteMovimentacao.setBloquearAcoesTela(true);
                assistenteMovimentacao.setExecutando(false);
                redireciona();
                FacesUtil.executaJavaScript("clearInterval(timerSalvarAlienacao)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.executaJavaScript("aguarde.hide()");
                FacesUtil.atualizarComponente("Formulario");
                futureSalvarAvaliacao = null;
                desbloquearMovimentoSingleton();
            }
        }
    }

    public void consultarFutureSalvarLotes() {
        if (futureSalvarLotes != null && futureSalvarLotes.isDone()) {
            try {
                List<LoteAvaliacaoAlienacao> lotesSalvos = futureSalvarLotes.get();
                if (lotesSalvos != null && !lotesSalvos.isEmpty()) {
                    assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
                    assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
                    assistenteMovimentacao.setExecutando(true);
                    futureSalvarLotes = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                        () -> facade.salvarLotes(selecionado, selecionado.getLotes(), assistenteMovimentacao));
                    facade.salvarItens(assistenteMovimentacao, lotesSalvos);
                    FacesUtil.executaJavaScript("iniciarSalvarItens()");
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Não foi possível salvar os lotes");
                }
                futureSalvarLotes = null;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        }
    }

    public void consultarFutureSalvarItens() {
        if (futureSalvarItens != null && futureSalvarItens.isDone()) {
            try {
                futureSalvarItens = null;
                if (selecionado.getId() != null) {
                    FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                    redirecionarParaVer();
                }
                desbloquearMovimentoSingleton();
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        }
    }

    private void validarSeExisteLoteAdicionado() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLotes().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta avaliação não possui lotes. Crie os lotes para continuar.");
        }
        ve.lancarException();
    }

    public void concluirValidarSequenciaRegistroPatrimonial() {
        buscarBensAprovados();
    }

    private void validarSequenciaRegistroPatrimonial() {
        try {
            iniciarAssistenteMovimentacao();
            bloquearMovimentoSingleton();
            assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
            assistenteMovimentacao.setExecutando(true);
            futureValidar = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.verificarItensNaoSelecionadosParaMesmaSequenciaCodigoPatrimonial(assistenteMovimentacao, selecionado, bensAprovados));
            facade.verificarItensNaoSelecionadosParaMesmaSequenciaCodigoPatrimonial(assistenteMovimentacao, selecionado, bensAprovados);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("iniciarValidacao()");
        } catch (MovimentacaoBemException ex) {
            redireciona();
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimentoSingleton();
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            bensAprovados = Lists.newArrayList();
        }
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno("/avaliacao-de-alienacao/listar/");
    }

    public BigDecimal getValorTotalLote(LoteAvaliacaoAlienacao lote) {
        BigDecimal total = BigDecimal.ZERO;
        for (VOItemSolicitacaoAlienacao item : lote.getItensSolicitacao()) {
            total = total.add(item.getValorAvaliado());
        }
        return total;
    }

    public String getValorTotalOriginalItensPorLote(LoteAvaliacaoAlienacao lote) {
        BigDecimal total = BigDecimal.ZERO;
        if (lote.getItensSolicitacao() != null) {
            for (VOItemSolicitacaoAlienacao item : lote.getItensSolicitacao()) {
                total = total.add(item.getValorOriginal());
            }
        }
        return Util.formataValor(total);
    }

    public String getValorTotalAjusteItensPorLote(LoteAvaliacaoAlienacao lote) {
        BigDecimal total = BigDecimal.ZERO;
        if (lote.getItensSolicitacao() != null) {
            for (VOItemSolicitacaoAlienacao item : lote.getItensSolicitacao()) {
                total = total.add(item.getValorAjuste());
            }
        }
        return Util.formataValor(total);
    }

    public String getValorTotalLiquidoItensPorLote(LoteAvaliacaoAlienacao lote) {
        BigDecimal total = BigDecimal.ZERO;
        if (lote.getItensSolicitacao() != null) {
            for (VOItemSolicitacaoAlienacao item : lote.getItensSolicitacao()) {
                total = total.add(item.getValorLiquido());
            }
        }
        return Util.formataValor(total);
    }

    public String getValorTotalAvaliadoItensPorLote(LoteAvaliacaoAlienacao lote) {
        BigDecimal total = BigDecimal.ZERO;
        if (lote.getItensSolicitacao() != null) {
            for (VOItemSolicitacaoAlienacao item : lote.getItensSolicitacao()) {
                total = total.add(item.getValorAvaliado());
            }
        }
        return Util.formataValor(total);
    }

    private void iniciarAssistenteMovimentacao() {
        if (configMovimentacaoBem == null) {
            recuperarConfiguracaoMovimentacaoBem();
        }
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDataAvaliacao(), operacao);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistenteMovimentacao.zerarContadoresProcesso();
        assistenteMovimentacao.setSelecionado(selecionado);

        Set<UnidadeOrganizacional> unidades = new HashSet<>();
        Set<SolicitacaoAlienacao> solicitacoesAlienacao = new HashSet<>();
        if (isOperacaoNovo()) {
            List<AprovacaoAlienacao> aprovacoes = facade.getAprovacaoAlienacaoFacade().buscarAprovacaoDosBensEmAvaliacao();
            for (AprovacaoAlienacao aprovacao : aprovacoes) {
                solicitacoesAlienacao.add(aprovacao.getSolicitacaoAlienacao());
                unidades.add(aprovacao.getSolicitacaoAlienacao().getUnidadeAdministrativa());

            }
        } else {
            for (LoteAvaliacaoAlienacao lote : selecionado.getLotes()) {
                List<SolicitacaoAlienacao> solicitacoes = facade.getSolicitacaoAlienacaoFacade().buscarSolicitacaoPorAvaliacaoAlienacao(lote.getAvaliacaoAlienacao());
                for (SolicitacaoAlienacao sol : solicitacoes) {
                    solicitacoesAlienacao.add(sol);
                    unidades.add(sol.getUnidadeAdministrativa());
                }
            }
        }
        assistenteMovimentacao.getSolicitacoesAlienacao().addAll(solicitacoesAlienacao);
        assistenteMovimentacao.getUnidades().addAll(unidades);
    }

    public void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void buscarBensAprovados() {
        try {
            iniciarAssistenteMovimentacao();
            validarDataLancamentoAndDataOperacaoBem();
            assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
            assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
            assistenteMovimentacao.setDataAtual(facade.getSistemaFacade().getDataOperacao());
            assistenteMovimentacao.setExecutando(true);
            futureConsultaItensLote = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () ->  facade.buscarLotesAvaliados(selecionado,filtroPesquisaBem,assistenteMovimentacao));
        facade.buscarLotesAvaliados(selecionado, filtroPesquisaBem, assistenteMovimentacao);
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarDataLancamentoAndDataOperacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataAvaliacao(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    public void pesquisarBens() {
        List<VOItemSolicitacaoAlienacao> resultado;
        resultado = pesquisarBensAprovado();
        bensAprovados = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            bensAprovados.addAll(resultado);
        }
        pesquisarBemAoEditarLote();
        Collections.sort(bensAprovados);
    }

    private void pesquisarBemAoEditarLote() {
        if (filtroPesquisaBem.getHierarquiaAdministrativa() == null
            && filtroPesquisaBem.getHierarquiaOrcamentaria() == null
            && filtroPesquisaBem.getGrupoBem() == null
            && filtroPesquisaBem.getBem() == null) {

            bensAprovados.addAll(bensAprovadosPesquisa);
            if (!bensSelecionados.isEmpty() && editandoLote) {
                bensAprovados.addAll(bensSelecionados);
            }
        } else {
            if (bensAprovados.isEmpty()) {
                FacesUtil.addAtencao("Nenhum resultado para os filtros informados.");
            }
        }
    }

    private List<VOItemSolicitacaoAlienacao> pesquisarBensAprovado() {
        List<VOItemSolicitacaoAlienacao> toReturn = Lists.newArrayList();
        for (VOItemSolicitacaoAlienacao item : bensAprovadosPesquisa) {
            if (filtroPesquisaBem.getGrupoBem() != null && !filtroPesquisaBem.getGrupoBem().getId().equals(item.getIdGrupoBem())) {
                continue;
            }
            if (filtroPesquisaBem.getBem() != null && !filtroPesquisaBem.getBem().getId().equals(item.getIdBem())) {
                continue;
            }
            if (filtroPesquisaBem.getHierarquiaOrcamentaria() != null && !filtroPesquisaBem.getHierarquiaOrcamentaria().getSubordinada().getId().equals(item.getIdUnidadeOrc())) {
                continue;
            }
            if (filtroPesquisaBem.getHierarquiaAdministrativa() != null && !filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada().getId().equals(item.getIdUnidadeAdm())) {
                continue;
            }
            if (filtroPesquisaBem.getHierarquiaOrcamentaria() != null
                || filtroPesquisaBem.getHierarquiaAdministrativa() != null
                || filtroPesquisaBem.getGrupoBem() != null
                || filtroPesquisaBem.getBem() != null) {
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public void acompanharPesquisa() {
        if (futureConsultaItensLote != null && futureConsultaItensLote.isDone()) {
            if (!assistenteMovimentacao.getMensagens().isEmpty()) {
                futureConsultaItensLote = null;
                ValidacaoException validacao = new ValidacaoException();
                for (String mensagem : assistenteMovimentacao.getMensagens()) {
                    validacao.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
                }
                FacesUtil.printAllFacesMessages(validacao.getAllMensagens());
            }
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() throws ExecutionException, InterruptedException {
        bensAprovados = Lists.newArrayList();
        if (futureConsultaItensLote != null && futureConsultaItensLote.get() != null) {
            bensAprovados.addAll(futureConsultaItensLote.get().getBensAprovados());
            bensAprovadosPesquisa.addAll(bensAprovados);
            if (isOperacaoVer()) {
                if (selecionado.getLotes() != null && !selecionado.getLotes().isEmpty()) {
                    selecionado.getLotes().clear();
                }
                selecionado.setLotes(futureConsultaItensLote.get().getLotesAvaliacao());
                validarSequenciaRegistroPatrimonial();
            }
            futureConsultaItensLote = null;
            if (bensAprovados.isEmpty() && isOperacaoNovo()) {
                FacesUtil.addAtencao("Nenhum bem aprovado para realizar avaliação.");
            }
        }
    }

    private void inicializarAtributos() {
        selecionado.setDataAvaliacao(getDataOperacao());
        selecionado.setResponsavel(facade.getSistemaFacade().getUsuarioCorrente());
        setEditandoLote(Boolean.FALSE);
    }

    public void adicionarLote() {
        try {
            loteSelecionado.realizarValidacoes();
            validarItensSelecionados();
            loteSelecionado.setAvaliacaoAlienacao(selecionado);
            loteSelecionado.getItensSolicitacao().clear();
            for (VOItemSolicitacaoAlienacao item : bensSelecionados) {
                item.setLoteAvaliacaoAlienacao(loteSelecionado);
                if (!loteSelecionado.getItensSolicitacao().contains(item)) {
                    loteSelecionado.getItensSolicitacao().add(item);
                }
                bensAprovadosPesquisa.remove(item);
                bensAprovados.remove(item);
            }
            Util.adicionarObjetoEmLista(selecionado.getLotes(), loteSelecionado);
            bensSelecionados = Lists.newArrayList();
            cancelarLote();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void novoLoteAvaliacao() {
        try {
            novoFiltroPesquisaBem();
            bensAprovados = bensAprovadosPesquisa;
            Collections.sort(bensAprovados);
            loteSelecionado = new LoteAvaliacaoAlienacao();
            loteSelecionado.setNumero(Long.valueOf(selecionado.getLotes().size() + 1));
            setValorAvaliacao(BigDecimal.ZERO);
            setEditandoLote(Boolean.FALSE);
            atribuirZeroValorAvaliacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado);
        filtroPesquisaBem.setTipoBem(TipoBem.MOVEIS);
    }

    private void validarItensSelecionados() {
        ValidacaoException ve = new ValidacaoException();
        if (bensSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um bem para adicionar o lote.");
        }
        ve.lancarException();
        for (VOItemSolicitacaoAlienacao item : bensSelecionados) {
            if (item.getValorAvaliado() == null || item.getValorAvaliado().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o valor de avaliação para o bem " + item.getRegistroPatrimonial() + " - " + item.getDescricao() + ".");
            }
        }
        ve.lancarException();
    }

    public void editarLote(LoteAvaliacaoAlienacao lote) {
        loteSelecionado = ((LoteAvaliacaoAlienacao) Util.clonarObjeto(lote));
        bensAprovados = Lists.newArrayList();
        bensAprovados.addAll(lote.getItensSolicitacao());
        bensAprovados.addAll(bensAprovadosPesquisa);
        bensSelecionados.addAll(lote.getItensSolicitacao());
        Collections.sort(bensAprovados);
        Collections.sort(bensSelecionados);
        setEditandoLote(Boolean.TRUE);
        setValorAvaliacao(BigDecimal.ZERO);
    }

    public void removerLote(LoteAvaliacaoAlienacao lote) {
        bensAprovadosPesquisa.addAll(lote.getItensSolicitacao());
        selecionado.getLotes().remove(lote);
    }

    public void cancelarLote() {
        setLoteSelecionado(null);
        setEditandoLote(Boolean.FALSE);
        novoFiltroPesquisaBem();
    }

    public String getValorTotalOriginalItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (VOItemSolicitacaoAlienacao item : bensSelecionados) {
                total = total.add(item.getValorOriginal());
            }
        }
        return Util.formataValor(total);
    }

    public String getValorTotalAjusteItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (VOItemSolicitacaoAlienacao item : bensSelecionados) {
                total = total.add(item.getValorAjuste());
            }
        }
        return Util.formataValor(total);
    }

    public String getValorTotalLiquidoItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (VOItemSolicitacaoAlienacao item : bensSelecionados) {
                total = total.add(item.getValorLiquido());
            }
        }
        return Util.formataValor(total);
    }

    public String getValorTotalAvaliadoItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (selecionado != null) {
            for (VOItemSolicitacaoAlienacao item : bensSelecionados) {
                total = total.add(item.getValorAvaliado());
            }
        }
        return Util.formataValor(total);
    }

    public void atribuirValorAvaliacao(VOItemSolicitacaoAlienacao item) {
        item.setValorAvaliado(getValorAvaliacao());
    }

    public void atribuirValorAvaliacao() {
        for (VOItemSolicitacaoAlienacao item : bensSelecionados) {
            item.setValorAvaliado(getValorAvaliacao());
        }
    }

    public void atribuirZeroValorAvaliacao() {
        for (VOItemSolicitacaoAlienacao itensSelecionado : bensSelecionados) {
            if (loteSelecionado.getItensSolicitacao().contains(itensSelecionado)) {
                VOItemSolicitacaoAlienacao itemDaPesquisa = (VOItemSolicitacaoAlienacao) Util.clonarObjeto(itensSelecionado);
                bensAprovadosPesquisa.add(itemDaPesquisa);
            }
            itensSelecionado.setValorAvaliado(BigDecimal.ZERO);
        }
        for (VOItemSolicitacaoAlienacao itensSelecionado : bensAprovados) {
            itensSelecionado.setValorAvaliado(BigDecimal.ZERO);
        }
    }

    public void removerValorAvaliacao(VOItemSolicitacaoAlienacao item) {
        item.setValorAvaliado(BigDecimal.ZERO);
    }

    public Boolean mostrarIconCheckAll() {
        return bensAprovados.size() == bensSelecionados.size();
    }

    public void desmarcarTodos() {
        atribuirZeroValorAvaliacao();
        bensSelecionados.clear();
    }

    public void selecionarTodos() {
        bensSelecionados.clear();
        bensSelecionados.addAll(bensAprovados);
        atribuirValorAvaliacao();
    }

    public Boolean itemSelecionado(VOItemSolicitacaoAlienacao item) {
        return bensSelecionados.contains(item);
    }

    public void desmarcarItem(VOItemSolicitacaoAlienacao item) {
        if (loteSelecionado.getItensSolicitacao().contains(item)) {
            VOItemSolicitacaoAlienacao itemDaPesquisa = (VOItemSolicitacaoAlienacao) Util.clonarObjeto(item);
            bensAprovadosPesquisa.add(itemDaPesquisa);
        }
        removerValorAvaliacao(item);
        bensSelecionados.remove(item);
    }

    public void selecionarItem(VOItemSolicitacaoAlienacao item) {
        atribuirValorAvaliacao(item);
        bensSelecionados.add(item);
    }

    public void gerarRelatorioConferencia() {
        try {
            String nomeRelatorio = "RELATÓRIO DE AVALIAÇÃO DE ALIENAÇÃO DE BENS MÓVEIS";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + facade.getSistemaFacade().getMunicipio().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("idAvaliacaoAlienacao", selecionado.getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/avaliacao-alienacao/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (filtroPesquisaBem.getHierarquiaAdministrativa() != null && filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada() != null) {
            return facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada(),
                getDataOperacao());

        } else {
            return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaOrganizacionalOrcamentariaPorCodigoOrDescricao(parte.trim());
        }
    }

    public List<Bem> completarBens(String parte) {
        List<Bem> lista = Lists.newArrayList();
        try {
            lista = facade.getBemFacade().buscarFiltrandoBem(parte.trim(), filtroPesquisaBem);
            verificarSeExisteBensParaUnidade(lista);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return lista;
    }

    private void verificarSeExisteBensParaUnidade(List<Bem> lista) {
        ValidacaoException ve = new ValidacaoException();
        if (filtroPesquisaBem.getHierarquiaAdministrativa() != null && lista.isEmpty()) {
            String descricaoHierarquia = facade.getBemFacade().getHierarquiaOrganizacionalFacade().buscarCodigoDescricaoHierarquia(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                filtroPesquisaBem.getHierarquiaAdministrativa().getSubordinada(),
                selecionado.getDataAvaliacao()
            );
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum bem encontrado para unidade administrativa <b>" + descricaoHierarquia + " </b>");
        } else if (lista.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum bem encontrado");
        }
        ve.lancarException();
    }

    public List<AvaliacaoAlienacao> completarAvaliacaoAlienacaoParaLeilao(String parte) {
        return facade.buscarAvaliacaoAlienacaoParaLeilao(parte.trim());
    }

    public Future<AssistenteConsultaAvaliacaoAlienacao> getFutureConsultaItensLote() {
        return futureConsultaItensLote;
    }

    public void setFutureConsultaItensLote(CompletableFuture<AssistenteConsultaAvaliacaoAlienacao> futureConsultaItensLote) {
        this.futureConsultaItensLote = futureConsultaItensLote;
    }

    public List<VOItemSolicitacaoAlienacao> getBensSelecionados() {
        return bensSelecionados;
    }

    public void setBensSelecionados(List<VOItemSolicitacaoAlienacao> bensSelecionados) {
        this.bensSelecionados = bensSelecionados;
    }

    public List<VOItemSolicitacaoAlienacao> getBensAprovados() {
        return bensAprovados;
    }

    public void setBensAprovados(List<VOItemSolicitacaoAlienacao> bensAprovados) {
        this.bensAprovados = bensAprovados;
    }

    public LoteAvaliacaoAlienacao getLoteSelecionado() {
        return loteSelecionado;
    }

    public void setLoteSelecionado(LoteAvaliacaoAlienacao loteSelecionado) {
        this.loteSelecionado = loteSelecionado;
    }

    public BigDecimal getValorAvaliacao() {
        return valorAvaliacao;
    }

    public void setValorAvaliacao(BigDecimal valorAvaliacao) {
        this.valorAvaliacao = valorAvaliacao;
    }

    public Boolean getEditandoLote() {
        return editandoLote;
    }

    public void setEditandoLote(Boolean editandoLote) {
        this.editandoLote = editandoLote;
    }

    public AssistenteMovimentacaoBens getAssistenteMovimentacao() {
        return assistenteMovimentacao;
    }

    public void setAssistenteMovimentacao(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }

    public List<LoteAvaliacaoAlienacao> getLotesRemovidos() {
        return lotesRemovidos;
    }

    public void setLotesRemovidos(List<LoteAvaliacaoAlienacao> lotesRemovidos) {
        this.lotesRemovidos = lotesRemovidos;
    }

    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }
}
