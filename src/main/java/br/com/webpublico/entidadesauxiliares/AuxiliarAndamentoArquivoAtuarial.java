package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Html2Pdf;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Buzatto on 18/05/2016.
 */
public class AuxiliarAndamentoArquivoAtuarial {

    protected static final Logger logger = LoggerFactory.getLogger(AuxiliarAndamentoArquivoAtuarial.class);

    public static final String ATIVOS = "Segurados Ativos";
    public static final String APOSENTADOS = "APOSENTADOS";
    public static final String PENSIONISTAS = "PENSIONISTAS";
    public static final String ATIVOS_FALECIDOS_OU_EXONERADOS = "ATIVOS FALECIDOS OU EXONERADOS";
    public static final String APOSENTADOS_FALECIDOS = "APOSENTADOS FALECIDOS";
    public static final String PENSIONISTAS_FALECIDOS = "PENSIONISTAS FALECIDOS";
    public static final String DEPENDENTES = "DEPENDENTES";


    private Integer totalRegistros;
    private Integer contadorProcessos;
    private List<String> logIncosistencia;
    private Long iniciadoEm;
    private Boolean parado;
    private HashMap<TipoLog, List<String>> logGeral;
    private List<String> mensagensLog = Lists.newArrayList();
    private HSSFCellStyle estiloMonetario;

    HSSFWorkbook pastaTrabalho;
    HSSFCellStyle styleFonteNegrito;
    HSSFCellStyle styleMonetario;

    File xlsFile;
    FileOutputStream fileOutputStream;

    File zipFile;
    ZipOutputStream zipOutputStream;

    File pdfFile;

    String caminhoBrasao;

    private Map<Entidade, List<UnidadeOrganizacional>> mapEntidade;
    private Map<HierarquiaOrganizacional, List<UnidadeOrganizacional>> mapHierarquia;

    public AuxiliarAndamentoArquivoAtuarial() {
    }

    public void iniciarProcesso() {
        this.logIncosistencia = new ArrayList<>();
        this.contadorProcessos = 0;
        this.totalRegistros = 0;
        this.iniciadoEm = System.currentTimeMillis();
        this.parado = Boolean.FALSE;
        this.mapEntidade = new HashMap<>();
        this.mapHierarquia = new HashMap<>();
    }

    public Double getPorcentagemDoCalculo() {
        if (contadorProcessos == null || totalRegistros == null) {
            return 0d;
        }
        return (contadorProcessos.doubleValue() / totalRegistros) * 100;
    }

    public void incrementarContador() {
        contadorProcessos++;
    }

    public void decrementarContador() {
        contadorProcessos--;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        long decorrido = (System.currentTimeMillis() - this.iniciadoEm);
        if (decorrido < HOUR) {
            return String.format("%1$TM:%1$TS%n", decorrido);
        } else {
            return String.format("%d:%2$TM:%2$TS%n", decorrido / HOUR, decorrido % HOUR);
        }
    }

    public String getTempoEstimado() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        long unitario = (System.currentTimeMillis() - this.iniciadoEm) / (this.contadorProcessos + 1);
        Double qntoFalta = Double.parseDouble("" + unitario * (this.totalRegistros - this.contadorProcessos));
        if (qntoFalta < HOUR) {
            return String.format("%1$TM:%1$TS%n", qntoFalta.longValue());
        } else {
            return String.format("%d:%2$TM:%2$TS%n", qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        }
    }

    public void pararProcessamento() {
        this.parado = Boolean.TRUE;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Integer getContadorProcessos() {
        return contadorProcessos;
    }

    public void setContadorProcessos(Integer contadorProcessos) {
        this.contadorProcessos = contadorProcessos;
    }

    public List<String> getLogIncosistencia() {
        return logIncosistencia;
    }

    public void setLogIncosistencia(List<String> logIncosistencia) {
        this.logIncosistencia = logIncosistencia;
    }

    public Long getIniciadoEm() {
        return iniciadoEm;
    }

    public void setIniciadoEm(Long iniciadoEm) {
        this.iniciadoEm = iniciadoEm;
    }

    public Boolean getParado() {
        return parado;
    }

    public void setParado(Boolean parado) {
        this.parado = parado;
    }

    public HSSFWorkbook getPastaTrabalho() {
        return pastaTrabalho;
    }

    public void setPastaTrabalho(HSSFWorkbook pastaTrabalho) {
        this.pastaTrabalho = pastaTrabalho;
    }

    public File getXlsFile() {
        return xlsFile;
    }

    public void setXlsFile(File xlsFile) {
        this.xlsFile = xlsFile;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    public void setFileOutputStream(FileOutputStream fileOutputStream) {
        this.fileOutputStream = fileOutputStream;
    }

    public File getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(File pdfFile) {
        this.pdfFile = pdfFile;
    }

    public String getCaminhoBrasao() {
        return caminhoBrasao;
    }

    public void setCaminhoBrasao(String caminhoBrasao) {
        this.caminhoBrasao = caminhoBrasao;
    }

    public HashMap<TipoLog, List<String>> getLogGeral() {
        if (logGeral == null) {
            logGeral = new HashMap<>();
        }
        return logGeral;
    }

    public void setLogGeral(HashMap<TipoLog, List<String>> logGeral) {
        this.logGeral = logGeral;
    }

    public List<String> getMensagensLog() {
        return mensagensLog;
    }

    public void setMensagensLog(List<String> mensagensLog) {
        this.mensagensLog = mensagensLog;
    }

    public void criarArquivoXLSZipado() {
        try {
            xlsFile = File.createTempFile("ESTUDO_ATUARIAL", "xls");
            fileOutputStream = new FileOutputStream(xlsFile);
            pastaTrabalho.write(fileOutputStream);

            //todo: zipar aquivo xls
            zipFile = File.createTempFile("EstudoAtuarial", "zip");
            fileOutputStream = new FileOutputStream(zipFile);
            zipOutputStream = new ZipOutputStream(fileOutputStream);

            zipOutputStream.putNextEntry(new ZipEntry("ESTUDO_ATUARIAL.xls"));
            FileInputStream fin = new FileInputStream(xlsFile);
            zipOutputStream = escreverConteudoArquivo(fin, zipOutputStream);

            fin.close();
            zipOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            logger.error("Erro: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar arquivos estudo atuarial! " + e);
        }
    }

    public void criarArquivoPDFLog() {
        try {
            pdfFile = File.createTempFile("ESTUDO_ATUARIAL_LOG", "pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoArquivoPDF(), baos);
            byte[] bytes = baos.toByteArray();
            fos.write(bytes);

            fos.close();
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao gerar arquivos estudo atuarial! " + e);
        }
    }

    private String getConteudoArquivoPDF() {
        String content = "";
        content += "<?xml version='1.0' encoding='iso-8859-1'?>\n";
        content += "<!DOCTYPE HTML PUBLIC 'HTML 4.01 Transitional//PT' 'http://www.w3.org/TR/html4/loose.dtd'>\n";
        content += "<html>\n";
        content += " <head>";
        content += " <title></title>";
        content += " </head>\n";
        content += " <body style='font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 10px;'>";

        content += "<div style='border: 1px solid black;text-align: left; padding : 3px;'>\n";
        content += " <table>" + "<tr>";
        content += " <td width='100'><img src='" + caminhoBrasao + "' alt='Smiley face' height='80' width='75' /></td>   ";
        content += " <td><b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n";
        content += "         MUNICÍPIO DE RIO BRANCO<br/>\n";
        content += "         INCONSISTÊNCIAS ARQUIVO ESTUDO ATUARIAL </b></td>\n";
        content += "</tr>" + "</table>";
        content += "</div>\n";

        content += "<div style='border: 1px solid black;text-align: left; margin-top: -1px; padding : 3px;'>\n";
        content += " <table cellpadding='4' cellspacing='0' style='margin-top: 10px; border-collapse: collapse; align=left'>";
        content += "<tr>";
        content += "  <td><b>Mensagens:</b></td>";
        content += "</tr>";
        content += getMensagensLogInconsistencias();
        content += "</table>";
        content += "</div>\n";

        content += "</body>";
        content += "</html>";
        return content;
    }

    private String getMensagensLogInconsistencias() {
        String parteMensagens = "";
        if (getLogGeral().isEmpty()) {
            parteMensagens += "<tr>";
            parteMensagens += "    <td style='font-size: 10px'>Nenhuma inconsistência encontrada na geração do arquivo.</td>";
            parteMensagens += "</tr>";
        } else {
            for (TipoLog tipoLog : getLogGeral().keySet()) {
                for (String inconsistencia : getLogGeral().get(tipoLog)) {
                    mensagensLog.add(inconsistencia);
                    parteMensagens += "<tr>";
                    parteMensagens += "    <td style='font-size: 10px'>" + inconsistencia + "</td>";
                    parteMensagens += "</tr>";
                }
            }
        }
        return parteMensagens;
    }

    private ZipOutputStream escreverConteudoArquivo(FileInputStream fin, ZipOutputStream zout) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fin.read(buffer)) > 0) {
            zout.write(buffer, 0, length);
        }
        return zout;
    }

    public void criarPastaTrabalho() {
        setPastaTrabalho(new HSSFWorkbook());
        this.estiloMonetario = criarEstiloMonetario();
    }

    public HSSFCellStyle getStyleFonteNegrito() {
        if (styleFonteNegrito == null) {
            styleFonteNegrito = pastaTrabalho.createCellStyle();
        }
        return pastaTrabalho.getCellStyleAt(styleFonteNegrito.getIndex());
    }


    public HSSFSheet criarPlanilha(String nomePlanilha) {
        return pastaTrabalho.createSheet(nomePlanilha);
    }

    public HSSFRow getNovaLinha(HSSFSheet planilha) {
        int proximaLinha = planilha.getRow(0) == null ? 0 : planilha.getLastRowNum() + 1;
        return planilha.createRow(proximaLinha);
    }

    public void montarCabecalhoServidoresAtivos(HSSFSheet planilhaAtivos) {
        HSSFRow cabecalho = getNovaLinha(planilhaAtivos);

        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.ANO_EXERCICIO.getIndex()).setCellValue(ColunaServidoresAtivos.ANO_EXERCICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.MES.getIndex()).setCellValue(ColunaServidoresAtivos.MES.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_DO_ENTE_NO_IBGE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.NOME_DO_ENTE.getIndex()).setCellValue(ColunaServidoresAtivos.NOME_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(ColunaServidoresAtivos.SIGLA_DA_UF_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.COMPOSICAO_DA_MASSA.getIndex()).setCellValue(ColunaServidoresAtivos.COMPOSICAO_DA_MASSA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.TIPO_DE_FUNDO.getIndex()).setCellValue(ColunaServidoresAtivos.TIPO_DE_FUNDO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CNPJ.getIndex()).setCellValue(ColunaServidoresAtivos.CNPJ.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.NOME_ORGAO.getIndex()).setCellValue(ColunaServidoresAtivos.NOME_ORGAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_PODER.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_TIPO_PODER.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_TIPO_PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_TIPO_POPULACAO_COBERTA.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_TIPO_POPULACAO_COBERTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_TIPO_DE_CARGO.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_TIPO_DE_CARGO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_CRITERIO_DE_ELEGIBILIDADE.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_CRITERIO_DE_ELEGIBILIDADE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR.getIndex()).setCellValue(ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR_CPF.getIndex()).setCellValue(ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR_PIS_PASEP.getIndex()).setCellValue(ColunaServidoresAtivos.IDENTIFICACAO_DO_SERVIDOR_PIS_PASEP.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_SEXO.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_SEXO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_ESTADO_CIVIL.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_ESTADO_CIVIL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.DATA_DE_NASCIMENTO.getIndex()).setCellValue(ColunaServidoresAtivos.DATA_DE_NASCIMENTO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_SITUACAO_FUNCIONAL.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_SITUACAO_FUNCIONAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CODIGO_TIPO_DE_VINCULO.getIndex()).setCellValue(ColunaServidoresAtivos.CODIGO_TIPO_DE_VINCULO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_SERVICO_PUBLICO.getIndex()).setCellValue(ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_SERVICO_PUBLICO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getIndex()).setCellValue(ColunaServidoresAtivos.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.NOME_DA_CARREIRA_ATUAL.getIndex()).setCellValue(ColunaServidoresAtivos.NOME_DA_CARREIRA_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_CARGO_ATUAL.getIndex()).setCellValue(ColunaServidoresAtivos.DATA_DE_INGRESSO_NO_CARGO_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.NOME_DO_CARGO_ATUAL.getIndex()).setCellValue(ColunaServidoresAtivos.NOME_DO_CARGO_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(ColunaServidoresAtivos.BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(ColunaServidoresAtivos.REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(ColunaServidoresAtivos.CONTRIBUICAO_MENSAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_RGPS_ANTERIOR_A_ADMISSAO_NO_ENTE.getIndex()).setCellValue(ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_RGPS_ANTERIOR_A_ADMISSAO_NO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getIndex()).setCellValue(ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getIndex()).setCellValue(ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getIndex()).setCellValue(ColunaServidoresAtivos.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.NUMERO_DE_DEPENDENTES_DO_SERVIDOR.getIndex()).setCellValue(ColunaServidoresAtivos.NUMERO_DE_DEPENDENTES_DO_SERVIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.INDICADOR_RECEBIMENTO_ABONO_DE_PERMANENCIA.getIndex()).setCellValue(ColunaServidoresAtivos.INDICADOR_RECEBIMENTO_ABONO_DE_PERMANENCIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.DATA_DE_INICIO_RECEBIMENTO_ABONO_DE_PERMANENCIA.getIndex()).setCellValue(ColunaServidoresAtivos.DATA_DE_INICIO_RECEBIMENTO_ABONO_DE_PERMANENCIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue(ColunaServidoresAtivos.PREVIDENCIA_COMPLEMENTAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(ColunaServidoresAtivos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaServidoresAtivos.DATA_PROVAVEL_DE_APOSENTADORIA.getIndex()).setCellValue(ColunaServidoresAtivos.DATA_PROVAVEL_DE_APOSENTADORIA.getDescricao());
    }

    public void montarCabecalhoAtivosFalecidosOuExonerados(HSSFSheet planilhaAtivosFalecidosOuExonerados) {
        HSSFRow cabecalho = getNovaLinha(planilhaAtivosFalecidosOuExonerados);

        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.ANO_REFERENCIA.getIndex()).setCellValue(ColunaServidoresAtivos.ANO_EXERCICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.MES.getIndex()).setCellValue(ColunaServidoresAtivos.MES.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.CODIGO_DO_ENTE_NO_IBGE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.NOME_DO_ENTE.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.NOME_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.SIGLA_DA_UF_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.COMPOSICAO_DA_MASSA_A.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.COMPOSICAO_DA_MASSA_B.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.CNPJ.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.CNPJ.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DENOMINACAO_ORGAO_ENTIDADE.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DENOMINACAO_ORGAO_ENTIDADE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.PODER.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.TIPO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.TIPO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.A_POPULACAO_COBERTA.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.A_POPULACAO_COBERTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.C_CRITERIO_DE_ELEGIBILIDADE.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.C_CRITERIO_DE_ELEGIBILIDADE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO_CPF.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO_PIS_PASEP.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_SEGURADO_PIS_PASEP.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.SEXO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.SEXO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.ESTADO_CIVIL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.ESTADO_CIVIL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DATA_DE_NASCIMENTO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DATA_DE_NASCIMENTO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.SITUACAO_FUNCIONAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.SITUACAO_FUNCIONAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DATA_SITUACAO_ATUAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DATA_SITUACAO_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.TIPO_DE_VINCULO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.TIPO_DE_VINCULO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_SERVICO_PUBLICO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_SERVICO_PUBLICO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_NO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DATA_DE_INGRESSO_NA_CARREIRA_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DA_CARREIRA_ATUAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DA_CARREIRA_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DATA_DE_INICIO_DE_EXERCICIO_NO_CARGO_ATUAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DATA_DE_INICIO_DE_EXERCICIO_NO_CARGO_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_CARGO_ATUAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.IDENTIFICACAO_DO_CARGO_ATUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.CONTRIBUICAO_MENSAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_ATIVO_ANTERIOR_A_ADMISSAO_NO_ENTE_PARA_O_RGPS.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_ATIVO_ANTERIOR_A_ADMISSAO_NO_ENTE_PARA_O_RGPS.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.NUMERO_DE_DEPENDENTES_DO_SERVIDOR.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.NUMERO_DE_DEPENDENTES_DO_SERVIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.SEGURADO_EM_ABONO_DE_PERMANENCIA.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.SEGURADO_EM_ABONO_DE_PERMANENCIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.DATA_DE_INICIO_DE_ABONO_DE_PERMANENCIA_DO_SERVIDOR_ATIVO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.DATA_DE_INICIO_DE_ABONO_DE_PERMANENCIA_DO_SERVIDOR_ATIVO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.PREVIDENCIA_COMPLEMENTAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAtivosFalecidosOuExonerados.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(ColunaAtivosFalecidosOuExonerados.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getDescricao());
    }

    public void montarCabecalhoAposentados(HSSFSheet planilhaAposentados) {
        HSSFRow cabecalho = getNovaLinha(planilhaAposentados);

        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.ANO_EXERCICIO.getIndex()).setCellValue(ColunaAposentados.ANO_EXERCICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.MES.getIndex()).setCellValue(ColunaAposentados.MES.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(ColunaAposentados.CODIGO_DO_ENTE_NO_IBGE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.NOME_DO_ENTE.getIndex()).setCellValue(ColunaAposentados.NOME_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(ColunaAposentados.SIGLA_DA_UF_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(ColunaAposentados.COMPOSICAO_DA_MASSA_A.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(ColunaAposentados.COMPOSICAO_DA_MASSA_B.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.CNPJ.getIndex()).setCellValue(ColunaAposentados.CNPJ.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.NOME_ORGAO.getIndex()).setCellValue(ColunaAposentados.NOME_ORGAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.PODER.getIndex()).setCellValue(ColunaAposentados.PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.TIPO.getIndex()).setCellValue(ColunaAposentados.TIPO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.A_POPULACAO_COBERTA.getIndex()).setCellValue(ColunaAposentados.A_POPULACAO_COBERTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getIndex()).setCellValue(ColunaAposentados.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.TIPO_DO_BENEFICIO.getIndex()).setCellValue(ColunaAposentados.TIPO_DO_BENEFICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_MATRICULA.getIndex()).setCellValue(ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_MATRICULA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_CPF.getIndex()).setCellValue(ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_PIS_PASEP.getIndex()).setCellValue(ColunaAposentados.IDENTIFICACAO_DO_APOSENTADO_PIS_PASEP.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.SEXO_DO_APOSENTADO.getIndex()).setCellValue(ColunaAposentados.SEXO_DO_APOSENTADO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.ESTADO_CIVIL_DO_APOSENTADO.getIndex()).setCellValue(ColunaAposentados.ESTADO_CIVIL_DO_APOSENTADO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.DATA_DE_NASCIMENTO_APOSENTADO.getIndex()).setCellValue(ColunaAposentados.DATA_DE_NASCIMENTO_APOSENTADO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.DATA_DE_INGRESSO_NO_SERVICO_PUBLICO.getIndex()).setCellValue(ColunaAposentados.DATA_DE_INGRESSO_NO_SERVICO_PUBLICO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(ColunaAposentados.DATA_DE_INGRESSO_NO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA.getIndex()).setCellValue(ColunaAposentados.DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA.getIndex()).setCellValue(ColunaAposentados.VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.CONTRIBUICAO_MENSAL_DO_APOSENTADO.getIndex()).setCellValue(ColunaAposentados.CONTRIBUICAO_MENSAL_DO_APOSENTADO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue(ColunaAposentados.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_APOSENTADO.getIndex()).setCellValue(ColunaAposentados.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_APOSENTADO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.CONDICAO_DO_APOSENTADO.getIndex()).setCellValue(ColunaAposentados.CONDICAO_DO_APOSENTADO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO.getIndex()).setCellValue(ColunaAposentados.NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getIndex()).setCellValue(ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getIndex()).setCellValue(ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getIndex()).setCellValue(ColunaAposentados.TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue(ColunaAposentados.PREVIDENCIA_COMPLEMENTAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentados.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(ColunaAposentados.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getDescricao());
    }

    public void montarCabecalhoAposentadosFalecidos(HSSFSheet planilhaAposentados) {
        HSSFRow cabecalho = getNovaLinha(planilhaAposentados);

        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.ANO_REFERENCIA.getIndex()).setCellValue(ColunaAposentadosFalecidos.ANO_REFERENCIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.MES.getIndex()).setCellValue(ColunaAposentadosFalecidos.MES.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(ColunaAposentadosFalecidos.CODIGO_DO_ENTE_NO_IBGE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.NOME_DO_ENTE.getIndex()).setCellValue(ColunaAposentadosFalecidos.NOME_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(ColunaAposentadosFalecidos.SIGLA_DA_UF_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(ColunaAposentadosFalecidos.COMPOSICAO_DA_MASSA_A.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(ColunaAposentadosFalecidos.COMPOSICAO_DA_MASSA_B.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.CNPJ.getIndex()).setCellValue(ColunaAposentadosFalecidos.CNPJ.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.DENOMINACAO_ORGAO_ENTIDADE.getIndex()).setCellValue(ColunaAposentadosFalecidos.DENOMINACAO_ORGAO_ENTIDADE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.PODER.getIndex()).setCellValue(ColunaAposentadosFalecidos.PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.TIPO.getIndex()).setCellValue(ColunaAposentadosFalecidos.TIPO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.A_POPULACAO_COBERTA.getIndex()).setCellValue(ColunaAposentadosFalecidos.A_POPULACAO_COBERTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getIndex()).setCellValue(ColunaAposentadosFalecidos.B_ESPECIFICACAO_DO_TIPO_DE_CARGO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.TIPO_DO_BENEFICIO.getIndex()).setCellValue(ColunaAposentadosFalecidos.TIPO_DO_BENEFICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_MATRICULA.getIndex()).setCellValue(ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_MATRICULA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_CPF.getIndex()).setCellValue(ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_PIS_PASEP.getIndex()).setCellValue(ColunaAposentadosFalecidos.IDENTIFICACAO_DO_APOSENTADO_MILITAR_PIS_PASEP.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.SEXO_DO_APOSENTADO_MILITAR.getIndex()).setCellValue(ColunaAposentadosFalecidos.SEXO_DO_APOSENTADO_MILITAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.ESTADO_CIVIL_DO_APOSENTADO_MILITAR.getIndex()).setCellValue(ColunaAposentadosFalecidos.ESTADO_CIVIL_DO_APOSENTADO_MILITAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.DATA_DE_NASCIMENTO_APOSENTADO_MILITAR.getIndex()).setCellValue(ColunaAposentadosFalecidos.DATA_DE_NASCIMENTO_APOSENTADO_MILITAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.SITUACAO.getIndex()).setCellValue(ColunaAposentadosFalecidos.SITUACAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.DATA_SITUACAO.getIndex()).setCellValue(ColunaAposentadosFalecidos.DATA_SITUACAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.DATA_DE_INGRESSO_SERVICO_PUBLICO.getIndex()).setCellValue(ColunaAposentadosFalecidos.DATA_DE_INGRESSO_SERVICO_PUBLICO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.DATA_DE_INGRESSO_NO_ENTE.getIndex()).setCellValue(ColunaAposentadosFalecidos.DATA_DE_INGRESSO_NO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR.getIndex()).setCellValue(ColunaAposentadosFalecidos.DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR.getIndex()).setCellValue(ColunaAposentadosFalecidos.VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(ColunaAposentadosFalecidos.CONTRIBUICAO_MENSAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue(ColunaAposentadosFalecidos.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getIndex()).setCellValue(ColunaAposentadosFalecidos.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.CONDICAO_DO_APOSENTADO_MILITAR.getIndex()).setCellValue(ColunaAposentadosFalecidos.CONDICAO_DO_APOSENTADO_MILITAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue(ColunaAposentadosFalecidos.PREVIDENCIA_COMPLEMENTAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(ColunaAposentadosFalecidos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaAposentadosFalecidos.NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO.getIndex()).setCellValue(ColunaAposentadosFalecidos.NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO.getDescricao());
    }

    public enum ColunaPensionistasFalecidos {
        ANO_REFERENCIA("ANO DE REFERÊNCIA", 0),
        MES("MÊS", 1),
        CODIGO_DO_ENTE_NO_IBGE("CÓDIGO DO ENTE NO IBGE", 2),
        NOME_DO_ENTE("NOME DO ENTE", 3),
        SIGLA_DA_UF_DO_ENTE("SIGLA DA UF DO ENTE", 4),
        COMPOSICAO_DA_MASSA_A("COMPOSIÇÃO DA MASSA", 5),
        COMPOSICAO_DA_MASSA_B("COMPOSIÇÃO DA MASSA", 6),
        CNPJ("CNPJ", 7),
        DENOMINACAO_DO_ORGAO_ENTIDADE("DENOMINAÇÃO DO ÓRGÃO/ENTIDADE", 8),
        PODER("PODER", 9),
        TIPO("TIPO", 10),
        A1_IDENTIFICACAO_INSTITUIDOR_DA_PENSAO("A1 - IDENTIFICAÇÃO DO INSTITUIDOR DA PENSÃO", 11),
        A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA("A2 - IDENTIFICAÇÃO DO SEGURADO INSTITUIDOR DA PENSÃO", 12),
        A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF("A3 - IDENTIFICAÇÃO DO SEGURADO INSTITUIDOR DA PENSÃO", 13),
        A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP("A4 - IDENTIFICAÇÃO DO SEGURADO INSTITUIDOR DA PENSÃO", 14),
        A5_DATA_NASCIMENTO_INSTITUIDOR_PENSAO("A5 - DATA DE NASCIMENTO DO INSTITUIDOR DA PENSÃO", 15),
        A6_DATA_FALESCIMENTO_INSTITUIDADE_PENSAO("A6 - DATA DO FALECIMENTO DO INSTITUIDOR DA PENSÃO", 16),
        B1_IDENTIFICACAO_PENSIONISTA_CPF("B1 - IDENTIFICAÇÃO DO PENSIONISTA", 17),
        B2_MATRICULA_PENSIONISTA("B2 - MATRÍCULA DO PENSIONISTA", 18),
        B3_SEXO_DO_PENSIONISTA("B3 - SEXO DO PENSIONISTA", 19),
        B4_DATA_DE_NASCIMENTO_PENSIONISTA("B4 - DATA DE NASCIMENTO DO PENSIONISTA", 20),
        B5_SITUACAO("B5 - SITUAÇÃO", 21),
        B6_DATA_SITUACAO("B6 - DATA DA SITUAÇÃO", 22),
        B7_TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_DO_INSTITUIDOR("B7 - TIPO DE RELAÇÃO DO PENSIONISTA COM O SEGURADO DO INSTITUIDOR", 23),
        DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO("DATA DE INÍCIO DO BENEFÍCIO DE PENSÃO", 24),
        VALOR_MENSAL_DO_BENEFICIO_RECEBIDO_PELO_PENSIONISTA("VALOR MENSAL DO BENEFÍCIO RECEBIDO PELO PENSIONISTA", 25),
        VALOR_TOTAL_PENSAO("VALOR TOTAL DA PENSÃO", 26),
        VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA("VALOR PERCENTUAL DA QUOTA RECEBIDA PELO PENSIONISTA", 27),
        CONTRIBUICAO_MENSAL("CONTRIBUIÇÃO MENSAL", 28),
        VALOR_MENSAL_COMPENSACAO_PREVIDENCIARIA("VALOR MENSAL DA COMPENSAÇÃO PREVIDENCIÁRIA", 29),
        IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS("IDENTIFICADOR DE PARIDADE COM SERVIDORES ATIVOS", 30),
        CONDICAO_DO_PENSIONISTA("CONDIÇÃO DO PENSIONISTA", 31),
        DURACAO_DO_BENEFICIO("DURAÇÃO DO BENEFÍCIO", 32),
        TEMPO_DE_DURACAO_DO_BENEFICIO("TEMPO DE DURAÇÃO DO BENEFÍCIO", 33),
        PREVIDENCIA_COMPLEMENTAR("PREVIDÊNCIA COMPLEMENTAR", 34),
        TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO("TETO CONSTITUCIONAL REMUNERATÓRIO ESPECÍFICO", 35);


        private String descricao;
        private int index;

        ColunaPensionistasFalecidos(String descricao, int index) {
            this.descricao = descricao;
            this.index = index;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public void montarCabecalhoPensionistas(HSSFSheet planilhaPensionistas) {
        HSSFRow cabecalho = getNovaLinha(planilhaPensionistas);

        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.ANO_EXERCICIO.getIndex()).setCellValue(ColunaPensionistas.ANO_EXERCICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.MES.getIndex()).setCellValue(ColunaPensionistas.MES.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(ColunaPensionistas.CODIGO_DO_ENTE_NO_IBGE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.NOME_DO_ENTE.getIndex()).setCellValue(ColunaPensionistas.NOME_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(ColunaPensionistas.SIGLA_DA_UF_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(ColunaPensionistas.COMPOSICAO_DA_MASSA_A.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(ColunaPensionistas.COMPOSICAO_DA_MASSA_B.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.CNPJ.getIndex()).setCellValue(ColunaPensionistas.CNPJ.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.NOME_DO_ORGAO_ENTIDADE.getIndex()).setCellValue(ColunaPensionistas.NOME_DO_ORGAO_ENTIDADE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.PODER.getIndex()).setCellValue(ColunaPensionistas.PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.TIPO.getIndex()).setCellValue(ColunaPensionistas.TIPO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.CO_TIPO_INSTITUIDOR.getIndex()).setCellValue(ColunaPensionistas.CO_TIPO_INSTITUIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA.getIndex()).setCellValue(ColunaPensionistas.A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF.getIndex()).setCellValue(ColunaPensionistas.A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP.getIndex()).setCellValue(ColunaPensionistas.A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.CODIGO_SEXO_INSTITUIDOR_DA_PENSAO.getIndex()).setCellValue(ColunaPensionistas.CODIGO_SEXO_INSTITUIDOR_DA_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.DATA_NASCIMENTO_INSTITUIDOR_PENSAO.getIndex()).setCellValue(ColunaPensionistas.DATA_NASCIMENTO_INSTITUIDOR_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.DATA_FALESCIMENTO_INSTITUIDADE_PENSAO.getIndex()).setCellValue(ColunaPensionistas.DATA_FALESCIMENTO_INSTITUIDADE_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.MATRICULA_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistas.MATRICULA_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.IDENTIFICACAO_PENSIONISTA_CPF.getIndex()).setCellValue(ColunaPensionistas.IDENTIFICACAO_PENSIONISTA_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.CODIGO_SEXO_DO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistas.CODIGO_SEXO_DO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.DATA_DE_NASCIMENTO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistas.DATA_DE_NASCIMENTO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_INSTITUIDOR.getIndex()).setCellValue(ColunaPensionistas.TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_INSTITUIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO.getIndex()).setCellValue(ColunaPensionistas.DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.VALOR_MENSAL_DO_BENEFICIO_DE_PENSAO.getIndex()).setCellValue(ColunaPensionistas.VALOR_MENSAL_DO_BENEFICIO_DE_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.VALOR_TOTAL_PENSAO.getIndex()).setCellValue(ColunaPensionistas.VALOR_TOTAL_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistas.VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.VALOR_MENSAL_DA_CONTRIBUCAO_PREVIDENCIARIA.getIndex()).setCellValue(ColunaPensionistas.VALOR_MENSAL_DA_CONTRIBUCAO_PREVIDENCIARIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue(ColunaPensionistas.VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getIndex()).setCellValue(ColunaPensionistas.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.CONDICAO_DO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistas.CONDICAO_DO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.DURACAO_DO_BENEFICIO.getIndex()).setCellValue(ColunaPensionistas.DURACAO_DO_BENEFICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.TEMPO_DE_DURACAO_DO_BENEFICIO.getIndex()).setCellValue(ColunaPensionistas.TEMPO_DE_DURACAO_DO_BENEFICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue(ColunaPensionistas.PREVIDENCIA_COMPLEMENTAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistas.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(ColunaPensionistas.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getDescricao());
    }

    public void montarCabecalhoPensionistasFalecidos(HSSFSheet planilhaPensionistasFalecidos) {
        HSSFRow cabecalho = getNovaLinha(planilhaPensionistasFalecidos);

        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.ANO_REFERENCIA.getIndex()).setCellValue(ColunaPensionistasFalecidos.ANO_REFERENCIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.MES.getIndex()).setCellValue(ColunaPensionistasFalecidos.MES.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(ColunaPensionistasFalecidos.CODIGO_DO_ENTE_NO_IBGE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.NOME_DO_ENTE.getIndex()).setCellValue(ColunaPensionistasFalecidos.NOME_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(ColunaPensionistasFalecidos.SIGLA_DA_UF_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.COMPOSICAO_DA_MASSA_A.getIndex()).setCellValue(ColunaPensionistasFalecidos.COMPOSICAO_DA_MASSA_A.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.COMPOSICAO_DA_MASSA_B.getIndex()).setCellValue(ColunaPensionistasFalecidos.COMPOSICAO_DA_MASSA_B.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.CNPJ.getIndex()).setCellValue(ColunaPensionistasFalecidos.CNPJ.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.DENOMINACAO_DO_ORGAO_ENTIDADE.getIndex()).setCellValue(ColunaPensionistasFalecidos.DENOMINACAO_DO_ORGAO_ENTIDADE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.PODER.getIndex()).setCellValue(ColunaPensionistasFalecidos.PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.TIPO.getIndex()).setCellValue(ColunaPensionistasFalecidos.TIPO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.A1_IDENTIFICACAO_INSTITUIDOR_DA_PENSAO.getIndex()).setCellValue(ColunaPensionistasFalecidos.A1_IDENTIFICACAO_INSTITUIDOR_DA_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA.getIndex()).setCellValue(ColunaPensionistasFalecidos.A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF.getIndex()).setCellValue(ColunaPensionistasFalecidos.A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP.getIndex()).setCellValue(ColunaPensionistasFalecidos.A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.A5_DATA_NASCIMENTO_INSTITUIDOR_PENSAO.getIndex()).setCellValue(ColunaPensionistasFalecidos.A5_DATA_NASCIMENTO_INSTITUIDOR_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.A6_DATA_FALESCIMENTO_INSTITUIDADE_PENSAO.getIndex()).setCellValue(ColunaPensionistasFalecidos.A6_DATA_FALESCIMENTO_INSTITUIDADE_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.B1_IDENTIFICACAO_PENSIONISTA_CPF.getIndex()).setCellValue(ColunaPensionistasFalecidos.B1_IDENTIFICACAO_PENSIONISTA_CPF.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.B2_MATRICULA_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistasFalecidos.B2_MATRICULA_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.B3_SEXO_DO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistasFalecidos.B3_SEXO_DO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.B4_DATA_DE_NASCIMENTO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistasFalecidos.B4_DATA_DE_NASCIMENTO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.B5_SITUACAO.getIndex()).setCellValue(ColunaPensionistasFalecidos.B5_SITUACAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.B6_DATA_SITUACAO.getIndex()).setCellValue(ColunaPensionistasFalecidos.B6_DATA_SITUACAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.B7_TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_DO_INSTITUIDOR.getIndex()).setCellValue(ColunaPensionistasFalecidos.B7_TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_DO_INSTITUIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO.getIndex()).setCellValue(ColunaPensionistasFalecidos.DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.VALOR_MENSAL_DO_BENEFICIO_RECEBIDO_PELO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistasFalecidos.VALOR_MENSAL_DO_BENEFICIO_RECEBIDO_PELO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.VALOR_TOTAL_PENSAO.getIndex()).setCellValue(ColunaPensionistasFalecidos.VALOR_TOTAL_PENSAO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistasFalecidos.VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.CONTRIBUICAO_MENSAL.getIndex()).setCellValue(ColunaPensionistasFalecidos.CONTRIBUICAO_MENSAL.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.VALOR_MENSAL_COMPENSACAO_PREVIDENCIARIA.getIndex()).setCellValue(ColunaPensionistasFalecidos.VALOR_MENSAL_COMPENSACAO_PREVIDENCIARIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getIndex()).setCellValue(ColunaPensionistasFalecidos.IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.CONDICAO_DO_PENSIONISTA.getIndex()).setCellValue(ColunaPensionistasFalecidos.CONDICAO_DO_PENSIONISTA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.DURACAO_DO_BENEFICIO.getIndex()).setCellValue(ColunaPensionistasFalecidos.DURACAO_DO_BENEFICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.TEMPO_DE_DURACAO_DO_BENEFICIO.getIndex()).setCellValue(ColunaPensionistasFalecidos.TEMPO_DE_DURACAO_DO_BENEFICIO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.PREVIDENCIA_COMPLEMENTAR.getIndex()).setCellValue(ColunaPensionistasFalecidos.PREVIDENCIA_COMPLEMENTAR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaPensionistasFalecidos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getIndex()).setCellValue(ColunaPensionistasFalecidos.TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO.getDescricao());
    }

    public void montarCabecalhoDependentes(HSSFSheet planilhaDependentes) {
        HSSFRow cabecalho = getNovaLinha(planilhaDependentes);

        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.ANO_REFERENCIA.getIndex()).setCellValue(ColunaDependentes.ANO_REFERENCIA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.MES.getIndex()).setCellValue(ColunaDependentes.MES.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.CODIGO_DO_ENTE_NO_IBGE.getIndex()).setCellValue(ColunaDependentes.CODIGO_DO_ENTE_NO_IBGE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.NOME_DO_ENTE.getIndex()).setCellValue(ColunaDependentes.NOME_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.SIGLA_DA_UF_DO_ENTE.getIndex()).setCellValue(ColunaDependentes.SIGLA_DA_UF_DO_ENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.COMPOSICAO_DA_MASSA.getIndex()).setCellValue(ColunaDependentes.COMPOSICAO_DA_MASSA.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.TIPO_DE_FUNDO.getIndex()).setCellValue(ColunaDependentes.TIPO_DE_FUNDO.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.CNPJ.getIndex()).setCellValue(ColunaDependentes.CNPJ.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.NOME_DO_ORGAO_ENTIDADE.getIndex()).setCellValue(ColunaDependentes.NOME_DO_ORGAO_ENTIDADE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.CODIGO_DO_PODER.getIndex()).setCellValue(ColunaDependentes.CODIGO_DO_PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.CODIGO_DO_TIPO_DE_PODER.getIndex()).setCellValue(ColunaDependentes.CODIGO_DO_TIPO_DE_PODER.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.MATRICULA_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(ColunaDependentes.MATRICULA_DO_SEGURADO_SERVIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.CPF_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(ColunaDependentes.CPF_DO_SEGURADO_SERVIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.PASEP_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(ColunaDependentes.PASEP_DO_SEGURADO_SERVIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.SEXO_DO_SEGURADO_SERVIDOR.getIndex()).setCellValue(ColunaDependentes.SEXO_DO_SEGURADO_SERVIDOR.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.IDENTIFICADOR_UNICO_DO_DEPENDENTE.getIndex()).setCellValue(ColunaDependentes.IDENTIFICADOR_UNICO_DO_DEPENDENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.CPF_DO_DEPENDENTE.getIndex()).setCellValue(ColunaDependentes.CPF_DO_DEPENDENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.DATA_DE_NASCIMENTO_DO_DEPENDENTE.getIndex()).setCellValue(ColunaDependentes.DATA_DE_NASCIMENTO_DO_DEPENDENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.SEXO_DO_DEPENDENTE.getIndex()).setCellValue(ColunaDependentes.SEXO_DO_DEPENDENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.CONDICAO_DO_DEPENDENTE.getIndex()).setCellValue(ColunaDependentes.CONDICAO_DO_DEPENDENTE.getDescricao());
        criarCelulaFonteNetrio(cabecalho, ColunaDependentes.TIPO_DEPENDENCIA_DEPENDENTE_COM_SEGURADO_SERVIDOR.getIndex()).setCellValue(ColunaDependentes.TIPO_DEPENDENCIA_DEPENDENTE_COM_SEGURADO_SERVIDOR.getDescricao());
    }

    public void montarCabecalhoCodigoAndDescricao(HSSFSheet planilha) {
        HSSFRow cabecalho = getNovaLinha(planilha);

        criarCelulaFonteNetrio(cabecalho, 0).setCellValue("Código");
        criarCelulaFonteNetrio(cabecalho, 1).setCellValue("Descrição");
    }

    public void montarCabecalhoSiglaAndDescricao(HSSFSheet planilha) {
        HSSFRow cabecalho = getNovaLinha(planilha);

        criarCelulaFonteNetrio(cabecalho, 0).setCellValue("Sigla");
        criarCelulaFonteNetrio(cabecalho, 1).setCellValue("Descrição");
    }

    public Cell criarCelula(HSSFRow linha, int celula) {
        return linha.createCell(celula);
    }

    public Cell criarCelulaFonteNetrio(HSSFRow linha, int celula) {
        HSSFCell cell = linha.createCell(celula);
        cell.setCellStyle(getEstiloFonteNegrito());
        return cell;
    }

    private HSSFCellStyle getEstiloFonteNegrito() {
        HSSFCellStyle estiloFonteNegrito = getStyleFonteNegrito();

        HSSFFont fonte = pastaTrabalho.createFont();
        fonte.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        estiloFonteNegrito.setFont(fonte);

        return estiloFonteNegrito;
    }

    public Cell criarCelulaMonetario(HSSFRow linha, int celula) {
        HSSFCell cell = linha.createCell(celula);
        cell.setCellStyle(estiloMonetario);
        return cell;
    }

    private HSSFCellStyle criarEstiloMonetario() {

        HSSFCellStyle estiloMonetario = pastaTrabalho.createCellStyle();

        HSSFCreationHelper createHelper = pastaTrabalho.getCreationHelper();
        estiloMonetario.setDataFormat(createHelper.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* 0.00_);_(@_)"));
        HSSFFont fonte = pastaTrabalho.createFont();
        fonte.setFontName("Calibri");
        estiloMonetario.setFont(fonte);

        return estiloMonetario;
    }

    public void adicionarLog(TipoLog tipo, String conteudo) {
        if (getLogGeral().get(tipo) == null) {
            getLogGeral().put(tipo, new ArrayList<String>());
        }

        if (getLogGeral().get(TipoLog.TODOS) == null) {
            getLogGeral().put(TipoLog.TODOS, new ArrayList<String>());
        }

        String agora = Util.dateHourToString(new Date());
        conteudo = agora + " - " + conteudo + "\n";

        getLogGeral().get(tipo).add(conteudo);
//        if (!tipo.equals(TipoLog.TODOS)) {
//            getLogGeral().get(TipoLog.TODOS).add(conteudo);
//        }
    }

    public Map<Entidade, List<UnidadeOrganizacional>> getMapEntidade() {
        return mapEntidade;
    }

    public void setMapEntidade(Map<Entidade, List<UnidadeOrganizacional>> mapEntidade) {
        this.mapEntidade = mapEntidade;
    }

    public Map<HierarquiaOrganizacional, List<UnidadeOrganizacional>> getMapHierarquia() {
        return mapHierarquia;
    }

    public void setMapHierarquia(Map<HierarquiaOrganizacional, List<UnidadeOrganizacional>> mapHierarquia) {
        this.mapHierarquia = mapHierarquia;
    }

    public boolean jaGerouArquivoZip() {
        return zipFile != null;
    }

    public enum ColunaServidoresAtivos {
        ANO_EXERCICIO("ANO DO EXERCÍCIO DA DRAA", 0),
        MES("MÊS", 1),
        CODIGO_DO_ENTE_NO_IBGE("CÓDIGO DO ENTE NO IBGE", 2),
        NOME_DO_ENTE("NOME DO ENTE", 3),
        SIGLA_DA_UF_DO_ENTE("SIGLA DA UF DO ENTE", 4),
        COMPOSICAO_DA_MASSA("COMPOSIÇÃO DA MASSA", 5),
        TIPO_DE_FUNDO("TIPO DE FUNDO", 6),
        CNPJ("CNPJ", 7),
        NOME_ORGAO("NOME DO ÓRGÃO/ENTIDADE", 8),
        CODIGO_PODER("CÓDIGO DO PODER", 9),
        CODIGO_TIPO_PODER("CÓDIGO DO TIPO  DE PODER", 10),
        CODIGO_TIPO_POPULACAO_COBERTA("CÓDIGO DO TIPO DE POPULAÇÃO COBERTA", 11),
        CODIGO_TIPO_DE_CARGO("CÓDIGO DO TIPO DE CARGO", 12),
        CODIGO_CRITERIO_DE_ELEGIBILIDADE("CÓDIGO DO CRITÉRIO DE ELEGIBILIDADE", 13),
        IDENTIFICACAO_DO_SERVIDOR("IDENTIFICAÇÃO DO SERVIDOR", 14),
        IDENTIFICACAO_DO_SERVIDOR_CPF("IDENTIFICAÇÃO DO SERVIDOR", 15),
        IDENTIFICACAO_DO_SERVIDOR_PIS_PASEP("IDENTIFICAÇÃO DO SERVIDOR", 16),
        CODIGO_SEXO("CÓDIGO DO SEXO DO SERVIDOR", 17),
        CODIGO_ESTADO_CIVIL("CÓDIGO DO ESTADO CIVIL  DO SERVIDOR", 18),
        DATA_DE_NASCIMENTO("DATA DE NASCIMENTO DO SERVIDOR", 19),
        CODIGO_SITUACAO_FUNCIONAL("CÓDIGO DA SITUAÇÃO FUNCIONAL SERVIDOR", 20),
        CODIGO_TIPO_DE_VINCULO("CÓDIGO DO TIPO DE VÍNCULO", 21),
        DATA_DE_INGRESSO_NO_SERVICO_PUBLICO("DATA DE INGRESSO NO SERVIÇO PÚBLICO", 22),
        DATA_DE_INGRESSO_NO_ENTE("DATA DE INGRESSO NO ENTE", 23),
        DATA_DE_INGRESSO_NA_CARREIRA_ATUAL("DATA DE INGRESSO NA CARREIRA ATUAL", 24),
        NOME_DA_CARREIRA_ATUAL("NOME DA CARREIRA ATUAL", 25),
        DATA_DE_INGRESSO_NO_CARGO_ATUAL("DATA DE INGRESSO NO CARGO ATUAL", 26),
        NOME_DO_CARGO_ATUAL("NOME DO CARGO ATUAL", 27),
        BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO("BASE DE CÁLCULO MENSAL DO SERVIDOR", 28),
        REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO("REMUNERAÇÃO MENSAL TOTAL DO SERVIDOR", 29),
        CONTRIBUICAO_MENSAL("CONTRIBUIÇÃO MENSAL", 30),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_RGPS_ANTERIOR_A_ADMISSAO_NO_ENTE("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA O RGPS, ANTERIOR À ADMISSÃO NO ENTE", 31),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA MUNICIPAL\", ANTERIOR À ADMISSÃO NO ENTE", 32),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA ESTADUAL\", ANTERIOR À ADMISSÃO NO ENTE", 33),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA FEDERAL\", ANTERIOR À ADMISSÃO NO ENTE", 34),
        NUMERO_DE_DEPENDENTES_DO_SERVIDOR("NÚMERO DE DEPENDENTES DO SERVIDOR", 35),
        INDICADOR_RECEBIMENTO_ABONO_DE_PERMANENCIA("INDICADOR DE RECEBIMENTO DE ABONO DE PERMANÊNCIA", 36),
        DATA_DE_INICIO_RECEBIMENTO_ABONO_DE_PERMANENCIA("DATA DE INÍCIO DE RECEBIMENTO DO ABONO DE PERMANÊNCIA", 37),
        PREVIDENCIA_COMPLEMENTAR("PREVIDÊNCIA COMPLEMENTAR", 38),
        TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO("TETO CONSTITUCIONAL REMUNERATÓRIO ESPECÍFICO (DOS SERVIDORES DO RESPECTIVO PODER)", 39),
        DATA_PROVAVEL_DE_APOSENTADORIA("DATA PROVAVEL DE APOSENTADORIA", 40);

        private String descricao;
        private int index;

        ColunaServidoresAtivos(String descricao, int index) {
            this.descricao = descricao;
            this.index = index;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum ColunaAposentados {
        ANO_EXERCICIO("ANO DO EXERCÍCIO DA DRAA", 0),
        MES("MÊS", 1),
        CODIGO_DO_ENTE_NO_IBGE("CÓDIGO DO ENTE NO IBGE", 2),
        NOME_DO_ENTE("NOME DO ENTE", 3),
        SIGLA_DA_UF_DO_ENTE("SIGLA DA UF DO ENTE", 4),
        COMPOSICAO_DA_MASSA_A("COMPOSIÇÃO DA MASSA", 5),
        COMPOSICAO_DA_MASSA_B("COMPOSIÇÃO DA MASSA", 6),
        CNPJ("CNPJ", 7),
        NOME_ORGAO("NOME DO ÓRGÃO/ENTIDADE", 8),
        PODER("PODER", 9),
        TIPO("TIPO", 10),
        A_POPULACAO_COBERTA("A - POPULAÇÃO COBERTA", 11),
        B_ESPECIFICACAO_DO_TIPO_DE_CARGO("B- ESPECIFICAÇÃO DO TIPO DE CARGO", 12),
        TIPO_DO_BENEFICIO("TIPO DO BENEFÍCIO", 13),
        IDENTIFICACAO_DO_APOSENTADO_MATRICULA("IDENTIFICAÇÃO DO APOSENTADO", 14),
        IDENTIFICACAO_DO_APOSENTADO_CPF("IDENTIFICAÇÃO DO APOSENTADO", 15),
        IDENTIFICACAO_DO_APOSENTADO_PIS_PASEP("IDENTIFICAÇÃO DO APOSENTADO", 16),
        SEXO_DO_APOSENTADO("SEXO DO APOSENTADO", 17),
        ESTADO_CIVIL_DO_APOSENTADO("ESTADO CIVIL DO APOSENTADO", 18),
        DATA_DE_NASCIMENTO_APOSENTADO("DATA DE NASCIMENTO DO APOSENTADO", 19),
        DATA_DE_INGRESSO_NO_SERVICO_PUBLICO("DATA DE INGRESSO NO SERVIÇO PÚBLICO", 20),
        DATA_DE_INGRESSO_NO_ENTE("DATA DE INGRESSO NO ENTE", 21),
        DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA("DATA DE INÍCIO DO BENEFÍCIO DE APOSENTADORIA", 22),
        VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA("VALOR MENSAL DO BENEFÍCIO DE APOSENTADORIA", 23),
        CONTRIBUICAO_MENSAL_DO_APOSENTADO("CONTRIBUIÇÃO MENSAL DO APOSENTADO", 24),
        VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA("VALOR PRÓ-RATA MENSAL RECEBIDO DE COMPENSAÇÃO PREVIDENCIÁRIA", 25),
        IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_APOSENTADO("IDENTIFICADOR DE PARIDADE COM SERVIDORES APOSENTADO", 26),
        CONDICAO_DO_APOSENTADO("CONDIÇÃO DO APOSENTADO", 27),
        NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO("NÚMERO DE DEPENDENTES DO APOSENTADO / MILITAR INATIVO", 28),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA MUNICIPAL\", ANTERIOR À ADMISSÃO NO ENTE", 29),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA ESTADUAL\", ANTERIOR À ADMISSÃO NO ENTE", 30),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA FEDERAL\", ANTERIOR À ADMISSÃO NO ENTE", 31),
        PREVIDENCIA_COMPLEMENTAR("PREVIDÊNCIA COMPLEMENTAR", 32),
        TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO("TETO CONSTITUCIONAL REMUNERATÓRIO ESPECÍFICO (DOS SERVIDORES DO RESPECTIVO PODER)", 33);

        private String descricao;
        private int index;

        ColunaAposentados(String descricao, int index) {
            this.descricao = descricao;
            this.index = index;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum ColunaPensionistas {
        ANO_EXERCICIO("ANO DO EXERCÍCIO DA DRAA", 0),
        MES("MÊS", 1),
        CODIGO_DO_ENTE_NO_IBGE("CÓDIGO DO ENTE NO IBGE", 2),
        NOME_DO_ENTE("NOME DO ENTE", 3),
        SIGLA_DA_UF_DO_ENTE("SIGLA DA UF DO ENTE", 4),
        COMPOSICAO_DA_MASSA_A("COMPOSIÇÃO DA MASSA", 5),
        COMPOSICAO_DA_MASSA_B("COMPOSIÇÃO DA MASSA", 6),
        CNPJ("CNPJ", 7),
        NOME_DO_ORGAO_ENTIDADE("NOME DO ÓRGÃO/ENTIDADE", 8),
        PODER("PODER", 9),
        TIPO("TIPO", 10),
        CO_TIPO_INSTITUIDOR("CÓDIGO DO TIPO DE INSTITUIDOR", 11),
        A2_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_MATRICULA("IDENTIFICAÇÃO DO SEGURADO INSTITUIDOR DA PENSÃO", 12),
        A3_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_CPF("IDENTIFICAÇÃO DO SEGURADO INSTITUIDOR DA PENSÃO", 13),
        A4_IDENTIFICACAO_SEGURADO_INSTITUIDOR_PENSAO_PIS_PASEP("IDENTIFICAÇÃO DO SEGURADO INSTITUIDOR DA PENSÃO", 14),
        CODIGO_SEXO_INSTITUIDOR_DA_PENSAO("CÓDIGO DO SEXO DO INSTITUIDOR DA PENSÃO", 15),
        DATA_NASCIMENTO_INSTITUIDOR_PENSAO("DATA DE NASCIMENTO DO INSTITUIDOR DA PENSÃO", 16),
        DATA_FALESCIMENTO_INSTITUIDADE_PENSAO("DATA DO FALECIMENTO DO INSTITUIDOR DA PENSÃO", 17),
        MATRICULA_PENSIONISTA("IDENTIFICAÇÃO DO PENSIONISTA", 18),
        IDENTIFICACAO_PENSIONISTA_CPF("IDENTIFICAÇÃO DO PENSIONISTA", 19),
        CODIGO_SEXO_DO_PENSIONISTA("CÓDIGO DO SEXO DO PENSIONISTA", 20),
        DATA_DE_NASCIMENTO_PENSIONISTA("DATA DE NASCIMENTO DO PENSIONISTA", 21),
        TIPO_RELACAO_PENSIONISTA_COM_SEGURADO_INSTITUIDOR("TIPO DE RELAÇÃO DO PENSIONISTA COM O SEGURADO  INSTITUIDOR", 22),
        DATA_DE_INICIO_DO_BENEFICIO_DE_PENSAO("DATA DE INÍCIO DO BENEFÍCIO DE PENSÃO", 23),
        VALOR_MENSAL_DO_BENEFICIO_DE_PENSAO("VALOR MENSAL DO BENEFÍCIO DE PENSÃO", 24),
        VALOR_TOTAL_PENSAO("VALOR TOTAL DA PENSÃO", 25),
        VALOR_PERCENTUAL_QUOTA_RECEBIDA_PELO_PENSIONISTA("VALOR PERCENTUAL DA QUOTA RECEBIDA PELO PENSIONISTA", 26),
        VALOR_MENSAL_DA_CONTRIBUCAO_PREVIDENCIARIA("VALOR MENSAL DA CONTRIBUÇÃO PREVIDENCIÁRIA", 27),
        VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA("VALOR MENSAL DA COMPENSAÇÃO PREVIDENCIÁRIA", 28),
        IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS("INDICADOR DE PARIDADE COM SERVIDORES", 29),
        CONDICAO_DO_PENSIONISTA("CONDIÇÃO DO PENSIONISTA", 30),
        DURACAO_DO_BENEFICIO("DURAÇÃO DO BENEFÍCIO", 31),
        TEMPO_DE_DURACAO_DO_BENEFICIO("TEMPO DE DURAÇÃO DO BENEFÍCIO", 32),
        PREVIDENCIA_COMPLEMENTAR("PREVIDÊNCIA COMPLEMENTAR", 33),
        TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO("TETO CONSTITUCIONAL REMUNERATÓRIO ESPECÍFICO", 34);


        private String descricao;
        private int index;

        ColunaPensionistas(String descricao, int index) {
            this.descricao = descricao;
            this.index = index;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum ColunaAtivosFalecidosOuExonerados {
        ANO_REFERENCIA("ANO DE REFERÊNCIA", 0),
        MES("MÊS", 1),
        CODIGO_DO_ENTE_NO_IBGE("CÓDIGO DO ENTE NO IBGE", 2),
        NOME_DO_ENTE("NOME DO ENTE", 3),
        SIGLA_DA_UF_DO_ENTE("SIGLA DA UF DO ENTE", 4),
        COMPOSICAO_DA_MASSA_A("COMPOSIÇÃO DA MASSA", 5),
        COMPOSICAO_DA_MASSA_B("COMPOSIÇÃO DA MASSA", 6),
        CNPJ("CNPJ", 7),
        DENOMINACAO_ORGAO_ENTIDADE("DENOMINAÇÃO DO ÓRGÃO/ENTIDADE", 8),
        PODER("PODER", 9),
        TIPO("TIPO", 10),
        A_POPULACAO_COBERTA("A - POPULAÇÃO COBERTA", 11),
        B_ESPECIFICACAO_DO_TIPO_DE_CARGO("B- ESPECIFICAÇÃO DO TIPO DE CARGO", 12),
        C_CRITERIO_DE_ELEGIBILIDADE("C - CRITÉRIO DE ELEGIBILIDADE", 13),
        IDENTIFICACAO_DO_SEGURADO("IDENTIFICAÇÃO DO SEGURADO", 14),
        IDENTIFICACAO_DO_SEGURADO_CPF("IDENTIFICAÇÃO DO SEGURADO", 15),
        IDENTIFICACAO_DO_SEGURADO_PIS_PASEP("IDENTIFICAÇÃO DO SEGURADO", 16),
        SEXO("SEXO", 17),
        ESTADO_CIVIL("ESTADO CIVIL", 18),
        DATA_DE_NASCIMENTO("DATA DE NASCIMENTO", 19),
        SITUACAO_FUNCIONAL("SITUAÇÃO", 20),
        DATA_SITUACAO_ATUAL("DATA DA SITUAÇÃO ATUAL", 21),
        TIPO_DE_VINCULO("TIPO DE VÍNCULO", 22),
        DATA_DE_INGRESSO_SERVICO_PUBLICO("DATA DE INGRESSO NO SERVIÇO PÚBLICO", 23),
        DATA_DE_INGRESSO_NO_ENTE("DATA DE INGRESSO NO ENTE", 24),
        DATA_DE_INGRESSO_NA_CARREIRA_ATUAL("DATA DE INGRESSO NA CARREIRA ATUAL", 25),
        IDENTIFICACAO_DA_CARREIRA_ATUAL("IDENTIFICAÇÃO DA CARREIRA ATUAL", 26),
        DATA_DE_INICIO_DE_EXERCICIO_NO_CARGO_ATUAL("DATA DE INÍCIO DE EXERCÍCIO NO CARGO ATUAL", 27),
        IDENTIFICACAO_DO_CARGO_ATUAL("IDENTIFICAÇÃO DO CARGO ATUAL", 28),
        BASE_DE_CALCULO_MENSAL_DO_SERVIDOR_ATIVO("BASE DE CÁLCULO MENSAL DO SERVIDOR ATIVO", 29),
        REMUNERACAO_MENSAL_TOTAL_DO_SERVIDOR_ATIVO("REMUNERAÇÃO MENSAL TOTAL DO SERVIDOR ATIVO", 30),
        CONTRIBUICAO_MENSAL("CONTRIBUIÇÃO MENSAL", 31),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_ATIVO_ANTERIOR_A_ADMISSAO_NO_ENTE_PARA_O_RGPS("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR ATIVO ANTERIOR À ADMISSÃO NO ENTE PARA O RGPS", 32),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_MUNICIPAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA MUNICIPAL\", ANTERIOR À ADMISSÃO NO ENTE", 33),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_ESTADUAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA ESTADUAL\", ANTERIOR À ADMISSÃO NO ENTE", 34),
        TEMPO_DE_CONTRIBUICAO_DO_SERVIDOR_PARA_OUTROS_RPPS_ANTERIOR_A_ADMISSAO_NO_ENTE_FEDERAL("TEMPO DE CONTRIBUIÇÃO DO SERVIDOR PARA OUTROS \"RPPS DA ESFERA FEDERAL\", ANTERIOR À ADMISSÃO NO ENTE", 35),
        NUMERO_DE_DEPENDENTES_DO_SERVIDOR("NÚMERO DE DEPENDENTES DO SERVIDOR", 36),
        SEGURADO_EM_ABONO_DE_PERMANENCIA("SEGURADO EM ABONO DE PERMANÊNCIA", 37),
        DATA_DE_INICIO_DE_ABONO_DE_PERMANENCIA_DO_SERVIDOR_ATIVO("DATA DE INÍCIO DE ABONO DE PERMANÊNCIA DO SERVIDOR ATIVO", 38),
        PREVIDENCIA_COMPLEMENTAR("PREVIDÊNCIA COMPLEMENTAR", 39),
        TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO("TETO CONSTITUCIONAL REMUNERATÓRIO ESPECÍFICO (DOS SERVIDORES DO RESPECTIVO PODER)", 40);

        private String descricao;
        private int index;

        ColunaAtivosFalecidosOuExonerados(String descricao, int index) {
            this.descricao = descricao;
            this.index = index;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum ColunaAposentadosFalecidos {
        ANO_REFERENCIA("ANO DE REFERÊNCIA", 0),
        MES("MÊS", 1),
        CODIGO_DO_ENTE_NO_IBGE("CÓDIGO DO ENTE NO IBGE", 2),
        NOME_DO_ENTE("NOME DO ENTE", 3),
        SIGLA_DA_UF_DO_ENTE("SIGLA DA UF DO ENTE", 4),
        COMPOSICAO_DA_MASSA_A("COMPOSIÇÃO DA MASSA", 5),
        COMPOSICAO_DA_MASSA_B("COMPOSIÇÃO DA MASSA", 6),
        CNPJ("CNPJ", 7),
        DENOMINACAO_ORGAO_ENTIDADE("DENOMINAÇÃO DO ÓRGÃO/ENTIDADE", 8),
        PODER("PODER", 9),
        TIPO("TIPO", 10),
        A_POPULACAO_COBERTA("A - POPULAÇÃO COBERTA", 11),
        B_ESPECIFICACAO_DO_TIPO_DE_CARGO("B- ESPECIFICAÇÃO DO TIPO DE CARGO", 12),
        TIPO_DO_BENEFICIO("TIPO DO BENEFÍCIO", 13),
        IDENTIFICACAO_DO_APOSENTADO_MILITAR_MATRICULA("IDENTIFICAÇÃO DO APOSENTADO / MILITAR INATIVO", 14),
        IDENTIFICACAO_DO_APOSENTADO_MILITAR_CPF("IDENTIFICAÇÃO DO APOSENTADO / MILITAR INATIVO", 15),
        IDENTIFICACAO_DO_APOSENTADO_MILITAR_PIS_PASEP("IDENTIFICAÇÃO DO APOSENTADO / MILITAR INATIVO", 16),
        SEXO_DO_APOSENTADO_MILITAR("SEXO DO APOSENTADO/ MILITAR INATIVO", 17),
        ESTADO_CIVIL_DO_APOSENTADO_MILITAR("ESTADO CIVIL DO APOSENTADO/ MILITAR INATIVO", 18),
        DATA_DE_NASCIMENTO_APOSENTADO_MILITAR("DATA DE NASCIMENTO DO APOSENTADO/ MILITAR INATIVO", 19),
        SITUACAO("SITUAÇÃO", 20),
        DATA_SITUACAO("DATA DA SITUAÇÃO", 21),
        DATA_DE_INGRESSO_SERVICO_PUBLICO("DATA DE INGRESSO NO SERVIÇO PÚBLICO", 22),
        DATA_DE_INGRESSO_NO_ENTE("DATA DE INGRESSO NO ENTE", 23),
        DATA_DE_INICIO_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR("DATA DE INÍCIO DO BENEFÍCIO DE APOSENTADORIA / INATIVIDADE DO MILITAR", 24),
        VALOR_MENSAL_DO_BENEFICIO_DE_APOSENTADORIA_INATIVIDADE_MILITAR("VALOR MENSAL DO BENEFÍCIO DE APOSENTADORIA / INATIVIDADE DO MILITAR", 25),
        CONTRIBUICAO_MENSAL("CONTRIBUIÇÃO MENSAL", 26),
        VALOR_MENSAL_DA_COMPENSACAO_PREVIDENCIARIA("VALOR MENSAL DA COMPENSAÇÃO PREVIDENCIÁRIA", 27),
        IDENTIFICADOR_DE_PARIDADE_COM_SERVIDORES_ATIVOS("IDENTIFICADOR DE PARIDADE COM SERVIDORES ATIVOS", 28),
        CONDICAO_DO_APOSENTADO_MILITAR("CONDIÇÃO DO APOSENTADO / MILITAR INATIVO", 29),
        PREVIDENCIA_COMPLEMENTAR("PREVIDÊNCIA COMPLEMENTAR", 30),
        TETO_CONSTITUCIONAL_REMUNERATORIO_ESPECIFICO("TETO CONSTITUCIONAL REMUNERATÓRIO ESPECÍFICO (DOS SERVIDORES DO RESPECTIVO PODER)", 31),
        NUMERO_DE_DEPENDENTES_DO_APOSENTADO_MILITAR_INATIVO("NÚMERO DE DEPENDENTES DO APOSENTADO / MILITAR INATIVO", 32);

        private String descricao;
        private int index;

        ColunaAposentadosFalecidos(String descricao, int index) {
            this.descricao = descricao;
            this.index = index;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum ColunaDependentes {
        ANO_REFERENCIA("ANO DE REFERÊNCIA", 0),
        MES("MÊS", 1),
        CODIGO_DO_ENTE_NO_IBGE("CÓDIGO DO ENTE NO IBGE", 2),
        NOME_DO_ENTE("NOME DO ENTE", 3),
        SIGLA_DA_UF_DO_ENTE("SIGLA DA UF DO ENTE", 4),
        COMPOSICAO_DA_MASSA("COMPOSIÇÃO DA MASSA", 5),
        TIPO_DE_FUNDO("TIPO DE FUNDO", 6),
        CNPJ("CNPJ", 7),
        NOME_DO_ORGAO_ENTIDADE("NOME DO ÓRGÃO/ENTIDADE", 8),
        CODIGO_DO_PODER("CÓDIGO DO PODER", 9),
        CODIGO_DO_TIPO_DE_PODER("CÓDIGO DO TIPO  DE PODER", 10),
        MATRICULA_DO_SEGURADO_SERVIDOR("MATRÍCULA  DO SEGURADO SERVIDOR", 11),
        CPF_DO_SEGURADO_SERVIDOR("CPF DO SEGURADO SERVIDOR", 12),
        PASEP_DO_SEGURADO_SERVIDOR("PASEP DO SEGURADO  SERVIDOR", 13),
        SEXO_DO_SEGURADO_SERVIDOR("SEXO DO SEGURADO SERVIDOR", 14),
        IDENTIFICADOR_UNICO_DO_DEPENDENTE("IDENTIFICADOR ÚNICO DO DEPENDENTE", 15),
        CPF_DO_DEPENDENTE("CPF DO DEPENDENTE", 16),
        DATA_DE_NASCIMENTO_DO_DEPENDENTE("DATA DE NASCIMENTO DO DEPENDENTE", 17),
        SEXO_DO_DEPENDENTE("SEXO DO DEPENDENTE", 18),
        CONDICAO_DO_DEPENDENTE("CONDIÇÃO DO DEPENDENTE", 19),
        TIPO_DEPENDENCIA_DEPENDENTE_COM_SEGURADO_SERVIDOR("TIPO DE DEPENDÊNCIA DO DEPENDENTE COM O SEGURADO SERVIDOR", 20);

        private String descricao;
        private int index;

        ColunaDependentes(String descricao, int index) {
            this.descricao = descricao;
            this.index = index;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum TipoAposentadoria {
        APOSENTADORIA_POR_INVALIDEZ(1, "Aposentadoria por invalidez"),
        APOSENTADORIA_VOLUNTARIA_POR_TEMPO_CONTRIBUICAO_IDADE(3, "Aposent. voluntária por tempo de contribuição e idade"),
        APOSENTADORIA_VOLUNTARIA_POR_IDADE(4, "Aposent. voluntária por idade"),
        APOSENTADORIA_COMPULSORIA(2, "Aposentadoria compulsória"),
        APOSENTADORIA_ESPECIAL(5, "Aposentadoria Especial");

        private int codigo;
        private String descricao;

        TipoAposentadoria(int codigo, String descricao) {
            this.codigo = codigo;
            this.descricao = descricao;
        }

        public int getCodigo() {
            return codigo;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return codigo + " - " + descricao;
        }
    }

    public enum OrigemPensao {
        POR_MORTE_DE_SERVIDOR_EM_ATIVIDADE(1, "Por morte de servidor em atividade"),
        POR_MORTE_DE_SERVIDOR_POSENTADO_POR_INVALIDEZ(2, "Por morte de servidor aposentado por invalidez"),
        POR_MORTE_DE_SERVIDOR_APOSENTADO_POR_VOLUNTARIO_OU_COMPULSORIO(3, "Por morte de servidor aposentado por voluntário ou compulsório");

        private int codigo;
        private String descricao;

        OrigemPensao(int codigo, String descricao) {
            this.codigo = codigo;
            this.descricao = descricao;
        }

        public int getCodigo() {
            return codigo;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return codigo + " - " + descricao;
        }
    }

    public enum SimNao {
        SIM("S", "Sim"),
        NAO("N", "Não");

        private String sigla;
        private String descricao;

        SimNao(String sigla, String descricao) {
            this.sigla = sigla;
            this.descricao = descricao;
        }

        public String getSigla() {
            return sigla;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum TipoVinculo {
        ESTATUTARIO(0, 1, "Estatutário"),
        CELETISTA(1, 2, "Celetista"),
        EFETIVO(2, 3, "Efetivo"),
        EXCLUSIVAMENTE_COMISSIONADO(3, 4, "Exclusivamente Comissionado");

        private int index;
        private int codigo;
        private String descricao;

        TipoVinculo(int index, int codigo, String descricao) {
            this.index = index;
            this.codigo = codigo;
            this.descricao = descricao;
        }

        public int getCodigo() {
            return codigo;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum TipoLog {
        TODOS("Todos", ""),
        SUCESSO("Sucesso", "green"),
        ALERTA("Alerta", "#f89406"),
        ERRO("Erro", "red");
        private String descricao;
        private String cor;

        TipoLog(String descricao, String cor) {
            this.descricao = descricao;
            this.cor = cor;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getCor() {
            return cor;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
