package br.com.webpublico.nfse.util;

import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.facades.ArquivoDesifFacade;
import com.google.common.collect.Lists;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.List;

public class GeradorExcelArquivoDesif extends AbstractGeradorExcel {


    private ArquivoDesif arquivoDesif;
    private ArquivoDesifFacade facade;


    public GeradorExcelArquivoDesif(ArquivoDesif arquivoDesif, ArquivoDesifFacade facade) {
        super();
        this.arquivoDesif = arquivoDesif;
        this.facade = facade;
    }

    @Override
    public void popularWorkbook() {
        criarAbaDadosGerais();
        switch (arquivoDesif.getModulo()) {
            case MODULO_1: {
                criarAbaRegistro0400();
                criarAbaRegistro0410();
                criarAbaRegistro1000();
                break;
            }
            case MODULO_2: {
                criarAbaRegistro0400();
                criarAbaRegistro0430();
                criarAbaRegistro0440();
                break;
            }
            case MODULO_3: {
                criarAbaRegistro0100();
                criarAbaRegistro0200();
                criarAbaRegistro0300();
                break;
            }
            case MODULO_4: {
                criarAbaRegistro1000();
                break;
            }
        }
    }

    private void criarAbaDadosGerais() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Dados Gerais");

        linha = 0;

        adicionarMunicipio(sheet);

        List<String> titulos = Lists.newArrayList("Prestador", "Enviado em", "Módulo", "Tipo", "Situação",
            "Protocolo", "Competência Inicial", "Competência Final", "Tipo Instituição",
            "Tipo de Consolidação", "Tipo de Arredondamento");
        List<Object[]> objetos = Lists.newArrayList();
        objetos.add(new Object[]{arquivoDesif.getCadastroEconomico().getCmcNomeCpfCnpj(), arquivoDesif.getEnviadoEm(),
            arquivoDesif.getModulo().getDescricao(), arquivoDesif.getTipo().getDescricao(),
            arquivoDesif.getSituacao().getDescricao(), arquivoDesif.getProtocolo(), arquivoDesif.getInicioCompetencia(),
            arquivoDesif.getFimCompetencia(), arquivoDesif.getTipoInstituicaoFinanceira().toString(),
            arquivoDesif.getTipoConsolidacao().getDescricao(), arquivoDesif.getTipoArredondamento().getDescricao()});

        linha++;

        adicionarLista(sheet, titulos, objetos);

    }

    private void criarAbaRegistro0100() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Plano Geral de Contas Comentado – PGCC");

        linha = 0;

        List<ArquivoDesifRegistro0100> registros0100 = facade.getArquivoDesifRegistro0100Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("Conta", "Desdobramento", "Nome");
        List<Object[]> objetos = Lists.newArrayList();
        if (registros0100 != null) {
            for (ArquivoDesifRegistro0100 registro0100 : registros0100) {
                objetos.add(new Object[]{registro0100.getConta(), registro0100.getDesdobramento(),
                    registro0100.getNome()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);

    }

    private void criarAbaRegistro0200() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Tarifas Bancárias");

        linha = 0;

        List<ArquivoDesifRegistro0200> registros0200 = facade.getArquivoDesifRegistro0200Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("Tarifa Bancária");
        List<Object[]> objetos = Lists.newArrayList();
        if (registros0200 != null) {
            for (ArquivoDesifRegistro0200 registro0200 : registros0200) {
                objetos.add(new Object[]{registro0200.getTarifaBancaria().toString()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);
    }

    private void criarAbaRegistro0300() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Identificação de Outros Produtos e Serviços");

        linha = 0;

        List<ArquivoDesifRegistro0300> registros0300 = facade.getArquivoDesifRegistro0300Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("Produto/Serviço");
        List<Object[]> objetos = Lists.newArrayList();
        if (registros0300 != null) {
            for (ArquivoDesifRegistro0300 registro0300 : registros0300) {
                objetos.add(new Object[]{registro0300.getProdutoServicoBancario().toString()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);
    }

    private void criarAbaRegistro0400() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Identificação da Dependência");

        linha = 0;

        List<ArquivoDesifRegistro0400> registros0400 = facade.getArquivoDesifRegistro0400Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("Código Dependência", "Identificação", "CNPJ Próprio", "Tipo Dependência",
            "CNPJ Responsável");
        List<Object[]> objetos = Lists.newArrayList();
        if (registros0400 != null) {
            for (ArquivoDesifRegistro0400 registro0400 : registros0400) {
                objetos.add(new Object[]{registro0400.getCodigoDependencia(),
                    registro0400.getIdentificacaoDependencia().getDescricao(), registro0400.getCnpjProprio(),
                    registro0400.getTipoDependencia().getDescricao(), registro0400.getCnpjResponsavel()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);
    }

    private void criarAbaRegistro0410() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Balancete Analítico Mensal");

        linha = 0;

        List<ArquivoDesifRegistro0410> registros0410 = facade.getArquivoDesifRegistro0410Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("Código Dependência", "Competência", "Conta", "Desdobramento",
            "Saldo Inicial", "Valor Débito", "Valor Crédito", "Saldo Final");
        List<Object[]> objetos = Lists.newArrayList();
        if (registros0410 != null) {
            for (ArquivoDesifRegistro0410 registro0410 : registros0410) {
                objetos.add(new Object[]{registro0410.getCodigoDependencia(),
                    registro0410.getCompetencia(), registro0410.getConta().getConta(),
                    registro0410.getConta().getDesdobramento(), registro0410.getSaldoInicial(),
                    registro0410.getValorDebito(), registro0410.getValorCredito(), registro0410.getSaldoFinal()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);
    }

    private void criarAbaRegistro0430() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Demonstrativo da apuração da receita tributável e do ISSQN mensal por Subtítulo");

        linha = 0;

        List<ArquivoDesifRegistro0430> registros0430 = facade.getArquivoDesifRegistro0430Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("Código da Dependência", "Conta", "Desdobramento",
            "Valor Crédito", "Valor Débito", "Valor Receita Tributável", "Base de Cálculo", "Alíquota",
            "Valor ISSQN Retido");

        List<Object[]> objetos = Lists.newArrayList();
        if (registros0430 != null) {
            for (ArquivoDesifRegistro0430 registro0430 : registros0430) {
                objetos.add(new Object[]{registro0430.getCodigoDependencia(),
                    registro0430.getConta().getConta(), registro0430.getConta().getDesdobramento(),
                    registro0430.getValorCredito(), registro0430.getValorDebito(),
                    registro0430.getValorReceitaTributavel(), registro0430.getBaseCalculo(),
                    registro0430.getAliquota(), registro0430.getValorIssqnRetido()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);
    }

    private void criarAbaRegistro0440() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Demonstrativo do ISSQN mensal a recolher");

        linha = 0;

        List<ArquivoDesifRegistro0440> registros0440 = facade.getArquivoDesifRegistro0440Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("CNPJ Dependência", "Código de Tributação", "Valor Receita Tributável",
            "Base de Cálculo", "Alíquota", "Valor ISSQN Devido", "Valor ISSQN Retido", "Valor Compensação",
            "Valor ISSQN Recolhido", "Valor ISSQN a Recolher");

        List<Object[]> objetos = Lists.newArrayList();
        if (registros0440 != null) {
            for (ArquivoDesifRegistro0440 registro0440 : registros0440) {
                objetos.add(new Object[]{registro0440.getCnpjDependencia(),
                    registro0440.getCodigoTributacao().toString(), registro0440.getValorReceitaTributavel(),
                    registro0440.getBaseCalculo(), registro0440.getAliquota(),
                    registro0440.getValorIssqn(), registro0440.getValorIssqnRetido(),
                    registro0440.getValorCompensacao(), registro0440.getValorIssqnRecolhido(),
                    registro0440.getValorIssqnRecolher()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);
    }

    private void criarAbaRegistro1000() {
        XSSFSheet sheet = excelUtil.criarSheet(workbook, "Demonstrativo das Partidas dos Lançamentos Contábeis");

        linha = 0;

        List<ArquivoDesifRegistro1000> registros1000 = facade.getArquivoDesifRegistro1000Facade().buscarRegistros(arquivoDesif);

        List<String> titulos = Lists.newArrayList("CNPJ Contabilização", "Número", "Data", "Valor da Partida",
            "Conta", "Tipo Partida");

        List<Object[]> objetos = Lists.newArrayList();
        if (registros1000 != null) {
            for (ArquivoDesifRegistro1000 registro1000 : registros1000) {
                objetos.add(new Object[]{registro1000.getCnpj(), registro1000.getNumeroLancamento(),
                    registro1000.getDataLancamento(), registro1000.getValorPartidaLancamento(),
                    registro1000.getConta(), registro1000.getTipoPartida().getDescricao()});
            }
        }

        linha++;

        adicionarLista(sheet, titulos, objetos);
    }
}
