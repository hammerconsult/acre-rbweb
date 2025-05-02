package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.entidades.GrupoObjetoCompraGrupoBem;
import br.com.webpublico.negocios.GrupoBemFacade;
import br.com.webpublico.negocios.GrupoObjetoCompraFacade;
import br.com.webpublico.negocios.GrupoObjetoCompraGrupoBemFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.ISuperLista;
import br.com.webpublico.util.SuperLista;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 25/02/15
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMapping(id = "importacao-grupo-objeto-compra-grupo-patrimonial", pattern = "/importacao-grupo-objeto-compra-grupo-patrimonial/", viewId = "/faces/administrativo/patrimonio/importacao/edita.xhtml")
public class ImportacaoObjetoDeCompraGrupoPatrimonialControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoObjetoDeCompraGrupoPatrimonialControlador.class);
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    private ISuperLista<GrupoObjetoCompraGrupoBem> grupos;
    private List<GrupoObjetoCompraGrupoBem> gruposParaSalva;

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @URLAction(mappingId = "importacao-grupo-objeto-compra-grupo-patrimonial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        SistemaControlador sistemaControlador = getSistemaControlador();
        gruposParaSalva = new ArrayList<>();
        grupos = new SuperLista<GrupoObjetoCompraGrupoBem>(gruposParaSalva, GrupoObjetoCompraGrupoBem.class);

    }

    public void handleFilesUploads(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();

            org.apache.poi.ss.usermodel.Workbook workbook = WorkbookFactory.create(file.getInputstream());

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);

            int rowsCount = sheet.getLastRowNum();

            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break;
                }
                int colCounts = row.getLastCellNum();

                String valorGrupoObjetoCompra = null;
                String valorGrupoPatrimonio = null;

                for (int j = 0; j < colCounts; j++) {
                    Cell cell = row.getCell(j);

                    if (j == 0) {
                        valorGrupoObjetoCompra = getValorCell(cell);
                    }

                    if (j == 1) {
                        valorGrupoPatrimonio = getValorCell(cell);
                    }

                }

                if (valorGrupoObjetoCompra != null && valorGrupoPatrimonio != null) {

                    criaAdicionaGrupoObjetoCompraGrupoBem(valorGrupoObjetoCompra, valorGrupoPatrimonio);

                } else {
                    valorGrupoObjetoCompra = null;
                    valorGrupoPatrimonio = null;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addOperacaoNaoPermitida("Erro ao importar arquivo. " + e.getMessage());
        }
    }

    public void salvar() {
        List<GrupoObjetoCompraGrupoBem> paraRemover = new ArrayList<>();
        for (GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem : gruposParaSalva) {
            if (grupoObjetoCompraGrupoBemFacade.jaExisteEstaAssociacao(grupoObjetoCompraGrupoBem)) {
                FacesUtil.addOperacaoNaoPermitida("A associação entre o grupo de bem " + grupoObjetoCompraGrupoBem.getGrupoBem() + " e o grupo de objeto de compra " + grupoObjetoCompraGrupoBem.getGrupoObjetoCompra() + " já existe.");
                paraRemover.add(grupoObjetoCompraGrupoBem);
            }
        }
        for (GrupoObjetoCompraGrupoBem grupoObjetoCompraGrupoBem : paraRemover) {
            gruposParaSalva.remove(grupoObjetoCompraGrupoBem);
        }
        grupoObjetoCompraGrupoBemFacade.salvar(gruposParaSalva);
        FacesUtil.addOperacaoRealizada("Forão associadas " + gruposParaSalva.size() + ".");
        novo();
    }

    private void criaAdicionaGrupoObjetoCompraGrupoBem(String valorGrupoObjetoCompra, String valorGrupoPatrimonio) {
        GrupoObjetoCompra grupoObjetoCompra = getGrupoObjetoCompra(valorGrupoObjetoCompra);
        GrupoBem grupoBem = getGrupoPatrimonial(valorGrupoPatrimonio);
        if (grupoObjetoCompra != null && grupoBem != null) {
            GrupoObjetoCompraGrupoBem e = new GrupoObjetoCompraGrupoBem(getSistemaControlador().getDataOperacao(), grupoBem, grupoObjetoCompra);
            grupos.add(e);
            grupos.adicionar(e);
        } else {
            valorGrupoObjetoCompra = null;
            valorGrupoPatrimonio = null;
        }
    }

    private GrupoBem getGrupoPatrimonial(String valorGrupoPatrimonio) {
        if (valorGrupoPatrimonio.trim().isEmpty()) {
            return null;
        }
        List<GrupoBem> grupoBems = grupoBemFacade.listaFiltrando(valorGrupoPatrimonio.trim(), "codigo");
        if (grupoBems == null || grupoBems.isEmpty()) {
            return null;
        }
        return grupoBems.get(0);
    }

    private GrupoObjetoCompra getGrupoObjetoCompra(String valorGrupoObjetoCompra) {
        if (valorGrupoObjetoCompra.trim().isEmpty()) {
            return null;
        }
        String[] split = valorGrupoObjetoCompra.split("-");
        if (split.length == 0) {
            return null;
        }
        String codigo = split[0];
        List<GrupoObjetoCompra> codigo1 = grupoObjetoCompraFacade.listaFiltrando(codigo.trim(), "codigo");

        if (codigo1 == null || codigo1.isEmpty()) {
            return null;
        }
        return codigo1.get(0);
    }

    private String getValorCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            Double valor = cell.getNumericCellValue();
            return valor.intValue() + "";
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

    public ISuperLista<GrupoObjetoCompraGrupoBem> getGrupos() {
        return grupos;
    }

    public void setGrupos(ISuperLista<GrupoObjetoCompraGrupoBem> grupos) {
        this.grupos = grupos;
    }

    public List<GrupoObjetoCompraGrupoBem> getGruposParaSalva() {
        return gruposParaSalva;
    }

    public void setGruposParaSalva(List<GrupoObjetoCompraGrupoBem> gruposParaSalva) {
        this.gruposParaSalva = gruposParaSalva;
    }
}
