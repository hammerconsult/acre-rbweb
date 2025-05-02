package br.com.webpublico.negocios.rh.rotinasanuaismensais;

import br.com.webpublico.controle.rh.rotinasanuaisemensais.ImportacaoContratoFPLoteControlador;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidadesauxiliares.rh.ContratoFPLote;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author peixe on 31/03/2017  14:15.
 */
@Stateless
public class ImportacaoContratoFPLoteFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ImportacaoContratoFPLoteFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;


    public void importarPessoas(List<ContratoFPLote> pessoasImportadas, InputStream inputstream) {
        try {
            Workbook workbook = WorkbookFactory.create(inputstream);
            Sheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    break;
                }
                percorrerCelulas(row, pessoasImportadas);
            }
        } catch (Exception e) {
            logger.error("Erro", e);
        }
    }

    private void percorrerCelulas(Row row, List<ContratoFPLote> pessoasImportadas) {
        ContratoFPLote item = new ContratoFPLote();
        for (ImportacaoContratoFPLoteControlador.CampoImportacao campoImportacao : ImportacaoContratoFPLoteControlador.CampoImportacao.values()) {
            Cell cell = row.getCell(campoImportacao.ordinal());
            String valorCelula = getValorCell(cell);
            switch (campoImportacao) {
                case NUMERO:
                    try {
                        BigDecimal numero = new BigDecimal(valorCelula);
                        item.setNumero(numero.intValue());
                    } catch (NumberFormatException nfe) {
                    }
                    break;
                case CPF:
                    definirPessoa(item, valorCelula);
                    break;
                case NOME:
                    item.setNome(valorCelula);
                    break;
                case ADMISSAO:
                    try {
                        item.setAdmissao(DataUtil.getDateParse(valorCelula));
                    } catch (Exception e) {
                    }
                    break;
                case VIGENCIA:
                    try {
                        item.setVigencia(DataUtil.getDateParse(valorCelula));
                    } catch (Exception e) {
                    }
                    break;
                case CARGO:
                    item.setCargo(valorCelula);
                    break;
                case CONTRATO:
                    item.setContrato(valorCelula);
                    break;
                case MATRICULA:
                    item.setMatricula(valorCelula);
                    break;
                case CODIGO:
                    item.setCodigo(valorCelula);
                    break;
                case CODIGO_LOTACAO_EXERCICIO:
                    item.setCodigoLotacaoExercicio(valorCelula);
                    break;
                case CODIGO_LOTACAO_FOLHA:
                    item.setCodigoLotacaoFolha(valorCelula);
                    break;
                case CARGA_HORARIA:
                    try {
                        String cargaHoraria = StringUtil.retornaApenasNumeros(valorCelula);
                        item.setCargaHoraria(Integer.valueOf(cargaHoraria));
                    } catch (Exception e) {
                    }
                    break;
                default:
                    break;
            }
        }
        pessoasImportadas.add(item);
    }

    private String getValorCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            try {
                if (cell.getDateCellValue() != null) {
                    String dataFormatada = DataUtil.getDataFormatada(cell.getDateCellValue());
                    if (new DateTime(1900, 1, 1, 0, 0, 0).getYear() == new DateTime(cell.getDateCellValue()).getYear()) {
                        return cell.getNumericCellValue() + "";
                    } else {
                        return dataFormatada;
                    }
                }
            } catch (Exception nf) {
            }

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

    private void definirPessoa(ContratoFPLote item, String valorCelula) {
        item.setCpf(Util.formatarCpf(valorCelula));
        Long idPessoaFisica = pessoaFisicaFacade.buscarIdDePessoaPorCpf(valorCelula);
        if (idPessoaFisica != null) {
            PessoaFisica pf = pessoaFisicaFacade.recuperar(idPessoaFisica);
            item.setPessoaFisica(pf);
        }
    }

}
