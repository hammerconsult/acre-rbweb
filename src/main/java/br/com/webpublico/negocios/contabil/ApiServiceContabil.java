package br.com.webpublico.negocios.contabil;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ApiServiceContabil {

    protected static final Logger logger = LoggerFactory.getLogger(ApiServiceContabil.class);

    public final static String HEADER_API = "webpublico-error";

    private static final String PARAMETER_KEY = "CONTABILSERVICE-API-KEY";
    private static final String PARAMETER_SECRET = "CONTABILSERVICE-API-SECRET";
    private static final String URL_CONTABIL_SERVICE = "URL_CONTABIL_SERVICE";
    private static final String DEFAULT_URL_CONTABIL_SERVICE = "http://localhost:8081";
    private static final String DEFAULT_KEY = "bed49a31-926f-4d59-b3bd-54a67c91b387";
    private static final String DEFAULT_SECRET = "api-contabil-service";
    private static final String url = Strings.isNullOrEmpty(System.getenv(URL_CONTABIL_SERVICE)) ? DEFAULT_URL_CONTABIL_SERVICE : System.getenv(URL_CONTABIL_SERVICE);
    private static final String API_KEY_CONTABIL = Strings.isNullOrEmpty(System.getenv(PARAMETER_KEY)) ? DEFAULT_KEY : System.getenv(PARAMETER_KEY);
    private static final String API_SECRET_CONTABIL = Strings.isNullOrEmpty(System.getenv(PARAMETER_SECRET)) ? DEFAULT_SECRET : System.getenv(PARAMETER_SECRET);

    public static ApiServiceContabil getService() {
        return (ApiServiceContabil) Util.getSpringBeanPeloNome("apiServiceContabil");
    }

    private static @NotNull HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(PARAMETER_KEY, API_KEY_CONTABIL);
        headers.add(PARAMETER_SECRET, API_SECRET_CONTABIL);
        return headers;
    }

    public ParametroEventoDTO contabilizar(ParametroEventoDTO parametroEventoDTO) {

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = getHttpHeaders();

            HttpEntity<ParametroEventoDTO> entity = new HttpEntity<ParametroEventoDTO>(parametroEventoDTO, headers);
            ResponseEntity<ParametroEventoDTO> response = restTemplate.exchange(url + "/api/contabilizar", HttpMethod.POST, entity, ParametroEventoDTO.class);
            return (ParametroEventoDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de contabilização. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de contabilização. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de contabilização. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public SaldoSubContaDTO gerarSaldoSubConta(SaldoSubContaDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoSubContaDTO> entity = new HttpEntity<SaldoSubContaDTO>(dto, headers);
            ResponseEntity<SaldoSubContaDTO> response = restTemplate.exchange(url + "/api/saldo-subconta", HttpMethod.POST, entity, SaldoSubContaDTO.class);
            return (SaldoSubContaDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo financeiro. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo financeiro. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo financeiro. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public SaldoFonteDespesaORCDTO gerarSaldoOrcamentario(SaldoFonteDespesaORCDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoFonteDespesaORCDTO> entity = new HttpEntity<SaldoFonteDespesaORCDTO>(dto, headers);
            ResponseEntity<SaldoFonteDespesaORCDTO> response = restTemplate.exchange(url + "/api/saldo-fonte-despesaorc", HttpMethod.POST, entity, SaldoFonteDespesaORCDTO.class);
            return (SaldoFonteDespesaORCDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo orçamentario. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo orçamentario. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo orçamentario. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public SaldoExtraorcamentarioDTO gerarSaldoExtraorcamentario(SaldoExtraorcamentarioDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoExtraorcamentarioDTO> entity = new HttpEntity<SaldoExtraorcamentarioDTO>(dto, headers);
            ResponseEntity<SaldoExtraorcamentarioDTO> response = restTemplate.exchange(url + "/api/saldo-extraorcamentario", HttpMethod.POST, entity, SaldoExtraorcamentarioDTO.class);
            return (SaldoExtraorcamentarioDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo extra. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo extra. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo extra. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }


    public SaldoGrupoMaterialDTO gerarSaldoGrupoMaterial(SaldoGrupoMaterialDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoGrupoMaterialDTO> entity = new HttpEntity<SaldoGrupoMaterialDTO>(dto, headers);
            ResponseEntity<SaldoGrupoMaterialDTO> response = restTemplate.exchange(url + "/api/saldo-grupomaterial", HttpMethod.POST, entity, SaldoGrupoMaterialDTO.class);
            return (SaldoGrupoMaterialDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo Grupo Material. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo Grupo Material. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo Grupo Material. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public MovimentoContabilDTO movimentarContabil(MovimentoContabilDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<MovimentoContabilDTO> entity = new HttpEntity<MovimentoContabilDTO>(dto, headers);
            ResponseEntity<MovimentoContabilDTO> response = restTemplate.exchange(url + "/api/movimento-contabil", HttpMethod.POST, entity, MovimentoContabilDTO.class);
            return (MovimentoContabilDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo contabil. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo contabil. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo contabil. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    private Object tratarRetorno(ResponseEntity response) {
        if (HttpStatus.OK.equals(response.getStatusCode()) || HttpStatus.CREATED.equals(response.getStatusCode())) {
            return response.getBody();
        }
        if (HttpStatus.NO_CONTENT.equals(response.getStatusCode())) {
            if (response.getHeaders().containsKey(HEADER_API)) {
                List<String> strings = response.getHeaders().get(HEADER_API);
                if (strings != null) {
                    for (String mensagem : strings) {
                        throw new ExcecaoNegocioGenerica(mensagem);
                    }
                }
            }
        }
        throw new ExcecaoNegocioGenerica("Erro não tratado... Código da resposta: " + response.getStatusCode().name());
    }

    public SaldoGrupoBemMovelDTO gerarSaldoGrupoBemMovel(SaldoGrupoBemMovelDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoGrupoBemMovelDTO> entity = new HttpEntity<SaldoGrupoBemMovelDTO>(dto, headers);
            ResponseEntity<SaldoGrupoBemMovelDTO> response = restTemplate.exchange(url + "/api/saldo-grupobens-moveis", HttpMethod.POST, entity, SaldoGrupoBemMovelDTO.class);
            return (SaldoGrupoBemMovelDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo Grupo Bem Móvel. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo Grupo Bem Móvel. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo Grupo Bem Móvel. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public SaldoGrupoBemImovelDTO gerarSaldoGrupoBemImovel(SaldoGrupoBemImovelDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoGrupoBemImovelDTO> entity = new HttpEntity<SaldoGrupoBemImovelDTO>(dto, headers);
            ResponseEntity<SaldoGrupoBemImovelDTO> response = restTemplate.exchange(url + "/api/saldo-grupobens-imoveis", HttpMethod.POST, entity, SaldoGrupoBemImovelDTO.class);
            return (SaldoGrupoBemImovelDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo Grupo Bem Imovel. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo Grupo Bem Imovel. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo Grupo Bem Imovel. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public SaldoGrupoBemIntangiveisDTO gerarSaldoGrupoBemIntangivel(SaldoGrupoBemIntangiveisDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoGrupoBemIntangiveisDTO> entity = new HttpEntity<SaldoGrupoBemIntangiveisDTO>(dto, headers);
            ResponseEntity<SaldoGrupoBemIntangiveisDTO> response = restTemplate.exchange(url + "/api/saldo-grupobens-intangiveis", HttpMethod.POST, entity, SaldoGrupoBemIntangiveisDTO.class);
            return (SaldoGrupoBemIntangiveisDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo Grupo Bem Intangivel. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo Grupo Bem Intangivel. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo Grupo Bem Intangivel. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public SaldoDividaPublicaDTO gerarSaldoDividaPublica(SaldoDividaPublicaDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoDividaPublicaDTO> entity = new HttpEntity<SaldoDividaPublicaDTO>(dto, headers);
            ResponseEntity<SaldoDividaPublicaDTO> response = restTemplate.exchange(url + "/api/saldo-divida-publica", HttpMethod.POST, entity, SaldoDividaPublicaDTO.class);
            return (SaldoDividaPublicaDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo Dívida Pública. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo Dívida Pública. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo Dívida Pública. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }


    public SaldoCreditoReceberDTO gerarSaldoCreditoReceber(SaldoCreditoReceberDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoCreditoReceberDTO> entity = new HttpEntity<SaldoCreditoReceberDTO>(dto, headers);
            ResponseEntity<SaldoCreditoReceberDTO> response = restTemplate.exchange(url + "/api/saldo-credito-receber", HttpMethod.POST, entity, SaldoCreditoReceberDTO.class);
            return (SaldoCreditoReceberDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo Crédito Receber. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo Crédito Receber. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo Crédito Receber. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public SaldoDividaAtivaContabilDTO gerarSaldoDividaAtiva(SaldoDividaAtivaContabilDTO dto) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<SaldoDividaAtivaContabilDTO> entity = new HttpEntity<SaldoDividaAtivaContabilDTO>(dto, headers);
            ResponseEntity<SaldoDividaAtivaContabilDTO> response = restTemplate.exchange(url + "/api/saldo-divida-ativa", HttpMethod.POST, entity, SaldoDividaAtivaContabilDTO.class);
            return (SaldoDividaAtivaContabilDTO) tratarRetorno(response);
        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo Dívida Ativa. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo Dívida Ativa. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo Dívida Ativa. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public void limparObjetos() {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();
            String mensagem = "Limpando objetos ";
            HttpEntity<String> entity = new HttpEntity<String>(mensagem, headers);
            ResponseEntity<Void> response = restTemplate.exchange(url + "/api/gerenciar-objetos/limpar", HttpMethod.POST, entity, Void.class);

        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo contabil. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo contabil. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo contabil. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }

    public void carregarContaAuxiliar(Exercicio exercicio) {
        try {

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = getHttpHeaders();

            HttpEntity<Integer> entity = new HttpEntity<Integer>(exercicio.getAno(), headers);
            ResponseEntity<Void> response = restTemplate.exchange(url + "/api/gerenciar-objetos/carregar-conta-auxiliar", HttpMethod.POST, entity, Void.class);

        } catch (ResourceAccessException e) {
            String mensagem = "Não foi possivel conectar ao sistema de saldo contabil. Url: " + url;
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (HttpServerErrorException e) {
            String mensagem = "Erro no sistema de saldo contabil. Erro: " + e.getResponseBodyAsString();
            throw new ExcecaoNegocioGenerica(mensagem);
        } catch (Exception e) {
            String mensagem = "Erro no sistema de saldo contabil. Entre em contato com o suporte. Erro: " + e.getMessage();
            throw new ExcecaoNegocioGenerica(mensagem);
        }
    }
}
