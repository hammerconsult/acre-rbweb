package br.com.webpublico.negocios.rh.integracaoponto;

import br.com.webpublico.entidades.rh.AtualizacaoDadosRBPonto;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.EditarFeriasDTO;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.ExcluirFeriasDTO;
import br.com.webpublico.entidadesauxiliares.rh.integracaoponto.RetornoGeracaoTokenPonto;
import br.com.webpublico.enums.rh.StatusIntegracaoRBPonto;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.enums.rh.TipoOperacaoIntegracaoRBPonto;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoWSRHFacade;
import br.com.webpublico.util.Util;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class AtualizacaoDadosRBPontoFacade extends AbstractFacade<AtualizacaoDadosRBPonto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ConfiguracaoWSRHFacade configuracaoWSRHFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    public AtualizacaoDadosRBPontoFacade() {
        super(AtualizacaoDadosRBPonto.class);
    }

    private HttpHeaders montarHeader(ConfiguracaoWebServiceRH configuracaoWebServiceRH) {
        RetornoGeracaoTokenPonto retornoToken = configuracaoWSRHFacade.buscarToken(configuracaoWebServiceRH);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        requestHeaders.set("Authorization", retornoToken.getToken());

        return requestHeaders;
    }

    public void excluirFerias(ExcluirFeriasDTO excluirFeriasDTO, ConfiguracaoWebServiceRH configuracaoWebServiceRH) {
        excluirFerias(excluirFeriasDTO, configuracaoWebServiceRH, null);
    }

    public void excluirFerias(ExcluirFeriasDTO excluirFeriasDTO, ConfiguracaoWebServiceRH configuracaoWebServiceRH, AtualizacaoDadosRBPonto atualizacaoDadosRBPonto) {
        boolean retentativa = atualizacaoDadosRBPonto != null;
        if (!retentativa) {
            atualizacaoDadosRBPonto = new AtualizacaoDadosRBPonto(Long.valueOf(excluirFeriasDTO.getIdRbweb()), TipoInformacaoEnvioRBPonto.FERIAS, TipoOperacaoIntegracaoRBPonto.EXCLUSAO);
        }
        try {
            if (!retentativa) {
                atualizacaoDadosRBPonto.setRequisicao(Util.objectToJsonString(excluirFeriasDTO, ExcluirFeriasDTO.class));
            }
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<ExcluirFeriasDTO> requestEntity = new HttpEntity<>(excluirFeriasDTO, montarHeader(configuracaoWebServiceRH));
            String url = configuracaoWebServiceRH.getUrl() + "/api/ferias/deletar";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            atualizacaoDadosRBPonto.setResposta(montarResposta(responseEntity));
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                atualizacaoDadosRBPonto.setStatusIntegracao(StatusIntegracaoRBPonto.INTEGRADO_COM_SUCESSO);
            }
        } catch(HttpClientErrorException httpClientErrorException) {
            atualizacaoDadosRBPonto.setStatusIntegracao(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO);
            atualizacaoDadosRBPonto.setResposta(montarResposta(httpClientErrorException));
        } catch(Exception exception) {
            atualizacaoDadosRBPonto.setStatusIntegracao(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO);
            atualizacaoDadosRBPonto.setLog(exception.getMessage());
        } finally {
            salvarTentativaComErroParaReprocessamento(retentativa, false, atualizacaoDadosRBPonto);
        }
    }

    public void atualizarFerias(EditarFeriasDTO editarFeriasDTO, ConfiguracaoWebServiceRH configuracaoWebServiceRH) {
        atualizarFerias(editarFeriasDTO, configuracaoWebServiceRH, null);
    }

    public void atualizarFerias(EditarFeriasDTO editarFeriasDTO, ConfiguracaoWebServiceRH configuracaoWebServiceRH, AtualizacaoDadosRBPonto atualizacaoDadosRBPonto) {
        boolean retentativa = atualizacaoDadosRBPonto != null;
        boolean existeAtualizacaoPendente = false;
        if (!retentativa) {
            AtualizacaoDadosRBPonto atualizacaoPendente = buscarPorStatusEIdentificador(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO, Long.valueOf(editarFeriasDTO.getIdRbweb()));
            existeAtualizacaoPendente = atualizacaoPendente != null;
            if (!existeAtualizacaoPendente) {
                atualizacaoDadosRBPonto = new AtualizacaoDadosRBPonto(Long.valueOf(editarFeriasDTO.getIdRbweb()), TipoInformacaoEnvioRBPonto.FERIAS, TipoOperacaoIntegracaoRBPonto.EDICAO);
            } else {
                atualizacaoDadosRBPonto = atualizacaoPendente;
            }
        }
        try {
            if (!retentativa) {
                atualizacaoDadosRBPonto.setRequisicao(Util.objectToJsonString(editarFeriasDTO, EditarFeriasDTO.class));
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<EditarFeriasDTO> requestEntity = new HttpEntity<>(editarFeriasDTO, montarHeader(configuracaoWebServiceRH));
            String url = configuracaoWebServiceRH.getUrl() + "/api/ferias/editar";
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            atualizacaoDadosRBPonto.setResposta(montarResposta(responseEntity));
            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
                atualizacaoDadosRBPonto.setStatusIntegracao(StatusIntegracaoRBPonto.INTEGRADO_COM_SUCESSO);
            }
        } catch(HttpClientErrorException httpClientErrorException) {
            atualizacaoDadosRBPonto.setStatusIntegracao(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO);
            atualizacaoDadosRBPonto.setResposta(montarResposta(httpClientErrorException));
        } catch(Exception exception) {
            atualizacaoDadosRBPonto.setStatusIntegracao(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO);
            atualizacaoDadosRBPonto.setLog(exception.getMessage());
        } finally {
            salvarTentativaComErroParaReprocessamento(retentativa, existeAtualizacaoPendente, atualizacaoDadosRBPonto);
        }
    }

    private void salvarTentativaComErroParaReprocessamento(boolean retentativa, boolean existeAtualizacaoPendente, AtualizacaoDadosRBPonto atualizacaoDadosRBPonto) {
        if (!retentativa && !existeAtualizacaoPendente) {
            if(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO.equals(atualizacaoDadosRBPonto.getStatusIntegracao())) {
                salvarNovo(atualizacaoDadosRBPonto);
            }
        } else {
            if(StatusIntegracaoRBPonto.INTEGRADO_COM_ERRO.equals(atualizacaoDadosRBPonto.getStatusIntegracao())) {
                salvar(atualizacaoDadosRBPonto);
            } else {
                remover(atualizacaoDadosRBPonto);
            }
        }
    }


    private String montarResposta(ResponseEntity<String> responseEntity) {
        return "{\n" +
            "    \"statusCode\": " + responseEntity.getStatusCode() + "\n" +
            "    \"body\": " + responseEntity.getBody() + "\n" +
            "}";
    }

    private String montarResposta(HttpClientErrorException httpClientErrorException) {
        return "{\n" +
            "    \"statusCode\": " + httpClientErrorException.getStatusCode() + "\n" +
            "    \"body\": " + httpClientErrorException.getResponseBodyAsString() + "\n" +
            "}";
    }

    public AtualizacaoDadosRBPonto buscarPorStatusEIdentificador(StatusIntegracaoRBPonto status, Long identificador) {
        String sql = "SELECT * FROM ATUALIZACAODADOSRBPONTO a " +
            "WHERE a.STATUSINTEGRACAO = :status " +
            "AND a.IDENTIFICADOR = :identificador ";

        Query q = getEntityManager().createNativeQuery(sql, AtualizacaoDadosRBPonto.class);
        q.setParameter("status", status.name());
        q.setParameter("identificador", identificador);
        List resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return null;
        }
        return (AtualizacaoDadosRBPonto) resultado.get(0);
    }

    public List<AtualizacaoDadosRBPonto> buscarPorStatus(StatusIntegracaoRBPonto status) {
        String sql = "SELECT * FROM ATUALIZACAODADOSRBPONTO a " +
            "WHERE a.STATUSINTEGRACAO = :status";
        Query q = getEntityManager().createNativeQuery(sql, AtualizacaoDadosRBPonto.class);
        q.setParameter("status", status.name());
        return q.getResultList();
    }
}
