package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.TipoAutonomo;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.AbstractVOConsultaLancamento;
import br.com.webpublico.entidadesauxiliares.AssistenteRelacaoLancamentoTributario;
import br.com.webpublico.entidadesauxiliares.RelatorioRelacaoLancamentoTributario;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoCobrancaTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.singletons.SingletonRelatorioRelacaoLancamentoTributario;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.slf4j.Logger;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Wellington on 24/11/2015.
 */
public abstract class AbstractRelacaoLancamentoTributarioControlador<T extends AbstractVOConsultaLancamento> implements Serializable {

    protected Long relatorioCriadoEm;
    @EJB
    protected SingletonRelatorioRelacaoLancamentoTributario singletonRelatorioRelacaoLancamentoTributario;
    @EJB
    protected NaturezaJuridicaFacade naturezaJuridicaFacade;
    @EJB
    protected TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    protected DividaFacade dividaFacade;
    @EJB
    protected ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    protected Future<List<T>> futureDadosRelatorio;
    protected List<Future<List<T>>> futuresDadosRelatorioCalculados;
    protected List<Future<JasperPrint>> futureJaspers;
    private boolean futureDadosRelatorioConcluida;
    private Future<ByteArrayOutputStream> futureBytesRelatorio;
    private boolean futureBytesRelatorioConcluida;
    private boolean iniciandoGeracaoPdf;
    private boolean futuresDadosRelatorioCalculadosConcluidas;
    private boolean jaspersGerados;
    private List<SelectItem> grausDeRisco;
    private List<SelectItem> tiposAutonomos;
    private List<SelectItem> dividasEconomico;
    private List<SelectItem> tiposCobranca;
    private List<SelectItem> dividasParcelamento;
    private List<SelectItem> dividasDividasDiversas;
    private List<SelectItem> dividasTaxasDiversas;
    private List<SelectItem> dividasISSQNAndNFSE;
    private List<SelectItem> dividasDividaAtiva;
    private List<SelectItem> dividasFiscalizacaoISSQN;
    private List<SelectItem> dividasFiscalizacaoSecretaria;
    private List<SelectItem> dividas;
    private long tempoInicioCalculo;
    private boolean gerandoBytes;
    private AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario;
    private AbstractFiltroRelacaoLancamentoDebito filtros;

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    protected abstract Logger getLogger();

    public abstract AbstractFiltroRelacaoLancamentoDebito getFiltroLancamento();

    protected abstract AbstractRelacaoLancamentoTributarioFacade getFacade();

    protected abstract Comparator getComparator();

    protected abstract String getNomeRelatorio();

    protected abstract RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario getRelacaoLancamentoTributario();

    protected abstract void instanciarFiltroLancamento();

    public void novo() {
        inicializarVariaveisDeControle();
        instanciarFiltroLancamento();
    }

    public void inicializarVariaveisDeControle() {
        futureDadosRelatorio = null;
        futureDadosRelatorioConcluida = false;
        futureBytesRelatorio = null;
        futureBytesRelatorioConcluida = false;
        iniciandoGeracaoPdf = false;
        futuresDadosRelatorioCalculados = null;
        futuresDadosRelatorioCalculadosConcluidas = false;
    }

    public void iniciarConsulta() {
        try {
            validarFiltrosUtilizados();

            inicializarVariaveisDeControle();

            futureDadosRelatorio = (Future<List<T>>) getFacade().consultarLancamentos(getFiltroLancamento());

            FacesUtil.executaJavaScript("acompanhaConsulta();");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private void validarFiltrosUtilizados() {
        ValidacaoException ve = new ValidacaoException();
        AbstractFiltroRelacaoLancamentoDebito filtroLancamento = getFiltroLancamento();
        if (filtroLancamento.getExercicioDividaInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Exercício Inicial!");
        }
        if (filtroLancamento.getExercicioDividaFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar o Exercício Final!");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataLancamentoInicial(),
            filtroLancamento.getDataLancamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Lançamento Inicial deve ser menor ou igual a Data de Lançamento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataVencimentoInicial(),
            filtroLancamento.getDataVencimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Vencimento Inicial deve ser menor ou igual a Data de Vencimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataMovimentoInicial(),
            filtroLancamento.getDataMovimentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Movimento Inicial deve ser menor ou igual a Data de Movimento Final");
        }
        if (DataUtil.verificaDataFinalMaiorQueDataInicial(filtroLancamento.getDataPagamentoInicial(),
            filtroLancamento.getDataPagamentoFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Pagamento Inicial deve ser menor ou igual a Data de Pagamento Final");
        }
        if (filtroLancamento.getExercicioInicial() != null && filtroLancamento.getExercicioFinal() != null) {
            if (filtroLancamento.getExercicioInicial().getAno() > filtroLancamento.getExercicioFinal().getAno()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Ano do Processo de Débito Inícial deve ser menor ou igual ao Ano do Processo de Débito Final.");
            }
        }
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        if (configuracaoTributario.getTributoHabitese() == null) {
            if (filtroLancamento.isEmitirHabitese()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado tributo de habite-se configurado para o IPTU nas configurações do tributário.");
            }
        }
        ve.lancarException();
    }

    public void iniciarCalculoDosValores() throws ExecutionException, InterruptedException {
        List<T> dados = futureDadosRelatorio.get();

        if (dados == null || dados.size() == 0) {
            FacesUtil.addWarn(SummaryMessages.ATENCAO.getDescricao(),
                "Não foram encontrados resultados para a pesquisa realizada! Filtros Utilizados: " + getFiltroLancamento().getFiltro().toString());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } else {
            futuresDadosRelatorioCalculados = Lists.newArrayList();

            getLogger().debug("Quantidade total de registro encontrados: " + dados.size());
            int sublistSize = dados.size();
            if (dados.size() >= 4) {
                sublistSize = dados.size() / 4;
            }
            for (List<T> parte : Lists.partition(dados, sublistSize)) {
                futuresDadosRelatorioCalculados.add((Future<List<T>>) getFacade().calcularAcrescimo(parte, getFiltroLancamento().getDataPagamentoInicial(),
                    getFiltroLancamento().getDataPagamentoFinal()));
            }
            FacesUtil.executaJavaScript("acompanhaCalculo()");
        }
    }

    public AssistenteRelacaoLancamentoTributario criarAssistenteRelacaoLancamentoTributario(AbstractFiltroRelacaoLancamentoDebito filtro) throws ExecutionException, InterruptedException {
        AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario = new AssistenteRelacaoLancamentoTributario();
        List<T> voConsultaAtualizada = popularResultados(assistenteRelacaoLancamentoTributario);
        Collections.sort(voConsultaAtualizada, getComparator());
        ajustarOrdemParcelasCompensadasCanceladasParcelamento(voConsultaAtualizada);
        assistenteRelacaoLancamentoTributario.setResultadoPorSituacao(getFacade().agruparPorSituacao(assistenteRelacaoLancamentoTributario.getResultado(), filtro));
        assistenteRelacaoLancamentoTributario.setSomenteTotalizador(filtro.getSomenteTotalizador());
        return assistenteRelacaoLancamentoTributario;
    }

    protected List<T> popularResultados(AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario) throws InterruptedException, ExecutionException {
        List<T> voConsultaAtualizada = Lists.newArrayList();
        for (Future<List<T>> futureVoConsultaCalculado : futuresDadosRelatorioCalculados) {
            voConsultaAtualizada.addAll(futureVoConsultaCalculado.get());
        }
        assistenteRelacaoLancamentoTributario.setResultado(voConsultaAtualizada);
        return voConsultaAtualizada;
    }

    protected void ajustarOrdemParcelasCompensadasCanceladasParcelamento(List<T> voConsultaAtualizada) {
        List<T> parcelasParaOrdenar = Lists.newArrayList();
        for (T abstractVOConsultaLancamento : voConsultaAtualizada) {
            if (abstractVOConsultaLancamento.getResultadoParcela().getTipoCalculoEnumValue().isDependenteDeOutroValorDivida()) {
                parcelasParaOrdenar.add(abstractVOConsultaLancamento);
            }
        }
        voConsultaAtualizada.removeAll(parcelasParaOrdenar);
        List<T> temporaria = Lists.newArrayList(voConsultaAtualizada);
        for (Iterator<T> voConsulta = parcelasParaOrdenar.iterator(); voConsulta.hasNext(); ) {
            T reOrdenar = voConsulta.next();
            boolean encontrouOndeAdicionar = false;
            for (Iterator<T> next = temporaria.iterator(); next.hasNext(); ) {
                T proximo = next.next();
                if (reOrdenar.getResultadoParcela().getReferencia().startsWith(proximo.getResultadoParcela().getReferencia())
                    && reOrdenar.getResultadoParcela().getParcelaNotNull().equals(proximo.getResultadoParcela().getParcelaNotNull())
                    && reOrdenar.getResultadoParcela().getCadastroNotNull().equals(proximo.getResultadoParcela().getCadastroNotNull())) {
                    int i = voConsultaAtualizada.indexOf(proximo);
                    voConsultaAtualizada.add(i + 1, reOrdenar);
                    encontrouOndeAdicionar = true;
                    break;
                }
            }
            if (!encontrouOndeAdicionar) {
                voConsultaAtualizada.add(reOrdenar);
            }
        }
    }

    public void criarAssistenteAndIniciarGeracaoPdf() throws ExecutionException, InterruptedException, IOException, JRException {
        try {
            AssistenteRelacaoLancamentoTributario assistente = criarAssistenteRelacaoLancamentoTributario(getFiltroLancamento());
            RelatorioRelacaoLancamentoTributario relatorioRelacaoLancamentoTributario = new RelatorioRelacaoLancamentoTributario(getRelacaoLancamentoTributario(),
                new Date(), getFacade().getSistemaFacade().getUsuarioCorrente(), getFiltroLancamento(),
                assistente);
            relatorioCriadoEm = relatorioRelacaoLancamentoTributario.getCriadoEm();
            singletonRelatorioRelacaoLancamentoTributario.adicionarRelatorioEmMemoriaSemBytes(relatorioRelacaoLancamentoTributario);

            iniciaGeracaoPdf(assistente, getFiltroLancamento());
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            getLogger().error("ERRO AO CRIAR O ASSISTENTE [{}]", e);
        }

    }

    public void iniciaGeracaoPdfFalhado(AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario, AbstractFiltroRelacaoLancamentoDebito filtros, Long relatorioCriadoEm) {
        this.relatorioCriadoEm = relatorioCriadoEm;
        iniciaGeracaoPdf(assistenteRelacaoLancamentoTributario, filtros);
    }

    public void iniciaGeracaoPdf(AssistenteRelacaoLancamentoTributario assistenteRelacaoLancamentoTributario, AbstractFiltroRelacaoLancamentoDebito filtros) {
        if (!iniciandoGeracaoPdf) {

            iniciandoGeracaoPdf = true;
            jaspersGerados = false;
            this.assistenteRelacaoLancamentoTributario = assistenteRelacaoLancamentoTributario;
            this.filtros = filtros;

            try {
                String caminhoReport = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF/report/") + "/";
                String caminhoImagem = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/img/") + "/";
                String usuario = getFacade().getSistemaFacade().getUsuarioCorrente().toString();
                futureJaspers = Lists.newArrayList();
                if (!getFacade().isSomenteTotalizador(assistenteRelacaoLancamentoTributario)) {
                    int bloco = 0;
                    getLogger().debug("TOTAL DE REGISTROS : " + assistenteRelacaoLancamentoTributario.getResultado().size());
                    int subListSize = assistenteRelacaoLancamentoTributario.getResultado().size() < 100 ? assistenteRelacaoLancamentoTributario.getResultado().size() : assistenteRelacaoLancamentoTributario.getResultado().size() / 4;
                    List<? extends List<? extends AbstractVOConsultaLancamento>> particoes = Lists.partition(assistenteRelacaoLancamentoTributario.getResultado(), subListSize);
                    for (List<? extends AbstractVOConsultaLancamento> parte : particoes) {
                        bloco += 1;
                        futureJaspers.add(getFacade().gerarJasper(usuario, caminhoReport, caminhoImagem, getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente(), getNomeRelatorio(), filtros.getFiltro().toString(), parte, bloco));
                    }
                }
                FacesUtil.executaJavaScript("acompanhaGeracaoPdf()");
            } catch (Exception e) {
                FacesUtil.executaJavaScript("aguarde.hide()");
                getLogger().error("ERRO AO GERAR O PDF [{}]", e);
            } finally {
                iniciandoGeracaoPdf = false;
            }
        }
    }

    private void gerarBytesRelatorio() {
        if (!gerandoBytes) {
            try {
                String caminhoReport = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/WEB-INF/report/") + "/";
                String caminhoImagem = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getRealPath("/img/") + "/";
                String usuario = getFacade().getSistemaFacade().getUsuarioCorrente().toString();
                List<JasperPrint> jaspers = Lists.newArrayList();
                for (Future<JasperPrint> jasperPrintFuture : futureJaspers) {
                    jaspers.add(jasperPrintFuture.get());
                }
                futureBytesRelatorio = getFacade().gerarRelatorio(jaspers, usuario, caminhoReport, caminhoImagem, getRelacaoLancamentoTributario(), filtros, assistenteRelacaoLancamentoTributario, getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
            } catch (Exception e) {
                FacesUtil.executaJavaScript("aguarde.hide()");
                getLogger().error("ERRO AO GERAR O PDF [{}]", e);
            } finally {
                gerandoBytes = false;
            }
        }
    }


    public void incluirRelatorioNoSingleton() {
        try {
            singletonRelatorioRelacaoLancamentoTributario.preencherBytesDoRelatorioEmMemoria(relatorioCriadoEm,
                futureBytesRelatorio.get());
        } catch (Exception e) {
            getLogger().debug("ERRO AO COLOCAR OS BYTES NO SINGLETON [{}]", e);
        }
    }

    public void baixarPDF(ByteArrayOutputStream byteArrayOutputStream) {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();

            HttpServletResponse response = (HttpServletResponse) faces.
                getExternalContext().getResponse();

            response.addHeader("Content-Disposition:", "attachment; filename=RelacaoLancamentoTributario.pdf");
            response.setContentType("application/pdf");

            byte[] bytes = byteArrayOutputStream.toByteArray();

            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();

            faces.responseComplete();
        } catch (Exception e) {
            getLogger().error("Erro ao baixar relatório [{}]", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<SituacaoParcela> getTodasAsSituacoes() {
        return SituacaoParcela.getValues();
    }

    public List<SelectItem> tiposCadastro() {
        return Util.getListSelectItem(TipoCadastroTributario.values());
    }

    public Future<List<T>> getFutureDadosRelatorio() {
        return futureDadosRelatorio;
    }

    public void setFutureDadosRelatorio(Future<List<T>> futureDadosRelatorio) {
        this.futureDadosRelatorio = futureDadosRelatorio;
    }

    public boolean isFutureDadosRelatorioConcluida() {
        if (futureDadosRelatorio == null) {
            return false;
        }

        return futureDadosRelatorio.isDone();
    }

    public void setFutureDadosRelatorioConcluida(boolean futureDadosRelatorioConcluida) {
        this.futureDadosRelatorioConcluida = futureDadosRelatorioConcluida;
    }

    public Future<ByteArrayOutputStream> getFutureBytesRelatorio() {
        return futureBytesRelatorio;
    }

    public void setFutureBytesRelatorio(Future<ByteArrayOutputStream> futureBytesRelatorio) {
        this.futureBytesRelatorio = futureBytesRelatorio;
    }

    public boolean isFutureBytesRelatorioConcluida() {
        if (!jaspersGerados) {
            if (futureJaspers == null) {
                return false;
            }
            boolean allFinish = true;
            for (Future<JasperPrint> jasperPrintFuture : futureJaspers) {
                if (jasperPrintFuture == null || !jasperPrintFuture.isDone()) {
                    allFinish = false;
                    break;
                }
            }
            if (allFinish) {
                jaspersGerados = true;
                gerarBytesRelatorio();
            }
        }
        if (futureBytesRelatorio == null) {
            return false;
        }
        return futureBytesRelatorio.isDone();
    }

    public void setFutureBytesRelatorioConcluida(boolean futureBytesRelatorioConcluida) {
        this.futureBytesRelatorioConcluida = futureBytesRelatorioConcluida;
    }

    public List<Future<List<T>>> getFuturesDadosRelatorioCalculados() {
        return futuresDadosRelatorioCalculados;
    }

    public void setFuturesDadosRelatorioCalculados(List<Future<List<T>>> futuresDadosRelatorioCalculados) {
        this.futuresDadosRelatorioCalculados = futuresDadosRelatorioCalculados;
    }

    public boolean isFuturesDadosRelatorioCalculadosConcluidas() throws ExecutionException, InterruptedException {
        futuresDadosRelatorioCalculadosConcluidas = true;
        if (futuresDadosRelatorioCalculados == null) {
            futuresDadosRelatorioCalculadosConcluidas = false;
        } else {
            for (int i = 0; i < futuresDadosRelatorioCalculados.size(); i++) {
                if (futuresDadosRelatorioCalculados.get(i) == null || !futuresDadosRelatorioCalculados.get(i).isDone()) {
                    futuresDadosRelatorioCalculadosConcluidas = false;
                    break;
                }
            }
        }
        return futuresDadosRelatorioCalculadosConcluidas;
    }

    public void setFuturesDadosRelatorioCalculadosConcluidas(boolean futuresDadosRelatorioCalculadosConcluidas) {
        this.futuresDadosRelatorioCalculadosConcluidas = futuresDadosRelatorioCalculadosConcluidas;
    }

    public List<RelatorioRelacaoLancamentoTributario> buscarRelatoriosEmMemoria() {
        return singletonRelatorioRelacaoLancamentoTributario.buscarRelatoriosEmMemoriaDoLancamento(getRelacaoLancamentoTributario());
    }

    public void limparRelatoriosEmMemoria() {
        singletonRelatorioRelacaoLancamentoTributario.limparRelatoriosEmMemoriaDoTipoDeCalculo(getRelacaoLancamentoTributario());
    }

    public List<SelectItem> getGrausDeRisco() {
        if (grausDeRisco == null) {
            grausDeRisco = Lists.newArrayList();
            grausDeRisco.add(new SelectItem(null, "     "));
            for (GrauDeRiscoDTO grauDeRisco : GrauDeRiscoDTO.values()) {
                grausDeRisco.add(new SelectItem(grauDeRisco, grauDeRisco.getDescricao()));
            }
        }
        return grausDeRisco;
    }

    public List<SelectItem> getTiposAutonomos() {
        if (tiposAutonomos == null) {
            tiposAutonomos = Lists.newArrayList();
            tiposAutonomos.add(new SelectItem(null, "     "));
            for (TipoAutonomo tipoAutonomo : tipoAutonomoFacade.lista()) {
                tiposAutonomos.add(new SelectItem(tipoAutonomo, tipoAutonomo.getDescricao()));
            }
        }
        return tiposAutonomos;
    }

    public List<SelectItem> getDividasEconomico() {
        if (dividasEconomico == null) {
            dividasEconomico = Lists.newArrayList();
            dividasEconomico.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.listaDividasDoTipoCadastro("", TipoCadastroTributario.ECONOMICO)) {
                dividasEconomico.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasEconomico;
    }

    public List<SelectItem> getDividasParcelamento() {
        if (dividasParcelamento == null) {
            dividasParcelamento = Lists.newArrayList();
            dividasParcelamento.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeParcelamentoDeDividaAtivaOrdenadoPorDescricao()) {
                dividasParcelamento.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasParcelamento;
    }

    public List<SelectItem> getTiposCobranca() {
        if (tiposCobranca == null) {
            tiposCobranca = new ArrayList<>();
            tiposCobranca.add(new SelectItem(null, ""));
            for (TipoCobrancaTributario tipoCobranca : TipoCobrancaTributario.values()) {
                tiposCobranca.add(new SelectItem(tipoCobranca, tipoCobranca.getDescricao()));
            }
        }
        return tiposCobranca;
    }

    public List<SelectItem> getDividasDividasDiversas() {
        if (dividasDividasDiversas == null) {
            dividasDividasDiversas = Lists.newArrayList();
            dividasDividasDiversas.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeDividasDiversas("")) {
                dividasDividasDiversas.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasDividasDiversas;
    }

    public List<SelectItem> getDividasTaxasDiversas() {
        if (dividasTaxasDiversas == null) {
            dividasTaxasDiversas = Lists.newArrayList();
            dividasTaxasDiversas.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeTaxasDiversas("")) {
                dividasTaxasDiversas.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasTaxasDiversas;
    }

    public List<SelectItem> getDividasISSQNAndNFSE() {
        if (dividasISSQNAndNFSE == null) {
            dividasISSQNAndNFSE = Lists.newArrayList();
            dividasISSQNAndNFSE.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeISSQNAndNFSE("")) {
                dividasISSQNAndNFSE.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasISSQNAndNFSE;
    }

    public List<SelectItem> getDividasDividaAtiva() {
        if (dividasDividaAtiva == null) {
            dividasDividaAtiva = Lists.newArrayList();
            dividasDividaAtiva.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeDividaAtiva("")) {
                dividasDividaAtiva.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasDividaAtiva;
    }

    public List<SelectItem> getDividasFiscalizacaoISSQN() {
        if (dividasFiscalizacaoISSQN == null) {
            dividasFiscalizacaoISSQN = Lists.newArrayList();
            dividasFiscalizacaoISSQN.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeFiscalizacaoISSQN("")) {
                dividasFiscalizacaoISSQN.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasFiscalizacaoISSQN;
    }

    public List<SelectItem> getDividasFiscalizacaoSecretaria() {
        if (dividasFiscalizacaoSecretaria == null) {
            dividasFiscalizacaoSecretaria = Lists.newArrayList();
            dividasFiscalizacaoSecretaria.add(new SelectItem(null, "     "));
            for (Divida divida : dividaFacade.buscarDividasDeFiscalizacaoSecretaria("")) {
                dividasFiscalizacaoSecretaria.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividasFiscalizacaoSecretaria;
    }

    public List<SelectItem> getDividas() {
        if (dividas == null) {
            dividas = Lists.newArrayList();
            dividas.add(new SelectItem(null, ""));
            for (Divida divida : dividaFacade.listaDividasOrdenadoPorDescricao()) {
                dividas.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return dividas;
    }
}
