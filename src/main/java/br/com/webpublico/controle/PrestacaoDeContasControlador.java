package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.ActionConsultaEntidade;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.controlerelatorio.RelatorioCreditosReceberControlador;
import br.com.webpublico.controlerelatorio.RelatorioDemonstrativoDisponibilidadeRecursoControlador;
import br.com.webpublico.controlerelatorio.RelatorioDemonstrativoDividaAtivaContControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.PrestacaoDeContasItens;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PrestacaoDeContasFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 31/01/14
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-prestacao-contas", pattern = "/prestacao-contas-tribunal/", viewId = "/faces/financeiro/prestacaodecontas/edita.xhtml"),
    @URLMapping(id = "lista-prestacao-contas", pattern = "/prestacao-contas-tribunal/listar/", viewId = "/faces/financeiro/prestacaodecontas/lista.xhtml")
})
public class PrestacaoDeContasControlador extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PrestacaoDeContasControlador.class);
    @EJB
    private PrestacaoDeContasFacade prestacaoDeContasFacade;
    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    private HashMap<String, File> files;
    private ConverterAutoComplete converterUnidadeGestora;
    private PrestacaoDeContasItens selecionado;

    public String caminhoPadrao() {
        return "prestacao-contas-tribunal";
    }

    @URLAction(mappingId = "novo-prestacao-contas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        parametrosIniciais();
    }

    private void parametrosIniciais() {
        SistemaControlador sistemaControlador = getSistemaControlador();
        selecionado = new PrestacaoDeContasItens();
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setQuantidadeDeLancamento(0);
        selecionado.setQuantidadeDeLancamentoPorMovimento(0);
        selecionado.setDecorrido(0l);
        selecionado.setTempo(0l);
        selecionado.setProcessados(0);
        selecionado.setTotal(0);
        selecionado.setCalculando(false);
        selecionado.setDataOperacao(sistemaControlador.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
        int mes1 = DataUtil.getMes(sistemaControlador.getDataOperacao());
        List<Mes> meses = Arrays.asList(Mes.values());
        for (Mes mesDaVez : meses) {
            if (mesDaVez.getNumeroMes() == mes1) {
                selecionado.setMes(mesDaVez);
            }
        }
        selecionado.setGerarArquivoOrcamento(Boolean.FALSE);
        selecionado.setBuscarSaldoInicial(Boolean.TRUE);
        List<String> tipoEventoContabils = new ArrayList<>();
        for (TipoEventoContabil tipoEventoContabil : Arrays.asList(TipoEventoContabil.values())) {
            tipoEventoContabils.add(tipoEventoContabil.getDescricao());
        }
        selecionado.setTiposEventosContabeis(new DualListModel<String>(new ArrayList<String>(), tipoEventoContabils));
        selecionado.setErrosGeracaoDoArquivo(new ArrayList<String>());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void apagar() {
        try {
            if (selecionado.getZipFile() != null) {
                selecionado.getZipFile().delete();
            }
            if (files != null) {
                for (Map.Entry<String, File> m : files.entrySet()) {
                    m.getValue().delete();
                }
            }
            selecionado.setStreamedContent(null);
        } catch (Exception e) {

        }
    }

    public void salvarTodosArquivosInicial() {
        try {
            for (LancamentoArquivoTCE lancamentoArquivoTCE : selecionado.getLancamentosSaldoInicial()) {
                prestacaoDeContasFacade.salvar(lancamentoArquivoTCE);
            }
            for (PartidaArquivoTCE partidaArquivoTCE : selecionado.getPartidasSaldoInicial()) {
                prestacaoDeContasFacade.salvar(partidaArquivoTCE);
            }
            FacesUtil.addInfo("Salvo com sucesso.", "");
        } catch (Exception e) {
            FacesUtil.addError("Não Foi possível salvar.", e.getMessage());
        }
    }

    public void alterarMes() {
        zerarMovimentos();
        selecionado.setBuscarSaldoInicial(Mes.JANEIRO.equals(selecionado.getMes()));
    }

    private void validarArquivo() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getGerarArquivoOrcamento() && selecionado.getUnidadeGestora() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Gestora deve ser informado.");
        }
        ve.lancarException();

    }

    public void gerarArquivo() {
        try {
            zerarMovimentos();
            validarArquivo();
            prestacaoDeContasFacade.gerarArquivo(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Erro ao gerar o Arquivo.", e.getMessage());
        }
    }

    public void zerarMovimentos() {
        selecionado.setQuantidadeDeLancamento(0);
        selecionado.setQuantidadeDeLancamentoPorMovimento(0);
        selecionado.setProcessados(0);
        selecionado.setTotal(0);
        apagar();
    }

    public void removerBarraStatus() {
        if (!selecionado.isCalculando()) {
            FacesUtil.atualizarComponente("Formulario");
            try {
                Thread.sleep(3000);
            } catch (Exception e) {

            }
            FacesUtil.executaJavaScript("efeitoFade();");
            FacesUtil.executaJavaScript("removeClassBarra();");
        }
    }


    public Double getPorcentagemDoCalculo() {
        if (selecionado.getProcessados() == null || selecionado.getTotal() == null) {
            return 0d;
        }
        return (selecionado.getProcessados().doubleValue() / selecionado.getTotal().doubleValue()) * 100;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);

        selecionado.setDecorrido(System.currentTimeMillis() - selecionado.getTempo());

        return String.format(formatoDataHora, selecionado.getDecorrido() / HOUR, selecionado.getDecorrido() % HOUR);
    }

    public String getTempoEstimado() {
        try {
            long HOUR = TimeUnit.HOURS.toMillis(1);
            long unitario = (System.currentTimeMillis() - selecionado.getTempo()) / (selecionado.getProcessados() + 1);
            Double qntoFalta = (unitario * (selecionado.getTotal() - selecionado.getProcessados().doubleValue()));

            return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        } catch (Exception e) {
            return "0";
        }
    }


    public List<UnidadeGestora> completaUnidadeGestora(String parte) {
        return prestacaoDeContasFacade.getUnidadeGestoraFacade().completaUnidadeGestoraDasUnidadeDoUsuarioLogadoVigentes(parte, getSistemaControlador().getUsuarioCorrente(),
            getSistemaControlador().getDataOperacao(), getSistemaControlador().getExercicioCorrente(), TipoUnidadeGestora.PRESTACAO_CONTAS);
    }

    public List<SelectItem> getMesSelect() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoArquivoTCESelect() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoArquivoTCE object : TipoArquivoTCE.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void gerarBalancete() {
        try {
            String filtro;
            List<ParametrosRelatorios> parametros = Lists.newArrayList();
            parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, OperacaoRelatorio.MENOR_IGUAL, prestacaoDeContasFacade.dataInicialAsString(selecionado.getMes(), selecionado.getExercicio()), null, 0, true));
            parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, OperacaoRelatorio.MENOR_IGUAL, prestacaoDeContasFacade.dataFimAsString(selecionado.getMes(), selecionado.getExercicio()), null, 0, true));
            parametros.add(new ParametrosRelatorios(null, ":DATAEXERCICIO", null, OperacaoRelatorio.MAIOR_IGUAL, "01/01/" + getSistemaControlador().getExercicioCorrente().getAno(), null, 0, true));
            List<String> tipoBalanceteInicial = Lists.newArrayList();
            if (Mes.JANEIRO.equals(selecionado.getMes())) {
                tipoBalanceteInicial.add(TipoBalancete.TRANSPORTE.name());
            } else {
                tipoBalanceteInicial.add(TipoBalancete.TRANSPORTE.name());
                tipoBalanceteInicial.add(TipoBalancete.ABERTURA.name());
                tipoBalanceteInicial.add(TipoBalancete.MENSAL.name());
            }
            parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteInicial", null, OperacaoRelatorio.IN, tipoBalanceteInicial, null, 0, false));
            List<String> tipoBalanceteSaldoAtual = Lists.newArrayList();
            if (Mes.DEZEMBRO.equals(selecionado.getMes())) {
                tipoBalanceteSaldoAtual.add(TipoBalancete.ENCERRAMENTO.name());
                tipoBalanceteSaldoAtual.add(TipoBalancete.MENSAL.name());
                tipoBalanceteSaldoAtual.add(TipoBalancete.APURACAO.name());
                parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteFinal", null, OperacaoRelatorio.IN, tipoBalanceteSaldoAtual, null, 0, false));
                parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteSaldoAtual", null, OperacaoRelatorio.IN, tipoBalanceteSaldoAtual, null, 0, false));
            } else {
                if (Mes.JANEIRO.equals(selecionado.getMes())) {
                    tipoBalanceteSaldoAtual.add(TipoBalancete.ABERTURA.name());
                    tipoBalanceteSaldoAtual.add(TipoBalancete.MENSAL.name());
                } else {
                    tipoBalanceteSaldoAtual.add(TipoBalancete.MENSAL.name());
                }

                parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteFinal", null, OperacaoRelatorio.IN, TipoBalancete.MENSAL.name(), null, 0, false));
                parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteSaldoAtual", null, OperacaoRelatorio.IN, tipoBalanceteSaldoAtual, null, 0, false));
            }
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, selecionado.getUnidadeGestora().getId(), null, 1, false));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
            parametros.add(new ParametrosRelatorios(" c.exercicio_id ", ":exercicio", null, OperacaoRelatorio.IGUAL, getSistemaControlador().getExercicioCorrente().getId(), null, 1, false));


            filtro = " Período: " + prestacaoDeContasFacade.dataInicialAsString(selecionado.getMes(), selecionado.getExercicio()) + " (" +
                (Mes.JANEIRO.equals(selecionado.getMes()) ? TipoBalancete.ABERTURA.getDescricao() : TipoBalancete.MENSAL.getDescricao()) + ") a " +
                prestacaoDeContasFacade.dataFimAsString(selecionado.getMes(), selecionado.getExercicio()) + " (" + (Mes.DEZEMBRO.equals(selecionado.getMes()) ? TipoBalancete.ENCERRAMENTO.getDescricao() : TipoBalancete.MENSAL.getDescricao()) + ") -";
            filtro += " Unidade Gestora: " + selecionado.getUnidadeGestora().getCodigo() + " -";
            filtro = filtro.substring(0, filtro.length() - 1);

            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("apresentacao", ApresentacaoRelatorio.CONSOLIDADO.getToDto());
            dto.adicionarParametro("pesquisouUg", selecionado.getUnidadeGestora() != null);
            dto.adicionarParametro("parametros", ParametrosRelatorios.parametrosToDto(parametros));
            dto.adicionarParametro("exibirContaAuxiliar", false);
            dto.adicionarParametro("dataInicial", prestacaoDeContasFacade.dataInicialAsString(selecionado.getMes(), selecionado.getExercicio()));
            dto.adicionarParametro("tipoBalancete", Mes.JANEIRO.equals(selecionado.getMes()) ? TipoBalancete.ABERTURA.getToDto() : TipoBalancete.MENSAL.getToDto());
            dto.adicionarParametro("tipoBalanceteFinal", Mes.DEZEMBRO.equals(selecionado.getMes()) ? TipoBalancete.ENCERRAMENTO.getToDto() : TipoBalancete.MENSAL.getToDto());
            dto.adicionarParametro("classificacaoContaAuxiliar", null);

            dto.adicionarParametro("USER", getSistemaControlador().getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("FILTRO", filtro);
            dto.adicionarParametro("DATAINICIAL", prestacaoDeContasFacade.dataInicialAsString(selecionado.getMes(), selecionado.getExercicio()));
            dto.adicionarParametro("DATAFINAL", prestacaoDeContasFacade.dataFimAsString(selecionado.getMes(), selecionado.getExercicio()));
            dto.adicionarParametro("APRESENTACAO", ApresentacaoRelatorio.CONSOLIDADO.name());
            dto.setNomeRelatorio(getNomeArquivo() + "-BLC");
            dto.setApi("contabil/balancete-contabil-tipo/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void gerarDemonstrativoCreditosReceber() {
        RelatorioCreditosReceberControlador controlador = (RelatorioCreditosReceberControlador) Util.getControladorPeloNome("relatorioCreditosReceberControlador");
        controlador.setDataInicial(prestacaoDeContasFacade.dataInicialAsDate(selecionado.getMes(), selecionado.getExercicio()));
        controlador.setDataFinal(prestacaoDeContasFacade.dataFimAsDate(selecionado.getMes(), selecionado.getExercicio()));
        controlador.setApresentacao(ApresentacaoRelatorio.CONSOLIDADO);
        controlador.setListaUnidades(new ArrayList<HierarquiaOrganizacional>());
        controlador.setUnidadeGestora(selecionado.getUnidadeGestora());
        controlador.gerarRelatorioCreditosReceber();
    }

    public void gerarDemonstrativoDividaAtiva() {
        RelatorioDemonstrativoDividaAtivaContControlador controlador = (RelatorioDemonstrativoDividaAtivaContControlador) Util.getControladorPeloNome("relatorioDemonstrativoDividaAtivaContControlador");
        controlador.setDataInicial(prestacaoDeContasFacade.dataInicialAsDate(selecionado.getMes(), selecionado.getExercicio()));
        controlador.setDataFinal(prestacaoDeContasFacade.dataFimAsDate(selecionado.getMes(), selecionado.getExercicio()));
        controlador.setListaUnidades(new ArrayList<HierarquiaOrganizacional>());
        controlador.setUnidadeGestora(selecionado.getUnidadeGestora() != null ? selecionado.getUnidadeGestora() : null);
        controlador.setItens(recuperaItensDemonstrativoDividaAtiva(controlador));
        controlador.gerarRelatorio();
    }

    private List<ItemDemonstrativoComponente> recuperaItensDemonstrativoDividaAtiva(RelatorioDemonstrativoDividaAtivaContControlador controlador) {
        ItemDemonstrativoComponenteControlador controladorItemDemonstrativo = (ItemDemonstrativoComponenteControlador) Util.getControladorPeloNome("itemDemonstrativoComponenteControlador");
        String nomeRelatorio = "Demonstrativo de Dívida Ativa";
        controladorItemDemonstrativo.novo(nomeRelatorio, TipoRelatorioItemDemonstrativo.OUTROS, controlador.getItens(), null);
        return controladorItemDemonstrativo.getItens();
    }

    public void gerarDemonstrativoDisponibilidadePorDestinacaoRecurso() {
        RelatorioDemonstrativoDisponibilidadeRecursoControlador controlador = (RelatorioDemonstrativoDisponibilidadeRecursoControlador) Util.getControladorPeloNome("relatorioDemonstrativoDisponibilidadeRecursoControlador");
        controlador.setFiltro("");
        controlador.setMes(selecionado.getMes());
        controlador.setMesFinal(selecionado.getMes().getNumeroMesString());
        controlador.setHierarquias(new ArrayList<HierarquiaOrganizacional>());
        controlador.setUnidadeGestora(selecionado.getUnidadeGestora() != null ? selecionado.getUnidadeGestora() : null);
        controlador.setApresentacaoRelatorio(ApresentacaoRelatorio.CONSOLIDADO);
        controlador.setTipoExibicao(TipoExibicao.CONTA_DE_DESTINACAO);
        controlador.setExercicio(selecionado.getExercicio());
        controlador.gerarRelatorio("PDF");
    }

    public void gerarDetalhamentoLiquidacaoPorElementoDeDespesa() {
        try {
            List<ParametrosRelatorios> parametros = new ArrayList<>();
            parametros.add(new ParametrosRelatorios(" trunc(liq.dataliquidacao) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(prestacaoDeContasFacade.dataInicialAsDate(selecionado.getMes(), selecionado.getExercicio())), DataUtil.getDataFormatada(prestacaoDeContasFacade.dataFimAsDate(selecionado.getMes(), selecionado.getExercicio())), 2, true));
            parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO) ", ":DATAINICIAL", ":DATAFINAL", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(prestacaoDeContasFacade.dataInicialAsDate(selecionado.getMes(), selecionado.getExercicio())), DataUtil.getDataFormatada(prestacaoDeContasFacade.dataFimAsDate(selecionado.getMes(), selecionado.getExercicio())), 3, true));
            parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, getSistemaControlador().getExercicioCorrente().getId(), null, 0, false));
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, selecionado.getUnidadeGestora().getId(), null, 1, false));
            String filtro = " Período: " + DataUtil.getDataFormatada(prestacaoDeContasFacade.dataInicialAsDate(selecionado.getMes(), selecionado.getExercicio())) + " a " + DataUtil.getDataFormatada(prestacaoDeContasFacade.dataFimAsDate(selecionado.getMes(), selecionado.getExercicio())) + " -";
            filtro += " Unidade Gestora: " + selecionado.getUnidadeGestora().getCodigo();

            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("contabil/detalhamento-liquidacao/");
            dto.setNomeRelatorio(getNomeArquivo() + "-LQD");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
            dto.adicionarParametro("APRESENTACAO", ApresentacaoRelatorio.CONSOLIDADO.name());
            dto.adicionarParametro("apresentacao", ApresentacaoRelatorio.CONSOLIDADO.getToDto());
            dto.adicionarParametro("pesquisouUg", selecionado.getUnidadeGestora() != null);
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getUsername(), Boolean.FALSE);
            dto.adicionarParametro("FILTRO", filtro);
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void gerarPorTipoEventoContabil() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("isPorEvento", false);
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("FILTRO", "Período de Referência: " + selecionado.getMes() + "/" + selecionado.getExercicio().getAno() + " " + (selecionado.getUnidadeGestora() != null ? "- Unidade Gestora:" + selecionado.getUnidadeGestora() : ""));
            dto.adicionarParametro("pesquisouUg", true);
            List<ParametrosRelatorios> parametros = Lists.newArrayList();
            parametros.add(new ParametrosRelatorios(" trunc(l.dataLancamento) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, prestacaoDeContasFacade.dataInicialAsString(selecionado.getMes(), selecionado.getExercicio()), prestacaoDeContasFacade.dataFimAsString(selecionado.getMes(), selecionado.getExercicio()), 1, true));
            if (selecionado.getUnidadeGestora() != null) {
                parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, this.selecionado.getUnidadeGestora().getId(), null, 1, false));
                parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, selecionado.getExercicio().getId(), null, 0, false));
            }
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
            dto.setNomeRelatorio(getNomeArquivo() + "-DTE");
            dto.setApi("contabil/demonstrativo-contabil-evento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getNomeArquivo() {
        return prestacaoDeContasFacade.getNomeArquivo(selecionado);
    }

    public ConverterAutoComplete getConverterUnidadeGestora() {
        if (converterUnidadeGestora == null) {
            converterUnidadeGestora = new ConverterAutoComplete(UnidadeGestora.class, prestacaoDeContasFacade.getUnidadeGestoraFacade());
        }
        return converterUnidadeGestora;
    }

    public PrestacaoDeContasItens getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(PrestacaoDeContasItens selecionado) {
        this.selecionado = selecionado;
    }

    public void redirecionarListarArquivos() {
        FacesUtil.redirecionamentoInterno("/prestacao-contas-tribunal/listar/");
    }

    public StreamedContent getStreamedContent() {
        prestacaoDeContasFacade.criarESalvarArquivoPrestacaoDeContas(selecionado);
        return selecionado.getStreamedContent();
    }

    public List<ActionConsultaEntidade> getActionsPesquisa() {
        ActionConsultaEntidade action = new ActionPrestacaoDeContas();
        action.setIcone("ui-icon-download");
        action.setTitle("Clique para fazer o download do arquivo.");
        action.setDownload(true);
        return Lists.newArrayList(action);
    }

    public class ActionPrestacaoDeContas extends ActionConsultaEntidade {
        @Override
        public void action(Map<String, Object> registro) throws IOException {
            Long idArquivoPDC = ((Number) registro.get("identificador")).longValue();
            ArquivoPrestacaoDeContas arquivoPDC = prestacaoDeContasFacade.recuperarArquivoPrestacaoDeContas(idArquivoPDC);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : arquivoPDC.getArquivo().getPartes()) {
                try {
                    buffer.write(a.getDados());
                } catch (IOException ex) {
                    logger.error("Erro: ", ex);
                }
            }
            InputStream is = new ByteArrayInputStream(buffer.toByteArray());
            StreamedContent streamedContent = new DefaultStreamedContent(is, arquivoPDC.getArquivo().getMimeType(), arquivoPDC.getArquivo().getNome());
            escreverNoResponse(IOUtils.toByteArray(streamedContent.getStream()), arquivoPDC.getArquivo().getNome(), streamedContent.getContentType());
        }
    }
}
