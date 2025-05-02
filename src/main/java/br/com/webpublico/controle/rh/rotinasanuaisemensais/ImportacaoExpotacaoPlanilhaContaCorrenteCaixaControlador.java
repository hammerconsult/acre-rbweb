package br.com.webpublico.controle.rh.rotinasanuaisemensais;

import br.com.webpublico.entidadesauxiliares.rh.ExportacaoPlanilhaContaCorrenteCaixa;
import br.com.webpublico.entidadesauxiliares.rh.ImportacaoPlanilhaContaCorrenteCaixa;
import br.com.webpublico.enums.GrauInstrucaoCAGED;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.rotinasanuaismensais.ImportacaoExportacaoPlanilhaContaCorrenteCaixaFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = "importacaoExpotacaoPlanilhaContaCorrenteCaixaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "exportacao-importacao-planilha-caixa-dados", pattern = "/importacao-exportacao-dados/caixa-economica/", viewId = "/faces/rh/rotinasanuaisemensais/planilhacaixa/edita.xhtml")
})
public class ImportacaoExpotacaoPlanilhaContaCorrenteCaixaControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoExpotacaoPlanilhaContaCorrenteCaixaControlador.class);
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
    @EJB
    private ImportacaoExportacaoPlanilhaContaCorrenteCaixaFacade facade;
    private List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas;
    private List<ExportacaoPlanilhaContaCorrenteCaixa> pessoasExportacao;

    @URLAction(mappingId = "exportacao-importacao-planilha-caixa-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        pessoasImportadas = Lists.newArrayList();
        pessoasExportacao = Lists.newArrayList();
    }

    public void limpar() {
        novo();
    }

    public void importar(FileUploadEvent event) {
        try {
            validarImportacaoArquivo();
            UploadedFile file = event.getFile();
            facade.importarPessoas(pessoasImportadas, file.getInputstream());
            logger.debug("Iniciando parametrização dos dados importados.");
            List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas = facade.preencherDadosExportacao(pessoasImportadas);
            verificarPessoasAdicionadas(pessoas, pessoasExportacao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a importação do arquivo: " + ex.getMessage());
        }
    }

    private void verificarPessoasAdicionadas(List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas, List<ExportacaoPlanilhaContaCorrenteCaixa> pessoasExportacao) {
        List<ExportacaoPlanilhaContaCorrenteCaixa> lista = Lists.newLinkedList();

        for (ExportacaoPlanilhaContaCorrenteCaixa exportacaoPlanilhaContaCorrenteCaixa : pessoas) {
            if (!isCpfAdicionado(exportacaoPlanilhaContaCorrenteCaixa.getCpf(), pessoasExportacao)) {
                lista.add(exportacaoPlanilhaContaCorrenteCaixa);
            }
        }
        pessoasExportacao.addAll(lista);
        if (pessoasExportacao.isEmpty()) {
            pessoasExportacao.addAll(pessoas);
        }

    }

    private boolean isCpfAdicionado(String cpf, List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas) {
        for (ExportacaoPlanilhaContaCorrenteCaixa pessoa : pessoas) {
            if (pessoa.getCpf().equals(cpf)) {
                return true;
            }
        }

        return false;
    }


    private void validarImportacaoArquivo() {

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
            List<Object[]> objetos = adicionarObjetos(pessoasExportacao);
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

    private List<Object[]> adicionarObjetos(List<ExportacaoPlanilhaContaCorrenteCaixa> pessoasExportacao) {
        List<Object[]> objetos = new ArrayList<>();
        for (ExportacaoPlanilhaContaCorrenteCaixa item : pessoasExportacao) {
            Object[] obj = new Object[39];
            obj[0] = item.getNomeCompleto();
            obj[1] = StringUtil.cortaOuCompletaDireita(item.getNomeReduzido(), 32, "");
            obj[2] = StringUtil.retornaApenasNumeros(item.getCpf());
            obj[3] = item.getPis();
            obj[4] = DataUtil.getDataFormatada(item.getNascimento());
            obj[5] = item.getLocalNascimento();
            obj[6] = item.getUfNascimento();
            obj[7] = "1";
            obj[8] = item.getNomeConjuge();
            obj[9] = ""; //CPF do conjuge
            obj[10] = item.getNomePai();
            obj[11] = item.getNomeMae();
            obj[12] = (item.getSexo() != null ? (Sexo.FEMININO.equals(item.getSexo()) ? "1" : "2") : "");
            obj[13] = (item.getRg() != null ? "1" : "");
            obj[14] = (item.getRg() != null ? StringUtil.retornaApenasNumeros(item.getRg().getNumero()) : "");
            obj[15] = (item.getRg() != null ? descobrirOrgaoEmissor(item.getRg().getOrgaoEmissao()) : "");
            obj[16] = (item.getRg() != null ? (item.getRg().getUf() != null ? item.getRg().getUf().getUf() : "").toUpperCase() : "");
            obj[17] = (item.getRg() != null ? DataUtil.getDataFormatada(item.getRg().getDataemissao()) : "");
            obj[18] = ""; //data validade
            obj[19] = ""; //data primeira CNH
            obj[20] = "298";
            obj[21] = "06/03/2017";
            obj[22] = (item.getEnderecoCorreio() != null ? "RUA" : "");
            obj[23] = (item.getEnderecoCorreio() != null ? StringUtil.tratarCampoVazio(item.getEnderecoCorreio().getLogradouro()) : "");
            obj[24] = (item.getEnderecoCorreio() != null ? StringUtil.tratarCampoVazio(item.getEnderecoCorreio().getNumero()) : "");
            obj[25] = (item.getEnderecoCorreio() != null ? StringUtil.tratarCampoVazio(item.getEnderecoCorreio().getBairro()) : "");
            obj[26] = (item.getEnderecoCorreio() != null ? StringUtil.tratarCampoVazio(item.getEnderecoCorreio().getLocalidade()) : "");
            obj[27] = (item.getEnderecoCorreio() != null ? StringUtil.tratarCampoVazio(item.getEnderecoCorreio().getUf()).toUpperCase() : "");
            obj[28] = (item.getEnderecoCorreio() != null ? StringUtil.retornaApenasNumeros(item.getEnderecoCorreio().getCep()) : "");
            obj[29] = (item.getTelefone() != null ? StringUtil.tratarCampoVazio(item.getTelefone().getDDD()) : "");
            obj[30] = (item.getTelefone() != null ? StringUtil.retornaApenasNumeros(item.getTelefone().getTelefone()) : "");
            obj[31] = item.getEmail();
            obj[32] = tratarCampoGrauInstrucao(item.getGrauInstrucao());
            obj[33] = "910";
            obj[34] = "";
            obj[35] = "";
            obj[36] = "";
            obj[37] = "";
            obj[38] = "";
            objetos.add(obj);
        }
        return objetos;
    }

    private String tratarCampoGrauInstrucao(GrauInstrucaoCAGED grauInstrucao) {
        if (grauInstrucao != null) {
            if (GrauInstrucaoCAGED.MESTRADO.equals(grauInstrucao)) {
                return "11";
            }
            if (GrauInstrucaoCAGED.DOUTORADO.equals(grauInstrucao)) {
                return "12";
            }
            return grauInstrucao.getCodigo();
        }
        return "";
    }

    private String descobrirOrgaoEmissor(String orgaoEmissao) {
        if (orgaoEmissao != null) {
            if (orgaoEmissao.toUpperCase().contains("SSP")) {
                return "SSP";
            }
            if (orgaoEmissao.toUpperCase().contains("DPC")) {
                return "DPC";
            }
            if (orgaoEmissao.toUpperCase().contains("SCCIV")) {
                return "SCCIV";
            }
            if (orgaoEmissao.toUpperCase().contains("MAE")) {
                return "MAE";
            }
            if (orgaoEmissao.toUpperCase().contains("MEX")) {
                return "MEX";
            }
            if (orgaoEmissao.toUpperCase().contains("DPF")) {
                return "DPF";
            }
            if (orgaoEmissao.toUpperCase().contains("RIC")) {
                return "RIC";
            }
        }
        return "";
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


    private String getNomeArquivo() {
        return "Conta  Caixa";
    }

    private int adicionarTituloColunas(ExcelUtil excel, int linhaInicial, XSSFSheet sheet) {
        /*//HSSFRow titulo = excel.criaRow(sheet, linhaInicial);
        //excel.criaCell(titulo, linhaInicial).setCellValue(getNomeArquivo());
        linhaInicial++;
*/
        List<String> titulosColunaIdentificacao = new ArrayList<>();

        titulosColunaIdentificacao.add("NOME COMPLETO");
        titulosColunaIdentificacao.add("NOME REDUZIDO");
        titulosColunaIdentificacao.add("CPF");
        titulosColunaIdentificacao.add("PIS");
        titulosColunaIdentificacao.add("DATA DE NASCIMENTO");
        titulosColunaIdentificacao.add("LOCAL DE NASCIMENTO");
        titulosColunaIdentificacao.add("UF NASCIMENTO");
        titulosColunaIdentificacao.add("ESTADO CIVIL");
        titulosColunaIdentificacao.add("NOME CÔNJUGE");
        titulosColunaIdentificacao.add("CPF CÔNJUGE");
        titulosColunaIdentificacao.add("NOME PAI");
        titulosColunaIdentificacao.add("NOME MÃE");
        titulosColunaIdentificacao.add("SEXO");
        titulosColunaIdentificacao.add("DOC - TIPO DE DOCUMENTO");
        titulosColunaIdentificacao.add("DOC - NÚMERO");
        titulosColunaIdentificacao.add("DOC - ÓRGÃO EMISSOR");
        titulosColunaIdentificacao.add("DOC - UF ÓRGÃO EMISSOR");
        titulosColunaIdentificacao.add("DOC -  DATA DE EMISSÃO");
        titulosColunaIdentificacao.add("DOC -  DATA DE VALIDADE");
        titulosColunaIdentificacao.add("DOC - DATA DA PRIMEIRA CNH");
        titulosColunaIdentificacao.add("OCUPAÇÃO");
        titulosColunaIdentificacao.add("DATA DE ADMISSÃO");
        titulosColunaIdentificacao.add("TIPO DE LOGRADOURO");
        titulosColunaIdentificacao.add("ENDEREÇO");
        titulosColunaIdentificacao.add("NÚMERO (Endereço)");
        titulosColunaIdentificacao.add("BAIRRO");
        titulosColunaIdentificacao.add("CIDADE");
        titulosColunaIdentificacao.add("UF");
        titulosColunaIdentificacao.add("CEP");
        titulosColunaIdentificacao.add("DDD");
        titulosColunaIdentificacao.add("TELEFONE");
        titulosColunaIdentificacao.add("EMAIL");
        titulosColunaIdentificacao.add("GRAU DE INSTRUÇÃO");
        titulosColunaIdentificacao.add("RENDA VALOR");
        titulosColunaIdentificacao.add("CONTA DESTINO - BANCO");
        titulosColunaIdentificacao.add("CONTA DESTINO - AGÊNCIA");
        titulosColunaIdentificacao.add("CONTA DESTINO - OPERAÇÃO");
        titulosColunaIdentificacao.add("CONTA DESTINO - CONTA");
        titulosColunaIdentificacao.add("CONTA DESTINO - DV");

        XSSFRow cabecalhoValor = excel.criaRow(sheet, linhaInicial);
        for (String atributo : titulosColunaIdentificacao) {
            excel.criaCell(cabecalhoValor, titulosColunaIdentificacao.indexOf(atributo)).setCellValue(atributo);
        }
        linhaInicial++;
        return linhaInicial;
    }

    private StreamedContent fileDownload(File file, String nomeArquivo) throws FileNotFoundException {
        InputStream stream = new FileInputStream(file);
        return new DefaultStreamedContent(stream, "application/xls", nomeArquivo + ".xls");
    }


    public List<ImportacaoPlanilhaContaCorrenteCaixa> getPessoasImportadas() {
        return pessoasImportadas;
    }

    public void setPessoasImportadas(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas) {
        this.pessoasImportadas = pessoasImportadas;
    }

    public enum CampoImportacao {
        CPF("CPF"),
        NOME("Nome");
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


    public List<ExportacaoPlanilhaContaCorrenteCaixa> getPessoasExportacao() {
        return pessoasExportacao;
    }

    public void setPessoasExportacao(List<ExportacaoPlanilhaContaCorrenteCaixa> pessoasExportacao) {
        this.pessoasExportacao = pessoasExportacao;
    }
}
