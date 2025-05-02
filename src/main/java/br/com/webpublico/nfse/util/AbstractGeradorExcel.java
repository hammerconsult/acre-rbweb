package br.com.webpublico.nfse.util;

import br.com.webpublico.util.ExcelUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public abstract class AbstractGeradorExcel {

    protected static final Logger logger = LoggerFactory.getLogger(GeradorExcelArquivoDesif.class);

    protected ExcelUtil excelUtil;
    protected XSSFWorkbook workbook;
    protected Integer linha;

    public AbstractGeradorExcel() {
        this.excelUtil = new ExcelUtil();
        this.linha = 0;
    }

    public File gerar() {
        try {
            String nomeArquivo = "fileExport";
            String nomePlanilha = nomeArquivo + System.currentTimeMillis() + "_temp";
            File file = File.createTempFile(nomePlanilha, ExcelUtil.XLSX_EXTENCAO);
            FileOutputStream fout = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            popularWorkbook();
            workbook.write(fout);
            return file;
        } catch (Exception e) {
            logger.error("Erro ao gerar excel {}", e);
        }

        return null;
    }

    public abstract void popularWorkbook();

    public void adicionarMunicipio(XSSFSheet sheet) {
        XSSFRow row = excelUtil.criarRow(sheet, linha++, excelUtil.styleFonteArial10(workbook));
        excelUtil.criarCell(workbook, row, 0, excelUtil.styleFonteArial10(workbook)).setCellValue(ExcelUtil.MUNICIPIO);
    }

    public void adicionarLista(XSSFSheet sheet,
                               List<String> titulos, List<Object[]> objetos) {
        XSSFRow cabecalho = excelUtil.criaRow(sheet, linha++);
        for (String titulo : titulos) {
            excelUtil.criarCellString(workbook, cabecalho, titulos.indexOf(titulo), excelUtil.styleFonteArial10Negrito(workbook), titulo);
        }
        for (Object objeto : objetos) {
            XSSFRow row = excelUtil.criaRow(sheet, linha++);

            Object[] item = (Object[]) objeto;
            int coluna = 0;
            for (Object valorItem : item) {
                if (valorItem != null) {
                    if (valorItem instanceof BigDecimal) {
                        excelUtil.criarCellMoeda(workbook, row, coluna, excelUtil.styleFonteArial10(workbook), (BigDecimal) valorItem);
                    } else if (valorItem instanceof Date) {
                        excelUtil.criarCellDate(workbook, row, coluna, excelUtil.styleFonteArial10(workbook), (Date) valorItem);
                    } else {
                        excelUtil.criarCellString(workbook, row, coluna, excelUtil.styleFonteArial10(workbook), (String) valorItem);
                    }
                }
                sheet.autoSizeColumn(coluna);
                coluna++;
            }
        }
    }
}
