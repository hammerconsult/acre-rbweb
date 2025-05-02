package br.com.webpublico.negocios.rh.integracaoponto;

import br.com.webpublico.entidades.rh.EnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.ItemEnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.RetornoGeracaoTokenPonto;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.InformacoesAfastamentoFeriasDTO;
import br.com.webpublico.enums.rh.TipoEnvioDadosRBPonto;
import br.com.webpublico.enums.tributario.TipoWebService;
import br.com.webpublico.exception.rh.EnvioDadosRBPontoException;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoWSRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EnvioDadosRBPontoExecutor implements Callable<EnvioDadosRBPonto> {
    private final Logger logger = LoggerFactory.getLogger(EnvioDadosRBPontoExecutor.class);

    private final ConfiguracaoWSRHFacade configuracaoWSRHFacade;
    private final EnvioDadosRBPontoFacade envioDadosRBPontoFacade;
    private EnvioDadosRBPonto envioDadosRBPonto;
    private TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto;
    private String statusGeracaoToken;

    public EnvioDadosRBPontoExecutor(ConfiguracaoWSRHFacade configuracaoWSRHFacade) {
        this.configuracaoWSRHFacade = configuracaoWSRHFacade;
        this.envioDadosRBPontoFacade = (EnvioDadosRBPontoFacade) Util.getFacadeViaLookup("java:module/EnvioDadosRBPontoFacade");
    }

    public Future<EnvioDadosRBPonto> execute(EnvioDadosRBPonto envio, TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto) {
        this.envioDadosRBPonto = envio;
        this.tipoEnvioDadosRBPonto = tipoEnvioDadosRBPonto;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<EnvioDadosRBPonto> submit = executorService.submit(this);
        executorService.shutdown();
        return submit;
    }

    public EnvioDadosRBPonto call() {
        ConfiguracaoWebServiceRH configuracaoWebServiceRH = configuracaoWSRHFacade.getConfiguracaoPorTipoDaKeyCorrente(TipoWebService.PONTO);
        RetornoGeracaoTokenPonto retornoToken = configuracaoWSRHFacade.buscarToken(configuracaoWebServiceRH);
        statusGeracaoToken = retornoToken.getStatusGeracaoToken();
        for (ItemEnvioDadosRBPonto item : envioDadosRBPonto.getItensEnvioDadosRBPontos()) {
            integracaoAfastamentoFerias(item, tipoEnvioDadosRBPonto, configuracaoWebServiceRH, retornoToken.getToken());
        }
        envioDadosRBPontoFacade.atualizarEnvioDados(envioDadosRBPonto);
        return envioDadosRBPonto;
    }

    public void integracaoAfastamentoFerias(ItemEnvioDadosRBPonto item, TipoEnvioDadosRBPonto tipoEnvioDadosRBPonto, ConfiguracaoWebServiceRH configuracaoWebServiceRH, String token) throws EnvioDadosRBPontoException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            InformacoesAfastamentoFeriasDTO afastamentoFerias = criarDTOEnvio(item);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
            headers.set("Authorization", token);

            HttpEntity<InformacoesAfastamentoFeriasDTO> requestEntity = new HttpEntity<>(afastamentoFerias, headers);
            String url = configuracaoWebServiceRH.getUrl() + "/api/salvar/afastamento";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            validarEnvio(responseEntity);

            if (responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody() != null && !responseEntity.getBody().isEmpty()) {
                envioDadosRBPontoFacade.atualizarFeriasAfastamento(item, tipoEnvioDadosRBPonto);
                item.setObservacao(responseEntity.getStatusCode() + " Registro cadastrado com sucesso.");
            }
        } catch (EnvioDadosRBPontoException en) {
            item.setObservacao(!statusGeracaoToken.isEmpty() ? statusGeracaoToken : en.getMessage());
            envioDadosRBPontoFacade.atualizarFeriasAfastamento(item, TipoEnvioDadosRBPonto.NAO_ENVIADO);
            logger.error(statusGeracaoToken + en.getMessage());
        } catch (Exception ex) {
            item.setObservacao(!statusGeracaoToken.isEmpty() ? statusGeracaoToken : (ex.getMessage() + " \"Erro ao integrar com o ponto\"."));
            envioDadosRBPontoFacade.atualizarFeriasAfastamento(item, TipoEnvioDadosRBPonto.NAO_ENVIADO);
            logger.error(statusGeracaoToken + "Erro ao integrar com o ponto {}", ex.getMessage());
        }
    }

    private InformacoesAfastamentoFeriasDTO criarDTOEnvio(ItemEnvioDadosRBPonto item) throws JSONException {
        InformacoesAfastamentoFeriasDTO dto = new InformacoesAfastamentoFeriasDTO();
        dto.setServidorId(item.getContratoFP().getMatriculaFP().getPessoa().getId().toString());
        dto.setVinculoId(item.getContratoFP().getId().toString());
        dto.setDataInicio(DataUtil.getDataFormatada(item.getDataInicial(), "yyyy-MM-dd HH:mm:ss"));
        dto.setDataFim(DataUtil.getDataFormatada(item.getDataFinal(), "yyyy-MM-dd HH:mm:ss"));
        dto.setTipo(item.getEnvioDadosRBPonto().getTipoInformacaoEnvioRBPonto().name());
        dto.setIdRbweb(item.getIdentificador().toString());
        return dto;
    }

    private void validarEnvio(ResponseEntity<String> responseEntity) throws EnvioDadosRBPontoException {
        if (responseEntity.getStatusCode() == HttpStatus.MOVED_PERMANENTLY){
            throw new EnvioDadosRBPontoException("O servidor mudou o local que você está tentando acessar " +
                responseEntity.getHeaders().getLocation() + " - Verifique os campos URL na aba WebServices nas configurações do RH, no item correspondente");
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new EnvioDadosRBPontoException("Na integração com o ponto houve o erro HTTP " + responseEntity.getStatusCode().toString() + responseEntity.getBody());
        }
    }

}
