package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoCalculoGeralIssFixo;
import br.com.webpublico.entidades.TipoAutonomo;
import br.com.webpublico.enums.SituacaoProcessoCalculoGeralIssFixo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonLancamentoGeralISSFixo;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 05/07/13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "calculaIssFixoGeralControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-lancamento-iss-fixo-geral", pattern = "/calcula-iss-fixo-geral/novo/", viewId = "/faces/tributario/issqn/calculogeralissfixo/lancamento/edita.xhtml"),
    @URLMapping(id = "editar-lancamento-iss-fixo-geral", pattern = "/calcula-iss-fixo-geral/editar/#{calculaIssFixoGeralControlador.id}/", viewId = "/faces/tributario/issqn/calculogeralissfixo/lancamento/edita.xhtml"),
    @URLMapping(id = "ver-lancamento-iss-fixo-geral", pattern = "/calcula-iss-fixo-geral/ver/#{calculaIssFixoGeralControlador.id}/", viewId = "/faces/tributario/issqn/calculogeralissfixo/lancamento/visualizar.xhtml"),
    @URLMapping(id = "listar-lancamento-iss-fixo-geral", pattern = "/calcula-iss-fixo-geral/listar/", viewId = "/faces/tributario/issqn/calculogeralissfixo/lancamento/lista.xhtml"),
    @URLMapping(id = "log-lancamento-iss-fixo-geral", pattern = "/calcula-iss-fixo-geral/log/", viewId = "/faces/tributario/issqn/calculogeralissfixo/lancamento/log.xhtml"),
    @URLMapping(id = "relatorio-lancamento-iss-fixo-geral", pattern = "/calcula-iss-fixo-geral/relatorio/#{calculaIssFixoGeralControlador.id}/", viewId = "/faces/tributario/issqn/calculogeralissfixo/lancamento/relatorio.xhtml"),
})
public class CalculaIssFixoGeralControlador extends PrettyControlador<ProcessoCalculoGeralIssFixo> implements Serializable, CRUD {

    @EJB
    private CalculaIssFixoGeralFacade calculaIssFixoGeralFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CalculaISSFacade calculaIssFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private ConverterGenerico converterExercicio;
    private ConverterAutoComplete converterTipoAutonomo;
    private ConverterAutoComplete converterProcessoCalculoGeralIssFixo;
    private String[] processoParaExibir;
    private List<String[]> listaMotivosDeLancamentosFalhos;
    private List<String[]> listaInfoLancamentosRealizados;
    private boolean relatorioAgrupado = false;

    public CalculaIssFixoGeralControlador() {
        super(ProcessoCalculoGeralIssFixo.class);
        obterAtributosDaSessao();
    }

    private void obterAtributosDaSessao() {
        SessionData sd = (SessionData) Web.pegaDaSessao(SessionData.class);
        if (sd != null) {
            processoParaExibir = sd.processoParaExibir;
            listaMotivosDeLancamentosFalhos = sd.listaMotivosDeLancamentosFalhos;
            listaInfoLancamentosRealizados = sd.listaInfoLancamentosRealizados;
            selecionado = sd.processo;
        }
    }

    @URLAction(mappingId = "novo-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
    }

    private void inicializarAtributos() {
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        selecionado.setCmcInicial("1");
        selecionado.setCmcFinal("9999999");
        selecionado.setSituacaoProcessoCalculoGeralIssFixo(SituacaoProcessoCalculoGeralIssFixo.SIMULACAO);
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        selecionado.setDataDoLancamento(sistemaFacade.getDataOperacao());
        SingletonLancamentoGeralISSFixo.limparInformacoes();
    }

    @URLAction(mappingId = "ver-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarAtributosParaVisualizacao();
    }

    private void carregarAtributosParaVisualizacao() {
        processoParaExibir = calculaIssFixoGeralFacade.obterProcessoParaExibir(selecionado);
        listaMotivosDeLancamentosFalhos = calculaIssFixoGeralFacade.obterListaMotivosDeLancamentosFalhos(selecionado);
        listaInfoLancamentosRealizados = calculaIssFixoGeralFacade.obterListaInfoDeLancamentosRealizados(selecionado);
    }

    @URLAction(mappingId = "editar-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }


    public void visualizarRelatorio() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "relatorio/" + selecionado.getId() + "/");
    }

    public void efetivar() {
        FacesUtil.redirecionamentoInterno("/efetiva-lancamento-iss-fixo-geral/direto/" + selecionado.getId() + "/");
    }

    @URLAction(mappingId = "relatorio-lancamento-iss-fixo-geral", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verRelatorio() {
        super.ver();
        carregarAtributosParaVisualizacao();
    }

    @Override
    public void salvar() {
        if (selecionado == null) {
            selecionado = (ProcessoCalculoGeralIssFixo) SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getSelecionado();
        }

        if (selecionado.getTipoAutonomo() != null && selecionado.getTipoAutonomo().getId() == null) {
            selecionado.setTipoAutonomo(null);
        }
        selecionado.setTempoDecorrido(SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getDecorrido());
        selecionado = calculaIssFixoGeralFacade.salva(selecionado);
    }

    public void listenerBotao() {
        SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().setExecutando(false);
        salvar();
        visualizarRelatorio();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/calcula-iss-fixo-geral/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return calculaIssFixoGeralFacade;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        for (Exercicio ex : exercicioFacade.listaDecrescente()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }

    public List<TipoAutonomo> completaTipoAutonomo(String parte) {
        List<TipoAutonomo> retorno = tipoAutonomoFacade.completaTipoAutonomo(parte);

        if (retorno.isEmpty()) {
            retorno.add(new TipoAutonomo("Nenhum tipo de autonômo encontrado!"));
        }
        return retorno;
    }

    public Converter getConverterTipoAutonomo() {
        if (converterTipoAutonomo == null) {
            converterTipoAutonomo = new ConverterAutoComplete(TipoAutonomo.class, tipoAutonomoFacade);
        }
        return converterTipoAutonomo;
    }

    public void lancarIssFixoGeral() {
        try {
            podeLancar();
            SingletonLancamentoGeralISSFixo.getInstance().setAssistenteBarraProgresso(new AssistenteBarraProgresso());
            SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().zerarContadoresProcesso();
            SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().setExecutando(true);
            boolean redirecionar = true;

            if (selecionado.getTipoAutonomo() != null && selecionado.getTipoAutonomo().getId() == null) {
                selecionado.setTipoAutonomo(null);
            }

            List<String> inscricoes = cadastroEconomicoFacade.recuperarInscricaoCadastralPorFaixaETipo(selecionado.getCmcInicial(), selecionado.getCmcFinal(), selecionado.getTipoAutonomo());

            if (inscricoes.isEmpty()) {
                FacesUtil.addError("Impossível continuar", "Não foi encontrado nenhum cadastro econômico no intervalo informado.");
                redirecionar = false;
            }

            if (redirecionar) {
                selecionado.setTotalDeInscricoes(inscricoes.size());
                SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().setTotal(inscricoes.size());
                SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
                SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().setDescricaoProcesso("Lançamento Geral de ISS Fixo");
                SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().removerProcessoDoAcompanhamento();

                SingletonLancamentoGeralISSFixo.getInstance().iniciarLancamento();

                realizarLancamentos(inscricoes);

                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "log/");
            }
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getAllMensagens());
        }

    }

    private void podeLancar() {
        ValidacaoException exception = new ValidacaoException();
        try {
            Util.validarCampos(selecionado);
        } catch (ValidacaoException e) {
            exception = e;
        }
        if (!exercicioEhValido()) {
            exception.adicionarMensagemDeOperacaoNaoPermitida("O exercício de lançamento deve ser no máximo um ano posterior ao ano atual.");

        }
        if (exception.temMensagens()) {
            throw exception;
        }
    }

    public Boolean exercicioEhValido() {
        if (selecionado.getExercicio() != null) {
            return selecionado.getExercicio().getAno() <= sistemaFacade.getExercicioCorrente().getAno() + 1;
        }

        return false;
    }

    private void realizarLancamentos(List<String> inscricoes) {
        selecionado.setProcessoCalculoISS(calculaIssFacade.criarProcessoFixo(null, selecionado.getExercicio()));

        salvar();
        SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().setSelecionado(selecionado);

        if (inscricoes.size() > 10) {
            List<List<String>> listaParticionada = particionarEmCinco(inscricoes);
            for (int i = 0; i < listaParticionada.size(); i++) {
                montarFuture(listaParticionada.get(i), " Parte " + (i + 1) + "/5");
            }
        } else {
            montarFuture(inscricoes, "");
        }
    }

    private void montarFuture(List<String> list, String sufixo) {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
            "Lançamento de ISS Fixo geral" + sufixo, 0);
        AsyncExecutor.getInstance().execute(assistente, () -> {
            calculaIssFixoGeralFacade.lancarIssFixoGeral(list, selecionado);
            return null;
        });
    }

    private List<List<String>> particionarEmCinco(List<String> inscricoes) {
        List<List<String>> retorno = new ArrayList<>();
        int parte = inscricoes.size() / 5;
        retorno.add(inscricoes.subList(0, parte));
        retorno.add(inscricoes.subList(parte, parte * 2));
        retorno.add(inscricoes.subList(parte * 2, parte * 3));
        retorno.add(inscricoes.subList(parte * 3, parte * 4));
        retorno.add(inscricoes.subList(parte * 4, inscricoes.size()));

        return retorno;
    }

    public Double getPorcentagemDoCalculo() {
        return SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getPorcentagemDoCalculo();
    }

    public Integer getNumeroLancamentoAtual() {
        return SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getCalculados();
    }

    public Integer getTotalCadastros() {
        return SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getTotal();
    }

    public void abortarLancamento() {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso(sistemaFacade.getUsuarioCorrente(),
            "Abortar Lançamento de ISS Fixo Geral", 0);
        AsyncExecutor.getInstance().execute(assistente, () -> {
            calculaIssFixoGeralFacade.abortarLancamento(selecionado);
            return null;
        });
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    public String getTempoDecorrido() {
        return SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getTempoDecorrido();
    }

    public String getTempoEstimado() {
        return SingletonLancamentoGeralISSFixo.getInstance().getAssistenteBarraProgresso().getTempoEstimado();
    }

    public String getTempoAsString(Long tempo) {
        long HOUR = TimeUnit.HOURS.toMillis(1);

        if (tempo < HOUR) {
            return String.format("%1$TM:%1$TS%n", tempo.longValue());
        } else {
            return String.format("%d:%2$TM:%2$TS%n", tempo.longValue() / HOUR, tempo.longValue() % HOUR);
        }
    }

    public String getTempoDecorridoProcessoVisualizar() {
        if (!processoParaExibir[5].equals("")) {
            return getTempoAsString(Long.valueOf(processoParaExibir[5]));
        }
        return "-";
    }

    public String[] getProcessoParaExibir() {
        return processoParaExibir;
    }

    public List<String[]> getListaMotivosDeLancamentosFalhos() {
        return listaMotivosDeLancamentosFalhos;
    }

    public List<String[]> getListaInfoLancamentosRealizados() {
        return listaInfoLancamentosRealizados;
    }

    public BigDecimal toBigDecimal(String valor) {
        return new BigDecimal(valor);
    }

    private class SessionData {
        public String[] processoParaExibir;
        public List<String[]> listaMotivosDeLancamentosFalhos;
        public List<String[]> listaInfoLancamentosRealizados;
        public ProcessoCalculoGeralIssFixo processo;

        public SessionData(String[] processoParaExibir, List<String[]> listaMotivosDeLancamentosFalhos, List<String[]> listaInfoLancamentosRealizados, ProcessoCalculoGeralIssFixo selecionado) {
            this.processoParaExibir = processoParaExibir;
            this.listaMotivosDeLancamentosFalhos = listaMotivosDeLancamentosFalhos;
            this.listaInfoLancamentosRealizados = listaInfoLancamentosRealizados;
            this.processo = selecionado;
        }
    }

    public boolean isRelatorioAgrupado() {
        return relatorioAgrupado;
    }

    public void setRelatorioAgrupado(boolean relatorioAgrupado) {
        this.relatorioAgrupado = relatorioAgrupado;
    }
}

