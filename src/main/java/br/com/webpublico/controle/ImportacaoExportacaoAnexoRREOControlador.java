package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.*;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.BimestreAnexosLei;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "importar-exportar-rreo", pattern = "/rreo/importacao-exportacao/", viewId = "/faces/financeiro/relatorioslrf/importacao-exportacao-rreo.xhtml")
})
public class ImportacaoExportacaoAnexoRREOControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoExportacaoAnexoRREOControlador.class);
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Workbook workbook;
    private BimestreAnexosLei bimestre;
    private File arquivo;
    private String nomeArquivo;
    private Integer numeroAbaAnexo1;
    private Integer numeroAbaAnexo2;
    private Integer numeroAbaAnexo3;
    private Integer numeroAbaAnexo4;
    private Integer numeroAbaAnexo6;
    private Integer numeroAbaAnexo7;
    private Integer numeroAbaAnexo13;
    private Integer numeroAbaAnexo14;
    private boolean processando;
    private String mensagem;

    @URLAction(mappingId = "importar-exportar-rreo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        bimestre = BimestreAnexosLei.PRIMEIRO_BIMESTRE;
        workbook = null;
        numeroAbaAnexo1 = 0;
        numeroAbaAnexo2 = 1;
        numeroAbaAnexo3 = 2;
        numeroAbaAnexo4 = 3;
        numeroAbaAnexo6 = 4;
        numeroAbaAnexo7 = 5;
        numeroAbaAnexo13 = 6;
        numeroAbaAnexo14 = 7;
        processando = false;
        mensagem = "";
    }

    public List<SelectItem> getBimestres() {
        return Util.getListSelectItemSemCampoVazio(BimestreAnexosLei.values(), false);
    }

    public void importar(FileUploadEvent event) {
        try {
            validarNumerosDasAbas();
            FacesUtil.executaJavaScript("");
            UploadedFile file = event.getFile();
            workbook = WorkbookFactory.create(file.getInputstream());
            nomeArquivo = event.getFile().getFileName().replace(".xls", "");
            arquivo = File.createTempFile(nomeArquivo + "_temp", ".xls");
            processando = true;
            atualizarPlanilha();
            workbook.close();
            processando = false;
            mensagem = "";
            FacesUtil.addOperacaoRealizada("O arquivo foi atualizado com sucesso!");
        } catch (ValidacaoException ve) {
            processando = false;
            arquivo = null;
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            processando = false;
            arquivo = null;
            logger.error("Erro ao exportar: {}" + ex);
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao importar/exportar os anexos: " + ex.getMessage());
        }
    }

    public void fecharDialog() {
        if (!isProcessando()) {
            FacesUtil.executaJavaScript("dialogProgressBar.hide()");
        }
    }

    private void validarNumerosDasAbas() {
        ValidacaoException ve = new ValidacaoException();
        validarNumeroDaAba(ve, numeroAbaAnexo1, "1");
        validarNumeroDaAba(ve, numeroAbaAnexo2, "2");
        validarNumeroDaAba(ve, numeroAbaAnexo3, "3");
        validarNumeroDaAba(ve, numeroAbaAnexo4, "4");
        validarNumeroDaAba(ve, numeroAbaAnexo6, "6");
        validarNumeroDaAba(ve, numeroAbaAnexo7, "7");
        validarNumeroDaAba(ve, numeroAbaAnexo13, "13");
        validarNumeroDaAba(ve, numeroAbaAnexo14, "14");
        ve.lancarException();
    }

    private void validarNumeroDaAba(ValidacaoException ve, Integer numeroAba, String numeroDoAnexo) {
        if (numeroAba == null) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("O campo Número da Aba do Anexo " + numeroDoAnexo + " deve ser informado.");
        }
    }

    public StreamedContent fileDownload() {
        try {
            FileOutputStream fout = new FileOutputStream(arquivo);
            workbook.write(fout);
            InputStream stream = new FileInputStream(arquivo);
            return new DefaultStreamedContent(stream, "application/xls", nomeArquivo + ".xls");
        } catch (Exception ex) {
            logger.error("Erro ao exportar: " + ex.getMessage());
        }
        return null;
    }

    private void atualizarPlanilha() {
        if (numeroAbaAnexo1 != null) {
            mensagem = "Calculando dados do Anexo 1";
            atualizarAnexo1();
        }
        if (numeroAbaAnexo2 != null) {
            mensagem = "Calculando dados do Anexo 2";
            atualizarAnexo2();
        }
        if (numeroAbaAnexo3 != null) {
            mensagem = "Calculando dados do Anexo 3";
            atualizarAnexo3();
        }
        if (numeroAbaAnexo4 != null) {
            mensagem = "Calculando dados do Anexo 4";
            atualizarAnexo4();
        }
        if (numeroAbaAnexo6 != null) {
            mensagem = "Calculando dados do Anexo 6";
            atualizarAnexo6();
        }
        if (numeroAbaAnexo7 != null) {
            mensagem = "Calculando dados do Anexo 7";
            atualizarAnexo7();
        }
        if (numeroAbaAnexo13 != null) {
            mensagem = "Calculando dados do Anexo 13";
            atualizarAnexo13();
        }
        if (numeroAbaAnexo14 != null) {
            mensagem = "Calculando dados do Anexo 14";
            atualizarAnexo14();
        }
    }

    private void atualizarAnexo1() {
        RelatorioRREOAnexo01Controlador relatorioRREOAnexo01Controlador = (RelatorioRREOAnexo01Controlador) Util.getControladorPeloNome("relatorioRREOAnexo01Controlador");
        Sheet abaAnexo1 = workbook.getSheetAt(numeroAbaAnexo1);
        relatorioRREOAnexo01Controlador.setItens(popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaFacade.getExercicioCorrente(), "", "Anexo 1", TipoRelatorioItemDemonstrativo.RREO)));
        relatorioRREOAnexo01Controlador.setBimestre(bimestre);
        relatorioRREOAnexo01Controlador.setRelatoriosItemDemonst(itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaFacade.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        relatorioRREOAnexo01Controlador.instanciarItemDemonstrativoFiltros();
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo01Controlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo1/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<RREOAnexo01ItemReceita> receita = mapper.convertValue(parameters.get("RECEITA"), new TypeReference<List<RREOAnexo01ItemReceita>>() {
        });
        List<RREOAnexo01ItemReceita> receitaIntra = mapper.convertValue(parameters.get("RECEITAINTRA"), new TypeReference<List<RREOAnexo01ItemReceita>>() {
        });
        List<LRFAnexo01ItemRelatorioDespesa> despesas = mapper.convertValue(parameters.get("DESPESAS"), new TypeReference<List<LRFAnexo01ItemRelatorioDespesa>>() {
        });
        List<LRFAnexo01ItemRelatorioDespesa> despesaIntra = mapper.convertValue(parameters.get("DESPESAINTRA"), new TypeReference<List<LRFAnexo01ItemRelatorioDespesa>>() {
        });
        atualizarReceitaAnexo1(abaAnexo1, (receita));
        atualizarReceitaAnexo1(abaAnexo1, (receitaIntra));
        atualizarDespesaAnexo1(abaAnexo1, (despesas));
        atualizarDespesaAnexo1(abaAnexo1, (despesaIntra));
    }

    private void limparLinhas(Sheet aba, Integer numeroLinhaNoXLS, Integer quantidadeDeCelulas) {
        for (Integer celula = 1; celula <= quantidadeDeCelulas; celula++) {
            limparLinha(aba, numeroLinhaNoXLS, celula);
        }
    }

    private void atualizarDespesaAnexo1(Sheet abaAnexo1, List<LRFAnexo01ItemRelatorioDespesa> despesas) {
        for (LRFAnexo01ItemRelatorioDespesa despesa : despesas) {
            if (despesa != null && despesa.getNumeroLinhaNoXLS() != null && despesa.getNumeroLinhaNoXLS() != 0) {
                limparLinhas(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 10);
            }
        }
        for (LRFAnexo01ItemRelatorioDespesa despesa : despesas) {
            if (despesa != null && despesa.getNumeroLinhaNoXLS() != null && despesa.getNumeroLinhaNoXLS() != 0) {
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 1, despesa.getDotacaoInicial() != null ? despesa.getDotacaoInicial() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 2, despesa.getDotacaoAtualizada() != null ? despesa.getDotacaoAtualizada() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 3, despesa.getDespesasEmpenhadasNoBimestre() != null ? despesa.getDespesasEmpenhadasNoBimestre() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 4, despesa.getDespesasEmpenhadasAteBimestre() != null ? despesa.getDespesasEmpenhadasAteBimestre() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 5, despesa.getDotacaoAtualizada().subtract(despesa.getDespesasEmpenhadasAteBimestre()));
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 6, despesa.getDespesasLiquidadasNoBimestre() != null ? despesa.getDespesasLiquidadasNoBimestre() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 7, despesa.getDespesasLiquidadasAteBimestre() != null ? despesa.getDespesasLiquidadasAteBimestre() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 8, despesa.getSaldoLiquidar() != null ? despesa.getSaldoLiquidar() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 9, despesa.getDespesasPagasAteBimestre() != null ? despesa.getDespesasPagasAteBimestre() : BigDecimal.ZERO);
                if (bimestre.isUltimoBimestre()) {
                    atualizarLinha(abaAnexo1, despesa.getNumeroLinhaNoXLS(), 10, despesa.getInscritosRestoPagar() != null ? despesa.getInscritosRestoPagar() : BigDecimal.ZERO);
                }
            }
        }
    }

    private void atualizarReceitaAnexo1(Sheet abaAnexo1, List<RREOAnexo01ItemReceita> receitas) {
        for (RREOAnexo01ItemReceita receita : receitas) {
            if (receita != null && receita.getNumeroLinhaNoXLS() != null && receita.getNumeroLinhaNoXLS() != 0) {
                limparLinhas(abaAnexo1, receita.getNumeroLinhaNoXLS(), 7);
            }
        }
        for (RREOAnexo01ItemReceita receita : receitas) {
            if (receita != null && receita.getNumeroLinhaNoXLS() != null && receita.getNumeroLinhaNoXLS() != 0) {
                atualizarLinha(abaAnexo1, receita.getNumeroLinhaNoXLS(), 1, receita.getPrevisaoInicial() != null ? receita.getPrevisaoInicial() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, receita.getNumeroLinhaNoXLS(), 2, receita.getPrevisaoAtualizada() != null ? receita.getPrevisaoAtualizada() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, receita.getNumeroLinhaNoXLS(), 3, receita.getReceitaRealizadaNoBimestre() != null ? receita.getReceitaRealizadaNoBimestre() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, receita.getNumeroLinhaNoXLS(), 4, receita.getReceitaRealizadaNoBimestrePercentual() != null ? receita.getReceitaRealizadaNoBimestrePercentual() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, receita.getNumeroLinhaNoXLS(), 5, receita.getReceitaRealizadaAteBimestre() != null ? receita.getReceitaRealizadaAteBimestre() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, receita.getNumeroLinhaNoXLS(), 6, receita.getReceitaRealizadaAteBimestrePercentual() != null ? receita.getReceitaRealizadaAteBimestrePercentual() : BigDecimal.ZERO);
                atualizarLinha(abaAnexo1, receita.getNumeroLinhaNoXLS(), 7, receita.getSaldoARealizar() != null ? receita.getSaldoARealizar() : BigDecimal.ZERO);
            }
        }
    }

    private void atualizarAnexo2() {
        RelatorioRREOAnexo02Controlador relatorioRREOAnexo02Controlador = (RelatorioRREOAnexo02Controlador) Util.getControladorPeloNome("relatorioRREOAnexo02Controlador");
        Sheet abaAnexo2 = workbook.getSheetAt(numeroAbaAnexo2);
        relatorioRREOAnexo02Controlador.limparCampos();
        relatorioRREOAnexo02Controlador.setItens(popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaFacade.getExercicioCorrente(), "", "Anexo 2", TipoRelatorioItemDemonstrativo.RREO)));
        relatorioRREOAnexo02Controlador.setBimestre(bimestre);
        relatorioRREOAnexo02Controlador.instanciarItemDemonstrativoFiltros();
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo02Controlador.validarConfiguracao();
        relatorioRREOAnexo02Controlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo2/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<RREOAnexo02Item> excetoIntra = mapper.convertValue(parameters.get("EXCETOINTRA"), new TypeReference<List<RREOAnexo02Item>>() {
        });
        List<RREOAnexo02Item> intra = mapper.convertValue(parameters.get("INTRA"), new TypeReference<List<RREOAnexo02Item>>() {
        });
        atualizarValoresAnexo2(abaAnexo2, excetoIntra);
        atualizarValoresAnexo2(abaAnexo2, intra);
    }

    private void atualizarValoresAnexo2(Sheet abaAnexo2, List<RREOAnexo02Item> itens) {
        for (RREOAnexo02Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                limparLinhas(abaAnexo2, item.getNumeroLinhaNoXLS(), 11);
            }
        }
        for (RREOAnexo02Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 1, item.getDotacaoInicial());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 2, item.getDotacaoAtualizada());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 3, item.getDespesaEmpenhadasNoBimestre());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 4, item.getDespesaEmpenhadasAteOBimestre());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 5, item.getDespesaEmpenhadasAteOBimestrePercentualTotal());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 6, item.getDotacaoAtualizada().subtract(item.getDespesaEmpenhadasAteOBimestre()));
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 7, item.getDespesaLiquidadaNoBimestre());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 8, item.getDespesaLiquidadaAteOBimestre());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 9, item.getDespesaLiquidadaAteOBimestrePercentualTotal());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 10, item.getSaldoALiquidar());
                atualizarLinha(abaAnexo2, item.getNumeroLinhaNoXLS(), 11, item.getInscritasEmRestosAPagar());
            }
        }
    }

    private void atualizarAnexo3() {
        RelatorioRREOAnexo03Controlador relatorioRREOAnexo03Controlador = (RelatorioRREOAnexo03Controlador) Util.getControladorPeloNome("relatorioRREOAnexo03Controlador");
        Sheet abaAnexo3 = workbook.getSheetAt(numeroAbaAnexo3);
        relatorioRREOAnexo03Controlador.setItens(popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaFacade.getExercicioCorrente(), "", "Anexo 3", TipoRelatorioItemDemonstrativo.RREO)));
        relatorioRREOAnexo03Controlador.setMes(bimestre.getMesFinal());
        relatorioRREOAnexo03Controlador.setRelatoriosItemDemonst(itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaFacade.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo03Controlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo3/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<LRFAnexo03ItemRelatorio> receitas = mapper.convertValue(parameters.get("RECEITAS"), new TypeReference<List<LRFAnexo03ItemRelatorio>>() {
        });
        for (LRFAnexo03ItemRelatorio receita : receitas) {
            if (receita.getNumeroLinhaNoXLS() != null) {
                limparLinhas(abaAnexo3, receita.getNumeroLinhaNoXLS(), 14);
            }
        }
        for (LRFAnexo03ItemRelatorio receita : receitas) {
            if (receita.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 1, receita.getMesMenosOnze());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 2, receita.getMesMenosDez());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 3, receita.getMesMenosNove());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 4, receita.getMesMenosOito());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 5, receita.getMesMenosSete());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 6, receita.getMesMenosSeis());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 7, receita.getMesMenosCinco());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 8, receita.getMesMenosQuatro());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 9, receita.getMesMenosTres());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 10, receita.getMesMenosDois());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 11, receita.getMesMenosUm());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 12, receita.getMesAtual());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 13, receita.getTotalUltimosMeses());
                atualizarLinha(abaAnexo3, receita.getNumeroLinhaNoXLS(), 14, receita.getPrevisaoAtualizada());
            }
        }
    }

    private void atualizarAnexo4() {
        Sheet abaAnexo4 = workbook.getSheetAt(numeroAbaAnexo4);
        RelatorioRREOAnexo04Controlador relatorioRREOAnexo04Controlador = (RelatorioRREOAnexo04Controlador) Util.getControladorPeloNome("relatorioRREOAnexo04Controlador");
        relatorioRREOAnexo04Controlador.setItens(popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaFacade.getExercicioCorrente(), "", "Anexo 4", TipoRelatorioItemDemonstrativo.RREO)));
        relatorioRREOAnexo04Controlador.setBimestre(bimestre);
        relatorioRREOAnexo04Controlador.instanciarItemDemonstrativoFiltros();
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo04Controlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo4/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<RREOAnexo04Item> grupo1 = mapper.convertValue(parameters.get("GRUPO1"), new TypeReference<List<RREOAnexo04Item>>() {
        });
        List<RREOAnexo04Item> grupo2 = mapper.convertValue(parameters.get("GRUPO2"), new TypeReference<List<RREOAnexo04Item>>() {
        });
        List<RREOAnexo04Item> grupo3 = mapper.convertValue(parameters.get("GRUPO3"), new TypeReference<List<RREOAnexo04Item>>() {
        });
        List<RREOAnexo04Item> grupo4 = mapper.convertValue(parameters.get("GRUPO4"), new TypeReference<List<RREOAnexo04Item>>() {
        });
        List<RREOAnexo04Item> grupo5 = mapper.convertValue(parameters.get("GRUPO5"), new TypeReference<List<RREOAnexo04Item>>() {
        });
        List<RREOAnexo04Item> grupo6 = mapper.convertValue(parameters.get("GRUPO6"), new TypeReference<List<RREOAnexo04Item>>() {
        });
        List<RREOAnexo04Item> grupo7 = mapper.convertValue(parameters.get("GRUPO7"), new TypeReference<List<RREOAnexo04Item>>() {
        });
        atualizarReceitasAnexo4(abaAnexo4, grupo1);
        atualizarDespesasAnexo4(abaAnexo4, grupo2);
        atualizarReceitasAnexo4(abaAnexo4, grupo3);
        atualizarReceitasAnexo4(abaAnexo4, grupo4);
        atualizarReceitasAnexo4(abaAnexo4, grupo5);
        atualizarReceitasAnexo4(abaAnexo4, grupo6);
        atualizarDespesasAnexo4(abaAnexo4, grupo7);
    }

    private void atualizarReceitasAnexo4(Sheet abaAnexo4, List<RREOAnexo04Item> itens) {
        for (RREOAnexo04Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                limparLinhas(abaAnexo4, item.getNumeroLinhaNoXLS(), 4);
            }
        }
        for (RREOAnexo04Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 1, item.getValorColuna1());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 2, item.getValorColuna2());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 3, item.getValorColuna4());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 4, item.getValorColuna5());
            }
        }
    }

    private void atualizarDespesasAnexo4(Sheet abaAnexo4, List<RREOAnexo04Item> itens) {
        for (RREOAnexo04Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                limparLinhas(abaAnexo4, item.getNumeroLinhaNoXLS(), 8);
            }
        }
        for (RREOAnexo04Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 1, item.getValorColuna1());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 2, item.getValorColuna2());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 3, item.getValorColuna3());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 4, item.getValorColuna4());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 5, item.getValorColuna5());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 6, item.getValorColuna6());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 7, item.getValorColuna7());
                atualizarLinha(abaAnexo4, item.getNumeroLinhaNoXLS(), 8, item.getValorColuna8());
            }
        }
    }

    private void atualizarAnexo6() {
        Sheet abaAnexo6 = workbook.getSheetAt(numeroAbaAnexo6);
        RelatorioRREOAnexo06NovoControlador relatorioRREOAnexo06NovoControlador = (RelatorioRREOAnexo06NovoControlador) Util.getControladorPeloNome("relatorioRREOAnexo06NovoControlador");
        relatorioRREOAnexo06NovoControlador.limparCampos();
        relatorioRREOAnexo06NovoControlador.setMes(bimestre.getMesFinal());
        relatorioRREOAnexo06NovoControlador.setRelatoriosItemDemonst(itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6 - Novo", sistemaFacade.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        relatorioRREOAnexo06NovoControlador.setItens(popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaFacade.getExercicioCorrente(), "", "Anexo 6 - Novo", TipoRelatorioItemDemonstrativo.RREO)));
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo06NovoControlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo6-novo/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<RREOAnexo06Item> itens1 = mapper.convertValue(parameters.get("ITEM01"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens2 = mapper.convertValue(parameters.get("ITEM02"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens3 = mapper.convertValue(parameters.get("ITEM03"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens4 = mapper.convertValue(parameters.get("ITEM04"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens5 = mapper.convertValue(parameters.get("ITEM05"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens6 = mapper.convertValue(parameters.get("ITEM06"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens7 = mapper.convertValue(parameters.get("ITEM07"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens8 = mapper.convertValue(parameters.get("ITEM08"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens9 = mapper.convertValue(parameters.get("ITEM09"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens10 = mapper.convertValue(parameters.get("ITEM10"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        List<RREOAnexo06Item> itens11 = mapper.convertValue(parameters.get("ITEM11"), new TypeReference<List<RREOAnexo06Item>>() {
        });
        atualizarValoresAnexo6(abaAnexo6, itens1);
        atualizarValoresAnexo6(abaAnexo6, itens2);
        atualizarValoresAnexo6(abaAnexo6, itens3);
        atualizarValoresAnexo6(abaAnexo6, itens4);
        atualizarValoresAnexo6(abaAnexo6, itens5);
        atualizarValoresAnexo6(abaAnexo6, itens6);
        atualizarValoresAnexo6(abaAnexo6, itens7);
        atualizarValoresAnexo6(abaAnexo6, itens8);
        atualizarValoresAnexo6(abaAnexo6, itens9);
        atualizarValoresAnexo6(abaAnexo6, itens10);
        atualizarValoresAnexo6(abaAnexo6, itens11);
    }

    private void atualizarValoresAnexo6(Sheet abaAnexo6, List<RREOAnexo06Item> itens) {
        for (RREOAnexo06Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                limparLinhas(abaAnexo6, item.getNumeroLinhaNoXLS(), 7);
            }
        }
        for (RREOAnexo06Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo6, item.getNumeroLinhaNoXLS(), 1, item.getValorColuna1());
                atualizarLinha(abaAnexo6, item.getNumeroLinhaNoXLS(), 2, item.getValorColuna2());
                atualizarLinha(abaAnexo6, item.getNumeroLinhaNoXLS(), 3, item.getValorColuna3());
                atualizarLinha(abaAnexo6, item.getNumeroLinhaNoXLS(), 4, item.getValorColuna4());
                atualizarLinha(abaAnexo6, item.getNumeroLinhaNoXLS(), 5, item.getValorColuna5());
                atualizarLinha(abaAnexo6, item.getNumeroLinhaNoXLS(), 6, item.getValorColuna6());
                atualizarLinha(abaAnexo6, item.getNumeroLinhaNoXLS(), 7, item.getValorColuna7());
            }
        }
    }

    private void atualizarAnexo7() {
        Sheet abaAnexo7 = workbook.getSheetAt(numeroAbaAnexo7);
        RelatorioRREOAnexo07Controlador relatorioRREOAnexo07Controlador = (RelatorioRREOAnexo07Controlador) Util.getControladorPeloNome("relatorioRREOAnexo07Controlador");
        relatorioRREOAnexo07Controlador.setMes(bimestre.getMesFinal());
        limparLinhas(abaAnexo7, 22, 12);
        limparLinhas(abaAnexo7, 23, 12);
        limparLinhas(abaAnexo7, 27, 12);
        limparLinhas(abaAnexo7, 28, 12);
        limparLinhas(abaAnexo7, 39, 12);
        limparLinhas(abaAnexo7, 40, 12);
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo07Controlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo7/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<RREOAnexo07Item> dados = mapper.convertValue(parameters.get("DADOS"), new TypeReference<List<RREOAnexo07Item>>() {
        });
        for (RREOAnexo07Item item : dados) {
            switch (item.getDescricao().trim()) {
                case "RESTOS A PAGAR (EXCETO INTRA-ORÇAMENTÁRIOS) (I)":
                    atualizarValoresAnexo7(item, 22, abaAnexo7);
                    break;
                case "EXECUTIVO":
                    atualizarValoresAnexo7(item, 23, abaAnexo7);
                    break;
                case "RESTOS A PAGAR (INTRA-ORÇAMENTÁRIOS) (II)":
                    atualizarValoresAnexo7(item, 27, abaAnexo7);
                    atualizarValoresAnexo7(item, 39, abaAnexo7);
                    atualizarValoresAnexo7(item, 40, abaAnexo7);
                    break;
                case "TOTAL (III) = (I + II)":
                    atualizarValoresAnexo7(item, 28, abaAnexo7);
                    break;
            }
        }
    }

    private void atualizarValoresAnexo7(RREOAnexo07Item item, Integer numeroLinhaXLS, Sheet abaAnexo7) {
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 1, item.getRestoPagarProcessadosInscritosExerciciosAnteriores());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 2, item.getRestoPagarProcessadosInscritosNoExercicioAnterior());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 3, item.getRestoPagarProcessadosPagos());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 4, item.getRestoPagarProcessadosCancelados());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 5, item.getRestoPagarProcessadosAPagar());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 6, item.getRestoPagarNaoProcessadosInscritosExerciciosAnteriores());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 7, item.getRestoPagarNaoProcessadosInscritosNoExercicioAnterior());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 8, item.getRestoPagarProcessadosLiquidados());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 9, item.getRestoPagarNaoProcessadosPagos());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 10, item.getRestoPagarNaoProcessadosCancelados());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 11, item.getRestoPagarNaoProcessadosAPagar());
        atualizarLinha(abaAnexo7, numeroLinhaXLS, 12, item.getRestoPagarProcessadosAPagar().add(item.getRestoPagarNaoProcessadosAPagar()));
    }

    private void atualizarAnexo13() {
        Sheet abaAnexo13 = workbook.getSheetAt(numeroAbaAnexo13);
        RelatorioRREOAnexo13Controlador relatorioRREOAnexo13Controlador = (RelatorioRREOAnexo13Controlador) Util.getControladorPeloNome("relatorioRREOAnexo13Controlador");
        relatorioRREOAnexo13Controlador.setBimestre(bimestre);
        relatorioRREOAnexo13Controlador.setRelatoriosItemDemonst(itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 13", sistemaFacade.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO));
        relatorioRREOAnexo13Controlador.setItens(popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaFacade.getExercicioCorrente(), "", "Anexo 13", TipoRelatorioItemDemonstrativo.RREO)));
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo13Controlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo13/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<RREOAnexo13ItemQuadroDois> itensQuadroDois = mapper.convertValue(parameters.get("QUADRODOIS"), new TypeReference<List<RREOAnexo13ItemQuadroDois>>() {
        });
        for (RREOAnexo13ItemQuadroDois item : itensQuadroDois) {
            if (item.getNumeroLinhaNoXLS() != null) {
                limparLinhas(abaAnexo13, item.getNumeroLinhaNoXLS(), 11);
            }
        }
        for (RREOAnexo13ItemQuadroDois item : itensQuadroDois) {
            if (item.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 1, item.getValorColuna1());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 2, item.getValorColuna2());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 3, item.getValorColuna3());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 4, item.getValorColuna4());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 5, item.getValorColuna5());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 6, item.getValorColuna6());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 7, item.getValorColuna7());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 8, item.getValorColuna8());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 9, item.getValorColuna9());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 10, item.getValorColuna10());
                atualizarLinha(abaAnexo13, item.getNumeroLinhaNoXLS(), 11, item.getValorColuna11());
            }
        }
    }

    private void atualizarAnexo14() {
        Sheet abaAnexo14 = workbook.getSheetAt(numeroAbaAnexo14);
        RelatorioRREOAnexo14Controlador relatorioRREOAnexo14Controlador = (RelatorioRREOAnexo14Controlador) Util.getControladorPeloNome("relatorioRREOAnexo14Controlador");
        relatorioRREOAnexo14Controlador.setBimestre(bimestre);
        relatorioRREOAnexo14Controlador.instanciarItemDemonstrativoFiltros();
        relatorioRREOAnexo14Controlador.setItens(popularComponentes(itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaFacade.getExercicioCorrente(), "", "Anexo 14", TipoRelatorioItemDemonstrativo.RREO)));
        RestTemplate restTemplate = new RestTemplate();
        RelatorioDTO dto = new RelatorioDTO();
        relatorioRREOAnexo14Controlador.montarDtoSemApi(dto);
        dto.setApi("contabil/rreo-anexo14/hash-map/");
        ConfiguracaoDeRelatorio configuracao = sistemaFacade.buscarConfiguracaoDeRelatorioPorChave();
        String urlWebreport = configuracao.getUrl() + dto.getApi();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ResponseEntity<LinkedHashMap> exchange = restTemplate.exchange(urlWebreport + "gerar", HttpMethod.POST, request, LinkedHashMap.class);
        LinkedHashMap parameters = exchange.getBody();
        ObjectMapper mapper = new ObjectMapper();
        List<RREOAnexo14Item> anexo1 = mapper.convertValue(parameters.get("ANEXO1"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo2 = mapper.convertValue(parameters.get("ANEXO2"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo3 = mapper.convertValue(parameters.get("ANEXO3"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo4 = mapper.convertValue(parameters.get("ANEXO4"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo5e6 = mapper.convertValue(parameters.get("ANEXO5E6"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo7 = mapper.convertValue(parameters.get("ANEXO7"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo8 = mapper.convertValue(parameters.get("ANEXO8"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo9 = mapper.convertValue(parameters.get("ANEXO9"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo10 = mapper.convertValue(parameters.get("ANEXO10"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo11 = mapper.convertValue(parameters.get("ANEXO11"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo12 = mapper.convertValue(parameters.get("ANEXO12"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        List<RREOAnexo14Item> anexo13 = mapper.convertValue(parameters.get("ANEXO13"), new TypeReference<List<RREOAnexo14Item>>() {
        });
        atualizarValoresAnexo14ComAnexo1e2(abaAnexo14, anexo1);
        atualizarValoresAnexo14ComAnexo1e2(abaAnexo14, anexo2);
        atualizarValoresAnexo14(abaAnexo14, anexo3);
        atualizarValoresAnexo14ComAnexo1e2(abaAnexo14, anexo4);
        atualizarValoresAnexo14(abaAnexo14, anexo5e6);
        atualizarValoresAnexo14(abaAnexo14, anexo7);
        atualizarValoresAnexo14(abaAnexo14, anexo8);
        atualizarValoresAnexo14(abaAnexo14, anexo9);
        atualizarValoresAnexo14(abaAnexo14, anexo10);
        atualizarValoresAnexo14(abaAnexo14, anexo11);
        atualizarValoresAnexo14(abaAnexo14, anexo12);
        atualizarValoresAnexo14(abaAnexo14, anexo13);
    }

    private void atualizarValoresAnexo14ComAnexo1e2(Sheet abaAnexo14, List<RREOAnexo14Item> itens) {
        for (RREOAnexo14Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                limparLinha(abaAnexo14, item.getNumeroLinhaNoXLS(), 1);
            }
        }
        for (RREOAnexo14Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo14, item.getNumeroLinhaNoXLS(), 1, item.getValorColuna2());
            }
        }
    }

    private void atualizarValoresAnexo14(Sheet abaAnexo14, List<RREOAnexo14Item> itens) {
        for (RREOAnexo14Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                limparLinhas(abaAnexo14, item.getNumeroLinhaNoXLS(), 4);
            }
        }
        for (RREOAnexo14Item item : itens) {
            if (item.getNumeroLinhaNoXLS() != null) {
                atualizarLinha(abaAnexo14, item.getNumeroLinhaNoXLS(), 1, item.getValorColuna1());
                atualizarLinha(abaAnexo14, item.getNumeroLinhaNoXLS(), 2, item.getValorColuna2());
                atualizarLinha(abaAnexo14, item.getNumeroLinhaNoXLS(), 3, item.getValorColuna3());
                atualizarLinha(abaAnexo14, item.getNumeroLinhaNoXLS(), 4, item.getValorColuna4());
            }
        }
    }

    private void atualizarLinha(Sheet planilha, Integer numeroLinha, Integer celula, BigDecimal valor) {
        if (valor != null && planilha.getRow(numeroLinha - 1) != null && planilha.getRow(numeroLinha - 1).getCell(celula) != null) {
            planilha.getRow(numeroLinha - 1).getCell(celula).setCellValue(planilha.getRow(numeroLinha - 1).getCell(celula).getNumericCellValue() + valor.doubleValue());
        }
    }

    private void limparLinha(Sheet planilha, Integer numeroLinha, Integer celula) {
        if (planilha.getRow(numeroLinha - 1) != null && planilha.getRow(numeroLinha - 1).getCell(celula) != null) {
            planilha.getRow(numeroLinha - 1).getCell(celula).setCellValue(0);
        }
    }

    private List<ItemDemonstrativoComponente> popularComponentes(List<ItemDemonstrativo> itensDemonstrativos) {
        List<ItemDemonstrativoComponente> itens = Lists.newArrayList();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        return itens;
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public BimestreAnexosLei getBimestre() {
        return bimestre;
    }

    public void setBimestre(BimestreAnexosLei bimestre) {
        this.bimestre = bimestre;
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public void setWorkbook(Workbook workbook) {
        this.workbook = workbook;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public Integer getNumeroAbaAnexo1() {
        return numeroAbaAnexo1;
    }

    public void setNumeroAbaAnexo1(Integer numeroAbaAnexo1) {
        this.numeroAbaAnexo1 = numeroAbaAnexo1;
    }

    public Integer getNumeroAbaAnexo2() {

        return numeroAbaAnexo2;
    }

    public void setNumeroAbaAnexo2(Integer numeroAbaAnexo2) {
        this.numeroAbaAnexo2 = numeroAbaAnexo2;
    }

    public Integer getNumeroAbaAnexo3() {
        return numeroAbaAnexo3;
    }

    public void setNumeroAbaAnexo3(Integer numeroAbaAnexo3) {
        this.numeroAbaAnexo3 = numeroAbaAnexo3;
    }

    public Integer getNumeroAbaAnexo4() {
        return numeroAbaAnexo4;
    }

    public void setNumeroAbaAnexo4(Integer numeroAbaAnexo4) {
        this.numeroAbaAnexo4 = numeroAbaAnexo4;
    }

    public Integer getNumeroAbaAnexo6() {
        return numeroAbaAnexo6;
    }

    public void setNumeroAbaAnexo6(Integer numeroAbaAnexo6) {
        this.numeroAbaAnexo6 = numeroAbaAnexo6;
    }

    public Integer getNumeroAbaAnexo7() {
        return numeroAbaAnexo7;
    }

    public void setNumeroAbaAnexo7(Integer numeroAbaAnexo7) {
        this.numeroAbaAnexo7 = numeroAbaAnexo7;
    }

    public Integer getNumeroAbaAnexo13() {
        return numeroAbaAnexo13;
    }

    public void setNumeroAbaAnexo13(Integer numeroAbaAnexo13) {
        this.numeroAbaAnexo13 = numeroAbaAnexo13;
    }

    public Integer getNumeroAbaAnexo14() {
        return numeroAbaAnexo14;
    }

    public void setNumeroAbaAnexo14(Integer numeroAbaAnexo14) {
        this.numeroAbaAnexo14 = numeroAbaAnexo14;
    }

    public boolean isProcessando() {
        return processando;
    }

    public void setProcessando(boolean processando) {
        this.processando = processando;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
