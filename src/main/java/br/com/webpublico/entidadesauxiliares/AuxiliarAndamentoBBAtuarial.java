/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.BBAtuarial;
import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Felipe Marinzeck
 */
public class AuxiliarAndamentoBBAtuarial implements Serializable {

    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    private Integer calculados, total;
    private List<String> log;
    private Long decorrido, tempo;
    private Boolean parado;
    private Double qntoFalta;

    private BBAtuarial bbAtuarial;

    // Servidores Ativos
    private File fileServidoresAtivos;
    private FileOutputStream outputStreamServidoresAtivos;
    private WorkBookBBAtuarial workBookServidoresAtivos;
    private HSSFSheet sheetServidoresAtivos;
    private Integer linhaAtualServidoresAtivos;

    // Aposentados
    private File fileAposentados;
    private FileOutputStream outputStreamAposentados;
    private WorkBookBBAtuarial workBookAposentados;
    private HSSFSheet sheetAposentados;
    private Integer linhaAtualAposentados;

    // Dependentes
    private File fileDependentes;
    private FileOutputStream outputStreamDependentes;
    private WorkBookBBAtuarial workBookDependentes;
    private HSSFSheet sheetDependentes;
    private Integer linhaAtualDependentes;

    // Pensionistas
    private File filePensionistas;
    private FileOutputStream outputStreamPensionistas;
    private WorkBookBBAtuarial workBookPensionistas;
    private HSSFSheet sheetPensionistas;
    private Integer linhaAtualPensionistas;

    public AuxiliarAndamentoBBAtuarial() {
        iniciarProcesso();
    }

    public void iniciarProcesso() {
        this.log = Lists.newArrayList();
        this.total = 0;
        calculados = 0;
        tempo = System.currentTimeMillis();
        this.parado = Boolean.FALSE;
        workBookServidoresAtivos = null;
        workBookAposentados = null;
        workBookDependentes = null;
        workBookPensionistas = null;
    }

    public Integer getLinhaAtualServidoresAtivos() {
        return linhaAtualServidoresAtivos;
    }

    public void setLinhaAtualServidoresAtivos(Integer linhaAtualServidoresAtivos) {
        this.linhaAtualServidoresAtivos = linhaAtualServidoresAtivos;
    }

    public WorkBookBBAtuarial getWorkBookPensionistas() {
        return workBookPensionistas;
    }

    public synchronized Integer getCalculados() {
        return calculados;
    }

    public void setCalculados(Integer calculados) {
        this.calculados = calculados;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getDecorrido() {
        return decorrido;
    }

    public void setDecorrido(Long decorrido) {
        this.decorrido = decorrido;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public List<String> getLog() {
        return log;
    }

    public void setLog(List<String> log) {
        this.log = log;
    }

    public Double getPorcentagemDoCalculo() {
        if (calculados == null || total == null) {
            return 0d;
        }
        return (calculados.doubleValue() / total.doubleValue()) * 100;
    }

    public synchronized void contar() {
        calculados++;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);

        decorrido = (System.currentTimeMillis() - tempo);

        return String.format(formatoDataHora, decorrido / HOUR, decorrido % HOUR);
    }

    public String getTempoEstimado() {
        long HOUR = TimeUnit.HOURS.toMillis(1);
        long unitario = (System.currentTimeMillis() - tempo) / (calculados + 1);
        qntoFalta = (unitario * (total - calculados.doubleValue()));

        return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
    }

    public void pararProcessamento() {
        this.parado = Boolean.TRUE;
    }

    public Boolean getParado() {
        return parado;
    }

    public void setParado(Boolean parado) {
        this.parado = parado;
    }

    public String getSomenteStringDoLog() {
        try {
            return this.log.toString().replace("[", "").replace("]", "").replace(",", "");
        } catch (ConcurrentModificationException cme) {
            corrigeLog();
            return "";
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public void corrigeLog() {
        List<String> copia = new ArrayList<String>();
        copia.addAll(log);
        log = new ArrayList<String>();
        log.addAll(copia);
    }

    public BBAtuarial getBbAtuarial() {
        return bbAtuarial;
    }

    public void setBbAtuarial(BBAtuarial bbAtuarial) {
        this.bbAtuarial = bbAtuarial;
    }

    public WorkBookBBAtuarial getWorkBookServidoresAtivos() {
        return workBookServidoresAtivos;
    }

    public void setWorkBookServidoresAtivos(WorkBookBBAtuarial workBookServidoresAtivos) {
        this.workBookServidoresAtivos = workBookServidoresAtivos;
    }

    public HSSFSheet getSheetServidoresAtivos() {
        return sheetServidoresAtivos;
    }

    public void setSheetServidoresAtivos(HSSFSheet sheetServidoresAtivos) {
        this.sheetServidoresAtivos = sheetServidoresAtivos;
    }

    public Integer getLinhaAtualAposentados() {
        return linhaAtualAposentados;
    }

    public void setLinhaAtualAposentados(Integer linhaAtualAposentados) {
        this.linhaAtualAposentados = linhaAtualAposentados;
    }

    public HSSFSheet getSheetAposentados() {
        return sheetAposentados;
    }

    public void setSheetAposentados(HSSFSheet sheetAposentados) {
        this.sheetAposentados = sheetAposentados;
    }

    public WorkBookBBAtuarial getWorkBookAposentados() {
        return workBookAposentados;
    }

    public void setWorkBookAposentados(WorkBookBBAtuarial workBookAposentados) {
        this.workBookAposentados = workBookAposentados;
    }

    public FileOutputStream getOutputStreamAposentados() {
        return outputStreamAposentados;
    }

    public void setOutputStreamAposentados(FileOutputStream outputStreamAposentados) {
        this.outputStreamAposentados = outputStreamAposentados;
    }

    public File getFileAposentados() {
        return fileAposentados;
    }

    public void setFileAposentados(File fileAposentados) {
        this.fileAposentados = fileAposentados;
    }

    public Integer getLinhaAtualDependentes() {
        return linhaAtualDependentes;
    }

    public void setLinhaAtualDependentes(Integer linhaAtualDependentes) {
        this.linhaAtualDependentes = linhaAtualDependentes;
    }

    public HSSFSheet getSheetDependentes() {
        return sheetDependentes;
    }

    public void setSheetDependentes(HSSFSheet sheetDependentes) {
        this.sheetDependentes = sheetDependentes;
    }

    public void setWorkBookPensionistas(WorkBookBBAtuarial workBookPensionistas) {
        this.workBookPensionistas = workBookPensionistas;
    }

    public WorkBookBBAtuarial getWorkBookDependentes() {
        return workBookDependentes;
    }

    public void setWorkBookDependentes(WorkBookBBAtuarial workBookDependentes) {
        this.workBookDependentes = workBookDependentes;
    }

    public FileOutputStream getOutputStreamDependentes() {
        return outputStreamDependentes;
    }

    public void setOutputStreamDependentes(FileOutputStream outputStreamDependentes) {
        this.outputStreamDependentes = outputStreamDependentes;
    }

    public File getFileDependentes() {
        return fileDependentes;
    }

    public void setFileDependentes(File fileDependentes) {
        this.fileDependentes = fileDependentes;
    }

    public Integer getLinhaAtualPensionistas() {
        return linhaAtualPensionistas;
    }

    public void setLinhaAtualPensionistas(Integer linhaAtualPensionistas) {
        this.linhaAtualPensionistas = linhaAtualPensionistas;
    }

    public HSSFSheet getSheetPensionistas() {
        return sheetPensionistas;
    }

    public void setSheetPensionistas(HSSFSheet sheetPensionistas) {
        this.sheetPensionistas = sheetPensionistas;
    }

    public FileOutputStream getOutputStreamPensionistas() {
        return outputStreamPensionistas;
    }

    public void setOutputStreamPensionistas(FileOutputStream outputStreamPensionistas) {
        this.outputStreamPensionistas = outputStreamPensionistas;
    }

    public File getFilePensionistas() {
        return filePensionistas;
    }

    public void setFilePensionistas(File filePensionistas) {
        this.filePensionistas = filePensionistas;
    }

    public File getFileServidoresAtivos() {
        return fileServidoresAtivos;
    }

    public void setFileServidoresAtivos(File fileServidoresAtivos) {
        this.fileServidoresAtivos = fileServidoresAtivos;
    }

    public FileOutputStream getOutputStreamServidoresAtivos() {
        return outputStreamServidoresAtivos;
    }

    public void setOutputStreamServidoresAtivos(FileOutputStream outputStreamServidoresAtivos) {
        this.outputStreamServidoresAtivos = outputStreamServidoresAtivos;
    }

    /**
     * ***************************************
     * MÉTODOS
     * ****************************************
     */

    private HSSFSheet criarSheet(HSSFWorkbook pasta, String nomeSheet) {
        return pasta.createSheet(nomeSheet);
    }


    /**
     * ***************************************
     * SERVIDORES ATIVOS
     * ****************************************
     */
    public synchronized HSSFRow criarRowServidoresAtivos() {
        linhaAtualServidoresAtivos++;
        HSSFRow row = sheetServidoresAtivos.getRow(linhaAtualServidoresAtivos) == null ? sheetServidoresAtivos.createRow(linhaAtualServidoresAtivos) : sheetServidoresAtivos.getRow(linhaAtualServidoresAtivos);
        return row;
    }

    private void criarCabecalhoServidoresAtivos() {
        HSSFRow cabecalho = sheetServidoresAtivos.createRow(getLinhaAtualServidoresAtivos());
        HSSFCellStyle estiloNegrito = workBookServidoresAtivos.criarFonteNegrito();
        int coluna = 0;
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("MATRÍCULA");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("CPF");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA NASCIMENTO");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("SEXO");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("ESTADO CIVIL");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DT. NASCIMENTO CÔNJUGE");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("NÚMERO DE DEPENDENTES");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DT. NASCIMENTO DEP. MAIS JOVEM");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("TEMPO CONTRIB. RGPS");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("TEMPO CONTRIB. RPPS");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA INGRESSO NO ENTE");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA DO CARGO");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("TIPO DE VÍNCULO");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("LOTAÇÃO - ÓRGÃO/ENTIDADE");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("CARREIRA");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("CARGO ATUAL");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("CRITÉRIO DIF. APOSENTADORIA ESPECIAL");
        workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("PREVIDÊNCIA COMPLEMENTAR");

        for (int i = DataUtil.getAno(bbAtuarial.getDataReferencia()) - 4; i <= DataUtil.getAno(bbAtuarial.getDataReferencia()) - 1; i++) {
            workBookServidoresAtivos.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("REMUNERAÇÃO DE CONTRIBUIÇÃO " + i);
        }
    }

    public void criarArquivoServidoresAtivos() {
        try {
            fileServidoresAtivos = File.createTempFile("SERVIDORES_ATIVOS", "xls");
            outputStreamServidoresAtivos = new FileOutputStream(fileServidoresAtivos);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void criarWorkBookServidoresAtivos() {
        workBookServidoresAtivos = new WorkBookBBAtuarial();
        sheetServidoresAtivos = criarSheet(workBookServidoresAtivos.getWorkBook(), "SERVIDORES ATIVOS");
        linhaAtualServidoresAtivos = 0;
        criarCabecalhoServidoresAtivos();
    }

    /**
     * ***************************************
     * APOSENTADOS
     * ****************************************
     */
    public synchronized HSSFRow criarRowAposentados() {
        linhaAtualAposentados++;
        HSSFRow row = sheetAposentados.getRow(linhaAtualAposentados) == null ? sheetAposentados.createRow(linhaAtualAposentados) : sheetAposentados.getRow(linhaAtualAposentados);
        return row;
    }

    private void criarCabecalhoAposentados() {
        HSSFRow cabecalho = sheetAposentados.createRow(getLinhaAtualAposentados());
        HSSFCellStyle estiloNegrito = workBookAposentados.criarFonteNegrito();
        int coluna = 0;
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("MATRÍCULA");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA NASCIMENTO");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("SEXO");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("ESTADO CIVIL");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA NASCIMENTO CONJUGE");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("QUANTIDADE DEPENDENTES");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DT. NASCIMENTO DEP. MAIS JOVEM");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA DE INGRESSO NO ENTE");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("TEMPO CONTRIBUIÇÃO RPPS");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("TEMPO CONTRIBUIÇÃO OUTROS REGIMES");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("CARREIRA");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("CARGO");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("TIPO APOSENTADORIA");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA CONCESSÃO");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("PREVIDÊNCIA COMPLEMENTAR");
        workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("COMPENSAÇÃO PREVIDENCIÁRIA");
        for (int i = DataUtil.getAno(bbAtuarial.getDataReferencia()) - 4; i <= DataUtil.getAno(bbAtuarial.getDataReferencia()) - 1; i++) {
            workBookAposentados.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("VALOR DO BENEFÍCIO " + i);
        }
    }

    public void criarArquivoAposentados() {
        try {
            fileAposentados = File.createTempFile("APOSENTADOS", "xls");
            outputStreamAposentados = new FileOutputStream(fileAposentados);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    public void criarWorkBookAposentados() {
        workBookAposentados = new WorkBookBBAtuarial();
        sheetAposentados = criarSheet(workBookAposentados.getWorkBook(), "APOSENTADOS");
        linhaAtualAposentados = 0;
        criarCabecalhoAposentados();
    }

    /**
     * ***************************************
     * DEPENDENTES
     * ****************************************
     */
    public synchronized HSSFRow criarRowDependentes() {
        linhaAtualDependentes++;
        HSSFRow row = sheetDependentes.getRow(linhaAtualDependentes) == null ? sheetDependentes.createRow(linhaAtualDependentes) : sheetDependentes.getRow(linhaAtualDependentes);
        return row;
    }

    private void criarCabecalhoDependentes() {
        HSSFRow cabecalho = sheetDependentes.createRow(getLinhaAtualDependentes());
        HSSFCellStyle estiloNegrito = workBookDependentes.criarFonteNegrito();
        int coluna = 0;
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("MATRÍCULA");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("STATUS");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("NOME");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("CPF");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("SEXO");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("SITUAÇÃO");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("PARENTESCO");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA DE NASCIMENTO");
        workBookDependentes.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("ESCOLARIDADE");
    }

    public void criarArquivoDependentes() {
        try {
            fileDependentes = File.createTempFile("DEPENDENTES", "xls");
            outputStreamDependentes = new FileOutputStream(fileDependentes);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void criarWorkBookDependentes() {
        workBookDependentes = new WorkBookBBAtuarial();
        sheetDependentes = criarSheet(workBookDependentes.getWorkBook(), "DEPENDENTES");
        linhaAtualDependentes = 0;
        criarCabecalhoDependentes();
    }

    /**
     * ***************************************
     * PENSIONISTAS
     * ****************************************
     */
    public synchronized HSSFRow criarRowPensionistas() {
        linhaAtualPensionistas++;
        HSSFRow row = sheetPensionistas.getRow(linhaAtualPensionistas) == null ? sheetPensionistas.createRow(linhaAtualPensionistas) : sheetPensionistas.getRow(linhaAtualPensionistas);
        return row;
    }

    private void criarCabecalhoPensionistas() {
        HSSFRow cabecalho = sheetPensionistas.createRow(getLinhaAtualPensionistas());
        HSSFCellStyle estiloNegrito = workBookPensionistas.criarFonteNegrito();
        int coluna = 0;
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("MATRÍCULA PENSIONISTA");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("MATRÍCULA INSTITUIDOR");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA NASCIMENTO PENSIONISTA");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("SEXO PENSIONISTA");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("TIPO PARENTESCO");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("INVALIDEZ");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DATA CONCESSÃO");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("DURAÇÃO DO BENEFÍCIO");
        workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("PREVIDÊNCIA COMPLEMENTAR");
        for (int i = DataUtil.getAno(bbAtuarial.getDataReferencia()) - 4; i <= DataUtil.getAno(bbAtuarial.getDataReferencia()) - 1; i++) {
            workBookPensionistas.criarCell(cabecalho, coluna++, estiloNegrito).setCellValue("VALOR DO BENEFÍCIO " + i);
        }
    }

    public void criarArquivoPensionistas() {
        try {
            filePensionistas = File.createTempFile("PENSIONISTAS", "xls");
            outputStreamPensionistas = new FileOutputStream(filePensionistas);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void criarWorkBookPensionistas() {
        workBookPensionistas = new WorkBookBBAtuarial();
        sheetPensionistas = criarSheet(workBookPensionistas.getWorkBook(), "PENSIONISTAS");
        linhaAtualPensionistas = 0;
        criarCabecalhoPensionistas();
    }

    public void criarSheetCargosServidoresAtivos(List<Cargo> conteudoCargoServidoresAtivos) {
        HSSFSheet sheet = workBookServidoresAtivos.getWorkBook().createSheet("CARGO-CARREIRA");

        HSSFCellStyle estiloNegrito = workBookServidoresAtivos.criarFonteNegrito();
        HSSFRow rowCabecalho = sheet.createRow(0);
        workBookServidoresAtivos.criarCell(rowCabecalho, 0, estiloNegrito).setCellValue("CÓDIGO CARREIRA");
        workBookServidoresAtivos.criarCell(rowCabecalho, 1, estiloNegrito).setCellValue("CÓDIGO DO CARGO");
        workBookServidoresAtivos.criarCell(rowCabecalho, 2, estiloNegrito).setCellValue("DESCRIÇÃO");

        Integer linha = 1;
        for (Cargo cargo : conteudoCargoServidoresAtivos) {
            HSSFRow row = sheet.createRow(linha);
            // Código carreira
            try {
                workBookServidoresAtivos.criarCell(row, 0).setCellValue(Double.parseDouble(cargo.getCodigoCarreira()));
            } catch (Exception e) {
                workBookServidoresAtivos.criarCell(row, 0).setCellValue(cargo.getCodigoCarreira());
            }

            // Código cargo
            try {
                workBookServidoresAtivos.criarCell(row, 1).setCellValue(Double.parseDouble(cargo.getCodigoDoCargo()));
            } catch (Exception e) {
                workBookServidoresAtivos.criarCell(row, 1).setCellValue(cargo.getCodigoDoCargo());
            }

            workBookServidoresAtivos.criarCell(row, 2).setCellValue(cargo.getDescricao());
            linha++;
        }
    }
}
