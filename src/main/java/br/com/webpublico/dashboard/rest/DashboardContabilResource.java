package br.com.webpublico.dashboard.rest;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.dashboard.ConfiguracaoDeDashboard;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.dashboard.ConfiguracaoDeDashboardFacade;
import br.com.webpublico.webboard.webboarddto.comum.HierarquiaDTO;
import br.com.webpublico.seguranca.service.SistemaService;
import br.com.webpublico.webboard.webboarddto.comum.TipoDashboard;
import br.com.webpublico.webboard.webboarddto.contabil.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by renato on 21/08/19.
 */
@RequestMapping("/dashboard-contabil")
@Controller
public class DashboardContabilResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ConfiguracaoDeDashboardFacade configuracaoDeDashboardFacade;
    private SistemaFacade sistemaFacade;
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ContaFacade contaFacade;

    @PostConstruct
    public void init() {
        try {
            configuracaoDeDashboardFacade = (ConfiguracaoDeDashboardFacade) new InitialContext().lookup("java:module/ConfiguracaoDeDashboardFacade");
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
            fonteDeRecursosFacade = (FonteDeRecursosFacade) new InitialContext().lookup("java:module/FonteDeRecursosFacade");
            hierarquiaOrganizacionalFacade = (HierarquiaOrganizacionalFacade) new InitialContext().lookup("java:module/HierarquiaOrganizacionalFacade");
            contaFacade = (ContaFacade) new InitialContext().lookup("java:module/ContaFacade");
        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/receita-realizada", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardReceitaRealizada(@RequestParam(value = "ano") Integer ano,
                                                            @RequestParam(value = "tipo") String tipo) throws UnsupportedEncodingException {
        try {
            if (ano != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    ReceitaRealizadaDTO dto = new ReceitaRealizadaDTO();
                    dto.setAno(ano);
                    dto.setTipoDashboard(TipoDashboard.getPorDescricao(tipo));

                    HttpEntity<ReceitaRealizadaDTO> request = new HttpEntity<ReceitaRealizadaDTO>(dto, headers);
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/receita-realizada"), HttpMethod.POST, request, String.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/receita-por-fonte", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardPorModalidadeContrato(@RequestParam(value = "porcentagem") Boolean porcentagem,
                                                                 @RequestParam(value = "agruparOrgaoFonte") Boolean agruparOrgaoFonte,
                                                                 @RequestParam(value = "tipoDash") String tipoDash,
                                                                 @RequestParam(value = "arrayExercicios") List<Long> arrayExercicios,
                                                                 @RequestParam(value = "arrayFontes") List<Long> arrayFontes,
                                                                 @RequestParam(value = "arraySecretarias") List<Long> arraySecretarias) throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ReceitaRealizadaPorFonteDTO dto = new ReceitaRealizadaPorFonteDTO();
                dto.setPorcentagem(porcentagem);
                dto.setFontes(montarFontes(arrayFontes));
                dto.setHierarquias(montarUnidades(arraySecretarias));
                dto.setExercicios(montarExercicios(arrayExercicios));
                dto.setAgruparOrgaoFonte(agruparOrgaoFonte);

                if (!Strings.isNullOrEmpty(tipoDash)) {
                    for (TipoDashboard tipoDashboard : TipoDashboard.values()) {
                        if (tipoDashboard.getDescricao().equals(tipoDash)) {
                            dto.setTipoDashboard(tipoDashboard);
                        }
                    }
                }
                HttpEntity<ReceitaRealizadaPorFonteDTO> request = new HttpEntity<ReceitaRealizadaPorFonteDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/receita-por-fonte"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private List<ReceitaPorExercicioDTO> montarExercicios(List<Long> arrayExercicios) {
        List<ReceitaPorExercicioDTO> retorno = Lists.newArrayList();
        for (Long idExercicio : arrayExercicios) {
            Exercicio exercicio = fonteDeRecursosFacade.getExercicioFacade().recuperar(idExercicio);
            ReceitaPorExercicioDTO dto = new ReceitaPorExercicioDTO();
            dto.setId(exercicio.getId());
            dto.setAno(exercicio.getAno());
            retorno.add(dto);
        }
        return retorno;
    }

    private List<HierarquiaDTO> montarUnidades(List<Long> arrayUnidades) {
        List<HierarquiaDTO> retorno = Lists.newArrayList();
        for (Long idUnidade : arrayUnidades) {
            HierarquiaOrganizacional ho = (HierarquiaOrganizacional) hierarquiaOrganizacionalFacade.recuperar(HierarquiaOrganizacional.class, idUnidade);
            HierarquiaDTO dto = new HierarquiaDTO();
            dto.setDescricao(ho.getDescricao());
            dto.setCodigo(ho.getCodigo());
            dto.setId(ho.getId());
            retorno.add(dto);
        }
        return retorno;
    }

    public List<ReceitaPorFonteDTO> montarFontes(List<Long> arrayFontes) {
        List<FonteDeRecursos> fontes = Lists.newArrayList();
        for (Long idFonte : arrayFontes) {
            FonteDeRecursos fonteDeRecursos = fonteDeRecursosFacade.recuperar(idFonte);
            fontes.add(fonteDeRecursos);
        }
        List<ReceitaPorFonteDTO> receitaRealizadaPorFonteDTOS = new ArrayList<>();
        for (FonteDeRecursos fonteDeRecursos : fontes) {
            ReceitaPorFonteDTO fonteDTO = new ReceitaPorFonteDTO();
            fonteDTO.setDescricao(fonteDeRecursos.getDescricao());
            fonteDTO.setAno(fonteDeRecursos.getExercicio().getAno());
            fonteDTO.setId(fonteDeRecursos.getId());
            fonteDTO.setCodigo(fonteDeRecursos.getCodigo());
            receitaRealizadaPorFonteDTOS.add(fonteDTO);
        }
        return receitaRealizadaPorFonteDTOS;
    }


    @RequestMapping(value = "/execucao-orcamentaria", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardExecucaoOrcamentaria(@RequestParam(value = "ano") Integer ano,
                                                                @RequestParam(value = "tipo") String tipo) throws UnsupportedEncodingException {
        try {
            if (ano != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    ExecucaoOrcamentariaDTO dto = new ExecucaoOrcamentariaDTO();
                    dto.setAno(ano);
                    dto.setTipoDashboard(TipoDashboard.getPorDescricao(tipo));

                    HttpEntity<ExecucaoOrcamentariaDTO> request = new HttpEntity<ExecucaoOrcamentariaDTO>(dto, headers);
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/execucao-orcamentaria"), HttpMethod.POST, request, String.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/diaria", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardDiaria(@RequestParam(value = "ano") Integer ano,
                                                  @RequestParam(value = "tipo") String tipo) throws UnsupportedEncodingException {
        try {
            if (ano != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    DiariaDTO dto = new DiariaDTO();
                    dto.setAno(ano);
                    dto.setTipoDashboard(TipoDashboard.getPorDescricao(tipo));

                    HttpEntity<DiariaDTO> request = new HttpEntity<DiariaDTO>(dto, headers);
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/diaria"), HttpMethod.POST, request, String.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/destinacao-recurso", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardDestinacaoRecurso(@RequestParam(value = "ano") Integer ano,
                                                             @RequestParam(value = "mes") Integer mes) throws UnsupportedEncodingException {
        try {
            if (ano != null) {
                ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
                if (config != null) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    DestinacaoDeRecursoDTO dto = new DestinacaoDeRecursoDTO();
                    dto.setAno(ano);
                    dto.setMes(mes);
                    dto.setTipoDashboard(TipoDashboard.PIE);

                    HttpEntity<DestinacaoDeRecursoDTO> request = new HttpEntity<DestinacaoDeRecursoDTO>(dto, headers);
                    ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/destinacao-recurso"), HttpMethod.POST, request, String.class);
                    if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                        return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                    }
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/orcamento-geral", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardPorSomaDeCreditos() throws UnsupportedEncodingException {

        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                OrcamentoGeralDTO dto = new OrcamentoGeralDTO();

                HttpEntity<OrcamentoGeralDTO> request = new HttpEntity<OrcamentoGeralDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/orcamento-geral"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/despesa-total", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardDespesasTotal() throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(config.getUrlCompleta("dashboard-contabil/despesa-total"), HttpMethod.POST, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/receita-despesa", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardReceitaEDespesa() throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(config.getUrlCompleta("dashboard-contabil/receita-despesa"), HttpMethod.POST, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/orcamento-geral-exercicio", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardOrcamentoGeralExercicio() throws UnsupportedEncodingException {

        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                OrcamentoGeralDTO dto = new OrcamentoGeralDTO();
                dto.setAno(SistemaService.getInstance().getExercicioCorrente().getAno());
                HttpEntity<OrcamentoGeralDTO> request = new HttpEntity<OrcamentoGeralDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/orcamento-geral-exercicio"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/comparativo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardComparativo() throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ComparativoDTO dto = new ComparativoDTO();
                dto.setTipoDashboard(TipoDashboard.PIE);
                dto.setAno(SistemaService.getInstance().getExercicioCorrente().getAno());
                HttpEntity<ComparativoDTO> request = new HttpEntity<ComparativoDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/comparativo"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/composicao-orcamento", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardComposicaoOrcamento() throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ComposicaoOrcamentoDTO dto = new ComposicaoOrcamentoDTO();
                dto.setTipoDashboard(TipoDashboard.PIE);
                dto.setAno(SistemaService.getInstance().getExercicioCorrente().getAno());
                HttpEntity<ComposicaoOrcamentoDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/composicao-orcamento"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/despesa-capital-corrente", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardDespesaCapitalCorrente() throws UnsupportedEncodingException {

        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                DespesaCapitalECorrenteDTO dto = new DespesaCapitalECorrenteDTO();
                dto.setTipoDashboard(TipoDashboard.BARRA);
                HttpEntity<DespesaCapitalECorrenteDTO> request = new HttpEntity<DespesaCapitalECorrenteDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/despesa-capital-corrente"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/despesa-categoria", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardDespesaCategoria(@RequestParam(value = "tipoDash") String tipoDash,
                                                            @RequestParam(value = "porcentagem") Boolean porcentagem,
                                                            @RequestParam(value = "arraySecretarias") List<Long> arraySecretarias,
                                                            @RequestParam(value = "arrayExercicios") List<Long> arrayExercicios,
                                                            @RequestParam(value = "arrayContas") List<Long> arrayContas) throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ContaPorCapitalECorrenteDTO dto = new ContaPorCapitalECorrenteDTO();
                dto.setPorcentagem(porcentagem);
                dto.setHierarquias(montarUnidades(arraySecretarias));
                dto.setExercicios(montarExerciciosDespesa(arrayExercicios));
                dto.setContas(montarContaPorCategoria(arrayContas));

                if (!Strings.isNullOrEmpty(tipoDash)) {
                    for (TipoDashboard tipoDashboard : TipoDashboard.values()) {
                        if (tipoDashboard.getDescricao().equals(tipoDash)) {
                            dto.setTipoDashboard(tipoDashboard);
                        }
                    }
                }
                HttpEntity<ContaPorCapitalECorrenteDTO> request = new HttpEntity<ContaPorCapitalECorrenteDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-contabil/conta-por-despesa"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }

            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    private List<ContaPorExercicioDTO> montarExerciciosDespesa(List<Long> arrayExercicios) {
        List<ContaPorExercicioDTO> retorno = Lists.newArrayList();
        for (Long idExercicio : arrayExercicios) {
            Exercicio exercicio = contaFacade.getExercicioFacade().recuperar(idExercicio);
            ContaPorExercicioDTO dto = new ContaPorExercicioDTO();
            dto.setId(exercicio.getId());
            dto.setAno(exercicio.getAno());
            retorno.add(dto);
        }
        return retorno;
    }

    private List<ContaPorCategoriaDTO> montarContaPorCategoria(List<Long> arrayContas) {
        List<Conta> contas = Lists.newArrayList();
        for (Long idConta : arrayContas) {
            Conta conta =
                contaFacade.recuperar(idConta);
            contas.add(conta);
        }
        List<ContaPorCategoriaDTO> contaPorCategoriaDTOS = new ArrayList<>();
        for (Conta conta : contas) {
            ContaPorCategoriaDTO contaPorCategoriaDTO = new ContaPorCategoriaDTO();
            contaPorCategoriaDTO.setId(conta.getId());
            contaPorCategoriaDTO.setDescricao(conta.getDescricao());
            contaPorCategoriaDTO.setCodigo(conta.getCodigo());
            contaPorCategoriaDTOS.add(contaPorCategoriaDTO);
        }
        return contaPorCategoriaDTOS;
    }

}
