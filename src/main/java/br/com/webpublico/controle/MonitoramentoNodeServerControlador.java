package br.com.webpublico.controle;

import br.com.webpublico.agendamentotarefas.SingletonAgendamentoTarefas;
import br.com.webpublico.entidades.ConfiguracaoMetrica;
import br.com.webpublico.entidades.MetricaMemoria;
import br.com.webpublico.entidades.MetricaSistema;
import br.com.webpublico.entidades.UsuarioNodeServer;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.message.RabbitMQService;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MonitoramentoNodeServerFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.negocios.comum.GarbageCollectorService;
import br.com.webpublico.negocios.tributario.services.ServiceAgendamentoTarefa;
import br.com.webpublico.seguranca.SingletonMetricas;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.seguranca.VerificadorSessoesAtivasUsuario;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpush.push.Push;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.joda.time.LocalDateTime;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by André on 27/01/2015.
 */
@ViewScoped
@ManagedBean(name = "monitoramentoNodeServerControlador")
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaMonitoramentoNode", pattern = "/monitoramento/", viewId = "/faces/admin/monitoramentonode/edita.xhtml"),
    @URLMapping(id = "processosAssincrono", pattern = "/processos-assincrono/", viewId = "/faces/admin/monitoramentonode/assincrono.xhtml")
})
public class MonitoramentoNodeServerControlador extends PrettyControlador<UsuarioNodeServer> {
    @EJB
    private MonitoramentoNodeServerFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    private ServiceAgendamentoTarefa serviceAgendamentoTarefa;
    private SingletonMetricas singletonMetricas;
    private List<UsuarioNodeServer> lista;
    private List<LinkedHashMap> sqlsEmExecucao;
    private Map<MetricaSistema.Tipo, Map<String, MediaPagina>> metricas;
    private Map<MetricaSistema.Tipo, Double> medias;
    private Date inicio, fim;
    private MetricaMemoria metricaMemoria;
    private Integer totalThreads;
    private Integer runningThreads;
    private List<GarbageCollectorMXBean> garbages;
    private CartesianChartModel cartesianChartModel;
    private JbossConnections jbossConnections;
    private ConfiguracaoMetrica configuracaoMetrica;
    private SingletonRecursosSistema singletonRecursosSistema;
    private SingletonAgendamentoTarefas singletonAgendamentoTarefas;
    private RestTemplate restTemplate;
    private AsyncExecutor asyncExecutor;
    private Date dataOperacao;
    private DashboarWebReportDTO dashboarWebReportDTO;
    private ConfiguracaoDeRelatorio configuracaoDeRelatorio;
    private CartesianChartModel graficoAcompanhamentoRelatorioDiario;

    @PostConstruct
    public void init() {
        serviceAgendamentoTarefa = (ServiceAgendamentoTarefa) Util.getSpringBeanPeloNome("serviceAgendamentoTarefa");
        singletonMetricas = (SingletonMetricas) Util.getSpringBeanPeloNome("singletonMetricas");
        singletonRecursosSistema = (SingletonRecursosSistema) Util.getSpringBeanPeloNome("singletonRecursosSistema");
        singletonAgendamentoTarefas = (SingletonAgendamentoTarefas) Util.getSpringBeanPeloNome("singletonAgendamentoTarefas");
        asyncExecutor = AsyncExecutor.getInstance();
    }

    public CartesianChartModel getGraficoAcompanhamentoRelatorioDiario() {
        return graficoAcompanhamentoRelatorioDiario;
    }

    public void setGraficoAcompanhamentoRelatorioDiario(CartesianChartModel graficoAcompanhamentoRelatorioDiario) {
        this.graficoAcompanhamentoRelatorioDiario = graficoAcompanhamentoRelatorioDiario;
    }

    public ConfiguracaoDeRelatorio getConfiguracaoDeRelatorio() {
        if (configuracaoDeRelatorio == null) {
            configuracaoDeRelatorio = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        }
        return configuracaoDeRelatorio;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public DashboarWebReportDTO getDashboarWebReportDTO() {
        return dashboarWebReportDTO;
    }

    public void setDashboarWebReportDTO(DashboarWebReportDTO dashboarWebReportDTO) {
        this.dashboarWebReportDTO = dashboarWebReportDTO;
    }

    public AsyncExecutor getAsyncExecutor() {
        return asyncExecutor;
    }

    public List<UsuarioNodeServer> getLista() {
        return lista;
    }

    public SingletonAgendamentoTarefas getSingletonAgendamentoTarefas() {
        return singletonAgendamentoTarefas;
    }

    public List<LinkedHashMap> getSqlsEmExecucao() {
        return sqlsEmExecucao;
    }

    public MetricaMemoria getMetricaMemoria() {
        return metricaMemoria;
    }

    public Double getPercentualMemoria() {
        if (metricaMemoria == null || metricaMemoria.getUsedMemory() == null || metricaMemoria.getTotalMemory() == null) {
            return 0d;
        }
        return (metricaMemoria.getUsedMemory().doubleValue() / metricaMemoria.getTotalMemory().doubleValue()) * 100;
    }

    public Integer getTotalThreads() {
        return totalThreads;
    }

    public Integer getRunningThreads() {
        return runningThreads;
    }

    public Double getPercentualThreads() {
        if (runningThreads == null || totalThreads == null) {
            return 0d;
        }
        return (runningThreads.doubleValue() / totalThreads.doubleValue()) * 100;
    }

    public List<GarbageCollectorMXBean> getGarbages() {
        return garbages;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public CartesianChartModel getCartesianChartModel() {
        return cartesianChartModel;
    }

    public Map<MetricaSistema.Tipo, Map<String, MediaPagina>> getMetricas() {
        return metricas;
    }

    public Map<MetricaSistema.Tipo, Double> getMedias() {
        return medias;
    }

    public List<MetricaSistema.Tipo> getTipos() {
        return Lists.newArrayList(metricas.keySet());
    }

    public List<String> buscarMetricasDoTipo(MetricaSistema.Tipo tipo) {
        return Lists.newArrayList(metricas.get(tipo).keySet());
    }

    public MediaPagina buscarMediaDaUrl(MetricaSistema.Tipo tipo, String url) {
        return metricas.get(tipo).get(url);
    }

    public ConfiguracaoMetrica getConfiguracaoMetrica() {
        return configuracaoMetrica;
    }

    public void setConfiguracaoMetrica(ConfiguracaoMetrica configuracaoMetrica) {
        this.configuracaoMetrica = configuracaoMetrica;
    }

    public JbossConnections getJbossConnections() {
        return jbossConnections;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaConsultaMonitoramentoNode", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        popularMetricas();
        dataOperacao = new Date();
        dashboarWebReportDTO = new DashboarWebReportDTO();
    }

    public void salvarConfiguracao() {
        try {
            configuracaoMetrica.setUltimoUsuario(sistemaFacade.getUsuarioCorrente().getLogin());
            facade.salvarConfiguracao(configuracaoMetrica);
            singletonMetricas.limparComfiguracao();
            serviceAgendamentoTarefa.agendaTarefas();
            popularMetricas();
        } catch (Exception e) {
            FacesUtil.addAtencao("Não foi possível salvar a configuração " + e.getMessage());
        }
    }

    public void popularMetricas() {
        configuracaoMetrica = singletonMetricas.getConfiguracaoMetrica();
        if (configuracaoMetrica == null) {
            configuracaoMetrica = new ConfiguracaoMetrica();
            configuracaoMetrica.setCron("0/15 * * * * ?");
            configuracaoMetrica.setLigado(true);
            configuracaoMetrica.setUsuarioJboss("admin");
            configuracaoMetrica.setSenhaJboss("jboss");
        }
        if (inicio == null || fim == null) {
            fim = new Date();
            inicio = LocalDateTime.fromDateFields(fim).minusMinutes(5).toDate();
        }
        popularUsuariosConectados();
        popularMetricasPaginas();
        popularMetricasMemoria();
        popularMetricasThreads();
        popularMetricasGarbage();
        buscarConnections();
        buscarSqlsEmExecucao();
    }

    private void popularUsuariosConectados() {
        lista = facade.buscarUltimosLoginsDeHoje(inicio);
        for (UsuarioNodeServer usuario : lista) {
            if (singletonRecursosSistema.getUltimoAcessoUsuario().get(usuario.getUsuario()) != null) {
                usuario.setAtivo(true);
            }
            String recursoSistema = singletonRecursosSistema.getUltimaPaginaUsuario().get(usuario.getUsuario());
            if (recursoSistema != null) {
                usuario.setUltimaPagina(recursoSistema);
            }
        }
        lista.sort((o1, o2) -> o2.getAtivo().compareTo(o1.getAtivo()));
    }

    public Integer getTotalUsuariosAtivos() {
        return singletonRecursosSistema.getUltimoAcessoUsuario().keySet().size();
    }


    private void popularMetricasPaginas() {
        metricas = Maps.newHashMap();
        for (MetricaSistema.Tipo tipo : MetricaSistema.Tipo.values()) {
            metricas.put(tipo, new HashMap<String, MediaPagina>());
            List<MetricaSistema> metricaSistemas = facade.buscarUltimosAcessos(inicio, fim, tipo);
            Collections.sort(metricaSistemas);
            for (MetricaSistema metricaSistema : metricaSistemas) {
                if (metricas.get(tipo).get(metricaSistema.getUrl()) == null) {
                    metricas.get(tipo).put(metricaSistema.getUrl(), new MediaPagina());
                }
                metricas.get(tipo).get(metricaSistema.getUrl()).getMetricaSistemas().add(metricaSistema);
            }
        }
        calcularMediasPaginas();
    }

    private void calcularMediasPaginas() {
        medias = Maps.newHashMap();
        for (MetricaSistema.Tipo tipo : metricas.keySet()) {

            Double media = 0.0;
            for (String s : metricas.get(tipo).keySet()) {
                MediaPagina mediaPagina = metricas.get(tipo).get(s);
                for (MetricaSistema metricaSistema : mediaPagina.getMetricaSistemas()) {
                    getMediaMinima(mediaPagina, metricaSistema);
                    getMediaMaxima(mediaPagina, metricaSistema);
                    getMediaGeral(mediaPagina, metricaSistema);
                }
                mediaPagina.media = mediaPagina.media / mediaPagina.getMetricaSistemas().size();
                media = media + mediaPagina.media;
                metricas.get(tipo).put(s, mediaPagina);
            }
            media = media / metricas.get(tipo).keySet().size();
            medias.put(tipo, media);
        }
        for (MetricaSistema.Tipo tipo : metricas.keySet()) {
            LinkedHashMap<String, MediaPagina> sortedMap = sortHashMapByValues(metricas.get(tipo));
            metricas.put(tipo, sortedMap);
        }
    }

    private void getMediaGeral(MediaPagina mediaPagina, MetricaSistema metricaSistema) {
        mediaPagina.media = mediaPagina.media + metricaSistema.getTimeInSeconds();
    }

    private void getMediaMaxima(MediaPagina mediaPagina, MetricaSistema metricaSistema) {
        if (mediaPagina.max == 0 || metricaSistema.getTimeInSeconds() >= mediaPagina.max) {
            mediaPagina.max = metricaSistema.getTimeInSeconds().doubleValue();
        }
    }

    private void getMediaMinima(MediaPagina mediaPagina, MetricaSistema metricaSistema) {
        if (mediaPagina.min == 0 || metricaSistema.getTimeInSeconds() <= mediaPagina.min) {
            mediaPagina.min = metricaSistema.getTimeInSeconds().doubleValue();
        }
    }

    private void popularMetricasGarbage() {
        garbages = ManagementFactory.getGarbageCollectorMXBeans();
    }

    private void popularMetricasThreads() {
        totalThreads = Thread.getAllStackTraces().keySet().size();
        runningThreads = 0;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getState() == Thread.State.RUNNABLE) runningThreads++;
        }
    }

    private void popularMetricasMemoria() {
        metricaMemoria = singletonMetricas.getMetricaMemoria();
        popularGraficoMemoria();
    }

    private void popularGraficoMemoria() {
        cartesianChartModel = new CartesianChartModel();
        LineChartSeries serie1 = new LineChartSeries("Memoria");
        serie1.setLabel("Total");
        LineChartSeries serie2 = new LineChartSeries("Memoria");
        serie2.setLabel("Usada");
        List<MetricaMemoria> metricaMemorias = facade.buscarUltimasMetricasMemoria(inicio, fim);
        Collections.sort(metricaMemorias);
        for (MetricaMemoria memoria : metricaMemorias) {
            serie1.set(DataUtil.getDataFormatada(memoria.getData(), "HH:mm:ss"), metricaMemoria.getTotalMemory());
            serie2.set(DataUtil.getDataFormatada(memoria.getData(), "HH:mm:ss"), memoria.getUsedMemory());
        }
        cartesianChartModel.addSeries(serie1);
        cartesianChartModel.addSeries(serie2);

    }

    public void acompanhaProcessosAssincronos() {
        if (singletonAgendamentoTarefas.getProcessos().isEmpty()) {
            FacesUtil.executaJavaScript("terminaAcompanhamento()");
        }
    }


    private void buscarSqlsEmExecucao() {
        sqlsEmExecucao = facade.buscarSqlsEmExecucao();
    }

    private void buscarConnections() {
        try {
            if (restTemplate == null) {
                HttpComponentsClientHttpRequestFactory clientHttpRequestFactory;
                clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
                int timeout = 5000;
                clientHttpRequestFactory.setConnectTimeout(timeout);

                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                    new AuthScope("localhost", 9990),
                    new UsernamePasswordCredentials(configuracaoMetrica.getUsuarioJboss(), configuracaoMetrica.getSenhaJboss()));
                CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build();
                clientHttpRequestFactory.setHttpClient(httpClient);
                restTemplate = new RestTemplate(clientHttpRequestFactory);
            }

            String url = "http://localhost:9990/management/subsystem/datasources/data-source/webPublicoJNDI/statistics/pool?include-runtime=true";
            url = url.replace("{parameter}", "%2F");
            URI uri = new URI(url);
            ResponseEntity<String> forEntity = restTemplate.getForEntity(uri, String.class);
            ObjectMapper mapper = new ObjectMapper();
            jbossConnections = mapper.readValue(forEntity.getBody(), JbossConnections.class);

        } catch (Exception e) {
            logger.debug("Erro ao conectar com o Jboss", e);
            FacesUtil.addAtencao("Não foi possível conectar no Jboss '" + e.getMessage() + "'");
        }
    }


    public LinkedHashMap<String, MediaPagina> sortHashMapByValues(Map<String, MediaPagina> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<MediaPagina> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        LinkedHashMap<String, MediaPagina> sortedMap =
            new LinkedHashMap<>();
        Iterator<MediaPagina> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            MediaPagina val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                MediaPagina comp1 = passedMap.get(key);
                MediaPagina comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public void deslogar(UsuarioNodeServer user) {
        Push.sendTo(user.getUsuario(), VerificadorSessoesAtivasUsuario.MATAR_SESSAO);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class JbossConnections {
        @JsonProperty("ActiveCount")
        private String activeCount;
        @JsonProperty("AvailableCount")
        private String availableCount;
        @JsonProperty("AverageBlockingTime")
        private String averageBlockingTime;
        @JsonProperty("AverageCreationTime")
        private String averageCreationTime;
        @JsonProperty("CreatedCount")
        private String createdCount;
        @JsonProperty("DestroyedCount")
        private String destroyedCount;
        @JsonProperty("MaxCreationTime")
        private String maxCreationTime;
        @JsonProperty("MaxUsedCount")
        private String maxUsedCount;
        @JsonProperty("MaxWaitTime")
        private String maxWaitTime;
        @JsonProperty("TimedOut")
        private String timedOut;
        @JsonProperty("TotalBlockingTime")
        private String totalBlockingTime;
        @JsonProperty("TotalCreationTime")
        private String totalCreationTime;

        public JbossConnections() {
        }

        public String getActiveCount() {
            return activeCount;
        }

        public void setActiveCount(String activeCount) {
            this.activeCount = activeCount;
        }

        public String getAvailableCount() {
            return availableCount;
        }

        public void setAvailableCount(String availableCount) {
            this.availableCount = availableCount;
        }

        public String getAverageBlockingTime() {
            return averageBlockingTime;
        }

        public void setAverageBlockingTime(String averageBlockingTime) {
            this.averageBlockingTime = averageBlockingTime;
        }

        public String getAverageCreationTime() {
            return averageCreationTime;
        }

        public void setAverageCreationTime(String averageCreationTime) {
            this.averageCreationTime = averageCreationTime;
        }

        public String getCreatedCount() {
            return createdCount;
        }

        public void setCreatedCount(String createdCount) {
            this.createdCount = createdCount;
        }

        public String getDestroyedCount() {
            return destroyedCount;
        }

        public void setDestroyedCount(String destroyedCount) {
            this.destroyedCount = destroyedCount;
        }

        public String getMaxCreationTime() {
            return maxCreationTime;
        }

        public void setMaxCreationTime(String maxCreationTime) {
            this.maxCreationTime = maxCreationTime;
        }

        public String getMaxUsedCount() {
            return maxUsedCount;
        }

        public void setMaxUsedCount(String maxUsedCount) {
            this.maxUsedCount = maxUsedCount;
        }

        public String getMaxWaitTime() {
            return maxWaitTime;
        }

        public void setMaxWaitTime(String maxWaitTime) {
            this.maxWaitTime = maxWaitTime;
        }

        public String getTimedOut() {
            return timedOut;
        }

        public void setTimedOut(String timedOut) {
            this.timedOut = timedOut;
        }

        public String getTotalBlockingTime() {
            return totalBlockingTime;
        }

        public void setTotalBlockingTime(String totalBlockingTime) {
            this.totalBlockingTime = totalBlockingTime;
        }

        public String getTotalCreationTime() {
            return totalCreationTime;
        }

        public void setTotalCreationTime(String totalCreationTime) {
            this.totalCreationTime = totalCreationTime;
        }
    }


    public class MediaPagina implements Comparable {
        protected Double min;
        protected Double media;
        protected Double max;
        protected List<MetricaSistema> metricaSistemas;


        public MediaPagina() {
            this.min = 0.0;
            this.media = 0.0;
            this.max = 0.0;
            metricaSistemas = Lists.newArrayList();
        }

        public Double getMin() {
            return min;
        }

        public Double getMedia() {
            return media;
        }

        public Double getMax() {
            return max;
        }

        public List<MetricaSistema> getMetricaSistemas() {
            return metricaSistemas;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MediaPagina that = (MediaPagina) o;

            if (min != null ? !min.equals(that.min) : that.min != null) return false;
            if (media != null ? !media.equals(that.media) : that.media != null) return false;
            if (max != null ? !max.equals(that.max) : that.max != null) return false;
            return !(metricaSistemas != null ? !metricaSistemas.equals(that.metricaSistemas) : that.metricaSistemas != null);

        }

        @Override
        public int hashCode() {
            int result = min != null ? min.hashCode() : 0;
            result = 31 * result + (media != null ? media.hashCode() : 0);
            result = 31 * result + (max != null ? max.hashCode() : 0);
            result = 31 * result + (metricaSistemas != null ? metricaSistemas.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(Object o) {
            return ((MediaPagina) o).getMedia().compareTo(this.media);
        }

        @Override
        public String toString() {
            return "MediaPagina{" +
                "min=" + min +
                ", media=" + media +
                ", max=" + max +
                ", metricaSistemas=" + metricaSistemas +
                '}';
        }
    }

    public void alterarStatusGarbageCollector(boolean status) {
        GarbageCollectorService.updateEnviroment(status);
    }

    public void processarGarbageCollectorManual() {
        try {
            GarbageCollectorService.startGarbageColletor(false);
            FacesUtil.addOperacaoRealizada("Garbage Collector realizado com sucesso.");
        } catch (Exception e) {
            logger.error("Erro ao tentar processar garbage collector");
        }
    }

    public String getGarbageCollectorStatus() {
        return GarbageCollectorService.isUnlockedLabs(true) + "";
    }

    public void removerProcessoAssincrono(AssistenteBarraProgresso assistente) {
        singletonAgendamentoTarefas.removeProcesso(assistente);
    }

    public void atualizarDashboarRelatorios() {
        if (dataOperacao == null) {
            FacesUtil.addCampoObrigatorio("O campo Data deve ser informado.");
            return;
        }

        try {
            dashboarWebReportDTO = new DashboarWebReportDTO();
            buscarRelatorios();
            buscarRelatoriosPorRelatorio();
            buscarRelatoriosPorUsuario();
            buscarMaiorTempo();
            buscarTempoMedio();
            montarGraficoAcompanhamentoDiarioRelatorio();
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void buscarTempoMedio() {
        java.time.LocalDateTime dataInicial = getLocalDateTimeDataInicial();
        java.time.LocalDateTime dataFinal = getLocalDateTimeDataFinal();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = getConfiguracaoDeRelatorio().getUrlCompleta("relatorios/tempo-medio");
        url += "&dataInicial=" + dataInicial.toString() + "&dataFinal=" + dataFinal.toString();
        ResponseEntity<String> exchange = new RestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        dashboarWebReportDTO.setTempoMedioAsString(exchange.getBody());
    }

    private void buscarMaiorTempo() {
        java.time.LocalDateTime dataInicial = getLocalDateTimeDataInicial();
        java.time.LocalDateTime dataFinal = getLocalDateTimeDataFinal();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = getConfiguracaoDeRelatorio().getUrlCompleta("relatorios/maior-tempo");
        url += "&dataInicial=" + dataInicial.toString() + "&dataFinal=" + dataFinal.toString();
        ResponseEntity<String> exchange = new RestTemplate()
            .exchange(url, HttpMethod.GET, request, String.class);
        dashboarWebReportDTO.setTempoMaiorAsString(exchange.getBody());
    }

    private void buscarRelatoriosPorUsuario() {
        java.time.LocalDateTime dataInicial = getLocalDateTimeDataInicial();
        java.time.LocalDateTime dataFinal = getLocalDateTimeDataFinal();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = getConfiguracaoDeRelatorio().getUrlCompleta("relatorios/por-usuario");
        url += "&dataInicial=" + dataInicial.toString() + "&dataFinal=" + dataFinal.toString();
        ResponseEntity<List<ReportUsuariosDTO>> exchange = new RestTemplate()
            .exchange(url, HttpMethod.GET, request,
                new ParameterizedTypeReference<List<ReportUsuariosDTO>>() {
                });
        dashboarWebReportDTO.setRelatoriosPorUsuario(exchange.getBody());
        dashboarWebReportDTO.setQuantidadeUsuarios(dashboarWebReportDTO.getRelatoriosPorUsuario().size());
    }

    private void buscarRelatoriosPorRelatorio() {
        java.time.LocalDateTime dataInicial = getLocalDateTimeDataInicial();
        java.time.LocalDateTime dataFinal = getLocalDateTimeDataFinal();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = getConfiguracaoDeRelatorio().getUrlCompleta("relatorios/por-relatorio");
        url += "&page=0&size=10&dataInicial=" + dataInicial.toString() + "&dataFinal=" + dataFinal.toString();
        ResponseEntity<List<ReportRelatoriosDTO>> exchange = new RestTemplate()
            .exchange(url, HttpMethod.GET, request,
                new ParameterizedTypeReference<List<ReportRelatoriosDTO>>() {
                });
        dashboarWebReportDTO.setRelatoriosPorRelatorio(exchange.getBody());
        dashboarWebReportDTO.setQuantidadeRelatorios(dashboarWebReportDTO.getRelatoriosPorRelatorio().size());
    }

    public void alterarQuantidade(Integer quantidade) {
        dashboarWebReportDTO.setQuantidade(quantidade);
        dashboarWebReportDTO.setPagina(1);
        buscarRelatorios();
    }

    public void primeiraPaginaRelatorios() {
        dashboarWebReportDTO.setPagina(1);
        buscarRelatorios();
    }

    public void paginaAnteriorRelatorios() {
        dashboarWebReportDTO.setPagina(dashboarWebReportDTO.getPagina() - 1);
        buscarRelatorios();
    }

    public void proximaPaginaRelatorios() {
        dashboarWebReportDTO.setPagina(dashboarWebReportDTO.getPagina() + 1);
        buscarRelatorios();
    }

    public void ultimaPaginaRelatorios() {
        dashboarWebReportDTO.setPagina(dashboarWebReportDTO.getTotalPaginas());
        buscarRelatorios();
    }

    public java.time.LocalDate getLocalDateDataOperacao() {
        return dataOperacao.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
    }

    public java.time.LocalDateTime getLocalDateTimeDataInicial() {
        java.time.LocalDateTime localDateTime = dataOperacao.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        return localDateTime.withHour(0).withMinute(0).withSecond(0);
    }

    public java.time.LocalDateTime getLocalDateTimeDataFinal() {
        java.time.LocalDateTime localDateTime = dataOperacao.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        return localDateTime.withHour(23).withMinute(59).withSecond(59);
    }

    public void limparCamposRelatorios() {
        dashboarWebReportDTO.setUsuario("");
        dashboarWebReportDTO.setNomeRelatorio("");
        dashboarWebReportDTO.setStatus("");
    }

    public void buscarRelatorios() {
        java.time.LocalDateTime dataInicial = getLocalDateTimeDataInicial();
        java.time.LocalDateTime dataFinal = getLocalDateTimeDataFinal();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = getConfiguracaoDeRelatorio().getUrlCompleta("relatorios");
        url += "&page=" + (dashboarWebReportDTO.getPagina() - 1) +
            "&size=" + dashboarWebReportDTO.getQuantidade() +
            "&dataInicial=" + dataInicial.toString() + "&dataFinal=" + dataFinal.toString();
        url += !Strings.isNullOrEmpty(dashboarWebReportDTO.getUsuario()) ?
            "&usuario=" + dashboarWebReportDTO.getUsuario() : "";
        url += !Strings.isNullOrEmpty(dashboarWebReportDTO.getNomeRelatorio()) ?
            "&nomeRelatorio=" + dashboarWebReportDTO.getNomeRelatorio() : "";
        url += !Strings.isNullOrEmpty(dashboarWebReportDTO.getStatus()) ?
            "&status=" + dashboarWebReportDTO.getStatus() : "";
        ResponseEntity<PageDTO<WebReportRelatorioDTO>> exchange = new RestTemplate()
            .exchange(url, HttpMethod.GET, request,
                new ParameterizedTypeReference<PageDTO<WebReportRelatorioDTO>>() {
                });
        PageDTO<WebReportRelatorioDTO> page = exchange.getBody();
        dashboarWebReportDTO.setTotalPaginas(page.getTotalPages());
        dashboarWebReportDTO.setRelatorios(Arrays.asList(exchange.getBody().getContent()));
    }

    private void montarGraficoAcompanhamentoDiarioRelatorio() {
        LocalDate dataOperacao = getLocalDateDataOperacao();

        graficoAcompanhamentoRelatorioDiario = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = getConfiguracaoDeRelatorio().getUrlCompleta("relatorios/acompanhamento-diario");
        url += "&dataOperacao=" + dataOperacao.toString();
        GraficoAcompanhamentoDiarioDTO grafico = new RestTemplate()
            .exchange(url, HttpMethod.GET, request, GraficoAcompanhamentoDiarioDTO.class).getBody();

        if (grafico.getLabels() == null || grafico.getLabels().length == 0) return;
        graficoAcompanhamentoRelatorioDiario = new CartesianChartModel();
        ChartSeries chartSeries = new ChartSeries("Relatórios");
        for (int i = 0; i < grafico.getLabels().length; i++) {
            chartSeries.set(grafico.getLabels()[i], grafico.getDados()[i]);
        }
        graficoAcompanhamentoRelatorioDiario.addSeries(chartSeries);
    }

    public static void cancelarGeracaoRelatorio(WebReportRelatorioDTO webReportRelatorioDTO, boolean mostrarMensagem) throws IOException {
        if (webReportRelatorioDTO.getThreadId() != null) {
            try {
                RabbitMQService.getService()
                    .basicPublishExchange("webreport.cancel.report.fanout",
                        webReportRelatorioDTO.getUuid().getBytes());
                if(mostrarMensagem) {
                    FacesUtil.addOperacaoRealizada("Solicitação de cancelamento enviada com sucesso.");
                }
            } catch (Exception e) {
                if(mostrarMensagem) {
                    FacesUtil.addErrorGenerico(e);
                } else {
                    throw e;
                }
            }
        }
    }

    public StreamedContent baixarRelatorio(WebReportRelatorioDTO webReportRelatorioDTO) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DashboarWebReportDTO> request = new HttpEntity<>(headers);
        String url = getConfiguracaoDeRelatorio().getUrlCompleta("relatorio/imprimir");
        url += "&uuid=" + webReportRelatorioDTO.getUuid();
        byte[] dados = new RestTemplate().exchange(url, HttpMethod.GET, request, byte[].class).getBody();
        return new DefaultStreamedContent(new ByteArrayInputStream(dados),
            webReportRelatorioDTO.getMimeType(), webReportRelatorioDTO.getNomeArquivo());
    }
}
