package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AssociacaoGrupoObjetoCompraGrupoMaterialFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Wellington Abdo on 30/01/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "importar", pattern = "/associacao-grupo-objeto-compra-grupo-material/importar/",
        viewId = "/faces/administrativo/materiais/associacaogrupoobjetocompragrupomaterial/importar.xhtml")

})
public class ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialControlador implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialControlador.class);
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade facade;
    private UploadedFile file;
    private List<ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO> associacoes;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void handleFileUpload(FileUploadEvent event) {
        associacoes = Lists.newArrayList();
        file = event.getFile();
        preencherAssociacoesViaFile(file);
    }

    public List<ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO> getAssociacoes() {
        return associacoes;
    }

    public void setAssociacoes(List<ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO> associacoes) {
        this.associacoes = associacoes;
    }

    private void preencherAssociacoesViaFile(UploadedFile file) {
        try {
            HashSet<ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO> associacoesNoArquivo = new HashSet();
            XSSFWorkbook myWorkBook = new XSSFWorkbook(file.getInputstream());
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);
            Iterator<Row> rowIterator = mySheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO dto = new ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO();
                dto.setGrupoObjetoCompra(row.getCell(0).getStringCellValue());
                dto.setGrupoMaterial(row.getCell(1).getStringCellValue());
                dto.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
                associacoesNoArquivo.add(dto);
            }
            associacoes.addAll(associacoesNoArquivo);
            FacesUtil.addOperacaoRealizada("A leitura do arquivo foi realizada com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao importar associação de GOC com GM {}", e);
            FacesUtil.addOperacaoNaoRealizada("Formato da planilha para importação invalido.");
        }
    }

    public void registrarAssociacoes() {
        try {
            validarAssociacoesSelecionada();
            facade.inserirAssociacaoViaImportacao(associacoes);
            FacesUtil.addOperacaoRealizada("As associações foram registradas com sucesso!");
            FacesUtil.redirecionamentoInterno("/associacao-grupo-objeto-compra-grupo-material/importar/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarAssociacoesSelecionada() {
        ValidacaoException ve = new ValidacaoException();
        if (associacoes == null || associacoes.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhuma associação selecionada para registro.");
        }
        ve.lancarException();
    }
}
