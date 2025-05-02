package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.AssistenteBarraProgresso;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AssistenteIntegracaoSit extends AssistenteBarraProgresso {
    private static final String AUTH_HEADER_NAME = "API-KEY";
    private static final String API_KEY = "fcc5f16f-452b-478e-8803-6d3d33fc68bf";

    private Integer integrados;
    private Date inicio, fim;
    private String setor, quadra, lote, bloco, api, uuid, login;
    List<String> logs = Lists.newArrayList();
    List<String> inscricoesCadastrais = Lists.newArrayList();

    public AssistenteIntegracaoSit() {
        uuid = UUID.randomUUID().toString();
        integrados = 0;
        setDescricaoProcesso("Integração dos Cadastros Imobiliários como SIT");
    }


    public AssistenteIntegracaoSit(Date inicio, Date fim) {
        this();
        this.inicio = inicio;
        this.fim = fim;
        setTotal(1);
    }

    public AssistenteIntegracaoSit(String setor, String quadra) {
        this();
        this.setor = setor;
        this.quadra = quadra;
        setTotal(1);
    }

    public AssistenteIntegracaoSit(String bloco) {
        this();
        this.bloco = bloco;
        if (getBloco().contains(",")) {
            setTotal(getBloco().split(",").length);
        } else {
            setTotal(getBloco().split("\n").length);
        }
    }

    public AssistenteIntegracaoSit(String setor, String quadra, String lote) {
        this();
        this.setor = setor;
        this.quadra = quadra;
        this.lote = lote;
        setTotal(1);
    }

    public AssistenteIntegracaoSit(List<String> inscricoes) {
        this();
        inscricoesCadastrais = inscricoes;
        setTotal(inscricoes.size());
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public String getBloco() {
        return bloco;
    }


    public Integer getIntegrados() {
        return integrados;
    }

    public Date getInicio() {
        return inicio;
    }

    public Date getFim() {
        return fim;
    }

    public String getSetor() {
        return setor;
    }

    public String getQuadra() {
        return quadra;
    }

    public String getLote() {
        return lote;
    }

    public synchronized void conta() {
        super.conta();
        integrados = integrados + 1;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<String> getInscricoesCadastrais() {
        return inscricoesCadastrais;
    }

    public void setInscricoesCadastrais(List<String> inscricoesCadastrais) {
        this.inscricoesCadastrais = inscricoesCadastrais;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void integrar(String api) {
        this.api = api;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTH_HEADER_NAME, API_KEY);
        HttpEntity<AssistenteIntegracaoSit> entity = new HttpEntity<>(this, headers);
        restTemplate.exchange(this.api + "/integrar", HttpMethod.POST, entity, Void.class);
        removerProcessoDoAcompanhamento();
    }

    public void consultar() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(AUTH_HEADER_NAME, API_KEY);
        HttpEntity<AssistenteIntegracaoSit> entity = new HttpEntity<>(this, headers);
        ResponseEntity<AssistenteDTO> exchange = restTemplate.exchange(api + "/consultar/" + uuid, HttpMethod.GET, entity, AssistenteDTO.class);
        AssistenteDTO result = exchange.getBody();
        if (result != null) {
            this.integrados = result.integrados;
            setTotal(result.total);
            setCalculados(integrados);
        }
    }

    public boolean isDone() {
        return integrados >= getTotal();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AssistenteDTO {
        private Integer integrados, total;
        private String uuid;


        public AssistenteDTO() {
        }

        public Integer getIntegrados() {
            return integrados;
        }

        public void setIntegrados(Integer integrados) {
            this.integrados = integrados;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }
}
