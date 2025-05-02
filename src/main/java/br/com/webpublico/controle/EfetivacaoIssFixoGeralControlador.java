package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoProcessoCalculoGeralIssFixo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EfetivacaoIssFixoGeralFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonEfetivacaoIssFixoGeral;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.itextpdf.io.IOException;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static br.com.webpublico.controle.Web.pegaDaSessao;
import static br.com.webpublico.controle.Web.poeNaSessao;

@ManagedBean(name = "efetivacaoIssFixoGeralControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-efetivacao-lancamento-iss-fixo-geral", pattern = "/efetiva-lancamento-iss-fixo-geral/novo/", viewId = "/faces/tributario/issqn/calculogeralissfixo/efetivacao/edita.xhtml"),
    @URLMapping(id = "efetivacao-por-lancamento-iss-fixo-geral", pattern = "/efetiva-lancamento-iss-fixo-geral/direto/#{efetivacaoIssFixoGeralControlador.id}/", viewId = "/faces/tributario/issqn/calculogeralissfixo/efetivacao/edita.xhtml"),
    @URLMapping(id = "editar-efetivacao-lancamento-iss-fixo-geral", pattern = "/efetiva-lancamento-iss-fixo-geral/editar/#{efetivacaoIssFixoGeralControlador.id}/", viewId = "/faces/tributario/issqn/calculogeralissfixo/efetivacao/edita.xhtml"),
    @URLMapping(id = "ver-efetivacao-lancamento-iss-fixo-geral", pattern = "/efetiva-lancamento-iss-fixo-geral/ver/#{efetivacaoIssFixoGeralControlador.id}/", viewId = "/faces/tributario/issqn/calculogeralissfixo/efetivacao/visualizar.xhtml"),
    @URLMapping(id = "listar-efetivacao-lancamento-iss-fixo-geral", pattern = "/efetiva-lancamento-iss-fixo-geral/listar/", viewId = "/faces/tributario/issqn/calculogeralissfixo/efetivacao/lista.xhtml"),
    @URLMapping(id = "log-efetivacao-lancamento-iss-fixo-geral", pattern = "/efetiva-lancamento-iss-fixo-geral/log/", viewId = "/faces/tributario/issqn/calculogeralissfixo/efetivacao/log.xhtml"),
    @URLMapping(id = "relatorio-efetivacao-lancamento-iss-fixo-geral", pattern = "/efetiva-lancamento-iss-fixo-geral/relatorio/", viewId = "/faces/tributario/issqn/calculogeralissfixo/efetivacao/relatorio.xhtml")
})
public class EfetivacaoIssFixoGeralControlador extends PrettyControlador<EfetivacaoProcessoIssFixoGeral> implements Serializable, CRUD {
    @EJB
    private EfetivacaoIssFixoGeralFacade efetivacaoIssFixoGeralFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<ProcessoCalculoGeralIssFixo> processosFiltrados;
    private List<ProcessoCalculoGeralIssFixo> processosEfetivacaoGeralSelecionadas;
    private List<String[]> listaDeInformacoesDeLancamentosNaoEfetivados;
    private List<String[]> listaDeInformacoesDeLancamentosEfetivados;
    private Map<ProcessoCalculoGeralIssFixo, List<String[]>> mapaDosNaoEfetivados;
    private Map<ProcessoCalculoGeralIssFixo, List<String[]>> mapaDosEfetivados;
    private List<ProcessoCalculoGeralIssFixo> processosVisualizar;
    private ProcessoCalculoGeralIssFixo processoSelecionado;
    private Filtro filtro;
    private EfetivacaoIssFixoGeral efetivacaoGeralSelecionada;
    private String msgAndamento;
    private List<CompletableFuture<EfetivacaoIssFixoGeral>> future;
    private List<BigDecimal> idsCalculos;

    public EfetivacaoIssFixoGeralControlador() {
        super(EfetivacaoProcessoIssFixoGeral.class);
    }

    @URLAction(mappingId = "nova-efetivacao-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        efetivacaoGeralSelecionada = (EfetivacaoIssFixoGeral) Web.pegaDaSessao(EfetivacaoIssFixoGeral.class);

        if (efetivacaoGeralSelecionada == null) {
            efetivacaoGeralSelecionada = new EfetivacaoIssFixoGeral();
            super.novo();
            processosEfetivacaoGeralSelecionadas = new ArrayList<>();
            processosFiltrados = new ArrayList<>();
            SingletonEfetivacaoIssFixoGeral.getInstance().limparDependencias();
            filtro = new Filtro();
            efetivacaoGeralSelecionada.setDataDaEfetivacao(sistemaFacade.getDataOperacao());
            efetivacaoGeralSelecionada.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        } else {
            filtro = new Filtro();
            efetivacaoGeralSelecionada = efetivacaoIssFixoGeralFacade.recarregar(efetivacaoGeralSelecionada);
            filtrar();
            processosEfetivacaoGeralSelecionadas = new ArrayList<>();
            SingletonEfetivacaoIssFixoGeral.getInstance().limparDependencias();
            if (processosFiltrados != null) {
                for (EfetivacaoProcessoIssFixoGeral p : efetivacaoGeralSelecionada.getListaDeProcessos()) {
                    processosEfetivacaoGeralSelecionadas.add(p.getProcesso());
                    processosFiltrados.add(p.getProcesso());
                }
            }
        }
    }

    @URLAction(mappingId = "efetivacao-por-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPorLancamento() {
        efetivacaoGeralSelecionada = new EfetivacaoIssFixoGeral();
        super.novo();
        processosEfetivacaoGeralSelecionadas = new ArrayList<>();
        processosFiltrados = new ArrayList<>();
        SingletonEfetivacaoIssFixoGeral.getInstance().limparDependencias();
        filtro = new Filtro();
        efetivacaoGeralSelecionada.setDataDaEfetivacao(sistemaFacade.getDataOperacao());
        efetivacaoGeralSelecionada.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        processosEfetivacaoGeralSelecionadas.add(efetivacaoIssFixoGeralFacade.obterProcessosFiltrados(getId()));
        efetivarEfetivacoesSelecionadas();

    }

    @URLAction(mappingId = "log-efetivacao-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() {
        future = (List<CompletableFuture<EfetivacaoIssFixoGeral>>) Web.pegaDaSessao("FutureEfetivacaoISS");
        SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().setExecutando(false);
        efetivacaoGeralSelecionada = (EfetivacaoIssFixoGeral) Web.pegaDaSessao(EfetivacaoIssFixoGeral.class);
    }


    @URLAction(mappingId = "ver-efetivacao-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        listaDeInformacoesDeLancamentosNaoEfetivados = efetivacaoIssFixoGeralFacade.obterInformacoesDosLancamentosNaoEfetivados(selecionado.getProcesso());
        listaDeInformacoesDeLancamentosEfetivados = efetivacaoIssFixoGeralFacade.obterInformacoesDosLancamentosEfetivados(selecionado.getProcesso());
    }

    @URLAction(mappingId = "editar-efetivacao-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/efetiva-lancamento-iss-fixo-geral/";
    }

    @Override
    public Object getUrlKeyValue() {
        return efetivacaoGeralSelecionada.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return efetivacaoIssFixoGeralFacade;
    }

    public String getMsgAndamento() {
        return msgAndamento;
    }

    public List<ProcessoCalculoGeralIssFixo> getProcessosFiltrados() {
        if (processosFiltrados == null) {
            filtrar();
        }

        return processosFiltrados;
    }

    public void setProcessosFiltrados(List<ProcessoCalculoGeralIssFixo> processosFiltrados) {
        this.processosFiltrados = processosFiltrados;
    }

    public List<ProcessoCalculoGeralIssFixo> getProcessosEfetivacaoGeralSelecionadas() {
        return processosEfetivacaoGeralSelecionadas;
    }

    public void setProcessosEfetivacaoGeralSelecionadas(List<ProcessoCalculoGeralIssFixo> processosEfetivacaoGeralSelecionadas) {
        this.processosEfetivacaoGeralSelecionadas = processosEfetivacaoGeralSelecionadas;
    }

    public void filtrar() {
        processosFiltrados = efetivacaoIssFixoGeralFacade.obterProcessosFiltrados(filtro.getExercicio(), filtro.getDataLancamentoInicial(), filtro.getDataLancamentoFinal());
    }

    private void inicializaProcessosefetivacaoGeralSelecionadas() {
        if (processosEfetivacaoGeralSelecionadas == null) {
            processosEfetivacaoGeralSelecionadas = new ArrayList<ProcessoCalculoGeralIssFixo>();
        }
    }

    public void selecionarLancamento(ProcessoCalculoGeralIssFixo processo) {
        inicializaProcessosefetivacaoGeralSelecionadas();
        processosEfetivacaoGeralSelecionadas.add(processo);
    }

    public void removerLancamento(ProcessoCalculoGeralIssFixo processo) {
        inicializaProcessosefetivacaoGeralSelecionadas();
        processosEfetivacaoGeralSelecionadas.remove(processo);
    }

    public boolean efetivacaoGeralSelecionada(ProcessoCalculoGeralIssFixo processo) {
        if (processo == null)
            return false;

        inicializaProcessosefetivacaoGeralSelecionadas();
        return processosEfetivacaoGeralSelecionadas.contains(processo);
    }

    public void efetivarEfetivacoesSelecionadas() {
        try {
            validarEfetivacao();

            SingletonEfetivacaoIssFixoGeral.getInstance().setAssistenteBarraProgresso(new AssistenteBarraProgresso());
            AssistenteBarraProgresso assistente = SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso();
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Efetivação de Lançamento Geral de ISS Fixo");
            assistente.setExecutando(true);
            assistente.setTotal(idsCalculos.size());
            assistente.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            assistente.removerProcessoDoAcompanhamento();
            SingletonEfetivacaoIssFixoGeral.getInstance().setListacalculosParaEfetivar(idsCalculos);

            for (ProcessoCalculoGeralIssFixo processo : processosEfetivacaoGeralSelecionadas) {
                efetivacaoGeralSelecionada.getListaDeProcessos().add(new EfetivacaoProcessoIssFixoGeral(efetivacaoGeralSelecionada, processo));
            }
            efetivacaoGeralSelecionada = efetivacaoIssFixoGeralFacade.salvarEfetivacao(efetivacaoGeralSelecionada);
            assistente.setSelecionado(efetivacaoGeralSelecionada);

            future = Lists.newArrayList();
            if (SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().getTotal() > 5) {
                List<List<BigDecimal>> listasParticionadas = Lists.partition(SingletonEfetivacaoIssFixoGeral.getInstance().getListacalculosParaEfetivar(),
                    SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().getTotal() / 5);
                for (int i = 0; i < listasParticionadas.size(); i++) {
                    future.add(montarFuture(listasParticionadas.get(i), " Parte " + (i + 1) + "/5"));
                }
            } else {
                future.add(montarFuture(SingletonEfetivacaoIssFixoGeral.getInstance().getListacalculosParaEfetivar(), ""));
            }
            Web.poeNaSessao("FutureEfetivacaoISS", future);
            Web.poeNaSessao(efetivacaoGeralSelecionada);
            for (EfetivacaoProcessoIssFixoGeral efetivacaoProcesso : efetivacaoGeralSelecionada.getListaDeProcessos()) {
                ProcessoCalculoGeralIssFixo processo = efetivacaoIssFixoGeralFacade.getCalculaIssFixoGeralFacade().recuperar(efetivacaoProcesso.getProcesso().getId());
                processo.setSituacaoProcessoCalculoGeralIssFixo(SituacaoProcessoCalculoGeralIssFixo.EFETIVADO);
                efetivacaoIssFixoGeralFacade.getCalculaIssFixoGeralFacade().salvar(processo);
            }
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "log/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao efetivar lançamentos. Detalhes: " + e.getMessage());
            logger.error("Erro ao efetivar lancamentos. ", e);
        }
    }

    private CompletableFuture<EfetivacaoIssFixoGeral> montarFuture(List<BigDecimal> list, String sufixo) {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
            "Efetivação de Lançamento Geral de ISS Fixo" + sufixo, 0);
        return AsyncExecutor.getInstance().execute(assistente,
            () -> {
                try {
                    return efetivacaoIssFixoGeralFacade.efetivarProcessos(efetivacaoGeralSelecionada, list);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    private void validarEfetivacao() {
        ValidacaoException ve = new ValidacaoException();
        if (processosEfetivacaoGeralSelecionadas == null || processosEfetivacaoGeralSelecionadas.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foram encontrados lançamentos para efetivar.");
        } else {
            idsCalculos = efetivacaoIssFixoGeralFacade.buscarIdsDeCalculosParaEfetivar(processosEfetivacaoGeralSelecionadas);
            if (idsCalculos == null || idsCalculos.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Os lançamentos selecionados não possuem cálculos para serem efetivados.");
            }
        }
        ve.lancarException();
    }

    private List<List<EfetivacaoProcessoIssFixoGeral>> particionarEmDuas(List<EfetivacaoProcessoIssFixoGeral> processos) {
        List<List<EfetivacaoProcessoIssFixoGeral>> retorno = new ArrayList<>();

        int parte = processos.size() / 2;
        retorno.add(processos.subList(0, parte));
        retorno.add(processos.subList(parte, processos.size()));

        return retorno;
    }

    public boolean mostraBotaoSelecionarTodos() {
        for (ProcessoCalculoGeralIssFixo processoFiltrado : processosFiltrados) {
            if (!processosEfetivacaoGeralSelecionadas.contains(processoFiltrado)) {
                return true;
            }
        }

        return false;
    }

    public void selecionarTodos() {
        processosEfetivacaoGeralSelecionadas.clear();
        processosEfetivacaoGeralSelecionadas.addAll(processosFiltrados);
    }

    public void desmarcarTodos() {
        processosEfetivacaoGeralSelecionadas.clear();
    }

    public List<String[]> getListaDeInformacoesDeLancamentosNaoEfetivados() {
        return listaDeInformacoesDeLancamentosNaoEfetivados;
    }

    public void setListaDeInformacoesDeLancamentosNaoEfetivados(List<String[]> listaDeInformacoesDeLancamentosNaoEfetivados) {
        this.listaDeInformacoesDeLancamentosNaoEfetivados = listaDeInformacoesDeLancamentosNaoEfetivados;
    }

    public List<String[]> getListaDeInformacoesDeLancamentosEfetivados() {
        return listaDeInformacoesDeLancamentosEfetivados;
    }

    public void setListaDeInformacoesDeLancamentosEfetivados(List<String[]> listaDeInformacoesDeLancamentosEfetivados) {
        this.listaDeInformacoesDeLancamentosEfetivados = listaDeInformacoesDeLancamentosEfetivados;
    }

    public List<ProcessoCalculoGeralIssFixo> getProcessosVisualizar() {
        return processosVisualizar;
    }

    public void setProcessosVisualizar(List<ProcessoCalculoGeralIssFixo> processosVisualizar) {
        this.processosVisualizar = processosVisualizar;
    }

    public ProcessoCalculoGeralIssFixo getProcessoSelecionado() {
        return processoSelecionado;
    }

    public void setProcessoSelecionado(ProcessoCalculoGeralIssFixo processoSelecionado) {
        this.processoSelecionado = processoSelecionado;
    }

    public void visualizarDetalhesDoProcesso(SelectEvent event) {
        this.processoSelecionado = (ProcessoCalculoGeralIssFixo) event.getObject();
        listaDeInformacoesDeLancamentosEfetivados = obterCalculosEfetivados(processoSelecionado);
        listaDeInformacoesDeLancamentosNaoEfetivados = obterCalculosNaoEfetivados(processoSelecionado);
    }

    private List<String[]> obterCalculosEfetivados(ProcessoCalculoGeralIssFixo p) {
        if (mapaDosEfetivados == null) {
            mapaDosEfetivados = new HashMap<>();
        }

        List<String[]> toReturn = mapaDosEfetivados.get(p);

        if (toReturn == null) {
            mapaDosEfetivados.put(p, efetivacaoIssFixoGeralFacade.obterInformacoesDosLancamentosEfetivados(p));
        }

        return mapaDosEfetivados.get(p);
    }

    private List<String[]> obterCalculosNaoEfetivados(ProcessoCalculoGeralIssFixo p) {
        if (mapaDosNaoEfetivados == null) {
            mapaDosNaoEfetivados = new HashMap<>();
        }

        List<String[]> toReturn = mapaDosNaoEfetivados.get(p);

        if (toReturn == null) {
            mapaDosNaoEfetivados.put(p, efetivacaoIssFixoGeralFacade.obterInformacoesDosLancamentosNaoEfetivados(p));
        }

        return mapaDosNaoEfetivados.get(p);
    }

    public double getPorcentagemDoCalculo() {
        double porcentagemDoCalculo = SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().getPorcentagemDoCalculo();
        if (porcentagemDoCalculo <= 0) {
            msgAndamento = "Preparando rotinas para iniciar o processo";
        } else if (porcentagemDoCalculo > 0 && porcentagemDoCalculo <= 95) {
            msgAndamento = "Processando calculo de ISS e gerando os débitos";
        } else {
            msgAndamento = "Finalizando processo e salvando os débitos";
        }
        return porcentagemDoCalculo;
    }


    public void consultaAndamento() {
        if (future != null && !future.isEmpty()) {
            for (Future<EfetivacaoIssFixoGeral> fut : future) {
                if (!fut.isDone()) {
                    return;
                }
            }
            for (OcorrenciaEfetivacaoIssFixoGeral ocorrencia : SingletonEfetivacaoIssFixoGeral.getInstance().getOcorrencias()) {
                ocorrencia.setEfetivacaoProcesso(efetivacaoGeralSelecionada.getListaDeProcessos().get(0));
            }
            efetivacaoIssFixoGeralFacade.salvarOcorrencias(SingletonEfetivacaoIssFixoGeral.getInstance().getOcorrencias());
            future = null;
            FacesUtil.executaJavaScript("finalizaProcesso()");
        }
    }

    public int numeroLancamentoAtual() {
        return SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().getCalculados();
    }

    public int totalCalculos() {
        return SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().getTotal();
    }

    public void irParaRelatorioFinal() {
        efetivacaoGeralSelecionada = efetivacaoIssFixoGeralFacade.recarregar((EfetivacaoIssFixoGeral) SingletonEfetivacaoIssFixoGeral.getInstance().
            getAssistenteBarraProgresso().getSelecionado());
        processosVisualizar = efetivacaoIssFixoGeralFacade.obterProcessosEfetivados(efetivacaoGeralSelecionada);
        poeNaSessao(processosVisualizar);
        poeNaSessao(efetivacaoGeralSelecionada);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "relatorio/");
    }

    @URLAction(mappingId = "relatorio-efetivacao-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void visualizarRelatorio() {
        efetivacaoGeralSelecionada = (EfetivacaoIssFixoGeral) pegaDaSessao(EfetivacaoIssFixoGeral.class);
        processosVisualizar = (List<ProcessoCalculoGeralIssFixo>) pegaDaSessao(new ArrayList<ProcessoCalculoGeralIssFixo>().getClass());
        listaDeInformacoesDeLancamentosNaoEfetivados = Lists.newArrayList();
        listaDeInformacoesDeLancamentosEfetivados = Lists.newArrayList();
        for (EfetivacaoProcessoIssFixoGeral efetivacaoProcessoIssFixoGeral : efetivacaoGeralSelecionada.getListaDeProcessos()) {
            listaDeInformacoesDeLancamentosNaoEfetivados.addAll(efetivacaoIssFixoGeralFacade.obterInformacoesDosLancamentosNaoEfetivados(efetivacaoProcessoIssFixoGeral.getProcesso()));
            listaDeInformacoesDeLancamentosEfetivados.addAll(efetivacaoIssFixoGeralFacade.obterInformacoesDosLancamentosEfetivados(efetivacaoProcessoIssFixoGeral.getProcesso()));
        }
    }


    public void concluir() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public boolean mostraBotaoEfetivar() {
        return efetivacaoGeralSelecionada.getId() == null;
    }

    public String getTempoDecorrido() {
        return SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().getTempoDecorrido();
    }

    public String getTempoEstimado() {
        return SingletonEfetivacaoIssFixoGeral.getInstance().getAssistenteBarraProgresso().getTempoEstimado();
    }

    public Filtro getFiltro() {
        return filtro;
    }

    public void setFiltro(Filtro filtro) {
        this.filtro = filtro;
    }

    public class Filtro {

        private Exercicio exercicio;
        private Date dataLancamentoInicial;
        private Date dataLancamentoFinal;

        public Exercicio getExercicio() {
            return exercicio;
        }

        public void setExercicio(Exercicio exercicio) {
            this.exercicio = exercicio;
        }

        public Date getDataLancamentoInicial() {
            return dataLancamentoInicial;
        }

        public void setDataLancamentoInicial(Date dataLancamentoInicial) {
            this.dataLancamentoInicial = dataLancamentoInicial;
        }

        public Date getDataLancamentoFinal() {
            return dataLancamentoFinal;
        }

        public void setDataLancamentoFinal(Date dataLancamentoFinal) {
            this.dataLancamentoFinal = dataLancamentoFinal;
        }
    }
}
