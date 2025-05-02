package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ConfiguracaoAgendamentoTarefa;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonLoteEncerramentoMensalServico;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.LoteDeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.dtos.DeclaracaoMensalServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NotaFiscalSearchDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.nfse.facades.LoteDeclaracaoMensalServicoFacade;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by William on 01/02/2018.
 */


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "loteDeclaracaoMensalListar", pattern = "/nfse/lote-declaracao-mensal/listar/",
        viewId = "/faces/tributario/nfse/lotedeclaracaomensal/lista.xhtml"),
    @URLMapping(id = "loteDeclaracaoMensalVer", pattern = "/nfse/lote-declaracao-mensal/ver/#{loteDeclaracaoMensalServicoControlador.id}/",
        viewId = "/faces/tributario/nfse/lotedeclaracaomensal/visualizar.xhtml"),
    @URLMapping(id = "loteDeclaracaoMensalLancar", pattern = "/nfse/lote-declaracao-mensal/novo/",
        viewId = "/faces/tributario/nfse/lotedeclaracaomensal/lancar.xhtml")
})
public class LoteDeclaracaoMensalServicoControlador extends PrettyControlador<LoteDeclaracaoMensalServico> implements CRUD {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    @EJB
    private LoteDeclaracaoMensalServicoFacade loteDeclaracaoMensalServicoFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Future<List<NotaFiscalSearchDTO>> futureBuscaNotasLancamentoMensal;
    private Future<List<DeclaracaoMensalServicoNfseDTO>> futurePreparaDeclaracoesMensais;
    private Future<List<DeclaracaoMensalServico>> futureDeclaracoesMensais;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<NotaFiscalSearchDTO> notasDeclarar;
    private List<NotaFiscalSearchDTO> notasDeclararSelecionadas;
    private FiltroPesquisaLoteDMS filtro;
    private LazyDataModel<LoteDeclaracaoMensalServico> lotes;
    @EJB
    private SingletonLoteEncerramentoMensalServico singletonLoteEncerramentoMensalServico;
    private ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipoTarefaAgendada = ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada.GERAR_DECLARACAO_MENSAL_SERVICO;
    private CompletableFuture futureCancelamento;

    public LoteDeclaracaoMensalServicoControlador() {
        super(LoteDeclaracaoMensalServico.class);
    }

    public ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada getTipoTarefaAgendada() {
        return tipoTarefaAgendada;
    }

    public LazyDataModel<LoteDeclaracaoMensalServico> getLotes() {
        return lotes;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    @Override
    public AbstractFacade getFacede() {
        return loteDeclaracaoMensalServicoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/lote-declaracao-mensal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<NotaFiscalSearchDTO> getNotasDeclarar() {
        return notasDeclarar;
    }

    public void setNotasDeclarar(List<NotaFiscalSearchDTO> notasDeclarar) {
        this.notasDeclarar = notasDeclarar;
    }

    public FiltroPesquisaLoteDMS getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroPesquisaLoteDMS filtro) {
        this.filtro = filtro;
    }

    public void pesquisar() {
        lotes = new LazyEventosDataModel(new ArrayList<LoteDeclaracaoMensalServico>());
    }

    @Override
    @URLAction(mappingId = "loteDeclaracaoMensalVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "loteDeclaracaoMensalListar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        filtro = new FiltroPesquisaLoteDMS();
        lotes = new LazyEventosDataModel(new ArrayList<LoteDeclaracaoMensalServico>());
    }

    @URLAction(mappingId = "loteDeclaracaoMensalLancar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoLancamentoGeral() {
        newSelecionado();
        notasDeclarar = Lists.newArrayList();
        notasDeclararSelecionadas = Lists.newArrayList();
    }

    private void newSelecionado() {
        selecionado = new LoteDeclaracaoMensalServico();
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        selecionado.setCmcInicial("1");
        selecionado.setCmcFinal("9999999");
        selecionado.setMes(Mes.getMesToInt(DataUtil.getMes(new Date())));
        selecionado.setTipoMovimento(TipoMovimentoMensal.NORMAL);
    }

    public AssistenteBarraProgresso andamentoLote(LoteDeclaracaoMensalServico lote) {
        return singletonLoteEncerramentoMensalServico.getAndamento(lote.getId());
    }

    public void pesquisarNotasParaLancamentoGeral() {
        notasDeclarar = Lists.newArrayList();
        notasDeclararSelecionadas = Lists.newArrayList();
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        futureBuscaNotasLancamentoMensal = notaFiscalFacade.buscarNotasParaLancamentoGeralPorFiltro(assistenteBarraProgresso, selecionado);
        FacesUtil.executaJavaScript("iniciarAcompanhamentoConsulta()");
    }

    public void verificiarAndamentoBuscaNotasLancamentoMensal() {
        if (futureBuscaNotasLancamentoMensal != null && futureBuscaNotasLancamentoMensal.isDone()) {
            FacesUtil.executaJavaScript("finalizarAcompanhamentoConsulta()");
        }
    }


    public void iniciarLancamentoDasDeclaracoesMensais() {
        if (!selecionado.getSelecionarNotas()) {
            pesquisarNotasParaLancamentoGeral();
        } else {
            lancarDeclaracoesMensais();
        }
    }

    private void lancarDeclaracoesMensais() {
        try {
            validarLancamentoDeclaracoes();
            List<NotaFiscalSearchDTO> notaFiscalSearchDTOS = notasDeclararSelecionadas;
            assistenteBarraProgresso = new AssistenteBarraProgresso(notaFiscalSearchDTOS.size());
            assistenteBarraProgresso.setDescricaoProcesso(notaFiscalSearchDTOS.size() + " Notas foram encontradas");

            selecionado.setQtdDeclaracoes(0);
            selecionado.setSituacao(LoteDeclaracaoMensalServico.Situacao.ABERTO);
            selecionado.setInicio(new Date());
            selecionado.setReferencia(DataUtil.getPrimeiroDiaMes(selecionado.getExercicio().getAno(), selecionado.getMes().getNumeroMes() + 1));
            selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            selecionado.setTotalServicos(BigDecimal.ZERO);
            selecionado.setTotalIss(BigDecimal.ZERO);

            selecionado = loteDeclaracaoMensalServicoFacade.salvarRetornando(selecionado);

            futurePreparaDeclaracoesMensais = declaracaoMensalServicoFacade.criarDeclaracoesMensalServicoAsync(assistenteBarraProgresso, selecionado, notaFiscalSearchDTOS);
            FacesUtil.executaJavaScript("iniciarLancamentoDasDeclaracoesMensais()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("Erro ao lançar declaracoes ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possivel continuar");
        }
    }

    private void validarLancamentoDeclaracoes() {
        ValidacaoException ve = new ValidacaoException();
        if (notasDeclararSelecionadas == null || notasDeclararSelecionadas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma nota selecionada para o lançamento das declarações.");
        }
        ve.lancarException();
    }


    public void acompanharAndamentoCriarDeclaracoesMensais() {
        if (futurePreparaDeclaracoesMensais != null && futurePreparaDeclaracoesMensais.isDone()) {
            FacesUtil.executaJavaScript("finalizarAndamentoCriarDeclaracoesMensais()");
        }
    }


    public void salvarLancamentoDasDeclaracoesMensais() {
        try {
            List<DeclaracaoMensalServicoNfseDTO> declaracoes = futurePreparaDeclaracoesMensais.get();
            futurePreparaDeclaracoesMensais = null;
            assistenteBarraProgresso = new AssistenteBarraProgresso(declaracoes.size());
            assistenteBarraProgresso.setDescricaoProcesso(declaracoes.size() + " Declarações foram encontradas");
            futureDeclaracoesMensais = declaracaoMensalServicoFacade.declararAsync(assistenteBarraProgresso, selecionado, declaracoes);
            FacesUtil.executaJavaScript("iniciarSalvarDeclaracoesMensais()");
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addOperacaoRealizada("Não foi possivel continuar");
        }
    }

    public void acompanharSalvarLancamentoDasDeclaracoesMensais() throws ExecutionException, InterruptedException {
        if (futureDeclaracoesMensais != null && futureDeclaracoesMensais.isDone()) {
            FacesUtil.executaJavaScript("finalizarTudo()");
        }
    }

    public void finalizarSalvarLancamentoDasDeclaracoesMensais() {
        FacesUtil.addOperacaoRealizada("Pronto, As declarações foram salvas com sucesso.");
        FacesUtil.redirecionamentoInterno("/nfse/lote-declaracao-mensal/ver/" + selecionado.getId() + "/");
    }

    public List<SelectItem> getTiposMovimento() {
        return Util.getListSelectItem(Lists.newArrayList(TipoMovimentoMensal.NORMAL, TipoMovimentoMensal.RETENCAO, TipoMovimentoMensal.ISS_RETIDO));
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public void finalizarConsultaNotas() throws ExecutionException, InterruptedException {
        notasDeclarar = futureBuscaNotasLancamentoMensal.get();
        futureBuscaNotasLancamentoMensal = null;
        if (notasDeclarar == null || notasDeclarar.isEmpty()) {
            FacesUtil.addWarn("Atenção!", "Nenhuma nota encontrada para os filtros selecionado.");
            FacesUtil.executaJavaScript("fecharDialog()");
            return;
        } else {
            notasDeclararSelecionadas.addAll(notasDeclarar);
        }
        if (!selecionado.getSelecionarNotas()) {
            lancarDeclaracoesMensais();
        } else {
            FacesUtil.executaJavaScript("fecharDialog()");
        }
    }

    public void adicionarNota(NotaFiscalSearchDTO nota) {
        notasDeclararSelecionadas.add(nota);
    }

    public void retirarNota(NotaFiscalSearchDTO nota) {
        notasDeclararSelecionadas.remove(nota);
    }

    public boolean notaAdicionada(NotaFiscalSearchDTO nota) {
        for (NotaFiscalSearchDTO notaSelecionada : notasDeclararSelecionadas) {
            if (notaSelecionada.getId().equals(nota.getId()))
                return true;
        }
        return false;
    }

    public void handleSelecionarNotas() {
        notasDeclarar = null;
    }


    public class LazyEventosDataModel extends LazyDataModel<LoteDeclaracaoMensalServico> {

        private List<LoteDeclaracaoMensalServico> datasource;

        public LazyEventosDataModel(List<LoteDeclaracaoMensalServico> datasource) {
            this.datasource = datasource;
        }

        @Override
        public LoteDeclaracaoMensalServico getRowData(String rowKey) {
            for (LoteDeclaracaoMensalServico evento : datasource) {
                if (evento.toString().equals(rowKey)) {
                    return evento;
                }
            }
            return null;
        }

        @Override
        public Object getRowKey(LoteDeclaracaoMensalServico evento) {
            return evento;
        }

        @Override
        public List<LoteDeclaracaoMensalServico> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            List<LoteDeclaracaoMensalServico> data = loteDeclaracaoMensalServicoFacade.buscarLotes(filtro, first, pageSize);
            Integer dataSize = loteDeclaracaoMensalServicoFacade.contarLotes(filtro);
            this.setRowCount(dataSize);
            return data;
        }
    }

    public static class FiltroPesquisaLoteDMS {
        private Exercicio exercicio;
        private TipoMovimentoMensal tipoMovimento;
        private Mes competenciaInicial;
        private Mes competenciaFinal;
        private String cmcInicial;
        private String cmcFinal;

        public FiltroPesquisaLoteDMS() {
        }

        public Exercicio getExercicio() {
            return exercicio;
        }

        public void setExercicio(Exercicio exercicio) {
            this.exercicio = exercicio;
        }

        public TipoMovimentoMensal getTipoMovimento() {
            return tipoMovimento;
        }

        public void setTipoMovimento(TipoMovimentoMensal tipoMovimento) {
            this.tipoMovimento = tipoMovimento;
        }

        public Mes getCompetenciaInicial() {
            return competenciaInicial;
        }

        public void setCompetenciaInicial(Mes competenciaInicial) {
            this.competenciaInicial = competenciaInicial;
        }

        public Mes getCompetenciaFinal() {
            return competenciaFinal;
        }

        public void setCompetenciaFinal(Mes competenciaFinal) {
            this.competenciaFinal = competenciaFinal;
        }

        public String getCmcInicial() {
            return cmcInicial;
        }

        public void setCmcInicial(String cmcInicial) {
            this.cmcInicial = cmcInicial;
        }

        public String getCmcFinal() {
            return cmcFinal;
        }

        public void setCmcFinal(String cmcFinal) {
            this.cmcFinal = cmcFinal;
        }
    }

    public void cancelarLoteEncerramento() {
        try {
            validarCancelamentoLote();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            assistenteBarraProgresso.setDataAtual(sistemaFacade.getDataOperacao());
            futureCancelamento = AsyncExecutor.getInstance().execute(assistenteBarraProgresso, () -> {
                loteDeclaracaoMensalServicoFacade.  cancelarLoteEncerramento(assistenteBarraProgresso, selecionado);
                return null;
            });
            FacesUtil.executaJavaScript("pollCancelamento.start()");
            FacesUtil.executaJavaScript("openDialog(dialogAcompanhamento)");
            FacesUtil.atualizarComponente("formularioAcompanhamento");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("Erro ao cancelar o lote de encerramento mensal [{}]. {}", selecionado.getId(), e.getMessage());
            logger.debug("Detalhes do erro ao cancelar o lote de encerramento mensal [{}].", selecionado.getId(), e);
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarCancelamentoLote() {
        if (loteDeclaracaoMensalServicoFacade.hasDebitoDiferenteEmAberto(selecionado)) {
            throw new ValidacaoException("Existem encerramentos mensais de serviço, cujo seus débitos não estão mais com a situação 'Em Aberto'. " +
                "Por esse motivo não poderá ser feito o cancelamento.");
        }
    }

    public void acompanharCancelamento() {
        if (futureCancelamento.isDone()) {
            FacesUtil.executaJavaScript("pollCancelamento.stop()");
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
            FacesUtil.addOperacaoRealizada("Cancelamento do lote de encerramento mensal de serviço efetuado com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else if (futureCancelamento.isCancelled()) {
            FacesUtil.executaJavaScript("pollCancelamento.stop()");
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
            FacesUtil.addOperacaoNaoRealizada("Erro inesperado no cancelamento do lote de encerramento mensal de serviço!");
        }
    }
}
