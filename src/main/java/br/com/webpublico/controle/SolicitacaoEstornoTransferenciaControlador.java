package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Bem;
import br.com.webpublico.entidades.ConfigMovimentacaoBem;
import br.com.webpublico.entidades.LoteEfetivacaoTransferenciaBem;
import br.com.webpublico.entidades.SolicitacaoEstornoTransferencia;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SolicitacaoEstornoTransferenciaFacade;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 24/09/14
 * Time: 08:36
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "solicitacaoEstornoTransferenciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoEstornoTransferenciaMovel", pattern = "/solicitacao-estorno-transferencia-movel/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "editarEstornoTransferenciaMovel", pattern = "/solicitacao-estorno-transferencia-movel/editar/#{solicitacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/movel/edita.xhtml"),
    @URLMapping(id = "listarEstornoTransferenciaMovel", pattern = "/solicitacao-estorno-transferencia-movel/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/movel/lista.xhtml"),
    @URLMapping(id = "verEstornoTransferenciaMovel", pattern = "/solicitacao-estorno-transferencia-movel/ver/#{solicitacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/movel/visualizar.xhtml"),

    @URLMapping(id = "novoEstornoTransferenciaImovel", pattern = "/solicitacao-estorno-transferencia-imovel/novo/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "editarEstornoTransferenciaImovel", pattern = "/solicitacao-estorno-transferencia-imovel/editar/#{solicitacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/imovel/edita.xhtml"),
    @URLMapping(id = "listarEstornoTransferenciaImovel", pattern = "/solicitacao-estorno-transferencia-imovel/listar/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/imovel/lista.xhtml"),
    @URLMapping(id = "verEstornoTransferenciaImovel", pattern = "/solicitacao-estorno-transferencia-imovel/ver/#{solicitacaoEstornoTransferenciaControlador.id}/", viewId = "/faces/administrativo/patrimonio/solicitacaoestornotransferencia/imovel/visualizar.xhtml")
})
public class SolicitacaoEstornoTransferenciaControlador extends PrettyControlador<SolicitacaoEstornoTransferencia> implements Serializable, CRUD {

    @EJB
    private SolicitacaoEstornoTransferenciaFacade facade;
    private TipoBem tipoBem;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private AssistenteMovimentacaoBens assistente;
    private Future futureSalvar;
    private Future<List<Bem>> futurePesquisaBens;
    private List<Bem> bensDisponiveis;
    private List<Bem> bensSelecionados;

    public SolicitacaoEstornoTransferenciaControlador() {
        super(SolicitacaoEstornoTransferencia.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novoEstornoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEstornoTransferenciaMovel() {
        try {
            this.tipoBem = TipoBem.MOVEIS;
            novo();
            recuperarConfiguracaoMovimentacaoBem();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    @URLAction(mappingId = "novoEstornoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoEstornoTransferenciaImovel() {
        this.tipoBem = TipoBem.IMOVEIS;
        novo();
    }

    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(null);
        selecionado.setDataDeCriacao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setSolicitante(facade.getSistemaFacade().getUsuarioCorrente());
        inicializarListas();
    }

    @URLAction(mappingId = "verEstornoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEstornoTransferenciaMovel() {
        this.tipoBem = TipoBem.MOVEIS;
        ver();
    }

    @URLAction(mappingId = "verEstornoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verEstornoTransferenciaImovel() {
        this.tipoBem = TipoBem.IMOVEIS;
        ver();
    }

    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        bensSelecionados = facade.pesquisarBensVinculadoAoItemEstornoTrasnferencia(selecionado, null);
    }

    @URLAction(mappingId = "editarEstornoTransferenciaMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEstornoTransferenciaMovel() {
        this.tipoBem = TipoBem.MOVEIS;
        editar();
    }

    @URLAction(mappingId = "editarEstornoTransferenciaImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editarEstornoTransferenciaImovel() {
        this.tipoBem = TipoBem.IMOVEIS;
        editar();
    }

    @Override
    public void editar() {
        super.editar();
        inicializarListas();
    }

    private void inicializarListas() {
        bensDisponiveis = Lists.newArrayList();
        bensSelecionados = Lists.newArrayList();
    }

    private void validarRegrasConfiguracaoMovimentacaoBem() {
        recuperarConfiguracaoMovimentacaoBem();
        if (configMovimentacaoBem != null) {
            configMovimentacaoBem.validarDatasMovimentacao(selecionado.getDataDeCriacao(), facade.getSistemaFacade().getDataOperacao(), operacao);
        }
    }

    private void recuperarConfiguracaoMovimentacaoBem() {
        ConfigMovimentacaoBem configMovimentacaoBem = facade.getConfigMovimentacaoBemFacade().buscarConfiguracaoMovimentacaoBem(selecionado.getDataDeCriacao(), OperacaoMovimentacaoBem.SOLICITACAO_TRANSFERENCIA_BEM_ESTORNO);
        if (configMovimentacaoBem != null) {
            this.configMovimentacaoBem = configMovimentacaoBem;
        }
    }

    @Override
    public String getCaminhoPadrao() {
        switch (this.tipoBem) {
            case IMOVEIS:
                return "/solicitacao-estorno-transferencia-imovel/";
            case MOVEIS:
                return "/solicitacao-estorno-transferencia-movel/";
            default:
                return "";
        }
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public void concluirSolicitacao() {
        try {
            validarSalvarAndConcluir();
            novoAssistente();
            futureSalvar = facade.concluirSolicitacao(selecionado, bensSelecionados, assistente);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            descobrirETratarException(ex);
        }
    }

    @Override
    public void salvar() {
        try {
            validarSalvarAndConcluir();
            novoAssistente();
            futureSalvar = facade.salvarSolicitacao(selecionado, bensSelecionados, assistente);
            FacesUtil.executaJavaScript("openDialog(dlgSalvar)");
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (ValidacaoException ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
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

    public void pesquisarBens() {
        try {
            validarPesquisaBem();
            inicializarListas();
            novoAssistente();
            futurePesquisaBens = facade.pesquisarBens(assistente);
            FacesUtil.executaJavaScript("iniciarPesquisa()");
        } catch (ValidacaoException ve) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarSalvarAndConcluir() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (DataUtil.dataSemHorario(selecionado.getDataDeCriacao()).before(DataUtil.dataSemHorario(selecionado.getLoteEfetivacaoTransferencia().getDataEfetivacao()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de solicitação de estorno de transferência deve ser posterior ou igual a data da efetivação de transferência n° " + selecionado.getLoteEfetivacaoTransferencia().getCodigo() + ".");
        }
        if (bensSelecionados.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione ao menos um bem para solicitar o estornar.");
        }
        ve.lancarException();
        validarRegrasConfiguracaoMovimentacaoBem();
    }

    public List<LoteEfetivacaoTransferenciaBem> completaLoteEfetivacao(String filtro) {
        return facade.getLotefacade().completaLoteEfetivacaoPorUsuarioExercicio(filtro.trim(), this.tipoBem);
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
        bensSelecionados.add(bem);
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
            bensDisponiveis.addAll(futurePesquisaBens.get());
            if (isOperacaoEditar()) {
                List<Bem> bensSalvos = facade.pesquisarBensVinculadoAoItemEstornoTrasnferencia(selecionado, assistente);
                bensDisponiveis.addAll(bensSalvos);
                bensSelecionados.addAll(bensSalvos);
                Collections.sort(bensSelecionados);
            }
            Collections.sort(bensDisponiveis);
            futurePesquisaBens = null;
            FacesUtil.executaJavaScript("terminarPesquisa()");
        }
    }

    private void validarPesquisaBem() {
        Util.validarCampos(selecionado);
        validarRegrasConfiguracaoMovimentacaoBem();
    }

    public void buscarBensAoEditar() {
        if (isOperacaoEditar()) {
            novoAssistente();
            pesquisarBens();
        }
    }

    public void finalizarProcesssoSalvar() {
        redirecionarParaVer();
        FacesUtil.executaJavaScript("clearInterval(timerAcompanhaSalvar)");
        if (selecionado.isEmElaboracao()) {
            FacesUtil.addOperacaoRealizada(" Solicitação de estorno de transferência de bens salva com sucesso.");
        } else {
            FacesUtil.addOperacaoRealizada(" Solicitação de estorno de transferência de bens concluída com sucesso.");
        }
    }

    protected void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public boolean hasBensSelecinados() {
        return bensSelecionados != null && !bensSelecionados.isEmpty();
    }

    private void selecionarMesmoBemAoPesquisarNoEditar() {
        if (isOperacaoEditar()) {
            for (Bem bem : facade.pesquisarBensVinculadoAoItemEstornoTrasnferencia(selecionado, assistente)) {
                if (!bensSelecionados.contains(bem)) {
                    bensSelecionados.add(bem);
                }
                if (!bensDisponiveis.contains(bem)) {
                    bensDisponiveis.add(bem);
                }
            }
            Util.ordenarListas(bensDisponiveis);
        }
    }

    public void finalizarPesquisa() {
        FacesUtil.atualizarComponente("Formulario:tabGeral:tabelaBens");
        FacesUtil.atualizarComponente("Formulario:tabGeral:outputPanelInconsistencias");
        FacesUtil.atualizarComponente("Formulario:msgInconsistencias");
        FacesUtil.executaJavaScript("aguarde.hide()");
    }

    private void novoAssistente() {
        assistente = new AssistenteMovimentacaoBens(selecionado.getDataDeCriacao(), operacao);
        assistente.setConfigMovimentacaoBem(configMovimentacaoBem);
        assistente.setSelecionado(selecionado);
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        assistente.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
    }

    public void consultarFutureSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                SolicitacaoEstornoTransferencia solicitacaoSalva = (SolicitacaoEstornoTransferencia) futureSalvar.get();
                if (solicitacaoSalva != null) {
                    selecionado = solicitacaoSalva;
                    finalizarProcesssoSalvar();
                    futureSalvar = null;
                }
            } catch (Exception ex) {
                descobrirETratarException(ex);
            }
        }
    }

    public void gerarRelatorioSolicitacaoEstorno() {
        try {
            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            String arquivoJasper = "RelatorioSolicitacaoEstornoTransferencia.jasper";
            HashMap parameters = Maps.newHashMap();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(facade.buscarRegistrosRelatorioSolicitacaoEstornoBens(selecionado));
            parameters.put("MODULO", "Patrimônio");
            parameters.put("ENTIDADE", facade.getEntidadeFacade().recuperarEntidadePorUnidadeOrganizacional(selecionado.getLoteEfetivacaoTransferencia().getUnidadeOrganizacional()).getNome().toUpperCase());
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("NOMERELATORIO", "RELATÓRIO DE SOLICITAÇÃO DE ESTORNO DE TRANSFERÊNCIA DE BENS " + selecionado.getLoteEfetivacaoTransferencia().getTipoBem().getDescricao().toUpperCase());
            parameters.put("BRASAO", abstractReport.getCaminhoImagem());
            parameters.put("ID", selecionado.getId());
            if (facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameters.put("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getLogin());
            }
            abstractReport.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio: " + ex);
            FacesUtil.addErroAoGerarRelatorio("Não foi possível gerar o relatório ");
        }
    }

    public String getDescricaoSolicitacao() {
        String descricao = facade.getHierarquiaOrganizacionalFacade().getDescricaoHierarquia(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getLoteEfetivacaoTransferencia().getUnidadeOrganizacional(),
            selecionado.getDataDeCriacao());
        return selecionado.getLoteEfetivacaoTransferencia().getCodigo() + " - " + descricao;
    }

    public BigDecimal getValorTotalBens() {
        BigDecimal total = BigDecimal.ZERO;
        if (bensDisponiveis != null && !bensDisponiveis.isEmpty()) {
            for (Bem bem : bensDisponiveis) {
                total = total.add(bem.getValorOriginal());
            }
        }
        return total;
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
