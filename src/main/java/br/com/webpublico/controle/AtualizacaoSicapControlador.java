package br.com.webpublico.controle;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.Iterator;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-atualizacao-classificacao-cargoVerba", pattern = "/atualizacao-classificacao-cargoVerba/novo/", viewId = "/faces/rh/administracaodepagamento/atualizacaoclassificacaocargoverba/edita.xhtml"),
})
public class AtualizacaoSicapControlador implements Serializable {
    private Logger logger = LoggerFactory.getLogger(AtualizacaoSicapControlador.class);
    private CargoVerbaSicap cargoVerbaSicap;
    private List<String> insertsOrUpdates;
    private List<UploadedFile> file;

    @URLAction(mappingId = "novo-atualizacao-classificacao-cargoVerba", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        insertsOrUpdates = Lists.newArrayList();
        file = Lists.newArrayList();
    }

    public List<String> getInsertsOrUpdates() {
        return insertsOrUpdates;
    }

    public void setInsertsOrUpdates(List<String> insertsOrUpdates) {
        this.insertsOrUpdates = insertsOrUpdates;
    }

    public CargoVerbaSicap getCargoVerbaSicap() {
        return cargoVerbaSicap;
    }

    public void setCargoVerbaSicap(CargoVerbaSicap cargoVerbaSicap) {
        this.cargoVerbaSicap = cargoVerbaSicap;
    }

    public List<UploadedFile> getFile() {
        return file;
    }

    public void setFile(List<UploadedFile> file) {
        this.file = file;
    }

    public List<SelectItem> montarTiposClassificacao() {
        return Util.getListSelectItem(CargoVerbaSicap.values());
    }

    private void validarLeituraArquivo() {
        ValidacaoException ve = new ValidacaoException();
        if (cargoVerbaSicap == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Classificação deve ser informado.");
        }
        ve.lancarException();
    }

    private boolean isXlsx() {
        String inputFilename = file.get(0).getFileName();
        return inputFilename.substring(inputFilename.lastIndexOf(".") + 1).equalsIgnoreCase("xlsx");
    }

    public void lerArquivoSicap(FileUploadEvent event) {
        try {
            validarLeituraArquivo();
            limparListas();

            file.add(event.getFile());

            if (!file.isEmpty()) {
                Iterator linhas;
                if (isXlsx()) {
                    XSSFWorkbook wb = new XSSFWorkbook(file.get(0).getInputstream());
                    XSSFSheet sheet = wb.getSheetAt(0);
                    linhas = sheet.rowIterator();
                } else {
                    HSSFWorkbook wb = new HSSFWorkbook(file.get(0).getInputstream());
                    HSSFSheet sheet = wb.getSheetAt(0);
                    linhas = sheet.rowIterator();
                }
                if (!linhas.hasNext()) {
                    throw new ValidacaoException("O arquivo está vazio.");
                }
                preencherLista(linhas);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao ler xlsx, ", e);
            FacesUtil.addOperacaoNaoRealizada("Erro ao ler arquivo. Detalhes: " + e.getMessage());
        } finally {
            cargoVerbaSicap = null;
        }
    }

    private void preencherLista(Iterator linhas) {
        while (linhas.hasNext()) {
            Iterator celulas;
            if (isXlsx()) {
                XSSFRow linha = (XSSFRow) linhas.next();
                celulas = linha.cellIterator();
            } else {
                HSSFRow linha = (HSSFRow) linhas.next();
                celulas = linha.cellIterator();
            }

            String codigo = "";
            String descricao = "";

            while (celulas.hasNext()) {
                Integer index;

                if (isXlsx()) {
                    XSSFCell celula = (XSSFCell) celulas.next();
                    index = celula.getColumnIndex();

                    if (index.equals(0)) {
                        codigo = (int) celula.getNumericCellValue() + "";
                    }
                    if (index.equals(1)) {
                        descricao = celula.getStringCellValue();
                    }
                } else {
                    HSSFCell celula = (HSSFCell) celulas.next();
                    index = celula.getColumnIndex();

                    if (index.equals(0)) {
                        codigo = (int) celula.getNumericCellValue() + "";
                    }
                    if (index.equals(1)) {
                        descricao = celula.getStringCellValue();
                    }
                }
            }

            if (!Strings.isNullOrEmpty(codigo) || !Strings.isNullOrEmpty(descricao)) {
                insertsOrUpdates.add(gerarInsertOrUpdate(codigo, descricao, cargoVerbaSicap.getTabela()));
            }
        }
    }

    private String gerarInsertOrUpdate(String codigo, String descricao, String tabela) {
        return " merge into " + tabela + " using dual on (id = (select id from " + tabela + " where codigo = '" + codigo + "' )) " +
            " when matched then update set codigo = '" + codigo + "', descricao = '" + descricao + "' " +
            " when not matched then insert (id, codigo, descricao) values (hibernate_sequence.nextval, '" + codigo + "', '" + descricao + "'); ";
    }

    private void validarTxt() {
        ValidacaoException ve = new ValidacaoException();
        if (insertsOrUpdates != null && insertsOrUpdates.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O arquivo está nulo ou vazio.");
        }
        ve.lancarException();
    }

    public void removeArquivo() {
        limparListas();
    }

    private void limparListas() {
        insertsOrUpdates.clear();
        file.clear();
    }

    public DefaultStreamedContent gerarTxt() {
        try {
            validarTxt();
            if (insertsOrUpdates != null && !insertsOrUpdates.isEmpty()) {
                try {
                    StringBuilder conteudo = new StringBuilder();

                    for (String insertsOrUpdate : insertsOrUpdates) {
                        conteudo.append(insertsOrUpdate).append("</br>");
                    }

                    String nomeArquivo = "AtualizacaoSicap" + (cargoVerbaSicap != null ? cargoVerbaSicap.getDescricao() : "") + ".txt";
                    File arquivo = new File(nomeArquivo);
                    FileOutputStream fos = new FileOutputStream(arquivo);
                    fos.write(conteudo.toString().replace("</br>", System.getProperty("line.separator"))
                        .replace("<b>", " ")
                        .replace("</b>", " ")
                        .replace("<font color='red'>", " ")
                        .replace("<font color='blue'>", " ")
                        .replace("</font>", " ").getBytes());
                    InputStream stream = new FileInputStream(arquivo);
                    return new DefaultStreamedContent(stream, "text/plain", nomeArquivo);
                } catch (ValidacaoException ve) {
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                } catch (Exception e) {
                    FacesUtil.addErroAoGerarRelatorio(e.getMessage());
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
        return null;
    }

    public enum CargoVerbaSicap implements EnumComDescricao {
        CARGO("Cargo", "classificacaocargo"),
        VERBA("Verba", "classificacaoverba");

        String descricao;
        String tabela;

        CargoVerbaSicap(String descricao, String tabela) {
            this.descricao = descricao;
            this.tabela = tabela;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        public String getTabela() {
            return tabela;
        }
    }
}
