package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.ImportacaoExportacaoDadosSIOPE;
import br.com.webpublico.entidadesauxiliares.rh.ResumoFichaFinanceira;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HardRock on 29/03/2017.
 */
@ManagedBean(name = "importacaoExportacaoDadosSIOPEControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "exportacao-importacao-dados", pattern = "/importacao-exportacao-dados/SIOPE/", viewId = "/faces/rh/dados-siope/edita.xhtml")
})
public class ImportacaoExportacaoDadosSIOPEControlador implements Serializable {

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
    private List<ImportacaoExportacaoDadosSIOPE> dadosSIOPE;
    private Integer mes;
    private Exercicio exercicio;

    @URLAction(mappingId = "exportacao-importacao-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        dadosSIOPE = Lists.newArrayList();
    }

    public void importar(FileUploadEvent event) {
        dadosSIOPE = Lists.newArrayList();
        try {
            validarImportacaoArquivo();
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
            preencherColunaValor();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a importação do arquivo: " + ex.getMessage());
        }
    }

    private void preencherColunaValor() {
        for (ImportacaoExportacaoDadosSIOPE item : dadosSIOPE) {
            for (VinculoFP vinculoFP : item.getVinculos()) {
                List<ResumoFichaFinanceira> resumos = fichaFinanceiraFPFacade.buscarResumoFichaFinanceira(vinculoFP.getId(), Mes.getMesToInt(mes), exercicio.getAno(), TipoFolhaDePagamento.NORMAL);
                item.setCargaHoraria(contratoFPFacade.buscarCargaHorariaPorContrato(vinculoFP.getId()));
                if (resumos != null) {
                    for (ResumoFichaFinanceira resumo : resumos) {
                        item.setSalarioBase(resumo.getVencimentoBase());
                        item.setRemuneracaoParcelaMaximaSessenta(BigDecimal.ZERO);
                        item.setRemuneracaoParcelaMaximaQuarenta(BigDecimal.ZERO);
                        item.setOutrasReceita(resumo.getOutrasVerbas());
                        item.setTotal(item.getRemuneracaoParcelaMaximaSessenta().add(item.getRemuneracaoParcelaMaximaQuarenta().add(item.getOutrasReceita())));
                    }
                }
            }
        }
    }

    private void validarImportacaoArquivo() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        ve.lancarException();
        if (mes != null && mes < 1 || mes > 12) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        ve.lancarException();
    }

    private void percorrerCelulas(Row row) {
        ImportacaoExportacaoDadosSIOPE item = new ImportacaoExportacaoDadosSIOPE();
        for (CampoImportacao campoImportacao : CampoImportacao.values()) {
            Cell cell = row.getCell(campoImportacao.ordinal());
            String valorCelula = getValorCell(cell);
            switch (campoImportacao) {
                case NUMERO:
                    item.setNumero(converterString(valorCelula));
                    break;
                case CPF:
                    item.setCpf(Util.formatarCpf(valorCelula));
                    Long idPessoaFisica = pessoaFisicaFacade.buscarIdDePessoaPorCpf(valorCelula);
                    if (idPessoaFisica != null) {
                        PessoaFisica pf = pessoaFisicaFacade.recuperar(idPessoaFisica);
                        item.setPessoaFisica(pf);
                        item.setVinculos(vinculoFPFacade.buscarVinculosVigentesPorPessoa(pf, sistemaFacade.getDataOperacao()));
                    }
                    break;
                case NOME:
                    item.setNome(valorCelula);
                    break;
                case NUMERO_LOCAL_EXERCICIO:
                    item.setNumeroLocalExercicio(converterString(valorCelula));
                    break;
                case LOCAL_EXERCICIO:
                    item.setLocalExercicio(valorCelula);
                    break;
                default:
                    break;
            }
            item.setTipoCategoria("");
            item.setCategoria("");
        }
        dadosSIOPE.add(item);
    }

    private String converterString(String descricao) {
        try {
            return Integer.valueOf(descricao).toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public StreamedContent exportar() {
        try {
            List<Object[]> objetos = adicionarObjetos();
            ExcelUtil excel = new ExcelUtil();
            String nomeArquivo = getNomeArquivo();
            String nomePlanilha = nomeArquivo + "_temp";
            File file = File.createTempFile(nomePlanilha, "xls");
            int linhaInicial = 0;

            FileOutputStream fout = new FileOutputStream(file);
            XSSFWorkbook pastaDeTrabalho = new XSSFWorkbook();
            XSSFSheet sheet = excel.criarSheet(pastaDeTrabalho, nomePlanilha);

            linhaInicial = adicionarTituloColunas(excel, linhaInicial, sheet);
            adicionarValorColunas(objetos, excel, linhaInicial, sheet);
            pastaDeTrabalho.write(fout);
            return fileDownload(file, nomeArquivo);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a exportação do arquivo: " + ex.getMessage());
        }
        return null;
    }

    private List<Object[]> adicionarObjetos() {
        List<Object[]> objetos = new ArrayList<>();
        for (ImportacaoExportacaoDadosSIOPE item : dadosSIOPE) {
            Object[] obj = new Object[15];
            obj[0] = item.getNumero();
            obj[1] = item.getCpf();
            obj[2] = item.getNome();
            obj[3] = item.getNumeroLocalExercicio();
            obj[4] = item.getLocalExercicio();
            obj[5] = item.getCargaHoraria();
            obj[6] = item.getTipoCategoria();
            obj[7] = item.getCategoria();
            obj[8] = item.getSalarioBase();
            obj[9] = item.getRemuneracaoParcelaMaximaSessenta();
            obj[10] = item.getRemuneracaoParcelaMaximaQuarenta();
            obj[11] = item.getOutrasReceita();
            obj[12] = item.getTotal();
            objetos.add(obj);
        }
        return objetos;
    }

    private void adicionarValorColunas(List<Object[]> objetos, ExcelUtil excel, int linhaInicial, XSSFSheet sheet) {
        for (Object o : objetos) {
            XSSFRow linha = excel.criaRow(sheet, linhaInicial);

            Object[] objeto = (Object[]) o;
            int i = 0;
            for (Object atributo : objeto) {
                if (atributo != null) {
                    excel.criaCell(linha, i).setCellValue(atributo.toString());
                }
                i++;
            }
            linhaInicial++;
        }
    }

    private String getMesFormatado() {
        if (mes < 10) {
            return "0" + mes;
        }
        return String.valueOf(mes);
    }

    private String getNomeArquivo() {
        if (mes != null) {
            return "Remuneração dos Profissionais de Educação / " + getMesFormatado() + " - " + DataUtil.getDescricaoMes(mes);
        }
        return "Remuneração dos Profissionais de Educação";
    }

    private int adicionarTituloColunas(ExcelUtil excel, int linhaInicial, XSSFSheet sheet) {
        XSSFRow titulo = excel.criaRow(sheet, linhaInicial);
        excel.criaCell(titulo, linhaInicial).setCellValue(getNomeArquivo());
        linhaInicial++;

        List<String> titulosColunaIdentificacao = new ArrayList<>();
        titulosColunaIdentificacao.add("Nº");
        titulosColunaIdentificacao.add("CPF");
        titulosColunaIdentificacao.add("Nome");
        titulosColunaIdentificacao.add("Local de Exercício");
        titulosColunaIdentificacao.add(" ");
        titulosColunaIdentificacao.add("Valor Bruto da Remuneração (R$)");

        XSSFRow cabecalho = excel.criaRow(sheet, linhaInicial);
        for (String atributo : titulosColunaIdentificacao) {
            excel.criaCell(cabecalho, titulosColunaIdentificacao.indexOf(atributo)).setCellValue(atributo);
        }
        linhaInicial++;

        List<String> titulosColunaValor = new ArrayList<>();
        titulosColunaValor.add(" ");
        titulosColunaValor.add(" ");
        titulosColunaValor.add(" ");
        titulosColunaValor.add(" ");
        titulosColunaValor.add(" ");
        titulosColunaValor.add("Carga Horária");
        titulosColunaValor.add("Tipo Categoria");
        titulosColunaValor.add("Categoria");
        titulosColunaValor.add("Salário ou Vencimento Básico R$");
        titulosColunaValor.add("Com Parcela Máxima de 60% do FUNDEB (a)");
        titulosColunaValor.add("Com Parcela Máxima de 40% do FUNDEB (b)");
        titulosColunaValor.add("Outras Receitas (c)");
        titulosColunaValor.add("Total (a+b+c)");

        XSSFRow cabecalhoValor = excel.criaRow(sheet, linhaInicial);
        for (String atributo : titulosColunaValor) {
            excel.criaCell(cabecalhoValor, titulosColunaValor.indexOf(atributo)).setCellValue(atributo);
        }
        linhaInicial++;
        return linhaInicial;
    }

    private StreamedContent fileDownload(File file, String nomeArquivo) throws FileNotFoundException {
        InputStream stream = new FileInputStream(file);
        return new DefaultStreamedContent(stream, "application/xls", nomeArquivo + ".xls");
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

    public List<CampoImportacao> getCamposImportacao() {
        return Arrays.asList(CampoImportacao.values());
    }

    public List<ImportacaoExportacaoDadosSIOPE> getDadosSIOPE() {
        return dadosSIOPE;
    }

    public void setDadosSIOPE(List<ImportacaoExportacaoDadosSIOPE> dadosSIOPE) {
        this.dadosSIOPE = dadosSIOPE;
    }

    public enum CampoImportacao {
        NUMERO("Nº"),
        CPF("CPF"),
        NOME("Nome"),
        NUMERO_LOCAL_EXERCICIO("Nº Local Exercício"),
        LOCAL_EXERCICIO("Local Exercício");
        private String descricao;

        CampoImportacao(String descricao) {
            this.descricao = descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}
