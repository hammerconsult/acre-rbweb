package br.com.webpublico.dashboard.rest;


import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidades.MovimentoEstoque;
import br.com.webpublico.entidades.comum.dashboard.ConfiguracaoDeDashboard;
import br.com.webpublico.negocios.GrupoMaterialFacade;
import br.com.webpublico.negocios.LocalEstoqueFacade;
import br.com.webpublico.negocios.MovimentoEstoqueFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.dashboard.ConfiguracaoDeDashboardFacade;
import br.com.webpublico.webboard.webboarddto.administrativo.*;
import br.com.webpublico.webboard.webboarddto.comum.TipoDashboard;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.jfree.data.time.Year;
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
 * Created by renato on 08/08/19.
 */
@RequestMapping("/dashboard-administrativo")
@Controller
public class DashboardAdministrativoResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ConfiguracaoDeDashboardFacade configuracaoDeDashboardFacade;
    private SistemaFacade sistemaFacade;
    private LocalEstoqueFacade localEstoqueFacade;
    private GrupoMaterialFacade grupoMaterialFacade;


    @PostConstruct
    public void init() {
        try {
            configuracaoDeDashboardFacade = (ConfiguracaoDeDashboardFacade) new InitialContext().lookup
                ("java:module/ConfiguracaoDeDashboardFacade");
            sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
            grupoMaterialFacade = (GrupoMaterialFacade) new InitialContext().lookup("java:module/GrupoMaterialFacade");
            localEstoqueFacade = (LocalEstoqueFacade) new InitialContext().lookup("java:module/LocalEstoqueFacade");

        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/protocolo-gerado", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardProtocoloGerado() throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ProtocoloDTO dto = new ProtocoloDTO();
                dto.setTipoDashboard(TipoDashboard.LINE);

                HttpEntity<ProtocoloDTO> request = new HttpEntity<ProtocoloDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                    .getUrlCompleta("dashboard-administrativo/protocolo-gerado"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/protocolo-arquivado", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardProtocoloArquivado() throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ProtocoloDTO dto = new ProtocoloDTO();
                dto.setTipoDashboard(TipoDashboard.LINE);

                HttpEntity<ProtocoloDTO> request = new HttpEntity<ProtocoloDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                    .getUrlCompleta("dashboard-administrativo/protocolo-arquivado"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/protocolo-finalizado", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardProtocoloFinalizado() throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ProtocoloDTO dto = new ProtocoloDTO();

                HttpEntity<ProtocoloDTO> request = new HttpEntity<ProtocoloDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                    .getUrlCompleta("dashboard-administrativo/protocolo-finalizado"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/protocolo-por-situacao", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardPorSituacao(@RequestParam(value = "porcentagem") Boolean porcentagem) throws UnsupportedEncodingException {
        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ProtocoloDTO dto = new ProtocoloDTO();
                dto.setAno(sistemaFacade.getExercicioCorrente().getAno());
                dto.setTipoDashboard(TipoDashboard.PIE);
                dto.setPorcentagem(porcentagem);

                HttpEntity<ProtocoloDTO> request = new HttpEntity<ProtocoloDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                    .getUrlCompleta("dashboard-administrativo/protocolo-por-situacao"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @RequestMapping(value = "/obras-por-subtipo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardObraPorSubTipo(@RequestParam(value = "porcentagem") Boolean porcentagem) throws UnsupportedEncodingException {

        try {

            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ObraDTO dto = new ObraDTO();
                dto.setTipoDashboard(TipoDashboard.PIE);
                dto.setPorcentagem(porcentagem);

                HttpEntity<ObraDTO> request = new HttpEntity<ObraDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-administrativo/obras-por-subtipo"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/bensmoveis-aquisicao-incorporacao", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardAquisicaoEIncorporacao() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                BensMoveisDTO dto = new BensMoveisDTO();
                dto.setTipoDashboard(TipoDashboard.BARRA);

                HttpEntity<BensMoveisDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                    .getUrlCompleta("dashboard-administrativo/bensmoveis-aquisicao-incorporacao"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/bensmoveis-cedidos", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardCedidos() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                BensMoveisDTO dto = new BensMoveisDTO();
                dto.setTipoDashboard(TipoDashboard.HORIZONTAL_BARRA);

                HttpEntity<BensMoveisDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                    .getUrlCompleta("dashboard-administrativo/bensmoveis-cedidos"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/bensmoveis-estados", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardEstados() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                BensMoveisDTO dto = new BensMoveisDTO();
                dto.setTipoDashboard(TipoDashboard.HORIZONTAL_BARRA);

                HttpEntity<BensMoveisDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/bensmoveis-estados"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/bensmoveis-conservacao", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardConservacao() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                BensMoveisDTO dto = new BensMoveisDTO();
                dto.setTipoDashboard(TipoDashboard.PIE);

                HttpEntity<BensMoveisDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/bensmoveis-conservacao"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/contrato-por-compra-servico", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardCompraServico() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ContratoLicitacaoDTO dto = new ContratoLicitacaoDTO();
                dto.setAno(sistemaFacade.getExercicioCorrente().getAno());
                HttpEntity<ContratoLicitacaoDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/contrato-por-compra-servico"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }


            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/contrato-por-obra-eng", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardObraEng() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ContratoLicitacaoDTO dto = new ContratoLicitacaoDTO();
                dto.setAno(sistemaFacade.getExercicioCorrente().getAno());
                HttpEntity<ContratoLicitacaoDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/contrato-por-obra-eng"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/licitacao-por-obra-eng", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardLicitaoObraEng() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ContratoLicitacaoDTO dto = new ContratoLicitacaoDTO();
                dto.setAno(sistemaFacade.getExercicioCorrente().getAno());
                HttpEntity<ContratoLicitacaoDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/licitacao-por-obra-eng"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/licitacao-por-compra-servico", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardLicitacaoCompraServico() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ContratoLicitacaoDTO dto = new ContratoLicitacaoDTO();
                dto.setAno(sistemaFacade.getExercicioCorrente().getAno());
                HttpEntity<ContratoLicitacaoDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/licitacao-por-compra-servico"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/total-licitacao", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> valorTotalLicitacao() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ContratoLicitacaoDTO dto = new ContratoLicitacaoDTO();
                dto.setAno(sistemaFacade.getExercicioCorrente().getAno());
                HttpEntity<ContratoLicitacaoDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/total-licitacao"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/total-contrato", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> valorTotalContrato() throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                ContratoLicitacaoDTO dto = new ContratoLicitacaoDTO();
                dto.setAno(sistemaFacade.getExercicioCorrente().getAno());
                HttpEntity<ContratoLicitacaoDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config
                        .getUrlCompleta("dashboard-administrativo/total-contrato"),
                    HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/grupo-material-adquirido", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardGrupoMaterialAdquirido(@RequestParam(value = "porcentagem") Boolean porcentagem,
                                                                  @RequestParam(value = "tipoDash") String tipoDash,
                                                                  @RequestParam(value = "arrayExercicios") List<Long> arrayExercicios,
                                                                  @RequestParam(value = "arrayGrupoMaterial") List<Long> arrayGrupoMaterial,
                                                                  @RequestParam(value = "arrayLocalEstoque") List<Long> arrayLocalEstoque) throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                GrupoMaterialRealizadoDTO dto = new GrupoMaterialRealizadoDTO();
                dto.setPorcentagem(porcentagem);
                dto.setGruposMaterias(montarGrupoMatereial(arrayGrupoMaterial));
                dto.setLocaisEstoques(montarLocalEstoque(arrayLocalEstoque));
                dto.setExercicios(montarExercicios(arrayExercicios));


                if (!Strings.isNullOrEmpty(tipoDash)) {
                    for (TipoDashboard tipoDashboard : TipoDashboard.values()) {
                        if (tipoDashboard.getDescricao().equals(tipoDash)) {
                            dto.setTipoDashboard(tipoDashboard);
                        }
                    }
                }
                HttpEntity<GrupoMaterialRealizadoDTO> request = new HttpEntity<GrupoMaterialRealizadoDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-administrativo/grupo-material-adquirido"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/grupo-material-consumido", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardGrupoMaterialConsumido(@RequestParam(value = "porcentagem") Boolean porcentagem,
                                                                  @RequestParam(value = "tipoDash") String tipoDash,
                                                                  @RequestParam(value = "arrayExercicios") List<Long> arrayExercicios,
                                                                  @RequestParam(value = "arrayGrupoMaterial") List<Long> arrayGrupoMaterial,
                                                                  @RequestParam(value = "arrayLocalEstoque") List<Long> arrayLocalEstoque) throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                GrupoMaterialRealizadoDTO dto = new GrupoMaterialRealizadoDTO();
                dto.setPorcentagem(porcentagem);
                dto.setGruposMaterias(montarGrupoMatereial(arrayGrupoMaterial));
                dto.setLocaisEstoques(montarLocalEstoque(arrayLocalEstoque));
                dto.setExercicios(montarExercicios(arrayExercicios));


                if (!Strings.isNullOrEmpty(tipoDash)) {
                    for (TipoDashboard tipoDashboard : TipoDashboard.values()) {
                        if (tipoDashboard.getDescricao().equals(tipoDash)) {
                            dto.setTipoDashboard(tipoDashboard);
                        }
                    }
                }
                HttpEntity<GrupoMaterialRealizadoDTO> request = new HttpEntity<GrupoMaterialRealizadoDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-administrativo/grupo-material-consumido"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/grupo-material-disponivel", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashboardGrupoMaterialDisponivel(@RequestParam(value = "porcentagem") Boolean porcentagem,
                                                                   @RequestParam(value = "tipoDash") String tipoDash,
                                                                   @RequestParam(value = "arrayExercicios") List<Long> arrayExercicios,
                                                                   @RequestParam(value = "arrayGrupoMaterial") List<Long> arrayGrupoMaterial,
                                                                   @RequestParam(value = "arrayLocalEstoque") List<Long> arrayLocalEstoque) throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                GrupoMaterialRealizadoDTO dto = new GrupoMaterialRealizadoDTO();
                dto.setPorcentagem(porcentagem);
                dto.setGruposMaterias(montarGrupoMatereial(arrayGrupoMaterial));
                dto.setLocaisEstoques(montarLocalEstoque(arrayLocalEstoque));
                dto.setExercicios(montarExercicios(arrayExercicios));


                if (!Strings.isNullOrEmpty(tipoDash)) {
                    for (TipoDashboard tipoDashboard : TipoDashboard.values()) {
                        if (tipoDashboard.getDescricao().equals(tipoDash)) {
                            dto.setTipoDashboard(tipoDashboard);
                        }
                    }
                }
                HttpEntity<GrupoMaterialRealizadoDTO> request = new HttpEntity<GrupoMaterialRealizadoDTO>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-administrativo/grupo-material-disponivel"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private List<GrupoMaterialPorExcercicioDTO> montarExercicios(List<Long> arrayExercicios) {
        List<GrupoMaterialPorExcercicioDTO> retorno = Lists.newArrayList();
        for (Long idExercicio : arrayExercicios) {
            Exercicio exercicio = grupoMaterialFacade.getExercicioFacade().recuperar(idExercicio);
            GrupoMaterialPorExcercicioDTO dto = new GrupoMaterialPorExcercicioDTO();
            dto.setId(exercicio.getId());
            dto.setAno(exercicio.getAno());
            retorno.add(dto);
        }
        return retorno;
    }

    private List<LocalEstoqueDTO> montarLocalEstoque(List<Long> arrayLocalEstoque) {
        List<LocalEstoqueDTO> retorno = Lists.newArrayList();
        for (Long idLocalEstoque : arrayLocalEstoque) {
            LocalEstoque localEstoque = (LocalEstoque) localEstoqueFacade.recuperar(LocalEstoque.class, idLocalEstoque);
            LocalEstoqueDTO dto = new LocalEstoqueDTO();
            dto.setDescricao(localEstoque.getDescricao());
            dto.setCodigo(localEstoque.getCodigo());
            dto.setId(localEstoque.getId());
            retorno.add(dto);
        }
        return retorno;
    }

    public List<GrupoMaterialDTO> montarGrupoMatereial(List<Long> arrayGrupoMaterial) {
        List<GrupoMaterial> gruposMaterias = Lists.newArrayList();
        for (Long idGrupoMaterial : arrayGrupoMaterial) {
            GrupoMaterial grupoMaterial = grupoMaterialFacade.recuperar(idGrupoMaterial);
            gruposMaterias.add(grupoMaterial);
        }
        List<GrupoMaterialDTO> grupoMaterialDTOS = new ArrayList<>();
        for (GrupoMaterial grupoMaterial : gruposMaterias) {
            GrupoMaterialDTO grupoMaterialDTO = new GrupoMaterialDTO();
            grupoMaterialDTO.setDescricao(grupoMaterial.getDescricao());
            grupoMaterialDTO.setCodigo(grupoMaterial.getCodigo());
            grupoMaterialDTO.setId(grupoMaterial.getId());
            grupoMaterialDTOS.add(grupoMaterialDTO);
        }
        return grupoMaterialDTOS;
    }

    @RequestMapping(value = "/grupobem-hierarquia", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardGrupoBemPorHierarquia(@RequestParam(value = "quantidadeMax") Integer quantidadeMax) throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                GrupoBemDTO dto = new GrupoBemDTO();
                dto.setTipoDashboard(TipoDashboard.HORIZONTAL_BARRA);
                dto.setQuantidadeMax(quantidadeMax);
                HttpEntity<GrupoBemDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-administrativo/grupobem-hierarquia"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/baixa-de-bens", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> dashBoardBensBaixadosPorHierarquia(@RequestParam(value = "quantidadeMax") Integer quantidadeMax) throws UnsupportedEncodingException {
        try {
            ConfiguracaoDeDashboard config = configuracaoDeDashboardFacade.getConfiguracaoPorChave();
            if (config != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                BaixaDeBensDTO dto = new BaixaDeBensDTO();
                dto.setTipoDashboard(TipoDashboard.HORIZONTAL_BARRA);
                dto.setQuantidadeMax(quantidadeMax);
                HttpEntity<BaixaDeBensDTO> request = new HttpEntity<>(dto, headers);
                ResponseEntity<String> responseEntity = new RestTemplate().exchange(config.getUrlCompleta("dashboard-administrativo/baixa-de-bens"), HttpMethod.POST, request, String.class);
                if (!responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                    return new ResponseEntity<>(new JSONObject(responseEntity.getBody()).toString(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (JSONException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
