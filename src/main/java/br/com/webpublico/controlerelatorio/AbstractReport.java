package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ExportPDFMonitor;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.LeitorPersistenceXML;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static br.com.webpublico.controle.Web.getSessionMap;

public class AbstractReport implements Serializable {

    public static final String RECURSOS_HUMANOS = "Recursos Humanos";
    public static final String TRIBUTARIO = "Tributário";
    public static final String CONTABIL = "Contábil";
    public static final String ADMINISTRATIVO = "Administrativo";
    protected static final Logger logger = LoggerFactory.getLogger(AbstractReport.class);
    public final String NOME_IMAGEM_BRASAO_PREFEITURA = "Brasao_de_Rio_Branco.gif";
    protected Boolean geraNoDialog = false;
    protected Boolean fazDownload = false;
    private ExercicioFacade exercicioFacade;
    private SistemaFacade sistemaFacade;
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private ConfiguracaoCabecalhoFacade configuracaoCabecalhoFacade;
    private ArquivoFacade arquivoFacade;
    private PortalTransparenciaFacade portalTransparenciaFacade;

    public static AbstractReport getAbstractReport() {
        return new AbstractReport();
    }

    @Deprecated
    /*Usar  AbstractReport.getAbstractReport()*/
    public AbstractReport() {
        init();
    }

    @PostConstruct
    public void init() {
        inicializarFacades();
    }


    public Connection recuperaConexaoJDBC() {
        Connection conexao = null;
        try {
            logger.debug("Conexão JDBC recuperada ... garanta seu 'encerramento' ");
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(LeitorPersistenceXML.getInstance().getNomeDataSource());
            conexao = ds.getConnection();
            return conexao;
        } catch (SQLException | NamingException ex) {
            logger.error("Erro: ", ex);
            return conexao;
        }
    }

    public static void poeRelatorioNaSessao(byte[] bytes) {
        InputStream teste = new ByteArrayInputStream(bytes);
        StreamedContent relatorio = new DefaultStreamedContent(teste, "application/pdf", "Demonstrativo");
        Web.getSessionMap().put("relatorio", relatorio);
        atualizaComponentesDoDialog();
    }

    public static ByteArrayOutputStream gerarReport(HashMap parametros, String nomeReport, JRBeanCollectionDataSource dataSource) throws JRException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(nomeReport, parametros, dataSource);
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bytes);
        exporter.exportReport();
        return bytes;
    }

    public static ByteArrayOutputStream gerarReport(HashMap parametros, String nomeReport, Connection conn) throws JRException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(nomeReport, parametros, conn);
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, bytes);
        exporter.exportReport();
        return bytes;
    }

    public static void poeRelatorioNaSessao(byte[] bytes, String nomeArquivo) {
        InputStream teste = new ByteArrayInputStream(bytes);
        if (nomeArquivo.contains(".")) {
            int corte = nomeArquivo.indexOf(".");
            nomeArquivo = nomeArquivo.substring(0, corte);
        }
        StreamedContent relatorio = new DefaultStreamedContent(teste, "application/pdf", nomeArquivo);
        Web.getSessionMap().put("relatorio", relatorio);
        atualizaComponentesDoDialog();
    }

    private static void atualizaComponentesDoDialog() {
        FacesUtil.atualizarComponente("FormularioRelatorio");
        FacesUtil.executaJavaScript("mostraRelatorio()");
        FacesUtil.executaJavaScript("ajustaImpressaoRelatorio()");
    }

    private void inicializarFacades() {
        try {
            exercicioFacade = (ExercicioFacade) new InitialContext().lookup("java:module/ExercicioFacade");
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
            unidadeOrganizacionalFacade = (UnidadeOrganizacionalFacade) new InitialContext().lookup("java:module/UnidadeOrganizacionalFacade");
            configuracaoCabecalhoFacade = (ConfiguracaoCabecalhoFacade) new InitialContext().lookup("java:module/ConfiguracaoCabecalhoFacade");
            arquivoFacade = (ArquivoFacade) new InitialContext().lookup("java:module/ArquivoFacade");
            portalTransparenciaFacade = (PortalTransparenciaFacade) new InitialContext().lookup("java:module/PortalTransparenciaFacade");
        } catch (Exception e) {
            logger.error("Erro ao injetar os facades via lookup no AbstractReport {}", e);
        }
    }


    public String getCaminho(String path) {
        FacesContext facesContext = getFacesContext();
        String caminho = ((ServletContext) facesContext.getExternalContext().getContext()).getRealPath(path);
        caminho += "/";
        return caminho;
    }

    public String getCaminho() {
        return getCaminho("/WEB-INF/report/");
    }

    public String getCaminhoImagem() {
        return getCaminho("/img/");
    }

    public String getCaminhoSubReport() {
        return ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF") + "/report/";
    }

    public void gerarRelatorio(String arquivoJasper, HashMap parametros) throws JRException, IOException {
        gerarRelatorioAlterandoNomeArquivo(arquivoJasper, arquivoJasper, parametros);
    }

    private JasperReport compilarJrxml(String nomeJrxml) throws JRException {
        return JasperCompileManager.compileReport(getClass().getResourceAsStream("/br/com/webpublico/report/" + nomeJrxml));
    }

    public void gerarRelatorioAlterandoNomeArquivo(String nomeArquivo, String arquivoJasper, HashMap parametros) throws JRException, IOException {
        final Connection conn = recuperaConexaoJDBC();
        try {
            definirLocaleAndPerfilApp(parametros);
            gerarRelatorio(nomeArquivo, arquivoJasper, parametros, conn);
        } catch (Exception e) {
            logger.error("Erro: ", e);
        } finally {
            fecharConnection(conn);
        }
    }

    public ServletContext getServletContext() {
        FacesContext facesContext = getFacesContext();
        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
        return scontext;
    }

    private FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    public void gerarRelatorioVariosEmUm(String nome, Map<String, Map<String, Object>> parametros) throws JRException, IOException {
        List jpList = new ArrayList();
        Connection connection = recuperaConexaoJDBC();
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        try {
            for (String arquivo : parametros.keySet()) {
                definirLocaleAndPerfilApp(parametros.get(arquivo));
                jpList.add(JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivo), parametros.get(arquivo), connection));
            }
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jpList);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
            exporter.exportReport();

            if (getSistemaFacadeSemInjetar().isPerfilDev()) {
                ByteArrayOutputStream novosDadosByte = adicionarTarjaPerfilAplicacao(dadosByte);
                if (novosDadosByte != null) dadosByte = novosDadosByte;
            }
        } catch (Exception e) {
            logger.error("Não foi possível colocar a tarja de perfil da app: ", e);
        } finally {
            fecharConnection(connection);
        }
        escreveNoResponse(nome, dadosByte.toByteArray());
    }

    public ByteArrayOutputStream getRelatorioEmByteArrayOutputStream(String caminho, String caminhoImg, Map<String, HashMap<String, Object>> parametros) throws JRException, IOException {
        List jpList = new ArrayList();
        Connection connection = recuperaConexaoJDBC();
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        try {
            for (String arquivo : parametros.keySet()) {
                definirLocaleAndPerfilApp(parametros.get(arquivo));
                jpList.add(JasperFillManager.fillReport(caminho + arquivo, parametros.get(arquivo), connection));
            }
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jpList);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
            exporter.exportReport();
            if (getSistemaFacadeSemInjetar().isPerfilDev()) {
                ByteArrayOutputStream novosDadosByte = adicionarTarjaPerfilAplicacao(dadosByte, caminhoImg);
                if (novosDadosByte != null) dadosByte = novosDadosByte;
            }
        } catch (Exception e) {
            logger.trace("Não foi possível colocar a tarja de perfil da app " + e.getMessage());
        } finally {
            fecharConnection(connection);
        }
        return dadosByte;
    }

    public ByteArrayOutputStream getRelatorioEmByteArrayOutputStreamEmCollection(String caminho, String caminhoImg, Map<String, HashMap<String, Object>> parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        List jpList = new ArrayList();
        for (String arquivo : parametros.keySet()) {
            definirLocaleAndPerfilApp(parametros.get(arquivo));
            jpList.add(JasperFillManager.fillReport(caminho + arquivo, parametros.get(arquivo), jrbc));
        }
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jpList);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        try {
            if (getSistemaFacadeSemInjetar().isPerfilDev()) {
                ByteArrayOutputStream novosDadosByte = adicionarTarjaPerfilAplicacao(dadosByte, caminhoImg);
                if (novosDadosByte != null) dadosByte = novosDadosByte;
            }
        } catch (Exception e) {
            logger.trace("Não foi possível colocar a tarja de perfil da app " + e.getMessage());
        }
        return dadosByte;
    }

    public void gerarRelatorio(String arquivoJasper, HashMap parametros, Connection con)
        throws JRException, IOException {
        gerarRelatorio(arquivoJasper, arquivoJasper, parametros, con);
    }

    public byte[] gerarRelatorio(String nomeArquivo, String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException {
        definirLocaleAndPerfilApp(parametros);
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;

        JasperPrint jasperPrint = JasperFillManager.fillReport(subReport + arquivoJasper, parametros, jrbc);
        return preparaExportaReport(jasperPrint).toByteArray();
    }

    public void gerarRelatorio(String nomeArquivo, String arquivoJasper, HashMap parametros, Connection con)
        throws JRException, IOException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, con);
        escreveNoResponse(nomeArquivo, preparaExportaReport(jasperPrint).toByteArray());
    }

    public void gerarRelatorio(JasperReport jasperReport, HashMap parametros) throws JRException, IOException {
        Connection connection = recuperaConexaoJDBC();
        try {
            definirLocaleAndPerfilApp(parametros);
            atribuirConfiguracaoCabecalho(parametros);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);
            escreveNoResponse(jasperPrint.getName(), preparaExportaReport(jasperPrint).toByteArray());
        } finally {
            fecharConnection(connection);
        }
    }

    public void gerarRelatorio(JasperPrint jasperPrint) throws JRException, IOException {
        escreveNoResponse((String) jasperPrint.getName(), (byte[]) preparaExportaReport(jasperPrint).toByteArray());
    }

    public void gerarRelatorioComDadosEmCollection(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        escreveNoResponse((String) arquivoJasper, (byte[]) preparaExportaReport(jasperPrint).toByteArray());
    }

    public void gerarRelatorioComDadosEmCollectionSemCabecalhoPadrao(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        definirLocaleAndPerfilApp(parametros);
        JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        escreveNoResponse((String) arquivoJasper, (byte[]) preparaExportaReport(jasperPrint).toByteArray());
    }

    public void gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(String nomeArquivo, String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        escreveNoResponse((String) nomeArquivo, (byte[]) preparaExportaReport(jasperPrint).toByteArray());
    }

    public ByteArrayOutputStream preparaExportaReport(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        try {
            if (getSistemaFacadeSemInjetar().isPerfilDev()) {
                ByteArrayOutputStream novosDadosByte = adicionarTarjaPerfilAplicacao(dadosByte);
                if (novosDadosByte != null) return novosDadosByte;
            }
        } catch (Exception e) {
            logger.trace("Não foi possível colocar a tarja de perfil da app " + e.getMessage());
        }
        return dadosByte;
    }

    protected ByteArrayOutputStream adicionarTarjaPerfilAplicacao(ByteArrayOutputStream dadosByte) {
        return adicionarTarjaPerfilAplicacao(dadosByte, getCaminho("/img/"));
    }

    protected ByteArrayOutputStream adicionarTarjaPerfilAplicacao(ByteArrayOutputStream dadosByte, String caminhoImg) {
        try {
            ByteArrayOutputStream novosDadosByte = new ByteArrayOutputStream();
            PdfReader reader = new PdfReader(dadosByte.toByteArray());
            PdfStamper pdfStamper = new PdfStamper(reader, novosDadosByte);
            Image image = Image.getInstance(caminhoImg + "ambiente_homologacao.png");
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                Rectangle pageRectangle = reader.getPageSizeWithRotation(i);
                float width = pageRectangle.getWidth();
                float height = pageRectangle.getHeight();
                PdfContentByte content = pdfStamper.getUnderContent(i);
                image.setAbsolutePosition(width / 2 - image.getWidth() / 2,
                    height / 2 - image.getHeight() / 2);
                content.addImage(image);
            }
            pdfStamper.close();
            return novosDadosByte;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void definirLocaleAndPerfilApp(Map parametros) {
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        try {
            SistemaFacade sistemaFacade = getSistemaFacadeSemInjetar();
            parametros.put("PERFIL_APP", sistemaFacade.getPerfilAplicacao().name());
        } catch (Exception e) {
            logger.trace("Erro ao definir locale e perfil da app no report {}", e);
        }
    }

    public void definirLocaleAndPerfilApp(RelatorioDTO dto) {
        dto.adicionarParametro(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        try {
            SistemaFacade sistemaFacade = getSistemaFacadeSemInjetar();
            dto.adicionarParametro("PERFIL_APP", sistemaFacade.getPerfilAplicacao().name());
        } catch (Exception e) {
            logger.trace("Erro ao definir locale e perfil da app no report {}", e);
        }
    }

    protected void atribuirConfiguracaoCabecalho(HashMap parametros) {
        try {
            atribuirConfiguracaoCabecalho(parametros, null);
        } catch (Exception e) {
            logger.error("Erro ao atribuir CONFIGURACAO_CABECALHO ative o trace para ver os detalhes");
            logger.trace("CONFIGURACAO_CABECALHO::", e);
        }
    }

    protected void atribuirConfiguracaoCabecalho(HashMap parametros, UnidadeOrganizacional unidadeOrcamentariaCorrente) {
        try {
            if (unidadeOrcamentariaCorrente != null) {
                parametros.put("CONFIGURACAO_CABECALHO", unidadeOrcamentariaCorrente);
            } else {
                parametros.put("CONFIGURACAO_CABECALHO", configuracaoCabecalhoFacade.buscarConfiguracaoCabecalhoPorUnidade(sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente()));
            }
        } catch (Exception e) {
            logger.error("Erro ao atribuir CONFIGURACAO_CABECALHO {}", e);
        }
    }

    protected void atribuirConfiguracaoCabecalho(RelatorioDTO dto) {
        try {
            dto.adicionarParametro("CONFIGURACAO_CABECALHO", configuracaoCabecalhoFacade.buscarConfiguracaoCabecalhoPorUnidade(sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente()));
        } catch (Exception e) {
            logger.error("Erro ao atribuir CONFIGURACAO_CABECALHO {}", e);
        }
    }


    public SistemaFacade getSistemaFacadeSemInjetar() throws NamingException {
        return (SistemaFacade) new InitialContext()
            .lookup("java:module/SistemaFacade");
    }

    public void escreveNoResponse(String arquivoJasper, byte[] bytes) throws IOException {
        if (bytes != null && bytes.length > 0) {
            if (geraNoDialog) {
                poeRelatorioNaSessao(bytes, arquivoJasper);
            } else {
                escreveNoResponse(arquivoJasper, "application/pdf", "pdf", bytes);
            }
        }
    }

    public void escreveNoResponse(String nome, String contentType, String extencao, byte[] bytes) throws IOException {
        if (nome.contains(".")) {
            int recorte = nome.indexOf(".");
            nome = nome.substring(0, recorte);
        }
        HttpServletResponse response = (HttpServletResponse) getFacesContext().getExternalContext().getResponse();
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", fazDownload ? "attachment" : "inline" + "; filename=" + nome + "." + extencao);
        response.setContentLength(bytes.length);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();
        outputStream.close();
        getFacesContext().responseComplete();
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public UsuarioSistema usuarioLogado() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public Boolean getGeraNoDialog() {
        return geraNoDialog;
    }

    public void setGeraNoDialog(Boolean geraNoDialog) {
        this.geraNoDialog = geraNoDialog;
    }

    //Utlizado para relatórios com grande quantidade de páginas, onde a query já está otimizada mas a construção do relatório está lenta.
    public void gerarRelatorioComDadosEmCollectionView(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        escreveNoResponse((String) arquivoJasper, (byte[]) preparaExportaReport(jasperPrint).toByteArray());
        virtualizer.cleanup();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public JasperPrint gerarBytesDeRelatorioComDadosEmCollectionView(String caminhoReport, String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc, UnidadeOrganizacional unidadeOrganizacional) throws JRException, IOException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros, unidadeOrganizacional);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        JasperPrint jasperPrint = JasperFillManager.fillReport(caminhoReport + arquivoJasper, parametros, jrbc);
        return jasperPrint;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public JasperPrint gerarBytesDeRelatorioComDadosEmCollectionView(String caminhoReport, String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        JasperPrint jasperPrint = JasperFillManager.fillReport(caminhoReport + arquivoJasper, parametros, jrbc);
        return jasperPrint;
    }


    public ByteArrayOutputStream exportarJaspersParaPDF(List<JasperPrint> jasperPrints) throws JRException, IOException {
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrints);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        return dadosByte;
    }

    public ByteArrayOutputStream exportarJaspersParaPDF(ExportPDFMonitor monitor, JasperPrint... jasperPrints) throws JRException, IOException {
        Integer totalPaginas = 0;
        for (JasperPrint jasperPrint : jasperPrints) {
            totalPaginas += jasperPrint.getPages().size();
        }
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, Arrays.asList(jasperPrints));
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        if (monitor != null) {
            monitor.setEstado(ExportPDFMonitor.Estado.GERANDO_PDF);
            monitor.setTotalPaginas(totalPaginas);
            monitor.setPaginasGeradas(0);
            exporter.setParameter(JRExporterParameter.PROGRESS_MONITOR, monitor);
        }
        exporter.exportReport();
        return dadosByte;
    }


    public ByteArrayOutputStream exportarJasperParaPDF(JasperPrint jasperPrint) throws JRException, IOException {
        logger.debug("exportarJasperParaPDF " + jasperPrint.getPages().size());
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);

        logger.debug("Exportando " + jasperPrint.getPages().size());
        exporter.exportReport();
        logger.debug("Exportou " + jasperPrint.getPages().size());

        return dadosByte;
    }

    public ByteArrayOutputStream gerarBytesEmPdfDeRelatorioComDadosEmCollectionView(String caminho, String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        ByteArrayOutputStream retorno;
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        return exportarJasperParaPDF(JasperFillManager.fillReport(caminho + arquivoJasper, parametros, jrbc));
    }

    public ByteArrayOutputStream gerarBytesEmPdfDeRelatorioComDadosEmCollectionViewSemCabecalhoPadrao(String caminho, String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        ByteArrayOutputStream retorno;
        definirLocaleAndPerfilApp(parametros);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        return exportarJasperParaPDF(JasperFillManager.fillReport(caminho + arquivoJasper, parametros, jrbc));
    }

    public ByteArrayOutputStream gerarBytesEmPdfDeRelatorioComDadosEmCollectionViewAsync(String caminho, String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc, UnidadeOrganizacional unidadeOrcamentariaCorrente) throws JRException, IOException {
        ByteArrayOutputStream retorno;
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros, unidadeOrcamentariaCorrente);
        JRGzipVirtualizer virtualizer = new JRGzipVirtualizer(100);
        parametros.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        return exportarJasperParaPDF(JasperFillManager.fillReport(caminho + arquivoJasper, parametros, jrbc));
    }

    public JasperPrint gerarJasperPrint(String nome, HashMap parametros, Connection connection) throws JRException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        return JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + nome), parametros, connection);
    }

    public void salvarArquivoPortalTransparencia(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc, PortalTransparencia anexo) throws JRException, IOException {
        try {
            definirLocaleAndPerfilApp(parametros);
            atribuirConfiguracaoCabecalho(parametros);
            JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
            montarArquivoPortalTransparencia(arquivoJasper, anexo, jasperPrint);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public void salvarArquivoPortalTransparencia(RelatorioDTO dto, String nomeRelatorio, PortalTransparencia anexo, byte[] bytes) throws JRException, IOException {
        try {
            definirLocaleAndPerfilApp(dto);
            atribuirConfiguracaoCabecalho(dto);
            montarArquivoPortalTransparencia(nomeRelatorio, anexo, bytes);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void montarArquivoPortalTransparencia(String arquivoJasper, PortalTransparencia anexo, JasperPrint jasperPrint) throws Exception {
        byte[] bytes = preparaExportaReport(jasperPrint).toByteArray();
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(arquivoJasper);
        arquivo.setMimeType("application/pdf");
        arquivo.setNome(arquivoJasper);

        anexo.setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, new ByteArrayInputStream(bytes)));
        portalTransparenciaFacade.salvarPortalTransparenciaArquivo(anexo);
    }

    private void montarArquivoPortalTransparencia(String nomeRelatorio, PortalTransparencia anexo, byte[] bytes) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(nomeRelatorio);
        arquivo.setMimeType("application/pdf");
        arquivo.setNome(nomeRelatorio);

        anexo.setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, new ByteArrayInputStream(bytes)));
        portalTransparenciaFacade.salvarPortalTransparenciaArquivo(anexo);
    }

    public void salvarArquivoPortalTransparencia(String arquivoJasper, HashMap parametros, PortalTransparencia anexo) throws JRException, IOException {
        final Connection conn = recuperaConexaoJDBC();
        try {
            definirLocaleAndPerfilApp(parametros);
            atribuirConfiguracaoCabecalho(parametros);
            JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, conn);
            montarArquivoPortalTransparencia(arquivoJasper, anexo, jasperPrint);
        } catch (Exception e) {
            logger.error("Erro: ", e);
        } finally {
            fecharConnection(conn);
        }
    }

    public static void fecharConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public Boolean getFazDownload() {
        return fazDownload;
    }

    public void setFazDownload(Boolean fazDownload) {
        this.fazDownload = fazDownload;
    }

    public String montarNomeDoMunicipio() {
        return "MUNICÍPIO DE " + getSistemaFacade().getMunicipio().toUpperCase();
    }

    public void adicionarUserNoMap(HashMap parameters) {
        if (sistemaFacade.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaFacade.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaFacade.getUsuarioCorrente().getUsername());
        }
    }

    public static void exportarJasperToExcel(JasperPrint jasperPrint, String fileName) throws JRException, IOException {
        JRXlsExporter exporter = new JRXlsExporter();
        ByteArrayOutputStream streamDados = new ByteArrayOutputStream();
        exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, streamDados);
        exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.MAXIMUM_ROWS_PER_SHEET, Integer.decode("65000"));
        exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS, Boolean.TRUE);
        exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN, Boolean.TRUE);
        exporter.exportReport();
        streamDados.flush();
        streamDados.close();

        byte[] bytes = streamDados.toByteArray();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentLength(streamDados.size());
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();
        outputStream.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public static void downloadExcel(byte[] bytes, String fileName) {
        try {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            logger.error("Erro ao fazer download do excel {}", e);
        }
    }

    public void carregaRelatorio(byte[] bytes, String nome) {
        StreamedContent relatorio;
        if (bytes != null) {
            try {
                InputStream teste = new ByteArrayInputStream(bytes);
                relatorio = new DefaultStreamedContent(teste, "application/pdf", nome);
                Web.getSessionMap().put("relatorio", relatorio);
                FacesUtil.atualizarComponente("FormularioRelatorio");
                FacesUtil.executaJavaScript("mostraRelatorio()");
                FacesUtil.executaJavaScript("ajustaImpressaoRelatorio()");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public byte[] gerarBytesRelatorio(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException {
        definirLocaleAndPerfilApp(parametros);
        atribuirConfiguracaoCabecalho(parametros);
        JasperPrint jasperPrint = JasperFillManager.fillReport(getServletContext().getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        return preparaExportaReport(jasperPrint).toByteArray();
    }
}
