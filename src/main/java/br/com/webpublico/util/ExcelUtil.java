package br.com.webpublico.util;

import br.com.webpublico.consultaentidade.TAG;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Arquivo;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.*;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by romanini on 03/06/15.
 */
public class ExcelUtil {

    public static final String XLS_CONTENTTYPE = "application/xls";
    public static final String XLS_EXTENCAO = ".xls";
    public static final String XLSX_CONTENTTYPE = "application/xlsx";
    public static final String XLSX_EXTENCAO = ".xlsx";
    public static final String CSV_CONTENTTYPE = "application/csv";
    public static final String CSV_EXTENCAO = ".csv";
    public static final String MUNICIPIO = "MUNICÍPIO DE RIO BRANCO";
    public static final String WEBPUBLICO = "WebPúblico - Sistema Integrado de Gestão Pública";
    private static final String DELIMITER = ";";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private StreamedContent fileDownload;
    private File file = null;
    private String nomeArquivo;
    private String contentType;
    private String extensao;
    private Boolean ajustarTamanhoColuna;

    public Boolean getAjustarTamanhoColuna() {
        return ajustarTamanhoColuna != null ? ajustarTamanhoColuna : true;
    }

    public void setAjustarTamanhoColuna(Boolean ajustarTamanhoColuna) {
        this.ajustarTamanhoColuna = ajustarTamanhoColuna;
    }

    public static String getValorCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            BigDecimal valor = BigDecimal.valueOf(cell.getNumericCellValue());
            return valor + "";
        } else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getStringCellValue();
        } else if (Cell.CELL_TYPE_BOOLEAN == cell.getCellType()) {
            return cell.getBooleanCellValue() ? "Sim " : "Não";
        } else if (Cell.CELL_TYPE_FORMULA == cell.getCellType()) {
            if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCachedFormulaResultType() == HSSFCell.CELL_TYPE_NUMERIC) {
                return String.valueOf(cell.getNumericCellValue());
            } else {
                return "";
            }
        } else if (Cell.CELL_TYPE_ERROR == cell.getCellType()) {
            return "error";
        } else if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
            return "";
        } else {
            return "";

        }
    }

    public static Enum getEnum(String descricao, Object[] valores) {
        for (Object value : Lists.newArrayList(valores)) {
            Class<?> classe = value.getClass();
            if (classe.isEnum()) {
                for (Field field : classe.getDeclaredFields()) {
                    if (Util.getDescricaoDoEnum(classe, field).equals(descricao)) {
                        return Enum.valueOf((Class<? extends Enum>) classe, field.getName());
                    }
                }
            }
        }
        return null;
    }

    public static String getValorCellParaCpf(Cell cell) {
        if (cell == null) {
            return "";
        }
        String valor = "";
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            valor = cell.getStringCellValue().length() < 11 ? Util.zerosAEsquerda(cell.getStringCellValue(), 11) : cell.getStringCellValue();
        } else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            valor = cell.getStringCellValue();
        }
        valor = valor.length() < 11 ? Util.zerosAEsquerda(valor, 11) : valor;
        return valor;
    }

    public void gerarExcelXLSX(String titulo, String nomeDoArquivo, List<String> titulos, List<Object[]> objetos, String filtros, Boolean ajustarColuna) {
        this.contentType = XLSX_CONTENTTYPE;
        this.extensao = XLSX_EXTENCAO;
        this.ajustarTamanhoColuna = ajustarColuna;
        gerarExcel(titulo, nomeDoArquivo, titulos, objetos, filtros);
    }

    public void gerarExcel(String titulo, String nomeDoArquivo, List<String> titulos, List<Object[]> objetos, String filtros, Boolean ajustarColuna) {
        this.ajustarTamanhoColuna = ajustarColuna;
        gerarExcel(titulo, nomeDoArquivo, titulos, objetos, filtros);
    }

    public void gerarExcel(String titulo, String nomeDoArquivo, List<String> titulos, List<Object[]> objetos, String filtros) {
        gerarExcel(titulo, nomeDoArquivo, titulos, objetos, filtros,
            getSistemaControlador().getUsuarioCorrente().getPessoaFisica().toString(), getSistemaControlador().getDataOperacao());
    }

    public void gerarExcel(String titulo,
                           String nomeDoArquivo,
                           List<String> titulos,
                           List<Object[]> objetos,
                           String filtros,
                           String usuario,
                           Date geradoEm) {
        try {
            if (contentType == null) {
                contentType = XLS_CONTENTTYPE;
                extensao = XLS_EXTENCAO;
            }
            nomeArquivo = nomeDoArquivo;
            String nomePlanilha = nomeDoArquivo + "_temp";
            file = File.createTempFile(nomePlanilha, extensao);

            int linhaInicial = 0;

            FileOutputStream fout = new FileOutputStream(file);
            XSSFWorkbook pastaDeTrabalho = new XSSFWorkbook();
            XSSFSheet sheet = criarSheet(pastaDeTrabalho, titulo);


            XSSFRow municipio = criaRow(sheet, linhaInicial);
            criaCell(municipio, linhaInicial).setCellValue(MUNICIPIO);
            linhaInicial++;

            XSSFRow tituloDaPlanilha = criaRow(sheet, linhaInicial);
            criaCell(tituloDaPlanilha, 0).setCellValue(titulo);
            linhaInicial++;
            if (filtros != null) {
                XSSFRow filtrosUtilizados = criaRow(sheet, linhaInicial);
                criaCell(filtrosUtilizados, 0).setCellValue(filtros.trim());
            }
            linhaInicial++;
            linhaInicial++;


            XSSFRow cabecalho = criaRow(sheet, linhaInicial);
            for (String atributo : titulos) {
                criaCell(cabecalho, titulos.indexOf(atributo)).setCellValue(atributo);
            }
            linhaInicial++;
            for (Object o : objetos) {
                XSSFRow linha = criaRow(sheet, linhaInicial);

                Object[] objeto = (Object[]) o;
                int i = 0;
                for (Object atributo : objeto) {
                    if (atributo != null) {
                        if (atributo instanceof BigDecimal) {
                            XSSFCell hssfCell = criaCell(linha, i);
                            hssfCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            hssfCell.setCellValue(((BigDecimal) atributo).doubleValue());
                        } else {
                            criaCell(linha, i).setCellValue(atributo.toString());
                        }
                    }
                    i++;
                }
                linhaInicial++;
            }

            if (getAjustarTamanhoColuna()) {
                for (int i = 0; i < titulos.size(); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            linhaInicial = linhaInicial + 5;

            XSSFRow rowUsuario = criaRow(sheet, linhaInicial);
            criaCell(rowUsuario, 1).setCellValue(usuario);
            linhaInicial++;

            XSSFRow rodape = criaRow(sheet, linhaInicial);
            criaCell(rodape, 1).setCellValue(WEBPUBLICO);

            linhaInicial++;

            XSSFRow rowGeradoEm = criaRow(sheet, linhaInicial);
            criaCell(rowGeradoEm, 1).setCellValue("Gerado em - " + DataUtil.getDataFormatada(geradoEm));

            pastaDeTrabalho.write(fout);
        } catch (IOException ioe) {
            FacesUtil.addError("Erro ao gerar o arquivo", "");
        }
    }

    public void gerarExcelUtilizandoArquivoLayout(List<Object[]> objetos, Arquivo arquivoLayout, Integer numeroAba, Integer numeroLinhaInicial, boolean ajustarTamanhoColuna, String novoNomeAba) {
        if (isXlsx(arquivoLayout.getNome())) {
            gerarExcelXLSXUtilizandoArquivoLayout(objetos, arquivoLayout, numeroAba, numeroLinhaInicial, ajustarTamanhoColuna, novoNomeAba);
        } else {
            gerarExcelXLSUtilizandoArquivoLayout(objetos, arquivoLayout, numeroAba, numeroLinhaInicial, ajustarTamanhoColuna, novoNomeAba);
        }
    }

    public void gerarExcelXLSXUtilizandoArquivoLayout(List<Object[]> objetos, Arquivo arquivoLayout, Integer numeroAba, Integer numeroLinhaInicial, boolean ajustarTamanhoColuna, String novoNomeAba) {
        try {
            this.ajustarTamanhoColuna = ajustarTamanhoColuna;
            this.contentType = XLSX_CONTENTTYPE;
            this.extensao = XLSX_EXTENCAO;
            this.file = File.createTempFile(arquivoLayout.getNome(), extensao);
            FileOutputStream fout = new FileOutputStream(this.file);
            XSSFWorkbook wb = new XSSFWorkbook(arquivoLayout.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(numeroAba);
            alterarTagsXLSX(sheet);
            if (novoNomeAba != null) {
                wb.setSheetName(wb.getSheetIndex(sheet), novoNomeAba);
            }
            for (Object o : objetos) {
                XSSFRow linha = criaRow(sheet, numeroLinhaInicial);
                Object[] objeto = (Object[]) o;
                int i = 0;
                for (Object atributo : objeto) {
                    if (atributo != null) {
                        if (atributo instanceof BigDecimal) {
                            XSSFCell hssfCell = criaCell(linha, i);
                            hssfCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            hssfCell.setCellValue(((BigDecimal) atributo).doubleValue());
                        } else {
                            criaCell(linha, i).setCellValue(atributo.toString());
                        }
                    }
                    if (getAjustarTamanhoColuna()) {
                        sheet.autoSizeColumn(i);
                    }
                    i++;
                }
                numeroLinhaInicial++;
            }
            wb.write(fout);
        } catch (IOException ioe) {
            FacesUtil.addError("Erro ao gerar o arquivo", "");
        }
    }

    public void gerarExcelXLSUtilizandoArquivoLayout(List<Object[]> objetos, Arquivo arquivoLayout, Integer numeroAba, Integer numeroLinhaInicial, boolean ajustarTamanhoColuna, String novoNomeAba) {
        try {
            this.ajustarTamanhoColuna = ajustarTamanhoColuna;
            this.contentType = XLS_CONTENTTYPE;
            this.extensao = XLS_EXTENCAO;
            this.file = File.createTempFile(arquivoLayout.getNome(), this.extensao);
            FileOutputStream fout = new FileOutputStream(this.file);
            HSSFWorkbook wb = new HSSFWorkbook(arquivoLayout.getInputStream());
            HSSFSheet sheet = wb.getSheetAt(numeroAba);
            alterarTagsXLS(sheet);
            if (novoNomeAba != null) {
                wb.setSheetName(wb.getSheetIndex(sheet), novoNomeAba);
            }
            for (Object o : objetos) {
                HSSFRow linha = criaRow(sheet, numeroLinhaInicial);
                Object[] objeto = (Object[]) o;
                int i = 0;
                for (Object atributo : objeto) {
                    if (atributo != null) {
                        if (atributo instanceof BigDecimal) {
                            HSSFCell hssfCell = criaCell(linha, i);
                            hssfCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            hssfCell.setCellValue(((BigDecimal) atributo).doubleValue());
                        } else {
                            criaCell(linha, i).setCellValue(atributo.toString());
                        }
                    }
                    if (getAjustarTamanhoColuna()) {
                        sheet.autoSizeColumn(i);
                    }
                    i++;
                }
                numeroLinhaInicial++;
            }
            wb.write(fout);
        } catch (IOException ioe) {
            FacesUtil.addError("Erro ao gerar o arquivo", "");
        }
    }

    private void alterarTagsXLS(HSSFSheet sheet) {
        Iterator rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            HSSFRow row = (HSSFRow) rowIterator.next();
            Iterator cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                HSSFCell cell = (HSSFCell) cellIterator.next();
                cell.setCellValue(cell.getStringCellValue().replace("$" + TAG.DATA_OPERACAO.name(), getSistemaControlador().getDataOperacaoFormatada()));
            }
        }
    }

    private void alterarTagsXLSX(XSSFSheet sheet) {
        Iterator rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();
            Iterator cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                XSSFCell cell = (XSSFCell) cellIterator.next();
                cell.setCellValue(cell.getStringCellValue().replace("$" + TAG.DATA_OPERACAO.name(), getSistemaControlador().getDataOperacaoFormatada()));
            }
        }
    }

    public boolean isXlsx(String nomeArquivo) {
        return nomeArquivo != null && nomeArquivo.toLowerCase().trim().contains(XLSX_EXTENCAO);
    }

    public void gerarCSV(String titulo, String nomeDoArquivo, List<String> titulos, List<Object[]> objetos, boolean gerarCabecalhoAndRodape) {
        gerarCSV(titulo, nomeDoArquivo, titulos, objetos, gerarCabecalhoAndRodape, true, true);
    }

    public void gerarCSV(String titulo, String nomeDoArquivo, List<String> titulos, List<Object[]> objetos, boolean gerarCabecalhoAndRodape, Boolean substituirPontoNoBigDecimal, Boolean adicionarDelimitadorNaUltimaColunaDaLinha) {
        BufferedWriter fileWriter = null;
        try {
            if (contentType == null) {
                contentType = CSV_CONTENTTYPE;
                extensao = CSV_EXTENCAO;
            }
            nomeArquivo = nomeDoArquivo;
            String nomePlanilha = nomeDoArquivo + "_temp";
            file = File.createTempFile(nomePlanilha, extensao);
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "ISO-8859-1"));

            if (gerarCabecalhoAndRodape) {
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(MUNICIPIO);
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(titulo);
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(NEW_LINE_SEPARATOR);
            }


            for (String t : titulos) {
                fileWriter.append(t);

                if (adicionarDelimitadorNaUltimaColunaDaLinha) {
                    fileWriter.append(DELIMITER);
                } else if (!titulos.get(titulos.size() - 1).equals(t)) {
                    fileWriter.append(DELIMITER);
                }
            }
            fileWriter.append(NEW_LINE_SEPARATOR);


            for (Object o : objetos) {
                Object[] objeto = (Object[]) o;
                for (Object atributo : objeto) {
                    if (atributo != null) {
                        if (atributo instanceof BigDecimal && substituirPontoNoBigDecimal) {
                            fileWriter.append(atributo.toString().replace(".", ","));
                        } else {
                            fileWriter.append(atributo.toString());
                        }
                        if (adicionarDelimitadorNaUltimaColunaDaLinha) {
                            fileWriter.append(DELIMITER);
                        } else if (!objeto[objeto.length - 1].equals(atributo)) {
                            fileWriter.append(DELIMITER);
                        }
                    }
                }
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            if (gerarCabecalhoAndRodape) {
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(getSistemaControlador().getUsuarioCorrente().getPessoaFisica().toString());
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append(WEBPUBLICO);
                fileWriter.append(NEW_LINE_SEPARATOR);
                fileWriter.append("Gerado em - " + DataUtil.getDataFormatada(getSistemaControlador().getDataOperacao()));
            }
        } catch (Exception e) {
            FacesUtil.addError("Erro ao gerar o arquivo", e.getMessage());
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public XSSFSheet criarSheet(XSSFWorkbook pasta, String nomeSheet) {
        return pasta.createSheet(WorkbookUtil.createSafeSheetName(nomeSheet));
    }

    public StreamedContent fileDownload() throws FileNotFoundException, IOException {
        InputStream stream = new FileInputStream(file);
        fileDownload = new DefaultStreamedContent(stream, contentType, nomeArquivo + extensao);
        return fileDownload;
    }

    public XSSFCell criaCell(XSSFRow row, Integer posicao) {
        return row.getCell(posicao) == null ? row.createCell(posicao) : row.getCell(posicao);
    }

    public HSSFCell criaCell(HSSFRow row, Integer posicao) {
        return row.getCell(posicao) == null ? row.createCell(posicao) : row.getCell(posicao);
    }

    public XSSFCell criarCell(XSSFWorkbook workbook,
                              XSSFRow row,
                              Integer posicao,
                              CellStyle cellStyle) {
        XSSFCell cell = row.getCell(posicao);
        if (cell == null)
            cell = row.createCell(posicao);
        cell.setCellStyle(cellStyle);
        return cell;
    }

    public XSSFCell criarCellString(XSSFWorkbook workbook,
                                    XSSFRow row,
                                    Integer posicao,
                                    CellStyle cellStyle,
                                    String value) {
        XSSFCell cell = row.getCell(posicao);
        if (cell == null)
            cell = row.createCell(posicao);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        return cell;
    }

    public XSSFCell criaCellDateTime(XSSFWorkbook workbook,
                                     XSSFRow row,
                                     Integer posicao,
                                     CellStyle cellStyle,
                                     Date value) {
        XSSFCell cell = row.getCell(posicao);
        if (cell == null) {
            cell = row.createCell(posicao);
        }
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        formatCell(workbook, cell, "dd/MM/yyyy hh:mm");
        return cell;
    }

    private void formatCell(XSSFWorkbook workbook, XSSFCell cell, String format) {
        CreationHelper createHelper = workbook.getCreationHelper();
        cell.getCellStyle().setDataFormat(
            createHelper.createDataFormat().getFormat(format));
    }

    public XSSFCell criarCell(XSSFWorkbook workbook,
                              XSSFRow row,
                              Integer posicao,
                              CellStyle cellStyle,
                              String value) {
        XSSFCell cell = criaCell(row, posicao);
        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        return cell;
    }

    public XSSFCell criarCellDate(XSSFWorkbook workbook,
                                  XSSFRow row,
                                  Integer posicao,
                                  CellStyle cellStyle,
                                  Date value) {
        XSSFCell cell = criaCell(row, posicao);


        cell.setCellValue(value);
        cell.setCellStyle(cellStyle);
        formatCell(workbook, cell, "dd/MM/yyyy");
        return cell;
    }

    public XSSFCell criarCellMoeda(XSSFWorkbook workbook,
                                   XSSFRow row,
                                   Integer posicao,
                                   CellStyle cellStyle,
                                   BigDecimal value) {
        XSSFCell cell = criaCell(row, posicao);
        cell.setCellValue(value.doubleValue());
        cell.setCellStyle(cellStyle);
        formatCell(workbook, cell, "R$ #,##0.00");
        return cell;
    }

    public XSSFCell criarCellPorcentagem(XSSFWorkbook workbook,
                                         XSSFRow row,
                                         Integer posicao,
                                         CellStyle cellStyle,
                                         BigDecimal value) {
        if (value == null) value = BigDecimal.ZERO;
        XSSFCell cell = criaCell(row, posicao);
        cell.setCellValue(value.doubleValue() / 100);
        cell.setCellStyle(cellStyle);
        formatCell(workbook, cell, "% #,##0.00");
        return cell;
    }

    private HSSFCell criaCell(HSSFRow row, Integer posicao, HSSFCellStyle estilo) {
        HSSFCell celula = row.getCell(posicao) == null ? row.createCell(posicao) : row.getCell(posicao);
        celula.setCellStyle(estilo);
        return celula;
    }

    public XSSFRow criaRow(XSSFSheet sheet, Integer linha) {
        return sheet.getRow(linha) == null ? sheet.createRow(linha) : sheet.getRow(linha);
    }

    public HSSFRow criaRow(HSSFSheet sheet, Integer linha) {
        return sheet.getRow(linha) == null ? sheet.createRow(linha) : sheet.getRow(linha);
    }

    public XSSFRow criarRow(XSSFSheet sheet, Integer linha, CellStyle cellStyle) {
        XSSFRow row = sheet.getRow(linha);
        if (row == null) {
            row = sheet.createRow(linha);
        }
        row.setRowStyle(cellStyle);
        return row;
    }

    public CellStyle styleFonteArial10(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setBold(false);
        font.setItalic(false);
        cellStyle.setFont(font);
        return cellStyle;
    }

    public CellStyle styleFonteArial10Negrito(XSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        font.setBold(true);
        font.setItalic(false);
        cellStyle.setFont(font);
        return cellStyle;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getExtensao() {
        return extensao;
    }

    public void setExtensao(String extensao) {
        this.extensao = extensao;
    }

    public static Integer getNumeroLinhas(XSSFSheet sheet) {
        Integer numeroLinhas = 0;
        Iterator<Row> itr = sheet.iterator();
        if (itr.hasNext()) itr.next();
        while (itr.hasNext()) {
            Row row = itr.next();
            if (row.getCell(0) == null)
                break;
            numeroLinhas++;
        }
        return numeroLinhas;
    }

    public static String[] splitCSV(String linha) {
        char separator = ',';
        char aspasDuplas = '"';
        final List<String> partes = Lists.newArrayList();
        final StringBuilder sb = new StringBuilder();
        for (boolean hasAspasDuplas = false; ; sb.append('\n')) {
            if (linha == null)
                break;
            for (int i = 0, len = linha.length(); i < len; i++) {
                final char c = linha.charAt(i);
                if (c == aspasDuplas) {
                    if (hasAspasDuplas && i < len - 1 && linha.charAt(i + 1) == aspasDuplas) {
                        sb.append(c);
                        i++;
                    } else {
                        if (hasAspasDuplas) {
                            if (i == len - 1 || linha.charAt(i + 1) == separator) {
                                hasAspasDuplas = false;
                                continue;
                            }
                        } else {
                            if (sb.length() == 0) {
                                hasAspasDuplas = true;
                                continue;
                            }
                        }
                        sb.append(c);
                    }
                } else if (c == separator && !hasAspasDuplas) {
                    partes.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
            if (!hasAspasDuplas)
                break;
        }
        partes.add(sb.toString());
        return partes.toArray(new String[partes.size()]);
    }
}
