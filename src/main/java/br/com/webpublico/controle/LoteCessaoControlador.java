package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.LoteCessaoFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 05/05/14
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "loteCessaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCessaoBens", pattern = "/cessao-de-bens/novo/", viewId = "/faces/administrativo/patrimonio/cessao/edita.xhtml"),
    @URLMapping(id = "editarCessaoBens", pattern = "/cessao-de-bens/editar/#{loteCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/cessao/edita.xhtml"),
    @URLMapping(id = "listarCessaoBens", pattern = "/cessao-de-bens/listar/", viewId = "/faces/administrativo/patrimonio/cessao/lista.xhtml"),
    @URLMapping(id = "verCessaoBens", pattern = "/cessao-de-bens/ver/#{loteCessaoControlador.id}/", viewId = "/faces/administrativo/patrimonio/cessao/visualizar.xhtml")
})
public class LoteCessaoControlador extends PrettyControlador<LoteCessao> implements Serializable, CRUD {

    @EJB
    private LoteCessaoFacade facade;
    private PrazoCessao prazoCessao;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrigem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalDestino;
    private HashMap<LoteCessaoDevolucao, List<CessaoDevolucao>> mapDevolucao;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private FiltroPesquisaBem filtroPesquisaBem;
    private AssistenteMovimentacaoBens assistente;
    private CompletableFuture<LoteCessao> futureSalvar;
    private Future<List<Bem>> futurePesquisaBens;
    private List<Bem> bensDisponiveis;
    private List<Bem> bensSelecionados;

    public LoteCessaoControlador() {
        super(LoteCessao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoCessaoBens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            selecionado.setTipoBem(TipoBem.MOVEIS);
            selecionado.setDataHoraCriacao(getDataOperacao());
            novoFiltroPesquisaBem();
            iniciarPrazoCessao();
            iniciarAssistenteMovimentacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void novoFiltroPesquisaBem() {
        filtroPesquisaBem = new FiltroPesquisaBem(selecionado.getTipoBem(), selecionado);
        filtroPesquisaBem.setDataOperacao(selecionado.getDataHoraCriacao());
    }

    @URLAction(mappingId = "verCessaoBens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        prazoCessao = selecionado.getUltimoPrazoCessao();
        recuperarDevolucoes();
        definirHierarquiasOrigemAndDestino();
        iniciarAssistenteMovimentacao();
    }

    @URLAction(mappingId = "editarCessaoBens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        operacao = Operacoes.EDITAR;
        selecionado = facade.recuperarComDependencias(getId());
        iniciarPrazoCessao();
        definirHierarquiasOrigemAndDestino();
        novoFiltroPesquisaBem();
        inicializarListas();
        iniciarAssistenteMovimentacao();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cessao-de-bens/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            iniciarAssistenteMovimentacao();
            validarRegrasDeNegocio();
            bloquearBens();
            assistente.setDescricaoProcesso("Salvando Solicitação de Cessão de Bens");
            futureSalvar = AsyncExecutor.getInstance().execute(assistente, () -> facade.salvarRetornando(selecionado, bensSelecionados, assistente));
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (MovimentacaoBemException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            if (!isOperacaoNovo()) {
                redireciona();
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            desbloquearMovimentacaoSingleton();
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

    private void desbloquearMovimentacaoSingleton() {
        facade.getSingletonBloqueioPatrimonio().desbloquearMovimentacaoSingleton(assistente, LoteCessao.class);
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    public void concluir() {
        try {
            iniciarAssistenteMovimentacao();
            validarAoConcluir();
            bloquearBens();
            assistente.setDescricaoProcesso("Concluíndo Solicitação de Cessão de Bens");
            futureSalvar = AsyncExecutor.getInstance().execute(assistente, () -> facade.concluirCessao(selecionado, assistente));
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
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            desbloquearMovimentacaoSingleton();
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            desbloquearMovimentacaoSingleton();
            descobrirETratarException(e);
        }
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

    public void pesquisaBens() {
        try {
            iniciarAssistenteMovimentacao();
            validarPesquisaBem();
            if (verificarBensBloqueadoSingletonAoPesquisar()) return;
            inicializarListas();
            Util.adicionarObjetoEmLista(selecionado.getListaDePrazos(), prazoCessao);
            futurePesquisaBens = facade.pesquisarBens(filtroPesquisaBem, assistente);
            FacesUtil.executaJavaScript("iniciarPesquisa()");
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

    private void bloquearBens() {
        if (isOperacaoVer()) {
            facade.getSingletonBloqueioPatrimonio().bloquearMovimentoPorUnidade(LoteCessao.class, selecionado.getUnidadeOrigem(), assistente);
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

    public void buscarBensAoEditar() {
        if (isOperacaoEditar()) {
            definirUnidadeFiltroPesquisa();
            pesquisaBens();
        }
    }

    private void iniciarPrazoCessao() {
        prazoCessao = selecionado.getUltimoPrazoCessao();
    }

    private void definirHierarquiasOrigemAndDestino() {
        if (selecionado.getUnidadeOrigem() != null) {
            hierarquiaOrganizacionalOrigem = recuperarHierarquiaPorUnidade(selecionado.getUnidadeOrigem());
        }
        if (selecionado.getUnidadeDestino() != null) {
            hierarquiaOrganizacionalDestino = recuperarHierarquiaPorUnidade(selecionado.getUnidadeDestino());
        }
    }

    private HierarquiaOrganizacional recuperarHierarquiaPorUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidadeOrganizacional, selecionado.getDataHoraCriacao()
        );
    }

    private void validarPesquisaBem() {
        validarUnidadeOrigemPesquisa();
        selecionado.realizarValidacoes();
        prazoCessao.realizarValidacoes();
        validarDataLancamentoAndDataOperacaoBem();
    }

    public void definirUnidadeFiltroPesquisa() {
        if (hierarquiaOrganizacionalOrigem != null) {
            filtroPesquisaBem.setHierarquiaAdministrativa(hierarquiaOrganizacionalOrigem);
        }
    }

    public void acompanharPesquisa() throws ExecutionException, InterruptedException {
        if (futurePesquisaBens != null && futurePesquisaBens.isDone()) {
            bensDisponiveis.addAll(futurePesquisaBens.get());
            futurePesquisaBens = null;
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    public void finalizarPesquisa() {
        if (isOperacaoEditar()) {
            List<Bem> bensSalvos = assistente.getBensSalvos();
            bensDisponiveis.addAll(bensSalvos);
            bensSelecionados.addAll(bensSalvos);
        }
        Collections.sort(bensDisponiveis);
        Collections.sort(bensSelecionados);
        FacesUtil.atualizarComponente("Formulario");
    }

    private void iniciarAssistenteMovimentacao() {
        recuperarConfiguracaoMovimentacaoBem();
        assistente = new AssistenteMovimentacaoBens(selecionado.getDataHoraCriacao(), operacao);
        assistente.setSelecionado(selecionado);
        assistente.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistente.setBensSelecionados(bensSelecionados);
        assistente.setUnidadeOrganizacional(selecionado.getUnidadeOrigem());
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    public void consultarFutureSalvar() {
        try {
            if (futureSalvar != null && futureSalvar.isDone()) {
                LoteCessao loteCessaoSalvo = futureSalvar.get();
                if (loteCessaoSalvo != null) {
                    selecionado = loteCessaoSalvo;
                    desbloquearMovimentacaoSingleton();
                    assistente.setMensagens(Lists.<String>newArrayList());
                    FacesUtil.executaJavaScript("finalizarSalvar()");
                }
            }
        } catch (Exception ex) {
            assistente.setBloquearAcoesTela(true);
            FacesUtil.executaJavaScript("clearInterval(timerSalvar)");
            FacesUtil.executaJavaScript("closeDialog(dlgSalvar)");
            FacesUtil.atualizarComponente("Formulario");
            futureSalvar = null;
            desbloquearMovimentacaoSingleton();
            assistente.descobrirETratarException(ex);
        }
    }

    public void finalizarFutureSalvar() {
        futureSalvar = null;
        if (selecionado.emElaboracao()) {
            FacesUtil.addOperacaoRealizada("Solicitação de cessão de bens salva com sucesso.");
        } else {
            FacesUtil.addOperacaoRealizada("Solicitação de cessão de bens concluída com sucesso.");
        }
        redirecionarParaVer();
    }

    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            return bensDisponiveis.size() != bensSelecionados.size();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public void desmarcarTodos() {
        bensSelecionados.clear();
    }

    public Boolean itemSelecionado(Bem bem) {
        return bensSelecionados.contains(bem);
    }

    public void desmarcarItem(Bem bem) {
        bensSelecionados.remove(bem);
    }

    public void selecionarItem(Bem bem) {
        try {
            ValidacaoException ve = new ValidacaoException();
            ve.lancarException();
            bensSelecionados.add(bem);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void selecionarTodos() {
        try {
            desmarcarTodos();
            bensSelecionados.addAll(bensDisponiveis);
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    private void inicializarListas() {
        bensSelecionados = Lists.newArrayList();
        bensDisponiveis = Lists.newArrayList();
    }

    private void validarUnidadeOrigemPesquisa() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroPesquisaBem.getHierarquiaAdministrativa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa de Origem deve ser informado.");
        }
        ve.lancarException();
    }

    public void validarRegrasDeNegocio() throws ValidacaoException, ExcecaoNegocioGenerica {
        selecionado.realizarValidacoes();
        prazoCessao.realizarValidacoes();

        ValidacaoException ve = new ValidacaoException();
        if (selecionado.isExterno() && (selecionado.getUnidadeExterna() != null && selecionado.getUnidadeExterna().trim().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Destino Externo deve ser informado.");
        }
        if (selecionado.isExterno() && (selecionado.getResponsavelExterno() != null && selecionado.getResponsavelExterno().trim().isEmpty())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Responsável do Destino Externo deve ser informado.");
        }
        if (selecionado.isInterno() && selecionado.getUnidadeDestino() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Administrativa de Destino deve ser informado.");
        }
        if (selecionado.isInterno() && selecionado.getResponsavelPeloRecebimento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Responsável de Destino deve ser informado.");
        }
        if (selecionado.isInterno() && selecionado.getUnidadeDestino().equals(selecionado.getUnidadeOrigem())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade de destino deve ser diferente da unidade de origem.");
        }
        if (isOperacaoNovo() && (bensSelecionados == null || bensSelecionados.isEmpty())) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("A cessão não possui bens. Para continuar, adicione um ou mais bens para esta cessão.");
        }
        ve.lancarException();
        prazoCessao.validarNegocio(DataUtil.dataSemHorario(getDataOperacao()));

        ParametroPatrimonio parametroPatrimonio = facade.getParametroPatrimonioFacade().recuperarParametroComDependenciasEntidadeGeradoCodigo();
        facade.verificarSeUnidadePossuiSequenciaPropria(selecionado.getUnidadeOrigem(), parametroPatrimonio, selecionado);
        facade.verificarSeUnidadePossuiSequenciaPropria(selecionado.getUnidadeDestino(), parametroPatrimonio, selecionado);

        validarDataLancamentoAndDataOperacaoBem();
        ve.lancarException();
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataHoraCriacao(), OperacaoMovimentacaoBem.SOLICITACAO_CESSAO_BEM);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
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

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getUrlKeyValue() + "/");
    }

    private void validarAoConcluir() {
        validarRegrasDeNegocio();
        validarDataLancamentoAndDataOperacaoBem();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalDestino() {
        return hierarquiaOrganizacionalDestino;
    }

    public void setHierarquiaOrganizacionalDestino(HierarquiaOrganizacional destino) {
        this.hierarquiaOrganizacionalDestino = destino;
        if (destino != null) {
            selecionado.setUnidadeDestino(destino.getSubordinada());
            try {
                selecionado.setResponsavelPeloRecebimento(facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(destino.getSubordinada(), getDataOperacao()).getResponsavel());
            } catch (ExcecaoNegocioGenerica ex) {
                selecionado.setResponsavelPeloRecebimento(null);
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrigem() {
        return hierarquiaOrganizacionalOrigem;
    }

    public void setHierarquiaOrganizacionalOrigem(HierarquiaOrganizacional origem) {
        this.hierarquiaOrganizacionalOrigem = origem;
        if (origem != null) {
            selecionado.setUnidadeOrigem(origem.getSubordinada());
            try {
                selecionado.setResponsavelPeloEnvio(facade.getParametroPatrimonioFacade().recuperarResponsavelVigente(origem.getSubordinada(), getDataOperacao()).getResponsavel());
            } catch (ExcecaoNegocioGenerica ex) {
                selecionado.setResponsavelPeloEnvio(null);
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }

    public PrazoCessao getPrazoCessao() {
        return prazoCessao;
    }

    public void setPrazoCessao(PrazoCessao prazoCessao) {
        this.prazoCessao = prazoCessao;
    }

    public Boolean mostrarBotaoExcluir() {
        return !selecionado.foiAceita() && !selecionado.foiRecusado();
    }

    public Boolean mostrarBotaoEditar() {
        return selecionado.emElaboracao();
    }

    public List<LoteCessaoDevolucao> getListaLoteCessaoDevolucao() {
        try {
            return Lists.newArrayList(mapDevolucao.keySet());
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public List<CessaoDevolucao> getListaCessaoDevolucao(LoteCessaoDevolucao lote) {
        try {
            return mapDevolucao.get(lote);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void recuperarDevolucoes() {
        List<CessaoDevolucao> lista = facade.getCessaoDevolucaoFacade().buscarCessoesDevolvidas(selecionado);
        mapDevolucao = new HashMap<>();
        for (CessaoDevolucao cessaoDevolucao : lista) {
            if (mapDevolucao.containsKey(cessaoDevolucao.getLoteCessaoDevolucao())) {
                mapDevolucao.get(cessaoDevolucao.getLoteCessaoDevolucao()).add(cessaoDevolucao);
            } else {
                List<CessaoDevolucao> nova = new ArrayList<>();
                nova.add(cessaoDevolucao);
                mapDevolucao.put(cessaoDevolucao.getLoteCessaoDevolucao(), nova);
            }
        }
    }

    public List<SelectItem> getTiposCessao() {
        return Util.getListSelectItem(TipoCessao.values());
    }

    public void limparOrigens() {
        selecionado.setUnidadeDestino(null);
        selecionado.setUnidadeOrigem(null);
        selecionado.setResponsavelPeloEnvio(null);
        selecionado.setResponsavelPeloRecebimento(null);
        selecionado.setResponsavelExterno(null);
        selecionado.setUnidadeExterna(null);
        setHierarquiaOrganizacionalOrigem(null);
        setHierarquiaOrganizacionalDestino(null);
    }

    public FiltroPesquisaBem getFiltroPesquisaBem() {
        return filtroPesquisaBem;
    }

    public void setFiltroPesquisaBem(FiltroPesquisaBem filtroPesquisaBem) {
        this.filtroPesquisaBem = filtroPesquisaBem;
    }

    public AssistenteMovimentacaoBens getAssistente() {
        return assistente;
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

    public List<Bem> getBensSelecionados() {
        return bensSelecionados;
    }

    public void setBensSelecionados(List<Bem> bensSelecionados) {
        this.bensSelecionados = bensSelecionados;
    }
}
