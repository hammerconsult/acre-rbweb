package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.BarCode;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.PixDTO;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.auxiliares.FiltroConsultaDebitos;
import br.com.webpublico.tributario.ParcelaPixDTO;
import br.com.webpublico.tributario.ParcelaPixWrapperDTO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.dto.ConfiguracaoTributarioDTO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.ws.model.WSDivida;
import br.com.webpublico.ws.model.WSImovel;
import br.com.webpublico.ws.model.WsIPTU;
import br.com.webpublico.ws.model.WsIssqnFixo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@Controller
@RequestMapping("/integracao/tributario/consulta-debitos")
public class ConsultaDebitoRestController extends PortalRestController {

    private static final String CONTADOR_CONSULTA_DEBITOS_PESSOA = "Contador da Consulta de Débitos";
    private static final String CONTAR_CONSULTA_FILTRADA = "Contador de Consulta de Débitos Filtrada";
    private static final String GERAR_DAM = "Gerar Dam";
    private static final String GERAR_QRCODEPIX = "Gerar QrCode Pix";
    private static final String BUSCAR_DEBITOS_PESSOA = "Buscar Débitos Filtrando Por Pessoa";
    private static final String BUSCAR_DEBITOS_CPF = "Buscar Débitos Por Pessoa CPF";
    private static final String BUSCAR_DEBITOS_CADASTRO = "Buscar Débitos Por Cadastro";
    private static final String BUSCAR_DEBITOS_PESSOA_GERAL = "Buscar Débitos Por Pessoa Contribuinte Geral";
    private static final String GEAR_DEMONSTRATIVO = "Gerar Demonstrativo de Débitos";
    private static final String BUSCAR_DIVIDA = "Buscar Dívida";
    private static final String EMISSAO_IPTU = "Emissão IPTU";
    private static final String EMISSAO_IPTU_ANO = "Buscar Ano de Emisssão de IPTU";

    @RequestMapping(value = "/get-debitos-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> buscarDebitosPorCpfCnpj(@RequestParam(value = "cpf") String cpf,
                                                     @RequestParam(value = "first", required = false) Integer first,
                                                     @RequestParam(value = "max", required = false) Integer max) {
        try {
            sistemaService.setUsuarioAlternativo(cpf);
            singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CPF, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            FiltroConsultaDebitos filtro = new FiltroConsultaDebitos(cpf, first, max);
            filtro.setTipoConsulta(TipoCadastroTributario.PESSOA.name());
            List<ResultadoParcela> resultadoParcelas = getPortalContribunteFacade().buscarDebitosPortalWeb(filtro);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CPF, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            return ResponseEntity.ok().body(resultadoParcelas);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CPF, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        }
    }

    @RequestMapping(value = "/count-debitos-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarDebitosPorCpfCnpj(@RequestParam(value = "cpf", required = true) String cpf) {

        try {
            sistemaService.setUsuarioAlternativo(cpf);
            singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), CONTADOR_CONSULTA_DEBITOS_PESSOA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            Integer parcelas = getPortalContribunteFacade().contarDebitosPortalWeb(new FiltroConsultaDebitos(cpf));
            return new ResponseEntity<>(parcelas, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), CONTADOR_CONSULTA_DEBITOS_PESSOA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-dam",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> gerarDam(@RequestBody List<LinkedHashMap> param) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] bytes = new byte[0];
        List<ResultadoParcela> parcelas = getResultadoParcelas(param);
        try {
            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
            List<DAM> dams = getPortalContribunteFacade().gerarDAMPortalWeb(parcelas);
            bytes = getPortalContribunteFacade().getConsultaDebitoFacade().gerarImpressaoDAMPortal(dams).toByteArray();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-qrcodes-pix",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> gerarQrCodePix(@RequestBody ParcelaPixWrapperDTO parcelaPixWrapper) {
        try {
            singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), GERAR_QRCODEPIX, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            List<ParcelaPixDTO> parcelasPix = parcelaPixWrapper.getParcelasPix();
            parcelasPix = gerarDansPix(parcelasPix);
            parcelaPixWrapper.setParcelasPix(parcelasPix);
            return ResponseEntity.ok().body(parcelaPixWrapper);
        } catch (Exception e) {
            getLogger().error("Erro ao gerar qrCodePix portal contribunte. ", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } finally {
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), GERAR_QRCODEPIX, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        }
    }

    public List<ParcelaPixDTO> gerarDansPix(List<ParcelaPixDTO> parcelasPix) {
        List<DAM> dans = getPortalContribunteFacade().gerarDAMPixPortal(parcelasPix);
        List<ResultadoParcela> parcelas = montarParcelas(parcelasPix);
        Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());

        List<PixDTO> lotePix = getPortalContribunteFacade().getConsultaDebitoFacade().gerarQrCodePix(dans);

        for (PixDTO pix : lotePix) {
            for (ParcelaPixDTO parcelaPix : parcelasPix) {
                if (pix.getIdDam().equals(parcelaPix.getIdDam())) {
                    parcelaPix.setQrCodePix(pix.getQrCode());
                    break;
                }
            }
        }
        return parcelasPix;
    }

    private List<ResultadoParcela> montarParcelas(List<ParcelaPixDTO> parcelasPix) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ParcelaPixDTO parcelaPix : parcelasPix) {
            ResultadoParcela parcela = new ResultadoParcela();

            parcela.setIdParcela(parcelaPix.getIdParcela());
            parcela.setIdCadastro(parcelaPix.getIdCadastro());
            parcela.setIdPessoa(parcelaPix.getIdPessoa());
            parcela.setIdDivida(parcelaPix.getIdDivida());
            parcela.setOrdemApresentacao(parcelaPix.getOrdemApresentacao());
            parcela.setVencimento(parcelaPix.getVencimento());
            parcela.setTipoCalculo(parcelaPix.getTipoCalculo());

            parcelas.add(parcela);
        }
        Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
        return parcelas;
    }

    @RequestMapping(value = "/gerar-dam-base64",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarDamBase64(@RequestBody List<LinkedHashMap> param) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] bytes = new byte[0];
        List<ResultadoParcela> parcelas = getResultadoParcelas(param);
        try {
            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
            List<DAM> dams = getPortalContribunteFacade().gerarDAMPortalWeb(parcelas);
            bytes = getPortalContribunteFacade().getConsultaDebitoFacade().gerarImpressaoDAMPortal(dams).toByteArray();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        Base64 codec = new Base64();
        String encoded = codec.encodeBase64String(bytes);
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(encoded, HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-dam-agrupado-base64",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarDamAgrupadoBase64(@RequestBody List<LinkedHashMap> param) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] bytes = new byte[0];
        List<ResultadoParcela> parcelas = getResultadoParcelas(param);
        try {
            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
            bytes = getPortalContribunteFacade().getConsultaDebitoFacade().gerarImpressaoDAMAgrupado(getPortalContribunteFacade().gerarDAMAgrupadoPortalWeb(parcelas), HistoricoImpressaoDAM.TipoImpressao.PORTAL, parcelas, null, true).toByteArray();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        Base64 codec = new Base64();
        String encoded = codec.encodeBase64String(bytes);
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(encoded, HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-pix-base64",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarPixBase64(@RequestBody List<LinkedHashMap> param) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), GERAR_QRCODEPIX, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<ResultadoParcela> parcelas = getResultadoParcelas(param);
        String jsonPix = "";
        try {
            if (!parcelas.isEmpty()) {
                List<DAM> dans = getPortalContribunteFacade().gerarDAMPortalWeb(parcelas);

                if (dans != null && !dans.isEmpty()) {
                    DAM dam = dans.get(0);
                    List<PixDTO> lotePix = getPortalContribunteFacade().getConsultaDebitoFacade().gerarQrCodePix(Lists.newArrayList(dam));
                    String qrCode = "";
                    if (!lotePix.isEmpty()) {
                        qrCode = lotePix.get(0).getQrCode();
                    } else if (dam.getQrCodePIX() != null) {
                        qrCode = dam.getQrCodePIX();
                    }
                    String base64Pix = BarCode.generateBase64QRCodePng(qrCode, 350, 350);

                    jsonPix = new JSONObject()
                        .put("qrCode", qrCode)
                        .put("base64Pix", base64Pix)
                        .put("vencimento", DataUtil.getDataFormatada(dam.getVencimento()))
                        .toString();
                }
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), GERAR_QRCODEPIX, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(jsonPix, HttpStatus.OK);
    }

    @RequestMapping(
        value = "/verificar-dados-emissao-carne-iptu",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)

    @ResponseBody
    public ResponseEntity<WSImovel> verificarDadosEmissaoCarneIPTU(@RequestBody WsIPTU wsIPTU) {
        try {
            List<WSImovel> imoveis = getPortalContribunteFacade().buscarCadastroImobiliarioAtivoPorPessoaAndInscricaoCadastral(wsIPTU.getCpfCnpj(), wsIPTU.getMatriculaImovel());
            if (!imoveis.isEmpty()) {
                WSImovel imovel = imoveis.get(0);
                return new ResponseEntity<>(imovel, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Error: ", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-configuracao-tributario",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ConfiguracaoTributarioDTO> getConfiguracaoTributarioDTO() {
        try {
            ConfiguracaoTributario configuracaoTributario = getPortalContribunteFacade().getConfiguracaoTributarioFacade().retornaUltimo();
            if (configuracaoTributario != null) {
                ConfiguracaoTributarioDTO config = new ConfiguracaoTributarioDTO();
                config.setExercicioIptu(configuracaoTributario.getExercicioPortal() != null ? configuracaoTributario.getExercicioPortal().getAno() : null);
                config.setAtivaAlteracaoCadastral(configuracaoTributario.getAtivaAlteracaoCadastral());
                return new ResponseEntity<>(config, HttpStatus.OK);
            }
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/emitir-iptu",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> emitirIPTU(@RequestBody WsIPTU wsIPTU) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), EMISSAO_IPTU, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] bytes = new byte[0];
        try {
            bytes = getPortalContribunteFacade().gerarCarneIPTUPortalWeb(wsIPTU.getAno(), wsIPTU.getMatriculaImovel()).toByteArray();
        } catch (Exception e) {
            getLogger().error("Exception {}", e);
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), EMISSAO_IPTU, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-dams-emissao-carne", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> gerarNovosDAMsParaEmissaoCarne(@RequestBody WsIPTU wsIPTU) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        try {
            List<ResultadoParcela> parcelasVencidas = getPortalContribunteFacade().buscarDebitosIPTU(wsIPTU, true);
            Collections.sort(parcelasVencidas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
            getPortalContribunteFacade().gerarDAMPortalWeb(parcelasVencidas);
            singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), GERAR_DAM, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/quantidade-parcelas-vencidas",
        method = RequestMethod.GET,
        produces = "application/json;charset=UTF-8")
    @ResponseBody
    public ResponseEntity<Integer> buscarQuantidadeParcelasVencidas(@RequestParam(value = "parcelas") List<Long> parcelas) {
        Integer qtdParcelasVencidas = 0;
        try {
            List<DAM> dams = getPortalContribunteFacade().recuperarDamsPeloIdParcela(parcelas);
            if (dams != null) {
                qtdParcelasVencidas = parcelas.size() - dams.size();
                qtdParcelasVencidas = qtdParcelasVencidas.compareTo(0) >= 0 ? qtdParcelasVencidas : 0;
            }
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(qtdParcelasVencidas, HttpStatus.OK);
    }

    @RequestMapping(value = "/count-debitos-por-pessoa-filtrando",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarConsultaFiltrada(@RequestBody FiltroConsultaDebitos filtro) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), CONTAR_CONSULTA_FILTRADA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        Integer count = 0;
        try {
            count = getPortalContribunteFacade().contarDebitosPortalWeb(filtro);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), CONTAR_CONSULTA_FILTRADA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-debitos-por-pessoa-filtrando",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ResultadoParcela>> buscarConsultaFiltrada(@RequestBody FiltroConsultaDebitos filtro) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_PESSOA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.CONSULTA_DEBITO, filtro.getCpf());
        try {
            parcelas = getPortalContribunteFacade().buscarDebitosPortalWeb(filtro);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_PESSOA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(parcelas, HttpStatus.OK);
    }

    private List<ResultadoParcela> getResultadoParcelas(List<LinkedHashMap> param) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (LinkedHashMap lhm : param) {
            parcelas.add(traduzirHasMapParaResultadoParcela(lhm));
        }
        return parcelas;
    }

    @RequestMapping(value = "/gerar-demonstrativo",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> gerarDemonstrativo(@RequestBody FiltroConsultaDebitos filtro) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), GEAR_DEMONSTRATIVO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        byte[] bytes = new byte[0];
        try {
            sistemaService.setUsuarioAlternativo(filtro.getCpf());
            List<ResultadoParcela> parcelas = getPortalContribunteFacade().buscarDebitosPortalWeb(filtro);
            bytes = getPortalContribunteFacade().gerarImpressaoDemonstrativo(parcelas).toByteArray();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), GEAR_DEMONSTRATIVO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-dividas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSDivida>> buscarDividas(@RequestParam(value = "cpf", required = true) String cpf) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DIVIDA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<WSDivida> dividas = Lists.newArrayList();
        try {
            sistemaService.setUsuarioAlternativo(cpf);
            dividas = getPortalContribunteFacade().buscarDividasPortalWeb(cpf);
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DIVIDA, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(dividas, HttpStatus.OK);
    }

    private ResultadoParcela traduzirHasMapParaResultadoParcela(LinkedHashMap lhm) {
        if (sistemaService.getUsuarioAlternativo() == null) {
            Pessoa pessoa = getPortalContribunteFacade().recuperar(((Number) lhm.get("idPessoa")).longValue());
            sistemaService.setUsuarioAlternativo(pessoa.getCpf_Cnpj());
        }
        ResultadoParcela rp = new ResultadoParcela();
        rp.setIdPessoa(((Number) lhm.get("idPessoa")).longValue());
        rp.setIdCadastro(lhm.get("idCadastro") != null ? ((Number) lhm.get("idCadastro")).longValue() : null);
        rp.setIdParcela(((Number) lhm.get("idParcela")).longValue());
        rp.setIdDivida(((Number) lhm.get("idDivida")).longValue());
        rp.setOrdemApresentacao((Integer) lhm.get("ordemApresentacao"));
        rp.setVencimento(new Date((Long) lhm.get("vencimento")));
        rp.setTipoCalculo(lhm.get("tipoCalculoEnumValue") != null ? (String) lhm.get("tipoCalculoEnumValue") : null);
        rp.setIdCalculo(lhm.get("idCalculo") != null ? ((Number) lhm.get("idCalculo")).longValue() : null);
        rp.setReferencia(lhm.get("referencia") != null ? (String) lhm.get("referencia") : null);
        rp.setSd(lhm.get("sd") != null ? (Integer) lhm.get("sd") : null);
        rp.setValorImposto(lhm.get("valorImposto") != null ? BigDecimal.valueOf(((Number) lhm.get("valorImposto")).doubleValue()) : null);
        rp.setValorJuros(lhm.get("valorJuros") != null ? BigDecimal.valueOf(((Number) lhm.get("valorJuros")).doubleValue()) : null);
        rp.setValorMulta(lhm.get("valorMulta") != null ? BigDecimal.valueOf(((Number) lhm.get("valorMulta")).doubleValue()) : null);
        rp.setValorOriginal(lhm.get("valorOriginal") != null ? BigDecimal.valueOf(((Number) lhm.get("valorOriginal")).doubleValue()) : null);
        rp.setValorPago(lhm.get("valorPago") != null ? BigDecimal.valueOf(((Number) lhm.get("valorPago")).doubleValue()) : null);
        rp.setValorTaxa(lhm.get("valorTaxa") != null ? BigDecimal.valueOf(((Number) lhm.get("valorTaxa")).doubleValue()) : null);
        rp.setValorCorrecao(lhm.get("valorCorrecao") != null ? BigDecimal.valueOf(((Number) lhm.get("valorCorrecao")).doubleValue()) : null);
        rp.setValorHonorarios(lhm.get("valorHonorarios") != null ? BigDecimal.valueOf(((Number) lhm.get("valorHonorarios")).doubleValue()) : null);
        rp.setValorDesconto(lhm.get("valorDesconto") != null ? BigDecimal.valueOf(((Number) lhm.get("valorDesconto")).doubleValue()) : null);
        rp.setCadastro(lhm.get("cadastro") != null ? (String) lhm.get("cadastro") : null);
        rp.setEnderecoCadastro(lhm.get("enderecoCadastro") != null ? (String) lhm.get("enderecoCadastro") : null);
        rp.setDivida(lhm.get("divida") != null ? (String) lhm.get("divida") : null);
        rp.setParcela(lhm.get("parcela") != null ? (String) lhm.get("parcela") : null);


        return rp;
    }


    @RequestMapping(value = "/get-debitos-por-cadastro-filtrando",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ResultadoParcela>> buscarConsultaPorCadastroFiltrada(@RequestBody FiltroConsultaDebitos filtro) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CADASTRO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        try {
            parcelas = getPortalContribunteFacade().buscarDebitosPortalWebPorCadastro(filtro);
            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CADASTRO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(parcelas, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-debitos-por-pessoa-contribuinte-geral",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ResultadoParcela>> buscarConsultaPorPessoaContribuinteGeral(@RequestBody FiltroConsultaDebitos filtro) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_PESSOA_GERAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        try {
            parcelas = getPortalContribunteFacade().buscarDebitosPortalWebPorPessoa(filtro);
            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_PESSOA_GERAL, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(parcelas, HttpStatus.OK);
    }

    @RequestMapping(value = "/consultar-debitos-emissao-iptu", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ResultadoParcela>> consultaDebitosIPTU(@RequestBody WsIPTU wsIPTU) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CADASTRO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.CONSULTA_DEBITO, wsIPTU.getCpfCnpj());
        try {
            parcelas = getPortalContribunteFacade().buscarDebitosIPTU(wsIPTU, false);
            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CADASTRO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(parcelas, HttpStatus.OK);
    }

    @RequestMapping(value = "/consultar-debitos-emissao-issqn-fixo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<ResultadoParcela>> consultaDebitosIssqnFixo(@RequestBody WsIssqnFixo wsISSQNFixo) {
        singletonMetricas.iniciarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CADASTRO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.CONSULTA_DEBITO, wsISSQNFixo.getCpfCnpj());
        try {
            parcelas = getPortalContribunteFacade().buscarDebitosISSQNFixo(wsISSQNFixo);
            Collections.sort(parcelas, ResultadoParcela.getComparatorByValuePadraoSemCadastro());
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        singletonMetricas.finalizarMetrica(SistemaFacade.obtemLogin(), BUSCAR_DEBITOS_CADASTRO, System.currentTimeMillis(), MetricaSistema.Tipo.ACESSO_PORTAL);
        return new ResponseEntity<>(parcelas, HttpStatus.OK);
    }

    @RequestMapping(value = "/consultar-permissao-emissao-carne-iptu", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> consultaDebitosIPTU() {
        boolean permiteEmitir = false;
        try {
            permiteEmitir = getPortalContribunteFacade().permiteEmitirCarneIPTU();
        } catch (Exception e) {
            getLogger().error(e.getMessage());
        }
        return new ResponseEntity<>(permiteEmitir, HttpStatus.OK);
    }

}
