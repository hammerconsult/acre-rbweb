package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.softplan.dto.*;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoParcelamento;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.message.RabbitMQService;
import br.com.webpublico.negocios.tributario.LeitorWsConfig;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.itextpdf.io.IOException;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Stateful
public class ComunicaSofPlanFacade implements Serializable {

    private static final String QUEUE_ENVIAR_SOFTPLAN_AUTOMATICO = "queueEnviarSoftPlanAutomatico";
    private static final String QUEUE_ENVIAR_SOFTPLAN_MANUAL = "queueEnviarSoftPlanManual";
    public static final Locale localeBrasil = new Locale("pt_BR_", "pt", "BR");
    public static final FastDateFormat formatterDataSemBarra = FastDateFormat.getInstance("yyyyMMdd", localeBrasil);
    public static final String NOME_DO_ARQUIVO = "DVA_ARQ_4020_";
    public static final BigDecimal CEM = BigDecimal.valueOf(100.00);
    protected static final Logger logger = LoggerFactory.getLogger(ComunicaSofPlanFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private LivroDividaAtivaFacade livroDividaAtivaFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private LeitorWsConfig leitorWsConfig;
    private ConfiguracaoTributario configuracaoTributario;
    private ObjectMapper objectMapper;
    private RabbitMQService rabbitMQService;

    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }

    public RabbitMQService getRabbitMQService() {
        try {
            rabbitMQService = (RabbitMQService) Util.getSpringBeanPeloNome("rabbitMQService");
        } catch (Exception e) {
            logger.error("Erro ao buscar instância do RabbitMQService no ComunicaSofPlanFacade. {}", e.getMessage());
            logger.debug("Detalhes do erro ao buscar instância do RabbitMQService no ComunicaSofPlanFacade.", e);
        }
        return rabbitMQService;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        if (configuracaoTributario == null) {
            configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        }
        return configuracaoTributario;
    }

    public ConsultaDebitoFacade getConsultaDebitoFacade() {
        return consultaDebitoFacade;
    }

    public CertidaoDividaAtivaFacade getCertidaoDividaAtivaFacade() {
        return certidaoDividaAtivaFacade;
    }

    public MoedaFacade getMoedaFacade() {
        return moedaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametrosDividaAtivaFacade getParametrosDividaAtivaFacade() {
        return parametrosDividaAtivaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    @Asynchronous
    public Future alterarCDA(List<CertidaoDividaAtiva> certidoes) {
        for (CertidaoDividaAtiva cda : certidoes) {
            try {
                alterarCDA(cda);
            } catch (Exception e) {
                logger.error("Erro no método alterarCDA. {}", e.getMessage());
                logger.debug("Detalhes do erro no método alterarCDA.", e);
            }
        }
        return new AsyncResult<>(null);
    }

    @Asynchronous
    public Future enviarCDA(List<CertidaoDividaAtiva> certidoes) {
        if (certidoes != null && !certidoes.isEmpty()) {
            AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
            assistente.setDescricaoProcesso("Enviando CDA para procuradoria.");
            assistente.setTotal(certidoes.size());
            comunicarCDA(certidoes, false, false);
            assistente.contar(certidoes.size());
        }
        return new AsyncResult<>(null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void enviarTodasCdasDisponiveis() {
        int inicio = 0;
        ParametrosDividaAtiva parametrosDividaAtiva = getParametrosDividaAtivaFacade()
            .parametrosDividaAtivaPorExercicio(getExercicioAtual());
        List<CertidaoDividaAtiva> certidoes = certidaoDividaAtivaFacade.recuperaCDAValidasSoftPlan(inicio,
            parametrosDividaAtiva.getIdDividas());
        while (certidoes != null && !certidoes.isEmpty()) {
            comunicarCDA(certidoes, false, true);
            inicio = inicio + 500;
            certidoes = certidaoDividaAtivaFacade.recuperaCDAValidasSoftPlan(inicio,
                parametrosDividaAtiva.getIdDividas());
        }
    }

    @Asynchronous
    public void enviarParcelamentosEstornados(UsuarioSistema usuarioSistema) {
        int inicio = 0;
        Exercicio exercicio = getExercicioAtual();
        ParametrosDividaAtiva parametrosDividaAtiva = getParametrosDividaAtivaFacade()
            .parametrosDividaAtivaPorExercicio(exercicio);

        List<ProcessoParcelamento> parcelamentos = certidaoDividaAtivaFacade.recuperaParcelamentosEstornadosSoftPlan(inicio, parametrosDividaAtiva.getIdDividas());
        int enviados = 0;
        while (parcelamentos != null && !parcelamentos.isEmpty()) {
            AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
            assistente.setDescricaoProcesso("Enviando parcelamento estornados para procuradoria.");
            assistente.setUsuarioSistema(usuarioSistema);
            assistente.setTotal(parcelamentos.size());
            for (ProcessoParcelamento parcelamento : parcelamentos) {
                alterarSituacaoParcelamento(parcelamento);
                enviados++;
                assistente.conta();
            }
            inicio = inicio + 500;
            parcelamentos = certidaoDividaAtivaFacade.recuperaParcelamentosEstornadosSoftPlan(inicio, parametrosDividaAtiva.getIdDividas());
        }

    }

    private Exercicio getExercicioAtual() {
        Calendar c = GregorianCalendar.getInstance();
        return certidaoDividaAtivaFacade.getExercicioFacade().getExercicioPorAno(c.get(Calendar.YEAR));
    }

    @Asynchronous
    public void enviarTodosParcelamentos(UsuarioSistema usuarioSistema) {
        int inicio = 0;
        Exercicio exercicio = getExercicioAtual();
        ParametrosDividaAtiva parametrosDividaAtiva = getParametrosDividaAtivaFacade()
            .parametrosDividaAtivaPorExercicio(exercicio);

        List<ProcessoParcelamento> parcelamentos = certidaoDividaAtivaFacade.recuperaParcelamentosValidosSoftPlan(inicio, parametrosDividaAtiva.getIdDividas());
        while (parcelamentos != null && !parcelamentos.isEmpty()) {
            AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
            assistente.setDescricaoProcesso("Enviando todos parcelamentos para procuradoria.");
            assistente.setUsuarioSistema(usuarioSistema);
            assistente.setTotal(parcelamentos.size());
            for (ProcessoParcelamento processoParcelamento : parcelamentos) {
                processoParcelamentoFacade.comunicaInsercaoAlteracaoParcelamento(processoParcelamento);
                assistente.conta();
            }
            inicio = inicio + 500;
            parcelamentos = certidaoDividaAtivaFacade.recuperaParcelamentosValidosSoftPlan(inicio, parametrosDividaAtiva.getIdDividas());
        }
    }

    @Asynchronous
    public Future alterarSituacaoParcelamento(ProcessoParcelamento parcelamento) {
        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Alterando situação do parcelamento para procuradoria.");
        assistente.setTotal(1);
        try {
            alterarParcelamento(parcelamento);
        } catch (Exception e) {
            logger.error("Erro ao alterar situação do parcelamento na procuradoria. {}", e.getMessage());
            logger.debug("Detalhes do erro ao alterar situação do parcelamento na procuradoria.", e);
        }
        assistente.conta();
        return new AsyncResult<>(null);
    }

    private void alterarParcelamento(ProcessoParcelamento parcelamento) {
        CertidaoDividaAtiva certidao = null;
        List<CertidaoDividaAtiva> certidoes = Lists.newArrayList();

        List<ItemCertidaoDividaAtiva> itensDoParcelamento = certidaoDividaAtivaFacade.buscarItensCertidaoDoParcelamento(parcelamento);
        for (ItemCertidaoDividaAtiva item : itensDoParcelamento) {
            certidoes.add(item.getCertidao());
        }
        if (!certidoes.isEmpty()) {
            certidao = certidoes.get(0);
            try {
                logger.debug("Vai alterar situação do parcelamento na procuradoria. Parcelamento [{}]",
                    parcelamento);

                AlteracaoParcelamentoSoftPlanDTO alteracaoParcelamentoSoftPlan =
                    new AlteracaoParcelamentoSoftPlanDTO(certidao.getId(), parcelamento.getId());

                enviarCertidoes(Lists.newArrayList(alteracaoParcelamentoSoftPlan), false);
            } catch (Exception e) {
                logger.error("Erro no método alterarParcelamento, na alteração de situação do parcelamento. {}", e.getMessage());
                logger.debug("Detalhes do erro no método alterarParcelamento, na alteração de situação do parcelamento.", e);
            }
            try {
                comunicarCDA(certidoes, false, false);
            } catch (Exception e) {
                logger.error("Erro no método alterarParcelamento, na comunicação da cda. {}", e.getMessage());
                logger.debug("Detalhes do erro no método alterarParcelamento, na comunicação da cda.", e);
            }
        }
    }

    public void comunicarParcelamento(ProcessoParcelamento parcelamento, boolean reenviar) {
        try {
            logger.debug("Vai enviar o parcelamento " + parcelamento.getNumeroCompostoComAno() + " - " + parcelamento.getSituacaoParcelamento());
            CertidaoDividaAtiva certidao = null;
            String xml = "";
            if (!reenviar && isParcelamentoComunicado(parcelamento)) {
                return;
            }
            List<ItemCertidaoDividaAtiva> itensDoParcelamento = certidaoDividaAtivaFacade.buscarItensCertidaoDoParcelamento(parcelamento);
            certidao = itensDoParcelamento.get(0).getCertidao();
            InclusaoParcelamentoSoftPlanDTO inclusaoParcelamentoSoftPlan =
                new InclusaoParcelamentoSoftPlanDTO(certidao.getId(), parcelamento.getId());
            enviarCertidoes(Lists.newArrayList(inclusaoParcelamentoSoftPlan), false);
        } catch (Exception e) {
            logger.error("Erro no método comunicarParcelamento. {}", e.getMessage());
            logger.debug("Detalhe do erro no método comunicarParcelamento. {}", e);
        }
    }

    public void alterarCDA(CertidaoDividaAtiva cda) {
        AlteracaoCDASoftplanDTO alteracaoCDA = new AlteracaoCDASoftplanDTO(cda.getId(), null);
        enviarCertidoes(Lists.newArrayList(alteracaoCDA), false);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void comunicarCDA(List<CertidaoDividaAtiva> cdas, boolean comunicaParcelamento, boolean envioAutomatico) {
        try {
            List<ServicoSoftplanDTO> servicos = Lists.newArrayList();
            for (CertidaoDividaAtiva cda : cdas) {
                cda = em.find(CertidaoDividaAtiva.class, cda.getId());

                servicos.add(new InclusaoCDASoftplanDTO(cda.getId(), null));

                if (SituacaoCertidaoDA.QUITADA.equals(cda.getSituacaoCertidaoDA())) {
                    servicos.add(new AlteracaoCDASoftplanDTO(cda.getId(), null));
                }

                if (comunicaParcelamento) {
                    List<Long> idsParcelas = certidaoDividaAtivaFacade.recuperarIdsParcelasPorCertidaoDividaAtiva(cda);
                    List<ProcessoParcelamento> parcelamentos = Lists.newArrayList();
                    List<ProcessoParcelamento> parcelamentosParcela = processoParcelamentoFacade.buscarTodosParcelamentosComParcelaPaga(idsParcelas);
                    for (ProcessoParcelamento processoParcelamento : parcelamentosParcela) {
                        if (!parcelamentos.contains(processoParcelamento)) {
                            parcelamentos.add(processoParcelamento);
                        }
                    }
                    for (ProcessoParcelamento parcelamento : parcelamentos) {
                        servicos.add(new InclusaoParcelamentoSoftPlanDTO(null, parcelamento.getId()));
                        if (!parcelamento.getSituacaoParcelamento().equals(SituacaoParcelamento.FINALIZADO)) {
                            servicos.add(new AlteracaoParcelamentoSoftPlanDTO(null, parcelamento.getId()));
                        }
                    }
                }
            }
            enviarCertidoes(servicos, envioAutomatico);
        } catch (Exception e) {
            logger.error("Erro no método comunicarCDA. {}", e.getMessage());
            logger.debug("Detalhes do erro no método comunicarCDA.", e);
        }
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public StreamedContent geraAquivoSofPlan(AssistenteBarraProgresso assistente, List<CertidaoDividaAtiva> certidoes) throws Exception {
        assistente.setTotal(certidoes.size());
        Date hoje = new Date();
        Integer contadorParcelamento = 0;
        File arquivoSoftPlan = File.createTempFile("arquivoSoftPlan", ".tmp");
        int subListSize = certidoes.size() > 10 ? certidoes.size() / 10 : certidoes.size();
        List<List<CertidaoDividaAtiva>> certidoesDivididas = Lists.partition(certidoes, subListSize);
        List<CompletableFuture<File>> futureFiles = Lists.newArrayList();
        assistente.removerProcessoDoAcompanhamento();
        for (int i = 0; i < certidoesDivididas.size(); i++) {
            int finalI = i;
            CompletableFuture<File> future = AsyncExecutor.getInstance().execute(assistente, () -> {
                try {
                    assistente.setDescricaoProcesso(assistente.getDescricaoProcesso() + "Parte " + finalI + "/" + subListSize);
                    return certidaoDividaAtivaFacade.geraLinhasCDA(assistente, certidoesDivididas.get(finalI));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            futureFiles.add(future);
        }
        boolean todasTerminadas;
        do {
            todasTerminadas = true;
            for (Future<File> future : futureFiles) {
                if (!future.isDone() && !future.isCancelled()) {
                    todasTerminadas = false;
                    break;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } while (!todasTerminadas);
        List<File> files = Lists.newArrayList();
        for (Future<File> future : futureFiles) {
            files.add(future.get());
        }
        try (FileOutputStream outputStream = new FileOutputStream(arquivoSoftPlan)) {
            outputStream.write((gerarCabecalho(hoje) + pulelinha()).getBytes());
            for (File file : files) {
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[10 * 1024];
                    for (int length; (length = inputStream.read(buffer)) != -1; ) {
                        outputStream.write(buffer, 0, length);
                    }
                }
            }
            outputStream.write((gerarRodape(contadorParcelamento, certidoes) + pulelinha()).getBytes());
        }
        StreamedContent streamedContent = gerarZip(arquivoSoftPlan);
        arquivoSoftPlan.delete();
        for (File file : files) {
            file.delete();
        }
        return streamedContent;
    }

    private String gerarRodape(Integer contadorParcelamento, List<CertidaoDividaAtiva> certidoes) {
        StringBuilder rodape = new StringBuilder("");
        rodape.append("9");//fixo de acordo com o layout
        rodape.append(StringUtil.cortarOuCompletarEsquerda(String.valueOf(certidoes.size() + contadorParcelamento), 7, "0"));
        rodape.append(StringUtil.cortarOuCompletarEsquerda(String.valueOf(certidoes.size()), 7, "0"));
        rodape.append(StringUtil.cortarOuCompletarEsquerda(String.valueOf(contadorParcelamento), 7, "0"));
        rodape.append(StringUtil.cortarOuCompletarEsquerda("1", 7, "0"));
        return rodape.toString();
    }

    private String gerarCabecalho(Date hoje) {
        String nomeArquivo = NOME_DO_ARQUIVO + formatterDataSemBarra.format(hoje);
        StringBuilder cabacalho = new StringBuilder("");
        cabacalho.append("0");//fixo de acordo com o layout
        cabacalho.append(ComunicaSofPlanFacade.formatterDataSemBarra.format(hoje));
        cabacalho.append(nomeArquivo);
        cabacalho.append("0000001");//fixo de acordo com o layout
        return cabacalho.toString();
    }

    private String pulelinha() {
        return System.getProperty("line.separator");
    }

    private StreamedContent gerarZip(File file) throws Exception {
        String nome = NOME_DO_ARQUIVO + formatterDataSemBarra.format(new Date());
        File arquivo = File.createTempFile("arquivo", "zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(arquivo));
        String fileNameInsideZip = nome + ".txt";
        ZipEntry e = new ZipEntry(fileNameInsideZip);
        out.putNextEntry(e);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[10 * 1024];
            for (int length; (length = inputStream.read(buffer)) != -1; ) {
                out.write(buffer, 0, length);
            }
        }
        out.closeEntry();
        out.flush();
        out.close();
        FileInputStream fis = new FileInputStream(arquivo);
        DefaultStreamedContent defaultStreamedContent = new DefaultStreamedContent(fis, "application/zip", nome + ".zip");
        return defaultStreamedContent;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void geraArquivoCdasDisponiveis(AssistenteBarraProgresso assistente) {
        List<CertidaoDividaAtiva> cdas = certidaoDividaAtivaFacade.buscarCDAsParaArquivoSoftPlan();
        try {
            String lineSeparator = System.getProperty("line.separator");
            System.setProperty("line.separator", "\r\n");
            StreamedContent arquivo = geraAquivoSofPlan(assistente, cdas);
            enviaArquivoFTP(arquivo);
            System.setProperty("line.separator", lineSeparator);
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    public void enviaArquivoFTP(StreamedContent arquivo) {
        String nomeArquivo = arquivo.getName();
        FTPClient ftp = new FTPClient();
        try {
            ConfiguracaoWebService configuracaoWs = leitorWsConfig
                .getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.ARQUIVO_CDA,
                    sistemaFacade.getUsuarioBancoDeDados());
            if (configuracaoWs != null) {
                try {
                    ftp.connect(configuracaoWs.getUrl(), 21);
                } catch (Exception ex) {
                    ftp = new FTPSClient(false);
                    ftp.connect(configuracaoWs.getUrl(), 21);
                }
                if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    ftp.login(configuracaoWs.getUsuario(), configuracaoWs.getSenha());
                    logger.debug("Conectou com o servidor de FTP");
                } else {
                    ftp.disconnect();
                    logger.debug("Conexão recusada pelo servidor de FTP");
                }
                logger.debug("Diretório " + configuracaoWs.getDetalhe());
                boolean b = ftp.changeWorkingDirectory(configuracaoWs.getDetalhe());
                logger.debug("Encontrou o diretório dentro do servidor de FTP " + b);
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                logger.debug("Enviando arquivo " + nomeArquivo + "...");
                if (ftp.storeFile(nomeArquivo, arquivo.getStream())) {
                    logger.debug("Arquivo " + nomeArquivo + " enviado com sucesso!");
                } else {
                    logger.debug("Arquivo " + nomeArquivo + " não foi enviado!");
                    logger.debug("Vai tentar com enterLocalPassiveMode!");
                    ftp.enterLocalPassiveMode();
                    if (ftp.storeFile(nomeArquivo, arquivo.getStream())) {
                        logger.debug("Arquivo " + nomeArquivo + " enviado com sucesso!");
                    } else {
                        logger.debug("Arquivo " + nomeArquivo + " não foi enviado!");
                    }
                }
                ftp.logout();
                ftp.disconnect();
                logger.debug("Encerrando a conexão FTP");
            } else {
                logger.debug("Não foi encontrado configuração de FTP para envio!");
            }
        } catch (Exception e) {
            logger.error("Ocorreu um erro: {}", e);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public void retificaCDA() {
        logger.debug("Retificação de CDA via Processo de agendamento");
        List<Pessoa> pessoas = certidaoDividaAtivaFacade.recuperaPessoasComRetificacaoAindaNaoEnvidas();
        logger.debug("Vai retificar " + pessoas.size() + " Pessoas");
        for (Pessoa pessoa : pessoas) {
            List<CertidaoDividaAtiva> certidoes = certidaoDividaAtivaFacade.listaCertidaoDividaAtivaPorPessoa(pessoa);
            enviarCDA(certidoes);
        }
        logger.debug("Terminou a retificação de  " + pessoas.size() + " Pessoas");
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future enviarCDA(AssistenteBarraProgresso assistenteBarraProgresso, String sql, boolean consideraParcelamento) {
        assistenteBarraProgresso.setTotal(certidaoDividaAtivaFacade.contarCDABySQL(sql));
        int inicio = 0;
        int max = 100;
        List<CertidaoDividaAtiva> certidoes = certidaoDividaAtivaFacade.buscarCDABySQL(sql, inicio, max);
        while (certidoes != null && !certidoes.isEmpty()) {
            List<CertidaoDividaAtiva> paraEnviar = Lists.newArrayList();
            for (CertidaoDividaAtiva certidao : certidoes) {
                paraEnviar.add(certidao);
                assistenteBarraProgresso.conta();
                if (paraEnviar.size() == 50) {
                    comunicarCDA(paraEnviar, consideraParcelamento, false);
                    paraEnviar = Lists.newArrayList();
                }
            }
            if (!paraEnviar.isEmpty()) {
                comunicarCDA(paraEnviar, consideraParcelamento, false);
            }
            inicio = inicio + max;
            logger.debug("Já Enviou " + assistenteBarraProgresso.getCalculados());
            certidoes = certidaoDividaAtivaFacade.buscarCDABySQL(sql, inicio, max);
        }
        logger.debug("Acabou");
        return new AsyncResult<>(null);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future enviarParcelamento(AssistenteBarraProgresso assistenteBarraProgresso, String sql) {
        assistenteBarraProgresso.setTotal(certidaoDividaAtivaFacade.contarParcelamentoBySQL(sql));
        int inicio = 0;
        int max = 1000;
        List<ProcessoParcelamento> parcelamentos = certidaoDividaAtivaFacade.buscarParcelamentoBySQL(sql, inicio, max);
        int total = 0;
        while (parcelamentos != null && !parcelamentos.isEmpty()) {
            List<ServicoSoftplanDTO> servicos = Lists.newArrayList();
            for (ProcessoParcelamento parcelamento : parcelamentos) {
                servicos.add(new InclusaoParcelamentoSoftPlanDTO(null, parcelamento.getId()));

                if (!parcelamento.getSituacaoParcelamento().equals(SituacaoParcelamento.FINALIZADO)) {
                    servicos.add(new AlteracaoParcelamentoSoftPlanDTO(null, parcelamento.getId()));
                }

                if (servicos.size() == 50) {
                    enviarCertidoes(servicos, false);
                    servicos = Lists.newArrayList();
                }
                assistenteBarraProgresso.conta();
                total++;
            }
            if (!servicos.isEmpty()) {
                enviarCertidoes(servicos, false);
            }
            inicio = inicio + max;
            parcelamentos = certidaoDividaAtivaFacade.buscarParcelamentoBySQL(sql, inicio, max);
        }
        logger.debug("Total de parcelamentos enviados: " + total);
        return new AsyncResult<>(null);
    }

    public boolean isParcelamentoComunicado(ProcessoParcelamento parcelamento) {
        Query q = em.createNativeQuery("select id from COMUNICACAOSOFTPLAN " +
            " where SERVICEID = 'ParcelamentoConcedido' " +
            " and CODIGORESPOSTA in ('00','11') " +
            " and processoparcelamento_id = :parcelamento");
        q.setParameter("parcelamento", parcelamento.getId());
        boolean existe = !q.getResultList().isEmpty();
        logger.debug("Parcelamento " + (existe ? "Ja" : "Nao") + " inclusa na base da procuradoria");
        return existe;
    }

    public enum ServiceId {
        INCLUSAO(Constants.incRetCDA, "Inclusão de CDA", "/certida-divida-ativa/inclusao/"),
        ALTERACAO(Constants.alteracaoSituacao, "Alteração de CDA", "/certida-divida-ativa/alteracao/"),
        RETIFICACAO(Constants.retificacaoCDA, "Retificação de CDA", "/certida-divida-ativa/retificacao/"),
        INCLUSAO_PARCELAMENTO(Constants.parcelamentoConcedido, "Inclusão de Parcelamento", "/certida-divida-ativa/inclusao-parcelamento/"),
        ALTERACAO_PARCELAMENTO(Constants.alteracaoSituacaoParcelamento, "Alteração de Parcelamento", "/certida-divida-ativa/alteracao-parcelamento/");
        public String id;
        public String descricao;
        public String link;

        ServiceId(String id, String descricao, String link) {
            this.id = id;
            this.descricao = descricao;
            this.link = link;
        }

        public static ServiceId getPorValor(String valor) {
            switch (valor) {
                case "IncRetCDA":
                    return ServiceId.INCLUSAO;
                case "AlteracaoSituacao":
                    return ServiceId.ALTERACAO;
                case "ParcelamentoConcedido":
                    return ServiceId.INCLUSAO_PARCELAMENTO;
                case "AlteracaoSituacaoParcelamento":
                    return ServiceId.ALTERACAO_PARCELAMENTO;
                case "RetificacaoCDA":
                    return ServiceId.RETIFICACAO;
            }
            return null;
        }

        public String getDescricao() {
            return descricao;
        }

        public static class Constants {
            public static final String incRetCDA = "IncRetCDA";
            public static final String alteracaoSituacao = "AlteracaoSituacao";
            public static final String retificacaoCDA = "RetificacaoCDA";
            public static final String parcelamentoConcedido = "ParcelamentoConcedido";
            public static final String alteracaoSituacaoParcelamento = "AlteracaoSituacaoParcelamento";
        }
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void enviarCertidoes(List<ServicoSoftplanDTO> servicos, boolean envioAutomatico) {
        try {
            logger.debug("Enviando serviços a procuradoria. Total de serviços [{}]", servicos.size());
            for (ServicoSoftplanDTO servico : servicos) {
                enviarRabbitMq(servico, envioAutomatico);
            }
        } catch (Exception e) {
            logger.error("Erro no envio de serviço a procuradoria. {}", e.getMessage());
            logger.debug("Detalhes do erro no envio de serviço a procuradoria.", e);
        }
    }

    private void enviarRabbitMq(ServicoSoftplanDTO servico, boolean envioAutomatico) throws java.io.IOException {
        getRabbitMQService().basicPublish(envioAutomatico ? QUEUE_ENVIAR_SOFTPLAN_AUTOMATICO : QUEUE_ENVIAR_SOFTPLAN_MANUAL,
            getObjectMapper().writeValueAsBytes(servico));
    }
}
