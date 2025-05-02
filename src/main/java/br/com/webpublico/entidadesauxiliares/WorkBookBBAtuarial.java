/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import org.apache.poi.hssf.usermodel.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
public class WorkBookBBAtuarial implements Serializable {

    private HSSFWorkbook workBook;
    private HSSFCellStyle estiloCalibri;
    private HSSFCellStyle estiloDate;
    private HSSFCellStyle estiloInteiro;
    private HSSFCellStyle estiloMonetario;

    public WorkBookBBAtuarial() {
        workBook = new HSSFWorkbook();
        criarEstilos();
    }

    public HSSFWorkbook getWorkBook() {
        return workBook;
    }

    public void setWorkBook(HSSFWorkbook workBook) {
        this.workBook = workBook;
    }

    public HSSFCellStyle getEstiloCalibri() {
        return estiloCalibri;
    }

    public void setEstiloCalibri(HSSFCellStyle estiloCalibri) {
        this.estiloCalibri = estiloCalibri;
    }

    public HSSFCellStyle getEstiloDate() {
        return estiloDate;
    }

    public void setEstiloDate(HSSFCellStyle estiloDate) {
        this.estiloDate = estiloDate;
    }

    public HSSFCellStyle getEstiloInteiro() {
        return estiloInteiro;
    }

    public void setEstiloInteiro(HSSFCellStyle estiloInteiro) {
        this.estiloInteiro = estiloInteiro;
    }

    public HSSFCellStyle getEstiloMonetario() {
        return estiloMonetario;
    }

    public void setEstiloMonetario(HSSFCellStyle estiloMonetario) {
        this.estiloMonetario = estiloMonetario;
    }

    public HSSFCell criarCell(HSSFRow row, Integer posicao, HSSFCellStyle estilo) {
        HSSFCell cell = criarCell(row, posicao);
        cell.setCellStyle(estilo);
        return cell;
    }

    public HSSFCell criarCell(HSSFRow row, Integer posicao) {
        HSSFCell cell = row.getCell(posicao) == null ? row.createCell(posicao) : row.getCell(posicao);
        cell.setCellStyle(estiloCalibri);
        return cell;
    }

    private HSSFCell criarCellDate(HSSFRow row, Integer posicao) {
        HSSFCell cell = criarCell(row, posicao);
        cell.setCellStyle(estiloDate);
        return cell;
    }

    private HSSFCell criarCellNumerico(HSSFRow row, Integer posicao) {
        HSSFCell cell = criarCell(row, posicao);
        cell.setCellStyle(estiloInteiro);
        return cell;
    }

    public HSSFCell criarCellMonetario(HSSFRow row, Integer posicao) {
        HSSFCell cell = criarCell(row, posicao);
        cell.setCellStyle(estiloMonetario);
        return cell;
    }

    public HSSFCellStyle criarFonteNegrito() {
        HSSFCellStyle style = workBook.createCellStyle();
        HSSFFont fonte = workBook.createFont();
        fonte.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        fonte.setFontName("Calibri");
        style.setFont(fonte);
        return style;
    }

    private void criarEstiloCalibri() {
        if (estiloCalibri != null) {
            return;
        }
        estiloCalibri = workBook.createCellStyle();
        HSSFFont fonte = workBook.createFont();
        fonte.setFontName("Calibri");
        estiloCalibri.setFont(fonte);
    }

    private void criarEstiloDate() {
        if (estiloDate != null) {
            return;
        }

        estiloDate = workBook.createCellStyle();
        HSSFCreationHelper createHelper = workBook.getCreationHelper();
        estiloDate.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        HSSFFont fonte = workBook.createFont();
        fonte.setFontName("Calibri");
        estiloDate.setFont(fonte);
    }

    private void criarEstiloInteiro() {
        if (estiloInteiro != null) {
            return;
        }

        estiloInteiro = workBook.createCellStyle();
        HSSFCreationHelper createHelper = workBook.getCreationHelper();
        estiloInteiro.setDataFormat(createHelper.createDataFormat().getFormat("0"));
        HSSFFont fonte = workBook.createFont();
        fonte.setFontName("Calibri");
        estiloInteiro.setFont(fonte);
    }

    private void criarEstiloMonetario() {
        if (estiloMonetario != null) {
            return;
        }

        estiloMonetario = workBook.createCellStyle();
        HSSFCreationHelper createHelper = workBook.getCreationHelper();
        estiloMonetario.setDataFormat(createHelper.createDataFormat().getFormat("_(R$* #,##0.00_);_(R$* (#,##0.00);_(R$* 0.00_);_(@_)"));
        HSSFFont fonte = workBook.createFont();
        fonte.setFontName("Calibri");
        estiloMonetario.setFont(fonte);
    }

    private void criarEstilos() {
        criarEstiloCalibri();
        criarEstiloDate();
        criarEstiloInteiro();
        criarEstiloMonetario();
    }

    public void escreverEm(HSSFRow row, Integer coluna, Object valor) {
        if (valor == null || valor.toString() == null || valor.toString().trim() == null) {
            criarCell(row, coluna).setCellValue("");
            return;
        }

        // Quando Date
        if (valor instanceof Date) {
            criarCellDate(row, coluna).setCellValue((Date) valor);
        }

        // Quando String
        if (valor instanceof String) {
            criarCell(row, coluna).setCellValue((String) valor);
        }

        // Quando Numérico
        if (valor instanceof Number) {
            String numero = valor+"";
            criarCell(row, coluna).setCellValue(Double.parseDouble(numero));
        }
    }

    public void escreverEmMonetario(HSSFRow row, Integer coluna, Object valor) {
        if (valor == null || valor.toString() == null || valor.toString().trim() == null) {
            criarCell(row, coluna).setCellValue("");
            return;
        }

        criarCellMonetario(row, coluna).setCellValue((Double) valor);
    }
}
