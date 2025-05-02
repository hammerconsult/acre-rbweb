package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.ConfiguracaoWebService;
import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.HistoricoImpressaoDAM;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.AssistenteDAM;
import br.com.webpublico.entidadesauxiliares.AssistenteMalaDiretaGeral;
import br.com.webpublico.entidadesauxiliares.AssistenteMalaDiretaRBTrans;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.handlers.RestTemplateResponseErrorHandler;
import br.com.webpublico.negocios.tributario.dao.JdbcHistoricoImpressaoDAM;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

public class ImprimeDAM extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(ImprimeDAM.class);

    private HashMap<String, Object> parameters;
    JdbcHistoricoImpressaoDAM dao;
    private DAMFacade damFacade;
    private RestTemplate restTemplate;

    public ImprimeDAM() {
        try {
            dao = Util.recuperarSpringBean(JdbcHistoricoImpressaoDAM.class);
            damFacade = (DAMFacade) Util.getFacadeViaLookup("java:module/DAMFacade");
            restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
        } catch (Exception e) {
            logger.error("Erro ao recuperar EJB no ImprimeDAM : ", e);
        }
    }

    public void adicionarParametro(HashMap<String, Object> mapa) {
        if (mapa != null) {
            for (String keySet : mapa.keySet()) {
                adicionarParametro(keySet, mapa.get(keySet));
            }
        }
    }

    public void adicionarParametro(String nome, Object valor) {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(nome, valor);
    }

    public byte[] gerarByteImpressaoDamUnicoViaApi(DAM dam) {
        return gerarByteImpressaoDamUnicoViaApi(Lists.newArrayList(dam));
    }

    public byte[] gerarByteImpressaoDamUnicoViaApi(List<DAM> dams) {
        return gerarByteImpressaoDamUnicoViaApi(dams, HistoricoImpressaoDAM.TipoImpressao.SISTEMA);
    }

    public byte[] gerarByteImpressaoDamUnicoViaApi(List<DAM> dams,
                                                   HistoricoImpressaoDAM.TipoImpressao tipoImpressao) {
        return gerarByteImpressaoDamUnicoViaApi(dams, null, tipoImpressao);
    }

    public byte[] gerarByteImpressaoDamUnicoViaApi(List<DAM> dams,
                                                   Long idPessoa,
                                                   HistoricoImpressaoDAM.TipoImpressao tipoImpressao) {
        try {
            List<Long> idsDam = Lists.newArrayList();
            for (DAM dam : dams) {
                idsDam.add(dam.getId());
            }
            AssistenteDAM assistenteDAM = new AssistenteDAM();
            assistenteDAM.setIdsDam(idsDam);
            assistenteDAM.setIdPessoa(idPessoa);
            try {
                assistenteDAM.setUsuario(usuarioLogado().getLogin());
            } catch (Exception e) {
                logger.error("Nenhum usuário autenticado.", e);
            }
            assistenteDAM.setTipoImpressaoDAM(tipoImpressao);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteDAM> httpEntity = new HttpEntity<>(assistenteDAM, httpHeaders);
            ConfiguracaoWebService configWs = damFacade.getConfiguracaoWsDAM();
            ResponseEntity<byte[]> response = restTemplate.exchange(configWs.getUrl() + "/imprimir-dam-unico",
                HttpMethod.POST, httpEntity, byte[].class);
            return response.getBody();
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public void imprimirDamUnicoViaApi(DAM dam) throws Exception {
        imprimirDamUnicoViaApi(Lists.newArrayList(dam));
    }

    public void imprimirDamUnicoViaApi(List<DAM> dams) throws Exception {
        imprimirDamUnicoViaApi(dams, HistoricoImpressaoDAM.TipoImpressao.SISTEMA);
    }

    public void imprimirDamUnicoViaApi(List<DAM> dams, Long idPessoa) throws Exception {
        imprimirDamUnicoViaApi(dams, idPessoa, HistoricoImpressaoDAM.TipoImpressao.SISTEMA);
    }

    public void imprimirDamUnicoViaApi(List<DAM> dams,
                                       HistoricoImpressaoDAM.TipoImpressao tipoImpressao) throws Exception {
        imprimirDamUnicoViaApi(dams, null, tipoImpressao);
    }

    public void imprimirDamUnicoViaApi(List<DAM> dams,
                                       Long idPessoa,
                                       HistoricoImpressaoDAM.TipoImpressao tipoImpressao) throws Exception {
        byte[] bytes = gerarByteImpressaoDamUnicoViaApi(dams, idPessoa, tipoImpressao);
        escreveNoResponse("DAM.pdf", bytes);
    }

    public byte[] gerarBytesImpressaoDamCompostoViaApi(DAM dam) {
        return gerarBytesImpressaoDamCompostoViaApi(dam, null);
    }

    public byte[] gerarBytesImpressaoDamCompostoViaApi(DAM dam, Pessoa pessoa) {
        try {
            AssistenteDAM assistenteDAM = new AssistenteDAM();
            assistenteDAM.setIdDam(dam.getId());
            assistenteDAM.setIdPessoa(pessoa != null ? pessoa.getId() : null);
            try {
                assistenteDAM.setUsuario(usuarioLogado().getLogin());
            } catch (Exception e) {
                logger.error("Nenhum usuário autenticado.", e);
            }
            assistenteDAM.setTipoImpressaoDAM(HistoricoImpressaoDAM.TipoImpressao.SISTEMA);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteDAM> httpEntity = new HttpEntity<>(assistenteDAM, httpHeaders);
            ConfiguracaoWebService configWs = damFacade.getConfiguracaoWsDAM();
            ResponseEntity<byte[]> response = restTemplate.exchange(configWs.getUrl() + "/imprimir-dam-composto",
                HttpMethod.POST, httpEntity, byte[].class);
            return response.getBody();
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public void imprimirDamCompostoViaApi(DAM dam) throws Exception {
        imprimirDamCompostoViaApi(dam, null);
    }

    public void imprimirDamCompostoViaApi(DAM dam, Pessoa pessoa) throws Exception {
        byte[] bytes = gerarBytesImpressaoDamCompostoViaApi(dam, pessoa);
        escreveNoResponse("DAM_COMPOSTO.pdf", bytes);
    }

    public byte[] gerarBytesImpressaoMalaDiretaGeral(String usuario,
                                                     Long idMala,
                                                     List<Long> idsItens) {
        try {
            AssistenteMalaDiretaGeral assistenteMalaDiretaGeral = new AssistenteMalaDiretaGeral();
            assistenteMalaDiretaGeral.setIdMala(idMala);
            assistenteMalaDiretaGeral.setIdsItens(idsItens);
            assistenteMalaDiretaGeral.setUsuario(usuario);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteMalaDiretaGeral> httpEntity = new HttpEntity<>(assistenteMalaDiretaGeral, httpHeaders);
            ConfiguracaoWebService configWs = damFacade.getConfiguracaoWsDAM();
            ResponseEntity<byte[]> response = restTemplate.exchange(configWs.getUrl() + "/mala-direta-geral/gerar-dam",
                HttpMethod.POST, httpEntity, byte[].class);
            return response.getBody();
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public void imprimirDamMalaDiretaGeral(String usuario,
                                           Long idMala,
                                           List<Long> idsItens) throws Exception {
        byte[] bytes = gerarBytesImpressaoMalaDiretaGeral(usuario, idMala, idsItens);
        geraNoDialog = true;
        escreveNoResponse("Mala Direta Geral.pdf", bytes);
    }

    public byte[] gerarBytesImpressaoMalaDiretaRbTrans(String usuario,
                                                       Long idMala,
                                                       List<Long> idsItens) {
        try {
            AssistenteMalaDiretaRBTrans assistenteMalaDiretaRBTrans = new AssistenteMalaDiretaRBTrans();
            assistenteMalaDiretaRBTrans.setIdMala(idMala);
            assistenteMalaDiretaRBTrans.setIdsItens(idsItens);
            assistenteMalaDiretaRBTrans.setUsuario(usuario);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AssistenteMalaDiretaRBTrans> httpEntity = new HttpEntity<>(assistenteMalaDiretaRBTrans, httpHeaders);
            ConfiguracaoWebService configWs = damFacade.getConfiguracaoWsDAM();
            ResponseEntity<byte[]> response = restTemplate.exchange(configWs.getUrl() + "/mala-direta-rbtrans/gerar-dam",
                HttpMethod.POST, httpEntity, byte[].class);
            return response.getBody();
        } catch (ResourceAccessException rae) {
            logger.error("Erro ao conectar na api de geração do dam", rae);
            throw new ValidacaoException("Não foi possível se conectar a api de geração do DAM.");
        }
    }

    public void imprimirDamMalaDiretaRBTrans(String usuario,
                                             Long idMala,
                                             List<Long> idsItens) throws Exception {
        byte[] bytes = gerarBytesImpressaoMalaDiretaRbTrans(usuario, idMala, idsItens);
        geraNoDialog = true;
        escreveNoResponse("Mala Direta Geral.pdf", bytes);
    }
}
