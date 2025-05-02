package br.com.webpublico.controle;

import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.PessoaInfo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.*;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 23/03/2017.
 */
@ManagedBean(name = "pesquisaVinculoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "pesquisa-vinculo", pattern = "/pesquisa-vinculo/", viewId = "/faces/rh/pesquisa-vinculo/edita.xhtml")
})
public class PesquisaVinculoControlador {

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<PessoaInfo> pessoas;
    private Boolean somenteVinculosVigentes;

    @URLAction(mappingId = "pesquisa-vinculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        pessoas = Lists.newArrayList();
        somenteVinculosVigentes = Boolean.FALSE;
    }

    public void importar(FileUploadEvent event) {
        pessoas = Lists.newArrayList();
        try {
            UploadedFile file = event.getFile();
            Workbook workbook = WorkbookFactory.create(file.getInputstream());
            Sheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break;
                }
                percorrerCelulas(row);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao importar os vinculos: " + ex.getMessage());
        }
    }

    private void percorrerCelulas(Row row) {
        List<CampoImportacao> campos = Lists.newArrayList(CampoImportacao.values());
        for (CampoImportacao campoImportacao : CampoImportacao.values()) {
            Cell cell = row.getCell(campos.indexOf(campoImportacao));
            String valorCelula = getValorCell(cell);
            if (CampoImportacao.CPF.equals(campoImportacao)) {
                Long idPessoaFisica = pessoaFisicaFacade.buscarIdDePessoaPorCpf(valorCelula);
                if (idPessoaFisica != null) {
                    PessoaFisica pf = pessoaFisicaFacade.recuperar(idPessoaFisica);
                    PessoaInfo pessoaInfo = new PessoaInfo();
                    pessoaInfo.setPessoa(pf);
                    pessoaInfo.setVinculos(vinculoFPFacade.listaTodosVinculosPorPessoa(pf));
                    pessoas.add(pessoaInfo);
                }
            }
        }
    }

    public StreamedContent exportar() {
        try {
            List<String> titulos = new ArrayList<>();
            titulos.add("CPF");
            titulos.add("NOME");
            titulos.add("INÍCIO VINCULO");
            titulos.add("FIM VINCULO");
            List<Object[]> objetos = new ArrayList<>();
            for (PessoaInfo pessoaInfo : pessoas) {
                if (somenteVinculosVigentes) {
                    pessoaInfo.setVinculos(vinculoFPFacade.buscarVinculosVigentesPorPessoa(pessoaInfo.getPessoa(), sistemaFacade.getDataOperacao()));
                }
                for (VinculoFP vinculoFP : pessoaInfo.getVinculos()) {
                    Object[] obj = new Object[12];
                    obj[0] = pessoaInfo.getPessoa().getCpf_Cnpj();
                    obj[1] = pessoaInfo.getPessoa().getNome();
                    obj[2] = DataUtil.getDataFormatada(vinculoFP.getInicioVigencia());
                    if (vinculoFP.getFinalVigencia() != null) {
                        obj[3] = DataUtil.getDataFormatada(vinculoFP.getFinalVigencia());
                    }
                    objetos.add(obj);
                }
            }
            ExcelUtil excel = new ExcelUtil();
            excel.gerarExcel("Pessoas com Vínculo", "pessoas-vinculo", titulos, objetos, null);
            return excel.fileDownload();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a exportação do arquivo: " + ex.getMessage());
        }
        return null;
    }

    private String getValorCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
            return cell.getStringCellValue().length() < 11 ? Util.zerosAEsquerda(cell.getStringCellValue(), 11) : cell.getStringCellValue();
        } else if (Cell.CELL_TYPE_STRING == cell.getCellType()) {
            return cell.getStringCellValue();
        }
        return "";
    }

    public Boolean getSomenteVinculosVigentes() {
        return somenteVinculosVigentes;
    }

    public void setSomenteVinculosVigentes(Boolean somenteVinculosVigentes) {
        this.somenteVinculosVigentes = somenteVinculosVigentes;
    }

    public List<CampoImportacao> getCamposImportacao() {
        return Arrays.asList(CampoImportacao.values());
    }

    public List<PessoaInfo> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaInfo> pessoas) {
        this.pessoas = pessoas;
    }

    public enum CampoImportacao {
        NOME("Nome"),
        CPF("CPF");

        private String descricao;

        CampoImportacao(String descricao) {
            this.descricao = descricao;
        }

        CampoImportacao() {
        }

        public String getDescricao() {
            return descricao;
        }
    }

}
