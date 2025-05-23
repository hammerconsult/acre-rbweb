package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemBaixaPatrimonial;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoParecer;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ParecerBaixaPatrimonialFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 08:37
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "parecerBaixaPatrimonialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParecerBaixaMovel", pattern = "/parecer-baixa-patrimonial-movel/novo/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/movel/edita.xhtml"),
    @URLMapping(id = "editarParecerBaixaMovel", pattern = "/parecer-baixa-patrimonial-movel/editar/#{parecerBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/movel/edita.xhtml"),
    @URLMapping(id = "listarParecerBaixaMovel", pattern = "/parecer-baixa-patrimonial-movel/listar/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/movel/lista.xhtml"),
    @URLMapping(id = "verParecerBaixaMovel", pattern = "/parecer-baixa-patrimonial-movel/ver/#{parecerBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/movel/visualizar.xhtml"),

    @URLMapping(id = "novoParecerBaixaImovel", pattern = "/parecer-baixa-patrimonial-imovel/novo/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/imovel/edita.xhtml"),
    @URLMapping(id = "editarParecerBaixaImovel", pattern = "/parecer-baixa-patrimonial-imovel/editar/#{parecerBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/imovel/edita.xhtml"),
    @URLMapping(id = "listarParecerBaixaImovel", pattern = "/parecer-baixa-patrimonial-imovel/listar/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/imovel/lista.xhtml"),
    @URLMapping(id = "verParecerBaixaImovel", pattern = "/parecer-baixa-patrimonial-imovel/ver/#{parecerBaixaPatrimonialControlador.id}/", viewId = "/faces/administrativo/patrimonio/parecerbaixa/imovel/visualizar.xhtml")
})
public class ParecerBaixaPatrimonialControlador extends PrettyControlador<ParecerBaixaPatrimonial> implements Serializable, CRUD {

    @EJB
    private ParecerBaixaPatrimonialFacade facade;
    private List<VOItemBaixaPatrimonial> bensSolicitacaoBaixa;
    private List<VOItemBaixaPatrimonial> bensDaUnidade;
    private CompletableFuture<List<VOItemBaixaPatrimonial>> futurePesquisaBens;
    private CompletableFuture<ParecerBaixaPatrimonial> futureSalvar;
    private AssistenteMovimentacaoBens assistenteMovimentacao;
    private ConfigMovimentacaoBem configMovimentacaoBem;

    public ParecerBaixaPatrimonialControlador() {
        super(ParecerBaixaPatrimonial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setDateParecer(getDataOperacao());
        selecionado.setParecerista(facade.getSistemaFacade().getUsuarioCorrente());
        inicializarListas();
    }

    private void inicializarListas() {
        bensSolicitacaoBaixa = Lists.newArrayList();
        bensDaUnidade = Lists.newArrayList();
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        selecionado = facade.recuperarComDependeciaArquivo(getId());
        inicializarListas();
        buscarBensAoVisualizar();
    }

    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependeciaArquivo(getId());
        if (!isOperacaoNovo()) {
            redirecionarParaVer();
        }
        recuperarDadosSolicitacao();
    }

    @URLAction(mappingId = "novoParecerBaixaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoParecerMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novoParecerBaixaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoParecerIMovel() {
        try {
            novo();
            selecionado.setTipoBem(TipoBem.IMOVEIS);
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "verParecerBaixaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verParecerMovel() {
        ver();
    }

    @URLAction(mappingId = "verParecerBaixaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verParecerImovel() {
        ver();
    }

    @URLAction(mappingId = "editarParecerBaixaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarParecerMovel() {
        editar();
    }

    @URLAction(mappingId = "editarParecerBaixaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarParecerImovel() {
        editar();
    }

    @Override
    public String getCaminhoPadrao() {
        switch (selecionado.getTipoBem()) {
            case MOVEIS:
                return "/parecer-baixa-patrimonial-movel/";
            case IMOVEIS:
                return "/parecer-baixa-patrimonial-imovel/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void iniciarAssistenteMovimentacao() {
        assistenteMovimentacao = new AssistenteMovimentacaoBens(selecionado.getDateParecer(), operacao);
        assistenteMovimentacao.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistenteMovimentacao.zerarContadoresProcesso();
        assistenteMovimentacao.setSelecionado(selecionado);
        assistenteMovimentacao.setConfigMovimentacaoBem(configMovimentacaoBem);
        recuperarConfiguracaoMovimentacaoBem();
        adicionarUnidadesAssistente();
    }

    private void adicionarUnidadesAssistente() {
        if (selecionado.getSolicitacaoBaixa() != null) {
            if (selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
                List<HierarquiaOrganizacional> hierarquias = facade.getSolicitacaoFacade().buscarHierarquiaAdministrativaItemLoteLeilao(selecionado.getSolicitacaoBaixa().getLeilaoAlienacao());
                for (HierarquiaOrganizacional ho : hierarquias) {
                    assistenteMovimentacao.getUnidades().add(ho.getSubordinada());
                }
            } else {
                assistenteMovimentacao.getUnidades().add(selecionado.getSolicitacaoBaixa().getUnidadeAdministrativa());
            }
        }
    }

    @Override
    public void salvar() {
        try {
            validarParecer();
            iniciarAssistenteMovimentacao();
            validarRegrasConfiguracaoMovimentacaoBem();
            bloquearMovimentacaoBens();

            List<VOItemBaixaPatrimonial> bensParaProcessar = popularListaBensParaProcessar();
            assistenteMovimentacao.setTotal(bensParaProcessar.size());

            futureSalvar = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                () -> facade.salvarRetornando(selecionado, bensParaProcessar, assistenteMovimentacao));

            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
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

    private void bloquearMovimentacaoBens() {
        for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(ParecerBaixaPatrimonial.class, unidade, assistenteMovimentacao);
        }
    }

    private void desbloquearMovimentoSingleton() {
        if (selecionado.getSolicitacaoBaixa() != null) {
            for (UnidadeOrganizacional unidade : assistenteMovimentacao.getUnidades()) {
                facade.getSingletonBloqueioPatrimonio().desbloquearMovimentoPorUnidade(ParecerBaixaPatrimonial.class, unidade);
            }
        }
    }

    private void validarParecer() {
        ValidacaoException ve = new ValidacaoException();
        selecionado.realizarValidacoes();

        if (!selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao() && bensSolicitacaoBaixa.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar é necessário que o parecer possua bens adicionados.");
        }
        if (selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
            for (VOItemBaixaPatrimonial voItemBaixaPatrimonial : bensDaUnidade) {
                if (voItemBaixaPatrimonial.getBensAgrupados().isEmpty()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade " + voItemBaixaPatrimonial.getHierarquiaAdministrativa() + " não possui bens para continuar.");
                }
            }
        }
        if (selecionado.getDateParecer() != null
            && DataUtil.dataSemHorario(selecionado.getDateParecer()).before(DataUtil.dataSemHorario(selecionado.getSolicitacaoBaixa().getDataSolicitacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data do parecer da baixa deve ser posterior ou igual a data da solicitação da baixa: " + DataUtil.getDataFormatada(selecionado.getSolicitacaoBaixa().getDataSolicitacao()) + ".");
        }
        ve.lancarException();
    }

    private List<VOItemBaixaPatrimonial> popularListaBensParaProcessar() {
        List<VOItemBaixaPatrimonial> bensParaProcessar = Lists.newArrayList();
        if (selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
            for (VOItemBaixaPatrimonial unidade : bensDaUnidade) {
                bensParaProcessar.addAll(unidade.getBensAgrupados());
            }
        } else {
            bensParaProcessar.addAll(bensSolicitacaoBaixa);
        }
        return bensParaProcessar;
    }

    public EstadoBem getUltimoEstadoBem(Long idBem) {
        return facade.getBemFacade().recuperarUltimoEstadoDoBem(idBem);
    }

    public List<SolicitacaoBaixaPatrimonial> completarSolicitacaoQueNaoTemParecerMovel(String filtro) {
        if (selecionado.getTipoBem().isMovel()) {
            return facade.getSolicitacaoFacade().buscarSolicitacaoSemParecerBaixaPorTipoBem(filtro.trim(), TipoBem.MOVEIS);
        }
        return facade.getSolicitacaoFacade().buscarSolicitacaoSemParecerBaixaPorTipoBem(filtro.trim(), TipoBem.IMOVEIS);
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public List<SelectItem> getSituacoesDoParecer() {
        return Util.getListSelectItem(Arrays.asList(SituacaoParecer.values()));
    }

    public List<ParecerBaixaPatrimonial> completarParecerDeferidoBemMoveis(String filtro) {
        return facade.buscarParecerDeferido(filtro.trim(), TipoBem.MOVEIS);
    }

    public List<ParecerBaixaPatrimonial> completaParecerDeferidoBemImoveis(String filtro) {
        return facade.buscarParecerDeferido(filtro.trim(), TipoBem.IMOVEIS);
    }

    public void recuperarDadosSolicitacao() {
        try {
            if (selecionado.getSolicitacaoBaixa() != null) {
                iniciarAssistenteMovimentacao();
                validarRegrasConfiguracaoMovimentacaoBem();
                selecionado.setSolicitacaoBaixa(facade.getSolicitacaoFacade().recuperarComDependenciasArquivo(selecionado.getSolicitacaoBaixa().getId()));
                if (bensDaUnidade != null) {
                    bensDaUnidade.clear();
                }
                if (bensSolicitacaoBaixa != null) {
                    bensSolicitacaoBaixa.clear();
                }
                futurePesquisaBens = AsyncExecutor.getInstance().execute(assistenteMovimentacao,
                        () -> facade.buscarBensSolicitacaoBaixa(selecionado, assistenteMovimentacao));
                FacesUtil.executaJavaScript("iniciarPesquisa()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDateParecer(), OperacaoMovimentacaoBem.PARECER_BAIXA_PATRIMONIAL);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDateParecer(), getDataOperacao(), operacao);
        }
    }

    private Date getDataOperacao() {
        return facade.getSistemaFacade().getDataOperacao();
    }

    private void buscarBensAoVisualizar() {
        if (selecionado.getSolicitacaoBaixa() != null) {
            selecionado.setSolicitacaoBaixa(facade.getSolicitacaoFacade().recuperarComDependenciasArquivo(selecionado.getSolicitacaoBaixa().getId()));
            List<VOItemBaixaPatrimonial> bens = facade.buscarBensParecerBaixa(selecionado);
            if (selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
                bensDaUnidade.addAll(bens);
            } else {
                bensSolicitacaoBaixa.addAll(bens);
            }
        }
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                ParecerBaixaPatrimonial parecerSalvo = futureSalvar.get();
                if (parecerSalvo != null) {
                    selecionado = parecerSalvo;
                    desbloquearMovimentoSingleton();
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            } catch (Exception ex) {
                FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
                FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
                FacesUtil.executaJavaScript("aguarde.hide()");
                assistenteMovimentacao.setBloquearAcoesTela(true);
                futureSalvar = null;
                desbloquearMovimentoSingleton();
                assistenteMovimentacao.descobrirETratarException(ex);
            }
        }
    }

    public void finalizarProcesssoSalvar() {
        FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        redirecionarParaVer();
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (futurePesquisaBens != null && futurePesquisaBens.isDone()) {
            if (selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
                bensDaUnidade.addAll(futurePesquisaBens.get());
            } else {
                bensSolicitacaoBaixa.addAll(futurePesquisaBens.get());
            }
            if (assistenteMovimentacao.getMensagens() != null && !assistenteMovimentacao.getMensagens().isEmpty()) {
                for (String msg : assistenteMovimentacao.getMensagens()) {
                    FacesUtil.addOperacaoNaoPermitida(msg);
                }
            }
            FacesUtil.executaJavaScript("terminarPesquisa()");
            futurePesquisaBens = null;
        }
    }

    public void finalizarPesquisa() {
        if (assistenteMovimentacao.getMensagens() != null && !assistenteMovimentacao.getMensagens().isEmpty()) {
            bensDaUnidade.clear();
            bensSolicitacaoBaixa.clear();
            selecionado.setSolicitacaoBaixa(null);
        }
        selecionado.setSolicitacaoBaixa(facade.getSolicitacaoFacade().recuperar(selecionado.getSolicitacaoBaixa().getId()));
        FacesUtil.atualizarComponente("Formulario");
    }

    public AssistenteMovimentacaoBens getAssistenteBarraProgresso() {
        return assistenteMovimentacao;
    }

    public void setAssistenteBarraProgresso(AssistenteMovimentacaoBens assistenteBarraProgresso) {
        this.assistenteMovimentacao = assistenteBarraProgresso;
    }

    public List<VOItemBaixaPatrimonial> getBensSolicitacaoBaixa() {
        return bensSolicitacaoBaixa;
    }

    public void setBensSolicitacaoBaixa(List<VOItemBaixaPatrimonial> bensSolicitacaoBaixa) {
        this.bensSolicitacaoBaixa = bensSolicitacaoBaixa;
    }

    public CompletableFuture<List<VOItemBaixaPatrimonial>> getFuturePesquisaBens() {
        return futurePesquisaBens;
    }

    public void setFuturePesquisaBens(CompletableFuture<List<VOItemBaixaPatrimonial>> futurePesquisaBens) {
        this.futurePesquisaBens = futurePesquisaBens;
    }

    public List<VOItemBaixaPatrimonial> getBensDaUnidade() {
        return bensDaUnidade;
    }

    public void setBensDaUnidade(List<VOItemBaixaPatrimonial> bensDaUnidade) {
        this.bensDaUnidade = bensDaUnidade;
    }

    public void gerarRelatorioParecer() {
        try {
            String nomeRelatorio = "RELATÓRIO DE PARECER DE BAIXA DE BENS MÓVEIS";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("ENTIDADE", getDescricaoEntidade());
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE  RIO BRANCO - AC");
            dto.adicionarParametro("condicao", " and parecer.id = " + selecionado.getId());
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(facade.getSistemaFacade().getDataOperacao()));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/parecer-baixa-patrimonial/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getDescricaoEntidade() {
        String descricaoEntidade = " ";
        HierarquiaOrganizacional hierarquiaAdministrativa = selecionado.getSolicitacaoBaixa().getHierarquiaAdministrativa();
        if (hierarquiaAdministrativa != null) {
            Entidade entidade = facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(hierarquiaAdministrativa.getSubordinada());
            descricaoEntidade = entidade != null ? entidade.getNome().toUpperCase() : " ";
        }
        return descricaoEntidade;
    }
}
